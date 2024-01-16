//
// Created by dimankarp on 28/12/23.
//

#ifndef IMAGE_TRANSFORMER_FILTER_ERROR_H
#define IMAGE_TRANSFORMER_FILTER_ERROR_H

typedef enum filter_status {
    FLTR_OK = 0,
    FLTR_ALLOC_FAIL
} filter_status;

static const char* const FILTER_STATUS_DESC[] = {
        [FLTR_OK] = "FLTR| Filtering was successful!",
        [FLTR_ALLOC_FAIL] = "FLTR| Failed to execute malloc()."
};

#endif //IMAGE_TRANSFORMER_FILTER_ERROR_H
