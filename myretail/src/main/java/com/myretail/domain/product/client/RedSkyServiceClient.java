package com.myretail.domain.product.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.domain.product.exception.ProductException;

@Service
public class RedSkyServiceClient {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Value("${redsky.target.path}")
	String redSkyTargetPath;

	@Value("${redsky.target.request}")
	String redSkyTargetRequest;

	private Logger LOG = LoggerFactory.getLogger(RedSkyServiceClient.class);

	RedSkyServiceClient() {
	}

	RedSkyServiceClient(RestTemplate restTemplate, ObjectMapper objectMapper, String redSkyTargetPath,
			String redSkyTargetRequest) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
		this.redSkyTargetPath = redSkyTargetPath;
		this.redSkyTargetRequest = redSkyTargetRequest;
	}
	
	// Making External api call for fetching product name
	public String getRedskyProductName(String productId) {
		String uriPath = redSkyTargetPath + productId + redSkyTargetRequest;
		LOG.info("Red SKy product url {}", uriPath);
		String redSkyProductName = "";
		String redSkyProduct = "";
		try {
			URI uri = new URI(uriPath);
			// external api call
			ResponseEntity<String> redSkyProductResponse = restTemplate.getForEntity(uri, String.class);
			if (redSkyProductResponse.getStatusCode() == HttpStatus.OK) {
				redSkyProduct = redSkyProductResponse.getBody();
				// parsing Json (product.item.product_description.title - > product name)
				JsonNode root = objectMapper.readTree(redSkyProduct);
				if (root.findValue("product") != null) {
					root = root.findValue("product");
					if (root.findValue("item") != null) {
						root = root.findValue("item");
						if (root.findValue("product_description") != null) {
							root = root.findValue("product_description");
							if (root.findValue("title") != null) {
								redSkyProductName = root.findValue("title").asText();
								LOG.info("Red sky product name {}", redSkyProductName);
							}
						}
					}
				}

			} else {
				LOG.error("red sky exeption ,HTTPStatus {}", redSkyProductResponse.getStatusCode());
			}
		} catch (URISyntaxException e) {
			LOG.error("invalid redSky URL - {}", uriPath);
			throw new ProductException("invalid redSky URL - " + e.getMessage());
		} catch (IOException e) {
			LOG.error("redSky respose parsing failed - {}", redSkyProduct);
			throw new ProductException("redSky respose parsing failed - " + e.getMessage());
		} catch (HttpClientErrorException e) {
			LOG.error("call to red sky failed ,HTTPStatus {}", e.getMessage());
		} catch (Exception e) {
			LOG.error("redSky call failed - - {}", e.getMessage());
			throw new ProductException("redSky call failed - " + e.getMessage());
		}
		return redSkyProductName;
	}
}
