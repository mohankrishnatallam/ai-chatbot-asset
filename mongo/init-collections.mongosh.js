/**
 * Run with: mongosh "mongodb://localhost:27017" init-collections.mongosh.js
 * Or: mongosh <your-uri> --file mongo/init-collections.mongosh.js
 *
 * Creates collections + validators aligned with:
 * - ai-chatbot-api: AiResponse, Assistant domains, OrderTools, InventoryTools, ReportingTools
 * - ai-chatbot-ui: AuthPage (users), HomePage (sessions + turns)
 *
 * Requires MongoDB 5.0+ for some $jsonSchema keywords (e.g. anyOf on chat_turns.assistantPayload).
 */
const dbName = "asset_chatbot";
const conn = db.getSiblingDB(dbName);

function dropIfExists(name) {
  if (conn.getCollectionNames().includes(name)) {
    conn.getCollection(name).drop();
  }
}

// Set true to recreate from scratch (drops data).
const RESET = false;
if (RESET) {
  ["report_snapshots", "inventory_items", "orders", "chat_turns", "chat_sessions", "users"].forEach(dropIfExists);
}

const strict = { validationLevel: "strict", validationAction: "error" };

if (!conn.getCollectionNames().includes("users")) {
  conn.createCollection("users", {
    validator: {
      $jsonSchema: {
        bsonType: "object",
        required: ["username", "passwordHash", "createdAt", "updatedAt"],
        properties: {
          _id: { bsonType: "objectId" },
          username: { bsonType: "string", minLength: 1 },
          passwordHash: { bsonType: "string", minLength: 1 },
          createdAt: { bsonType: "date" },
          updatedAt: { bsonType: "date" },
        },
        additionalProperties: false,
      },
    },
    ...strict,
  });
}
conn.users.createIndex({ username: 1 }, { unique: true });

if (!conn.getCollectionNames().includes("chat_sessions")) {
  conn.createCollection("chat_sessions", {
    validator: {
      $jsonSchema: {
        bsonType: "object",
        required: ["title", "createdAt", "updatedAt"],
        properties: {
          _id: { bsonType: "objectId" },
          userId: { bsonType: ["objectId", "null"] },
          title: { bsonType: "string", minLength: 1 },
          createdAt: { bsonType: "date" },
          updatedAt: { bsonType: "date" },
        },
        additionalProperties: false,
      },
    },
    ...strict,
  });
}
conn.chat_sessions.createIndex({ userId: 1, updatedAt: -1 });
conn.chat_sessions.createIndex({ updatedAt: -1 });

if (!conn.getCollectionNames().includes("chat_turns")) {
  conn.createCollection("chat_turns", {
    validator: {
      $jsonSchema: {
        bsonType: "object",
        required: ["sessionId", "question", "answerText", "sequence", "createdAt"],
        properties: {
          _id: { bsonType: "objectId" },
          sessionId: { bsonType: "objectId" },
          question: { bsonType: "string", minLength: 1 },
          answerText: { bsonType: "string" },
          assistantPayload: {
            anyOf: [
              { bsonType: "null" },
              {
                bsonType: "object",
                required: ["type", "status", "message"],
                properties: {
                  type: { enum: ["ORDER", "INVENTORY", "REPORT", "INFO", "ERROR"] },
                  status: { enum: ["SUCCESS", "FAILED"] },
                  data: {
                    bsonType: ["object", "array", "string", "double", "int", "long", "bool", "null"],
                  },
                  message: { bsonType: "string" },
                },
                additionalProperties: false,
              },
            ],
          },
          sequence: { bsonType: "int", minimum: 0 },
          createdAt: { bsonType: "date" },
        },
        additionalProperties: false,
      },
    },
    ...strict,
  });
}
conn.chat_turns.createIndex({ sessionId: 1, sequence: 1 }, { unique: true });
conn.chat_turns.createIndex({ sessionId: 1, createdAt: 1 });

if (!conn.getCollectionNames().includes("orders")) {
  conn.createCollection("orders", {
    validator: {
      $jsonSchema: {
        bsonType: "object",
        required: ["orderId", "quantity", "status", "createdAt", "updatedAt"],
        properties: {
          _id: { bsonType: "objectId" },
          orderId: { bsonType: "string", minLength: 1 },
          customerId: { bsonType: ["string", "null"] },
          productId: { bsonType: ["string", "null"] },
          productName: { bsonType: ["string", "null"] },
          quantity: { bsonType: "int", minimum: 1 },
          shippingAddress: { bsonType: ["string", "null"] },
          status: { bsonType: "string", minLength: 1 },
          totalPrice: { bsonType: ["double", "int", "long", "null"] },
          createdAt: { bsonType: "date" },
          updatedAt: { bsonType: "date" },
        },
        additionalProperties: false,
      },
    },
    ...strict,
  });
}
conn.orders.createIndex({ orderId: 1 }, { unique: true });
conn.orders.createIndex({ customerId: 1, createdAt: -1 });
conn.orders.createIndex({ productId: 1 });

if (!conn.getCollectionNames().includes("inventory_items")) {
  conn.createCollection("inventory_items", {
    validator: {
      $jsonSchema: {
        bsonType: "object",
        required: ["productId", "stock", "reserved", "available", "updatedAt"],
        properties: {
          _id: { bsonType: "objectId" },
          productId: { bsonType: "string", minLength: 1 },
          stock: { bsonType: "int", minimum: 0 },
          reserved: { bsonType: "int", minimum: 0 },
          available: { bsonType: "bool" },
          updatedAt: { bsonType: "date" },
        },
        additionalProperties: false,
      },
    },
    ...strict,
  });
}
conn.inventory_items.createIndex({ productId: 1 }, { unique: true });

if (!conn.getCollectionNames().includes("report_snapshots")) {
  conn.createCollection("report_snapshots", {
    validator: {
      $jsonSchema: {
        bsonType: "object",
        required: ["reportType", "payload", "generatedAt"],
        properties: {
          _id: { bsonType: "objectId" },
          reportType: { enum: ["SALES", "INVENTORY"] },
          payload: { bsonType: "object" },
          generatedAt: { bsonType: "date" },
          requestedByUserId: { bsonType: ["objectId", "null"] },
        },
        additionalProperties: false,
      },
    },
    ...strict,
  });
}
conn.report_snapshots.createIndex({ reportType: 1, generatedAt: -1 });

print("MongoDB collections ready in database: " + dbName);
