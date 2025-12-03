# Invoice Approval System

Invoice Creation and Approval System implemented with SOAP, ActiveMQ, and Hibernate using Jakarta EE.

## Project Overview

This system implements a three-layer architecture for invoice management:

- **API Layer**: SOAP Web Services (JAX-WS) for external communication
- **Messaging Layer**: ActiveMQ for asynchronous processing and approval queue
- **Data Layer**: Hibernate/JPA for persistent data management with relational database

## Technology Stack

- **Java**: 17
- **Jakarta EE**: 10.0.0
- **SOAP**: JAX-WS 4.0.1
- **Messaging**: ActiveMQ 5.18.0
- **ORM**: Hibernate 6.2.7
- **Database**: H2 (in-memory)
- **Build Tool**: Maven 3.x

## Project Structure

```
invoice-approval-system/
├── pom.xml                                    # Parent POM
├── invoice-approval-data/                     # Data Layer
│   ├── src/main/java/
│   │   └── com/invoice/approval/data/
│   │       ├── entity/                        # JPA Entities
│   │       └── repository/                    # Data Repositories
│   └── src/main/resources/
│       └── META-INF/
│           └── persistence.xml                # JPA Configuration
├── invoice-approval-messaging/                # Messaging Layer
│   └── src/main/java/
│       └── com/invoice/approval/messaging/
│           ├── config/                        # ActiveMQ Configuration
│           ├── model/                         # Message Models
│           ├── producer/                      # Message Producer
│           └── consumer/                      # Message Consumer
├── invoice-approval-api/                      # API Layer
│   └── src/main/java/
│       └── com/invoice/approval/api/
│           ├── model/                         # SOAP Request/Response Models
│           ├── service/                       # Business Logic
│           └── soap/                          # SOAP Web Services
└── invoice-approval-web/                      # Web Application
    ├── src/main/webapp/
    │   ├── WEB-INF/
    │   │   ├── web.xml                       # Web Application Configuration
    │   │   ├── beans.xml                     # CDI Configuration
    │   │   └── sun-jaxws.xml                 # JAX-WS Configuration
    │   └── index.html                        # Welcome Page
    └── src/main/java/
        └── com/invoice/approval/web/
            └── ApplicationConfig.java         # Application Configuration
```

## Prerequisites

1. **Java Development Kit (JDK)**: Version 17 or higher
2. **Maven**: Version 3.6 or higher
3. **ActiveMQ**: Version 5.18.0 or higher (or use embedded broker)
4. **Jakarta EE Application Server**: 
   - Payara Server
   - WildFly
   - GlassFish
   - Or any Jakarta EE 10 compatible server

## Setup Instructions

### 1. Clone and Build the Project

```bash
cd invoice-approval-system
mvn clean install
```

### 2. Start ActiveMQ

#### Option A: Standalone ActiveMQ
Download and start ActiveMQ:
```bash
# Download ActiveMQ from https://activemq.apache.org/
# Extract and run:
./bin/activemq start
```

#### Option B: Embedded ActiveMQ (for development)
The application can be configured to use an embedded broker.

### 3. Configure Database

The project uses H2 in-memory database by default. Configuration is in:
- `invoice-approval-data/src/main/resources/META-INF/persistence.xml`

To use a different database, update the persistence.xml file.

### 4. Deploy to Application Server

#### Payara Server
```bash
# Copy WAR file to Payara deployments directory
cp invoice-approval-web/target/invoice-approval-system.war $PAYARA_HOME/glassfish/domains/domain1/autodeploy/
```

#### WildFly
```bash
# Copy WAR file to WildFly deployments directory
cp invoice-approval-web/target/invoice-approval-system.war $WILDFLY_HOME/standalone/deployments/
```

### 5. Access the Application

- **Web Interface**: http://localhost:8080/invoice-approval-system/
- **SOAP WSDL**: http://localhost:8080/invoice-approval-system/services/InvoiceWebService?wsdl
- **SOAP Endpoint**: http://localhost:8080/invoice-approval-system/services/InvoiceWebService

## SOAP API Documentation

### Endpoints

**Base URL**: `http://localhost:8080/invoice-approval-system/services/InvoiceWebService`

### Operations

#### 1. createInvoice

Creates a new invoice and sends it to the approval queue.

**Request**:
```xml
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
  <soap:Body>
    <createInvoice xmlns="http://api.approval.invoice.com/">
      <createInvoiceRequest>
        <invoiceNumber>INV-2024-001</invoiceNumber>
        <vendorName>Acme Corporation</vendorName>
        <amount>1500.00</amount>
        <createdBy>John Doe</createdBy>
        <description>Monthly service invoice</description>
      </createInvoiceRequest>
    </createInvoice>
  </soap:Body>
</soap:Envelope>
```

**Response**:
```xml
<soap:Envelope>
  <soap:Body>
    <createInvoiceResponse>
      <success>true</success>
      <invoiceId>1</invoiceId>
      <invoiceNumber>INV-2024-001</invoiceNumber>
      <message>Invoice created successfully and sent for approval</message>
    </createInvoiceResponse>
  </soap:Body>
</soap:Envelope>
```

#### 2. approveInvoice

Approves an invoice.

**Request**:
```xml
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
  <soap:Body>
    <approveInvoice xmlns="http://api.approval.invoice.com/">
      <approveInvoiceRequest>
        <invoiceId>1</invoiceId>
        <approverName>Manager</approverName>
        <comments>Approved for payment</comments>
      </approveInvoiceRequest>
    </approveInvoice>
  </soap:Body>
</soap:Envelope>
```

#### 3. rejectInvoice

Rejects an invoice.

**Request**:
```xml
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
  <soap:Body>
    <rejectInvoice xmlns="http://api.approval.invoice.com/">
      <rejectInvoiceRequest>
        <invoiceId>1</invoiceId>
        <approverName>Manager</approverName>
        <comments>Incorrect amount</comments>
      </rejectInvoiceRequest>
    </rejectInvoice>
  </soap:Body>
</soap:Envelope>
```

## Testing with Postman

1. Import the WSDL URL in Postman:
   - `http://localhost:8080/invoice-approval-system/services/InvoiceWebService?wsdl`

2. Create a new SOAP request:
   - Method: POST
   - URL: `http://localhost:8080/invoice-approval-system/services/InvoiceWebService`
   - Headers: `Content-Type: application/soap+xml`
   - Body: Use the SOAP request examples above

## System Flow

1. **Invoice Creation**:
   - Client calls `createInvoice` SOAP service
   - Invoice is saved to database with status `IN_APPROVAL`
   - Invoice message is sent to ActiveMQ approval queue

2. **Approval Processing**:
   - Message consumer receives invoice from ActiveMQ queue
   - Approval workflow is processed
   - Invoice status is updated in database

3. **Approval/Rejection**:
   - Manager calls `approveInvoice` or `rejectInvoice` SOAP service
   - Invoice status is updated
   - Approval message is sent to ActiveMQ queue

## Database Schema

### Invoice Table
- `id` (Primary Key)
- `invoice_number` (Unique)
- `vendor_name`
- `amount`
- `status` (PENDING, IN_APPROVAL, APPROVED, REJECTED)
- `created_by`
- `created_at`
- `approved_by`
- `approved_at`
- `description`

### Approval Table
- `id` (Primary Key)
- `invoice_id` (Foreign Key)
- `approver_name`
- `decision` (APPROVED, REJECTED, PENDING)
- `decision_date`
- `comments`

## Development

### Building Individual Modules

```bash
# Build data layer
cd invoice-approval-data
mvn clean install

# Build messaging layer
cd invoice-approval-messaging
mvn clean install

# Build API layer
cd invoice-approval-api
mvn clean install

# Build web application
cd invoice-approval-web
mvn clean package
```

### Running Tests

```bash
mvn test
```

## Configuration

### ActiveMQ Configuration

Default configuration in `ActiveMQConfig.java`:
- Broker URL: `tcp://localhost:61616`
- Queue Name: `invoice.approval.queue`

To change, modify:
- `invoice-approval-messaging/src/main/java/com/invoice/approval/messaging/config/ActiveMQConfig.java`

### Database Configuration

Default H2 configuration in `persistence.xml`:
- JDBC URL: `jdbc:h2:mem:invoice_approval_db`
- Username: `sa`
- Password: (empty)

To use a different database, update:
- `invoice-approval-data/src/main/resources/META-INF/persistence.xml`

## Troubleshooting

### ActiveMQ Connection Issues
- Ensure ActiveMQ broker is running on `localhost:61616`
- Check firewall settings
- Verify ActiveMQ configuration

### SOAP Service Not Available
- Check application server logs
- Verify WAR file is deployed correctly
- Ensure JAX-WS dependencies are included

### Database Issues
- Check H2 database is accessible
- Verify persistence.xml configuration
- Check entity manager factory initialization

## Team Responsibilities

Each team member is responsible for one layer:

1. **Data Layer Developer**: Hibernate/JPA entities, repositories, database configuration
2. **Messaging Layer Developer**: ActiveMQ integration, message producers/consumers
3. **API Layer Developer**: SOAP services, request/response models, business logic

## License

This project is developed for educational purposes.

## Authors

- Group-13
  - Eren Karakaş
  - İsmail Ambarkütük
  - Metehan Kartop

