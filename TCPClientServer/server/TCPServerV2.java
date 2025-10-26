import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TCPServerV2 {

    private static final int PORT = 8080;
    private static final String threadPoolName = "TCPClientServerThreads";

    public static void main(String... args) throws InterruptedException {

        ThreadFactory threadFactory = new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(threadPoolName + "-" + thread.getId());
                thread.setPriority(5);
                thread.setDaemon(true);
                return thread;
            }
        };

        ExecutorService executorService = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS , new ArrayBlockingQueue<>(50), threadFactory);

        ServerSocket serverSock = null;
        try {
            serverSock = new ServerSocket(PORT);
            System.out.println("Server listening on port :" + PORT);

            while(true) {
                Socket clientSocket = serverSock.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());
                executorService.submit(() -> handleClient(clientSocket));
            }

        } catch(IOException exception) {
            System.err.println(exception);
        } finally {
            System.out.println("executing finally block");
            if (serverSock != null) {
                try{
                    serverSock.close();
                } catch(IOException ex) {
                    System.err.println(ex);
                }
                
            }
        }

        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

    private static void handleClient(Socket clientSocket) {
        InputStream inputStream;
        try {
            inputStream = clientSocket.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = bReader.readLine()) != null) {
                System.out.print(line);
                System.out.println();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
