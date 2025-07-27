# CoffeeApp

<img width="3840" height="486" alt="Untitled diagram _ Mermaid Chart-2025-07-26-120843" src="https://github.com/user-attachments/assets/4c65943d-9a1e-43de-9411-b616798fc1dc" />


This repository contains three services and the infrastructure needed to run them together:

- **order-producer**: a Dropwizard service that publishes new coffee orders to RabbitMQ
- **order-consumer**: a Dropwizard service that consumes orders from RabbitMQ and writes them to PostgreSQL, with schema managed by Liquibase migrations
- **coffee-client**: a React/Next.js frontend for placing and viewing orders

## Prerequisites

- Docker & Docker Compose
- Java 17 + Maven
- Node.js & npm

## 1. Start infrastructure

From the project root, run:

docker-compose up -d

This will launch:

- **postgres** (port 5432, user=`coffee`, database=`coffee_orders`)
- **rabbitmq** (port 5672, vhost=`/`, user/password=`coffee`/`coffee`)

## 2. Apply database migrations

All schema changes are tracked by Liquibase in **order-consumer**. From the project root:

```bash
cd order-consumer\
mvn clean package\
java -jar target/order-consumer-1.0-SNAPSHOT.jar db migrate config.yml
```

Liquibase will record which migrations have already been applied, so you only ever need to run this once.

## 3. Start backend services

### order-producer

```bash
cd order-producer\
mvn clean package\
java -jar target/order-producer-1.0-SNAPSHOT.jar server config.yml
```

It runs on port **8080**.

### order-consumer

```bash
cd order-consumer\
java -jar target/order-consumer-1.0-SNAPSHOT.jar server config.yml
```

It runs on port **8081**.

## 4. Run the frontend

```bash
cd coffee-client\
npm install\
npm run dev
```

Open your browser at **[http://localhost:3000](http://localhost:3000)**.

## 5. Test the end-to-end flow

1. Place an order in the frontend → producer publishes to RabbitMQ
2. Consumer picks up the message → inserts into Postgres
3. View Orders page → shows pending orders from the database

## Cleanup

```bash
docker-compose down
```

Stops and removes the Postgres and RabbitMQ containers.

Enjoy ☕!
