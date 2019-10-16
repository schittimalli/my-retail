package com.myretail.interfaces;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.myretail.ProductCreator;
import com.myretail.domain.product.exception.ProductException;
import com.myretail.domain.product.model.Product;
import com.myretail.domain.product.service.ProductDomainService;

@RunWith(MockitoJUnitRunner.class)
public class ProductControllerTest {

	@Mock
	ProductDomainService productDomainServiceMock;

	ProductController productController;

	@Before
	public void setUp() {
		productController = new ProductController(productDomainServiceMock);
	}

	//getProductsById call returns product.HTTPStatus must be OK
	@Test
	public void testGetProductsByIdReturnNotNullProduct() {
		ProductCreator productCreator = new ProductCreator();
		Product expectedProduct = productCreator.create("13860428", "", "123", "USD");
		Mockito.when(productDomainServiceMock.getProductsById(Mockito.eq("13860428"))).thenReturn(expectedProduct);
		ResponseEntity<Product> response = productController.getProductsById("13860428");
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	//getProductsById call returns no product.HTTPStatus must be NOT_FOUND
	@Test
	public void testGetProductsByIdReturnNullProduct() {
		Product expectedProduct = null;
		Mockito.when(productDomainServiceMock.getProductsById(Mockito.eq("13860428"))).thenReturn(expectedProduct);
		ResponseEntity<Product> response = productController.getProductsById("13860428");
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	
	//UpdateProduct call with id and no payload. Validation must fail and must put validation error message
	@Test
	public void testUpdateProductEmptyProductPayload() {
		assertThatThrownBy(() -> productController.updateProduct("13860428", null)).isInstanceOf(ProductException.class)
				.hasMessage("product payload is required");
	}

	//UpdateProduct call with id and payload but missmatch ids. Validation must fail and must put validation error message
	@Test
	public void testUpdateProductMissMatchProductId() {
		Product product = new Product();

		assertThatThrownBy(() -> productController.updateProduct("13860428", product))
				.isInstanceOf(ProductException.class)
				.hasMessage("invalid payload, payload product id and request product id are not matching");
	}

	//UpdateProduct call with no product for Id. There should not be update.Response status must be NOT_FOUND
	@Test
	public void testUpdateProductNoProductFound() {
		ProductCreator productCreator = new ProductCreator();
		Mockito.when(productDomainServiceMock.getProductsById(Mockito.eq("13860428"))).thenReturn(null);
		Product updateProduct = productCreator.create("13860428", "", "39.02", "USD");
		ResponseEntity<Product> response = productController.updateProduct("13860428", updateProduct);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	//Successfull UpdateProduct.Response status must be OK and updated product
	@Test
	public void testUpdateProductProductFound() {
		ProductCreator productCreator = new ProductCreator();
		Product expectedProduct = productCreator.create("13860428", "", "123", "USD");
		Mockito.when(productDomainServiceMock.getProductsById(Mockito.eq("13860428"))).thenReturn(expectedProduct);
		Product updateProduct = productCreator.create("13860428", "", "39.02", "USD");
		ResponseEntity<Product> response = productController.updateProduct("13860428", updateProduct);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}


}
