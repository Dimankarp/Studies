//
// Created by dimankarp on 15/11/23.
//

#include "bmp_format.h"

#include <stdbool.h>
#include <stdlib.h>

#include "image.h"
#include "util.h"

struct __attribute__((packed)) bmp_header {
  uint8_t bfType[2];
  uint32_t bfileSize;
  uint32_t bfReserved;
  uint32_t bOffBits;
  uint32_t biSize;
  uint32_t biWidth;
  uint32_t biHeight;
  uint16_t biPlanes;
  uint16_t biBitCount;
  uint32_t biCompression;
  uint32_t biSizeImage;
  uint32_t biXPelsPerMeter;
  uint32_t biYPelsPerMeter;
  uint32_t biClrUsed;
  uint32_t biClrImportant;
};

static const uint32_t default_biSize = 40;

/*
 * It does copy image.h pixel, but who
 *  knows what could happen during development...
 */
struct bmp_pixel {
  uint8_t b, g, r;
};

static bool bmp_header_is_valid(const struct bmp_header *head) {
  if (head->bfType[0] != 'B' || head->bfType[1] != 'M') return false;
  if (head->biBitCount != 24) return false;
  return true;
}

static uint8_t get_row_padding(uint64_t width) {
  return (4 - (width * sizeof(struct bmp_pixel)) % 4) % 4;
}
bmp_status bmp_deserialize(const void *in, size_t byte_len, struct image *img,
                           struct bmp_header **head_out) {
  if (byte_len < sizeof(struct bmp_header)) return BMP_HEADER_SMALL;
  struct bmp_header head = *((struct bmp_header *)in);
  if (!bmp_header_is_valid(&head)) return BMP_INVALID_HEADER;
  if (head.bfileSize > byte_len) return BMP_FILE_CORRUPTED;

  struct bmp_header *header_copy = malloc(sizeof(struct bmp_header));
  if (header_copy == NULL) return BMP_MALLOC_FAILED;
  *header_copy = head;
  *head_out = header_copy;

  // Moving to the pixel grid
  in = (char *)in + head.bOffBits;

  uint8_t row_padding = get_row_padding(head.biWidth);
  struct pixel *pixel_data_head =
      malloc(head.biWidth * head.biHeight * sizeof(struct pixel));
  if (pixel_data_head == NULL) return BMP_MAP_FAIL;

  struct pixel *pixel_data_ptr = pixel_data_head;
  const size_t row_byte_size = head.biWidth * sizeof(struct bmp_pixel);

  for (size_t i = 0; i < head.biHeight; i++) {
    memcpy2(pixel_data_ptr, in, row_byte_size);
    pixel_data_ptr += head.biWidth;
    in = (char *)in + row_byte_size + row_padding;
  }

  *img = image_init(head.biWidth, head.biHeight, pixel_data_head);
  return BMP_OK;
}

size_t calc_bmp_format_size(struct image img) {
  uint8_t padding = get_row_padding(img.width);
  return sizeof(struct bmp_header) +
         img.width * img.height * sizeof(struct bmp_pixel) +
         padding * img.height;
}

bmp_status bmp_serialize(void *out, const struct bmp_header *bmp_head,
                         size_t byte_len, struct image img) {
  uint64_t estimated_size = calc_bmp_format_size(img);
  if (byte_len < estimated_size) return BMP_BUFFER_SMALL;

  uint8_t row_padding = get_row_padding(img.width);
  *(struct bmp_header *)out = *bmp_head;
  ((struct bmp_header *)out)->bfileSize = estimated_size;
  ((struct bmp_header *)out)->bOffBits = sizeof(struct bmp_header);
  ((struct bmp_header *)out)->biWidth = img.width;
  ((struct bmp_header *)out)->biHeight = img.height;
  ((struct bmp_header *)out)->biSize = default_biSize;
  out = ((struct bmp_header *)out) + 1;

  struct pixel *pixel_data_ptr = img.data;
  const size_t row_byte_size =
      img.width * sizeof(struct bmp_pixel) + row_padding;

  for (size_t i = 0; i < img.height; i++) {
    memcpy2(out, pixel_data_ptr, img.width * sizeof(struct bmp_pixel));
    pixel_data_ptr += img.width;
    out = (char *)out + row_byte_size;
  }

  return BMP_OK;
}
