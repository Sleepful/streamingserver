//
//  Hello World client in Java
//  Connects REQ socket to tcp://localhost:5555
//  Sends "Hello" to server, expects "World" back
//

import java.util.Objects;

import org.zeromq.ZMQ;
public class main {

    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);

        //  Socket to talk to server
        System.out.println("Connecting to hello world server…");

        ZMQ.Socket requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://206.189.228.117:5555");

     // 
     // rtsp://206.189.228.117:8554/ [url]

        //este es un bloque ejemplo que envia "hello" y recibe "world"
        //importante: cada vez que hace un REQUEST >>tiene<< que hacer un RECEIVE
        // RECEIVE: "requester.recv(0);"
        for (int requestNbr = 0; requestNbr != 1; requestNbr++) {
            String request = "Hello";
            System.out.println("Sending: Hello " + requestNbr);
            requester.send(request.getBytes(), 0);

            byte[] reply = requester.recv(0);
            System.out.println("Received: " + new String(reply) + " " + requestNbr);
        }
        
        // este es el bloque de codigo para pedir urls
        byte[] reply = null;
        int count=0;
        //while:
        // reply = lo que responde el servidor
        // null = para la primera vez que se corre y aun no hay nada en el reply
        // !Objects.equals("end:",new String(reply)) = terminar cuando el servidor responde
        //			con "end:"
        // count = para indicarle al servidor por cual url vamos pidiendo
        
        // el protocolo es el siguiente:
        // enviar "urls:#" donde "#" es el indice del url por el que va.
        // se devuelve el final de cada url, ese se le agrega al rtsp://ip:port/ + url
        // cuando envia un "#" mayor a los urls disponibles el server devuelve la
        // llamada "end:"
        while(reply== null || !Objects.equals("end:",new String(reply))) {
	        String request = "urls:"+count;
	        System.out.println("Sending: urls:"+count);
	        requester.send(request.getBytes(), 0);
	        count++;
	        reply = requester.recv(0);
	        System.out.println("Received: " + new String(reply));
        }
        
        //protocolo "user:<nombre>", sirve para que el server imprima en pantalla (terminal)
        //que un user se ha conectado. Correr esto 1 vez al principio solo para cumplir
        //con la especificacion del profe de enunciar los usuarios
        String request = "user:Markus";
        System.out.println("Sending: "+request);
        requester.send(request.getBytes(), 0);
        count++;
        reply = requester.recv(0);
        System.out.println("Received: " + new String(reply));

        //protocolo "play:<nombre>", sirve para que el server imprima en pantalla (terminal)
        //cual cancion . Correr esto 1 vez con el url de el video a hacer play, solo para
        //cumplir con la especificacion de enunciar cuales videos se les hace play
        request = "play:Papa";
        System.out.println("Sending: "+request);
        requester.send(request.getBytes(), 0);
        count++;
        reply = requester.recv(0);
        System.out.println("Received: " + new String(reply));
        
        requester.close();
        context.term();
    }
}