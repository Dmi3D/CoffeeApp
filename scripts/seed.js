#!/usr/bin/env node
//
// Usage:
//   node mockOrders.js db    # generate + INSERT 1000 mock orders into Postgres
//   node mockOrders.js os    # BULK‐INDEX those orders from Postgres into OpenSearch
//

const { Client: PgClient } = require('pg');
const { Client: OsClient } = require('@opensearch-project/opensearch');
const faker = require('faker'); // npm install faker

const mode = process.argv[2];
if (!['db', 'os'].includes(mode)) {
    console.error('▶️  Usage: node mockOrders.js <db|os>');
    process.exit(1);
}

// --- Postgres client config
const pg = new PgClient({
    host:     'localhost',
    port:     5432,
    database: 'coffee_orders',
    user:     'coffee',
    password: 'coffee',
});

// --- OpenSearch client config
const os = new OsClient({
    node: 'http://localhost:9200'
});

async function seedDb() {
    await pg.connect();
    console.log('🗄  Connected to Postgres, truncating orders table…');
    await pg.query(`TRUNCATE orders;`);

    const insertSQL = `
    INSERT INTO orders(
      id, customer_name, coffee_type, milk_type,
      num_shots, syrups, status,
      created_at, completed_at, cancelled_at, updated_at
    ) VALUES (
      gen_random_uuid(), $1, $2, $3,
      $4, $5, $6,
      $7, $8, $9, now()
    )
  `;

    console.log('☕ Generating + inserting 1000 mock orders…');
    for (let i = 0; i < 1000; i++) {
        const created = faker.date.between(
            new Date(Date.now() - 90*24*60*60*1000),
            new Date()
        );
        const isCancelled = Math.random() < 0.15;   // ~15% cancelled
        const status      = isCancelled ? 'cancelled' : 'completed';
        const completed   = isCancelled
            ? null
            : new Date(created.getTime() + (2 + Math.random()*5)*60*1000); // 2–7m later

        await pg.query(insertSQL, [
            faker.name.firstName(),
            faker.helpers.randomize(['espresso','latte','cappuccino','mocha','americano']),
            faker.helpers.randomize(['whole','skim','oat','almond']),
            faker.datatype.number({ min:1, max:3 }),
            faker.helpers.shuffle(['vanilla','caramel','hazelnut']).slice(0, faker.datatype.number({max:2})),
            status,
            created,
            completed,
            isCancelled ? created : null,
        ]);
    }

    console.log('✅ Seeded Postgres with 1000 orders.');
    await pg.end();
}

async function seedOs() {
    await pg.connect();
    console.log('🔍 Fetching all orders from Postgres…');
    const { rows } = await pg.query(`SELECT * FROM orders`);
    await pg.end();

    console.log(`📦 Bulk‐indexing ${rows.length} orders into OpenSearch…`);
    const body = [];

    for (const o of rows) {
        body.push({ index: { _index: 'orders', _id: o.id } });
        body.push({
            id:           o.id,
            customerName: o.customer_name,
            coffeeType:   o.coffee_type,
            milkType:     o.milk_type,
            numShots:     o.num_shots,
            syrups:       o.syrups,
            status:       o.status,
            createdAt:    o.created_at,
            completedAt:  o.completed_at,
            cancelledAt:  o.cancelled_at,
            updatedAt:    o.updated_at
        });
    }

    const { body: bulkResponse } = await os.bulk({ refresh: true, body });
    if (bulkResponse.errors) {
        console.error('❌ Errors encountered during bulk index:', bulkResponse.items);
    } else {
        console.log('✅ Successfully bulk‐indexed all orders.');
    }
}

;(async () => {
    try {
        if (mode === 'db')  await seedDb();
        if (mode === 'os')  await seedOs();
    } catch (err) {
        console.error(err);
        process.exit(1);
    }
})();