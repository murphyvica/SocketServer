import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class SocketServer {
    public static Boolean LISTEN = true;
    public static class MyRunnableTCP implements Runnable {
        private ServerSocket soc;
        public MyRunnableTCP (ServerSocket s) {
            this.soc = s;
        }

        public void run() {
            try {
                Socket sock = soc.accept();
                System.out.println("Connection Successful!");
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println(randomString());
                System.out.println("Quote sent");
                out.close();
                LISTEN = false;

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class MyRunnableUDP implements Runnable {
        private DatagramSocket soc;
        public MyRunnableUDP (DatagramSocket s) {
            this.soc = s;
        }

        public void run() {
            try {
                byte[] b = new byte[256];
                DatagramPacket pack = new DatagramPacket(b, b.length);
                soc.receive(pack);
                InetAddress address = pack.getAddress();
                int port = pack.getPort();
                String message = randomString();
                byte[] quote = message.getBytes();
                DatagramPacket newP = new DatagramPacket(quote, quote.length, address, port);
                soc.send(newP);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws Exception {
            ServerSocket servSockT = new ServerSocket(17);
            DatagramSocket servSockU = new DatagramSocket(17);
            while (LISTEN == true) {
                Thread t2 = new Thread(new MyRunnableUDP(servSockU));
                t2.start();
                Thread t1 = new Thread(new MyRunnableTCP(servSockT));
                t1.start();
                t2.join();
            }
            servSockU.close();
            servSockT.close();


    }

    public static String randomString () {
        List<String> quotes = Arrays.asList("Life is pain, Highness. Anyone who says differently is selling something " +
                "- Princess Bride", "You can’t hurt me. Westley and I are joined by the bonds of love. And you " +
                "cannot track that, not with a thousand bloodhounds, and you cannot break it, not with a thousand swords " +
                "- Princess Bride", "\"You miss 100% of the shots you don't take – Wayne Gretzky – Michael Scott\"- The Office",
                "I knew exactly what to do. But in a much more real sense, I had no idea what to do. - The Office",
                "Love is the one thing that transcends time and space. Maybe we should trust that, even if we cannot " +
                "understand it - Interstellar", "A machine doesn't improvise well because you cannot program a fear of death." +
                " Our survival instinct is our greatest source of inspiration - Interstellar");
        Random rand = new Random();
        String randString = quotes.get(rand.nextInt(quotes.size()));
        return randString;
    }
}