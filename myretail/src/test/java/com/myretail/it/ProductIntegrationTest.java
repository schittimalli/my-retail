package com.myretail.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.myretail.MyretailApplication;
import com.myretail.ProductCreator;
import com.myretail.domain.product.client.RedSkyServiceClient;
import com.myretail.domain.product.model.Product;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyretailApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class })
public class ProductIntegrationTest {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private RedSkyServiceClient redSkyServiceClient;

	//Set data in embedded MongoDB
	@Before
	public void createMongoData() {
		mongoTemplate.createCollection("product");
		ProductCreator productCreator = new ProductCreator();
		Product product1 = productCreator.create("13860428", "", "123", "USD");
		Product product2 = productCreator.create("15117729", "", "432", "USD");
		mongoTemplate.save(product1);
		mongoTemplate.save(product2);
		Mockito.when(redSkyServiceClient.getRedskyProductName("13860428")).thenReturn("test product name");
		Mockito.when(redSkyServiceClient.getRedskyProductName("15117729")).thenReturn("");
	}

	//Drop collection after test
	@After
	public void deleteMongoData() {
		mongoTemplate.dropCollection("product");
	}

	//Test embedded mongo .
	@Test
	public void testMongo() throws Exception {
		assertNotNull(mongoTemplate);
		assertTrue(mongoTemplate.collectionExists("product"));
	}

	//Integration test for getting product by id
	@Test
	public void testGetProductByIdApi() throws Exception {
		ResponseEntity<Product> responseEntity = restTemplate.getForEntity("/products/13860428", Product.class, "");
		Product productFromApi = responseEntity.getBody();
		assertEquals("13860428", productFromApi.getId());
		assertEquals("test product name", productFromApi.getName());
		assertEquals("123", productFromApi.getCurrent_price().getValue());
		assertEquals("USD", productFromApi.getCurrent_price().getCurrency_code());
	}

	//Integration test for getting product by id with no name from external api call 
	@Test
	public void testGetProductByIdApiNoRedProductName() throws Exception {
		ResponseEntity<Product> responseEntity = restTemplate.getForEntity("/products/15117729", Product.class, "");
		Product productFromApi = responseEntity.getBody();
		assertEquals("15117729", productFromApi.getId());
		assertEquals("", productFromApi.getName());
		assertEquals("432", productFromApi.getCurrent_price().getValue());
		assertEquals("USD", productFromApi.getCurrent_price().getCurrency_code());
	}

	//Integration test for updating product by id and product payload 
	@Test
	public void testUpdateProductApi() throws Exception {
		ProductCreator productCreator = new ProductCreator();
		Product product = productCreator.create("13860428", "", "123", "USD");
		ResponseEntity<Product> responseEntity = restTemplate.exchange(new URI("/products/13860428"), HttpMethod.PUT,
				new HttpEntity<Product>(product), Product.class);
		Product productFromApi = responseEntity.getBody();
		assertEquals("13860428", productFromApi.getId());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("123", productFromApi.getCurrent_price().getValue());
		assertEquals("USD", productFromApi.getCurrent_price().getCurrency_code());
	}
}
