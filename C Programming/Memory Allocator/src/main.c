//
// Created by dimankarp on 15/12/23.
//
#define _GNU_SOURCE
#include <stdio.h>
#include <sys/mman.h>

#include "black_box_testing_utils.h"
#include "mem.h"

/*
 * Custom tests
 */

CREATE_TEST(regular_alloc) {
  INIT_HEAP(REGION_MIN_SIZE)

  ALLOC_AND_FILL(ptrs, 10, REGION_MIN_SIZE / 10)
  heap_term();
  return MLC_OK;
}

CREATE_TEST(unconsecutive_free) {
  INIT_HEAP(REGION_MIN_SIZE)

  ALLOC_AND_FILL(ptrs, 10, REGION_MIN_SIZE / 10)
  _free(ptrs[0]);
  _free(ptrs[3]);
  _free(ptrs[9]);
  ALLOC_AND_FILL(new_ptrs, 3, REGION_MIN_SIZE / 10);
  if (ptrs[0] != new_ptrs[0]) return MLC_FREE_REUSE_FAIL;
  if (ptrs[9] != new_ptrs[2]) return MLC_FREE_REUSE_FAIL;
  heap_term();
  return MLC_OK;
}

CREATE_TEST(consecutive_free) {
  INIT_HEAP(REGION_MIN_SIZE)

  ALLOC_AND_FILL(ptrs, 10, REGION_MIN_SIZE / 10)
  for (size_t i = 0; i < 10; i++) {
    _free(ptrs[i]);
  }
  ALLOC_AND_FILL(new_ptrs, 10, REGION_MIN_SIZE / 10);
  for (size_t i = 0; i < 10; i++) {
    if (ptrs[i] != new_ptrs[i]) return MLC_FREE_REUSE_FAIL;
  }

  heap_term();
  return MLC_OK;
}

CREATE_TEST(extend_region) {
  INIT_HEAP(REGION_MIN_SIZE/2)

  ALLOC_AND_FILL(ptrs, 4, REGION_MIN_SIZE/2 / 5)
  void* new_ptr = _malloc(REGION_MIN_SIZE / 2);
  if (new_ptr == NULL) return MLC_MALLOC_FAIL;
  for (size_t i = 0; i < REGION_MIN_SIZE / 2; i++) {
    *(char*)(new_ptr + i) = -1;
  }
  for (size_t i = 0; i < 4; i++) {
    _free(ptrs[i]);
  }
  _free(new_ptr);
  heap_term();
  return MLC_OK;
}

CREATE_TEST(region_alloc_jump) {
  INIT_HEAP(REGION_MIN_SIZE)

  ALLOC_AND_FILL(ptrs, 9, REGION_MIN_SIZE/ 10)
  void* mapped_ptr = mmap(heap + REGION_MIN_SIZE*2, REGION_MIN_SIZE, PROT_READ | PROT_WRITE,
                          MAP_PRIVATE | MAP_ANONYMOUS | MAP_FIXED_NOREPLACE, -1, 0);
  if(mapped_ptr == MAP_FAILED) return MLC_BLOCKING_REGION_FAIL;

  void* new_ptr = _malloc(REGION_MIN_SIZE*3);
  if (new_ptr == NULL) return MLC_MALLOC_FAIL;

  for (size_t i = 0; i < REGION_MIN_SIZE*3; i++) {
    *(char*)(new_ptr + i) = -1;
  }
  _free(new_ptr);
  heap_term();
  return MLC_OK;
}

int main(void) {
  RUN_TEST(regular_alloc)
  RUN_TEST(unconsecutive_free)
  RUN_TEST(consecutive_free)
  RUN_TEST(extend_region)
  RUN_TEST(region_alloc_jump)
}