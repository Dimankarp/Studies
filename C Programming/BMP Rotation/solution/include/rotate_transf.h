//
// Created by dimankarp on 16/11/23.
//

#ifndef IMAGE_TRANSFORMER_ROTATE_TRANSF_H
#define IMAGE_TRANSFORMER_ROTATE_TRANSF_H
#include "image.h"
#include "transf_error.h"

transform_status image_get_rotated(struct image src, struct image *out,
                                   double radians);

#endif  // IMAGE_TRANSFORMER_ROTATE_TRANSF_H
