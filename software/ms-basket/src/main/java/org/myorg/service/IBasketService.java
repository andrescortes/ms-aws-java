package org.myorg.service;

import java.util.List;
import org.myorg.model.Basket;

public interface IBasketService {

    List<Basket> getAllBaskets();

    Basket getBasketById(String userName);

    Basket createBasket(Basket basket);



    Basket deleteBasket(String userName);


}
