//
// Created by dimankarp on 17/11/23.
//

#include "transf_util.h"

#include "math.h"

void image_get_edges(struct ipoint *to, struct image src) {
  struct cpoint cntr = image_get_center(src);
  // From upper left clockwise
  to[0] = (struct ipoint){.x = -cntr.gridc.x, .y = cntr.gridc.y};
  to[1] = (struct ipoint){.x = cntr.gridc.x, .y = cntr.gridc.y};
  to[2] = (struct ipoint){.x = cntr.gridc.x, .y = -cntr.gridc.y};
  to[3] = (struct ipoint){.x = -cntr.gridc.x, .y = -cntr.gridc.y};
}

struct cpoint image_get_center(struct image src) {
  // NOTE! uint64_t/2 can in fact be stored in int64_t
  struct cpoint cntr;
  if (src.width % 2 == 0) {
    cntr.xtype = IS_IMAGINARY;
  } else {
    cntr.xtype = IS_REAL;
  }
  if (src.height % 2 == 0) {
    cntr.ytype = IS_IMAGINARY;
  } else {
    cntr.ytype = IS_REAL;
  }
  cntr.gridc = (struct ipoint){.x = (int64_t)(src.width / 2),
                               .y = (int64_t)(src.height / 2)};
  return cntr;
}

/*
 * NOTE! See dpoint, ipoint, gpoint description
 * in transf_util.h.
 */

struct gpoint dpoint_to_gpoint(struct dpoint pt, struct image img) {
  return ipoint_to_gpoint(dpoint_to_ipoint(pt), img);
}

struct ipoint dpoint_to_ipoint(struct dpoint pt) {
  return (struct ipoint){.x = lround(pt.x), .y = lround(pt.y)};
}

struct gpoint ipoint_to_gpoint(struct ipoint pt, struct image img) {
  struct cpoint cntr = image_get_center(img);
  struct gpoint ret_pt;
  ret_pt.col = pt.x + cntr.gridc.x;
  ret_pt.row = pt.y + cntr.gridc.y;

  /*
   * Since imaginary centers exist, fixing subtractions
   * must be done to mitigate for their existence on the
   * coordinate plain and non-existence in the pixel grid.
   */

  if (cntr.xtype == IS_IMAGINARY) {
    ret_pt.col -= (pt.x > 0);
  }
  if (cntr.ytype == IS_IMAGINARY) {
    ret_pt.row -= (pt.y > 0);
  }
  return ret_pt;
}
