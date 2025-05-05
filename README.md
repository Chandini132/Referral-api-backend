# Referral API Backend with PostgreSQL, JWT, and Exception Handling

A Spring Boot backend for a Referral API, using **PostgreSQL** for persistence, **JWT** for authentication, and comprehensive **exception handling**. It supports user signups, referral tracking, profile completion, and generating a CSV report of referrals using the `opencsv` library.

## Prerequisites
Before you begin, ensure you have the following installed:
- **Java 17**: Required to run the Spring Boot application.
- **Maven**: For building and managing dependencies.
- **PostgreSQL**: Using an AWS RDS instance (e.g., `referral-db`).
- **AWS Account**: For deploying to Elastic Beanstalk.
- **Postman**: For testing API endpoints.

## Setup
Follow these steps to set up the project locally and prepare it for testing or deployment:

1. **Clone the Repository**  
   Clone the project from GitHub to your local machine:
   ```bash
   git clone https://github.com/Chandini132/Referral-api-backend.git
   cd referral-backend
   ```

2. **Configure the Database**  
   - Ensure your AWS RDS PostgreSQL instance (`referral-db`) is running.
   - Create a database named `referral_db`:
     ```sql
     CREATE DATABASE referral_db;
     ```
   - Update the database configuration in `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:postgresql://<your-rds-endpoint>:5432/referral_db
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     ```
   - Replace `<your-rds-endpoint>`, `your_username`, and `your_password` with your RDS details.

3. **Configure JWT Authentication**  
   - Add a secure JWT secret key in `application.properties`:
     ```properties
     jwt.secret=your_jwt_secret_key
     jwt.expiration=3600000
     ```
   - **Note**: Keep the secret key secure and avoid committing it to version control.

4. **Build the Project**  
   Use Maven to build the project:
   ```bash
   mvn clean install
   ```

5. **Run the Application Locally**  
   Start the Spring Boot application:
   ```bash
   java -jar target/referral-backend-1.0.0.jar
   ```
   - The application will run on `http://localhost:8080`.

## Endpoints
This section lists the API endpoints and provides step-by-step instructions to test them using **Postman** or `curl`. Each endpoint is detailed with its purpose, request format, and expected response. The examples use two users: `userA` (the referrer) and `userB` (the referred user).

### **Testing with Postman**
Follow these steps to test the API endpoints using Postman:

1. **Set Up Postman**  
   - Open Postman and create a new collection named `Referral API`.
   - Add a request for each endpoint below.
   - Set the **Base URL** to your deployed application:  
     ```
     http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com
     ```
     Alternatively, use `http://localhost:8080` if testing locally.

2. **Add Authorization for Protected Endpoints**  
   - For endpoints requiring a JWT token (e.g., `/api/users/profile`, `/api/users/{userId}/referrals`, `/api/referrals/report`), add the token to the `Authorization` header:
     - **Header**: `Authorization`
     - **Value**: `Bearer your_jwt_token`
   - Obtain the JWT token by calling the `/api/auth/login` endpoint (see below).

---

### **1. POST /api/users/signup**  
**Purpose**: Sign up a new user (public endpoint). The client must provide a `userId`. Optionally, include a `referralCode` to link the user to a referrer.

#### **Request**  
- **Method**: POST
- **URL**: `http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com/api/users/signup`
- **Headers**:
  - `Content-Type: application/json`
- **Body** (JSON):
  - **Example 1**: Sign up `userA` (the referrer) without a referral code:
    ```json
    {"userId": "userA", "password": "passwordA", "referralCode": null}
    ```
  - **Example 2**: Sign up `userB` (the referred user) with `userA`’s referral code:
    ```json
    {"userId": "userB", "password": "passwordB", "referralCode": "36eb91c1"}
    ```
    - **Note**: Replace `36eb91c1` with the actual referral code generated for `userA` after their signup.

#### **Test with Postman**  
1. Create a new request in the `Referral API` collection.
2. Set the method to `POST` and the URL to the endpoint above.
3. Add the `Content-Type: application/json` header.
4. In the body, select `raw` > `JSON` and paste one of the example bodies.
5. Send the request.
6. For `userA`, note the `referralCode` in the response to use for `userB`’s signup.

#### **Test with curl**  
- **Sign Up `userA` (Referrer)**:
  ```bash
  curl --location 'http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com/api/users/signup' \
  --header 'Content-Type: application/json' \
  --data '{"userId": "userA", "password": "passwordA", "referralCode": null}'
  ```
- **Sign Up `userB` (Referred User)**:
  ```bash
  curl --location 'http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com/api/users/signup' \
  --header 'Content-Type: application/json' \
  --data '{"userId": "userB", "password": "passwordB", "referralCode": "36eb91c1"}'
  ```

#### **Expected Response**  
- **Status**: `200 OK`
- **Body** (JSON):
  - For `userA`:
    ```json
    {"userId": "userA", "referralCode": "36eb91c1", "profileCompleted": false, "password": "...", "role": "USER"}
    ```
  - For `userB`:
    ```json
    {"userId": "userB", "referralCode": "generated-code", "profileCompleted": false, "password": "...", "role": "USER"}
    ```

---

### **2. POST /api/auth/login**  
**Purpose**: Authenticate a user and obtain a **JWT token** for accessing protected endpoints.

#### **Request**  
- **Method**: POST
- **URL**: `http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com/api/auth/login`
- **Headers**:
  - `Content-Type: application/json`
- **Body** (JSON):
  - For `userA`:
    ```json
    {"userId": "userA", "password": "passwordA"}
    ```

#### **Test with Postman**  
1. Create a new request in the `Referral API` collection.
2. Set the method to `POST` and the URL to the endpoint above.
3. Add the `Content-Type: application/json` header.
4. In the body, select `raw` > `JSON` and paste the example body.
5. Send the request.
6. Copy the `token` from the response for use in protected endpoints.

#### **Test with curl**  
```bash
curl --location 'http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com/api/auth/login' \
--header 'Content-Type: application/json' \
--data '{"userId": "userA", "password": "passwordA"}'
```

#### **Expected Response**  
- **Status**: `200 OK`
- **Body** (JSON):
  ```json
  {"token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQSIsInJvbGUiOiJVU0VSIiwiZXhwIjoxNzQ2NDM0MDU5LCJpYXQiOjE3NDY0MzA0NTl9.DCK--G1waiu24kvXjtOuksVwl9qtUYWfcDqXauk6xEzmYR5ieDXaxsSEyn6WJHQNory11ztY6cbR5dOOc1Zavw"}
  ```

---

### **3. POST /api/users/profile**  
**Purpose**: Complete a user’s profile (protected endpoint, requires JWT).

#### **Request**  
- **Method**: POST
- **URL**: `http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com/api/users/profile`
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer your_jwt_token`
- **Body** (JSON):
  - For `userB` (using `userA`’s JWT token, assuming `userA` has permission):
    ```json
    {"userId": "userB"}
    ```

#### **Test with Postman**  
1. Create a new request in the `Referral API` collection.
2. Set the method to `POST` and the URL to the endpoint above.
3. Add the headers:
   - `Content-Type: application/json`
   - `Authorization: Bearer <your_jwt_token>` (replace `<your_jwt_token>` with the token from `/api/auth/login`).
4. In the body, select `raw` > `JSON` and paste the example body.
5. Send the request.

#### **Test with curl**  
```bash
curl --location 'http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com/api/users/profile' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQSIsInJvbGUiOiJVU0VSIiwiZXhwIjoxNzQ2NDM0MDU5LCJpYXQiOjE3NDY0MzA0NTl9.DCK--G1waiu24kvXjtOuksVwl9qtUYWfcDqXauk6xEzmYR5ieDXaxsSEyn6WJHQNory11ztY6cbR5dOOc1Zavw' \
--data '{"userId": "userB"}'
```

#### **Expected Response**  
- **Status**: `200 OK`
- **Body** (JSON):
  ```json
  {"message": "Profile updated successfully for userB"}
  ```

---

### **4. GET /api/users/{userId}/referrals**  
**Purpose**: Retrieve a user’s referrals (protected endpoint, requires JWT).

#### **Request**  
- **Method**: GET
- **URL**: `http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com/api/users/userA/referrals`
- **Headers**:
  - `Authorization: Bearer your_jwt_token`

#### **Test with Postman**  
1. Create a new request in the `Referral API` collection.
2. Set the method to `GET` and the URL to the endpoint above.
3. Add the header:
   - `Authorization: Bearer <your_jwt_token>`
4. Send the request.

#### **Test with curl**  
```bash
curl --location 'http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com/api/users/userA/referrals' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQSIsInJvbGUiOiJVU0VSIiwiZXhwIjoxNzQ2NDM0MDU5LCJpYXQiOjE3NDY0MzA0NTl9.DCK--G1waiu24kvXjtOuksVwl9qtUYWfcDqXauk6xEzmYR5ieDXaxsSEyn6WJHQNory11ztY6cbR5dOOc1Zavw'
```

#### **Expected Response**  
- **Status**: `200 OK`
- **Body** (JSON):
  ```json
  [{"referredUserId": "userB", "signupDate": "2025-05-05T12:00:00Z"}]
  ```

---

### **5. GET /api/referrals/report**  
**Purpose**: Download a CSV report of all referrals (protected endpoint, requires JWT).

#### **Request**  
- **Method**: GET
- **URL**: `http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com/api/referrals/report`
- **Headers**:
  - `Authorization: Bearer your_jwt_token`
  - `Content-Disposition: attachment` (optional, for file download)

#### **Test with Postman**  
1. Create a new request in the `Referral API` collection.
2. Set the method to `GET` and the URL to the endpoint above.
3. Add the headers:
   - `Authorization: Bearer <your_jwt_token>`
   - `Content-Disposition: attachment`
4. Send the request.
5. Save the response as a file (Postman’s “Save Response” feature) named `referral_report.csv`.

#### **Test with curl**  
```bash
curl --location 'http://moksha-api-env.eba-563xwfyc.eu-north-1.elasticbeanstalk.com/api/referrals/report' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQSIsInJvbGUiOiJVU0VSIiwiZXhwIjoxNzQ2NDM0MDU5LCJpYXQiOjE3NDY0MzA0NTl9.DCK--G1waiu24kvXjtOuksVwl9qtUYWfcDqXauk6xEzmYR5ieDXaxsSEyn6WJHQNory11ztY6cbR5dOOc1Zavw' \
--header 'Content-Disposition: attachment' \
--output referral_report.csv
```

#### **Expected Response**  
- **Status**: `200 OK`
- **Content-Type**: `text/csv`
- **File Content** (example):
  ```
  userId,referredUserId,signupDate
  userA,userB,2025-05-05T12:00:00Z
  ```
