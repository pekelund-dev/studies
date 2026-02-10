package dev.pekelund.studies.spatial;

import java.util.ArrayList;
import java.util.List;

/**
 * QuadTree data structure for efficient 2D spatial indexing.
 * 
 * A QuadTree recursively divides 2D space into four quadrants (NW, NE, SW, SE).
 * Each node can hold a limited number of points before subdividing.
 * 
 * Time Complexity:
 * - Insert: O(log n) average, O(n) worst case
 * - Query (range search): O(log n + k) where k is results
 * 
 * Space Complexity: O(n)
 * 
 * Use cases:
 * - Collision detection in games
 * - Image compression
 * - Spatial databases
 * - Nearest neighbor searches
 * - Rendering optimization (culling)
 */
public class QuadTree {
    
    /**
     * Represents a point in 2D space with optional data.
     */
    public static class Point {
        public final double x;
        public final double y;
        public final Object data;
        
        public Point(double x, double y) {
            this(x, y, null);
        }
        
        public Point(double x, double y, Object data) {
            this.x = x;
            this.y = y;
            this.data = data;
        }
        
        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", x, y);
        }
    }
    
    /**
     * Represents a rectangular boundary.
     */
    public static class Rectangle {
        public final double x;      // Center x
        public final double y;      // Center y
        public final double width;  // Half-width
        public final double height; // Half-height
        
        public Rectangle(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        /**
         * Checks if this rectangle contains the given point.
         */
        public boolean contains(Point point) {
            return (point.x >= x - width &&
                    point.x <= x + width &&
                    point.y >= y - height &&
                    point.y <= y + height);
        }
        
        /**
         * Checks if this rectangle intersects with another rectangle.
         */
        public boolean intersects(Rectangle other) {
            return !(other.x - other.width > x + width ||
                     other.x + other.width < x - width ||
                     other.y - other.height > y + height ||
                     other.y + other.height < y - height);
        }
        
        @Override
        public String toString() {
            return String.format("Rectangle[center: (%.2f, %.2f), width: %.2f, height: %.2f]",
                x, y, width * 2, height * 2);
        }
    }
    
    private static class Node {
        Rectangle boundary;
        int capacity;
        List<Point> points;
        
        // Children nodes (null until subdivision)
        Node northwest;
        Node northeast;
        Node southwest;
        Node southeast;
        
        boolean divided;
        
        Node(Rectangle boundary, int capacity) {
            this.boundary = boundary;
            this.capacity = capacity;
            this.points = new ArrayList<>();
            this.divided = false;
        }
        
        /**
         * Subdivides this node into four quadrants.
         */
        void subdivide() {
            double x = boundary.x;
            double y = boundary.y;
            double w = boundary.width / 2;
            double h = boundary.height / 2;
            
            Rectangle nw = new Rectangle(x - w, y - h, w, h);
            Rectangle ne = new Rectangle(x + w, y - h, w, h);
            Rectangle sw = new Rectangle(x - w, y + h, w, h);
            Rectangle se = new Rectangle(x + w, y + h, w, h);
            
            northwest = new Node(nw, capacity);
            northeast = new Node(ne, capacity);
            southwest = new Node(sw, capacity);
            southeast = new Node(se, capacity);
            
            divided = true;
        }
        
        /**
         * Inserts a point into this node or its children.
         */
        boolean insert(Point point) {
            // Point is not in this boundary
            if (!boundary.contains(point)) {
                return false;
            }
            
            // If we have capacity and haven't divided, add here
            if (points.size() < capacity && !divided) {
                points.add(point);
                return true;
            }
            
            // Need to subdivide if not already divided
            if (!divided) {
                subdivide();
                
                // Move existing points to children
                for (Point p : points) {
                    insertIntoChild(p);
                }
                points.clear();
            }
            
            // Insert into appropriate child
            return insertIntoChild(point);
        }
        
        private boolean insertIntoChild(Point point) {
            if (northwest.insert(point)) return true;
            if (northeast.insert(point)) return true;
            if (southwest.insert(point)) return true;
            if (southeast.insert(point)) return true;
            return false;
        }
        
        /**
         * Queries for all points within the given range.
         */
        void query(Rectangle range, List<Point> found) {
            // No intersection, nothing to find
            if (!boundary.intersects(range)) {
                return;
            }
            
            // Check points in this node
            for (Point p : points) {
                if (range.contains(p)) {
                    found.add(p);
                }
            }
            
            // Recursively check children
            if (divided) {
                northwest.query(range, found);
                northeast.query(range, found);
                southwest.query(range, found);
                southeast.query(range, found);
            }
        }
    }
    
    private final Node root;
    
    /**
     * Creates a QuadTree with the given boundary and capacity per node.
     * 
     * @param boundary the boundary of the entire space
     * @param capacity maximum points per node before subdivision
     */
    public QuadTree(Rectangle boundary, int capacity) {
        this.root = new Node(boundary, capacity);
    }
    
    /**
     * Inserts a point into the QuadTree.
     * 
     * @param point the point to insert
     * @return true if inserted successfully, false if outside boundary
     */
    public boolean insert(Point point) {
        return root.insert(point);
    }
    
    /**
     * Queries for all points within the given rectangular range.
     * 
     * @param range the search range
     * @return list of points within the range
     */
    public List<Point> query(Rectangle range) {
        List<Point> found = new ArrayList<>();
        root.query(range, found);
        return found;
    }
    
    /**
     * Demonstrates QuadTree operations.
     */
    public static void demo() {
        System.out.println("=== QuadTree Demo ===\n");
        
        // Create a QuadTree for a 200x200 space (centered at origin)
        Rectangle boundary = new Rectangle(0, 0, 100, 100);
        QuadTree quadTree = new QuadTree(boundary, 4); // Max 4 points per node
        
        System.out.println("Created QuadTree:");
        System.out.println("  Boundary: " + boundary);
        System.out.println("  Capacity per node: 4 points\n");
        
        // Insert some points
        System.out.println("Inserting points...");
        Point[] points = {
            new Point(10, 10, "A"),
            new Point(15, 12, "B"),
            new Point(-20, 30, "C"),
            new Point(-25, 32, "D"),
            new Point(50, -40, "E"),
            new Point(52, -38, "F"),
            new Point(-60, -70, "G"),
            new Point(70, 80, "H"),
            new Point(75, 82, "I"),
            new Point(20, 20, "J"),
            new Point(22, 21, "K"),
            new Point(18, 19, "L")
        };
        
        for (Point p : points) {
            quadTree.insert(p);
            System.out.printf("  Inserted %s at %s\n", p.data, p);
        }
        
        System.out.println("\nExplanation: QuadTree subdivides when a region has more than");
        System.out.println("4 points, creating four quadrants (NW, NE, SW, SE)\n");
        
        // Query for points in different ranges
        System.out.println("Range Queries:");
        
        // Query 1: Small range around (20, 20)
        Rectangle range1 = new Rectangle(20, 20, 10, 10);
        List<Point> result1 = quadTree.query(range1);
        System.out.println("  Query range around (20, 20) with size 20x20:");
        System.out.println("    Range: " + range1);
        System.out.print("    Found " + result1.size() + " points: ");
        for (Point p : result1) {
            System.out.print(p.data + " ");
        }
        System.out.println("\n    Explanation: Only returns points within the query range\n");
        
        // Query 2: Larger range in positive quadrant
        Rectangle range2 = new Rectangle(40, 40, 60, 60);
        List<Point> result2 = quadTree.query(range2);
        System.out.println("  Query range in positive quadrant (larger area):");
        System.out.println("    Range: " + range2);
        System.out.print("    Found " + result2.size() + " points: ");
        for (Point p : result2) {
            System.out.print(p.data + " ");
        }
        System.out.println("\n    Explanation: Finds all points in NE quadrant\n");
        
        // Query 3: Range that spans multiple quadrants
        Rectangle range3 = new Rectangle(0, 0, 30, 30);
        List<Point> result3 = quadTree.query(range3);
        System.out.println("  Query range centered at origin:");
        System.out.println("    Range: " + range3);
        System.out.print("    Found " + result3.size() + " points: ");
        for (Point p : result3) {
            System.out.print(p.data + " ");
        }
        System.out.println("\n    Explanation: Efficiently searches only relevant quadrants\n");
        
        // Demonstrate efficiency
        System.out.println("Efficiency Demonstration:");
        System.out.println("  Total points in QuadTree: " + points.length);
        System.out.println("  Small range query checked only relevant quadrants,");
        System.out.println("  not all points - this is O(log n + k) complexity\n");
        
        System.out.println("Use Cases:");
        System.out.println("  - Game collision detection (check nearby objects only)");
        System.out.println("  - Finding points of interest near a location");
        System.out.println("  - Rendering optimization (only render visible objects)");
        System.out.println("  - Spatial databases and GIS applications\n");
        
        // Compare with naive search
        System.out.println("Comparison with naive search:");
        System.out.println("  Naive: Check all " + points.length + " points for each query");
        System.out.println("  QuadTree: Only check points in relevant quadrants");
        System.out.println("  Speedup increases with more points (especially for small queries)\n");
    }
}
