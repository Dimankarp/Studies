file(GLOB_RECURSE sources CONFIGURE_DEPENDS
        src/*.c
        src/*.asm
        src/*.h
        include/*.h
)
add_executable(image-transformer ${sources})
target_include_directories(image-transformer PRIVATE src include)
target_link_libraries(image-transformer m)