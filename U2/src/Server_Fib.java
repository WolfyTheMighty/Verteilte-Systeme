import java.net.*;
import java.io.*;

public class Server_Fib {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
         Server_Fib server=new Server_Fib();
        server.start(6868);
    }

    public Server_Fib() {
    }

    private static int generateFibonacci(int n) {
        if (n > 1) return generateFibonacci(n - 2) + generateFibonacci(n - 1);
        return n;
    }

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(6868);
            Socket socket = serverSocket.accept();
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if ("hi".equals(in.readLine())) {
                out.println("hello client");
                out.println(generateFibonacci(9));
            }
            else {
                out.println("unrecognised greeting");
            }
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
