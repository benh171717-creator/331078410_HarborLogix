package harborlogix.cargo;

import harborlogix.clients.Client;
import harborlogix.ops.TariffPolicy;

/**
 * A DIRECT child of CargoUnit - a separate branch from the container family.
 *
 * Extra state : capacityLitres (positive), fillPercent (0-100, mutable)
 * currentLitres() : capacityLitres * fillPercent / 100
 * Daily fee   : LIQUID_RATE * currentLitres()   (actual content, not capacity)
 * Category    : "Tank"
 */
public class LiquidTank extends CargoUnit {

    private final double capacityLitres;
    private double fillPercent;

    public LiquidTank(String unitId, Client owner, double weightKg,
                      int daysStored, double capacityLitres, double fillPercent) {
        super(unitId, owner, weightKg, daysStored);
        if (capacityLitres <= 0) {
            throw new IllegalArgumentException("Capacity must be positive, got " + capacityLitres);
        }
        if (fillPercent < 0 || fillPercent > 100) {
            throw new IllegalArgumentException("Fill percent must be 0-100, got " + fillPercent);
        }
        this.capacityLitres = capacityLitres;
        this.fillPercent = fillPercent;
    }

    public double getCapacityLitres() {
        return capacityLitres;
    }

    public double getFillPercent() {
        return fillPercent;
    }

    public double currentLitres() {
        return capacityLitres * fillPercent / 100.0;
    }

    /**
     * Pumps out up to `litres` and returns how much was actually moved
     * (never more than is present). Updates fillPercent accordingly.
     */
    public double transferOut(double litres) {
        if (litres <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive, got " + litres);
        }
        double available = currentLitres();
        double moved = Math.min(litres, available);
        fillPercent = (available - moved) / capacityLitres * 100.0;
        return moved;
    }

    @Override
    public double dailyStorageFee() {
        return TariffPolicy.LIQUID_RATE * currentLitres();
    }

    @Override
    public String handlingCategory() {
        return "Tank";
    }

    @Override
    public String safetyBriefing() {
        return String.format("Liquid tank - currently %.1f%% full (%.1f L).", fillPercent, currentLitres());
    }

    @Override
    public String toString() {
        return super.toString() + String.format("[capacity=%.1f L, fill=%.1f%%]", capacityLitres, fillPercent);
    }
}
