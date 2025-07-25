# CoffeeApp

This project includes three services and infrastructure:

* **order-producer**: a Dropwizard service that publishes coffee orders to RabbitMQ
* **order-consumer**: a Dropwizard service that consumes orders from RabbitMQ and writes them to PostgreSQL
* **coffee-client**: a React/Next.js frontend for placing and viewing orders

## Prerequisites

* Docker and Docker Compose
* Java 17 and Maven (for the backend services)
* Node.js and npm (for the frontend)

## 1. Start infrastructure with Docker

From the root folder:

```bash
# spin up Postgres and RabbitMQ
docker-compose up -d
```

This will create containers:

* **postgres**: listening on port 5432, user `coffee`, database `coffee_orders`
* **rabbitmq**: listening on port 5672, default vhost `/`, user `coffee` / `coffee`

## 2. Create the orders table in PostgreSQL

Connect to the Postgres container and create the `orders` table:

```bash
docker exec -it $(docker ps -qf "name=coffee-app_db") psql -U coffee -d coffee_orders
```

At the `psql` prompt, run:

```sql
CREATE TABLE orders (
  id UUID PRIMARY KEY,
  customer_name TEXT NOT NULL,
  coffee_type   TEXT NOT NULL,
  milk_type     TEXT NOT NULL,
  num_shots     INT  NOT NULL,
  syrups        TEXT[] NOT NULL,
  created_at    TIMESTAMP NOT NULL,
  status        TEXT NOT NULL DEFAULT 'pending'
);
```

Exit psql with:

```sql
\q
```

## 3. Run the backend services

### order-producer

```bash
cd order-producer
mvn clean package
java -jar target/order-producer-1.0-SNAPSHOT.jar server config.yml
```

It will start on port 8080.

### order-consumer

```bash
cd order-consumer
mvn clean package
java -jar target/order-consumer-1.0-SNAPSHOT.jar server config.yml
```

It will start on port 8081.

## 4. Run the frontend

```bash
cd coffee-client
npm install
npm run dev
```

Open your browser at `http://localhost:3000`.

## 5. Test the flow

1. In the frontend, place a new order (producer publishes to RabbitMQ).
2. The consumer service will pick it up and insert into Postgres.
3. Refresh the frontend "View Orders" page to see pending orders.

## Cleanup

To stop and remove containers:

```bash
docker-compose down
```
