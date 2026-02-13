import java.util.ArrayList;
import java.util.List;

public class RecordImmutabilityDemo {
    public static void main(String[] args) {
        System.out.println("=== Java Records: Shallow vs Deep Immutability ===");

        List<String> shallowRoles = new ArrayList<>(List.of("reader", "writer"));
        ShallowProfile shallowProfile = new ShallowProfile("Alice", shallowRoles);

        System.out.println("\nShallow immutable record (mutable component reference):");
        System.out.println("Initial roles: " + shallowProfile.roles());
        shallowRoles.add("admin");
        System.out.println("After mutating source list: " + shallowProfile.roles());
        shallowProfile.roles().add("owner");
        System.out.println("After mutating via accessor: " + shallowProfile.roles());

        List<String> deepRoles = new ArrayList<>(List.of("reader", "writer"));
        DeepProfile deepProfile = new DeepProfile("Bob", deepRoles);

        System.out.println("\nDeep immutable record (defensive copy + unmodifiable list):");
        System.out.println("Initial roles: " + deepProfile.roles());
        deepRoles.add("admin");
        System.out.println("After mutating source list: " + deepProfile.roles());

        try {
            deepProfile.roles().add("owner");
        } catch (UnsupportedOperationException ex) {
            System.out.println("Mutating via accessor fails: " + ex.getClass().getSimpleName());
        }

        System.out.println("Final deep immutable roles: " + deepProfile.roles());
    }
}

record ShallowProfile(String name, List<String> roles) {
}

record DeepProfile(String name, List<String> roles) {
    DeepProfile {
        roles = List.copyOf(roles);
    }
}
