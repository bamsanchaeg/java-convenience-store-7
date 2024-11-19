package store;

public class MembershipDiscount {

    private final static double DISCOUNT_RATE = 0.3;
    private final static int FULL_RATE = 1;

    public double applyDiscount(double originalPrice) {
        return originalPrice * (FULL_RATE - DISCOUNT_RATE);
    }
}
