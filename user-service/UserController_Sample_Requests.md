# User Controller Test - Sample Requests

## Base URL
```
http://localhost:8081/api/user
```

---

## 1. Create User
**Endpoint:** `POST /api/user/create`

**Description:** Create a new user account

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "pwd": "password123",
  "phno": 9876543210,
  "roleName": "ROLE_USER"
}
```

**Expected Response (201 Created):**
```json
{
  "statusCode": 201,
  "message": "User created successfully",
  "data": {
    "userId": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "pwd": "password123",
    "phno": 9876543210,
    "createdDate": "2026-02-12",
    "updatedDate": "2026-02-12",
    "pwdUpdated": "NO",
    "roleName": "ROLE_USER"
  }
}
```

---

## 2. Login User
**Endpoint:** `POST /api/user/login`

**Description:** Login with email and password

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "pwd": "password123"
}
```

**Expected Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "pwd": "password123",
    "phno": 9876543210,
    "createdDate": "2026-02-12",
    "updatedDate": "2026-02-12",
    "pwdUpdated": "NO",
    "roleName": "ROLE_USER"
  }
}
```

---

## 3. Reset Password
**Endpoint:** `POST /api/user/reset-password`

**Description:** Reset user password

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "newPwd": "newpassword456",
  "confirmPwd": "newpassword456"
}
```

**Expected Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Password reset successfully",
  "data": null
}
```

---

## 4. Get User by Email
**Endpoint:** `GET /api/user/email?email=john.doe@example.com`

**Description:** Retrieve user information by email

**Request Parameters:**
- `email` (required): User email address

**Expected Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "User retrieved successfully",
  "data": {
    "userId": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "pwd": "newpassword456",
    "phno": 9876543210,
    "createdDate": "2026-02-12",
    "updatedDate": "2026-02-12",
    "pwdUpdated": "YES",
    "roleName": "ROLE_USER"
  }
}
```

---

## 5. Save Shipping Address
**Endpoint:** `POST /api/user/{userId}/shipping-address`

**Description:** Save shipping address for a user

**Path Parameters:**
- `userId` (required): User ID (e.g., 1)

**Request Body:**
```json
{
  "houseNum": "123",
  "street": "Main Street",
  "city": "New York",
  "state": "NY",
  "pinCode": "10001",
  "country": "USA"
}
```

**Expected Response (201 Created):**
```json
{
  "statusCode": 201,
  "message": "Shipping address saved successfully",
  "data": {
    "addrId": 1,
    "houseNum": "123",
    "street": "Main Street",
    "city": "New York",
    "state": "NY",
    "pinCode": "10001",
    "country": "USA"
  }
}
```

---

## 6. Delete Shipping Address
**Endpoint:** `DELETE /api/user/shipping-address/{shippingAddressId}`

**Description:** Delete shipping address by ID

**Path Parameters:**
- `shippingAddressId` (required): Address ID (e.g., 1)

**Expected Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Shipping address deleted successfully",
  "data": {
    "addrId": 1,
    "houseNum": "123",
    "street": "Main Street",
    "city": "New York",
    "state": "NY",
    "pinCode": "10001",
    "country": "USA"
  }
}
```

---

## cURL Command Examples

### 1. Create User
```bash
curl -X POST http://localhost:8081/api/user/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "pwd": "password123",
    "phno": 9876543210,
    "roleName": "ROLE_USER"
  }'
```

### 2. Login User
```bash
curl -X POST http://localhost:8081/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "pwd": "password123"
  }'
```

### 3. Reset Password
```bash
curl -X POST http://localhost:8081/api/user/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "newPwd": "newpassword456",
    "confirmPwd": "newpassword456"
  }'
```

### 4. Get User by Email
```bash
curl -X GET "http://localhost:8081/api/user/email?email=john.doe@example.com" \
  -H "Content-Type: application/json"
```

### 5. Save Shipping Address
```bash
curl -X POST http://localhost:8081/api/user/1/shipping-address \
  -H "Content-Type: application/json" \
  -d '{
    "houseNum": "123",
    "street": "Main Street",
    "city": "New York",
    "state": "NY",
    "pinCode": "10001",
    "country": "USA"
  }'
```

### 6. Delete Shipping Address
```bash
curl -X DELETE http://localhost:8081/api/user/shipping-address/1 \
  -H "Content-Type: application/json"
```

---

## Testing Steps

1. **Start the Auth Service**
   ```bash
   cd auth-service
   mvn spring-boot:run
   ```

2. **Create a User** - Use sample request #1
3. **Login** - Use sample request #2 to verify user credentials
4. **Reset Password** - Use sample request #3 to update password
5. **Get User** - Use sample request #4 to retrieve user info
6. **Add Shipping Address** - Use sample request #5 (use userId from #1)
7. **Delete Shipping Address** - Use sample request #6 (use addressId from #5)

---

## Error Responses

### 400 Bad Request
```json
{
  "statusCode": 400,
  "message": "Invalid input data",
  "data": null
}
```

### 404 Not Found
```json
{
  "statusCode": 404,
  "message": "User not found",
  "data": null
}
```

### 500 Internal Server Error
```json
{
  "statusCode": 500,
  "message": "Internal server error",
  "data": null
}
```

---

## Notes

- Replace `localhost:8081` with your actual server address if deployed remotely
- Ensure MySQL database is running and accessible
- Email notifications will be sent to Gmail account configured in application.properties
- Default role for new users is `ROLE_USER`
- Password reset requires matching confirm password
