package dev.pekelund.studies.grpc;

import dev.pekelund.studies.grpc.generated.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * gRPC Server implementation demonstrating various RPC patterns.
 * 
 * gRPC (gRPC Remote Procedure Call) is a high-performance, open-source framework
 * developed by Google for building distributed systems.
 * 
 * Key features:
 * - Uses Protocol Buffers for serialization (efficient, typed)
 * - HTTP/2 based (multiplexing, streaming, flow control)
 * - Multiple language support
 * - Bidirectional streaming
 * 
 * RPC patterns demonstrated:
 * 1. Unary: Single request, single response (like REST)
 * 2. Server streaming: Single request, multiple responses
 * 3. Client streaming: Multiple requests, single response
 * 4. Bidirectional streaming: Multiple requests and responses
 * 
 * Use cases:
 * - Microservices communication
 * - Real-time bidirectional communication
 * - Mobile-to-backend communication
 * - High-performance APIs
 */
public class GreetingServer {
    
    private Server server;
    private final int port;
    
    /**
     * Creates a gRPC server on the specified port.
     */
    public GreetingServer(int port) {
        this.port = port;
    }
    
    /**
     * Starts the gRPC server.
     */
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new GreetingServiceImpl())
                .build()
                .start();
        
        System.out.println("✓ gRPC Server started on port " + port);
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** Shutting down gRPC server (JVM shutting down)");
            try {
                GreetingServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** Server shut down");
        }));
    }
    
    /**
     * Stops the gRPC server.
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
    
    /**
     * Waits for the server to terminate.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    
    /**
     * Implementation of the GreetingService.
     */
    static class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
        
        /**
         * Unary RPC: Single request, single response.
         * This is the most common pattern, similar to traditional REST APIs.
         */
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
            System.out.println("→ Received unary request from: " + request.getName());
            
            // Build response
            HelloResponse response = HelloResponse.newBuilder()
                    .setMessage("Hello, " + request.getName() + "!")
                    .setTimestamp(System.currentTimeMillis())
                    .build();
            
            // Send response
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            System.out.println("← Sent response to: " + request.getName());
        }
        
        /**
         * Server-side streaming RPC: Single request, stream of responses.
         * Useful for sending multiple messages over time in response to a single request.
         * Example use cases: Stock price updates, news feeds, notifications.
         */
        @Override
        public void sayHelloMany(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
            System.out.println("→ Received server-streaming request from: " + request.getName());
            
            // Send multiple responses
            for (int i = 1; i <= 5; i++) {
                HelloResponse response = HelloResponse.newBuilder()
                        .setMessage("Hello #" + i + ", " + request.getName() + "!")
                        .setTimestamp(System.currentTimeMillis())
                        .build();
                
                responseObserver.onNext(response);
                System.out.println("← Sent response #" + i + " to: " + request.getName());
                
                // Simulate delay between messages
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            responseObserver.onCompleted();
            System.out.println("✓ Completed server streaming to: " + request.getName());
        }
        
        /**
         * Client-side streaming RPC: Stream of requests, single response.
         * Client sends multiple messages, server responds once with aggregated result.
         * Example use cases: File upload in chunks, batch processing, data aggregation.
         */
        @Override
        public StreamObserver<HelloRequest> sayHelloFromMany(
                StreamObserver<HelloSummary> responseObserver) {
            
            return new StreamObserver<HelloRequest>() {
                private int count = 0;
                private StringBuilder names = new StringBuilder();
                
                @Override
                public void onNext(HelloRequest request) {
                    count++;
                    if (names.length() > 0) {
                        names.append(", ");
                    }
                    names.append(request.getName());
                    System.out.println("→ Received request #" + count + " from: " + request.getName());
                }
                
                @Override
                public void onError(Throwable t) {
                    System.err.println("✗ Error in client streaming: " + t.getMessage());
                }
                
                @Override
                public void onCompleted() {
                    // Send aggregated response
                    HelloSummary summary = HelloSummary.newBuilder()
                            .setRequestCount(count)
                            .setSummaryMessage("Received greetings from: " + names.toString())
                            .build();
                    
                    responseObserver.onNext(summary);
                    responseObserver.onCompleted();
                    
                    System.out.println("✓ Completed client streaming. Total requests: " + count);
                }
            };
        }
        
        /**
         * Bidirectional streaming RPC: Stream of requests and responses.
         * Both client and server can send messages independently.
         * Example use cases: Chat applications, real-time collaboration, multiplayer games.
         */
        @Override
        public StreamObserver<HelloRequest> chatHello(
                StreamObserver<HelloResponse> responseObserver) {
            
            return new StreamObserver<HelloRequest>() {
                @Override
                public void onNext(HelloRequest request) {
                    System.out.println("→ Received chat message from: " + request.getName());
                    
                    // Immediately respond to each message
                    HelloResponse response = HelloResponse.newBuilder()
                            .setMessage("Echo: Hello, " + request.getName() + "!")
                            .setTimestamp(System.currentTimeMillis())
                            .build();
                    
                    responseObserver.onNext(response);
                    System.out.println("← Sent chat response to: " + request.getName());
                }
                
                @Override
                public void onError(Throwable t) {
                    System.err.println("✗ Error in bidirectional streaming: " + t.getMessage());
                }
                
                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                    System.out.println("✓ Completed bidirectional streaming");
                }
            };
        }
    }
    
    /**
     * Demonstrates running the gRPC server.
     * In a real application, this would run indefinitely.
     */
    public static void demo() throws IOException, InterruptedException {
        System.out.println("=== gRPC Server Demo ===\n");
        
        System.out.println("Starting gRPC server...");
        GreetingServer server = new GreetingServer(50051);
        server.start();
        
        System.out.println("\nServer is ready to accept requests.");
        System.out.println("In a real application, the server would run indefinitely.");
        System.out.println("For this demo, the server will run for 3 seconds...\n");
        
        // In demo mode, run for a few seconds then stop
        Thread.sleep(3000);
        
        System.out.println("Stopping server...");
        server.stop();
        System.out.println("✓ Server stopped\n");
    }
}
