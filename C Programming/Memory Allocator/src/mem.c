#define _DEFAULT_SOURCE

#include "mem.h"

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "mem_internals.h"
#include "util.h"

void debug_block(struct block_header *b, const char *fmt, ...);

void debug(const char *fmt, ...);

extern inline block_size size_from_capacity(block_capacity cap);

extern inline block_capacity capacity_from_size(block_size sz);

static bool block_is_big_enough(size_t query, struct block_header *block) {
  return block->capacity.bytes >= query;
}

static size_t pages_count(size_t mem) {
  return mem / getpagesize() + ((mem % getpagesize()) > 0);
}

static size_t round_pages(size_t mem) {
  return getpagesize() * pages_count(mem);
}

static void block_init(void *restrict addr, block_size block_sz,
                       void *restrict next) {
  *((struct block_header *)addr) = (struct block_header){
      .next = next, .capacity = capacity_from_size(block_sz), .is_free = true};
}

static size_t region_actual_size(size_t query) {
  return size_max(round_pages(query), REGION_MIN_SIZE);
}

extern inline bool region_is_invalid(const struct region *r);

static void *map_pages(void const *addr, size_t length, int additional_flags) {
  return mmap((void *)addr, length, PROT_READ | PROT_WRITE,
              MAP_PRIVATE | MAP_ANONYMOUS | additional_flags, -1, 0);
}

/*  аллоцировать регион памяти и инициализировать его блоком */
static struct region alloc_region(void const *addr, size_t query) {
  size_t actual_size =
      region_actual_size(query + offsetof(struct block_header, contents));
  void *pages_ptr = map_pages(addr, actual_size, MAP_FIXED_NOREPLACE);
  if (pages_ptr == MAP_FAILED) {
    /*
     * addr is used instead of NULL, because the kernel will try to
     * map page nearest to the addr possible (if not at the addr exactly).
     */
    pages_ptr = map_pages(addr, actual_size, 0);
  }
  if (pages_ptr == MAP_FAILED) {
    return REGION_INVALID;
  }
  block_init(pages_ptr, (block_size){.bytes = actual_size}, NULL);
  return (struct region){
      .addr = pages_ptr, .size = actual_size, .extends = (pages_ptr == addr)};
}

static void *block_after(struct block_header const *block);

static bool blocks_continuous(struct block_header const *fst,
                              struct block_header const *snd);

void *heap_init(size_t initial) {
  const struct region region = alloc_region(HEAP_START, initial);
  if (region_is_invalid(&region)) return NULL;

  return region.addr;
}

/*  освободить всю память, выделенную под кучу */
void heap_term() {
  /*
   * Assuming at least one region was mapped and
   * exactly at HEAP_START. Otherwise - undefined behaviour.
   */
  void *curr_region_start_ptr = HEAP_START;
  size_t curr_region_length = 0;

  struct block_header *next_header_ptr = (struct block_header *)(HEAP_START);

  while (next_header_ptr != NULL) {
    struct block_header *curr_header_ptr = next_header_ptr;
    struct block_header curr_header = *(curr_header_ptr);
    curr_region_length += size_from_capacity(curr_header.capacity).bytes;
    next_header_ptr = curr_header.next;

    // End of consecutive regions
    if (next_header_ptr == NULL ||
        !blocks_continuous(curr_header_ptr, next_header_ptr)) {
      munmap(curr_region_start_ptr, curr_region_length);
      curr_region_start_ptr = next_header_ptr;
      curr_region_length = 0;
    }
  }
}

#define BLOCK_MIN_CAPACITY 24

/*  --- Разделение блоков (если найденный свободный блок слишком большой )--- */

static bool block_splittable(struct block_header *restrict block,
                             size_t query) {
  return block->is_free &&
         query + offsetof(struct block_header, contents) + BLOCK_MIN_CAPACITY <=
             block->capacity.bytes;
}

static bool split_if_too_big(struct block_header *block, size_t query) {
  if (block_splittable(block, query)) {
    block_capacity orig_capacity = block->capacity;

    block->capacity.bytes = query;
    void *new_block_address = block_after(block);

    block_init(new_block_address,
               (block_size){.bytes = orig_capacity.bytes - query}, block->next);
    block->next = new_block_address;
    return true;
  } else
    return false;
}

/*  --- Слияние соседних свободных блоков --- */

static void *block_after(struct block_header const *block) {
  return (void *)(block->contents + block->capacity.bytes);
}

static bool blocks_continuous(struct block_header const *fst,
                              struct block_header const *snd) {
  return (void *)snd == block_after(fst);
}

static bool mergeable(struct block_header const *restrict fst,
                      struct block_header const *restrict snd) {
  return fst->is_free && snd->is_free && blocks_continuous(fst, snd);
}

static bool try_merge_with_next(struct block_header *block) {
  if (block != NULL && block->next != NULL && mergeable(block, block->next)) {
    block->capacity.bytes += size_from_capacity(block->next->capacity).bytes;
    block->next = block->next->next;
    return true;
  } else
    return false;
}

/*  --- ... ecли размера кучи хватает --- */

struct block_search_result {
  enum { BSR_FOUND_GOOD_BLOCK, BSR_REACHED_END_NOT_FOUND, BSR_CORRUPTED } type;
  struct block_header *block;
};

static struct block_search_result find_good_or_last(
    struct block_header *restrict block, size_t sz) {
  while (block != NULL) {
    if (block->is_free) {
      while (block->next != NULL) {
        if (!try_merge_with_next(block)) break;
      }
      if (block_is_big_enough(sz, block)) {
        return (struct block_search_result){.type = BSR_FOUND_GOOD_BLOCK,
                                            .block = block};
      } else
        goto last_or_continue;
    } else {
    last_or_continue:
      if (block->next == NULL) {
        return (struct block_search_result){.type = BSR_REACHED_END_NOT_FOUND,
                                            .block = block};
      } else {
        block = block->next;
        continue;
      }
    }
  }
  return (struct block_search_result){.type = BSR_CORRUPTED, .block = NULL};
}

/*  Попробовать выделить память в куче начиная с блока `block` не пытаясь
 расширить кучу Можно переиспользовать как только кучу расширили. */
static struct block_search_result try_memalloc_existing(
    size_t query, struct block_header *block) {
  query = size_max(BLOCK_MIN_CAPACITY, query);
  if (block == NULL) {
    return (struct block_search_result){.block = block, .type = BSR_CORRUPTED};
  }
  struct block_search_result potential_block = find_good_or_last(block, query);
  if (potential_block.type == BSR_FOUND_GOOD_BLOCK) {
    split_if_too_big(potential_block.block, query);
    potential_block.block->is_free = false;
  }
  return potential_block;
}

static struct block_header *grow_heap(struct block_header *restrict last,
                                      size_t query) {
  struct region new_region = alloc_region(block_after(last), query);
  if (!region_is_invalid(&new_region)) {
    last->next = new_region.addr;
    if (new_region.extends) {
      if (try_merge_with_next(last)) {
        return last;
      } else {
        return new_region.addr;
      }
    } else {
      return new_region.addr;
    }
  } else {
    return NULL;
  }
}

/*  Реализует основную логику malloc и возвращает заголовок выделенного блока */
static struct block_header *memalloc(size_t query,
                                     struct block_header *heap_start) {
  struct block_search_result no_grow_result =
      try_memalloc_existing(query, heap_start);
  switch (no_grow_result.type) {
    case BSR_FOUND_GOOD_BLOCK: {
      return no_grow_result.block;
    }
    case BSR_REACHED_END_NOT_FOUND: {
      struct block_header *grown_block = grow_heap(no_grow_result.block, query);
      if (grown_block) {
        struct block_search_result grown_block_result =
            try_memalloc_existing(query, grown_block);
        if (grown_block_result.type == BSR_FOUND_GOOD_BLOCK)
          return grown_block_result.block;
        else
          goto exit_with_null;
      } else
        goto exit_with_null;
    }
    case BSR_CORRUPTED:
    exit_with_null:
      return NULL;
  }
}

void *_malloc(size_t query) {
  /*
   * Aligning queries to keep all
   * the blocks aligned (block headers are
   * already getting aligned in mem_internals.h).
   */
  query = (query + BLOCK_ALIGNMENT - 1) / BLOCK_ALIGNMENT * BLOCK_ALIGNMENT;
  struct block_header *const addr =
      memalloc(query, (struct block_header *)HEAP_START);
  if (addr)
    return addr->contents;
  else
    return NULL;
}

static struct block_header *block_get_header(void *contents) {
  return (struct block_header *)(((uint8_t *)contents) -
                                 offsetof(struct block_header, contents));
}

void _free(void *mem) {
  if (!mem) return;
  struct block_header *header = block_get_header(mem);
  header->is_free = true;
  while (try_merge_with_next(header))
    ;
}
