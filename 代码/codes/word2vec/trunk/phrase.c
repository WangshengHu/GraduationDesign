//  Copyright 2013 Google Inc. All Rights Reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#include <stdio.h>
#include <string.h>
#include <math.h>
#include <malloc.h>

const long long max_size = 2000;         // max length of strings
const long long N = 40;                  // number of closest words that will be shown
const long long max_w = 50;              // max length of vocabulary entries

int main(int argc, char **argv) {
  FILE *f, *outf;
  char file_name[max_size], save_file[max_size];
  long long words, size, a, b, i;
  char *vocab;
  float *M;
  if (argc < 2) {
    printf("Usage: ./distance <FILE> <SAVEPATH>\nwhere FILE contains word projections in the BINARY FORMAT, and the extracted phrases will be saved to SAVEPATH\n");
    return 0;
  }
  strcpy(file_name, argv[1]);
  strcpy(save_file, argv[2]);
  f = fopen(file_name, "rb");
  outf = fopen(save_file, "w");
  if (f == NULL) {
    printf("Input file not found\n");
    return -1;
  }
  fscanf(f, "%lld", &words);
  fscanf(f, "%lld", &size);
  vocab = (char *)malloc((long long)words * max_w * sizeof(char));
  M = (float *)malloc((long long)words * (long long)size * sizeof(float));
  if (M == NULL) {
    printf("Cannot allocate memory: %lld MB    %lld  %lld\n", (long long)words * size * sizeof(float) / 1048576, words, size);
    return -1;
  }
  for (b = 0; b < words; b++) {
    a = 0;
    while (1) {
      vocab[b * max_w + a] = fgetc(f);
      if (feof(f) || (vocab[b * max_w + a] == ' ')) break;
      if ((a < max_w) && (vocab[b * max_w + a] != '\n')) a++;
    }
    vocab[b * max_w + a] = 0;
    for (a = 0; a < size; a++) fread(&M[a + b * size], sizeof(float), 1, f);
  }
  fclose(f);
  for (i = 0; i < words; i++) {
    if (strstr(&vocab[i * max_w], "_")) {
      fprintf(outf, "%s", &vocab[i * max_w]);
      fprintf(outf, "\n");
    }
  }
  fclose(outf);
  return 0;
}
