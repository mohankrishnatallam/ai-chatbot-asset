/**
 * Seed 100 inventory_items for local / Atlas testing.
 *
 * Schema (see mongo/schemas/inventory_items.collection.json):
 *   productId (string, unique), productName (string), stock, reserved, available, updatedAt
 *
 * Run:
 *   mongosh "<your-mongodb-uri>" --file mongo/seed-inventory-items.mongosh.js
 *
 * Or from mongo folder:
 *   mongosh "mongodb://localhost:27017" --file seed-inventory-items.mongosh.js
 *
 * Set REPLACE_EXISTING = true to delete all inventory_items before insert.
 */
const dbName = "chatdb";
const conn = db.getSiblingDB(dbName);
const REPLACE_EXISTING = true;

// productId, productName, stock (units/kg each)
const catalog = [
  // Fresh produce (10001–10020)
  ["10001", "Organic Bananas 1kg", 320],
  ["10002", "Red Apples 1kg", 280],
  ["10003", "Green Grapes 500g", 190],
  ["10004", "Navel Oranges 1kg", 210],
  ["10005", "Baby Spinach 250g", 145],
  ["10006", "Roma Tomatoes 1kg", 260],
  ["10007", "Yellow Onions 1kg", 300],
  ["10008", "Russet Potatoes 2kg", 240],
  ["10009", "Carrots 1kg", 275],
  ["10010", "Broccoli Crowns 500g", 165],
  ["10011", "Bell Peppers Mixed 3pk", 130],
  ["10012", "Cucumbers 3pk", 155],
  ["10013", "Avocados Hass 4pk", 120],
  ["10014", "Strawberries 400g", 95],
  ["10015", "Blueberries 300g", 88],
  ["10016", "Mushrooms White 250g", 140],
  ["10017", "Garlic Bulbs 3pk", 200],
  ["10018", "Ginger Root 200g", 175],
  ["10019", "Lemons 1kg", 160],
  ["10020", "Watermelon Whole", 45],
  // Meat & poultry (10021–10035) — use productId in orders, e.g. Chicken = 10021
  ["10021", "Chicken Breast Boneless 1kg", 180],
  ["10022", "Chicken Thighs 1kg", 165],
  ["10023", "Chicken Whole 1.5kg", 90],
  ["10024", "Ground Beef 80/20 1kg", 140],
  ["10025", "Beef Sirloin Steak 500g", 75],
  ["10026", "Pork Chops 1kg", 110],
  ["10027", "Lamb Leg 1kg", 55],
  ["10028", "Turkey Breast 1kg", 60],
  ["10029", "Bacon Smoked 400g", 130],
  ["10030", "Sausages Pork 6pk", 145],
  ["10031", "Salmon Fillet 500g", 80],
  ["10032", "Shrimp Peeled 400g", 70],
  ["10033", "Tilapia Fillet 500g", 95],
  ["10034", "Deli Ham Sliced 300g", 120],
  ["10035", "Deli Turkey Sliced 300g", 115],
  // Dairy & eggs (10036–10050)
  ["10036", "Whole Milk 1L", 400],
  ["10037", "Skim Milk 1L", 350],
  ["10038", "Greek Yogurt Plain 500g", 220],
  ["10039", "Cheddar Cheese Block 400g", 180],
  ["10040", "Mozzarella Shredded 300g", 195],
  ["10041", "Butter Unsalted 250g", 210],
  ["10042", "Heavy Cream 500ml", 160],
  ["10043", "Cottage Cheese 500g", 125],
  ["10044", "Cream Cheese 250g", 140],
  ["10045", "Eggs Large 12pk", 320],
  ["10046", "Eggs Organic 12pk", 150],
  ["10047", "Parmesan Grated 200g", 130],
  ["10048", "Sour Cream 400g", 170],
  ["10049", "Almond Milk 1L", 200],
  ["10050", "Oat Milk 1L", 185],
  // Beverages (10051–10065)
  ["10051", "Spring Water 24pk", 500],
  ["10052", "Sparkling Water 12pk", 280],
  ["10053", "Orange Juice 1L", 190],
  ["10054", "Apple Juice 1L", 175],
  ["10055", "Cola 2L", 300],
  ["10056", "Diet Cola 2L", 260],
  ["10057", "Green Tea Bags 100ct", 140],
  ["10058", "Coffee Beans Dark Roast 1kg", 95],
  ["10059", "Instant Coffee 200g", 160],
  ["10060", "Sports Drink 6pk", 210],
  ["10061", "Energy Drink 4pk", 180],
  ["10062", "Coconut Water 1L", 130],
  ["10063", "Lemonade 1.5L", 145],
  ["10064", "Iced Tea Peach 1L", 155],
  ["10065", "Herbal Tea Variety 40ct", 120],
  // Pantry & snacks (10066–10080)
  ["10066", "White Rice 2kg", 240],
  ["10067", "Basmati Rice 1kg", 180],
  ["10068", "Spaghetti Pasta 500g", 320],
  ["10069", "Penne Pasta 500g", 290],
  ["10070", "Olive Oil Extra Virgin 1L", 150],
  ["10071", "Vegetable Oil 1L", 200],
  ["10072", "All-Purpose Flour 2kg", 175],
  ["10073", "Granulated Sugar 1kg", 190],
  ["10074", "Table Salt 1kg", 250],
  ["10075", "Black Pepper Ground 100g", 220],
  ["10076", "Potato Chips Classic 200g", 380],
  ["10077", "Tortilla Chips 300g", 310],
  ["10078", "Chocolate Bar Milk 100g", 420],
  ["10079", "Cookies Chocolate Chip 300g", 290],
  ["10080", "Peanut Butter Creamy 500g", 165],
  // Household (10081–10090)
  ["10081", "Paper Towels 6pk", 200],
  ["10082", "Toilet Paper 12pk", 180],
  ["10083", "Dish Soap 750ml", 240],
  ["10084", "Laundry Detergent 2L", 150],
  ["10085", "Trash Bags 30ct", 175],
  ["10086", "Aluminum Foil 50m", 190],
  ["10087", "Plastic Wrap 100m", 160],
  ["10088", "Sponges 6pk", 210],
  ["10089", "Hand Soap Refill 1L", 145],
  ["10090", "All-Purpose Cleaner 1L", 130],
  // Electronics (10091–10095)
  ["10091", "USB-C Charging Cable 2m", 450],
  ["10092", "Wireless Mouse", 180],
  ["10093", "Bluetooth Earbuds", 95],
  ["10094", "Phone Screen Protector", 320],
  ["10095", "Portable Power Bank 10000mAh", 75],
  // Office supplies (10096–10098)
  ["10096", "A4 Copy Paper 500 sheets", 280],
  ["10097", "Ballpoint Pens 10pk", 400],
  ["10098", "Sticky Notes 12pk", 350],
  // Chat / Postman test product IDs
  ["12345", "Test Product Alpha", 500],
  ["23456", "Test Product Beta", 500],
];

if (REPLACE_EXISTING) {
  conn.inventory_items.deleteMany({});
  print("Cleared inventory_items collection.");
}

const now = new Date();
const documents = catalog.map(([productId, productName, stock]) => ({
  productId,
  productName,
  stock,
  reserved: 0,
  available: stock > 0,
  updatedAt: now,
}));

const result = conn.inventory_items.insertMany(documents, { ordered: true });
print(`Inserted ${result.insertedIds ? Object.keys(result.insertedIds).length : documents.length} inventory items into ${dbName}.inventory_items`);

// Quick sanity check
print("Sample lookup productId 10021 (Chicken Breast):");
printjson(conn.inventory_items.findOne({ productId: "10021" }));
print("Sample lookup productId 23456 (test):");
printjson(conn.inventory_items.findOne({ productId: "23456" }));
