package dev.pekelund.studies.grpc;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Helper class to run the complete gRPC demonstration.
 * Starts both server and client in a coordinated way.
 */
public class GrpcDemo {
    
    /**
     * Runs the complete gRPC demo showing server and client interaction.
     */
    public static void demo() throws Exception {
        System.out.println("=== Complete gRPC Demo ===\n");
        System.out.println("This demo shows a gRPC server and client communicating");
        System.out.println("using all four RPC patterns: unary, server streaming,");
        System.out.println("client streaming, and bidirectional streaming.\n");
        
        // Start server in background thread
        GreetingServer server = new GreetingServer(50051);
        CountDownLatch serverReady = new CountDownLatch(1);
        
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
                serverReady.countDown();
                // Keep server running
                Thread.sleep(30000); // Run for 30 seconds max
            } catch (Exception e) {
                System.err.println("Server error: " + e.getMessage());
            }
        });
        
        serverThread.setDaemon(true);
        serverThread.start();
        
        // Wait for server to start
        System.out.println("Waiting for server to start...");
        serverReady.await(5, TimeUnit.SECONDS);
        Thread.sleep(1000); // Give it a moment to be ready
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SERVER STARTED - Now running client demonstrations");
        System.out.println("=".repeat(60) + "\n");
        
        // Run client demonstrations
        GreetingClient client = new GreetingClient("localhost", 50051);
        
        try {
            // 1. Unary call
            System.out.println("\n" + "-".repeat(60));
            System.out.println("DEMO 1: Unary RPC (single request → single response)");
            System.out.println("-".repeat(60));
            client.unaryCall("Alice");
            Thread.sleep(1000);
            
            // 2. Server streaming
            System.out.println("\n" + "-".repeat(60));
            System.out.println("DEMO 2: Server Streaming (single request → multiple responses)");
            System.out.println("-".repeat(60));
            client.serverStreamingCall("Bob");
            Thread.sleep(1000);
            
            // 3. Client streaming
            System.out.println("\n" + "-".repeat(60));
            System.out.println("DEMO 3: Client Streaming (multiple requests → single response)");
            System.out.println("-".repeat(60));
            client.clientStreamingCall(Arrays.asList("Charlie", "David", "Eve"));
            Thread.sleep(1000);
            
            // 4. Bidirectional streaming
            System.out.println("\n" + "-".repeat(60));
            System.out.println("DEMO 4: Bidirectional Streaming (multiple ↔ multiple)");
            System.out.println("-".repeat(60));
            client.bidirectionalStreamingCall(Arrays.asList("Frank", "Grace", "Henry"));
            Thread.sleep(1000);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("ALL DEMOS COMPLETED SUCCESSFULLY");
            System.out.println("=".repeat(60));
            
            System.out.println("\nKey Takeaways:");
            System.out.println("──────────────────────────────────────────────────────────");
            System.out.println("✓ Unary RPC: Traditional request-response (like REST)");
            System.out.println("✓ Server Streaming: Real-time updates from server");
            System.out.println("✓ Client Streaming: Efficient batch uploads to server");
            System.out.println("✓ Bidirectional: Full-duplex communication (chat, games)");
            System.out.println();
            System.out.println("Why use gRPC?");
            System.out.println("──────────────────────────────────────────────────────────");
            System.out.println("• Protocol Buffers: Compact binary format (vs JSON)");
            System.out.println("• HTTP/2: Multiplexing, flow control, header compression");
            System.out.println("• Type Safety: Strongly-typed contracts");
            System.out.println("• Performance: ~7-10x faster than REST in many cases");
            System.out.println("• Streaming: Native support for all streaming patterns");
            System.out.println("• Multi-language: Java, Go, Python, C++, etc.\n");
            
        } finally {
            client.shutdown();
            server.stop();
            System.out.println("✓ Server and client shut down cleanly\n");
        }
    }
}
