package com.myretail.interfaces;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import com.myretail.domain.product.exception.ProductException;
import com.myretail.domain.product.model.Product;
import com.myretail.domain.product.service.ProductDomainService;

@RequestMapping("/products")
@RestController
public class ProductController {

	private Logger LOG = LoggerFactory.getLogger(ProductController.class);
	@Autowired
	ProductDomainService productDomainService;

	ProductController() {
	}

	ProductController(ProductDomainService productDomainService) {
		this.productDomainService = productDomainService;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Product> getProductsById(@PathVariable("id") String id) {
		LOG.info("Finding product for product id " + id);
		Product product = productDomainService.getProductsById(id);
		ResponseEntity<Product> response = product != null ? new ResponseEntity<>(product, HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return response;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Product> updateProduct(@PathVariable("id") String id, @RequestBody Product product) {
		if (product == null) {
			throw new ProductException("product payload is required");
		}
		if (!id.equalsIgnoreCase(product.getId())) {
			LOG.error("invalid payload,payload product id {} and request product id {} are not matching ",
					product.getId(), id);
			throw new ProductException("invalid payload, payload product id and request product id are not matching");
		}
		Product currentProduct = productDomainService.getProductsById(id);
		// If product is not exist , do not insert.This api is only for updating
		if (currentProduct != null) {
			product = productDomainService.updateProduct(id, product);
			return new ResponseEntity<>(product, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
