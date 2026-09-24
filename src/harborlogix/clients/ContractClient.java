package harborlogix.clients;

/**
 * A client with a negotiated discount between 0 and 40 percent.
 */
public class ContractClient extends Client {

    private final double contractDiscount;

    public ContractClient(String clientId, String name, double contractDiscount) {
        super(clientId, name);
        if (contractDiscount < 0 || contractDiscount > 40) {
            throw new IllegalArgumentException("Contract discount must be 0-40, got " + contractDiscount);
        }
        this.contractDiscount = contractDiscount;
    }

    @Override
    public double discountPercent() {
        return contractDiscount;
    }

    @Override
    public String clientTier() {
        return "Contract";
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
