# CMAKE generated file: DO NOT EDIT!
# Generated by CMake Version 3.26
cmake_policy(SET CMP0009 NEW)

# sources at CMakeLists.txt:1 (file)
file(GLOB_RECURSE NEW_GLOB FOLLOW_SYMLINKS LIST_DIRECTORIES false "/home/dimankarp/assignment-3-image-rotation/solution/include/*.h")
set(OLD_GLOB
  "/home/dimankarp/assignment-3-image-rotation/solution/include/bmp_format.h"
  "/home/dimankarp/assignment-3-image-rotation/solution/include/file_handler.h"
  "/home/dimankarp/assignment-3-image-rotation/solution/include/image.h"
  "/home/dimankarp/assignment-3-image-rotation/solution/include/rotate_transf.h"
  )
if(NOT "${NEW_GLOB}" STREQUAL "${OLD_GLOB}")
  message("-- GLOB mismatch!")
  file(TOUCH_NOCREATE "/home/dimankarp/assignment-3-image-rotation/solution/out/build/debug/CMakeFiles/cmake.verify_globs")
endif()

# sources at CMakeLists.txt:1 (file)
file(GLOB_RECURSE NEW_GLOB FOLLOW_SYMLINKS LIST_DIRECTORIES false "/home/dimankarp/assignment-3-image-rotation/solution/src/*.c")
set(OLD_GLOB
  "/home/dimankarp/assignment-3-image-rotation/solution/src/bmp_format.c"
  "/home/dimankarp/assignment-3-image-rotation/solution/src/file_handler.c"
  "/home/dimankarp/assignment-3-image-rotation/solution/src/image.c"
  "/home/dimankarp/assignment-3-image-rotation/solution/src/main.c"
  "/home/dimankarp/assignment-3-image-rotation/solution/src/rotate_transf.c"
  )
if(NOT "${NEW_GLOB}" STREQUAL "${OLD_GLOB}")
  message("-- GLOB mismatch!")
  file(TOUCH_NOCREATE "/home/dimankarp/assignment-3-image-rotation/solution/out/build/debug/CMakeFiles/cmake.verify_globs")
endif()

# sources at CMakeLists.txt:1 (file)
file(GLOB_RECURSE NEW_GLOB FOLLOW_SYMLINKS LIST_DIRECTORIES false "/home/dimankarp/assignment-3-image-rotation/solution/src/*.h")
set(OLD_GLOB
  )
if(NOT "${NEW_GLOB}" STREQUAL "${OLD_GLOB}")
  message("-- GLOB mismatch!")
  file(TOUCH_NOCREATE "/home/dimankarp/assignment-3-image-rotation/solution/out/build/debug/CMakeFiles/cmake.verify_globs")
endif()
