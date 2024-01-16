//
// Created by dimankarp on 28/12/23.
//

#ifndef IMAGE_TRANSFORMER_FILTER_UTIL_H
#define IMAGE_TRANSFORMER_FILTER_UTIL_H

#include "inttypes.h"

/*
 * Saturation arithmetic utilities:
 */
uint8_t sat(uint64_t val){
    if(val < 256) return val;
    else return 255;
}

#endif //IMAGE_TRANSFORMER_FILTER_UTIL_H
