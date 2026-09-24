package harborlogix.cargo;

import harborlogix.clients.Client;
import harborlogix.ops.TariffPolicy;

/**
 * StandardContainer IS-A CargoUnit.
 *
 * Extra state : volumeM3 (must be positive)
 * Daily fee   : BASE_STORAGE_RATE * volumeM3
 * Category    : "Standard"
 */
public class StandardContainer extends CargoUnit {

    private final double volumeM3;

    public StandardContainer(String unitId, Client owner, double weightKg,
                             int daysStored, double volumeM3) {
        super(unitId, owner, weightKg, daysStored);
        if (volumeM3 <= 0) {
            throw new IllegalArgumentException("Volume must be positive, got " + volumeM3);
        }
        this.volumeM3 = volumeM3;
    }

    public double getVolumeM3() {
        return volumeM3;
    }

    @Override
    public double dailyStorageFee() {
        return TariffPolicy.BASE_STORAGE_RATE * volumeM3;
    }

    @Override
    public String handlingCategory() {
        return "Standard";
    }

    @Override
    public String safetyBriefing() {
        return "Standard container - no special handling required.";
    }

    @Override
    public String toString() {
        return super.toString() + String.format("[volume=%.1f m3]", volumeM3);
    }
}
