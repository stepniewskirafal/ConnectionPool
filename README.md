# Custom Connection Pool

Custom Connection Pool is a Java library that provides efficient management of connections to a database. It offers a convenient way to handle database connections in a multi-threaded environment while ensuring optimal resource utilization and minimizing connection-related errors.

## Features

The Custom Connection Pool library offers the following key features:

### 1. Connection Allocation

Upon application startup, the library initializes itself with a predefined number of connections. These connections are immediately available for use, eliminating the need to establish a new connection every time one is required. This approach improves performance by reducing the overhead associated with creating and closing connections.

### 2. Connection Limit Control

The connection pool enforces a maximum limit on the number of connections that can be allocated simultaneously to the database. This limit prevents resource exhaustion and ensures that the system operates within acceptable bounds. If an attempt is made to acquire a connection when the pool has reached its maximum capacity, an appropriate message is returned, indicating that no connections are currently available.

### 3. Regular Cleanup

To optimize resource utilization, the connection pool periodically checks for inactive connections that exceed the initial allocation. These inactive connections are closed and removed from the pool, freeing up system resources for other requests. The cleanup process helps maintain an optimal pool size and prevents the accumulation of unused connections.

## Usage

To utilize the Custom Connection Pool library, follow these steps:

1. Include the library in your project's dependencies.

2. Configure the database connection properties (URL, username, password) in a properties file or any other appropriate configuration method.

3. Initialize the connection pool by creating an instance of the `ConnectionPool` class. This will initialize the pool with a default number of connections.

4. Acquire a connection from the pool by calling the `acquireConnection()` method. This method handles the allocation of an available connection from the pool.

5. Perform database operations using the acquired connection. Once the operations are complete, release the connection back to the pool by calling the `markConnectionFree()` method.

6. When the application shuts down, invoke the `removeAllConnections()` method to close and remove all connections from the pool.

## Example

Here's an example of how to use the Custom Connection Pool library:

```java
import ConnectionPool;

public class MyApp {
    private ConnectionPool connectionPool;

    public void initialize() {
        // Create the connection pool
        connectionPool = new ConnectionPool();

        // Perform other initialization tasks
        // ...
    }

    public void performDatabaseOperation() {
        try {
            // Acquire a connection from the pool
            DBConnection connection = connectionPool.acquireConnection();

            // Use the connection for database operations
            connection
                .getConnection()
                .createStatement()
                .execute("SQL STATEMENT STRING");

            // Release the connection back to the pool
            connectionPool.markConnectionFree(connection);
        } catch (SQLException e) {
            // Handle connection acquisition or database operation errors
            // ...
        }
    }

    public void shutdown() {
        // Close and remove all connections from the pool
        connectionPool.removeAllConnections();

        // Perform other shutdown tasks
        // ...
    }
}
```

In the example above, the `initialize()` method initializes the connection pool, the `performDatabaseOperation()` method acquires a connection from the pool for performing database operations, and the `shutdown()` method cleans up the pool by removing all connections.