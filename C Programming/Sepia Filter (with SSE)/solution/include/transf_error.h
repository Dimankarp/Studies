//
// Created by dimankarp on 16/11/23.
//

#ifndef IMAGE_TRANSFORMER_TRANSF_ERROR_H
#define IMAGE_TRANSFORMER_TRANSF_ERROR_H

typedef enum transform_status {
  TRNSF_OK = 0,
  TRNSF_ERR,
  TRNSF_WRONG_ARG,
  TRNSF_ALLOC_FAIL
} transform_status;

static const char* const TRANSFORM_STATUS_DESC[] = {
    [TRNSF_OK] = "TRNSF| Transform was successful!",
    [TRNSF_ERR] = "TRNSF| Unspecified error occurred.",
    [TRNSF_WRONG_ARG] =
        "TRNSF| Wrong argument was provided for transformation.",
    [TRNSF_ALLOC_FAIL] = "TRNSF| Failed to execute malloc().",
};

#endif  // IMAGE_TRANSFORMER_TRANSF_ERROR_H
