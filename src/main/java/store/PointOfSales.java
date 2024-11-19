package store;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PointOfSales {

    // 구매 처리
    public Map<String, Map<String, Integer>> processPurchase(Map<String, Integer> purchaseRequest,
                                                             InputView inputView) {
        Map<String, Integer> purchaseDetails = new HashMap<>();
        Map<String, Integer> bonusDetails = new HashMap<>();
        for (Map.Entry<String, Integer> entry : purchaseRequest.entrySet()) {
            processProduct(entry, purchaseDetails, bonusDetails, inputView);
        }

        return createResultMap(purchaseDetails, bonusDetails);
    }

    // 단일 제품에 대한 구매와 보너스 내역을 처리하는 메서드
    private Map<String, Integer> processProductPurchase(Product product, int requestedQuantity, InputView inputView) {
        int remainingQuantity = validateAndAdjustStock(product, requestedQuantity);
        // 재고 초과 확인
        Map<String, Integer> results = handlePromotion(product, remainingQuantity, inputView);
        // 최종 재고 차감 처리
        return finalizePurchase(product, results);
    }

    //재고초과 확인 및 조정
    private int validateAndAdjustStock(Product product, int requestedQuantity) {
        Product generalProduct = product.findGeneralProductByName(product.getName());
        int totalAvailableStock = generalProduct.getRegularStock() + product.getPromotionStock();
        validateOverStock(requestedQuantity, totalAvailableStock);
        return requestedQuantity;
    }

    private Map<String, Integer> finalizePurchase(Product product, Map<String, Integer> remainingQuantity) {
        return product.reduceStock(remainingQuantity);
    }

    private Map<String, Integer> handlePromotion(Product product, int remainingQuantity, InputView inputView) {
        Map<String, Integer> results = new LinkedHashMap<>();
        if (!isValidatePromotion(product)) {
            results.put("regularUsed", remainingQuantity);
            return results;
        }

        int triggeredQuantity = product.getPromotion().getTriggerQuantity();
        int additionalRequired = calculateAdditionalRequired(remainingQuantity, triggeredQuantity);

        int promotionStock = product.getPromotionStock();
        if (remainingQuantity > promotionStock) {
            int nonPromotionalQuantity = remainingQuantity - promotionStock;

            // 안내 메시지를 출력하고, 정가 결제 여부를 묻기
            System.out.printf("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 정가로 구매하시겠습니까? (Y/N)%n",
                    product.getName(), nonPromotionalQuantity);
            if (InputView.confirmUserInput()) {
                results.put("promoUsed", promotionStock);
                results.put("regularUsed", nonPromotionalQuantity);
            }
        }
        if (additionalRequired > 0 && confirmAdditionalPurchase(product, additionalRequired)) {
            remainingQuantity += additionalRequired;
            results.put("promoUsed", remainingQuantity);
        }

        System.out.println("구매해야할 수량 출력(POS)" + results);
        return results;
    }

    //추가 수량이 필요한지 계산하는 메서드
    private int calculateAdditionalRequired(int remainingQuantity, int triggerQuantity) {
        return remainingQuantity < triggerQuantity ? triggerQuantity - remainingQuantity : 0;
    }

    private boolean confirmAdditionalPurchase(Product product, int additionalRequired) {
        System.out.printf("현재 %s은(는) %d개를 더 가져오면 1개를 무료로 받을 수 있습니다. 추가하시겠습니까? (Y/N)%n",
                product.getName(), additionalRequired);
        return InputView.confirmUserInput();
    }

    private void validateOverStock(int remainQuantity, int totalAvailableStock) {
        if (remainQuantity > totalAvailableStock) {
            System.out.println("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    private boolean isValidatePromotion(Product product) {
        return product.hasPromotion() && product.getPromotion().isActive();
    }

    private void processProduct(Map.Entry<String, Integer> entry, Map<String, Integer> purchaseDetails,
                                Map<String, Integer> bonusDetails, InputView inputView) {
        String productName = entry.getKey();
        int requestQuantity = entry.getValue();
        Product product = Inventory.findProductByName(productName);
        if (product == null) {
            return;
        }

        Map<String,Integer> purchaseAndBonus = getPurchaseAndBonusDetails(product, requestQuantity, inputView);
        updatePurchaseAndBonusRecords(productName, purchaseAndBonus, purchaseDetails, bonusDetails);
    }

    private Map<String,Integer> getPurchaseAndBonusDetails(Product product, int requestQuantity, InputView inputView) {
        return processProductPurchase(product, requestQuantity, inputView);
    }

    private void updatePurchaseAndBonusRecords(String productName, Map<String,Integer> purchaseAndBonus,
                                               Map<String, Integer> purchaseDetails,
                                               Map<String, Integer> bonusDetails) {
        purchaseDetails.put(productName, purchaseAndBonus.getClass().getModifiers());
        if (purchaseAndBonus.get(1) > 0) {
            bonusDetails.put(productName, purchaseAndBonus.get(1));
        }
    }

    private Map<String, Map<String, Integer>> createResultMap(Map<String, Integer> purchaseDetails,
                                                              Map<String, Integer> bonusDetails) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        result.put("purchaseDetails", purchaseDetails);
        result.put("bonusDetails", bonusDetails);
        return result;
    }
}
