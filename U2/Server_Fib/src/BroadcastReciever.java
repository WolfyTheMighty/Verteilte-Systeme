import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

public class BroadcastReciever implements Runnable{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private int port;

    public BroadcastReciever(int port) {
        this.port = port;

    }


    @Override
    public void run() {
            try {
                socket = new DatagramSocket(port);
                // Ein Bytearray f�r die empfangenen Nachrichten wird erstellt
                byte[] buf = new byte[512];

                // Ein UDP Paket wird erstellt, der den Bytearray als Datenpuffer erh�lt
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                // Auf den Eingang eines Datenpakets wird gewartet
                socket.receive(packet);

                // Empfangene Nachricht wird in String umgewandelt
                String receivedMessage = new String(packet.getData());

                // Verbindungsdaten des Client werden ausgelesen (IP und Port)
                InetAddress clientAddress = packet.getAddress();
                String sourceIP = clientAddress.getHostAddress();
                int clientPortnumber = packet.getPort();

                System.out.println("Broadcast Reciever: habe eine Nachricht erhalten von " + sourceIP + ":"+ clientPortnumber);
                System.out.println("Broadcast Reciever: "+ receivedMessage);

                // Server erstellt Nachricht f�r die Antwort auf den Server
                String msgToSend = "Server hat den Broadcast erhalten!!";

                // Stringnachricht wird in ein Bytearray �bersetzt
                buf = msgToSend.getBytes();

                // UDP Paket wird erstellt und erh�lt die Verbindungdaten sowie die zu sendenden Stringnachricht
                DatagramPacket packetToClient = new DatagramPacket(buf, 0, buf.length, clientAddress, clientPortnumber);

                // UDP Paket wird gesendet
                socket.send(packetToClient);

                //Fibonacci server wird gefragt nach dem wert der Zahl 17
                sendFibRequest(17);
                socket.close();
//                run();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    //Aufgabe B 2b Fibonacci Server wird kontaktiert um den wert 17 auszurechnen
        private static void sendFibRequest(Integer n){
            try {

                Socket tcpClient = new Socket("localhost", 6868);
                // Printwriter wird zum einfachen Schreiben von Stringnachrichten erstellt
                PrintWriter outputWriter = new PrintWriter(tcpClient.getOutputStream());

                // Stringnachricht wird in den outputstream geschrieben
                outputWriter.println(n.toString());

                //Der outputstream wird geleert,d.h. werden sofort verschickt.
                outputWriter.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}

