package dev.pekelund.studies.spatial;

/**
 * Geohash implementation for encoding and decoding geographic coordinates.
 * 
 * Geohash is a geocoding system that encodes latitude/longitude into a short string
 * of letters and digits. It has the property that nearby locations have similar prefixes.
 * 
 * How it works:
 * - Repeatedly divides the world into smaller rectangles
 * - Each division adds one character to the geohash
 * - Uses base-32 encoding (0-9, a-z excluding a,i,l,o)
 * 
 * Properties:
 * - Longer geohash = more precise location
 * - Similar prefixes = nearby locations (not always, due to edge cases)
 * - Each character adds ~5 bits of precision
 * 
 * Use cases:
 * - Database indexing for location-based queries
 * - Proximity searches
 * - Caching location data
 * - URL shortening for coordinates
 */
public class Geohash {
    
    // Base32 encoding used in Geohash (excludes a,i,l,o to avoid confusion)
    private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";
    
    // Coordinate range representing a geohash
    public static class BoundingBox {
        public final double minLat;
        public final double maxLat;
        public final double minLon;
        public final double maxLon;
        
        public BoundingBox(double minLat, double maxLat, double minLon, double maxLon) {
            this.minLat = minLat;
            this.maxLat = maxLat;
            this.minLon = minLon;
            this.maxLon = maxLon;
        }
        
        public double getCenterLat() {
            return (minLat + maxLat) / 2;
        }
        
        public double getCenterLon() {
            return (minLon + maxLon) / 2;
        }
        
        @Override
        public String toString() {
            return String.format("BoundingBox[lat: %.6f to %.6f, lon: %.6f to %.6f]",
                minLat, maxLat, minLon, maxLon);
        }
    }
    
    /**
     * Encodes a latitude/longitude coordinate into a geohash string.
     * 
     * @param latitude latitude in degrees (-90 to 90)
     * @param longitude longitude in degrees (-180 to 180)
     * @param precision number of characters in the resulting geohash
     * @return geohash string
     */
    public static String encode(double latitude, double longitude, int precision) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        if (precision < 1 || precision > 12) {
            throw new IllegalArgumentException("Precision must be between 1 and 12");
        }
        
        StringBuilder geohash = new StringBuilder();
        
        double minLat = -90.0, maxLat = 90.0;
        double minLon = -180.0, maxLon = 180.0;
        
        int bit = 0;
        int ch = 0;
        boolean isEven = true; // Start with longitude
        
        while (geohash.length() < precision) {
            double mid;
            
            if (isEven) {
                // Divide longitude
                mid = (minLon + maxLon) / 2;
                if (longitude >= mid) {
                    ch |= (1 << (4 - bit));
                    minLon = mid;
                } else {
                    maxLon = mid;
                }
            } else {
                // Divide latitude
                mid = (minLat + maxLat) / 2;
                if (latitude >= mid) {
                    ch |= (1 << (4 - bit));
                    minLat = mid;
                } else {
                    maxLat = mid;
                }
            }
            
            isEven = !isEven;
            
            if (bit < 4) {
                bit++;
            } else {
                // 5 bits accumulated, add character
                geohash.append(BASE32.charAt(ch));
                bit = 0;
                ch = 0;
            }
        }
        
        return geohash.toString();
    }
    
    /**
     * Decodes a geohash string into a bounding box.
     * 
     * @param geohash the geohash string to decode
     * @return bounding box representing the area encoded by the geohash
     */
    public static BoundingBox decode(String geohash) {
        if (geohash == null || geohash.isEmpty()) {
            throw new IllegalArgumentException("Geohash cannot be null or empty");
        }
        
        double minLat = -90.0, maxLat = 90.0;
        double minLon = -180.0, maxLon = 180.0;
        
        boolean isEven = true;
        
        for (char c : geohash.toLowerCase().toCharArray()) {
            int idx = BASE32.indexOf(c);
            if (idx == -1) {
                throw new IllegalArgumentException("Invalid geohash character: " + c);
            }
            
            // Extract 5 bits from the character
            for (int bit = 4; bit >= 0; bit--) {
                double mid;
                
                if (isEven) {
                    // Longitude bit
                    mid = (minLon + maxLon) / 2;
                    if ((idx & (1 << bit)) != 0) {
                        minLon = mid;
                    } else {
                        maxLon = mid;
                    }
                } else {
                    // Latitude bit
                    mid = (minLat + maxLat) / 2;
                    if ((idx & (1 << bit)) != 0) {
                        minLat = mid;
                    } else {
                        maxLat = mid;
                    }
                }
                
                isEven = !isEven;
            }
        }
        
        return new BoundingBox(minLat, maxLat, minLon, maxLon);
    }
    
    /**
     * Calculates approximate distance in kilometers between two geohashes.
     * Uses the Haversine formula for accuracy.
     */
    public static double distance(String geohash1, String geohash2) {
        BoundingBox box1 = decode(geohash1);
        BoundingBox box2 = decode(geohash2);
        
        return haversineDistance(
            box1.getCenterLat(), box1.getCenterLon(),
            box2.getCenterLat(), box2.getCenterLon()
        );
    }
    
    /**
     * Haversine formula for calculating distance between two points on Earth.
     */
    private static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Earth's radius in kilometers
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * Demonstrates Geohash encoding and decoding.
     */
    public static void demo() {
        System.out.println("=== Geohash Demo ===\n");
        
        // Example locations
        double lat1 = 40.7128;  // New York
        double lon1 = -74.0060;
        
        double lat2 = 40.7589;  // Times Square (nearby)
        double lon2 = -73.9851;
        
        double lat3 = 51.5074;  // London (far away)
        double lon3 = -0.1278;
        
        System.out.println("Encoding coordinates to geohashes:");
        
        // Encode with different precisions
        for (int precision = 3; precision <= 9; precision++) {
            String geohash = encode(lat1, lon1, precision);
            System.out.printf("  Precision %d: %s\n", precision, geohash);
        }
        
        System.out.println("\nExplanation: Longer geohash = more precise location");
        System.out.println("Each character adds ~5 bits of precision\n");
        
        // Encode example locations
        String ny = encode(lat1, lon1, 7);
        String ts = encode(lat2, lon2, 7);
        String london = encode(lat3, lon3, 7);
        
        System.out.println("Location geohashes (precision 7):");
        System.out.printf("  New York: %s (%.4f, %.4f)\n", ny, lat1, lon1);
        System.out.printf("  Times Square: %s (%.4f, %.4f)\n", ts, lat2, lon2);
        System.out.printf("  London: %s (%.4f, %.4f)\n\n", london, lat3, lon3);
        
        System.out.println("Prefix similarity analysis:");
        System.out.println("  NY and Times Square share prefix: " + 
            ny.substring(0, Math.min(5, ny.length())) + 
            " (nearby locations)");
        System.out.println("  NY and London different prefixes: different continents\n");
        
        // Decode geohashes
        System.out.println("Decoding geohash back to coordinates:");
        BoundingBox bbox = decode(ny);
        System.out.println("  Geohash: " + ny);
        System.out.println("  " + bbox);
        System.out.printf("  Center: (%.4f, %.4f)\n", bbox.getCenterLat(), bbox.getCenterLon());
        System.out.println("  Explanation: Geohash represents a bounding box, not exact point\n");
        
        // Calculate distances
        System.out.println("Distance calculations:");
        double distNyTs = distance(ny, ts);
        double distNyLondon = distance(ny, london);
        System.out.printf("  NY to Times Square: %.2f km (nearby)\n", distNyTs);
        System.out.printf("  NY to London: %.2f km (far)\n\n", distNyLondon);
        
        // Demonstrate precision levels
        System.out.println("Geohash precision levels:");
        System.out.println("  1 char: ±2,500 km");
        System.out.println("  3 chars: ±78 km");
        System.out.println("  5 chars: ±2.4 km");
        System.out.println("  7 chars: ±76 m");
        System.out.println("  9 chars: ±2.4 m");
        System.out.println("\nUse cases:");
        System.out.println("- Fast proximity searches in databases");
        System.out.println("- Caching location data with similar prefixes");
        System.out.println("- URL-friendly coordinate encoding\n");
    }
}
