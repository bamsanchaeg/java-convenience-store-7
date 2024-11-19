package store;

import java.util.HashMap;
import java.util.Map;

public class Product {

    private String name;
    private double price;
    private int regularStock; // 일반 재고
    private int promotionStock; // 프로모션 재고
    private Promotion promotion;

    public Product(String name, double price, int regularStock, int promotionStock, PromotionType promotionType) {
        this.name = name;
        this.price = price;
        this.regularStock = regularStock;
        this.promotionStock = promotionStock;
        this.promotion = Promotion.getPromotionByType(promotionType);
    }

    // 상품 이름 getter
    public String getName() {
        return name;
    }

    // 가격 getter
    public double getPrice() {
        return price;
    }

    // 프로모션 getter
    public Promotion getPromotion() {
        return promotion;
    }


    // 재고 수량 getter
    public int getRegularStock() {
        return regularStock;
    }

    public int getPromotionStock() {
        return promotionStock;
    }


    public boolean hasPromotion() {
        return promotion != null && promotion.getType() != PromotionType.NONE;
    }


    public Map<String, Integer> reduceStock(Map<String, Integer> results) {
        int remainingQuantity = results.get("promoUsed") + results.get("regularUsed");
        int promoUsed = results.get("promoUsed");         // 프로모션 재고에서 사용된 수량
        int bonusQuantity = 0;     // 증정 수량
        int regularUsed = results.get("regularUsed");       // 일반 재고에서 사용된 수량

        // 프로모션이 활성 상태일 경우 프로모션을 먼저 사용
        if (isPromotionActive()) {
            bonusQuantity = applyPromotionDiscount(remainingQuantity);
            int promoReduction = calculatePromotionReduction(remainingQuantity, bonusQuantity);
            promoUsed = reducePromotionStock(promoReduction); // 프로모션 재고에서 차감
            remainingQuantity -= promoUsed;
        } else {
            // 프로모션이 비활성화된 경우 일반 재고로 결제
            Product generalProduct = findGeneralProductByName(this.name);
            if (generalProduct != null) {
                regularUsed = generalProduct.reduceRegularStock(remainingQuantity);
                remainingQuantity -= regularUsed;
            } else {
                regularUsed = reduceRegularStock(remainingQuantity);
                remainingQuantity -= regularUsed;
            }
        }

        // 프로모션 재고가 부족하여 남은 수량이 있을 경우 일반 재고로 처리
        if (remainingQuantity > 0) {
            regularUsed = reduceRegularStock(remainingQuantity);
            remainingQuantity -= regularUsed;
        }

        int purchasedQuantity = promoUsed + regularUsed;  // 최종 구매 수량
        // 최종 결과를 맵으로 반환하여, 프로모션 결제, 일반 재고 결제, 보너스 수량을 구분
        Map<String, Integer> result = new HashMap<>();
        result.put("purchasedQuantity", purchasedQuantity);  // 총 구매 수량
        result.put("bonusQuantity", bonusQuantity);                // 증정 수량
        result.put("regularUsed", regularUsed);                    // 일반 재고 사용량
        result.put("promoUsed", promoUsed);                        // 프로모션 재고 사용량
        System.out.println("최종결과" + result);
        return result;
    }


    // 동일 이름의 일반 재고 상품을 찾는 메서드 추가
    public Product findGeneralProductByName(String name) {
        return Inventory.getProducts().stream()
                .filter(product -> product.getName().equals(name) && !product.isPromotionActive()
                        && product.getRegularStock() > 0)
                .findFirst()
                .orElse(null);
    }


    private int applyPromotionDiscount(int remainingQuantity) {
        return promotion.calculateBonusQuantity(remainingQuantity);
    }

    //프로모션에서 차감할 수량을 계산하고 프로모션 재고에서 차감
    private int calculatePromotionReduction(int remainingQuantity, int bonusQuantity) {
        return promotion.calculatePromotionReduction(remainingQuantity, bonusQuantity);
    }

    public int reduceRegularStock(int remainingQuantity) {
        int reduction = Math.min(regularStock, remainingQuantity);
        regularStock -= reduction;
        return remainingQuantity - reduction;
    }

    public int reducePromotionStock(int quantity) {
        int reduction = Math.min(promotionStock, quantity);  // 차감할 수 있는 최대 수량 계산
        promotionStock -= reduction;
        return reduction;
    }

    public int calculateBonusQuantity(int promoUsed) {
        if (promotion == null || !promotion.isActive()) {
            return 0;
        }
        return promotion.calculateBonusQuantity(promoUsed);
    }

    private boolean isPromotionActive() {
        boolean active = promotion != null && promotion.isActive() && promotionStock > 0;

        if (!active) {
            // 프로모션 비활성화 시 프로모션 재고 0으로 설정
            promotionStock = 0;
        }

        return active;
    }


    @Override
    public String toString() {
        String promotionText = (promotion != null && promotion.isActive()) ? promotion.getType().getDisplayName() : "";
        return String.format("%s %,.0f원 %d개 %s", name, price, regularStock, promotionStock, promotionText);
    }
}
