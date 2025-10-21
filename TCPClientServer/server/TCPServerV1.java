import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

public class TCPServerV1 {

    private static final int PORT = 8080;

    public static void main(String... args) {
        Socket server = null;
        ServerSocket serverSock = null;
        try {
            serverSock = new ServerSocket(PORT);
            server = serverSock.accept();
            InputStream inputStream = server.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = bReader.readLine()) != null) {
                System.out.print(line);
            }
            System.out.println();
        } catch(IOException exception) {
            System.err.println(exception);
        } finally {
            if (serverSock != null) {
                try{
                    serverSock.close();
                } catch(IOException ex) {
                    System.err.println(ex);
                }
                
            }
        }
    }
}
