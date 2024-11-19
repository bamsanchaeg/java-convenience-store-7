package store;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PromotionLoader {

    private static List<Promotion> defaultPromotions = new ArrayList<>();
    private final String PRODUCT_DELIMITER = ",";

    //프로모션은 프로모션 그 자체로 놔줘야함. 프로모션 리스트를 만들어주는 곳이 필요함.
    //프로모션이라는 객체 안에다가 계속 프로모션을 만들어서 객체 자체가 유지가 안되는 것
    public void initializePromotions(List<String> promotionData) {
        defaultPromotions.clear();//비워지게 됨, 초기화를 안해주면 계속 쌓인 데이터가 남아있음(static이기 때문에)
        promotionData.forEach(this::processLines);

    }

    public static List<Promotion> getDefaultPromotions() {
        return defaultPromotions;
    }

    private void processLines(String input) {
        String[] parts = parseLine(input);
        if (isValidaFormat(parts)) {
            parseAndAddPromotion(parts);
        }
    }

    private String[] parseLine(String line) {
        String[] parts = line.split(PRODUCT_DELIMITER);
        if (parts.length < 4) {
            throw new IllegalArgumentException("[ERROR] 유효하지 않은 입력입니다.");
        }
        return parts;
    }

    private boolean isValidaFormat(String[] parts) {
        return parts.length == 5;
    }

    private void parseAndAddPromotion(String[] parts) {
        try {
            PromotionType type = parsePromotionType(parts[0].trim());
            int triggerQuantity = parseInteger(parts[1].trim());
            int bonusQuantity = parseInteger(parts[2].trim());
            LocalDate startDate = parseDate(parts[3].trim());
            LocalDate endDate = parseDate(parts[4].trim());
            addPromotion(type, triggerQuantity, bonusQuantity, startDate, endDate);
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    private PromotionType parsePromotionType(String typeStr) {
        PromotionType type = PromotionType.fromString(typeStr);
        if (type == null) {
            throw new IllegalArgumentException("[ERROR] 유효하지 않은 값입니다.");
        }
        return type;
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR}" + e.getMessage());
            return 0;
        }
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("[ERROR]" + e.getMessage());
            return null;
        }
    }

    private void addPromotion(PromotionType type, int triggerQuantity, int bonusQuantity, LocalDate startDate,
                              LocalDate endDate) {
        if (validAddPromotion(type, startDate, endDate)) {
            defaultPromotions.add(new Promotion(type, triggerQuantity, bonusQuantity, startDate, endDate));
        }
    }

    private boolean validAddPromotion(PromotionType type, LocalDate startDate,
                                      LocalDate endDate) {
        return type != null && startDate != null && endDate != null;
    }


}
