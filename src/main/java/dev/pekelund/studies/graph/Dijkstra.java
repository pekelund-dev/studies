package dev.pekelund.studies.graph;

import java.util.*;

/**
 * Dijkstra's shortest path algorithm implementation.
 * 
 * Finds the shortest path from a source vertex to all other vertices in a weighted graph
 * with non-negative edge weights. Uses a priority queue to efficiently select the next
 * vertex with minimum distance.
 * 
 * Time Complexity: O((V + E) log V) with binary heap priority queue
 * Space Complexity: O(V) for distances and parent maps
 * 
 * Use cases:
 * - GPS navigation systems
 * - Network routing protocols
 * - Finding cheapest flights
 * - Any scenario requiring shortest path in weighted graphs
 * 
 * Note: Does not work with negative edge weights (use Bellman-Ford for that)
 */
public class Dijkstra {
    
    /**
     * Result of Dijkstra's algorithm containing distances and paths.
     */
    public static class Result {
        public final Map<Integer, Double> distances;
        public final Map<Integer, Integer> parents;
        
        public Result(Map<Integer, Double> distances, Map<Integer, Integer> parents) {
            this.distances = distances;
            this.parents = parents;
        }
        
        /**
         * Reconstructs the shortest path to a given vertex.
         * 
         * @param target the target vertex
         * @return list of vertices from source to target, or empty if no path exists
         */
        public List<Integer> getPath(int target) {
            if (!distances.containsKey(target) || distances.get(target) == Double.POSITIVE_INFINITY) {
                return Collections.emptyList();
            }
            
            List<Integer> path = new ArrayList<>();
            Integer current = target;
            while (current != null) {
                path.add(current);
                current = parents.get(current);
            }
            Collections.reverse(path);
            return path;
        }
    }
    
    /**
     * Node used in the priority queue, containing vertex and its current distance.
     */
    private static class Node implements Comparable<Node> {
        int vertex;
        double distance;
        
        Node(int vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
        
        @Override
        public int compareTo(Node other) {
            return Double.compare(this.distance, other.distance);
        }
    }
    
    /**
     * Runs Dijkstra's algorithm from the source vertex.
     * 
     * @param graph the graph to search
     * @param source the starting vertex
     * @return Result object containing distances and parent pointers for path reconstruction
     */
    public static Result dijkstra(Graph graph, int source) {
        // Initialize distances with infinity for all vertices
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> parents = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Set<Integer> visited = new HashSet<>();
        
        // Initialize all distances to infinity
        for (int vertex : graph.getVertices()) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }
        
        // Distance to source is 0
        distances.put(source, 0.0);
        parents.put(source, null);
        pq.offer(new Node(source, 0.0));
        
        while (!pq.isEmpty()) {
            // Get vertex with minimum distance
            Node current = pq.poll();
            int u = current.vertex;
            
            // Skip if already processed (can happen with duplicate entries in PQ)
            if (visited.contains(u)) {
                continue;
            }
            visited.add(u);
            
            // Check all neighbors
            for (Graph.Edge edge : graph.getNeighbors(u)) {
                int v = edge.destination;
                double weight = edge.weight;
                
                // Calculate new distance through current vertex
                double newDistance = distances.get(u) + weight;
                
                // If we found a shorter path, update it
                if (newDistance < distances.get(v)) {
                    distances.put(v, newDistance);
                    parents.put(v, u);
                    pq.offer(new Node(v, newDistance));
                }
            }
        }
        
        return new Result(distances, parents);
    }
    
    /**
     * Demonstrates Dijkstra's algorithm with a sample graph.
     */
    public static void demo() {
        System.out.println("=== Dijkstra's Shortest Path Algorithm Demo ===\n");
        
        // Create a weighted graph
        //        7        1
        //    0 -----> 1 -----> 3
        //    |        |        ^
        //  5 |      2 |        | 1
        //    v        v        |
        //    2 ------------> 4
        //           3
        Graph graph = new Graph(true); // directed graph
        graph.addEdge(0, 1, 7.0);
        graph.addEdge(0, 2, 5.0);
        graph.addEdge(1, 3, 1.0);
        graph.addEdge(1, 4, 2.0);
        graph.addEdge(2, 4, 3.0);
        graph.addEdge(4, 3, 1.0);
        
        System.out.println("Weighted directed graph:");
        System.out.println(graph);
        
        // Run Dijkstra from vertex 0
        int source = 0;
        Result result = dijkstra(graph, source);
        
        System.out.println("Shortest distances from vertex " + source + ":");
        for (Map.Entry<Integer, Double> entry : result.distances.entrySet()) {
            int vertex = entry.getKey();
            double distance = entry.getValue();
            
            if (distance == Double.POSITIVE_INFINITY) {
                System.out.printf("  To vertex %d: unreachable\n", vertex);
            } else {
                List<Integer> path = result.getPath(vertex);
                System.out.printf("  To vertex %d: distance = %.1f, path = %s\n", 
                    vertex, distance, path);
            }
        }
        
        System.out.println("\nExplanation:");
        System.out.println("- Dijkstra's algorithm finds the shortest path by always selecting the");
        System.out.println("  vertex with the minimum tentative distance");
        System.out.println("- It guarantees optimal paths for graphs with non-negative weights");
        System.out.println("- The path to vertex 3 goes through 4 (0->2->4->3) instead of direct");
        System.out.println("  path (0->1->3) because it's shorter (5+3+1=9 vs 7+1=8)\n");
        
        // Another example with undirected graph
        System.out.println("Example 2: Undirected weighted graph");
        Graph graph2 = new Graph(false);
        graph2.addEdge(0, 1, 4.0);
        graph2.addEdge(0, 2, 1.0);
        graph2.addEdge(1, 3, 1.0);
        graph2.addEdge(2, 1, 2.0);
        graph2.addEdge(2, 3, 5.0);
        
        Result result2 = dijkstra(graph2, 0);
        System.out.println("Shortest path from 0 to 3: " + result2.getPath(3));
        System.out.println("Distance: " + result2.distances.get(3));
        System.out.println("Explanation: Goes 0->2->1->3 (1+2+1=4) instead of 0->1->3 (4+1=5)\n");
    }
}
