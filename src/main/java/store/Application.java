package store;

import java.util.List;
import java.util.Map;

public class Application {
    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        FileLoader fileLoader = new FileLoader();
        PointOfSales pointOfSales = new PointOfSales();

        List<String> products = fileLoader.loadProductsFile();
        List<String> promotionsData = fileLoader.loadPromotionsFile();
        Inventory inventory = new Inventory(products, promotionsData);
        MembershipDiscount membershipDiscount = new MembershipDiscount();

        boolean continueShopping = true;
        while (continueShopping) {
            OutputView.displayProducts(inventory);

            Map<String, Integer> product = inputView.purchaseProduct();
            Map<String, Map<String, Integer>> purchaseResults = pointOfSales.processPurchase(product,
                    inputView);

            System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");
            boolean applyMembershipDiscount = InputView.confirmUserInput();

            OutputView.displayFinalReceipt(inventory, purchaseResults, membershipDiscount, applyMembershipDiscount);

            if (!InputView.confirmUserInput()) {
                continueShopping = false;
            }
        }

    }
}
