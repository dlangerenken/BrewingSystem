#include "connect.h"

void *get_in_addr(struct sockaddr *sa){
  if(sa->sa_family == AF_INET){
    return &(((struct sockaddr_in *)sa)->sin_addr);
  }else{
    return &(((struct sockaddr_in6 *)sa)->sin6_addr);
  }
}

int connectToBrewberry(char *address, char *port){
  struct addrinfo hints, *server, *p;
  int rv, sock;
  char s[INET6_ADDRSTRLEN];

  memset(&hints, 0, sizeof hints);
  hints.ai_family = AF_UNSPEC;
  hints.ai_socktype = SOCK_STREAM;

  if((rv = getaddrinfo(address, port, &hints, &server)) != 0){
    fprintf(stderr, "getaddrinfo error: %s\n", gai_strerror(rv));
    return -1;
  }

  for(p = server; p != NULL; p = p->ai_next){
    if((sock = socket(p->ai_family, p-> ai_socktype, p->ai_protocol)) == -1){
      perror("cliet: socket");
      return -2;
    }

    if(connect(sock, p->ai_addr, p->ai_addrlen) == -1){
      perror("client: connect");
      return -3;
    }

    break;
  }
  if(p == NULL){
    fprintf(stderr, "client failed to connect.\n");
    return -4;
  }
  
  inet_ntop(p->ai_family, get_in_addr((struct sockaddr *)p->ai_addr), s, sizeof s);
  printf("Client connected to: %s\n", s);
  freeaddrinfo(server);

  return sock;
}
  
int isAlive(char *ip, char *port){
  int sock, n;
  char buffer[DATASIZE], sendline[DATASIZE];

  if((sock = connectToBrewberry(ip, port)) < 1){
    fprintf(stderr, "could not connect\n");
    return -2;
  }

  memset(buffer, 0, DATASIZE);
  // Form request
  sprintf(sendline, 
     "GET %s HTTP/1.0\r\n"  // POST or GET, both tested and works. Both HTTP 1.0 HTTP 1.1 works, but sometimes 
     "Host: %s\r\n"     // but sometimes HTTP 1.0 works better in localhost type
     "Content-type: application/x-www-form-urlencoded\r\n"
     "Content-length: %d\r\n\r\n"
     "%s\r\n", "/alive", ip, 4, "HEAD");

  // Write the request
  if (write(sock, sendline, strlen(sendline))>= 0) 
  {
    // Read the response
    while ((n = read(sock, buffer, DATASIZE)) > 0) 
    {
	printf("result: %s\n", buffer);
    }
  }
  close(sock);

  return 0;
}

int startSecuritySurveilance(int maxFailedConnects, char *ip, char *port, int sleepTime){
  int failedConnects = 0;
  
  while(failedConnects < maxFailedConnects){
    if(isAlive(ip, port) == 0){
      failedConnects = 0;
    }else{
      failedConnects++;
      printf("Security surveilance failed %d times. Aborting.\n", failedConnects);
    }
    sleep(sleepTime);
  }

  return 0;
}
