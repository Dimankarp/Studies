# CMAKE generated file: DO NOT EDIT!
# Generated by CMake Version 3.26
cmake_policy(SET CMP0009 NEW)

# test_directories at CMakeLists.txt:10 (file)
file(GLOB NEW_GLOB LIST_DIRECTORIES true "/home/dimankarp/assignment-3-image-rotation/tester/tests/*")
set(OLD_GLOB
  "/home/dimankarp/assignment-3-image-rotation/tester/tests/.gitignore"
  "/home/dimankarp/assignment-3-image-rotation/tester/tests/1"
  "/home/dimankarp/assignment-3-image-rotation/tester/tests/2"
  "/home/dimankarp/assignment-3-image-rotation/tester/tests/3"
  "/home/dimankarp/assignment-3-image-rotation/tester/tests/4"
  "/home/dimankarp/assignment-3-image-rotation/tester/tests/5"
  "/home/dimankarp/assignment-3-image-rotation/tester/tests/6"
  )
if(NOT "${NEW_GLOB}" STREQUAL "${OLD_GLOB}")
  message("-- GLOB mismatch!")
  file(TOUCH_NOCREATE "/home/dimankarp/assignment-3-image-rotation/tester/out/build/msan/CMakeFiles/cmake.verify_globs")
endif()

# sources at CMakeLists.txt:1 (file)
file(GLOB_RECURSE NEW_GLOB FOLLOW_SYMLINKS LIST_DIRECTORIES false "/home/dimankarp/assignment-3-image-rotation/tester/include/*.h")
set(OLD_GLOB
  "/home/dimankarp/assignment-3-image-rotation/tester/include/bmp.h"
  "/home/dimankarp/assignment-3-image-rotation/tester/include/cmp.h"
  "/home/dimankarp/assignment-3-image-rotation/tester/include/common.h"
  "/home/dimankarp/assignment-3-image-rotation/tester/include/dimensions.h"
  "/home/dimankarp/assignment-3-image-rotation/tester/include/file_cmp.h"
  "/home/dimankarp/assignment-3-image-rotation/tester/include/image.h"
  "/home/dimankarp/assignment-3-image-rotation/tester/include/io.h"
  )
if(NOT "${NEW_GLOB}" STREQUAL "${OLD_GLOB}")
  message("-- GLOB mismatch!")
  file(TOUCH_NOCREATE "/home/dimankarp/assignment-3-image-rotation/tester/out/build/msan/CMakeFiles/cmake.verify_globs")
endif()

# sources at CMakeLists.txt:1 (file)
file(GLOB_RECURSE NEW_GLOB FOLLOW_SYMLINKS LIST_DIRECTORIES false "/home/dimankarp/assignment-3-image-rotation/tester/src/*.c")
set(OLD_GLOB
  "/home/dimankarp/assignment-3-image-rotation/tester/src/bmp.c"
  "/home/dimankarp/assignment-3-image-rotation/tester/src/file_cmp.c"
  "/home/dimankarp/assignment-3-image-rotation/tester/src/main.c"
  "/home/dimankarp/assignment-3-image-rotation/tester/src/util.c"
  )
if(NOT "${NEW_GLOB}" STREQUAL "${OLD_GLOB}")
  message("-- GLOB mismatch!")
  file(TOUCH_NOCREATE "/home/dimankarp/assignment-3-image-rotation/tester/out/build/msan/CMakeFiles/cmake.verify_globs")
endif()

# sources at CMakeLists.txt:1 (file)
file(GLOB_RECURSE NEW_GLOB FOLLOW_SYMLINKS LIST_DIRECTORIES false "/home/dimankarp/assignment-3-image-rotation/tester/src/*.h")
set(OLD_GLOB
  )
if(NOT "${NEW_GLOB}" STREQUAL "${OLD_GLOB}")
  message("-- GLOB mismatch!")
  file(TOUCH_NOCREATE "/home/dimankarp/assignment-3-image-rotation/tester/out/build/msan/CMakeFiles/cmake.verify_globs")
endif()
