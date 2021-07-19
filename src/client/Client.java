package client;

import graph.GraphMatrix;

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

        long startTime = System.nanoTime();
        new Thread(client, "client-1").start();
        new Thread(client, "client-2").start();
        new Thread(client, "client-3").start();
        new Thread(client, "client-4").start();
        new Thread(client, "client-5").start();
        new Thread(client, "client-6").start();
        new Thread(client, "client-7").start();
        new Thread(client, "client-8").start();
        long endTime = System.nanoTime();

        double duration = (double) (endTime - startTime) / 1000000;
        System.out.printf("Time execution: %.2f\n", duration);
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
        GraphMatrix graphMatrix = new GraphMatrix();
        graphMatrix.randomGraph();

        // String sentMessage = "mensaje del cliente al servidor";
        String sentMessage = graphMatrix.getMsg();

        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        writeBuffer.put(sentMessage.getBytes());
        writeBuffer.flip();
        client.write(writeBuffer);

        System.out.println("Sending message:");
        System.out.println(sentMessage);
        writeBuffer.clear();
    }

    // Read messages from server
    private void read(SocketChannel client) throws IOException {
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        
        int numRead = client.read(readBuffer);
        byte[] data = new byte[numRead];
        System.arraycopy(readBuffer.array(), 0, data, 0, numRead);
        
        System.out.println("Received message:");
        System.out.println(new String(data));
        readBuffer.clear();
    }
}