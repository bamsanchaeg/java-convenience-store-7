package store;

public interface PromotionCalculator {

    int calculateBonusQuantity(int remainingQuantity);

    int calculatePromotionReduction(int remainingQuantity, int bonusQuantity);
}
