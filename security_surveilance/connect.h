#ifndef CONNECT_H
#define CONNECT_H
  #ifndef STDIO_H
  #define STDIO_H
  #include <stdio.h>
  #endif
  #ifndef STDLIB_H
  #define STDLIB_H
  #include <stdlib.h>
  #endif
  #include <string.h>

  #include <netinet/in.h>
  #include <arpa/inet.h>
  #include <sys/types.h>
  #include <sys/socket.h>
  #include <netdb.h>

  #define DATASIZE 1000

  void *get_in_addr(struct sockaddr *sa);
  int connectToBrewberry(char *address, char *port);
  int isAlive(char *ip, char *port);
  int startSecuritySurveilance(int maxFailedConnects, char *ip, char *port, int sleepTime);
#endif
