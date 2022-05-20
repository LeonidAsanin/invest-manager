# invest-manager

## Table of content
* [General info](#General info)
* [Technologies](#Technologies)
* [Launch](#Launch)
* [Developer](#Developer)

## General info
Manager of your investment portfolio.

This project represents account-based web application 
where each user can add their purchases and sales (FIFO acts)
getting calculated profits from the sales and having an 
opportunity to see their list of remaining products with 
calculated possible profits assuming some current prices 
that can be set for each unique purchase.

## Technologies
### Main part of the application
* Java 17
* Spring Boot 2.6.3
* Spring MVC
* Spring Data JPA
* Spring Security
* Thymeleaf
* MySQL relational database

### Test part of the application
* Spring Boot Test (including JUnit 5 and Mockito)
* Spring Security Test
* H2 in-memory database

## Launch
Project is building into an executable **.jar** file. So
you can run it either in command prompt or your other
favorite environment that support JVM for Java 17.

Also, you must set connection to the database (MySQL by default, 
but you can substitute it for any other by changing 
[pom.xml](pom.xml) and [schema.sql]
(src/main/resources/schema.sql) files) in 
[application.properties](src/main/resources/application.properties) 
file.

To access a main page of the application go to
**http://_host_:_port_/invest-manager/** in your internet browser.
Where _host_ and _port_ can be set in 
[application.properties](src/main/resources/application.properties)
file.

## Developer
Application was developed by Leonid Asanin.

Contact info:
* [E-mail] [Leonid Asanin](mailto:l.asanin@mail.ru)
* [GitHub] (https://github.com/LeonidAsanin)