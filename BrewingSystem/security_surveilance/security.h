#ifndef SECURITY_H
#define SECURITY_H
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

  #define ALERT_PIN 1

  /* pings the http port of an url and returns 0 for success and -1 for errors */
  int ping(char* url, int port);
  /* turns of all pins at the rasperry except */
  int shutdownAll();
  /* activates the ALERT_PIN */
  int alert(int mode);
#endif
