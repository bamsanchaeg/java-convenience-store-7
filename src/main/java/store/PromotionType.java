package store;

public enum PromotionType implements PromotionCalculator {


    NONE("") {
        @Override
        public int calculateBonusQuantity(int remainingQuantity) {
            return 0;
        }

        @Override
        public int calculatePromotionReduction(int remainingQuantity, int bonusQuantity) {
            return 0;
        }
    },
    MD_RECOMMENDATION("MD추천상품") {
        @Override
        public int calculateBonusQuantity(int remainingQuantity) {
            return 0;
        }

        @Override
        public int calculatePromotionReduction(int remainingQuantity, int bonusQuantity) {
            return remainingQuantity;
        }
    },
    BUY_2_GET_1("탄산2+1") {
        @Override
        public int calculateBonusQuantity(int remainingQuantity) {
            int triggeredQuantity = 2;
            int bonusQuantity = 1;
            return (remainingQuantity / triggeredQuantity) * bonusQuantity;
        }

        @Override
        public int calculatePromotionReduction(int remainingQuantity, int bonusQuantity) {
            int triggeredQuantity = 2; // 예시
            int promoSets = remainingQuantity / triggeredQuantity;
            return promoSets * triggeredQuantity; // 프로모션 수량만큼 차감
        }
    },
    BUY_1_GET_1("1_1") {
        @Override
        public int calculateBonusQuantity(int remainingQuantity) {
            int triggeredQuantity = 2;
            int bonusQuantity = 1;
            return (remainingQuantity / triggeredQuantity) * bonusQuantity;
        }

        @Override
        public int calculatePromotionReduction(int remainingQuantity, int bonusQuantity) {
            int applicablePairs = remainingQuantity / 2;
            return applicablePairs * 2;
        }
    },
    FLASH_SALE("반짝할인") {
        @Override
        public int calculateBonusQuantity(int remainingQuantity) {
            return 0;
        }

        @Override
        public int calculatePromotionReduction(int remainingQuantity, int bonusQuantity) {
            return remainingQuantity;
        }
    };

    private final String displayName;

    PromotionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PromotionType fromString(String text) {
        for (PromotionType b : PromotionType.values()) {
            if (b.displayName.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null; // 찾지 못한 경우
    }

}
