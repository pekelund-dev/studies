package dev.pekelund.studies.graph;

import java.util.*;

/**
 * Depth-First Search (DFS) implementation for graph traversal.
 * 
 * DFS explores a graph by going as deep as possible along each branch before backtracking.
 * It uses a stack (either explicitly or via recursion) to keep track of vertices to visit.
 * 
 * Time Complexity: O(V + E) where V is vertices and E is edges
 * Space Complexity: O(V) for the visited set and recursion stack
 * 
 * Use cases:
 * - Finding connected components
 * - Detecting cycles
 * - Topological sorting
 * - Solving maze problems
 */
public class DFS {
    
    /**
     * Performs iterative DFS traversal starting from the given vertex.
     * Uses an explicit stack instead of recursion.
     * 
     * @param graph the graph to traverse
     * @param start the starting vertex
     * @return list of vertices in the order they were visited
     */
    public static List<Integer> iterativeDFS(Graph graph, int start) {
        List<Integer> visitOrder = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> stack = new Stack<>();
        
        // Start with the initial vertex
        stack.push(start);
        
        while (!stack.isEmpty()) {
            // Pop a vertex from the stack
            int current = stack.pop();
            
            // Skip if already visited
            if (visited.contains(current)) {
                continue;
            }
            
            // Mark as visited and add to result
            visited.add(current);
            visitOrder.add(current);
            
            // Add all unvisited neighbors to stack
            // We reverse the order to maintain left-to-right traversal
            List<Graph.Edge> neighbors = graph.getNeighbors(current);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                int neighbor = neighbors.get(i).destination;
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }
        
        return visitOrder;
    }
    
    /**
     * Performs recursive DFS traversal starting from the given vertex.
     * This is the classic DFS implementation using the call stack.
     * 
     * @param graph the graph to traverse
     * @param start the starting vertex
     * @return list of vertices in the order they were visited
     */
    public static List<Integer> recursiveDFS(Graph graph, int start) {
        List<Integer> visitOrder = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        dfsRecursive(graph, start, visited, visitOrder);
        return visitOrder;
    }
    
    /**
     * Helper method for recursive DFS.
     * 
     * @param graph the graph to traverse
     * @param vertex current vertex being visited
     * @param visited set of already visited vertices
     * @param visitOrder list to record the order of visits
     */
    private static void dfsRecursive(Graph graph, int vertex, Set<Integer> visited, List<Integer> visitOrder) {
        // Mark current vertex as visited
        visited.add(vertex);
        visitOrder.add(vertex);
        
        // Recursively visit all unvisited neighbors
        for (Graph.Edge edge : graph.getNeighbors(vertex)) {
            if (!visited.contains(edge.destination)) {
                dfsRecursive(graph, edge.destination, visited, visitOrder);
            }
        }
    }
    
    /**
     * Demonstrates DFS with a sample graph.
     */
    public static void demo() {
        System.out.println("=== Depth-First Search (DFS) Demo ===\n");
        
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
        
        // Perform iterative DFS
        System.out.println("Iterative DFS starting from vertex 0:");
        List<Integer> iterativeResult = iterativeDFS(graph, 0);
        System.out.println("Visit order: " + iterativeResult);
        System.out.println("Explanation: DFS goes deep first - visits 0, then explores the leftmost branch");
        System.out.println("             completely (0->1->3, then 4), then explores other branches (2->5)\n");
        
        // Perform recursive DFS
        System.out.println("Recursive DFS starting from vertex 0:");
        List<Integer> recursiveResult = recursiveDFS(graph, 0);
        System.out.println("Visit order: " + recursiveResult);
        System.out.println("Explanation: Same traversal using recursion instead of explicit stack\n");
        
        // Example with disconnected components
        Graph disconnectedGraph = new Graph(false);
        disconnectedGraph.addEdge(0, 1, 1.0);
        disconnectedGraph.addEdge(2, 3, 1.0);
        
        System.out.println("DFS on graph with disconnected components:");
        System.out.println("Component 1 (starting from 0): " + recursiveDFS(disconnectedGraph, 0));
        System.out.println("Component 2 (starting from 2): " + recursiveDFS(disconnectedGraph, 2));
        System.out.println("Explanation: DFS only visits vertices reachable from the starting vertex\n");
    }
}
