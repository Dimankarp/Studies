//
// Created by dimankarp on 16/11/23.
//

#include "rotate_transf.h"

#include <malloc.h>
#include <stddef.h>

#include "math.h"
#include "transf_util.h"

/*
 * For the description on the rotating method and definitions
 * go to rotate_transf.h
 */

static struct dpoint ipoint_rotate(struct ipoint pt, double sin, double cos) {
  /*
   * NOTE! Potentially there could be inaccurate type conversion.
   * Thankfully - only with images of size at least 55Mill GB.
   */
  return (struct dpoint){.x = (double)pt.x * cos + (double)pt.y * sin,
                         .y = -(double)pt.x * sin + (double)pt.y * cos};
}

transform_status image_get_rotated(struct image src, struct image *out,
                                   double radians) {
  struct ipoint edges[4];
  image_get_edges(edges, src);
  double cached_sin = sin(radians);
  double cached_cos = cos(radians);

  /*
   * Rotating edges to determine result-image sizes.
   * (i. e. new_width and new_height)
   */
  struct ipoint rotated_edges[4];
  int64_t max_x = 0;
  int64_t max_y = 0;
  int64_t min_y = 0;
  int64_t min_x = 0;
  for (size_t i = 0; i < sizeof(edges) / sizeof(*edges); i++) {
    rotated_edges[i] =
        dpoint_to_ipoint(ipoint_rotate(edges[i], cached_sin, cached_cos));
    if (rotated_edges[i].x > max_x)
      max_x = rotated_edges[i].x;
    else if (rotated_edges[i].x < min_x)
      min_x = rotated_edges[i].x;

    if (rotated_edges[i].y > max_y)
      max_y = rotated_edges[i].y;
    else if (rotated_edges[i].y < min_y)
      min_y = rotated_edges[i].y;
  }

  uint64_t new_width;
  uint64_t new_height;

  struct cpoint cntr = image_get_center(src);

  /*
   * Since center can be imaginary - additional check are made
   * and fixes applied... But only for rotation by an angle - multiple of 90.
   *
   * With rotation by an arbitrary angle the notion of center and mainly
   * lines that connect edges and go through center deforms, so precise size
   * calculation is difficult. (So I don't do it here)
   */
  if (1 - fabs(cached_cos) < 0.0001f || 1 - fabs(cached_sin) < 0.0001f) {
    // From landscape to portrait transform (x-axis is swapped with y)
    if (fabs(cached_sin) > fabs(cached_cos)) {
      new_width = max_x - min_x + 1 - (cntr.ytype == IS_IMAGINARY);
      new_height = max_y - min_y + 1 - (cntr.xtype == IS_IMAGINARY);
    } else {
      new_width = max_x - min_x + 1 - (cntr.xtype == IS_IMAGINARY);
      new_height = max_y - min_y + 1 - (cntr.ytype == IS_IMAGINARY);
    }
  } else {
    new_width = max_x - min_x + 1;
    new_height = max_y - min_y + 1;
  }

  struct pixel *pixel_data =
      malloc(new_height * new_width * sizeof(struct pixel));
  if (pixel_data == NULL) {
    return TRNSF_ALLOC_FAIL;
  }
  struct pixel *src_data = src.data;
  struct image new_img = image_init(new_width, new_height, pixel_data);

  /*
   * Applying transformation to every pixel, skipping
   * imaginary rows and columns.
   *
   * NOTE! There is a bug: when image is rotated by an arbitrary angle
   * a black line can appear in the middle of it. I bet the source is here.
   */
  for (int64_t y = edges[3].y; y <= edges[0].y; y++) {
    if (y == 0 && cntr.ytype == IS_IMAGINARY) continue;
    for (int64_t x = edges[0].x; x <= edges[1].x; x++) {
      if (x == 0 && cntr.xtype == IS_IMAGINARY) continue;
      struct gpoint grid_pos =
          dpoint_to_gpoint(ipoint_rotate((struct ipoint){.x = x, .y = y},
                                         cached_sin, cached_cos),
                           new_img);
      pixel_data[grid_pos.row * new_width + grid_pos.col] = *src_data;
      src_data++;
    }
  }

  *out = new_img;
  return TRNSF_OK;
}
