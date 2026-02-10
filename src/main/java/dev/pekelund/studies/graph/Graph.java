package dev.pekelund.studies.graph;

import java.util.*;

/**
 * Generic Graph data structure that can be used for various graph algorithms.
 * Supports both directed and undirected graphs with weighted edges.
 * 
 * This implementation uses an adjacency list representation which provides:
 * - Efficient storage for sparse graphs
 * - O(1) edge addition
 * - O(degree) neighbor lookup
 */
public class Graph {
    /**
     * Represents an edge in the graph with a destination vertex and weight.
     */
    public static class Edge {
        public final int destination;
        public final double weight;
        
        public Edge(int destination, double weight) {
            this.destination = destination;
            this.weight = weight;
        }
        
        @Override
        public String toString() {
            return String.format("-> %d (weight: %.2f)", destination, weight);
        }
    }
    
    /**
     * Adjacency list: For each vertex, stores a list of edges to neighboring vertices.
     */
    private final Map<Integer, List<Edge>> adjacencyList;
    
    /**
     * Indicates whether the graph is directed (true) or undirected (false).
     */
    private final boolean isDirected;
    
    /**
     * Creates a new Graph.
     * 
     * @param isDirected true for directed graph, false for undirected graph
     */
    public Graph(boolean isDirected) {
        this.adjacencyList = new HashMap<>();
        this.isDirected = isDirected;
    }
    
    /**
     * Adds a vertex to the graph.
     * 
     * @param vertex the vertex identifier
     */
    public void addVertex(int vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }
    
    /**
     * Adds an edge between two vertices with the specified weight.
     * For undirected graphs, adds edges in both directions.
     * 
     * @param from the source vertex
     * @param to the destination vertex
     * @param weight the edge weight (use 1.0 for unweighted graphs)
     */
    public void addEdge(int from, int to, double weight) {
        // Ensure both vertices exist
        addVertex(from);
        addVertex(to);
        
        // Add edge from -> to
        adjacencyList.get(from).add(new Edge(to, weight));
        
        // For undirected graphs, also add edge to -> from
        if (!isDirected) {
            adjacencyList.get(to).add(new Edge(from, weight));
        }
    }
    
    /**
     * Gets all neighbors of a given vertex.
     * 
     * @param vertex the vertex to get neighbors for
     * @return list of edges to neighboring vertices, or empty list if vertex doesn't exist
     */
    public List<Edge> getNeighbors(int vertex) {
        return adjacencyList.getOrDefault(vertex, Collections.emptyList());
    }
    
    /**
     * Gets all vertices in the graph.
     * 
     * @return set of all vertex identifiers
     */
    public Set<Integer> getVertices() {
        return adjacencyList.keySet();
    }
    
    /**
     * Gets the number of vertices in the graph.
     * 
     * @return vertex count
     */
    public int getVertexCount() {
        return adjacencyList.size();
    }
    
    /**
     * Checks if the graph is directed.
     * 
     * @return true if directed, false if undirected
     */
    public boolean isDirected() {
        return isDirected;
    }
    
    /**
     * Returns a string representation of the graph showing all vertices and edges.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph (").append(isDirected ? "directed" : "undirected").append("):\n");
        for (int vertex : adjacencyList.keySet()) {
            sb.append("  Vertex ").append(vertex).append(": ");
            List<Edge> edges = adjacencyList.get(vertex);
            if (edges.isEmpty()) {
                sb.append("no outgoing edges");
            } else {
                sb.append(edges);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
