# streamingserver

Servidor de streaming de video en C, con clientes en Java. Puede conectarse a varios clientes al mismo tiempo. Hecho con [zeromq](https://zeromq.org/).

Documentación completa en **\Docs\docu.pdf**

---

gstserver\\**urlgeneration.h**: utilizado para generar los URL de los streams, al tener su propio archivo asegura consistencia entre los varios programas que lo usan.

gstserver\\**zhelpers.h**: funciones útiles para ser reutilizadas constantemente junto a la biblioteca zeromq.

gstserver\\**mp4-streaming.c (-o mp4)**: La parte del servidor que hace el streaming. Programa que se encarga de crear los streams y asignar URLs. Utiliza `find` junto con pipes para crear los streams dinámicamente. Utiliza la biblioteca gstreamer para realizar el streaming del payload por medio del protocolo RTSP hacia el cliente por medio de un URL creado dinámicamente para cada archivo.

gstserver\\**unused.c**: Macros para evitar warning de gcc sobre parametros de funcion no utilizados.

gstserver\\**server.c (-o server)**: El servidor encargado de las conexiones de los clientes, realiza tareas como comunicar la lista de canciones, y en general la transferencia de mensajes entre el servidor y el usuario por medio de zeromq.

gstserver\\**makefile**: make file :p

gstserver\\**run.sh**: para correr el servidor.

gstserver\\**client**: cliente ejemplo en c utilizado para pedir mensajes

ExampleJava\src\\**main.java**: programa ejemplo en java para pedir mensajes

MediaPlayer\src\\**Main.java** & MediaPlayer\src\\**User.java**: Cliente con GUI realizado en Java
