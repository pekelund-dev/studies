# Java Data Structures & Algorithms Studies

A comprehensive educational repository containing well-documented implementations of various data structures, algorithms, and distributed systems concepts in Java.

## 📚 Contents

### Graph Algorithms
- **DFS (Depth-First Search)** - Both iterative and recursive implementations
- **BFS (Breadth-First Search)** - Level-order traversal with shortest path finding
- **Dijkstra's Algorithm** - Shortest path in weighted graphs
- **A* Pathfinding** - Heuristic-based pathfinding with various distance metrics
- **Graph Class** - Reusable graph data structure supporting directed/undirected weighted graphs

### Tree Structures
- **Trie (Prefix Tree)** - Complete implementation with search, autocomplete, and deletion

### Spatial Data Structures
- **Geohash** - Geographic coordinate encoding with proximity search
- **QuadTree** - 2D spatial partitioning for efficient range queries
- **Google S2** - Spherical geometry for geographic indexing

### Distributed Systems
- **gRPC** - Complete client-server example demonstrating all 4 RPC patterns:
  - Unary RPC (single request/response)
  - Server-side streaming
  - Client-side streaming
  - Bidirectional streaming

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Building the Project
```bash
# Clone the repository
git clone https://github.com/pekelund-dev/studies.git
cd studies

# Compile the project (this also generates gRPC code from protobuf)
mvn clean compile

# Package the application
mvn package
```

### Running the Application

#### Interactive Mode (Menu)
```bash
# Run with Maven
mvn exec:java -Dexec.mainClass="dev.pekelund.studies.Main"

# Or run the JAR
java -jar target/studies-1.0-SNAPSHOT.jar
```

This will present an interactive menu where you can choose which demonstration to run.

#### Run All Demonstrations
```bash
# Run all demos at once
mvn exec:java -Dexec.mainClass="dev.pekelund.studies.Main" -Dexec.args="all"

# Or with the JAR
java -jar target/studies-1.0-SNAPSHOT.jar all
```

## 📖 What You'll Learn

Each implementation includes:

- ✅ **Clear, comprehensive comments** explaining how the code works
- ✅ **Time and space complexity analysis**
- ✅ **Working demonstrations** with sample data
- ✅ **Visual output** showing the algorithm in action
- ✅ **Real-world use cases** and applications
- ✅ **Comparisons** between different approaches where applicable

## 🗂️ Project Structure

```
studies/
├── src/
│   ├── main/
│   │   ├── java/dev/pekelund/studies/
│   │   │   ├── graph/          # Graph algorithms
│   │   │   │   ├── Graph.java
│   │   │   │   ├── DFS.java
│   │   │   │   ├── BFS.java
│   │   │   │   ├── Dijkstra.java
│   │   │   │   └── AStar.java
│   │   │   ├── tree/           # Tree structures
│   │   │   │   └── Trie.java
│   │   │   ├── spatial/        # Spatial data structures
│   │   │   │   ├── Geohash.java
│   │   │   │   ├── QuadTree.java
│   │   │   │   └── S2Geometry.java
│   │   │   ├── grpc/           # gRPC examples
│   │   │   │   ├── GreetingServer.java
│   │   │   │   ├── GreetingClient.java
│   │   │   │   └── GrpcDemo.java
│   │   │   └── Main.java       # Main application
│   │   └── proto/              # Protocol buffer definitions
│   │       └── greeting.proto
├── pom.xml                     # Maven configuration
└── README.md
```

## 🎯 Example Output

When you run a demonstration, you'll see detailed output like:

```
=== Depth-First Search (DFS) Demo ===

Graph structure:
Graph (undirected):
  Vertex 0: [-> 1 (weight: 1.00), -> 2 (weight: 1.00)]
  Vertex 1: [-> 0 (weight: 1.00), -> 3 (weight: 1.00), -> 4 (weight: 1.00)]
  ...

Iterative DFS starting from vertex 0:
Visit order: [0, 1, 3, 4, 2, 5]
Explanation: DFS goes deep first - visits 0, then explores the leftmost branch
             completely (0->1->3, then 4), then explores other branches (2->5)
```

## 💡 Key Concepts Covered

- **Graph Traversal**: Understanding different ways to explore graphs
- **Shortest Path**: Finding optimal routes in weighted graphs
- **Heuristic Search**: Using domain knowledge to guide search algorithms
- **Prefix Trees**: Efficient string storage and retrieval
- **Spatial Indexing**: Organizing geographic and 2D data for fast queries
- **Remote Procedure Calls**: Modern distributed system communication
- **Streaming Protocols**: Bidirectional communication patterns

## 🛠️ Technologies Used

- **Java 11** - Core language
- **Maven** - Build and dependency management
- **gRPC** - High-performance RPC framework
- **Protocol Buffers** - Efficient serialization
- **Google S2** - Spherical geometry library

## 📝 Learning Path

Recommended order for beginners:

1. Start with **DFS** and **BFS** to understand graph traversal
2. Move to **Dijkstra** to learn about weighted graphs
3. Study **A*** to see how heuristics improve performance
4. Explore **Trie** for string manipulation algorithms
5. Learn **Geohash** and **QuadTree** for spatial reasoning
6. Study **S2** for advanced geographic concepts
7. Finish with **gRPC** for distributed systems

## 🤝 Contributing

This is a learning repository. Feel free to:
- Add more algorithms and data structures
- Improve documentation and comments
- Fix bugs or add test cases
- Suggest new features

## 📄 License

This project is open source and available for educational purposes.

## 🙏 Acknowledgments

Implementations are based on well-known algorithms from computer science literature and industry best practices, created for educational purposes.
