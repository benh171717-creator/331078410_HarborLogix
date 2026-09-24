package harborlogix.clients;

/**
 * DESIGN DECISION: kept concrete (see DESIGN.md section 1).
 * A "Standard"/walk-in client has a fully sensible default behaviour
 * (0% discount, no priority) - unlike CargoUnit, there is no missing
 * default here, so there is nothing forcing this class to be abstract.
 */
public class Client {

    private final String clientId;
    private final String name;

    public Client(String clientId, String name) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client ID cannot be empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Client name cannot be empty");
        }
        this.clientId = clientId;
        this.name = name;
    }

    public String getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }

    /** Walk-in clients get no discount. Subclasses may override. */
    public double discountPercent() {
        return 0.0;
    }

    public String clientTier() {
        return "Standard";
    }

    /** Priority clients are unloaded first. */
    public boolean priorityHandling() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, name=%s, discount=%.1f%%]",
                clientTier(), clientId, name, discountPercent());
    }
}
