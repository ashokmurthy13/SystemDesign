import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClientV2 {

    private static final String LOCALHOST = "localhost";
    private static final String SERVER_HOST = "34.84.112.220";
    private static final int PORT = 8080;

    public static void main(String... args) throws InterruptedException {

        try(Socket client = new Socket();) {

            client.connect(new InetSocketAddress(LOCALHOST, PORT));
            client.setKeepAlive(true);
            System.out.println(client.getInetAddress());
            System.out.println(client.getLocalPort());
            System.out.println(client.getKeepAlive());
            System.out.println(client.getPort());

            ExecutorService executorService = Executors.newFixedThreadPool(5);
            executorService.submit(() -> handleServer(client));

            Thread.currentThread().join();

        } catch(IOException exception) {
            System.err.println(exception);
        }
    }

    private static void handleServer(Socket server) {
        try(Scanner scanner = new Scanner(System.in);) {
            while(true) {
                String message = scanner.nextLine();
                server.getOutputStream().write((message + "\n").getBytes());
                server.getOutputStream().flush();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
