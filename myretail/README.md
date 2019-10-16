											Development Technologies
											------------------------
Java
----
	1	Mongo Db
	2	Spring Boot

Test Tools
----------
	1	Junit
	2	Embedded Mongo DB -de.flapdoodle.embed.mongo
	3	Mockito

Create data in MongoDb
----------------------
	use myretail
	db.createCollection("product")
	db.product.insert({"_id":"13860428","current_price":{"value":"13.49","currency_code":"USD"}})
	db.product.insert({"_id":"15117729","current_price":{"value":"17.39","currency_code":"USD"}})
	db.product.insert({"_id":"16483589","current_price":{"value":"31.19","currency_code":"USD"}})
	db.product.insert({"_id":"16696652","current_price":{"value":"27.12","currency_code":"USD"}})
	db.product.insert({"_id":"16752456","current_price":{"value":"57.32","currency_code":"USD"}})
	db.product.insert({"_id":"15643793","current_price":{"value":"45.02","currency_code":"USD"}})
	
-------------------------------------------------------------------------------------------------------------------
Build
-----
	1	Run Maven clean Install .This will create artifacts in Target.

-----------------------------------------------------------------------------------------------------------------------------------------------------
																	Testing
																	-------
Unit testing
--------------
	1	RedSkyServiceClientTest - Various test cases are written for testing external API call for product name
	2	ProductDomainServiceTest- Various test cases are written for testing service layer for getting product
	3	ProductControllerTest -Various test cases are written for testing request validations,response data,response http status and exceptions 
Integration Test
------------------
	1 ProductIntegrationTest - Test case for running get and update apis on embedded MongoDB.
	
------------------------------------------------------------------------------------------------------------------------------------------------------------
									Run
									----
Run this jar by using folllowing command
-----------------------------------------
 	1	cd to target directory 
 	2 	java -jar myretail-0.0.1-SNAPSHOT.jar
 	
call api from postman
----------------------
                             GET product
                             ---------
	 1	make this http get call - http://localhost:8080/products/13860428
	 2	Resonse :you get following response
			{
			    "id": "13860428",
			    "name": "The Big Lebowski (Blu-ray)",
			    "current_price": {
			        "value": "13.49",
			        "currency_code": "USD"
			    }
			}
	3	Response Status : 200
    
   If no data for product ID
  
    1	If there is no product found for id , response status will be 404(not found)
 -------------------------------------------------------------------------------------------------------------------------------------------------------
     
								     update call
								     -----------
       
      1	 make put call to http://localhost:8080/products/13860428
      2   request
		       	{
		    		"id": "13860428",
		    		"name": "",
		    		"current_price": {
		        	"value": "7.39",
		        "currency_code": "USD"
		    	}
		}
		3 Response 
				{
		    		"id": "13860428",
		    		"name": "",
		    		"current_price": {
		        	"value": "7.39",
		        	"currency_code": "USD"
		    }
		}
		4 Response Status
			: 200
		
		If no data for product ID
			If there is no product found for id , response status will be 404(not found)
			
If there are validation or internal errors for request , it will return in response.
       -------------------------------------------------------------------
       
 Environment properties are managed by application.properties and application-dev.properties.
 Logs are managed by logback-spring.xml