

import java.util.*;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    private ItemRepository itemRepository;
    private SalesPromotionRepository salesPromotionRepository;
    private Map<String, Item> itemMap;
    private Map<String, SalesPromotion> salesPromotionMap;
    private SalesPromotion salesPromotion;
    public static final String YUAN = "yuan";
    public static final String X = " x ";

    public App(ItemRepository itemRepository, SalesPromotionRepository salesPromotionRepository) {
        this.itemRepository = itemRepository;
        this.salesPromotionRepository = salesPromotionRepository;
        itemMap = new HashMap<>();
        List<Item> itemList = this.itemRepository.findAll();
        for (Item item : itemList) {
            itemMap.put(item.getId(), item);
        }
        List<SalesPromotion> salesPromotionList = this.salesPromotionRepository.findAll();

        for (SalesPromotion promotion : salesPromotionList) {
            if ("50%_DISCOUNT_ON_SPECIFIED_ITEMS".equals(promotion.getType())) {
                salesPromotion = promotion;
                break;
            }
        }
    }

    public String bestCharge(List<String> inputs) {
        StringBuilder output = new StringBuilder();
        Map<String, Item> orderItemMap = new HashMap<>();
        Map<String, String> orderItemDetailsMap = new HashMap<>();
        List<String> orderDiscountItemName = new ArrayList<>();



        output.append("============= Order details =============\n");
        for (String itemInput : inputs) {
            String[] itemDetail = itemInput.split(X);
            if (itemDetail.length == 2 && itemMap.containsKey(itemDetail[0])) {
                Item item = itemMap.get(itemDetail[0]);
                orderItemMap.put(itemDetail[0], item);
                orderItemDetailsMap.put(itemDetail[0], itemDetail[1]);
                if (salesPromotion.getRelatedItems().contains(item.getId())) {
                    orderDiscountItemName.add(item.getName());
                }
                output
                        .append(item.getName())
                        .append(X)
                        .append(itemDetail[1])
                        .append(" = ")
                        .append(calculateSingleItemTotalPrice(itemDetail[1], item))
                        .append(StringUtils.BLANK)
                        .append(YUAN)
                        .append("\n");
            }
        }
        output.append("-----------------------------------\n");
        String result;
        double chargeForDeduce6 = 0.0;
        for (Map.Entry<String, Item> entry : orderItemMap.entrySet()) {
            chargeForDeduce6 +=
                    entry.getValue().getPrice() * Double.parseDouble(orderItemDetailsMap.get(entry.getKey()));
        }
        if (chargeForDeduce6 >= 30) {
            chargeForDeduce6 -= 6;
        }

        double originalPrice = 0.0;
        double deducePriceAfterDiscount = 0;

        for (Map.Entry<String, Item> entry : orderItemMap.entrySet()) {
            originalPrice += entry.getValue().getPrice() * Double.parseDouble(orderItemDetailsMap.get(entry.getKey()));
        }
        if (null != salesPromotion && null != salesPromotion.getRelatedItems()) {
            for (Map.Entry<String, Item> entry : orderItemMap.entrySet()) {
                if (salesPromotion.getRelatedItems().contains(entry.getKey())) {
                    deducePriceAfterDiscount +=
                            entry.getValue().getPrice()
                                    * 0.5
                                    * Double.parseDouble(orderItemDetailsMap.get(entry.getKey()));
                } else {
                    deducePriceAfterDiscount +=
                            entry.getValue().getPrice()
                                    * Double.parseDouble(orderItemDetailsMap.get(entry.getKey()));
                }
            }
        }

        if (chargeForDeduce6 <= deducePriceAfterDiscount) {
            if (originalPrice >= 30.0) {
                output
                        .append("Promotion used:\n")
                        .append("满30减6 yuan，saving 6 yuan\n")
                        .append("-----------------------------------\n");
            }
            result = doubleToString(chargeForDeduce6);
        } else {
            output
                    .append("Promotion used:\n")
                    .append("Half price for certain dishes (")
                    .append(String.join("，", orderDiscountItemName))
                    .append(")，saving ")
                    .append((int)(originalPrice - deducePriceAfterDiscount))
                    .append(" yuan\n")
                    .append("-----------------------------------\n");
            result = doubleToString(deducePriceAfterDiscount);
        }
        output.append("Total：").append(result).append(" yuan\n");
        output.append("===================================");

        return output.toString();
    }

    public String calculateSingleItemTotalPrice(String amountStr, Item item) {
        int amount = Integer.parseInt(amountStr);
        double sumMoney = item.getPrice() * amount;
        return doubleToString(sumMoney);
    }

    public String doubleToString(double value) {
        String sumMoneyString = String.valueOf(value);
        if( sumMoneyString.indexOf(".") > 0){
            sumMoneyString = sumMoneyString.replaceAll("0+?$", "");
            sumMoneyString = sumMoneyString.replaceAll("[.]$", "");
        }
        return sumMoneyString;
    }

}
