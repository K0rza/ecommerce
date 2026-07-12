# Preliminary

API Gateway handles to routing requestes amoung services. In kubernetes and container world the ip adresses of pods and containers are dynamic which means that a pod or docker may die and invoke with different IP address and port. At this point the request must be routed dynamically and api-gateway copes such issues.

# Description

In this project, the basic routing service is added at application.yml file. When we surf on the local tomcat server as google-search endpoint, API-Gateway will redirect it to google.com automatically.