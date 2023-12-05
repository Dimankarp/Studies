#include <stdio.h>
#include <stdlib.h>

#include "bmp_format.h"
#include "file_handler.h"
#include "rotate_transf.h"

static _Noreturn void usage(char* msg) {
  fprintf(stderr,
          "%s. Expected usage: image-transformer <source-image> "
          "<transformed-image> <angle>",
          msg);
  exit(1);
}

static _Noreturn void error(int error_code,
                            const char* const* code_desc_table) {
  fprintf(stderr, "[ERROR]: %s\n", code_desc_table[error_code]);
  exit(1);
}

static _Noreturn void error_custom(const char* msg) {
  fprintf(stderr, "[ERROR]: %s\n", msg);
  exit(1);
}
/*
static  void warn(int error_code,  const char* const* code_desc_table){
    fprintf(stderr, "[WARN]: %s\n", code_desc_table[error_code]);
}

*/
int main(int argc, char** argv) {
  /*
   * HOW TO USE INSTRUCTIONS SHOULD BE SOMEWHERE HERE
   * /image-transformer <source-image> <transformed-image> <angle>
   */
  //(void) argc; (void) argv; // supress 'unused parameters' warning
  if (argc != 4) {
    usage("Not enough arguments");
  }

  FILE* src_file = fopen(argv[1], "rb");
  if (src_file == NULL) {
    error_custom("Failed to open source file.\n");
  }

  size_t src_file_len;
  fhandler_status fl_status = file_get_length(&src_file_len, src_file);
  if (fl_status != FL_OK) {
    error(fl_status, FHANDLER_STATUS_DESC);
  }

  struct mapped_file mfile;

  fl_status = file_mmap(&mfile, src_file, src_file_len);
  if (fl_status != FL_OK) {
    error(fl_status, FHANDLER_STATUS_DESC);
  }

  fclose(src_file);

  struct image img;
  struct bmp_header* head;

  bmp_status bmp_status = bmp_deserialize(mfile.adr, mfile.size, &img, &head);
  if (bmp_status != BMP_OK) {
    error(bmp_status, BMP_STATUS_DESC);
  }

  mapped_file_close(mfile);

  struct image rotated_img;

  double radians = atof(argv[3]) * (3.1415 / 180.0);  // Move to separate func
  transform_status trnsf_status = image_get_rotated(img, &rotated_img, radians);

  if (trnsf_status != TRNSF_OK) {
    error(trnsf_status, TRANSFORM_STATUS_DESC);
  }

  image_uninit(img);

  struct mapped_file resmfile;

  fl_status =
      file_create_mapped(&resmfile, argv[2], calc_bmp_format_size(rotated_img));
  if (fl_status != FL_OK) {
    error(fl_status, FHANDLER_STATUS_DESC);
  }

  bmp_status = bmp_serialize(resmfile.adr, head, resmfile.size, rotated_img);
  if (bmp_status != BMP_OK) {
    error(bmp_status, BMP_STATUS_DESC);
  }

  free(head);
  image_uninit(rotated_img);
  mapped_file_close(resmfile);
  return 0;
}
