#include "gpio.h"
#include "io.h"
#include "connect.h"
#include "security.h"

#define flush() while(getchar()!='\n')

int shutdownAll(){
  int i, error = 0;

  for(i = MIN_OUT; i <= MAX_OUT; i++){
    if(-1 == GPIOExport(i)){
      printf("Could not use pin %d\n", i);
      error = -1;
    }else{
      if(-1 == GPIODirection(i, OUT)){
	printf("Pin %d is no out-pin\n", i);
	error = -1;
      }else{
	if(-1 == GPIOWrite(i, LOW)){
	  printf("Unable to switch of pin %d\n", i);
	  error = -1;
	}
	if(-1 == GPIOUnexport(i)){
	  printf("Unable to free pin %d\n", i);
	  error = -1;
	}
      }
    }
  }
  return error;
}

int alert(int mode){
  int alertPin = ALERT_PIN;

  if(-1 == GPIOExport(alertPin)){
    printf("Could not activate alert\n");
    return -1;
   }else{
   if(-1 == GPIODirection(alertPin, OUT)){
      printf("Alert pin is not usable for output\n");
      return -2;
    }else{
      if(-1 == GPIOWrite(alertPin, mode)){
	printf("Unable to switch on alert pin\n");
	return -3;
      }
      if(-1 == GPIOUnexport(alertPin)){
	printf("Unable to free alert pin\n");
	return -4;
      }
    }
  } 
}

int main(int argc, char *argv[]){
  char* url, portString[10];
  int port, sleepTime, maxFailedAttempts;
  int running = 1, shutDownSuc, alertSuc, failCounter = 0, securityReturn;
  char c = 'n';
  SETTINGS s;

  s = loadSettings(FILE_PATH);
  url = s.url;
  port = s.port;
  sleepTime = s.sleepDelay;
  maxFailedAttempts = s.maxFailedTries;

  printSettings(s);

  memset(portString, 0, 10);
  sprintf(portString, "%d", port);

  printf("Debug: port %s, sleep %d\n", portString, sleepTime);
   
  securityReturn = startSecuritySurveilance(maxFailedAttempts, url, portString, sleepTime);
  printf("Security returned: %d\n", securityReturn);
  shutdownAll(); 
  while(c != 'y'){
    printf("End alert? (y/n)\n");
    scanf("%c", &c);
    if(c != '\n'){
      flush();
    }
  }
  alert(LOW); 
  return(0);
}
