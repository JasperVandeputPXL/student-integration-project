# Exercise introduction

The exercise is to implement the presented integration logic on Camel.

You will start from a base project that already has:
- all the dependencies required for all the steps defined
- a 'hello world' Camel route
- an integration test for Kafka ready to use  

Prerequisites:
- an IDE: Intellij CE or the one of your choice
- Git-bash: install it from https://git-scm.com/downloads (use the default installation options)
- Quarkus CLI: in Git-bash terminal run the "curl" install commands documented on https://quarkus.io/get-started/
- Docker or Podman daemon (Docker desktop or Podman desktop on Windows)

The exercise will make you:

1. Implement a REST API secured with OAuth2 to collect meters data and send it to Kafka.
2. Implement a scheduled API request call and send the response to Kafka.
3. Extra: consume Kafka events en send an notification for each of them. 
4. Extra: error handling.

    [to step 1](exercise-1-step-1) 
