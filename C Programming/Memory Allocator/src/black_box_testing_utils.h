//
// Created by dimankarp on 15/12/23.
//

#ifndef MEMORY_ALLOCATOR_BLACK_BOX_TESTING_UTILS_H
#define MEMORY_ALLOCATOR_BLACK_BOX_TESTING_UTILS_H

/*
 * Not really sure whether I should just include the
 * whole mem_internals.h here.
 */
#define BLOCK_MIN_CAPACITY 24
#define REGION_MIN_SIZE (2 * 4096)

enum malloc_test_result {
  MLC_OK = 0,
  MLC_HEAP_INIT_FAIL,
  MLC_MALLOC_FAIL,
  MLC_FREE_REUSE_FAIL,
  MLC_BLOCKING_REGION_FAIL

};
typedef enum malloc_test_result mlc_code;

static const char* malloc_test_result_desc[] = {
    [MLC_OK] = "Successfully passed test!",
    [MLC_HEAP_INIT_FAIL] = "Failed to init heap!",
    [MLC_MALLOC_FAIL] = "Failed to allocate query!",
    [MLC_FREE_REUSE_FAIL] = "Freed block wasn't reused!",
[MLC_BLOCKING_REGION_FAIL] = "Failed to block region extension with mmap!"};

#define CREATE_TEST(_name) static mlc_code malloc_test_##_name()

#define RUN_TEST(_name)                                                \
  printf("%.*s \n", 20, "--------------------------------");           \
  printf("Running test: " #_name " \n");                               \
  mlc_code result_##_name = malloc_test_##_name();                     \
  printf("%s : %s \n", result_##_name == MLC_OK ? "PASSED" : "FAILED", \
         malloc_test_result_desc[result_##_name]);                     \
  printf("%.*s \n", 20, "--------------------------------");

#define ALLOC_AND_FILL(_ptrs, _len, _sz)                            \
  void*(_ptrs)[(_len)];                                             \
  for (size_t i = 0; i < (_len); i++) {                             \
    (_ptrs)[i] = _malloc((_sz));                                    \
    if ((_ptrs)[i] == NULL) return MLC_MALLOC_FAIL;                 \
    for (size_t j = 0; j < (_sz); j++) ((char*)(_ptrs)[i])[j] = -1; \
  }

#define INIT_HEAP(_initial_size)           \
  void* heap = heap_init(REGION_MIN_SIZE); \
  if (heap == NULL) return MLC_HEAP_INIT_FAIL;
#endif  // MEMORY_ALLOCATOR_BLACK_BOX_TESTING_UTILS_H
