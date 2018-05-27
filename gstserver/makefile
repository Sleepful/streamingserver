CC=gcc
CFLAGS= -g -Wall -Wextra -pedantic `pkg-config --cflags --libs gstreamer-1.0 gstreamer-rtsp-server-1.0`

all: ogg mp4

ogg:
	$(CC) $(CFLAGS) test-ogg.c -o ogg

mp4:
	$(CC) $(CFLAGS) test-mp4.c -o mp4

clean:
	rm ogg mp4
