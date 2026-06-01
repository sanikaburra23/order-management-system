# Order Management System (Dropwizard Backend)

A backend service built using the Dropwizard framework to manage and process items, orders, and payments.


 ### project structure
 order_management_system_dropwizard/
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── b2r/
│   │   │           └── ordermgmt/
│   │   │               ├── core/
│   │   │               │   ├── Item.java
│   │   │               │   ├── Order.java
│   │   │               │   ├── OrderItem.java
│   │   │               │   └── Payment.java
│   │   │               ├── db/
│   │   │               │   ├── ItemDAO.java
│   │   │               │   ├── OrderDAO.java
│   │   │               │   └── PaymentDAO.java
│   │   │               ├── exceptions/
│   │   │               │   ├── InsufficientStockException.java
│   │   │               │   ├── InvalidOrderStateException.java
│   │   │               │   └── ResourceNotFoundException.java
│   │   │               ├── resources/
│   │   │               │   ├── ItemResource.java
│   │   │               │   ├── OrderResource.java
│   │   │               │   └── PaymentResource.java
│   │   │               ├── service/
│   │   │               │   └── OrderService.java
│   │   │               ├── OrderManagementApplication.java
│   │   │               └── OrderManagementConfiguration.java
│   │   └── resources/
│   └── test/
├── .gitignore
├── build.gradle.kts
├── config.yml
├── gradlew
├── gradlew.bat
└── settings.gradle.kts

---

## Architectural Overview
This system is organized using a clean, layered architecture to maintain a strict separation of concerns:

1. **Resources (API Layer)**: Handles HTTP requests and maps endpoints to functions.
2. **Services (Business Logic Layer)**: Coordinates processing rules and keeps the application secure and accurate.
3. **DB (Data Access Layer)**: Interfaces directly with the underlying database tables using JDBI / Hibernate DAOs.
4. **Core (Domain Layer)**: Contains the central data models representing the business entities.

---

## System Class Breakdown

### Application Entry & Configuration
* **`OrderManagementApplication`**: The main entry point of the microservice. It initializes the Dropwizard environment, binds dependencies, and registers the REST resources.
* **`OrderManagementConfiguration`**: De-serializes settings from the `config.yml` file (such as database connection pools and logging configurations) into accessible Java properties.

###  Core Entities (`core`)
These classes serve as the blueprint models for your data tracking and map directly to your database tables:
* **`Item`**: Represents products in the inventory, tracking details like name, price, and current stock level.
* **`Order`**: Tracks individual client transactions, including order timestamps, final cost, and status.
* **`OrderItem`**: Acts as a line-item join entity mapping specific quantities of an `Item` to a parent `Order`.
* **`Payment`**: Details transaction data, recording payment methods, transaction tokens, and payment statuses.

### Data Access Layer (`db`)
Handles all direct SQL querying and persistence interactions:
* **`ItemDAO`**: Controls operations related to inventory tracking (e.g., retrieving items, updating stock counts).
* **`OrderDAO`**: Manages the persistence of whole orders and individual line items into the database.
* **`PaymentDAO`**: Executes database records for logging and processing payments.

###  Business Logic Layer (`service`)
* **`OrderService`**: The core operational engine. It processes order requests, coordinates inventory adjustments via `ItemDAO`, ensures payment records are generated via `PaymentDAO`, and enforces business validations.

###  API Layer (`resources`)
Exposes the backend functionality to frontend systems or clients through secure RESTful endpoints:
* **`ItemResource`**: Exposes HTTP verbs (like `GET`, `POST`, `PUT`) to manage catalog items and check inventory.
* **`OrderResource`**: Provides endpoints to safely submit new orders (`POST`) and retrieve historical order states (`GET`).
* **`PaymentResource`**: Manages transaction submissions and processing interfaces.

### Exception Handlers (`exceptions`)
Custom failure triggers thrown by the business logic to elegantly stop invalid workflows and report descriptive context back to the client:
* **`InsufficientStockException`**: Thrown by the `OrderService` if an order's requested item quantity exceeds the warehouse's available stock.
* **`InvalidOrderStateException`**: Fired if an operation is attempted on an order that cannot support it (e.g., trying to cancel an order that has already shipped).
* **`ResourceNotFoundException`**: Triggered when a requested Item, Order, or Payment ID does not exist in the database.

---

##  Database Schema Design
The corresponding relational layout consists of four tightly-coupled tables:
1. **`items` Table**: Primary records for product inventory (`id`, `name`, `price`, `stock_quantity`).
2. **`orders` Table**: High-level details of a user purchase (`id`, `status`, `total_price`, `created_at`).
3. **`order_items` Table**: A bridging table tracking structural details of a sale (`order_id`, `item_id`, `quantity`).
4. **`payments` Table**: Records validating financial captures linked to the transactions (`id`, `order_id`, `amount`, `status`, `payment_method`).

