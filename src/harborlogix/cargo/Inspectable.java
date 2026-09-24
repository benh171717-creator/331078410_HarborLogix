package harborlogix.cargo;

/**
 * BONUS. A cross-cutting capability, not a step in the CargoUnit hierarchy:
 * only some cargo types have anything meaningful to report on inspection,
 * so this lives as an interface rather than a method on CargoUnit itself.
 * See DESIGN.md, bonus section, for the justification.
 */
public interface Inspectable {
    String inspectionNote();
}
