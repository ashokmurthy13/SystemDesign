import java.io.IOException;
import java.net.*;

public class TCPClientV1 {

    private static final String LOCALHOST = "localhost";
    private static final String SERVER_HOST = "34.84.112.220";
    private static final int PORT = 8080;
    private static final String DEFAULT_MESSAGE = "Welcome to Client Server Programming";

    public static void main(String... args) {

        try(Socket client = new Socket()) {
            client.connect(new InetSocketAddress(LOCALHOST, PORT));
            client.setKeepAlive(true);
            System.out.println(client.getInetAddress());
            System.out.println(client.getLocalPort());
            System.out.println(client.getKeepAlive());
            System.out.println(client.getPort());

            byte[] bytes = DEFAULT_MESSAGE.getBytes();
            client.getOutputStream().write(bytes);
        } catch(IOException exception) {
            System.err.println(exception);
        }
    }
}
