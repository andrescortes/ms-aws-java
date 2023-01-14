package org.myorg.model.transformer;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.myorg.model.Basket;

public class Mapper {

    public static List<Basket> mapToListEntity(ScanResult scanResult) {
        List<Basket> baskets = new ArrayList<>();
        for (Map<String, AttributeValue> item : scanResult.getItems()) {

            AttributeValue userName = item.getOrDefault("userName", new AttributeValue());
            AttributeValue totalPrice = item.getOrDefault("totalPrice", new AttributeValue());
            AttributeValue firstName = item.getOrDefault("firstName", new AttributeValue());
            AttributeValue lastName = item.getOrDefault("lastName", new AttributeValue());
            AttributeValue email = item.getOrDefault("email", new AttributeValue());
            AttributeValue address = item.getOrDefault("address", new AttributeValue());
            AttributeValue cardInfo = item.getOrDefault("cardInfo", new AttributeValue());
            AttributeValue paymentMethod = item.getOrDefault("paymentMethod", new AttributeValue());
            Basket basket = new Basket();
            basket.setUserName(userName.getS());
            basket.setTotalPrice(Double.parseDouble(totalPrice.getN()));
            basket.setFirstName(firstName.getS());
            basket.setLastName(lastName.getS());
            basket.setEmail(email.getS());
            basket.setAddress(address.getS());
            basket.setCardInfo(cardInfo.getS());
            basket.setPaymentMethod(Integer.parseInt(paymentMethod.getN()));

            baskets.add(basket);
        }
        return baskets;
    }
    /*
    *     private String userName;
    private double totalPrice;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String cardInfo;
    private int paymentMethod;*/
}
