# Custom Connection Pool

This project aims to create a custom connection pool library that provides efficient management of connections to a database.

## Functionality

The library should have the following features:

1. **Connection Allocation**: Upon application startup, the library should initialize itself with a predetermined number of connections (e.g., 5 or 10) that will be readily available when needed.

2. **Connection Limit Control**: The pool should enforce a maximum limit of connections (e.g., 100) to the database. If an attempt is made to acquire another connection beyond this limit, an appropriate message should be returned indicating that it is currently not possible.

3. **Regular Cleanup**: The connection pool should be regularly checked, ideally every minute, to identify and remove inactive connections that exceed the initial limit (e.g., 5-10), thereby freeing up system resources.

4. **Error Handling**: Connections that encounter errors should be destroyed, and new connections should be created in their place if necessary. This proactive approach ensures that connections with exceptions do not continue to generate errors.
