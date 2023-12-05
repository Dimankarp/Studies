//
// Created by dimankarp on 15/11/23.
//

#ifndef IMAGE_TRANSFORMER_FILE_HANDLER_H
#define IMAGE_TRANSFORMER_FILE_HANDLER_H

#include <stdio.h>

typedef enum fhandler_status {
  FL_OK = 0,
  FL_ERR,
  FL_MAP_FAIL,
  FL_STAT_FAIL,
  FL_CREATION_FAIL,
  FL_WRITE_FAIL,
  FL_UNMAP_FAIL
} fhandler_status;

static const char *const FHANDLER_STATUS_DESC[] = {
    [FL_OK] = "FL| Operation was successful!",
    [FL_ERR] = "FL| Unspecified error occurred.",
    [FL_UNMAP_FAIL] = "FL| Failed to unmap.",
    [FL_WRITE_FAIL] = "FL| Failed to write to a file.",
    [FL_MAP_FAIL] = "FL| Failed to execute mmap().",
    [FL_CREATION_FAIL] = "FL| Failed to create a file.",
    [FL_STAT_FAIL] = "FL| Failed to execute fstat()."};

struct mapped_file {
  void *adr;
  size_t size;
};

fhandler_status file_mmap(struct mapped_file *mfile, FILE *file, size_t len);
fhandler_status file_get_length(size_t *len, FILE *file);
fhandler_status file_create_mapped(struct mapped_file *to,
                                   const char *filename, size_t len);
fhandler_status mapped_file_close(struct mapped_file mfile);
fhandler_status file_create(FILE **to, const char *filename);
#endif  // IMAGE_TRANSFORMER_FILE_HANDLER_H
