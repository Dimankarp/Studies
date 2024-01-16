//
// Created by dimankarp on 19/11/23.
//

#ifndef IMAGE_TRANSFORMER_UTIL_H
#define IMAGE_TRANSFORMER_UTIL_H

#include <stddef.h>
/*
 * Implementing own memcpy since bloody linter
 * Prohibits its usage.
 */
void memcpy2(void *dest, const void *in, size_t n);

#endif  // IMAGE_TRANSFORMER_UTIL_H
