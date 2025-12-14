Christmas is coming, and “Santa Claus Christmas delivery service”, aka Santa4You,  is modernizing. 
You can now track Santa Claus's location and when he will deliver your presents under the Christmas Tree.

The following functional requirements are there:

You can enter your address and state your wish list of 3 items.
You can later edit these 3 items (also after rebooting the application).
Changes are made with a two-factor token by mail. (You can simulate the mail sending and get the code from the log)
You declare what you leave for Santa in return, either milk or cookies, as a return gift as by tradition.
The address has some validation checks.
You can track where Santa is(simulate and have some fun).


Once the application is run in local please follow:

Application UI: http://localhost:8080/index.html
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs


To Do Items:

1. Improve logging - not much logging at the moment
2. Add swagger annotations to the api's
3. Postgres integration in-place of in-memory db
4. comprehensive address validations and unit tests
5. Integration tests for all the flows and load testing to test race conditions and concurrency handling
6. Global exception handler and spring retry for token generation tests
7. Add checkstyle and code formatter
