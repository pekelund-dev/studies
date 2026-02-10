package dev.pekelund.studies.grpc;

import dev.pekelund.studies.grpc.generated.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * gRPC Client implementation demonstrating various RPC patterns.
 * 
 * This client demonstrates how to call all four types of gRPC methods:
 * 1. Unary RPC
 * 2. Server-side streaming RPC
 * 3. Client-side streaming RPC
 * 4. Bidirectional streaming RPC
 * 
 * The client connects to the GreetingServer and makes various types of calls.
 */
public class GreetingClient {
    
    private final ManagedChannel channel;
    private final GreetingServiceGrpc.GreetingServiceBlockingStub blockingStub;
    private final GreetingServiceGrpc.GreetingServiceStub asyncStub;
    
    /**
     * Creates a gRPC client that connects to the server at the given host and port.
     */
    public GreetingClient(String host, int port) {
        // Create a channel to the server
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // Disable TLS for simplicity in demo
                .build();
        
        // Create stubs for making calls
        this.blockingStub = GreetingServiceGrpc.newBlockingStub(channel);
        this.asyncStub = GreetingServiceGrpc.newStub(channel);
    }
    
    /**
     * Shuts down the client channel.
     */
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
    
    /**
     * Demonstrates unary RPC: Single request, single response.
     * This is synchronous and blocks until response is received.
     */
    public void unaryCall(String name) {
        System.out.println("\n1. Unary RPC Call");
        System.out.println("   Sending single request for: " + name);
        
        HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();
        
        try {
            HelloResponse response = blockingStub.sayHello(request);
            System.out.println("   ✓ Response: " + response.getMessage());
            System.out.println("   Timestamp: " + response.getTimestamp());
        } catch (Exception e) {
            System.err.println("   ✗ RPC failed: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates server-side streaming RPC: Single request, stream of responses.
     * Server sends multiple responses over time.
     */
    public void serverStreamingCall(String name) {
        System.out.println("\n2. Server-Side Streaming RPC Call");
        System.out.println("   Sending single request, expecting multiple responses");
        
        HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();
        
        try {
            Iterator<HelloResponse> responses = blockingStub.sayHelloMany(request);
            
            int count = 0;
            while (responses.hasNext()) {
                HelloResponse response = responses.next();
                count++;
                System.out.println("   ✓ Response #" + count + ": " + response.getMessage());
            }
            
            System.out.println("   Total responses received: " + count);
        } catch (Exception e) {
            System.err.println("   ✗ RPC failed: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates client-side streaming RPC: Stream of requests, single response.
     * Client sends multiple requests, server responds once with summary.
     */
    public void clientStreamingCall(List<String> names) {
        System.out.println("\n3. Client-Side Streaming RPC Call");
        System.out.println("   Sending multiple requests: " + names);
        
        final CountDownLatch finishLatch = new CountDownLatch(1);
        
        // Create response observer
        StreamObserver<HelloSummary> responseObserver = new StreamObserver<HelloSummary>() {
            @Override
            public void onNext(HelloSummary summary) {
                System.out.println("   ✓ Summary: " + summary.getSummaryMessage());
                System.out.println("   Total requests sent: " + summary.getRequestCount());
            }
            
            @Override
            public void onError(Throwable t) {
                System.err.println("   ✗ RPC failed: " + t.getMessage());
                finishLatch.countDown();
            }
            
            @Override
            public void onCompleted() {
                System.out.println("   ✓ Server completed processing");
                finishLatch.countDown();
            }
        };
        
        // Create request observer for sending requests
        StreamObserver<HelloRequest> requestObserver = asyncStub.sayHelloFromMany(responseObserver);
        
        try {
            // Send multiple requests
            for (String name : names) {
                HelloRequest request = HelloRequest.newBuilder()
                        .setName(name)
                        .build();
                requestObserver.onNext(request);
                System.out.println("   → Sent request for: " + name);
                
                // Simulate delay between requests
                Thread.sleep(200);
            }
            
            // Mark as completed
            requestObserver.onCompleted();
            
            // Wait for response
            finishLatch.await(5, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            System.err.println("   ✗ Error: " + e.getMessage());
            requestObserver.onError(e);
        }
    }
    
    /**
     * Demonstrates bidirectional streaming RPC: Stream of requests and responses.
     * Both client and server send messages independently.
     */
    public void bidirectionalStreamingCall(List<String> names) {
        System.out.println("\n4. Bidirectional Streaming RPC Call");
        System.out.println("   Starting bidirectional chat with: " + names);
        
        final CountDownLatch finishLatch = new CountDownLatch(1);
        
        // Create response observer
        StreamObserver<HelloResponse> responseObserver = new StreamObserver<HelloResponse>() {
            @Override
            public void onNext(HelloResponse response) {
                System.out.println("   ← Received: " + response.getMessage());
            }
            
            @Override
            public void onError(Throwable t) {
                System.err.println("   ✗ Chat failed: " + t.getMessage());
                finishLatch.countDown();
            }
            
            @Override
            public void onCompleted() {
                System.out.println("   ✓ Chat completed");
                finishLatch.countDown();
            }
        };
        
        // Create request observer
        StreamObserver<HelloRequest> requestObserver = asyncStub.chatHello(responseObserver);
        
        try {
            // Send multiple messages
            for (String name : names) {
                HelloRequest request = HelloRequest.newBuilder()
                        .setName(name)
                        .build();
                requestObserver.onNext(request);
                System.out.println("   → Sent: " + name);
                
                // Simulate typing delay
                Thread.sleep(300);
            }
            
            // Mark as completed
            requestObserver.onCompleted();
            
            // Wait for all responses
            finishLatch.await(5, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            System.err.println("   ✗ Error: " + e.getMessage());
            requestObserver.onError(e);
        }
    }
    
    /**
     * Demonstrates all gRPC call patterns.
     * Note: This assumes a server is already running on localhost:50051.
     */
    public static void demo() {
        System.out.println("=== gRPC Client Demo ===\n");
        
        System.out.println("Connecting to server at localhost:50051...");
        GreetingClient client = new GreetingClient("localhost", 50051);
        
        try {
            System.out.println("✓ Connected to server\n");
            System.out.println("Demonstrating all four RPC patterns:\n");
            
            // 1. Unary call
            client.unaryCall("Alice");
            
            // 2. Server streaming
            client.serverStreamingCall("Bob");
            
            // 3. Client streaming
            client.clientStreamingCall(Arrays.asList("Charlie", "David", "Eve"));
            
            // 4. Bidirectional streaming
            client.bidirectionalStreamingCall(Arrays.asList("Frank", "Grace", "Henry"));
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Summary of gRPC Patterns:");
            System.out.println("=".repeat(50));
            System.out.println("1. Unary: Like REST API - simple request/response");
            System.out.println("2. Server Streaming: One request, many responses (e.g., live updates)");
            System.out.println("3. Client Streaming: Many requests, one response (e.g., file upload)");
            System.out.println("4. Bidirectional: Both sides stream (e.g., chat, games)");
            System.out.println("\nAdvantages of gRPC:");
            System.out.println("- Efficient binary protocol (Protocol Buffers)");
            System.out.println("- HTTP/2 based (multiplexing, header compression)");
            System.out.println("- Strongly typed contracts");
            System.out.println("- Built-in streaming support");
            System.out.println("- Multi-language support\n");
            
        } catch (Exception e) {
            System.err.println("✗ Client error: " + e.getMessage());
            System.err.println("\nNote: Make sure the server is running first!");
        } finally {
            try {
                client.shutdown();
                System.out.println("✓ Client disconnected");
            } catch (InterruptedException e) {
                System.err.println("Error shutting down client");
            }
        }
    }
}
