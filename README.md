# 2022-Zendesk-coding-challenge

## Problem Statement
- Show all tickets in list view
- Display individual ticket
- Page through when tickets is more than 25

## Design
- As the API for tickets is already paginated, pagination when firing an API is not done
- For a good UX experience, it only shows 25 pages in list view
- One can navigate to next ('n') and previous ('p') page.
- You can quit anytime by typing 'quit'

## Requirements
- Java 8
- Apache Maven 3.8.3 (Should work on lower version too)

## How to run
- Method 1
  - Set username and password in config.properties
  - Open the project in an IDE and run Start.java
- Method 2
  - Set username and password in config.properties
  - mvn clean install -Dmaven.test.skip=true
  - java -jar target/zendesk-challenge-1.0-SNAPSHOT.jar

## Data
- Used tickets.json to upload data on zendesk platform
- Used the API responses to write tests
- Added screenshots in the screenshot folder

# Handling of errors
- If the status code of response in anything other than 200, it gives an error but the program continues
- If the user inputs a wrong command, it asks to enter again

