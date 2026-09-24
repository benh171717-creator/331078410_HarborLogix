package harborlogix.cargo;

import harborlogix.clients.Client;
import harborlogix.ops.TariffPolicy;

/**
 * Part D extension. Yourself directly under CargoUnit - the port added
 * this service without anyone touching Yard.java or CargoUnit.java.
 *
 * Extra state : lengthM (must exceed 12.0 m), needsHeavyCrane
 * Daily fee   : OVERSIZE_DAILY_FLAT + (lengthM * 5.0)
 * Category    : "Oversized"
 */
public class OversizedCargo extends CargoUnit {

    private final double lengthM;
    private final boolean needsHeavyCrane;

    public OversizedCargo(String unitId, Client owner, double weightKg,
                          int daysStored, double lengthM, boolean needsHeavyCrane) {
        super(unitId, owner, weightKg, daysStored);
        if (lengthM <= 12.0) {
            throw new IllegalArgumentException("Length must exceed 12.0 m, got " + lengthM);
        }
        this.lengthM = lengthM;
        this.needsHeavyCrane = needsHeavyCrane;
    }

    public double getLengthM() {
        return lengthM;
    }

    public boolean isNeedsHeavyCrane() {
        return needsHeavyCrane;
    }

    @Override
    public double dailyStorageFee() {
        return TariffPolicy.OVERSIZE_DAILY_FLAT + (lengthM * 5.0);
    }

    @Override
    public String handlingCategory() {
        return "Oversized";
    }

    @Override
    public String safetyBriefing() {
        return String.format("Oversized cargo - %.1f m long%s.",
                lengthM, needsHeavyCrane ? ", requires heavy crane" : "");
    }

    @Override
    public String toString() {
        return super.toString() + String.format("[length=%.1f m, heavyCrane=%b]", lengthM, needsHeavyCrane);
    }
}
