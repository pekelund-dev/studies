package dev.pekelund.studies.graph;

import java.util.*;

/**
 * Breadth-First Search (BFS) implementation for graph traversal.
 * 
 * BFS explores a graph level by level, visiting all neighbors of a vertex before
 * moving to the next level. It uses a queue to process vertices in FIFO order.
 * 
 * Time Complexity: O(V + E) where V is vertices and E is edges
 * Space Complexity: O(V) for the queue and visited set
 * 
 * Use cases:
 * - Finding shortest path in unweighted graphs
 * - Level-order traversal
 * - Finding all nodes within a certain distance
 * - Web crawling
 */
public class BFS {
    
    /**
     * Performs BFS traversal starting from the given vertex.
     * 
     * @param graph the graph to traverse
     * @param start the starting vertex
     * @return list of vertices in the order they were visited
     */
    public static List<Integer> bfs(Graph graph, int start) {
        List<Integer> visitOrder = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        
        // Start with the initial vertex
        queue.offer(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            // Dequeue a vertex
            int current = queue.poll();
            visitOrder.add(current);
            
            // Enqueue all unvisited neighbors
            for (Graph.Edge edge : graph.getNeighbors(current)) {
                int neighbor = edge.destination;
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        
        return visitOrder;
    }
    
    /**
     * Performs BFS and returns the shortest path distances from start to all reachable vertices.
     * This is useful for finding shortest paths in unweighted graphs.
     * 
     * @param graph the graph to traverse
     * @param start the starting vertex
     * @return map of vertex -> distance from start
     */
    public static Map<Integer, Integer> bfsWithDistances(Graph graph, int start) {
        Map<Integer, Integer> distances = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        
        // Initialize with start vertex at distance 0
        queue.offer(start);
        visited.add(start);
        distances.put(start, 0);
        
        while (!queue.isEmpty()) {
            int current = queue.poll();
            int currentDistance = distances.get(current);
            
            // Process all neighbors
            for (Graph.Edge edge : graph.getNeighbors(current)) {
                int neighbor = edge.destination;
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    distances.put(neighbor, currentDistance + 1);
                    queue.offer(neighbor);
                }
            }
        }
        
        return distances;
    }
    
    /**
     * Finds the shortest path between two vertices using BFS.
     * Returns the path as a list of vertices from start to end.
     * 
     * @param graph the graph to search
     * @param start the starting vertex
     * @param end the target vertex
     * @return list representing the shortest path, or empty list if no path exists
     */
    public static List<Integer> shortestPath(Graph graph, int start, int end) {
        if (start == end) {
            return Arrays.asList(start);
        }
        
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        
        queue.offer(start);
        visited.add(start);
        parent.put(start, null);
        
        boolean found = false;
        
        // BFS to find the path
        while (!queue.isEmpty() && !found) {
            int current = queue.poll();
            
            for (Graph.Edge edge : graph.getNeighbors(current)) {
                int neighbor = edge.destination;
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.offer(neighbor);
                    
                    if (neighbor == end) {
                        found = true;
                        break;
                    }
                }
            }
        }
        
        // Reconstruct path if found
        if (!found) {
            return Collections.emptyList();
        }
        
        List<Integer> path = new ArrayList<>();
        Integer current = end;
        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Demonstrates BFS with sample graphs.
     */
    public static void demo() {
        System.out.println("=== Breadth-First Search (BFS) Demo ===\n");
        
        // Create a sample graph
        //     0
        //    / \
        //   1   2
        //  / \   \
        // 3   4   5
        Graph graph = new Graph(false); // undirected graph
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(0, 2, 1.0);
        graph.addEdge(1, 3, 1.0);
        graph.addEdge(1, 4, 1.0);
        graph.addEdge(2, 5, 1.0);
        
        System.out.println("Graph structure:");
        System.out.println(graph);
        
        // Perform BFS
        System.out.println("BFS starting from vertex 0:");
        List<Integer> visitOrder = bfs(graph, 0);
        System.out.println("Visit order: " + visitOrder);
        System.out.println("Explanation: BFS visits level by level - first 0, then its neighbors (1,2),");
        System.out.println("             then their neighbors (3,4,5)\n");
        
        // BFS with distances
        System.out.println("BFS with distances from vertex 0:");
        Map<Integer, Integer> distances = bfsWithDistances(graph, 0);
        for (Map.Entry<Integer, Integer> entry : distances.entrySet()) {
            System.out.println("  Distance to vertex " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("Explanation: Shows the minimum number of edges to reach each vertex\n");
        
        // Shortest path
        System.out.println("Shortest path from 0 to 5:");
        List<Integer> path = shortestPath(graph, 0, 5);
        System.out.println("Path: " + path);
        System.out.println("Explanation: BFS guarantees the shortest path in unweighted graphs\n");
        
        // Comparison with DFS
        System.out.println("BFS vs DFS comparison:");
        System.out.println("BFS visit order: " + bfs(graph, 0));
        System.out.println("DFS visit order: " + DFS.recursiveDFS(graph, 0));
        System.out.println("Explanation: BFS explores breadth-first (level by level),");
        System.out.println("             while DFS explores depth-first (one branch at a time)\n");
    }
}
