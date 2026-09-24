package harborlogix.tests;

import harborlogix.cargo.*;
import harborlogix.clients.*;
import harborlogix.ops.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * PROVIDED FILE - DO NOT MODIFY. Any change to this file is an integrity breach.
 *
 * Run:  java harborlogix.tests.AcceptanceTest
 *
 * This test does NOT prove your assignment is correct. It proves your code
 * conforms to the specification well enough to be marked. You can pass every
 * check here and still lose most of the marks for poor design, so do not
 * treat a green run as "finished".
 */
public class AcceptanceTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println("HarborLogix acceptance test");
        System.out.println("Student ID declared: " + TariffPolicy.STUDENT_ID);
        System.out.println("--------------------------------------------------");

        checkTariffDerivation();
        checkStructure();
        checkFees();
        checkPolymorphism();
        checkClients();
        checkYard();
        checkValidation();

        System.out.println("--------------------------------------------------");
        System.out.println("passed: " + passed + "   failed: " + failed);
        if (failed > 0) {
            System.out.println("RESULT: FAIL - your submission is not yet conformant.");
        } else {
            System.out.println("RESULT: PASS - conformant. Design marks are assessed separately.");
        }
    }

    // ---------- 1. your constants must match your declared student ID ----------
    private static void checkTariffDerivation() {
        String id = TariffPolicy.STUDENT_ID;
        check("student ID is digits only, at least 2 digits",
                id != null && id.length() >= 2 && id.matches("\\d+"));

        int d1 = Character.getNumericValue(id.charAt(id.length() - 1));
        int d2 = Character.getNumericValue(id.charAt(id.length() - 2));

        eq("BASE_STORAGE_RATE derived from D1", 8.0 + d1, TariffPolicy.BASE_STORAGE_RATE);
        eq("POWER_RATE derived from D2", 1.5 + d2 * 0.1, TariffPolicy.POWER_RATE);
        eq("HAZMAT_MULTIPLIER derived from D1", 1.5 + d1 * 0.05, TariffPolicy.HAZMAT_MULTIPLIER);
        eq("LIQUID_RATE derived from D2", 0.02 + d2 * 0.002, TariffPolicy.LIQUID_RATE);
        eq("OVERSIZE_DAILY_FLAT derived from D1", 100.0 + d1 * 10, TariffPolicy.OVERSIZE_DAILY_FLAT);
        check("YARD_CAPACITY derived from D1", TariffPolicy.YARD_CAPACITY == 20 + d1);
    }

    // ---------- 2. structural requirements ----------
    private static void checkStructure() {
        check("CargoUnit is abstract",
                Modifier.isAbstract(CargoUnit.class.getModifiers()));

        check("RefrigeratedContainer extends StandardContainer (3 levels)",
                StandardContainer.class.isAssignableFrom(RefrigeratedContainer.class));
        check("HazmatContainer extends StandardContainer",
                StandardContainer.class.isAssignableFrom(HazmatContainer.class));
        check("LiquidTank extends CargoUnit directly",
                LiquidTank.class.getSuperclass() == CargoUnit.class);

        try {
            Method m = CargoUnit.class.getDeclaredMethod("totalStorageCharge");
            check("totalStorageCharge() is final", Modifier.isFinal(m.getModifiers()));
        } catch (NoSuchMethodException e) {
            check("totalStorageCharge() exists", false);
        }

        boolean allPrivate = true;
        for (Class<?> c : new Class<?>[]{StandardContainer.class, RefrigeratedContainer.class,
                HazmatContainer.class, LiquidTank.class, Client.class}) {
            for (Field f : c.getDeclaredFields()) {
                if (!Modifier.isPrivate(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
                    allPrivate = false;
                    System.out.println("      non-private field: " + c.getSimpleName() + "." + f.getName());
                }
            }
        }
        check("all instance fields are private", allPrivate);
    }

    // ---------- 3. fee formulas ----------
    private static void checkFees() {
        Client walkIn = new Client("C-1", "Walk In Ltd");

        StandardContainer std = new StandardContainer("U-STD", walkIn, 1000, 4, 30.0);
        eq("standard daily fee", TariffPolicy.BASE_STORAGE_RATE * 30.0, std.dailyStorageFee());
        eq("totalStorageCharge = fee x days",
                TariffPolicy.BASE_STORAGE_RATE * 30.0 * 4, std.totalStorageCharge());

        RefrigeratedContainer reefer =
                new RefrigeratedContainer("U-REF", walkIn, 1200, 3, 30.0, -18.0, 4.0);
        eq("reefer fee extends parent fee",
                TariffPolicy.BASE_STORAGE_RATE * 30.0 + TariffPolicy.POWER_RATE * 4.0,
                reefer.dailyStorageFee());
        check("reefer costs more than plain container of same volume",
                reefer.dailyStorageFee() > std.dailyStorageFee());

        HazmatContainer haz = new HazmatContainer("U-HAZ", walkIn, 900, 2, 30.0, 3, true);
        eq("hazmat fee multiplies parent fee",
                TariffPolicy.BASE_STORAGE_RATE * 30.0 * TariffPolicy.HAZMAT_MULTIPLIER,
                haz.dailyStorageFee());

        LiquidTank tank = new LiquidTank("U-TNK", walkIn, 2000, 5, 20000.0, 50.0);
        eq("tank fee uses actual content",
                TariffPolicy.LIQUID_RATE * 10000.0, tank.dailyStorageFee());

        double before = tank.dailyStorageFee();
        tank.transferOut(5000.0);
        check("transferOut reduces the daily fee", tank.dailyStorageFee() < before);
    }

    // ---------- 4. dynamic dispatch ----------
    private static void checkPolymorphism() {
        Client c = new Client("C-2", "Poly Ltd");

        CargoUnit asBase = new RefrigeratedContainer("U-P1", c, 1200, 1, 10.0, -18.0, 2.0);
        check("dispatch through base reference reaches Reefer",
                "Reefer".equals(asBase.handlingCategory()));

        CargoUnit tankAsBase = new LiquidTank("U-P2", c, 800, 1, 5000.0, 100.0);
        check("dispatch through base reference reaches Tank",
                "Tank".equals(tankAsBase.handlingCategory()));

        check("toString chain includes subclass detail",
                asBase.toString().contains("temp=") && asBase.toString().contains("volume="));

        check("hazmat briefing states the class",
                new HazmatContainer("U-P3", c, 100, 1, 5, 7, false)
                        .safetyBriefing().contains("7"));
    }

    // ---------- 5. client hierarchy ----------
    private static void checkClients() {
        Client std = new Client("C-10", "Standard Co");
        ContractClient con = new ContractClient("C-11", "Contract Co", 30.0);
        GovernmentClient gov = new GovernmentClient("C-12", "Port Authority", "AG-9");

        eq("standard client discount is 0", 0.0, std.discountPercent());
        eq("contract client discount", 30.0, con.discountPercent());
        eq("government client discount", 25.0, gov.discountPercent());
        check("government client has priority", gov.priorityHandling());
        check("standard client has no priority", !std.priorityHandling());
        check("tiers are distinct",
                !std.clientTier().equals(con.clientTier())
                        && !con.clientTier().equals(gov.clientTier()));
    }

    // ---------- 6. yard engine ----------
    private static void checkYard() {
        ContractClient con = new ContractClient("C-20", "Contract Co", 25.0);
        Client walkIn = new Client("C-21", "Walk In Ltd");

        Yard yard = new Yard("Test Yard", 5);
        StandardContainer a = new StandardContainer("Y-1", con, 1000, 10, 10.0);
        LiquidTank b = new LiquidTank("Y-2", walkIn, 3000, 10, 10000.0, 100.0);
        yard.receive(a);
        yard.receive(b);

        eq("yard revenue sums polymorphic fees",
                a.dailyStorageFee() + b.dailyStorageFee(), yard.totalDailyRevenue());

        eq("invoice applies the client's own discount",
                a.totalStorageCharge() * 0.75, yard.invoiceFor(con));
        eq("walk-in invoice has no discount",
                b.totalStorageCharge(), yard.invoiceFor(walkIn));

        check("heaviestUnit returns the heaviest", yard.heaviestUnit() == b);

        boolean threw = false;
        try {
            yard.receive(new StandardContainer("Y-1", con, 500, 1, 5.0));
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        check("duplicate unit ID is rejected", threw);

        Yard tiny = new Yard("Tiny", 1);
        tiny.receive(new StandardContainer("T-1", con, 100, 1, 1.0));
        threw = false;
        try {
            tiny.receive(new StandardContainer("T-2", con, 100, 1, 1.0));
        } catch (IllegalStateException e) {
            threw = true;
        }
        check("over-capacity throws IllegalStateException", threw);
    }

    // ---------- 7. validation ----------
    private static void checkValidation() {
        Client c = new Client("C-30", "Valid Co");

        throwsIAE("negative weight rejected",
                () -> new StandardContainer("V-1", c, -5, 1, 10.0));
        throwsIAE("zero volume rejected",
                () -> new StandardContainer("V-2", c, 100, 1, 0));
        throwsIAE("null owner rejected",
                () -> new StandardContainer("V-3", null, 100, 1, 10.0));
        throwsIAE("reefer above 8C rejected",
                () -> new RefrigeratedContainer("V-4", c, 100, 1, 10.0, 20.0, 2.0));
        throwsIAE("hazard class 0 rejected",
                () -> new HazmatContainer("V-5", c, 100, 1, 10.0, 0, false));
        throwsIAE("fill percent over 100 rejected",
                () -> new LiquidTank("V-6", c, 100, 1, 1000.0, 140.0));
        throwsIAE("contract discount over 40 rejected",
                () -> new ContractClient("V-7", "Too Generous", 55.0));
        throwsIAE("blank client name rejected",
                () -> new Client("V-8", "   "));
    }

    // ---------- helpers ----------
    private interface Block { void run(); }

    private static void throwsIAE(String label, Block b) {
        try {
            b.run();
            check(label, false);
        } catch (IllegalArgumentException e) {
            check(label, true);
        } catch (RuntimeException e) {
            System.out.println("      wrong exception type: " + e.getClass().getSimpleName());
            check(label, false);
        }
    }

    private static void eq(String label, double expected, double actual) {
        boolean ok = Math.abs(expected - actual) < 0.0001;
        if (!ok) {
            System.out.printf("      expected %.4f but got %.4f%n", expected, actual);
        }
        check(label, ok);
    }

    private static void check(String label, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  PASS  " + label);
        } else {
            failed++;
            System.out.println("  FAIL  " + label);
        }
    }
}
