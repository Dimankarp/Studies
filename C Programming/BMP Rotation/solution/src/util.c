//
// Created by dimankarp on 19/11/23.
//

#include "util.h"

void memcpy2(void *dest, const void *in, size_t n){
  for(size_t i = 0; i < n; i++){
    *((char*)dest) = *((char*)in);
    dest = (char*)dest + 1;
    in = (char*)in + 1;
  }
}
