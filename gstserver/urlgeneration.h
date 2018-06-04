#ifndef REMOVESPACES_H
#define REMOVESPACES_H

#define IP_EXAMPLE "rtsp://127.0.0.1:"
#define DEFAULT_RTSP_PORT "8554"


void RemoveSpaces(char* source)
{
  char* i = source;
  char* j = source;
  while(*j != 0)
  {
    *i = *j++;
    if(*i != ' ')
      i++;
  }
  *i = 0;
}


#endif /* REMOVESPACES_H */

