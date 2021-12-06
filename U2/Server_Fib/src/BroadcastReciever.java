import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;


/**
 * Empfäng eine gesendete broadcast nachricht und kontaktiert den fibonacci server
 */
public class BroadcastReciever implements Runnable{

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private int port;

    /**
     * Kostruktor für den Braodcast Reciever
     * @param port port uf dem er laufen soll
     */
    public BroadcastReciever(int port) {
        this.port = port;
    }


    @Override
    /**
     * Startet einen UDP server und hällt ausschau nach nachrichten
     */
    public void run() {
            try {
                socket = new DatagramSocket(port);
                byte[] buf = new byte[512];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                //ersetzen der leeren bytes im array
                String receivedMessage = new String(packet.getData()).replaceAll("\u0000.*", "");;

                // Verbindungsdaten des Client werden ausgelesen (IP und Port)
                InetAddress clientAddress = packet.getAddress();
                String sourceIP = clientAddress.getHostAddress();
                int clientPortnumber = packet.getPort();

                System.out.println("Broadcast Reciever: habe eine Nachricht erhalten von " + sourceIP + ":"+ clientPortnumber);
                System.out.println("Broadcast Reciever: "+ receivedMessage);

                // Server erstellt Nachricht für die Antwort auf den Server
                String msgToSend = "Server hat den Broadcast erhalten!";

                // Stringnachricht wird in ein Bytearray �bersetzt
                buf = msgToSend.getBytes();

                // UDP Paket wird erstellt und erhält die Verbindungdaten sowie die zu sendenden Stringnachricht
                DatagramPacket packetToClient = new DatagramPacket(buf, 0, buf.length, clientAddress, clientPortnumber);

                // UDP Paket wird gesendet
                socket.send(packetToClient);

                //Aufgabe B 2b Fibonacci Server wird kontaktiert um den wert 17 auszurechnen
                sendMessage("17");
                socket.close();
//                run();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    /**
     * Verschickt eine nachricht an den Fibonacci Server
     * @param n Nachricht als String
     */
    private static void sendMessage(String n){
//            try {
//
//                Socket tcpClient = new Socket("localhost", 6868);
//                // Printwriter wird zum einfachen Schreiben von Stringnachrichten erstellt
//                PrintWriter outputWriter = new PrintWriter(tcpClient.getOutputStream());
//
//                // Stringnachricht wird in den outputstream geschrieben
//                outputWriter.println(n.toString());
//
//                //Der outputstream wird geleert,d.h. werden sofort verschickt.
//                outputWriter.flush();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            try {
                DatagramSocket udpClient = new DatagramSocket();
                String msgToSend =  n;
                byte[] buf = msgToSend.getBytes();

                InetAddress targetAddress = InetAddress.getByName("localhost");

                //Erstellung des UDP packets
                DatagramPacket packetToSend = new DatagramPacket(buf, 0, buf.length, targetAddress, 6868);

                udpClient.send(packetToSend);

                buf = new byte[256];
                DatagramPacket packetResponse = new DatagramPacket(buf, buf.length);

                udpClient.receive(packetResponse);

                //ersetzen der leeren stellen im byte array
                String receivedMessage = new String(packetResponse.getData()).replaceAll("\u0000.*", "");
                System.out.println("Client: "+ receivedMessage);
//                sendMessage("Answer Confirmed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}

