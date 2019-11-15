## Billing appication

# Installation requirements
    1. Maven
    2. Java 11
    
# Installation
```#!bash
$ ./mvn clean install
```

# Run 
Use target directory to run built sources
```#!bash
$ java -jar ./backend/target/backend-0.0.1-SNAPSHOT.jar  
```
or run prebuilt jar file
```#!bash
$ java -jar backend-0.0.1-SNAPSHOT.jar  
```

# Usage
Test accounts are generated on application startup
```#!bash
2019-11-15 17:53:05.171  INFO 73752 --- [           main] c.d.starter.DefaultAccountsStarter       : Test account initialized 6e863a8c-6713-40bb-92de-e3fc5a03f6f9
2019-11-15 17:53:05.172  INFO 73752 --- [           main] c.d.starter.DefaultAccountsStarter       : Test account initialized 1d7a596d-e025-497a-85a9-ff4c1f5927dd
2019-11-15 17:53:05.172  INFO 73752 --- [           main] c.d.starter.DefaultAccountsStarter       : Test account initialized df470d49-4a19-4331-a463-bbef0428b2ea
```
Use this account ids to perform transactions on them. 
You can import POSTMAN collection of API requests from ```Billing.postman_collection.json``` file

Frontend is available under 
[frontend](http://localhost:8080)
