package harborlogix.ops;

import harborlogix.cargo.CargoUnit;
import harborlogix.cargo.Inspectable;
import harborlogix.clients.Client;
import java.util.ArrayList;

/**
 * The most important file in the assignment.
 *
 * Yard knows there is *a* cargo unit and *a* client. It knows which
 * questions to ask them (the abstract/public methods on CargoUnit and
 * Client). It must not know, and does not know, what concrete kind they are.
 */
public class Yard {

    private final String yardName;
    private final int capacity;
    private final ArrayList<CargoUnit> units = new ArrayList<>();

    public Yard(String yardName, int capacity) {
        if (yardName == null || yardName.isBlank()) {
            throw new IllegalArgumentException("Yard name cannot be empty");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive, got " + capacity);
        }
        this.yardName = yardName;
        this.capacity = capacity;
    }

    public String getYardName() {
        return yardName;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getUnitCount() {
        return units.size();
    }

    /**
     * Returns a COPY of the stored units, not the internal list itself.
     * A caller mutating the returned list must not be able to add or
     * remove units behind receive()'s back and bypass its validation.
     */
    public ArrayList<CargoUnit> getUnits() {
        return new ArrayList<>(units);
    }

    public void receive(CargoUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Cannot receive a null unit");
        }
        for (CargoUnit existing : units) {
            if (existing.getUnitId().equals(unit.getUnitId())) {
                throw new IllegalArgumentException("Duplicate unit ID: " + unit.getUnitId());
            }
        }
        if (units.size() >= capacity) {
            throw new IllegalStateException("Yard is at full capacity (" + capacity + ")");
        }
        units.add(unit);
    }

    /** Sum of every unit's daily fee. */
    public double totalDailyRevenue() {
        double total = 0;
        for (CargoUnit unit : units) {
            total += unit.dailyStorageFee();
        }
        return total;
    }

    /**
     * Total owed by one client: sum of totalStorageCharge() over that
     * client's units, reduced by that client's own discount percentage.
     */
    public double invoiceFor(Client client) {
        double total = 0;
        for (CargoUnit unit : units) {
            if (unit.getOwner().getClientId().equals(client.getClientId())) {
                total += unit.totalStorageCharge();
            }
        }
        return total * (1 - client.discountPercent() / 100.0);
    }

    /** The heaviest unit in the yard, or null if the yard is empty. */
    public CargoUnit heaviestUnit() {
        CargoUnit heaviest = null;
        for (CargoUnit unit : units) {
            if (heaviest == null || unit.getWeightKg() > heaviest.getWeightKg()) {
                heaviest = unit;
            }
        }
        return heaviest;
    }

    /**
     * BONUS. Prints an inspection note for each unit the caller identifies as
     * Inspectable. Yard itself never asks "is this unit Inspectable?" - the
     * caller (which is allowed to know about concrete types) hands over only
     * the units that qualify, so Yard stays as ignorant of concrete cargo
     * types as everywhere else in this file.
     */
    public void printInspectionNotes(ArrayList<Inspectable> inspectables) {
        System.out.println("--- Inspection notes ---");
        for (Inspectable inspectable : inspectables) {
            System.out.println(inspectable.inspectionNote());
        }
    }

    /** Prints one line per unit plus its safety briefing, then the daily revenue. */
    public void printManifest() {
        System.out.println("--- Manifest: " + yardName + " ---");
        for (CargoUnit unit : units) {
            System.out.println(unit + " | " + unit.safetyBriefing());
        }
        System.out.printf("Total daily revenue: %.2f%n", totalDailyRevenue());
    }
}
