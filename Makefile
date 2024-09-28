# Define a standard target that will be executed by default if a specific target is not specified.
.PHONY: up downv

up:
	docker-compose up -d

downv:
	docker-compose down -v

test:
	mvn clean test

