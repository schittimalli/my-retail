package com.myretail.domain.product.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.domain.product.exception.ProductException;

@RunWith(MockitoJUnitRunner.class)
public class RedSkyServiceClientTest {

	RedSkyServiceClient redSkyServiceClient;

	@Mock
	RestTemplate restTemplate;

	@Mock
	ObjectMapper objectMapper;

	@Before
	public void setUp() {

		redSkyServiceClient = new RedSkyServiceClient(restTemplate, objectMapper, "https://redsky.target.com",
				"?exclude=12");
	}

	@Test
	public void testGetRedskyProductNameSuccessCall() throws RestClientException, URISyntaxException, IOException {
		File redSkyJson = new File("src\\test\\java\\com\\myretail\\domain\\product\\client\\redSkyProductJson.json");
		JsonNode redSkyProductRootNode = new ObjectMapper().readTree(redSkyJson);
		String redSkyProduct = redSkyProductRootNode.asText();
		Mockito.when(restTemplate.getForEntity(Mockito.any(URI.class), Mockito.eq(String.class)))
				.thenReturn(new ResponseEntity<String>(redSkyProduct, HttpStatus.OK));
		Mockito.when(objectMapper.readTree(Mockito.eq(redSkyProduct))).thenReturn(redSkyProductRootNode);
		String productName = redSkyServiceClient.getRedskyProductName("13860428");
		assertEquals("The Big Lebowski (Blu-ray)", productName);
	}
	
	@Test
	public void testGetRedskyProductNameFailedCall() throws RestClientException, URISyntaxException, IOException {
		Mockito.when(restTemplate.getForEntity(Mockito.any(URI.class), Mockito.eq(String.class)))
				.thenReturn(new ResponseEntity<String>(HttpStatus.NOT_FOUND));
		String productName = redSkyServiceClient.getRedskyProductName("13860428");
		assertEquals("", productName);
	}
	
	@Test
	public void testGetRedskyProductNameParsedException() throws RestClientException, URISyntaxException, IOException {
		File redSkyJson = new File("src\\test\\java\\com\\myretail\\domain\\product\\client\\redSkyProductJson.json");
		JsonNode redSkyProductRootNode = new ObjectMapper().readTree(redSkyJson);
		String redSkyProduct = redSkyProductRootNode.asText();
		
		Mockito.when(restTemplate.getForEntity(Mockito.any(URI.class), Mockito.eq(String.class)))
				.thenReturn(new ResponseEntity<String>(redSkyProduct, HttpStatus.OK));
		Mockito.when(objectMapper.readTree(Mockito.eq(redSkyProduct))).thenThrow(new IOException("invalid response"));
		assertThatThrownBy(() -> redSkyServiceClient.getRedskyProductName("13860428"))
		.isInstanceOf(ProductException.class)
		.hasMessage("redSky respose parsing failed - invalid response");
		
		
		 
	}

	
	@Test
	public void testGetRedskyProductNameInvalidUri() throws RestClientException, URISyntaxException, IOException {
		 
		RedSkyServiceClient	redSkyServiceClient = new RedSkyServiceClient(restTemplate, objectMapper, "http://target.   @@ @@",
				"?exclude=12");
		assertThatThrownBy(() -> redSkyServiceClient.getRedskyProductName("13860428"))
		.isInstanceOf(ProductException.class)
		.hasMessageContaining("invalid redSky URL - ");
		 
	}
}
