import java.net.*;
import java.io.*;

public class Server_Fib_TCP {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
        Server_Fib_TCP server = new Server_Fib_TCP();
        server.start(6868);
    }

    public Server_Fib_TCP() {
    }

    private static int generateFibonacci(int n) {
        if (n > 1) return generateFibonacci(n - 1) + generateFibonacci(n - 2);
        return n;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(6868);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.println(generateFibonacci(Integer.parseInt(in.readLine())));

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
