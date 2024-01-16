//
// Created by dimankarp on 28/12/23.
//

#ifndef IMAGE_TRANSFORMER_SEPIA_FILTER_H
#define IMAGE_TRANSFORMER_SEPIA_FILTER_H

#include "filter_error.h"
#include "image.h"

filter_status image_get_sepia_filtered(struct image src, struct image *out);
filter_status image_get_fast_sepia_filtered(struct image src, struct image *out);

#endif //IMAGE_TRANSFORMER_SEPIA_FILTER_H
