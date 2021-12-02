import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class BroadcastReciever extends Thread{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private int port;

    public BroadcastReciever(int port) throws IOException {
        this.port = port;
        socket = new DatagramSocket(port);
    }


    @Override
    public void run() {
            try {

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

                System.out.println("Server: habe eine Nachricht erhalten von " + sourceIP + ":"+ clientPortnumber);
                System.out.println("Server: "+ receivedMessage);

                // Server erstellt Nachricht f�r die Antwort auf den Server
                String msgToSend = "Hallo Client, ich bin der Server!";

                // Stringnachricht wird in ein Bytearray �bersetzt
                buf = msgToSend.getBytes();

                // UDP Paket wird erstellt und erh�lt die Verbindungdaten sowie die zu sendenden Stringnachricht
                DatagramPacket packetToClient = new DatagramPacket(buf, 0, buf.length, clientAddress, clientPortnumber);

                // UDP Paket wird gesendet
                socket.send(packetToClient);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
}

