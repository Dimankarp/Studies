//
// Created by dimankarp on 17/11/23.
//

#ifndef IMAGE_TRANSFORMER_TRANSF_UTIL_H
#define IMAGE_TRANSFORMER_TRANSF_UTIL_H

#include <bits/stdint-intn.h>
#include <stddef.h>

#include "image.h"
/*
 * For all transform util functions:
 *
 *  1. For all mathematical coords center is (0,0)
 *
 */

/*
 * Point with double components. Usually represents
 * pixel position after transformation and before rounding.
 */
struct dpoint {
  double x, y;
};
/*
 * Point with integer components. Usually represents
 * pixels position before transformation or after rounding.
 */
struct ipoint {
  int64_t x, y;
};
/*
 * Point position within pixel-grid (center in lower-left corner).
 */
struct gpoint {
  int64_t row, col;
};

/*
 * Since in some cases (mostly when image sizes are even)
 * center coordinate doesn't have a real pixel under it in the grid
 * special modificators for center coordinates are introduced.
 */
enum cpoint_coords_types { IS_REAL, IS_IMAGINARY };

struct cpoint {
  enum cpoint_coords_types xtype;
  enum cpoint_coords_types ytype;
  struct ipoint gridc;
};

struct gpoint ipoint_to_gpoint(struct ipoint pt, struct image img);

struct ipoint dpoint_to_ipoint(struct dpoint pt);

struct gpoint dpoint_to_gpoint(struct dpoint pt, struct image img);

struct cpoint image_get_center(struct image src);

void image_get_edges(struct ipoint *to, struct image src);

#endif  // IMAGE_TRANSFORMER_TRANSF_UTIL_H
