package harborlogix.cargo;

import harborlogix.clients.Client;

/**
 * CargoUnit - the root of the cargo hierarchy.
 *
 * PROVIDED FILE - read it carefully, do NOT modify it.
 * It is the standard your own classes are expected to meet.
 *
 * WHY THIS CLASS IS abstract
 * --------------------------
 * There is no such thing as "a generic cargo unit" standing in the yard.
 * Every real unit is a container, a tank, or an oversized load. Declaring
 * CargoUnit abstract makes that fact enforceable: `new CargoUnit(...)` will
 * not compile, so no meaningless object can ever exist.
 *
 * THE THREE abstract METHODS
 * --------------------------
 * There is no sensible default answer for any of them - a tank and a reefer
 * genuinely charge differently. Rather than invent a fake default that
 * subclasses must remember to override, we declare them abstract and let the
 * compiler force every subclass to answer.
 *
 * WHY totalStorageCharge() IS final
 * ---------------------------------
 * The RULE (charge = daily fee x days) is fixed terminal policy and must be
 * identical for every cargo type. Only the daily FEE varies. Marking the
 * method final locks the rule while leaving the variable part open.
 * This pattern has a name - you will be asked about it in your design document.
 */
public abstract class CargoUnit {

    private static int unitsCreated = 0;

    private final String unitId;
    private final Client owner;
    private final double weightKg;
    private int daysStored;

    protected CargoUnit(String unitId, Client owner, double weightKg, int daysStored) {

        if (unitId == null || unitId.isBlank()) {
            throw new IllegalArgumentException("Unit ID cannot be empty");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Cargo unit must have an owner");
        }
        if (weightKg <= 0) {
            throw new IllegalArgumentException("Weight must be positive, got " + weightKg);
        }
        if (daysStored < 0) {
            throw new IllegalArgumentException("Days stored cannot be negative");
        }

        this.unitId = unitId;
        this.owner = owner;
        this.weightKg = weightKg;
        this.daysStored = daysStored;

        unitsCreated++;
    }

    public String getUnitId()   { return unitId; }
    public Client getOwner()    { return owner; }
    public double getWeightKg() { return weightKg; }
    public int getDaysStored()  { return daysStored; }

    public static int getUnitsCreated() { return unitsCreated; }

    public void addStorageDays(int extraDays) {
        if (extraDays <= 0) {
            throw new IllegalArgumentException("Extra days must be positive");
        }
        daysStored += extraDays;
    }

    // ---------- the contract every subclass must fulfil ----------

    /** Fee charged for ONE day of storage. Each cargo type computes this differently. */
    public abstract double dailyStorageFee();

    /** Short label used on the yard manifest, e.g. "Reefer". */
    public abstract String handlingCategory();

    /** One-line safety instruction shown to the crane operator. */
    public abstract String safetyBriefing();

    // ---------- fixed terminal policy ----------

    /** Terminal-wide billing rule. Deliberately not overridable. */
    public final double totalStorageCharge() {
        return dailyStorageFee() * daysStored;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, owner=%s, %.1f kg, %d days]",
                handlingCategory(), unitId, owner.getName(), weightKg, daysStored);
    }
}
