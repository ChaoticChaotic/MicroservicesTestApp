# MicroservicesTestApp

This is test task, required two microservices running in docker env.

What was done:
* added security generating custom jwt tokens for endpoint under protection
* added kafka notifications for notifying user services that order is created
* added unit tests for user service
* added openapi docks for order controller
* added registration and login endpoints under /auth/registration and /auth/login accordingly
* added feign client to communicate between services


What was not done:
* caching with Hazelcast, lack of time to study some docks for integration
* swagger do not seen from docker compos, never use it in docker env
* do not added .sh .bash scripts for starting app, lack of time for testing

How to:
- cd to directory containing proj
- cd /order & /user to build images
- mvn clean install -P "docker" 
- jib:dockerBuild
- cd ..
- docker compose up

