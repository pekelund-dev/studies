package dev.pekelund.studies;

import dev.pekelund.studies.graph.*;
import dev.pekelund.studies.tree.*;
import dev.pekelund.studies.spatial.*;
import dev.pekelund.studies.grpc.*;

import java.util.Scanner;

/**
 * Main application for running all data structure and algorithm demonstrations.
 * 
 * This application provides an interactive menu to explore various implementations:
 * - Graph algorithms (DFS, BFS, Dijkstra, A*)
 * - Tree structures (Trie)
 * - Spatial indexing (Geohash, QuadTree, S2)
 * - Network communication (gRPC)
 * 
 * Each demonstration includes:
 * - Clear explanations of the algorithm/structure
 * - Working code examples with sample data
 * - Time/space complexity analysis
 * - Common use cases
 * - Visual output showing the results
 */
public class Main {
    
    private static final String HEADER = 
        "╔═══════════════════════════════════════════════════════════════╗\n" +
        "║     Java Data Structures & Algorithms Study Application      ║\n" +
        "╚═══════════════════════════════════════════════════════════════╝";
    
    private static final String MENU = 
        "\nPlease select a demonstration:\n" +
        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
        "  Graph Algorithms:\n" +
        "    1. DFS (Depth-First Search)\n" +
        "    2. BFS (Breadth-First Search)\n" +
        "    3. Dijkstra's Shortest Path\n" +
        "    4. A* Pathfinding\n" +
        "\n" +
        "  Tree Structures:\n" +
        "    5. Trie (Prefix Tree)\n" +
        "\n" +
        "  Spatial Data Structures:\n" +
        "    6. Geohash\n" +
        "    7. QuadTree\n" +
        "    8. Google S2 Geometry\n" +
        "\n" +
        "  Distributed Systems:\n" +
        "    9. gRPC Client-Server Demo\n" +
        "\n" +
        "  Other Options:\n" +
        "    0. Run all demonstrations\n" +
        "    q. Quit\n" +
        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    
    public static void main(String[] args) {
        // If run with "all" argument, run all demos
        if (args.length > 0 && args[0].equals("all")) {
            runAllDemos();
            return;
        }
        
        // Interactive mode
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println(HEADER);
            System.out.println(MENU);
            System.out.print("\nYour choice: ");
            
            String choice = scanner.nextLine().trim();
            System.out.println();
            
            try {
                switch (choice) {
                    case "1":
                        DFS.demo();
                        break;
                    case "2":
                        BFS.demo();
                        break;
                    case "3":
                        Dijkstra.demo();
                        break;
                    case "4":
                        AStar.demo();
                        break;
                    case "5":
                        Trie.demo();
                        break;
                    case "6":
                        Geohash.demo();
                        break;
                    case "7":
                        QuadTree.demo();
                        break;
                    case "8":
                        S2Geometry.demo();
                        break;
                    case "9":
                        GrpcDemo.demo();
                        break;
                    case "0":
                        runAllDemos();
                        break;
                    case "q":
                    case "Q":
                        System.out.println("Thank you for using the study application!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        continue;
                }
                
                // Pause after each demo
                System.out.println("\n" + "─".repeat(63));
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
                System.out.println("\n\n");
                
            } catch (Exception e) {
                System.err.println("Error running demo: " + e.getMessage());
                e.printStackTrace();
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }
    
    /**
     * Runs all demonstrations in sequence.
     */
    private static void runAllDemos() {
        System.out.println(HEADER);
        System.out.println("\nRunning all demonstrations...\n");
        System.out.println("═".repeat(63));
        
        try {
            // Graph algorithms
            System.out.println("\n" + "═".repeat(63));
            DFS.demo();
            pause();
            
            System.out.println("\n" + "═".repeat(63));
            BFS.demo();
            pause();
            
            System.out.println("\n" + "═".repeat(63));
            Dijkstra.demo();
            pause();
            
            System.out.println("\n" + "═".repeat(63));
            AStar.demo();
            pause();
            
            // Tree structures
            System.out.println("\n" + "═".repeat(63));
            Trie.demo();
            pause();
            
            // Spatial structures
            System.out.println("\n" + "═".repeat(63));
            Geohash.demo();
            pause();
            
            System.out.println("\n" + "═".repeat(63));
            QuadTree.demo();
            pause();
            
            System.out.println("\n" + "═".repeat(63));
            S2Geometry.demo();
            pause();
            
            // gRPC (if not in non-interactive mode)
            System.out.println("\n" + "═".repeat(63));
            GrpcDemo.demo();
            
            System.out.println("\n" + "═".repeat(63));
            System.out.println("✓ ALL DEMONSTRATIONS COMPLETED SUCCESSFULLY!");
            System.out.println("═".repeat(63) + "\n");
            
            printSummary();
            
        } catch (Exception e) {
            System.err.println("Error running demonstrations: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Pauses for a moment between demos.
     */
    private static void pause() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Prints a summary of all implementations.
     */
    private static void printSummary() {
        System.out.println("Summary of Implementations:");
        System.out.println("━".repeat(63));
        System.out.println();
        
        System.out.println("Graph Algorithms:");
        System.out.println("  ✓ DFS - Depth-first traversal, O(V+E)");
        System.out.println("  ✓ BFS - Breadth-first traversal, O(V+E)");
        System.out.println("  ✓ Dijkstra - Shortest path, O((V+E)logV)");
        System.out.println("  ✓ A* - Heuristic pathfinding, O(ElogV)");
        System.out.println();
        
        System.out.println("Tree Structures:");
        System.out.println("  ✓ Trie - Prefix tree with autocomplete, O(m)");
        System.out.println();
        
        System.out.println("Spatial Indexing:");
        System.out.println("  ✓ Geohash - Geographic encoding");
        System.out.println("  ✓ QuadTree - 2D spatial partitioning");
        System.out.println("  ✓ S2 - Spherical geometry (Google)");
        System.out.println();
        
        System.out.println("Distributed Systems:");
        System.out.println("  ✓ gRPC - 4 RPC patterns with Protocol Buffers");
        System.out.println();
        
        System.out.println("All implementations include:");
        System.out.println("  • Comprehensive comments for learning");
        System.out.println("  • Working demonstrations with sample data");
        System.out.println("  • Time/space complexity analysis");
        System.out.println("  • Real-world use cases");
        System.out.println("  • Clear and informative output");
        System.out.println();
    }
}
