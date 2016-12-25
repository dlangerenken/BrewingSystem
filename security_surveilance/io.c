#include "io.h"

char **readLines(char *path, int *numberOfLines){
  int filesize = 0, linescount = 1, currentline = 0, i = 0, lasti = 0, j = 0;
  char **lines, *file, input;
  FILE *f;

  f = fopen(path, "r");
  if(f == NULL){
    printf("Could not open file: %s\n", path);
    return NULL;
  }
  while((input = fgetc(f)) != EOF){
    if(input == '\n'){
	    linescount++;
    }
    filesize++;
  }
  file = malloc(filesize * sizeof(char));
  lines = malloc(linescount * sizeof(char*));
  if(file == NULL || lines == NULL){
    printf("Malloc failed.\n");
    return NULL;
  }
  fseek(f, 0, SEEK_SET);

  linescount = 0;
  while((input = fgetc(f)) != EOF){
    if(input == '\n'){
      *(lines + linescount) = malloc((currentline + 1) * sizeof(char));
      for(j = lasti; j < i; j++){
	      *(*(lines + linescount) + j - lasti) = *(file + j);
      }
      *(*(lines + linescount) + currentline) = '\0';
      currentline = 0;
      lasti = i + 1;
      linescount++;
    }else{
      currentline++;
    }
    *(file+(i++))=input;
  }
  *(lines + linescount) = malloc((currentline + 1) * sizeof(char));
  for(j = lasti; j < i; j++){
    *(*(lines + linescount) + j - lasti) = *(file + j);
  }
  *(*(lines + linescount) + currentline) = '\0';
  currentline = 0;
  lasti = i + 1;
  
  fclose(f);
  
  if(file != NULL){
    free(file);
  }

  /* writing output */
  *numberOfLines = linescount;
  return lines;
}

void init(char ***tags, char ***values, int size){
  int i;

  *tags = malloc(size * sizeof(char *));
  *((*tags) + 0) = URL_TAG;
  *((*tags) + 1) = PORT_TAG;
  *((*tags) + 2) = SLEEP_DELAY_TAG;
  *((*tags) + 3) = FAILED_TRIES_TAG;

  *values = malloc(size * sizeof(char *));
  for(i = 0; i < size; i++){
    *((*values) + i) = NULL;
  }
}

int getValues(char *lines[], char *tags[], int numberOfLines, int numberOfTags, char *(*values[])){
  int i, tagLength; 
  char *value, *currentLine;
  if(numberOfLines != numberOfTags){
    printf("There should be %d lines, but there are %d.\nTrying to parse the existing ones.\n", numberOfTags, numberOfLines);
    numberOfTags = (numberOfLines < numberOfTags) ? numberOfLines : numberOfTags;
  }
  
  for(i = 0; i < numberOfTags; i++){
    currentLine = *(lines + i);
    value = strstr(currentLine, tags[i]);
    if(value == NULL){ /* not found */
      printf("%s not found.\n", tags[i]);
    }else{
      if(value != currentLine){ /* found, but not at begin */
	printf("%s does not start at the begin of the line.\n", tags[i]);
      }else{
	tagLength = strlen(tags[i]);
	if(strlen(currentLine) <= (tagLength + 1)){ /* found, but no value */
	  printf("There is no value for %s.\n", tags[i]);
	}else{ /* everything is good */
	  (*values)[i] = (currentLine + tagLength + 1); /* the value is everything in the line, except the tag and the char ':' */
	}
      }
    }
  }
  
  return 0; 
}

int fillResult(char **values, SETTINGS *result){
  int length;

  if(values[0] != NULL){
    length = strlen(values[0]);
    result->url = malloc((length + 1) * sizeof(char));
    if(result->url != NULL){ /* copy string, add '\0' */
      strcpy(result->url, values[0]);
      *(result->url + length) = '\0';
    }
  }else{
    result->url = DEFAULT_URL;
  }
  if(values[1] != NULL){
    result->port = atoi(values[1]);
  }else{
    result->port = DEFAULT_PORT;
  }
  if(values[2] != NULL){
    result->sleepDelay = atoi(values[2]);
  }else{
    result->sleepDelay = DEFAULT_SLEEP_DELAY;
  }
  if(values[3] != NULL){
    result->maxFailedTries = atoi(values[3]);
  }else{
    result->maxFailedTries = DEFAULT_MAX_FAILED_TRIES;
  }
}

SETTINGS loadSettings(char *path){
#define TAGS 4
  char **lines, **tags, **values, *value, *currentLine;
  int numberOfLines, i, numberOfTags, tagLength, length;
  SETTINGS result;

  /* TO FREE: values, tags, lines */

  memset(&result, 0, sizeof(SETTINGS));
  lines = readLines(path, &numberOfLines);
  if(lines == NULL){ /* error reading file */
    result.url = DEFAULT_URL;
    result.port = DEFAULT_PORT;
    result.sleepDelay = DEFAULT_SLEEP_DELAY;
    result.maxFailedTries = DEFAULT_MAX_FAILED_TRIES;

    storeSettings(path, result);
    return result;
  }else{ /* file read */
    /* init */
    init(&tags, &values, TAGS); 
    
    /* now parse lines */
    getValues(lines, tags, numberOfLines, TAGS, &values);
    
    /* every value is saved, no copy values to result */
    fillResult(values, &result); 

    free(tags);
    free(values);
    for(i = 0; i < numberOfLines; i++){
      free(*(lines + i));
    }
    free(lines);
    return result;
  }
}

int storeSettings(char *path, SETTINGS s){
  FILE *f;

  f = fopen(path, "w");
  if(f == NULL){
    printf("Could not open file: %s\n", path);
    return -1;
  }else{
    fprintf(f, "url:%s\nport:%d\nsleep:%d\ntries:%d\n", s.url, s.port, s.sleepDelay, s.maxFailedTries);
    fclose(f);
    return 0;
  }
}

void printSettings(SETTINGS s){
  printf("url:%s\nport:%d\nsleep:%d\ntries:%d\n", s.url, s.port, s.sleepDelay, s.maxFailedTries);
}
