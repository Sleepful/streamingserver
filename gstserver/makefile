CC=gcc
CFLAGS= -g -Wall -Wextra -pedantic `pkg-config --cflags --libs gstreamer-1.0 gstreamer-rtsp-server-1.0`

all: mp4 #launch ogg

mp4:
	$(CC) $(CFLAGS) test-mp4.c -o mp4

#launch:
#	$(CC) $(CFLAGS) test-launch.c -o launch
#
#ogg:
#	$(CC) $(CFLAGS) test-ogg.c -o ogg


clean:
	rm mp4 #launch ogg
