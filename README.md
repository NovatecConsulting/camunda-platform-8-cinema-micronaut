## Micronaut Zeebe Cinema
Micronaut Zeebe Cinema is a showcase for the Camunda Cloud platform and the Micronaut Zeebe intergration by Novatec

### Tech Stack
- Zeebe 1.4.x
- Micronaut 3.x
- Java 11

### Techniques
- Zeebe Workers in Micronaut
- BPMN
    - Messages
    - Errors
    - Timer
    - Gateways

## Process Model
<img alt="process model" src="src/main/resources/reserve-tickets.png" width="900">

## How to run it
- setup a Camunda Cloud instance / zeebe engine
    - remote
        - generate a client configuration on your Camunda Cloud account (tab API)
        - add your camunda cloud cluster credentials to the `src/main/resources/application.yml`
    - local
        - start a local containerized zeebe/operate setup (see `docker/docker-compose.yml`)
- start the application
    - run `./gradlew run` from your terminal
      or
    - use your IDE to start the ZeebeCinemaApp
- use a REST client to make a reservation and check it:
    - `POST http://localhost:8089/reservation/movie/{movie}?seats=A1,A2&userId=max.mustermann`
    - `POST http://localhost:8089/reservation/offer/c2a3cb6b-45ab-4840-9b40-b33cdb9df3ae`
- check the log output or Operate for the progress (maybe you need to refine log levels -> Zeebe is very noisy in DEBUG)
- in case alternative seats are offered 
    - you can check your log for an offer link: `To accept these seats, click the following link: ...`
    - clicking the link triggers the message correlation, otherwise the process stops after 2min
- you can also check Operate to see incidents or running instances under http://localhost:8080 -> user/pw is demo/demo
