package dev.pekelund.studies.graph;

import java.util.*;

/**
 * A* (A-star) pathfinding algorithm implementation.
 * 
 * A* is an extension of Dijkstra's algorithm that uses a heuristic function to guide
 * the search toward the goal, making it more efficient for point-to-point pathfinding.
 * 
 * The algorithm uses: f(n) = g(n) + h(n) where:
 * - g(n) is the actual cost from start to node n
 * - h(n) is the heuristic estimated cost from n to goal
 * - f(n) is the estimated total cost of path through n
 * 
 * Time Complexity: O(E log V) in practice, but depends on heuristic quality
 * Space Complexity: O(V)
 * 
 * Use cases:
 * - Game pathfinding (characters moving on a map)
 * - Robot navigation
 * - GPS route planning with heuristics
 * - Any scenario where you have domain knowledge to estimate remaining distance
 * 
 * Requirements:
 * - Heuristic must be admissible (never overestimate the actual cost)
 * - For optimal paths, heuristic should also be consistent
 */
public class AStar {
    
    /**
     * Represents a coordinate in 2D space.
     * Used for calculating heuristic distances.
     */
    public static class Coordinate {
        public final double x;
        public final double y;
        
        public Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        /**
         * Calculates Euclidean distance to another coordinate.
         */
        public double distanceTo(Coordinate other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
        
        /**
         * Calculates Manhattan distance to another coordinate.
         * Often used in grid-based movement where diagonal moves aren't allowed.
         */
        public double manhattanDistanceTo(Coordinate other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
        }
    }
    
    /**
     * Heuristic function interface for estimating cost from a vertex to the goal.
     */
    public interface Heuristic {
        double estimate(int from, int to);
    }
    
    /**
     * Result of A* algorithm.
     */
    public static class Result {
        public final List<Integer> path;
        public final double cost;
        public final int nodesExplored;
        
        public Result(List<Integer> path, double cost, int nodesExplored) {
            this.path = path;
            this.cost = cost;
            this.nodesExplored = nodesExplored;
        }
    }
    
    /**
     * Node used in the priority queue.
     */
    private static class Node implements Comparable<Node> {
        int vertex;
        double gScore; // Actual cost from start
        double fScore; // Estimated total cost (g + h)
        
        Node(int vertex, double gScore, double fScore) {
            this.vertex = vertex;
            this.gScore = gScore;
            this.fScore = fScore;
        }
        
        @Override
        public int compareTo(Node other) {
            return Double.compare(this.fScore, other.fScore);
        }
    }
    
    /**
     * Runs A* algorithm to find the shortest path from start to goal.
     * 
     * @param graph the graph to search
     * @param start the starting vertex
     * @param goal the goal vertex
     * @param heuristic function to estimate cost from any vertex to goal
     * @return Result containing the path, cost, and number of nodes explored
     */
    public static Result aStar(Graph graph, int start, int goal, Heuristic heuristic) {
        // Track actual cost from start to each vertex
        Map<Integer, Double> gScores = new HashMap<>();
        
        // Track best path to each vertex
        Map<Integer, Integer> cameFrom = new HashMap<>();
        
        // Priority queue ordered by f-score
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        
        // Track which vertices we've already processed
        Set<Integer> closedSet = new HashSet<>();
        
        int nodesExplored = 0;
        
        // Initialize start vertex
        gScores.put(start, 0.0);
        double hScore = heuristic.estimate(start, goal);
        openSet.offer(new Node(start, 0.0, hScore));
        
        while (!openSet.isEmpty()) {
            // Get vertex with lowest f-score
            Node current = openSet.poll();
            int currentVertex = current.vertex;
            
            // Skip if already processed
            if (closedSet.contains(currentVertex)) {
                continue;
            }
            
            closedSet.add(currentVertex);
            nodesExplored++;
            
            // Check if we reached the goal
            if (currentVertex == goal) {
                return new Result(
                    reconstructPath(cameFrom, goal),
                    gScores.get(goal),
                    nodesExplored
                );
            }
            
            // Examine all neighbors
            for (Graph.Edge edge : graph.getNeighbors(currentVertex)) {
                int neighbor = edge.destination;
                
                // Skip if already processed
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                
                // Calculate tentative g-score
                double tentativeGScore = gScores.get(currentVertex) + edge.weight;
                
                // If this is a better path to the neighbor
                if (!gScores.containsKey(neighbor) || tentativeGScore < gScores.get(neighbor)) {
                    // Update path
                    cameFrom.put(neighbor, currentVertex);
                    gScores.put(neighbor, tentativeGScore);
                    
                    // Calculate f-score and add to open set
                    double fScore = tentativeGScore + heuristic.estimate(neighbor, goal);
                    openSet.offer(new Node(neighbor, tentativeGScore, fScore));
                }
            }
        }
        
        // No path found
        return new Result(Collections.emptyList(), Double.POSITIVE_INFINITY, nodesExplored);
    }
    
    /**
     * Reconstructs the path from start to goal using the cameFrom map.
     */
    private static List<Integer> reconstructPath(Map<Integer, Integer> cameFrom, int current) {
        List<Integer> path = new ArrayList<>();
        while (current != -1) {
            path.add(current);
            current = cameFrom.getOrDefault(current, -1);
        }
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Demonstrates A* algorithm with a grid-based example.
     */
    public static void demo() {
        System.out.println("=== A* Pathfinding Algorithm Demo ===\n");
        
        // Create a grid-based graph (like a game map)
        // Grid layout (5x5):
        //  0  1  2  3  4
        //  5  6  7  8  9
        // 10 11 12 13 14
        // 15 16 17 18 19
        // 20 21 22 23 24
        
        Graph graph = new Graph(false);
        Map<Integer, Coordinate> positions = new HashMap<>();
        
        // Build a 5x5 grid graph
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int vertex = row * 5 + col;
                positions.put(vertex, new Coordinate(col, row));
                
                // Connect to right neighbor
                if (col < 4) {
                    graph.addEdge(vertex, vertex + 1, 1.0);
                }
                // Connect to bottom neighbor
                if (row < 4) {
                    graph.addEdge(vertex, vertex + 5, 1.0);
                }
            }
        }
        
        // Add some diagonal connections to make it more interesting
        graph.addEdge(6, 12, 1.4); // diagonal
        graph.addEdge(13, 19, 1.4); // diagonal
        
        System.out.println("Grid graph created (5x5 with some diagonals)");
        System.out.println("Start: vertex 0 (top-left)");
        System.out.println("Goal: vertex 24 (bottom-right)\n");
        
        int start = 0;
        int goal = 24;
        
        // Define heuristic: Euclidean distance
        Heuristic euclideanHeuristic = (from, to) -> {
            Coordinate fromPos = positions.get(from);
            Coordinate toPos = positions.get(to);
            return fromPos.distanceTo(toPos);
        };
        
        // Run A* with heuristic
        Result aStarResult = aStar(graph, start, goal, euclideanHeuristic);
        System.out.println("A* Result:");
        System.out.println("  Path: " + aStarResult.path);
        System.out.println("  Cost: " + String.format("%.2f", aStarResult.cost));
        System.out.println("  Nodes explored: " + aStarResult.nodesExplored);
        
        // Run Dijkstra for comparison (heuristic always returns 0)
        Heuristic noHeuristic = (from, to) -> 0.0;
        Result dijkstraResult = aStar(graph, start, goal, noHeuristic);
        System.out.println("\nDijkstra (A* with no heuristic) Result:");
        System.out.println("  Path: " + dijkstraResult.path);
        System.out.println("  Cost: " + String.format("%.2f", dijkstraResult.cost));
        System.out.println("  Nodes explored: " + dijkstraResult.nodesExplored);
        
        System.out.println("\nExplanation:");
        System.out.println("- A* explored fewer nodes than Dijkstra because the heuristic");
        System.out.println("  guides the search toward the goal");
        System.out.println("- The heuristic (Euclidean distance) never overestimates, so A*");
        System.out.println("  still finds the optimal path");
        System.out.println("- Both find the same path, but A* is more efficient");
        System.out.println("- In practice, A* is much faster for large graphs with good heuristics\n");
        
        // Example with Manhattan distance heuristic
        Heuristic manhattanHeuristic = (from, to) -> {
            Coordinate fromPos = positions.get(from);
            Coordinate toPos = positions.get(to);
            return fromPos.manhattanDistanceTo(toPos);
        };
        
        Result manhattanResult = aStar(graph, start, goal, manhattanHeuristic);
        System.out.println("A* with Manhattan distance heuristic:");
        System.out.println("  Nodes explored: " + manhattanResult.nodesExplored);
        System.out.println("Explanation: Manhattan distance is often used for grid-based movement\n");
    }
}
