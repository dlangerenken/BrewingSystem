#ifndef IO_H
#define IO_H
  #ifndef STDIO_H
  #define STDIO_H
  #include <stdio.h>
  #endif
  #ifndef STDLIB_H
  #define STDLIB_H
  #include <stdlib.h>
  #endif
  #ifndef STRING_H
  #define STRING_H
  #include <string.h>
  #endif
typedef struct settings{
  char *url;
  int port;
  int sleepDelay;
  int maxFailedTries;
} SETTINGS;

  #define FILE_PATH "/home/patrick/.security.config"

  #define DEFAULT_URL "brewberry.localhost"
  #define DEFAULT_PORT 1337
  #define DEFAULT_SLEEP_DELAY 5
  #define DEFAULT_MAX_FAILED_TRIES 10

  #define URL_TAG "url"
  #define PORT_TAG "port"
  #define SLEEP_DELAY_TAG "sleep"
  #define FAILED_TRIES_TAG "tries"
  
  /* reads the file path and returns an array of strings whichs length is saved int numberOfLines */
  char **readLines(char *path, int *numberOfLines);

  /* allocs memory for tags and values with size size */
  void init(char ***tags, char ***values, int size);

  /* searches for tags in lines and extracts the values to these tags */
  int getValues(char *lines[], char *tags[], int numberOfLines, int numberOfTags, char *(*values[]));

  /* copies the values to the SETTINGS struct in the corresponding file type */
  int fillResult(char **values, SETTINGS *result);
  
  /* loads the settings from the given path. if there is no file, one is created and filled with the default values */
  SETTINGS loadSettings(char *path);
  /* stores the settings to the specified path, overrides everything that was there before */
  int storeSettings(char *path, SETTINGS s); 

  /* prints the setting the way they are written in the file */
  void printSettings(SETTINGS s);

#endif
