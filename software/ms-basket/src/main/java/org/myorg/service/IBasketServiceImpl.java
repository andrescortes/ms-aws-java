package org.myorg.service;

import java.util.List;
import java.util.Objects;
import org.myorg.model.Basket;
import org.myorg.repository.IBasketRepositoryImpl;

public class IBasketServiceImpl implements IBasketService {

    private final IBasketRepositoryImpl repository = new IBasketRepositoryImpl();


    @Override
    public List<Basket> getAllBaskets() {
        return repository.getBaskets();
    }

    @Override
    public Basket getBasketById(String userName) {
        Basket basket = repository.getBasket(userName);

        if (Objects.isNull(basket)) {
            throw new IllegalArgumentException("No found basket with id " + userName);
        }
        return basket;
    }

    @Override
    public Basket createBasket(Basket basket) {
        return repository.createBasket(basket);
    }


    @Override
    public Basket deleteBasket(String userName) {
        return repository.deleteBasket(userName);
    }

}
