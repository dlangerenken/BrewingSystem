#ifndef GPIO_H
#define GPIO_H 
  #include <sys/stat.h>
  #include <sys/wait.h>
  #include <sys/types.h>
  #include <fcntl.h>
  #include <unistd.h>
  #ifndef STDIO_H
  #define STDIO_H
  #include <stdio.h>
  #endif
  #ifndef STDLIB_H
  #define STDLIB_H
  #include <stdlib.h>
  #endif

  #define IN  0
  #define OUT 1

  #define LOW  0
  #define HIGH 1

  #define MIN_IN 1
  #define MAX_IN 18
  #define MIN_OUT 1
  #define MAX_OUT 7

  /* all functions return -1 in error case */

  /* tries to enabgle the specified pin */
  int GPIOExport(int pin);
  /* frees the specified pin */
  int GPIOUnexport(int pin);
  /* specifies the direction the pin is used (IN or OUT) */
  int GPIODirection(int pin, int dir);
  /* reads the state of a pin */
  int GPIORead(int pin);
  /* sets the state of a pin */
  int GPIOWrite(int pin, int value);
#endif
