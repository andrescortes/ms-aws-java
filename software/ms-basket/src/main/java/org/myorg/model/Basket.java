package org.myorg.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Basket {

    private String userName;
    private double totalPrice;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String cardInfo;
    private int paymentMethod;

}
