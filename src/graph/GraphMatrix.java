package graph;

import java.util.Random;

public class GraphMatrix {

    final int MAX_LIMIT = 7;
    int vertex;
    int[][] matrix;
    Random random = new Random();

    public GraphMatrix() {
        this.vertex = random.nextInt(MAX_LIMIT) + 3;
        matrix = new int[vertex][vertex];
    }

    public static void main(String[] args) {
        GraphMatrix graph = new GraphMatrix();
        graph.randomGraph();
        graph.printGraph();
//        System.out.println(graph.getMsg());
//        graph.readMatrix(graph.getMsg());
//        System.out.println(graph.dijkstra(graph.matrix,0));
    }

    public void addEdge(int source, int destination, int weight) {
        matrix[source][destination] = weight;
        matrix[destination][source] = weight;
    }

    public void printGraph() {
        System.out.println("Graph: (Adjacency Matrix)");
        for (int i = 0; i < vertex; i++) {
            for (int j = 0; j < vertex; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public void randomGraph() {
        int num_edges = random.nextInt(this.vertex * (this.vertex - 1));
        for (int i = 0; i < num_edges; i++) {
            int x = random.nextInt(this.vertex);
            int y = random.nextInt(this.vertex);
            int w = random.nextInt(60) + 1;
            if (x != y && matrix[x][y] == 0) {
                addEdge(x, y, w);
            }
        }
    }

    public String getMsg() {
        StringBuilder str = new StringBuilder();
        str.append(this.vertex + "\n");
        for (int i = 0; i < this.vertex; i++) {
            for (int j = 0; j < this.vertex; j++) {
                str.append(matrix[i][j] + " ");
            }
            str.append("\n");
        }
        return str.toString();
    }

    public void readMatrix(String msg) {
        String[] x = msg.split("\n");
        int v = Integer.parseInt(x[0]);
        this.vertex = v;
        this.matrix = new int[this.vertex][this.vertex];
        for (int i = 1; i < x.length; i++) {
            String[] items = x[i].split(" ");
            for (int j = 0; j < items.length; j++) {
                matrix[i - 1][j] = Integer.parseInt(items[j]);
            }
        }
    }

    public String printSolution(int dist[]) {
        StringBuilder str = new StringBuilder();
        str.append("Vertex \t\t Distance from Source\n");
        for (int i = 0; i < this.vertex; i++)
            str.append(i + "   \t\t " + dist[i]+ "\n");

        return str.toString();
    }

    public int minDistance(int[] dist, Boolean[] sptSet) {

        int min = Integer.MAX_VALUE, min_index = -1;

        for (int v = 0; v < this.vertex; v++)
            if (!sptSet[v] && dist[v] <= min) {
                min = dist[v];
                min_index = v;
            }

        return min_index;
    }


    public String dijkstra(int src) {
        int dist[] = new int[this.vertex];
        Boolean sptSet[] = new Boolean[this.vertex];

        for (int i = 0; i < this.vertex; i++) {
            dist[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
        }

        dist[src] = 0;

        for (int count = 0; count < this.vertex - 1; count++) {
            int u = minDistance(dist, sptSet);

            sptSet[u] = true;

            for (int v = 0; v < this.vertex; v++)
                if (!sptSet[v] && matrix[u][v] != 0 && dist[u] != Integer.MAX_VALUE && dist[u] + matrix[u][v] < dist[v])
                    dist[v] = dist[u] + matrix[u][v];
        }

        return printSolution(dist);
    }
}