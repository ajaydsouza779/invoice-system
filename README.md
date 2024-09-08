# Invoice System

## Description
A simple invoicing system that allows users to create, pay, and manage invoices, including the handling of overdue payments. The system provides RESTful APIs for managing invoices, processing payments, and handling overdue invoices with late fees. 

## Features
- **Create Invoices**: Generate new invoices with unique IDs.
- **Pay Invoices**: Record payments against specific invoices.
- **Process Overdue Invoices**: Automatically handle overdue invoices by applying late fees and generating new invoices when needed.

## API Endpoints

### Create an Invoice
- **POST /invoices**
  - **Request Body:**
    ```json
    {
      "amount": 199.99,
      "due_date": "2021-09-11"
    }
    ```
  - **Response:**
    - **Status:** `201 Created`
    - **Body:**
    ```json
    {
     "id": 2,
     "amount": 210000,
     "paidAmount": 0,
     "dueDate": "2023-10-11",
     "status": "PENDING"
    }
    ```

### Get All Invoices
- **GET /invoices**
  - **Response:**
    - **Status:** `200 OK`
    - **Body:**
    ```json
    [
      {
        "id": "1234",
        "amount": 199.99,
        "paid_amount": 0,
        "due_date": "2021-09-11",
        "status": "PENDING|PAID|VOID"
      }
    ]
    ```
- **Get Invoice by ID**
    - **GET** `/invoices/{id}`
    - **Response**:
      ```json
      {
        "id": "1234",
        "amount": 199.99,
        "paid_amount": 0,
        "due_date": "2021-09-11",
        "status": "pending"
      }
      ```
    - **Response Status**: 200 OK
    - **Error Response**:
      ```json
      {
        "status": 404,
        "error": "Invoice not found with id 1234"
      }
      ```
    - **Response Status for Not Found**: 404 Not Found

### Pay an Invoice
- **POST /invoices/{invoice_id}/payments**
  - **Request Body:**
    ```json
    {
      "amount": 159.99
    }
    ```

### Process Overdue Invoices
- **POST /invoices/process-overdue**
  - **Request Body:**
    ```json
    {
      "late_fee": 10.5,
      "overdue_days": 10
    }
    ```

## Running the Application

Clone the repository:
   ```bash
   git clone https://github.com/ajaydsouza779/invoice-system.git
  ````

### Build and run with spring boot

1. Navigate to the project directory:
   ```bash
   cd invoice-system

2. Install the dependencies:
   ```bash
   mvn install
   
3. Run the application as below from command prompt:
   ```bash
   mvn spring-boot:run



### Build and Run with Docker Compose

To build and run the application using Docker Compose, follow these steps:

1. **Ensure Docker and Docker Compose are Installed**

   Make sure Docker and Docker Compose are installed on your machine. You can download them from [Docker's official website](https://www.docker.com/get-started).

2. **Navigate to the Project Directory**

   Open a terminal and navigate to the root directory of the project where the `docker-compose.yml` file is located.

3. **Choose the Docker Compose File**

    - **For In-Memory Database (Dev1 Configuration):**

      If you want to use the in-memory database setup, run the following command:
      ```bash
      docker-compose -f docker-compose.yml -f docker-compose.dev1.yml up --build
      ```

    - **For MySQL Database (Dev2 Configuration):**

      If you want to use the MySQL database setup, make sure to fill in the MySQL credentials in `docker-compose.dev2.yml`, then run:
      ```bash
      docker-compose -f docker-compose.yml -f docker-compose.dev2.yml up --build
      ```

4. **Build and Start the Containers**

   The above commands will build the Docker images for your application and start the containers as defined in the specified Docker Compose files.

5. **Access the Application**

   Once the containers are up and running, the application will be accessible at `http://localhost:8080`.

6. **Stopping the Containers**

   To stop the running containers, use the following command:
   ```bash
   docker-compose down
   ```
   This command will stop and remove the containers, but the data in your volumes will persist.

## Note
- The application defaults to dev1 profile (in-memory h2 db).
- Choose the appropriate Docker Compose file based on your database needs before running the containers.
- Ensure that you fill in the MySQL credentials in docker-compose.dev2.yml and application-dev2.properties before using dev2 profile for mysql credentials.


## Usage
- Access the application at `http://localhost:8080` (or your configured host/port).
- Use a tool like Postman or cURL to interact with the APIs.

## Postman Collection

A Postman collection for testing the API is available in the `postman` directory.

### Importing the Collection

1. Open Postman.
2. Click on the **Import** button.
3. Select the `invoice-system.postman_collection.json` file from the `postman` directory.
4. Click **Open** to import the collection.

### Using the Collection

Once imported, you can use the collection to test the API endpoints and view the example requests and responses.


## Contact
If you have any questions, feel free to open an issue or contact me.
