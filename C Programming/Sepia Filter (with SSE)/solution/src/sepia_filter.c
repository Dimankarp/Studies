//
// Created by dimankarp on 28/12/23.
//

#include "sepia_filter.h"

#include <malloc.h>

#include "filter_util.h"
#include "image.h"

const float SEPIA_MTRX[3][3] = {
    {.393f, .769f, .189f}, {.349f, .686f, .168f}, {.272f, .543f, .131f}};

/*
 * Transposed version of the SEPIA_MTRX
 * which is used in the faster implementations.
 * The transformation applied to it isn't actually transposition.
 * It's firstly transposed and them mirrored relative to central column and
 * central row.
 * //formatter:off
 * 123    963
 * 456 -> 852
 * 789    741
 * //formatter:on
 */
const float SEPIA_MTRX_TRNSPSD[3][3] = {
    {.131f, .168f, .189f}, {.543f, .686f, .769f}, {.272f, .349f, .393f}};

static struct pixel pixel_get_sepia_filtered(struct pixel pixel) {
  struct pixel old = pixel;
  /*
   * NOTICE!: Conversions aren't narrowing as long as filter
   * matrix is positive (which in theory it should always be like).
   */
  pixel.r = sat((uint64_t)((float)old.r * SEPIA_MTRX[0][0]) +
                (uint64_t)((float)old.g * SEPIA_MTRX[0][1]) +
                (uint64_t)((float)old.b * SEPIA_MTRX[0][2]));
  pixel.g = sat((uint64_t)((float)old.r * SEPIA_MTRX[1][0]) +
                (uint64_t)((float)old.g * SEPIA_MTRX[1][1]) +
                (uint64_t)((float)old.b * SEPIA_MTRX[1][2]));
  pixel.b = sat((uint64_t)((float)old.r * SEPIA_MTRX[2][0]) +
                (uint64_t)((float)old.g * SEPIA_MTRX[2][1]) +
                (uint64_t)((float)old.b * SEPIA_MTRX[2][2]));
  return pixel;
}

filter_status image_get_sepia_filtered(struct image src, struct image *out) {
  struct pixel *pixel_data =
      malloc(src.height * src.width * sizeof(struct pixel));
  if (pixel_data == NULL) {
    return FLTR_ALLOC_FAIL;
  }
  struct pixel *src_data = src.data;
  struct image new_img = image_init(src.width, src.height, pixel_data);

  for (size_t row = 0; row < src.height; row++) {
    for (size_t col = 0; col < src.width; col++) {
      *pixel_data = pixel_get_sepia_filtered(*src_data);
      pixel_data++, src_data++;
    }
  }
  *out = new_img;
  return FLTR_OK;
}

/*
 * FAST IMPLEMENTATION
 */

static float const byte_to_float[] = {
    0.0f,   1.0f,   2.0f,   3.0f,   4.0f,   5.0f,   6.0f,   7.0f,   8.0f,
    9.0f,   10.0f,  11.0f,  12.0f,  13.0f,  14.0f,  15.0f,  16.0f,  17.0f,
    18.0f,  19.0f,  20.0f,  21.0f,  22.0f,  23.0f,  24.0f,  25.0f,  26.0f,
    27.0f,  28.0f,  29.0f,  30.0f,  31.0f,  32.0f,  33.0f,  34.0f,  35.0f,
    36.0f,  37.0f,  38.0f,  39.0f,  40.0f,  41.0f,  42.0f,  43.0f,  44.0f,
    45.0f,  46.0f,  47.0f,  48.0f,  49.0f,  50.0f,  51.0f,  52.0f,  53.0f,
    54.0f,  55.0f,  56.0f,  57.0f,  58.0f,  59.0f,  60.0f,  61.0f,  62.0f,
    63.0f,  64.0f,  65.0f,  66.0f,  67.0f,  68.0f,  69.0f,  70.0f,  71.0f,
    72.0f,  73.0f,  74.0f,  75.0f,  76.0f,  77.0f,  78.0f,  79.0f,  80.0f,
    81.0f,  82.0f,  83.0f,  84.0f,  85.0f,  86.0f,  87.0f,  88.0f,  89.0f,
    90.0f,  91.0f,  92.0f,  93.0f,  94.0f,  95.0f,  96.0f,  97.0f,  98.0f,
    99.0f,  100.0f, 101.0f, 102.0f, 103.0f, 104.0f, 105.0f, 106.0f, 107.0f,
    108.0f, 109.0f, 110.0f, 111.0f, 112.0f, 113.0f, 114.0f, 115.0f, 116.0f,
    117.0f, 118.0f, 119.0f, 120.0f, 121.0f, 122.0f, 123.0f, 124.0f, 125.0f,
    126.0f, 127.0f, 128.0f, 129.0f, 130.0f, 131.0f, 132.0f, 133.0f, 134.0f,
    135.0f, 136.0f, 137.0f, 138.0f, 139.0f, 140.0f, 141.0f, 142.0f, 143.0f,
    144.0f, 145.0f, 146.0f, 147.0f, 148.0f, 149.0f, 150.0f, 151.0f, 152.0f,
    153.0f, 154.0f, 155.0f, 156.0f, 157.0f, 158.0f, 159.0f, 160.0f, 161.0f,
    162.0f, 163.0f, 164.0f, 165.0f, 166.0f, 167.0f, 168.0f, 169.0f, 170.0f,
    171.0f, 172.0f, 173.0f, 174.0f, 175.0f, 176.0f, 177.0f, 178.0f, 179.0f,
    180.0f, 181.0f, 182.0f, 183.0f, 184.0f, 185.0f, 186.0f, 187.0f, 188.0f,
    189.0f, 190.0f, 191.0f, 192.0f, 193.0f, 194.0f, 195.0f, 196.0f, 197.0f,
    198.0f, 199.0f, 200.0f, 201.0f, 202.0f, 203.0f, 204.0f, 205.0f, 206.0f,
    207.0f, 208.0f, 209.0f, 210.0f, 211.0f, 212.0f, 213.0f, 214.0f, 215.0f,
    216.0f, 217.0f, 218.0f, 219.0f, 220.0f, 221.0f, 222.0f, 223.0f, 224.0f,
    225.0f, 226.0f, 227.0f, 228.0f, 229.0f, 230.0f, 231.0f, 232.0f, 233.0f,
    234.0f, 235.0f, 236.0f, 237.0f, 238.0f, 239.0f, 240.0f, 241.0f, 242.0f,
    243.0f, 244.0f, 245.0f, 246.0f, 247.0f, 248.0f, 249.0f, 250.0f, 251.0f,
    252.0f, 253.0f, 254.0f, 255.0f,
};

/*
 * Since this implementation of the filter uses SSE
 * instructions, a batch of several pixels of the image
 * is filtered at once, so it's more efficient to handle all
 * of the full batches with SSE-ful function and then handle
 * the rest with naive C implementation.
 *
 * This variable is initialized in fast_sepia.asm.
 */
extern const uint8_t PIXELS_PER_BATCH;

extern void filter_batch(float batch[3][PIXELS_PER_BATCH],
                         struct pixel[static PIXELS_PER_BATCH]);

filter_status image_get_fast_sepia_filtered(struct image src,
                                            struct image *out) {
  struct pixel *pixel_data =
      malloc(src.height * src.width * sizeof(struct pixel));
  if (pixel_data == NULL) {
    return FLTR_ALLOC_FAIL;
  }
  struct pixel *src_data = src.data;
  struct image new_img = image_init(src.width, src.height, pixel_data);

  /*
   * Batch is a matrix of 3 color rows and PIXELS_PER_BATCH pixel cols
   */

  for (size_t batch = 0; batch < src.height * src.width / PIXELS_PER_BATCH;
       batch++) {
    float curr_batch[3][PIXELS_PER_BATCH];

    for (size_t i = 0; i < PIXELS_PER_BATCH; i++) {
      curr_batch[0][i] = byte_to_float[src_data->b];
      curr_batch[1][i] = byte_to_float[src_data->g];
      curr_batch[2][i] = byte_to_float[src_data->r];
      src_data++;
    }
    filter_batch(curr_batch, pixel_data);
    pixel_data += PIXELS_PER_BATCH;
  }

  for (size_t i = 0; i < (src.width * src.height) % PIXELS_PER_BATCH; i++) {
    *pixel_data = pixel_get_sepia_filtered(*src_data);
    pixel_data++;
    src_data++;
  }

  *out = new_img;
  return FLTR_OK;
}
