package com.myretail.domain.product.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.myretail.ProductCreator;
import com.myretail.domain.product.client.RedSkyServiceClient;
import com.myretail.domain.product.model.Product;
import com.myretail.domain.product.model.ProductRepository;

@RunWith(MockitoJUnitRunner.class)
public class ProductDomainServiceTest {

	@Mock
	ProductRepository productRepositoryMock;

	@Mock
	RedSkyServiceClient redSkyServiceClientMock;

	ProductDomainService productDomainService;

	@Before
	public void SetUp() {

		productDomainService = new ProductDomainService(productRepositoryMock, redSkyServiceClientMock);
	}

	
	@Test
	public void testGetProductsById() {
		
		ProductCreator productCreator = new ProductCreator();
		Product expectedProduct =productCreator.create("13860428", "","123", "USD");
		Mockito.when(productRepositoryMock.findById(Mockito.eq("13860428"))).thenReturn(Optional.of(expectedProduct));
		Product actualProduct = productDomainService.getProductsById("13860428");
		verify(redSkyServiceClientMock, atLeast(1)).getRedskyProductName("13860428");
		assertEquals(expectedProduct, actualProduct);
	}

}
