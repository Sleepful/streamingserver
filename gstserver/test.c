#include<stdio.h>
#include <linux/limits.h>


int main(int argc, char *argv[])
{
  for (int arg  = 1; arg<argc; ++arg ) { // for each arg
    printf("arg %d\n",arg);
    FILE *fp;
    int status;
    char path[PATH_MAX];

    char cmd[1024];

    snprintf(cmd, sizeof(cmd), "./urlify \"%s\"", argv[arg]);

    fp = popen(cmd, "r");
    if (fp == NULL)
        /* Handle error */;


    while (fgets(path, PATH_MAX, fp) != NULL)
        printf("%s\n", path);


    status = pclose(fp);
    if (status == -1) {
        /* Error reported by pclose() */
    } else {
        /* Use macros described under wait() to inspect `status' in order
           to determine success/failure of command executed by popen() */
    }
  }
  return 0;

}
