import java.net.*;
import java.io.*;

/**
 * Server der fibonacci zahlen berechnet
 */
public class Server_Fib_UDP implements Runnable{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    int port;


    /**
     * Konstruktor dür den Server
     * @param port port auf dem der server laufen soll
     */
    public Server_Fib_UDP(int port) {
        this.port = port;
    }

    /**
     * Berechnung der Fibonacci Zahl
     * @param n wert für den die berechnung Stattfinden soll
     * @return berechneter Wert
     */
    private static int generateFibonacci(int n) {
        if (n > 1) return generateFibonacci(n - 1) + generateFibonacci(n - 2);
        return n;
    }
    @Override
    public void run() {
        try {
            // Erstelle eine Instanz der Klasse DatagramSocket mit Portnummer 6868
            DatagramSocket udpServer = new DatagramSocket(6868);

            // Ein Bytearray f�r die empfangenen Nachrichten wird erstellt
            byte[] buf = new byte[256];

            // Ein UDP Paket wird erstellt, der den Bytearray als Datenpuffer erh�lt
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            // Auf den Eingang eines Datenpakets wird gewartet
            udpServer.receive(packet);

            // Empfangene Nachricht wird in String umgewandelt und leere stellen im bytearray ersetzt
            String receivedMessage = new String(packet.getData()).replaceAll("\u0000.*", "");;

            // Verbindungsdaten des Client werden ausgelesen (IP und Port)
            InetAddress clientAddress = packet.getAddress();
            String sourceIP = clientAddress.getHostAddress();
            int clientPortnumber = packet.getPort();

            System.out.println("Fibonacci Server: habe eine Nachricht erhalten von " + sourceIP + ":"+ clientPortnumber);
            System.out.println("Fibonacci Server:" + receivedMessage);

            //Antwort des Servers
            String msgToSend;
            Integer n;
            try{
                //check ob die erhaltene nachricht im korrekten format ist undzwar nur eine natürliche zahl als String
                n = Integer.valueOf(receivedMessage);

                //Antwort wenn alles OK ist
               msgToSend = "Alles OK! Berechnung wird gestartet, Ergebnis folgt";

             //  Ausgabe der berechneten Zahl
                System.out.println("Fibonacci Server: " +generateFibonacci(n));
//                buf = msgToSend.getBytes();
//                // UDP Paket wird erstellt und erh�lt die Verbindungdaten sowie die zu sendenden Stringnachricht
//
//                DatagramPacket packetToClient = new DatagramPacket(buf, 0, buf.length, clientAddress, clientPortnumber);
//
//                // UDP Paket wird gesendet
//                udpServer.send(packetToClient);
//                sendResult(n, clientAddress, sourceIP, clientPortnumber);

                //Wenn Falsches Fromat wird exception geworfen weil keine konvertierung möglich ist
            }catch (NumberFormatException e){
                //Antwort beim fehler
                msgToSend = "Fehler! : Falsches Format";
            }

            //Falls eingehende nachricht eine bestätigung des clients ist
            if (receivedMessage.equals("Answer Confirmed")) msgToSend = "OK";

            // Stringnachricht wird in ein Bytearray �bersetzt
            buf = msgToSend.getBytes();

            // UDP Paket wird erstellt und erh�lt die Verbindungdaten sowie die zu sendenden Stringnachricht
            DatagramPacket packetToClient = new DatagramPacket(buf, 0, buf.length, clientAddress, clientPortnumber);

            // UDP Paket wird gesendet
            udpServer.send(packetToClient);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * verschicken des berechneten werdes an den client
     */
//    public static void sendResult(Integer n, InetAddress clientAddress, String sourceIP, int clientPortnumber){
//        try {
//            DatagramSocket udpClient = new DatagramSocket();
//            String msgToSend = "" + generateFibonacci(n);
//            byte[] buf = msgToSend.getBytes();
//
//
//
//            DatagramPacket packetToSend = new DatagramPacket(buf, 0, buf.length, clientAddress, clientPortnumber);
//
//            udpClient.send(packetToSend);
//
//            buf = new byte[256];
//            DatagramPacket packetResponse = new DatagramPacket(buf, buf.length);
//
//            udpClient.receive(packetResponse);
//            String receivedMessage = new String(packetResponse.getData());
//            System.out.println("Client: "+ receivedMessage);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    }
