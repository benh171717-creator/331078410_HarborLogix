package harborlogix.ops;

/**
 * TariffPolicy - your personal tariff table.
 *
 * PROVIDED FILE - fill in the TODO, change nothing else.
 *
 * Every student works with a DIFFERENT tariff table, derived from the last
 * two digits of their student ID. AcceptanceTest verifies that these constants
 * really do match your declared ID, so copying another student's numbers is
 * detected automatically.
 *
 *   D1 = the LAST digit of your student ID
 *   D2 = the SECOND-TO-LAST digit of your student ID
 *
 * Example: student ID 20416  ->  D1 = 6, D2 = 1
 */
public final class TariffPolicy {

    /** TODO: your student ID as a String, digits only. */
    public static final String STUDENT_ID = "331078410";

    public static final int D1 = Character.getNumericValue(STUDENT_ID.charAt(STUDENT_ID.length() - 1));
    public static final int D2 = Character.getNumericValue(STUDENT_ID.charAt(STUDENT_ID.length() - 2));

    /** Storage rate per cubic metre per day.        8 + D1 */
    public static final double BASE_STORAGE_RATE = 8.0 + D1;

    /** Reefer power surcharge per kW per day.       1.5 + (D2 * 0.1) */
    public static final double POWER_RATE = 1.5 + (D2 * 0.1);

    /** Hazmat multiplier applied to the base fee.   1.5 + (D1 * 0.05) */
    public static final double HAZMAT_MULTIPLIER = 1.5 + (D1 * 0.05);

    /** Liquid rate per litre of ACTUAL content.     0.02 + (D2 * 0.002) */
    public static final double LIQUID_RATE = 0.02 + (D2 * 0.002);

    /** Flat daily fee for oversized cargo (Part D). 100 + (D1 * 10) */
    public static final double OVERSIZE_DAILY_FLAT = 100.0 + (D1 * 10);

    /** Maximum number of units one yard may hold.   20 + D1 */
    public static final int YARD_CAPACITY = 20 + D1;

    private TariffPolicy() {
        throw new UnsupportedOperationException("TariffPolicy is a constants holder");
    }
}
