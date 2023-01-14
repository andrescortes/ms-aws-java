package org.myorg.repository;

import java.util.List;
import org.myorg.model.Basket;

public interface IBasketRepository {

    List<Basket> getBaskets();

    Basket getBasket(String userName);

    Basket createBasket(Basket basket);

    Basket deleteBasket(String userName);


}
