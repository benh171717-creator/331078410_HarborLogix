package harborlogix.app;

import harborlogix.cargo.*;
import harborlogix.clients.*;
import harborlogix.ops.*;
import java.util.ArrayList;

/**
 * Demonstration program: manifest, invoices, drainage round, then Part D.
 */
public class TerminalApp {

    public static void main(String[] args) {
        System.out.println("HarborLogix terminal - student " + TariffPolicy.STUDENT_ID);

        Client walkIn = new Client("C-100", "Ad Hoc Shipping");
        ContractClient contract = new ContractClient("C-101", "Blue Wave Logistics", 20.0);
        GovernmentClient government = new GovernmentClient("C-102", "Ministry of Transport", "GOV-7");

        Yard yard = new Yard("Ashdod Terminal", TariffPolicy.YARD_CAPACITY);

        StandardContainer standard = new StandardContainer("U-1", walkIn, 5000, 4, 25.0);
        RefrigeratedContainer reefer = new RefrigeratedContainer("U-2", contract, 8000, 6, 30.0, -18.0, 3.5);
        HazmatContainer hazmat = new HazmatContainer("U-3", government, 7000, 3, 20.0, 6, true);
        LiquidTank tank = new LiquidTank("U-4", contract, 12000, 5, 20000.0, 80.0);

        yard.receive(standard);
        yard.receive(reefer);
        yard.receive(hazmat);
        yard.receive(tank);

        System.out.println();
        System.out.println("=== 1. Yard Manifest ===");
        yard.printManifest();

        System.out.println();
        System.out.println("=== 2. Client Invoices ===");
        printInvoice(yard, walkIn);
        printInvoice(yard, contract);
        printInvoice(yard, government);

        System.out.println();
        System.out.println("=== 3. Drainage Round ===");
        for (CargoUnit unit : yard.getUnits()) {
            if (unit instanceof LiquidTank liquidTank) {
                double moved = liquidTank.transferOut(3000.0);
                System.out.printf("Drained %.1f L from %s (now %.1f L)%n",
                        moved, liquidTank.getUnitId(), liquidTank.currentLitres());
            }
        }

        System.out.println();
        System.out.println("=== 4. Part D: Oversized Cargo ===");
        OversizedCargo oversized = new OversizedCargo("U-5", walkIn, 15000, 2, 18.0, true);
        yard.receive(oversized);
        yard.printManifest();

        System.out.println();
        System.out.println("=== 5. Bonus: Inspection Notes ===");
        ArrayList<Inspectable> inspectables = new ArrayList<>();
        for (CargoUnit unit : yard.getUnits()) {
            if (unit instanceof Inspectable inspectable) {
                inspectables.add(inspectable);
            }
        }
        yard.printInspectionNotes(inspectables);
    }

    private static void printInvoice(Yard yard, Client client) {
        System.out.printf("%s (%s, %.0f%% discount): %.2f%n",
                client.getName(), client.clientTier(), client.discountPercent(), yard.invoiceFor(client));
    }
}
