import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

/**
 * Gibt infos über Netzwerkschnittstellen und beinhaltet den großteil der gestellten aufaben als startpunkt
 */
public class NetworkAccesPoints {

    //zum Speichern der Broadcast ip-adressen
    public static ArrayList<InetAddress> inetAddresses = new ArrayList<>();

    public static void main(String[] args) {

        //Ausgabe der Netzwerkschnittstellen Aufgabe B 1a und 1b
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                displayInterfaceInformation(netint);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        out.println("\n\n\n\n"); // Trennung der Ausgaben auf der konsole der übersichts halber


        //Aufgabe B 2a Start des Braodcast recievers im seperatem Thread
        BroadcastReciever broadcastReciever = new BroadcastReciever(9876);
        Thread broadcastRecieverThread = new Thread(broadcastReciever);
        broadcastRecieverThread.start();

        //Aufgabe B 1d Start des Fibonacci Servers im sepereatem Thread
        Server_Fib_UDP server_fib_udp = new Server_Fib_UDP(6868);
        Thread fib_Server_Thread = new Thread(server_fib_udp);
        fib_Server_Thread.start();


        //Aufgabe B 1c - alle 5 sekunden einen Broadcast verschicken
        sendTimedPacket(5);
    }

    /**
     * Sendet an alle gespeicherten broadcast ip adressen im bestimmten intervall eine nachricht
     * @param t intervall in sek.
     */
    private static void sendTimedPacket(int t) {

        //deffinition der funktion zur späteren wiederholung durch ScheduledExecutor
        Runnable timedPacket = () -> {
            try {
                DatagramSocket udpClient = new DatagramSocket();
                String msgToSend = "Dieser Server wurden von der Gruppe [A01] implementiert und stellt die \n" +
                        "Fibonacci-Funktion als Dienst bereit. Um den Dienst zu nutzen, senden Sie eine \n" +
                        "Nachricht an Port [6868] auf diesem Server. Das \n" +
                        "Format der Nachricht sollte folgendermaßen aussehen [Fibonacci Zahl als String].";
                byte[] buf = msgToSend.getBytes();

                for (InetAddress targetAddress : inetAddresses) {


                    DatagramPacket packetToSend = new DatagramPacket(buf, 0, buf.length, targetAddress, 9876);

                    udpClient.send(packetToSend);

                    buf = new byte[256];
                    DatagramPacket packetResponse = new DatagramPacket(buf, buf.length);

                    udpClient.receive(packetResponse);
                    String receivedMessage = new String(packetResponse.getData()).replaceAll("\u0000.*", "");;
                    System.out.println("Broadcast Client: " + receivedMessage);
//                    udpClient.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        //wiederholung des runnable als executor
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(timedPacket, 0, t, TimeUnit.SECONDS);

    }

    /**
     * Gibt informationen name ip-adresse und deren binäre darstellung
     * @param netint Network interface wofür infos ausgegeben werden sollen
     * @throws SocketException
     */
    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("\n\nDisplay name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        List<InterfaceAddress> inetAddresses = netint.getInterfaceAddresses();
//        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InterfaceAddress inetAddress : inetAddresses) {
//            String inetAddressString = inetAddress.toString().replace("/", "");
//            String inetAddressString = inetAddress.toString();


// Aus http://www.tcpipguide.com/index.htm
//            One important change in the addressing model of IPv6 is the address types supported. IPv4 supported three address types: unicast, multicast and broadcast. Of these, the vast majority of actual traffic was unicast. IP multicast support was not widely deployed until many years after the Internet was established, and continues to be hampered by various issues. Use of broadcast in IP had to be severely restricted for performance reasons (we don't want any device to be able to broadcast across the entire Internet!)
//            IPv6 also supports three address types, but with some changes:
//            Unicast Addresses: These are standard unicast addresses as in IPv4, one per host interface.
//            Multicast Addresses: These are addresses that represent various groups of IP devices: a message sent to a multicast address goes to all devices in the group. IPv6 includes much better multicast features and many more multicast addresses than IPv4. Since multicast under IPv4 was hampered in large part due to lack of support of the feature by many hardware devices, support for multicasting is a required, not optional, part of IPv6.
//            Anycast Addresses: Anycast addressing is used when a message must be sent to any member of a group, but does not need to be sent to them all. Usually the member of the group that is easiest to reach will be sent the message. One common example of how anycast addressing could be used is in load sharing amongst a group of routers in an organization.
//            Key Concept: IPv6 has unicast and multicast addresses like IPv4. There is, however, no distinct concept of a broadcast address in IPv6. A new type of address, the anycast address, has been added to allow a message to be sent to any one member of a group of devices. Implications of the Changes to Address Types in IPv6
//            Broadcast addressing as a distinct addressing method is gone in IPv6. Broadcast functionality is implemented using multicast addressing to groups of devices. A multicast group to which all nodes belong can be used for broadcasting in a network, for example.
//            An important implication of the creation of anycast addressing is removal of the strict uniqueness requirement for IP addresses. Anycast is accomplished by assigning the same IP address to more than one device. The devices must also be specifically told that they are sharing an anycast address, but the addresses themselves are structurally the same as unicast addresses.

            //Ausgabe der Adresse typs ipv4
            if (inetAddress.getAddress() instanceof Inet4Address) {

                //ip adresse
                out.printf(" IPV4 InetAddress: %s\n", inetAddress.getAddress().toString().replace("/", ""));

                //zugehörige broadcast adresse
                out.printf(" IPV4 Broadcast: %s\n", inetAddress.getBroadcast().toString().replace("/", ""));

               //hinzufügen zur liste aller broadcast adressen für späteren broadcast
                NetworkAccesPoints.inetAddresses.add(inetAddress.getBroadcast());

               //binäre Darstellung der adresse
                out.printf(" IPV4 binary InetAddress: %s\n", ipv4toBinary(inetAddress.getAddress().toString().replace("/", "")));

            //Ausgabe der Adresse typs ipv6
            } else if (inetAddress.getAddress() instanceof Inet6Address) {
//                IPv6 defines at least three reachability scopes for addresses:
//
//                Globally addressable. This is an IPv6 address given to you by your ISP. It is available to use on the public Internet.
//                Global addresses are unique across the internet.

//                Link-local. This is similar to the 169.254.X.X range. It is an address that a computer assigns itself in order to facilitate local communications. These addresses don't get routed around on the public Internet because they're not globally unique.
//                Link-local addresses are designed to be used for addressing on a single link for purposes such as auto-address configuration, neighbor discovery, or when no routers are present.

//                Node-local. This is an address that identifies the local interface, similar to 127.0.0.1. Basically, this is the address ::1.
//                Site-local addresses are designed to be used for addressing inside of a site without the need for a global prefix.
//

                //link-local because the address begins with fe80, The number after the '%' is the scope ID.

               //ausgabe der ip adresse
                out.printf(" IPV6 InetAddress: %s\n", inetAddress.toString().replace("/", ""));
                //binäre darstellung
                out.printf(" IPV6 binary InetAddress: %s\n", ipv6toBinary(inetAddress.getAddress().toString().replace("/", "")));
            }
        }
    }

    /**
     * Wandelt eine ipv4 adresse in eine binäre darstellung um
     * @param ip ip adresse die umgewandelt werden soll als string
     * @return binäre darstellung als String
     */
    public static String ipv4toBinary(String ip) {

        String[] spl = ip.split("\\.");
        String result = "", del = "";
        for (String s : spl) {
//            s = s.split("%")[0];
            result += del
                    + String.format("%8s", new BigInteger(s, 10).toString(2)).replace(' ', '0');
            del = " ";
        }
        return result;
    }
    /**
     * Wandelt eine ipv4 adresse in eine binäre darstellung um
     * @param ip ip adresse die umgewandelt werden soll
     * @return binäre darstellung als String
     */
    public static String ipv6toBinary(String ip) {
        String[] spl = ip.split(":");
        String result = "", del = "";
        for (String s : spl) {
           s = s.split("%")[0];
            result += del
                    + String.format("%16s", new BigInteger(s, 16).toString(2)).replace(' ', '0');
            del = " ";
        }
        return result;
    }

}
