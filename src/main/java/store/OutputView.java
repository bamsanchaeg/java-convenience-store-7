package store;

import java.util.List;
import java.util.Map;
import store.Receipt;
import store.output.ConsoleWriter;
import store.output.Writer;

public class OutputView {

    private static final Writer DEFAULT_WRITER = new ConsoleWriter();

    private final Writer writer;

    public OutputView() {
        this(DEFAULT_WRITER);
    }

    public OutputView(Writer writer) {
        this.writer = writer;
    }

    public static void displayProducts(Inventory inventory) {

        System.out.println("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.");
        List<Product> products = inventory.getProducts();

        for (Product product : products) {
            String promoMessage = getPromotionMessage(product);
            String regularStockMessage = getStockMessage(product.getRegularStock(), "재고 없음");
            String promotionStockMessage = getStockMessage(product.getPromotionStock(), "");

            printProductInfo(product, regularStockMessage, promotionStockMessage, promoMessage);
        }
        System.out.println();

    }

    private static String getPromotionMessage(Product product) {
        if (product.getPromotion() != null && product.getPromotion().getType() != PromotionType.NONE) {
            return product.getPromotion().getType().getDisplayName();
        }
        return "";
    }

    private static String getStockMessage(int stock, String noStockMessage) {
        if (stock > 0) {
            return stock + "개";
        }
        return noStockMessage;
    }

    private static void printProductInfo(Product product, String regularStockMessage, String promotionStockMessage,
                                         String promoMessage) {
        if (!promotionStockMessage.isEmpty()) {
            System.out.printf("- %s %,.0f원 %s %s\n", product.getName(), product.getPrice(), promotionStockMessage,
                    promoMessage);
        }
        if (product.getRegularStock() > 0 || promotionStockMessage.isEmpty()) {
            System.out.printf("- %s %,.0f원 %s\n", product.getName(), product.getPrice(), regularStockMessage);
        }

    }

    public static void displayFinalReceipt(Inventory inventory, Map<String, Map<String, Integer>> purchaseResults,
                                           MembershipDiscount membershipDiscount, boolean hasMembershipDiscount) {
        Map<String, Integer> purchaseDetails = purchaseResults.get("purchaseDetails");
        Map<String, Integer> bonusDetails = purchaseResults.get("bonusDetails");

        Receipt receipt = new Receipt(purchaseDetails, bonusDetails);
        receipt.calculateCosts(inventory);
        double membershipDiscountAmount = receipt.applyMembershipDiscount(membershipDiscount, hasMembershipDiscount);
        receipt.displayReceipt(membershipDiscountAmount);
    }


}
