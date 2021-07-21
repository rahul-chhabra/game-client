# Game Client
_Author: Rahul Chhabra_

### Purpose

This project is a client for the game server.
It uses REST API to communicate with the server.

### Technologies
- Java 11.0.9
- Spring Boot 2.4.8
- Apache Maven 3.6.3

### How to Compile
From the project root directory, run the below command  
>`mvn clean install`

### How to run Client

_Prerequisite: Game Server is up and running._
<br/>

1. In a console, Player 1 run the command from the project root directory and provide inputs like player id, player name, column to move etc.  
>`java -jar target/game-client-1.0.0.jar`
2. In a separate console, Player 2 run the same command from the project root directory and provide inputs.  
>`java -jar target/game-client-1.0.0.jar`
		

### Future Improvements
1. Add more junit test cases to have a better code coverage and quality.
2. Incorporate SonarQube code quality review comments.
3. Add more validation of user inputs.
4. Retry mechanism, if server is not available.

