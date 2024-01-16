//
// Created by dimankarp on 16/11/23.
//

#ifndef IMAGE_TRANSFORMER_ROTATE_TRANSF_H
#define IMAGE_TRANSFORMER_ROTATE_TRANSF_H
#include "image.h"
#include "transf_error.h"

/*
 * HOW THE IMAGE IS ROTATED (CONCEPTUALLY):
 * First of all each pixel's center is given a coordinate on a plane
 * centered around image's center (this center has, obviously, coordinates
 * (0,0)).
 *
 * Then resulting image size is calculated.
 * Since after transformation original edges would also become new edges -
 * the resulting image sizes are calculated based on coordinates of rotated
 * edges.
 *
 * After that a simple rotation transformation is applied (using rotation
 * matrix) to every point(backed by a pixel) of the image. Result is then
 * rounded and translated back to the pixel-grid coordinates (index in
 * pixel-data array, basically).
 *
 * It all would have been that easy if the coordinates center was always backed
 * up by a real pixel (real image index), but since images can be of even sizes (or even mixed: e.g height is even, width - not)
 * special cases must be checked for on every described step.
 *
 *     01234
 *    2*****
 *    1*****  <- This image has a Real center (1,2)
 *    0*****
 *
 *     012345
 *    2******
 *    1****** <- This image's center has imaginary column component and real row component.
 *    0******
 *
 *   The usual fix that is being applied is subtracting 1 from the coordinate when translating or skipping row/col in cycles.
 *   For details please navigate to rotate_transf.c
 *
 *  NOTE! Recently I've been thinking about another solution that centers around setting coordinate
 *  center in the lower left point (i.e. in the pixel-grid center), but I still didn't manage to get my head around it.
 */

transform_status image_get_rotated(struct image src, struct image *out,
                                   double radians);

#endif  // IMAGE_TRANSFORMER_ROTATE_TRANSF_H
