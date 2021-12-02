import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

public class NetworkAccesPoints {
    public static ArrayList<InetAddress> inetAddresses = new ArrayList<>();

    public static void main(String[] args) {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                displayInterfaceInformation(netint);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        out.println("\n\n\n\n");
        try {
            BroadcastReciever server_fib_udp= new BroadcastReciever(9876);
            server_fib_udp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendTimedPacket(5);


    }

    private static void sendTimedPacket(int t){
        Runnable timedPacket = () -> {
            try {
                DatagramSocket udpClient = new DatagramSocket();
                String msgToSend = "Dieser Server wurden von der Gruppe [A01] implementiert und stellt die \n" +
                        "Fibonacci-Funktion als Dienst bereit. Um den Dienst zu nutzen, senden Sie eine \n" +
                        "Nachricht an Port [9876] auf diesem Server. Das \n" +
                        "Format der Nachricht sollte folgendermaßen aussehen [Fibonacci Zahl als String].";
                byte[] buf = msgToSend.getBytes();

                for (InetAddress targetAddress : inetAddresses) {


                    DatagramPacket packetToSend = new DatagramPacket(buf, 0, buf.length, targetAddress, 9876 );

                    udpClient.send(packetToSend);

                    buf = new byte[256];
                    DatagramPacket packetResponse = new DatagramPacket(buf, buf.length);

                    udpClient.receive(packetResponse);
                    String receivedMessage = new String(packetResponse.getData());
                    System.out.println("Client: " + receivedMessage);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(timedPacket, 0, t, TimeUnit.SECONDS);

    }


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

            if (inetAddress.getAddress() instanceof Inet4Address) {

                out.printf(" IPV4 InetAddress: %s\n", inetAddress.getAddress());
                out.printf(" IPV4 Broadcast: %s\n", inetAddress.getBroadcast());
                NetworkAccesPoints.inetAddresses.add(inetAddress.getBroadcast());
                out.printf(" IPV4 binary InetAddress: %s\n", toBinary(inetAddress.getAddress().toString()));
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
                out.printf(" IPV6 InetAddress: %s\n", inetAddress);
                out.printf(" IPV6 binary InetAddress: %s\n", toBinary(inetAddress.getAddress().toString()));
            }
        }
    }

    //https://mkyong.com/java/java-convert-string-to-binary/
    public static String toBinary(String input) {

        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar))
                            .replaceAll(" ", "0")
            );
        }
        return result.toString();

    }

}
