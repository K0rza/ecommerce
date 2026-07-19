# Description

The clean architecture styled e-commerce product service.

In this service there's a high bounded domain centric architectural patterns deployed.

To test this service:
1. Bring up eureka-service
2. Bring up api-gateway-service
3. Create database, such as:
    docker run --name postgres-ecommerce -e POSTGRES_USER=hilhan -e POSTGRES_PASSWORD=qwe123 -e POSTGRES_DB=ecommerce_db -p 5432:5432 -d postgres:latest

4. Send request to api-gateway, the service will route incoming request to product-service and the underlying business-logic will be called. And a product will be added to database.

'''
Invoke-RestMethod -Uri "http://localhost:8080/api/products" `
    -Method Post `
    -Headers @{"Content-Type"="application/json"} `
    -Body '{"title": "Laptop", "sku": "LPT-123", "priceAmount": 100, "currency": "TRY", "stock": 10}'
'''

5. Test the database the above data will be added.

    docker exec -it ecomm-postgres psql -U hilhan -d ecommerce_db -c "SELECT * FROM products;"
    