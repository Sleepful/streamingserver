//  Hello World server

#include <zmq.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <assert.h>
#include "zhelpers.h"
#include "urlgeneration.h"
#define REMOTE_IP "206.189.228.117"
#define LOCALHOST "127.0.0.1"

struct comType *comm, *comm1, *comm2;
char **urls;
int count =0;

void add_string(char** array, int* size, const char* string)
{
   urls = realloc(array, (*size + 1) *sizeof(char*) );
   urls[*size] = malloc(strlen(string)+1);
   strcpy(urls[*size], string);
   *size += 1;
}

void getUrls(){
  FILE *fp;
  int status;
  char path[URL_SIZE];
  fp = popen("find video -type f", "r");
  if (fp == NULL)
      /* Handle error */
      printf("error with fp");
  while (fgets(path, URL_SIZE, fp) != NULL){
      //printf("%s", path);
      add_string(urls,&count,path);
      //printf("1 %d\n", count);
      //printf("str %s\n",urls[count-1]);
  }
  status = pclose(fp);
  if (status == -1) {
      /* Error reported by pclose() */
      printf("error with pclose()");
  } else {
      /* Use macros described under wait() to inspect `status' in order
         to determine success/failure of command executed by popen() */
  }
}


int main (void)
{
    printf("Sessions server.\n");
    fflush(stdout);
    //  Socket to talk to clients
    void *context = zmq_ctx_new ();
    void *responder = zmq_socket (context, ZMQ_REP);
    int rc = zmq_bind (responder, "tcp://*:5555");
    assert (rc == 0);

    getUrls();

    while (1) {
        char *string = s_recv (responder);
        char url[URL_SIZE];
        strcpy(url, "world");
        printf ("Received: %s.\n", string);     //  Show progress
        if(strstr(string, "urls:") != NULL){
          if(string[5]-'0'<count){
            sprintf(url,"rtsp://%s:8554/%s", REMOTE_IP, urls[string[5]-'0']);
            RemoveSpaces(url);
          }
          else{
            strcpy(url,"end:");
          }
        }
        if(strstr(string, "name:") != NULL){
          if(string[5]-'0'<count){
            sprintf(url,"%s", urls[string[5]-'0']+6);
          }
          else{
            strcpy(url,"end:");
          }
        }
        if(strstr(string, "user:") != NULL){
            sprintf(url, "Hello, %s!", string+5);
        }
        if(strstr(string, "play:") != NULL){
            sprintf(url, "You are now playing: %s!", string+5);
        }
        fflush(stdout);
        free (string);
        //sleep (1);
        s_send (responder, url);        //  Send results to sink
    }
    zmq_close (responder);
    zmq_ctx_destroy (context);
    return 0;
}
