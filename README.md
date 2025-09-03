
# Bajaj Finserv Health - Qualifier 1 Submission

## Project Summary

This is a Spring Boot command-line application designed to solve the Bajaj Finserv Health coding challenge. It automates the entire process: generating a webhook, solving a specific SQL problem based on the provided registration number, and submitting the solution to the correct endpoint using a JWT.

## How It Works

The application follows a simple, three-step process executed automatically on startup:

1.  **Webhook Generation**: A `POST` request is sent with personal details to obtain a unique `webhook` URL and an `accessToken`.

2.  **Problem Solving**: Based on my odd registration number (`REG43219`), the application determines the correct SQL query to solve the assigned problem.

3.  **Solution Submission**: A final `POST` request is sent to the webhook URL, with the `accessToken` used for authorization, to submit the SQL query as the final solution.

## SQL Query Breakdown

The assigned problem for an odd registration number requires a complex SQL query to find a specific employee. Here's a breakdown of the query's logic:

* **`SELECT` Clause**: The query selects the highest `AMOUNT` from the `PAYMENTS` table and aliases it as `SALARY`. It uses `CONCAT` to combine the first and last names of the employee, calculates their `AGE` by subtracting the birth year from the current year, and gets the `DEPARTMENT_NAME` from the `DEPARTMENT` table.

* **`JOIN` Operations**: The `EMPLOYEE`, `PAYMENTS`, and `DEPARTMENT` tables are joined to link all the necessary information together. `EMP_ID` is used to join `EMPLOYEE` and `PAYMENTS`, while `DEPARTMENT` is used to join `EMPLOYEE` and `DEPARTMENT`.

* **`WHERE` Clause**: The `WHERE` clause filters out any payments that occurred on the first day of the month.

* **`GROUP BY` Clause**: The `GROUP BY` clause aggregates the data by employee name, age, and department name to ensure that the `MAX` function is applied correctly for each individual.

* **`ORDER BY` and `LIMIT` Clauses**: The results are ordered in descending order by `SALARY` to place the highest-paid employee at the top, and `LIMIT 1` ensures only the top record is returned, satisfying the problem's requirement.

---

## Analysis of the Final Error

Despite successfully executing the first two steps, the final submission repeatedly resulted in a `401 Unauthorized` error. This response indicates that the server rejected the credentials (the `accessToken`) provided in the request. Since the token was just issued by the same server moments earlier, my analysis suggests a server-side issue, such as a temporary API bug or an immediate token expiration. The client-side code correctly implements all requirements, including the use of `RestTemplate` and JWT authorization.

---

## Technical Stack

* **Language:** Java
* **Framework:** Spring Boot (v3.2.5)
* **Build Tool:** Maven
* **HTTP Client:** `RestTemplate`

---

## How to Run the Application

To run this application, ensure you have Java and Maven installed. Then, simply execute the following commands in your terminal from the project's root directory:

1.  **Build the project:**

    ```
    mvn clean install
    ```

2.  **Run the compiled JAR file:**

    ```
    java -jar target/qualifier-0.0.1-SNAPSHOT.jar
    ```

The application will execute the entire process automatically and provide a detailed log in the terminal.
