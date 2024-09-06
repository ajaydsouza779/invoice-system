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
      "id": "1234"
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
        "status": "pending|paid|void"
      }
    ]
    ```

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



## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/ajaydsouza779/invoice-system.git

2. Navigate to the project directory:
   ```bash
   cd invoice-system

3. Install the dependencies:
   ```bash
   mvn install
   
4. Run the application as below from command prompt:
   ```bash
   mvn spring-boot:run

## Usage
- Access the application at `http://localhost:8080` (or your configured host/port).
- Use a tool like Postman or cURL to interact with the APIs.

## Contact
If you have any questions, feel free to open an issue or contact me.
