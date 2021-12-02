import java.net.*;
import java.io.*;

public class Server_Fib_TCP implements Runnable{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    int port;

//    public static void main(String[] args) {
////        Server_Fib_TCP server = new Server_Fib_TCP();
////        server.start(6868);
//    }

    public Server_Fib_TCP(int port) {
        this.port = port;
    }

    private static int generateFibonacci(int n) {
        if (n > 1) return generateFibonacci(n - 1) + generateFibonacci(n - 2);
        return n;
    }
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
//            out = new PrintWriter(clientSocket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            out.println(generateFibonacci(Integer.parseInt(in.readLine())));
            System.out.println("Eingehende Verbindung erhalten.");

            // BufferedReader zum einfachen Auslesen aus dem InputStream wird erstellt
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Eine Zeile wird eingelesen
            String eingehendeNachricht = inputReader.readLine();

            System.out.println("Fibonacci Server: Wert für " + eingehendeNachricht+" = "+ generateFibonacci(Integer.parseInt(eingehendeNachricht)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
