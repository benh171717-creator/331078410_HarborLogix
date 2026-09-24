package harborlogix.cargo;

import harborlogix.clients.Client;
import harborlogix.ops.TariffPolicy;

/**
 * A sibling of the reefer - also a child of StandardContainer.
 *
 * Extra state : hazardClass (1-9 only), requiresEscort (boolean)
 * Daily fee   : the parent's fee MULTIPLIED by HAZMAT_MULTIPLIER
 * Category    : "Hazmat"
 */
public class HazmatContainer extends StandardContainer implements Inspectable {

    private final int hazardClass;
    private final boolean requiresEscort;

    public HazmatContainer(String unitId, Client owner, double weightKg,
                           int daysStored, double volumeM3,
                           int hazardClass, boolean requiresEscort) {
        super(unitId, owner, weightKg, daysStored, volumeM3);
        if (hazardClass < 1 || hazardClass > 9) {
            throw new IllegalArgumentException("Hazard class must be 1-9, got " + hazardClass);
        }
        this.hazardClass = hazardClass;
        this.requiresEscort = requiresEscort;
    }

    public int getHazardClass() {
        return hazardClass;
    }

    public boolean isRequiresEscort() {
        return requiresEscort;
    }

    @Override
    public double dailyStorageFee() {
        return super.dailyStorageFee() * TariffPolicy.HAZMAT_MULTIPLIER;
    }

    @Override
    public String handlingCategory() {
        return "Hazmat";
    }

    @Override
    public String safetyBriefing() {
        return "Hazard class " + hazardClass + " - escort "
                + (requiresEscort ? "required" : "not required") + ".";
    }

    @Override
    public String toString() {
        return super.toString() + String.format("[hazardClass=%d, escort=%b]", hazardClass, requiresEscort);
    }

    @Override
    public String inspectionNote() {
        return "Hazard class " + hazardClass + " - "
                + (requiresEscort ? "escort on file, verify before movement." : "no escort required.");
    }
}
