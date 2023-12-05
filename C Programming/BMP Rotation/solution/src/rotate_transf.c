//
// Created by dimankarp on 16/11/23.
//

#include "rotate_transf.h"

#include <malloc.h>
#include <stddef.h>

#include "math.h"
#include "transf_util.h"

static struct dpoint ipoint_rotate(struct ipoint pt, double sin, double cos) {
  return (struct dpoint){// FIX! Type conversions could be messed up with imgs
                         // of more than 55million GB.
                         .x = (double)pt.x * cos + (double)pt.y * sin,
                         .y = -(double)pt.x * sin + (double)pt.y * cos};
}

transform_status image_get_rotated(struct image src, struct image *out,
                                   double radians) {
  struct ipoint edges[4];
  image_get_edges(edges, src);
  double cached_sin = sin(radians);
  double cached_cos = cos(radians);

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
  // Rotation is done by a multiple of 90, that is -  it's size saving.
  if (1 - fabs(cached_cos) < 0.0001f || 1 - fabs(cached_sin) < 0.0001f) {
    // From landscape to portrait transform (x-axis is swapped with y)
    if (fabs(cached_sin) > fabs(cached_cos)) {
      new_width = max_x - min_x + 1 - (cntr.ytype == IS_IMAGINERY);
      new_height = max_y - min_y + 1 - (cntr.xtype == IS_IMAGINERY);
    } else {
      new_width = max_x - min_x + 1 - (cntr.xtype == IS_IMAGINERY);
      new_height = max_y - min_y + 1 - (cntr.ytype == IS_IMAGINERY);
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

  for (int64_t y = edges[3].y; y <= edges[0].y; y++) {
    if (y == 0 && cntr.ytype == IS_IMAGINERY) continue;
    for (int64_t x = edges[0].x; x <= edges[1].x; x++) {
      if (x == 0 && cntr.xtype == IS_IMAGINERY) continue;
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
