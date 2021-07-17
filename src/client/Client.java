package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) {
        String address = "localhost";
        int port = 9093;

        Runnable client = () -> {
            try {
                new Client().startClient(address, port);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        };

        new Thread(client, "client-A").start();
        new Thread(client, "client-B").start();
    }

    public void startClient(String address, int port) throws IOException, InterruptedException {
        InetSocketAddress hostAddress = new InetSocketAddress(address, port);
        SocketChannel client = SocketChannel.open(hostAddress);

        String threadName = Thread.currentThread().getName();
        System.out.printf("Client %s started\n", threadName);

        write(client);
        Thread.sleep(1000);
        read(client);

        client.close();
    }

    // Send messages to server
    private void write(SocketChannel client) throws IOException {
        String sentMessage = "mensaje del cliente al servidor";

        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        writeBuffer.put(sentMessage.getBytes());
        writeBuffer.flip();
        client.write(writeBuffer);

        System.out.printf("Sent message: %s\n", sentMessage);
        writeBuffer.clear();
    }

    // Read messages from server
    private void read(SocketChannel client) throws IOException {
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        
        int numRead = client.read(readBuffer);
        byte[] data = new byte[numRead];
        System.arraycopy(readBuffer.array(), 0, data, 0, numRead);
        
        System.out.printf("Received message: %s\n", new String(data));
        readBuffer.clear();
    }
}