# ACME  Insurance Services

To initialize PostgreSQL databases:
```
CREATE ROLE acme WITH LOGIN PASSWORD 'acme';
CREATE DATABASE products OWNER acme;
CREATE DATABASE fees OWNER acme;
CREATE DATABASE contracts OWNER acme;
```

To load configuration data (products and insurance fees):
```
curl -X POST "http://localhost:8080/products/upload" -H "accept: */*" -H "Content-Type: multipart/form-data" -F "file=@products.csv;type=text/csv"
curl -X POST "http://localhost:8081/fees/upload" -H "accept: */*" -H "Content-Type: multipart/form-data" -F "file=@fees.csv;type=text/csv"
```

To build all microservices:
```
 ./gradlew clean build test
```
 
To run all microservices:
```
java -jar acme-insurance-contracts/build/libs/acme-insurance-contracts-1.0.0.jar
java -jar acme-insurance-fees/build/libs/acme-insurance-fees-1.0.0.jar
java -jar acme-products/build/libs/acme-products-1.0.0.jar
```

To start Swagger UI:
http://localhost:8082/swagger-ui.html