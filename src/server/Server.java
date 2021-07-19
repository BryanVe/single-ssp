package server;

import graph.GraphMatrix;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private final int port;
    private final InetSocketAddress listenAddress;
    private Selector selector;
    private ArrayList<GraphMatrix> graphs = new ArrayList<>();

    public Server(String address, int port) throws IOException {
        this.port = port;
        listenAddress = new InetSocketAddress(address, port);
    }

    public static void main(String[] args) {
        try {
            int port = 9093;
            String address = "localhost";
            Server server = new Server(address, port);

            server.start();
        } catch (IOException error) {
            logger(error.getMessage());
            error.printStackTrace();
        }
    }

    private static void logger(String message) {
        System.out.println(message);
    }

    private void start() throws IOException {
        // creamos una instancia de un selector y la abrimos
        selector = Selector.open();

        // creamos el canal hacia el servidor y también lo abrimos
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        // deshabilitamos el bloqueo del canal para la asincronía
        serverChannel.configureBlocking(false);

        // asociamos el canal del servidor a la dirección
        serverChannel.socket().bind(listenAddress);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        // el servidor comienza a escuchar
        System.out.printf("Server is listening on port: %d\n", port);
        while (true) {
            // verificamos el número de keys
            int numberOfKeys = selector.select();
            if (numberOfKeys == 0) {
                continue;
            }

            // creamos una lista de los keys listos para procesar
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                // obtenemos una key de la lista
                SelectionKey key = iterator.next();

                // la quitamos de la lista para no repetirla
                iterator.remove();

                // verificamos si es válida
                if (!key.isValid()) {
                    continue;
                }

                // procesamos la operación de la key
                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                }
            }
        }
    }

    // aceptar key
    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        System.out.println("Connected to: " + remoteAddr);

        channel.register(selector, SelectionKey.OP_READ);
    }

    // read from the socket channel
    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead = channel.read(buffer);

        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);

        String receivedMessage = new String(data);
        System.out.println("Received message:");
        System.out.println(receivedMessage);

        GraphMatrix graphMatrix = new GraphMatrix();
        graphMatrix.readMatrix(receivedMessage);
        graphs.add(graphMatrix);

        buffer.clear();

        channel.register(selector, SelectionKey.OP_WRITE);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // envío de respuesta
        GraphMatrix graphMatrix = graphs.get(0);
        String sentMessage = graphMatrix.dijkstra(0);

        // String message = "mensaje del servidor al cliente";
        buffer.put(sentMessage.getBytes());
        buffer.flip();
        channel.write(buffer);
        System.out.printf("Sent message: %s\n", sentMessage);
        buffer.clear();

        // cerramos el canal
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        System.out.println("Connection closed by client: " + remoteAddr);
        graphs.remove(0);
        channel.close();
        key.cancel();
    }
}