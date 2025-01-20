const { Kafka } = require('kafkajs');
const { SchemaRegistry, SchemaType } = require('@kafkajs/confluent-schema-registry')

module.exports = {
  startConsumer,
  getEvents
};

console.log(config)
const kafka = new Kafka({
  clientId: 'acceptance-test-js',
  brokers: [config.kafkaBrokerUrl],
});

const consumer = kafka.consumer({ groupId: 'acceptanceTest-group' });
const registry = new SchemaRegistry({ host: config.schemaRegistryUrl })

let messages = [];

async function startConsumer() {
  messages = [];
  await consumer.connect();
  // reads from last committed offset
  await consumer.subscribe({ topics: [config.kafkaTopic], fromBeginning: false });
  await consumer.run({ eachMessage: async ({ topic, message }) => {
    message = {
      ...message,
      key: await registry.decode(message.key),
      value: await registry.decode(message.value)
    };
    messages.push(message);
  }});
}

async function getEvents() {
  if (!messages) {
    throw new Error("Call 'startConsumer' before requesting events");
  }
  await consumer.disconnect();
  return messages;
}
