# Preliminary 
Service Discovery is used to introduce the services on the system to a global address book. At this point, Eureka library is used to handle such cases. 

Each service in the application presents itself to Eureka service and Eureka identifies them and creates a table for connection addressses and services. 

At this point, when a request is received by Api-Gateway service, the service will lookup for Euroka service to route such request to revelant service.

# Description
In this project a Netflix Eureka service is created and initilized.