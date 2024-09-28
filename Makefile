# Define a standard target that will be executed by default if a specific target is not specified.
.PHONY: up downv

db-up:
	docker-compose up -d

db-downv:
	docker-compose down -v

test:
	mvn clean test

docker-test:
	docker run --rm \
		-v $(shell pwd):/app \
		-v /var/run/docker.sock:/var/run/docker.sock \
		-w /app \
		maven:3.8.7-eclipse-temurin-17 \
		mvn clean test
