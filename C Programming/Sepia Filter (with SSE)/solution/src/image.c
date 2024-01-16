//
// Created by dimankarp on 15/11/23.
//
#include "image.h"

#include <malloc.h>

struct image image_init(uint64_t width, uint64_t height, struct pixel *data) {
  return (struct image){.width = width, .height = height, .data = data};
}

void image_uninit(struct image img) { free(img.data); }
