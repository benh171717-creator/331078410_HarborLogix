package harborlogix.cargo;

import harborlogix.clients.Client;
import harborlogix.ops.TariffPolicy;

/**
 * Level 3 of the hierarchy.
 *
 * Extra state : targetTempC (must be 8.0 or below), powerDrawKw (positive)
 * Daily fee   : the parent's fee PLUS (POWER_RATE * powerDrawKw)
 * Category    : "Reefer"
 */
public class RefrigeratedContainer extends StandardContainer implements Inspectable {

    private final double targetTempC;
    private final double powerDrawKw;

    public RefrigeratedContainer(String unitId, Client owner, double weightKg,
                                 int daysStored, double volumeM3,
                                 double targetTempC, double powerDrawKw) {
        super(unitId, owner, weightKg, daysStored, volumeM3);
        if (targetTempC > 8.0) {
            throw new IllegalArgumentException("Target temperature must be 8.0C or below, got " + targetTempC);
        }
        if (powerDrawKw <= 0) {
            throw new IllegalArgumentException("Power draw must be positive, got " + powerDrawKw);
        }
        this.targetTempC = targetTempC;
        this.powerDrawKw = powerDrawKw;
    }

    public double getTargetTempC() {
        return targetTempC;
    }

    public double getPowerDrawKw() {
        return powerDrawKw;
    }

    @Override
    public double dailyStorageFee() {
        return super.dailyStorageFee() + TariffPolicy.POWER_RATE * powerDrawKw;
    }

    @Override
    public String handlingCategory() {
        return "Reefer";
    }

    @Override
    public String safetyBriefing() {
        return String.format("Reefer - maintain %.1fC, power draw %.1f kW.", targetTempC, powerDrawKw);
    }

    @Override
    public String toString() {
        return super.toString() + String.format("[temp=%.1f, power=%.1f kW]", targetTempC, powerDrawKw);
    }

    @Override
    public String inspectionNote() {
        return String.format("Target %.1fC, drawing %.1f kW - verify door seal integrity.", targetTempC, powerDrawKw);
    }
}
