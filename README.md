# CoffeeApp

This repository contains three services, a seed script, and infrastructure definitions to run them together:

- **order-producer**: Dropwizard service that publishes new coffee orders to RabbitMQ
- **order-consumer**: Dropwizard service that consumes messages from RabbitMQ, writes orders to PostgreSQL, indexes into OpenSearch, and exposes both order and analytics endpoints. Schema changes are managed with Liquibase migrations.
- **coffee-client**: React/Next.js frontend for placing orders, viewing pending orders, and displaying serve‑time analytics
- **scripts/seed.js**: Node.js script to bulk‑seed PostgreSQL and OpenSearch with mock orders

## Prerequisites

- Docker & Docker Compose
- Java 17 + Maven
- Node.js & npm

## 1. Start Infrastructure

From the project root run:

```bash
docker-compose up -d
```

This will launch:

- **postgres** (port 5432, user `coffee`, database `coffee_orders`)
- **rabbitmq** (port 5672, vhost `/`, user/password `coffee`/`coffee`)
- **opensearch** (port 9200, single-node, security disabled)

## 2. Apply Database Migrations

Liquibase is configured in **order-consumer**. Run this once (and again only when you add new change‑sets):

```bash
cd order-consumer
docker-compose exec postgres mvn clean package
java -jar target/order-consumer-1.0-SNAPSHOT.jar db migrate config.yml
```

Liquibase records applied migrations in the `databasechangelog` table.

## 3. (Optional) Seed with Mock Data

To generate 1000 realistic orders and bulk‑index into OpenSearch:

```bash
cd scripts
npm install pg @opensearch-project/opensearch faker
node seed.js db      # truncates orders and inserts 1000 orders
node seed.js os      # bulk-indexs orders into OpenSearch
```

## 4. Start Backend Services

### order-producer

```bash
cd order-producer
mvn clean package
java -jar target/order-producer-1.0-SNAPSHOT.jar server config.yml
```

Runs on port **8080**.

### order-consumer

```bash
cd order-consumer
java -jar target/order-consumer-1.0-SNAPSHOT.jar server config.yml
```

Runs on port **8081**.

## 5. Run the Frontend

```bash
cd coffee-client
npm install
npm run dev
```

Open your browser at http://localhost:3000

- **Order Page** → POST `/orders`
- **Pending Orders Page** → GET `/orders` + “Ready”/“Cancel” actions
- **Analytics Page** → GET `/search/time-to-serve` for weekly serve‑time chart

## Cleanup

```bash
docker-compose down
```

Enjoy your ☕ coffee analytics platform!