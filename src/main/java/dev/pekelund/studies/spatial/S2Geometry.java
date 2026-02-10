package dev.pekelund.studies.spatial;

import com.google.common.geometry.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper and demonstration of Google's S2 Geometry Library.
 * 
 * S2 is a hierarchical geospatial indexing system developed by Google.
 * It maps the Earth's surface onto a sphere and projects it to a cube, then
 * hierarchically subdivides the cube faces into cells.
 * 
 * Key features:
 * - Hierarchical cell structure (levels 0-30)
 * - Uniform cell sizes at each level
 * - Better than geohash at avoiding edge discontinuities
 * - Used by Google Maps, Foursquare, and others
 * 
 * Use cases:
 * - Geographic data indexing
 * - Proximity searches
 * - Coverage and containment queries
 * - Ridesharing and delivery zone management
 */
public class S2Geometry {
    
    /**
     * Converts latitude/longitude to an S2 Cell ID.
     * 
     * @param latitude latitude in degrees
     * @param longitude longitude in degrees
     * @param level cell level (0-30), higher = more precise
     * @return S2 cell ID
     */
    public static S2CellId getCellId(double latitude, double longitude, int level) {
        // Create a lat/lng point
        S2LatLng latLng = S2LatLng.fromDegrees(latitude, longitude);
        
        // Convert to S2 cell
        S2CellId cellId = S2CellId.fromLatLng(latLng);
        
        // Get cell at specified level
        return cellId.parent(level);
    }
    
    /**
     * Gets the bounding box (lat/lng rectangle) for an S2 cell.
     * 
     * @param cellId the S2 cell ID
     * @return lat/lng rectangle
     */
    public static S2LatLngRect getCellBounds(S2CellId cellId) {
        S2Cell cell = new S2Cell(cellId);
        return cell.getRectBound();
    }
    
    /**
     * Calculates approximate distance between two S2 cells in kilometers.
     * 
     * @param cellId1 first cell ID
     * @param cellId2 second cell ID
     * @return distance in kilometers
     */
    public static double distance(S2CellId cellId1, S2CellId cellId2) {
        S2Cell cell1 = new S2Cell(cellId1);
        S2Cell cell2 = new S2Cell(cellId2);
        
        S2Point center1 = cell1.getCenter();
        S2Point center2 = cell2.getCenter();
        
        // Get angle between centers and convert to distance
        S1Angle angle = new S1Angle(center1, center2);
        
        // Earth's radius in kilometers
        final double EARTH_RADIUS_KM = 6371.0;
        return angle.radians() * EARTH_RADIUS_KM;
    }
    
    /**
     * Gets all neighboring cells at the same level.
     * 
     * @param cellId the cell to get neighbors for
     * @return list of neighbor cell IDs
     */
    public static List<S2CellId> getNeighbors(S2CellId cellId) {
        List<S2CellId> neighbors = new ArrayList<>();
        
        // S2 provides 4 edge neighbors and 4 corner neighbors
        S2CellId[] edgeNeighbors = new S2CellId[4];
        cellId.getEdgeNeighbors(edgeNeighbors);
        
        for (S2CellId neighbor : edgeNeighbors) {
            neighbors.add(neighbor);
        }
        
        return neighbors;
    }
    
    /**
     * Finds all S2 cells at a given level that cover a circular region.
     * 
     * @param centerLat center latitude
     * @param centerLon center longitude
     * @param radiusKm radius in kilometers
     * @param level cell level
     * @return list of cell IDs covering the region
     */
    public static List<S2CellId> getCellsCovering(double centerLat, double centerLon, 
                                                   double radiusKm, int level) {
        // Create a point for the center
        S2LatLng center = S2LatLng.fromDegrees(centerLat, centerLon);
        
        // Convert radius to angle (radians)
        final double EARTH_RADIUS_KM = 6371.0;
        double radiusRadians = radiusKm / EARTH_RADIUS_KM;
        S1Angle radius = S1Angle.radians(radiusRadians);
        
        // Create a cap (circular region on sphere)
        S2Cap cap = S2Cap.fromAxisAngle(center.toPoint(), radius);
        
        // Get covering cells using builder pattern
        S2RegionCoverer coverer = S2RegionCoverer.builder()
                .setMinLevel(level)
                .setMaxLevel(level)
                .setMaxCells(100)
                .build();
        
        ArrayList<S2CellId> covering = new ArrayList<>();
        coverer.getCovering(cap, covering);
        
        return covering;
    }
    
    /**
     * Demonstrates S2 geometry operations.
     */
    public static void demo() {
        System.out.println("=== Google S2 Geometry Demo ===\n");
        
        // Example locations
        double nycLat = 40.7128;
        double nycLon = -74.0060;
        
        double sfLat = 37.7749;
        double sfLon = -122.4194;
        
        System.out.println("Working with two locations:");
        System.out.printf("  New York: (%.4f, %.4f)\n", nycLat, nycLon);
        System.out.printf("  San Francisco: (%.4f, %.4f)\n\n", sfLat, sfLon);
        
        // Get S2 cells at different levels
        System.out.println("S2 Cell IDs at different levels for New York:");
        for (int level : new int[]{5, 10, 15, 20}) {
            S2CellId cellId = getCellId(nycLat, nycLon, level);
            System.out.printf("  Level %2d: %s\n", level, cellId.toToken());
        }
        System.out.println("Explanation: Higher level = smaller cells = more precision\n");
        
        // Get cells for both locations at level 10
        int level = 10;
        S2CellId nycCell = getCellId(nycLat, nycLon, level);
        S2CellId sfCell = getCellId(sfLat, sfLon, level);
        
        System.out.println("Cell IDs at level " + level + ":");
        System.out.println("  NYC: " + nycCell.toToken());
        System.out.println("  SF:  " + sfCell.toToken());
        System.out.println("  Different tokens indicate distant locations\n");
        
        // Show cell bounds
        System.out.println("Cell bounds for NYC (level " + level + "):");
        S2LatLngRect bounds = getCellBounds(nycCell);
        S2LatLng lo = bounds.lo();
        S2LatLng hi = bounds.hi();
        System.out.printf("  SW corner: (%.6f, %.6f)\n", lo.latDegrees(), lo.lngDegrees());
        System.out.printf("  NE corner: (%.6f, %.6f)\n", hi.latDegrees(), hi.lngDegrees());
        System.out.println("  Explanation: S2 cell represents a region on Earth's surface\n");
        
        // Calculate distance between cells
        double dist = distance(nycCell, sfCell);
        System.out.printf("Distance between NYC and SF cells: %.0f km\n", dist);
        System.out.println("Explanation: S2 uses spherical geometry for accurate distances\n");
        
        // Get neighbors
        System.out.println("Edge neighbors of NYC cell:");
        List<S2CellId> neighbors = getNeighbors(nycCell);
        for (int i = 0; i < neighbors.size(); i++) {
            System.out.printf("  Neighbor %d: %s\n", i + 1, neighbors.get(i).toToken());
        }
        System.out.println("Explanation: Useful for proximity searches\n");
        
        // Cover a circular region
        System.out.println("Covering a 10km radius around NYC with level 13 cells:");
        List<S2CellId> covering = getCellsCovering(nycLat, nycLon, 10.0, 13);
        System.out.println("  Cells needed: " + covering.size());
        System.out.println("  First few cells:");
        for (int i = 0; i < Math.min(5, covering.size()); i++) {
            System.out.println("    " + covering.get(i).toToken());
        }
        System.out.println("  Explanation: S2 can efficiently cover regions with minimal cells\n");
        
        // Cell hierarchy
        System.out.println("Cell hierarchy (parent/child relationships):");
        S2CellId cell = getCellId(nycLat, nycLon, 15);
        System.out.println("  Level 15 cell: " + cell.toToken());
        System.out.println("  Parent (level 14): " + cell.parent(14).toToken());
        System.out.println("  Parent (level 13): " + cell.parent(13).toToken());
        System.out.println("  Explanation: Cells form a hierarchy like a tree\n");
        
        // Cell levels and approximate sizes
        System.out.println("Approximate S2 cell sizes by level:");
        System.out.println("  Level  0: ~85,000 km (entire face of cube)");
        System.out.println("  Level  5: ~1,300 km");
        System.out.println("  Level 10: ~20 km");
        System.out.println("  Level 15: ~300 m");
        System.out.println("  Level 20: ~5 m");
        System.out.println("  Level 25: ~8 cm");
        System.out.println("  Level 30: ~1 mm\n");
        
        System.out.println("Key advantages over Geohash:");
        System.out.println("  - More uniform cell sizes across the globe");
        System.out.println("  - Better handling of poles");
        System.out.println("  - Hierarchical structure (parent/child relationships)");
        System.out.println("  - Efficient region covering algorithms\n");
        
        System.out.println("Common use cases:");
        System.out.println("  - Ridesharing zone management (Uber, Lyft)");
        System.out.println("  - Location-based game mechanics (Pokémon Go)");
        System.out.println("  - Geographic data indexing at scale");
        System.out.println("  - Efficient proximity and containment queries\n");
    }
}
