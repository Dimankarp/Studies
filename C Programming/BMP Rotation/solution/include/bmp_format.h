//
// Created by dimankarp on 15/11/23.
//

#ifndef IMAGE_TRANSFORMER_BMP_FORMAT_H
#define IMAGE_TRANSFORMER_BMP_FORMAT_H

#include <bits/types/FILE.h>

#include "file_handler.h"
#include "image.h"
struct bmp_header;

typedef enum bmp_status {
  BMP_OK = 0,
  BMP_ERR,
  BMP_HEADER_SMALL,
  BMP_INVALID_HEADER,
  BMP_FILE_CORRUPTED,
  BMP_MAP_FAIL,
  BMP_BUFFER_SMALL,
  BMP_MALLOC_FAILED
} bmp_status;

static const char *const BMP_STATUS_DESC[] = {
    [BMP_OK] = "BMP| Operation was successful!",
    [BMP_ERR] = "BMP| Unspecified error occurred.",
    [BMP_HEADER_SMALL] = "BMP| Provided BMP header was too small.",
    [BMP_INVALID_HEADER] = "BMP| Provided BMP header was invalid.",
    [BMP_FILE_CORRUPTED] = "BMP| Provided BMP file was corrupted.",
    [BMP_MAP_FAIL] = "BMP| Failed to execute mmap().",
    [BMP_BUFFER_SMALL] = "BMP| Provided buffer was too small.",
    [BMP_MALLOC_FAILED] = "BMP| Failed to execute malloc()."};

bmp_status bmp_deserialize(const void *in, size_t byte_len, struct image *img,
                           struct bmp_header **head_out);

size_t calc_bmp_format_size(struct image img);

bmp_status bmp_serialize(void *out, const struct bmp_header *bmp_head,
                         size_t byte_len, struct image img);

#endif  // IMAGE_TRANSFORMER_BMP_FORMAT_H
