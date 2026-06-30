import { readFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'
import { MongoClient } from 'mongodb'

const uri = process.argv[2] || process.env.MONGODB_URI

if (!uri) {
  console.error('Usage: node run-seed.mjs "<mongodb-uri>"')
  console.error('Or set MONGODB_URI environment variable.')
  process.exit(1)
}

const dbName = 'chatdb'
const collectionName = 'inventory_items'
const replaceExisting = true

const __dirname = dirname(fileURLToPath(import.meta.url))
const catalog = JSON.parse(readFileSync(join(__dirname, 'inventory-catalog.json'), 'utf8'))

const client = new MongoClient(uri)

try {
  await client.connect()
  const collection = client.db(dbName).collection(collectionName)

  if (replaceExisting) {
    const deleted = await collection.deleteMany({})
    console.log(`Cleared ${deleted.deletedCount} existing documents from ${dbName}.${collectionName}`)
  }

  const now = new Date()
  const documents = catalog.map((entry) => {
    const [productId, productName, stock] = entry
    return {
      productId,
      productName,
      stock,
      reserved: 0,
      available: stock > 0,
      updatedAt: now,
    }
  })

  const result = await collection.insertMany(documents, { ordered: true })
  console.log(`Inserted ${result.insertedCount} inventory items into ${dbName}.${collectionName}`)

  const chicken = await collection.findOne({ productId: '10021' })
  const testProduct = await collection.findOne({ productId: '23456' })
  console.log('Sample 10021 (Chicken):', chicken)
  console.log('Sample 23456 (test):', testProduct)
} finally {
  await client.close()
}
