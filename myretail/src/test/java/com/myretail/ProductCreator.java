package com.myretail;

import com.myretail.domain.product.model.CurrentPrice;
import com.myretail.domain.product.model.Product;

public class ProductCreator {

	//Create Product
	public Product create(String id ,String name ,String price ,String currentCode) {
		CurrentPrice currencyPrice = new CurrentPrice(price, currentCode);
		Product product = new Product(id, name, currencyPrice);
		return product;
	}
}
