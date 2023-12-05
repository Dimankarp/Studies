//
// Created by dimankarp on 15/11/23.
//
#include <sys/mman.h>
#include <sys/stat.h>
#define __USE_POSIX
#define __USE_XOPEN_EXTENDED
#include <unistd.h>

#include "file_handler.h"

fhandler_status file_get_length(size_t *len, FILE *file) {
  struct stat stats;
  if (fstat(fileno(file), &stats) == 0) {
    *len = stats.st_size;
    return FL_OK;
  } else {
    return FL_STAT_FAIL;
  }
}

fhandler_status file_mmap(struct mapped_file *mfile, FILE *file,
                          size_t file_len) {
  void *mapped_file_pntr =
      mmap(NULL, file_len, PROT_READ, MAP_PRIVATE, fileno(file), 0);
  if (mapped_file_pntr != MAP_FAILED) {
    *mfile = (struct mapped_file){.adr = mapped_file_pntr, .size = file_len};
    return FL_OK;
  } else {
    return FL_MAP_FAIL;
  }
}

fhandler_status file_create_mapped(struct mapped_file *to, const char *filename,
                                   size_t len) {
  FILE *new_file;
  if (file_create(&new_file, filename) != FL_OK) return FL_CREATION_FAIL;

  ftruncate(fileno(new_file), (__off_t)len);

  void *mapped_file_pntr =
      mmap(NULL, len, PROT_READ | PROT_WRITE, MAP_SHARED, fileno(new_file), 0);

  fclose(new_file);

  if (mapped_file_pntr != MAP_FAILED) {
    *to = (struct mapped_file){.adr = mapped_file_pntr, .size = len};
    return FL_OK;
  } else {
    return FL_MAP_FAIL;
  }
}

fhandler_status mapped_file_close(struct mapped_file mfile) {
  if (munmap(mfile.adr, mfile.size) == 0) return FL_OK;
  return FL_UNMAP_FAIL;
}

fhandler_status file_create(FILE **to, const char *filename) {
  FILE *file = fopen(filename, "w+");
  if (file == NULL)
    return FL_CREATION_FAIL;
  else {
    *to = file;
    return FL_OK;
  }
}
