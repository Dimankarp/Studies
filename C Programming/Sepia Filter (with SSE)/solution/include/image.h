//
// Created by dimankarp on 15/11/23.
//

#ifndef IMAGE_TRANSFORMER_IMAGE_H
#define IMAGE_TRANSFORMER_IMAGE_H

#include <bits/stdint-uintn.h>

struct pixel {
  uint8_t b, g, r;
};

struct image {
  uint64_t width, height;
  struct pixel* data;
};

struct image image_init(uint64_t width, uint64_t height, struct pixel* data);

void image_uninit(struct image img);

#endif  // IMAGE_TRANSFORMER_IMAGE_H
