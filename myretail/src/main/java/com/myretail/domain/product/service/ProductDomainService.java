package com.myretail.domain.product.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myretail.domain.product.client.RedSkyServiceClient;
import com.myretail.domain.product.model.Product;
import com.myretail.domain.product.model.ProductRepository;

@Service
public class ProductDomainService {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	RedSkyServiceClient redSkyServiceClient;

	private Logger LOG = LoggerFactory.getLogger(ProductDomainService.class);

	ProductDomainService() {
	}

	ProductDomainService(ProductRepository productRepository, RedSkyServiceClient redSkyServiceClient) {
		this.productRepository = productRepository;
		this.redSkyServiceClient = redSkyServiceClient;
	}

	public Product getProductsById(String id) {
		//Getting product details from mongo db
		Optional<Product> productOptional = productRepository.findById(id);
		Product product = null;
		if (productOptional.isPresent()) {
			product = productOptional.get();
			//Getting product name from external api
			String productName = redSkyServiceClient.getRedskyProductName(id);
			LOG.info("Red Sky product name - {} ",productName);
			product.setName(productName);
		}
		return product;
	}

	public Product updateProduct(String id, Product product) {
		product = productRepository.save(product);
		return product;
	}

}
