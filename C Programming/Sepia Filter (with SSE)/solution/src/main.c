#include <stdio.h>
#include <stdlib.h>
#include <sys/resource.h>
/*
 * Importing getopt();
 */
#define __USE_POSIX2

#include <inttypes.h>
#include <stdbool.h>
#include <unistd.h>

#include "bmp_format.h"
#include "file_handler.h"
#include "rotate_transf.h"
#include "sepia_filter.h"

static _Noreturn void usage(char *msg) {
  fprintf(
      stderr,
      "%s. Expected usage: image-transformer [-r <angle>, -f] <source-image> "
      "<transformed-image> ",
      msg);
  exit(1);
}

static _Noreturn void error(int error_code,
                            const char *const *code_desc_table) {
  fprintf(stderr, "[ERROR]: %s\n", code_desc_table[error_code]);
  exit(1);
}

static _Noreturn void error_custom(const char *msg) {
  fprintf(stderr, "[ERROR]: %s\n", msg);
  exit(1);
}

static void notice(const char *msg) { fprintf(stderr, "[NOTICE]: %s\n", msg); }

int main(int argc, char **argv) {
  int opt;
  bool is_rotation_queried = false;
  double rotation_radians = 0;

  bool is_filtering_queried = false;
  bool is_fast_filtering_queried = false;

  while ((opt = getopt(argc, argv, "r:fF")) != -1) {
    switch (opt) {
      /*
       * Querying Rotation
       */
      case 'r': {
        is_rotation_queried = true;
        rotation_radians = strtod(optarg, NULL) * (3.1415 / 180.0);
        break;
      }
        /*
         * Querying Filtering
         */
      case 'f': {
        is_filtering_queried = true;
        break;
      }
      case 'F': {
        is_fast_filtering_queried = true;
      }
      default: {
        break;
      }
    }
  }
  /*
   * Checking for mandatory arguments
   */
  if (argv[optind] == NULL || argv[optind + 1] == NULL) {
    usage("Mandatory arguments are missing");
  }
  const char *const src_filename = argv[optind];
  const char *const out_filename = argv[optind + 1];

  FILE *src_file = fopen(src_filename, "rb");
  if (src_file == NULL) {
    error_custom("Failed to open source file.\n");
  }

  // Getting source file length
  size_t src_file_len = 0;
  fhandler_status fl_status = file_get_length(&src_file_len, src_file);
  if (fl_status != FL_OK) {
    error(fl_status, FHANDLER_STATUS_DESC);
  }

  // Mapping source file into memory
  struct mapped_file mfile = {0};
  fl_status = file_mmap(&mfile, src_file, src_file_len);
  if (fl_status != FL_OK) {
    error(fl_status, FHANDLER_STATUS_DESC);
  }
  fclose(src_file);

  // BMP deserialization
  struct image src_image = {0};
  struct bmp_header *head = NULL;

  bmp_status bmp_status =
      bmp_deserialize(mfile.adr, mfile.size, &src_image, &head);
  if (bmp_status != BMP_OK) {
    error(bmp_status, BMP_STATUS_DESC);
  }
  mapped_file_close(mfile);

  // Image transformation and filtering

  /*
   * NOTICE!: result_image always contains the image
   * currently mapped into memory, so by uninitializing
   * result_image in the end of main - the memory is totally
   * free.
   */
  struct image result_image = src_image;

  struct rusage usg;
  struct timeval start;
  struct timeval end;
  getrusage(RUSAGE_SELF, &usg);
  start = usg.ru_utime;

  /*
   * Transformations and filtering
   */
  if (is_rotation_queried) {
    transform_status trnsf_status =
        image_get_rotated(src_image, &result_image, rotation_radians);
    if (trnsf_status != TRNSF_OK) {
      error(trnsf_status, TRANSFORM_STATUS_DESC);
    }
    image_uninit(src_image);
    src_image = result_image;
  }

  if (is_filtering_queried) {
    filter_status fltr_status;

    if (is_fast_filtering_queried) {
      fltr_status = image_get_fast_sepia_filtered(src_image, &result_image);
    } else {
      fltr_status = image_get_sepia_filtered(src_image, &result_image);
    }

    if (fltr_status != FLTR_OK) {
      error(fltr_status, FILTER_STATUS_DESC);
    }
    image_uninit(src_image);
    src_image = result_image;
  }

  // Saving statistics
  getrusage(RUSAGE_SELF, &usg);
  end = usg.ru_utime;
  uint64_t elapsed =
      ((end.tv_sec - start.tv_sec) * 1000000L) + end.tv_usec - start.tv_usec;
  char buf[1024];

  snprintf(buf, sizeof(buf), //NOLINT: It's secure enough
           "The transformations section passed in [TRNSF_ELAPSED]: %" PRIi64,
           elapsed);

  notice(buf);

  // Image serializing into BMP
  struct mapped_file resmfile = {0};

  fl_status = file_create_mapped(&resmfile, out_filename,
                                 calc_bmp_format_size(result_image));
  if (fl_status != FL_OK) {
    error(fl_status, FHANDLER_STATUS_DESC);
  }

  bmp_status = bmp_serialize(resmfile.adr, head, resmfile.size, result_image);
  if (bmp_status != BMP_OK) {
    error(bmp_status, BMP_STATUS_DESC);
  }

  free(head);
  image_uninit(result_image);
  mapped_file_close(resmfile);
  return 0;
}
