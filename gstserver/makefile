CC=gcc
CFLAGS= -g -Wall -Wextra -pedantic
GSTRLIB=`pkg-config --cflags --libs gstreamer-1.0 gstreamer-rtsp-server-1.0`
ZMQLIB= -lzmq

all: mp4 server client #launch ogg

re: clean all

server:
	$(CC) $(CFLAGS) $(ZMQLIB) server.c -o server

mp4:
	$(CC) $(CFLAGS) $(GSTRLIB) mp4-streaming.c -o mp4

client:
	$(CC) $(CFLAGS) $(ZMQLIB) client.c -o client

#launch:
#	$(CC) $(CFLAGS) test-launch.c -o launch
#
#ogg:
#	$(CC) $(CFLAGS) test-ogg.c -o ogg


clean:
	rm mp4 server client #launch ogg
