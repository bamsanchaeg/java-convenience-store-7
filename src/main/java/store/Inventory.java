package store;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private final String PRODUCT_DELIMITER = ",";
    private final int NUMBER_OF_CATEGORY = 4;

    private static List<Product> products = new ArrayList<>();
    PromotionLoader applicationCommon = new PromotionLoader();

    public Inventory(List<String> fileProducts, List<String> filePromotions) {
        applicationCommon.initializePromotions(filePromotions);
        createProducts(fileProducts);
    }


    public void createProducts(List<String> lines) {
        lines.forEach(this::loadProductsAsObjects);
    }

    public void loadProductsAsObjects(String fileName) {
        List<String> lines = loadFile(fileName);
        List<Product> products = new ArrayList<>();

        // 첫 줄은 헤더이므로 건너뛴다
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] tokens = line.split(",");

            // name, price, quantity, promotion
            String name = tokens[0];
            int price = Integer.parseInt(tokens[1]);
            int quantity = Integer.parseInt(tokens[2]);
            String promotion = tokens.length > 3 && !tokens[3].equalsIgnoreCase("null") ? tokens[3] : null;

            products.add(new Product(name, price, quantity, promotion));
        }

    }


    // 상품명을 입력받아 해당 상품을 반환하는 메서드
    public static Product findProductByName(String productName) {
        Product product = products.stream()
                .filter(p -> p.getName().equals(productName))
                .findFirst()
                .orElse(null);

        if (product == null) {
            System.out.println("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
        return product;
    }

    public static List<Product> getProducts() {
        return products;
    }

    private boolean isValidaFormat(String[] parts) {
        return parts.length >= NUMBER_OF_CATEGORY;
    }

    private PromotionType getPromotionType(String promotionTypeStr) {
        String validPromotionTypeStr = getValidPromotionTypeStr(promotionTypeStr);
        return determinePromotionType(validPromotionTypeStr);
    }

    private void addProduct(String name, double price, int quantity, PromotionType promotionType) {
        int regularStock = calculateRegularStock(quantity, promotionType);
        int promotionStock = calculatePromotionStock(quantity, promotionType);
        products.add(new Product(name, price, regularStock, promotionStock, promotionType));
    }

    private String[] parseLine(String line) {
        String[] parts = line.split(PRODUCT_DELIMITER);
        if (parts.length < NUMBER_OF_CATEGORY) {
            throw new IllegalArgumentException("[ERROR] 유효하지 않은 입력입니다.");
        }
        return parts;
    }

    private double parsePrice(String priceStr) {
        return Double.parseDouble(priceStr.trim());
    }

    private int parseQuantity(String quantityStr) {
        return Integer.parseInt(quantityStr.trim());
    }

    private String getValidPromotionTypeStr(String promotionTypeStr) {
        return "null".equals(promotionTypeStr.trim()) ? null : promotionTypeStr.trim();
    }

    private PromotionType determinePromotionType(String promotionTypeStr) {
        if (promotionTypeStr == null) {
            return PromotionType.NONE;
        }

        PromotionType promotionType = parsePromotionType(promotionTypeStr);
        if (promotionType == PromotionType.NONE) {
            return PromotionType.NONE;
        }
        return findValidPromotionType(promotionType);
    }

    private PromotionType parsePromotionType(String promotionTypeStr) {
        PromotionType promotionType = PromotionType.fromString(promotionTypeStr);
        return (promotionType != null) ? promotionType : PromotionType.NONE;
    }

    private PromotionType findValidPromotionType(PromotionType promotionType) {
        return PromotionLoader.getDefaultPromotions().stream()
                .filter(promotion -> promotion.getType() == promotionType)
                .findFirst()
                .map(Promotion::getType)
                .orElse(PromotionType.NONE);
    }


    private int calculateRegularStock(int quantity, PromotionType promotionType) {
        if (promotionType == PromotionType.NONE) {
            return quantity;
        }
        return 0;
    }

    private int calculatePromotionStock(int quantity, PromotionType promotionType) {
        if (promotionType != PromotionType.NONE) {
            return quantity;
        }
        return 0;
    }

}
