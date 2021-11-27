# 2022-Zendesk-coding-challenge

## Problem Statement
- Show all tickets in list view (Option 1)
- Display individual ticket (Option 2)
- Page through when tickets is more than 25


## Design
![Alt text](Screenshots/zendesk.png?raw=true "Design")

## Design Decisions
- As the API for tickets is already paginated, pagination API is not used
- For a good UX experience, it only shows 25 pages in list view
- One can navigate to next ('n') and previous ('p') page when choosing Option 1
- You can quit anytime by typing 'quit'
- Check out the screenshots in the screenshots folder

### Testing
- Manual / Regression testing
- Wrong error inputs should return error messages accordingly
- When using paginated view (seeing all tickets with a page size of 25), options ('n', 'p', 'exit') should work properly
- Mocked all API responses. The responses should be parsed and saved properly.

## Requirements
- Java 8
- Apache Maven 3.8.3 (Should work on lower version too)

## How to run
- Method 1
  - Set username and password in config.properties
  - Open the project in an IDE and run Start.java
  - If dependencies are not getting resolved, invalidate cache (in IDE) and restart
- Method 2
  - Set username and password in config.properties
  - mvn clean install -Dmaven.test.skip=true
  - java -jar target/zendesk-challenge-1.0-SNAPSHOT.jar

## Data
- Used tickets.json to upload data on zendesk platform
- Used the API responses to write tests

# Handling of errors
- If the status code of response in anything other than 200, it gives an error but the program continues
- If the user inputs a wrong command, it asks to enter again

