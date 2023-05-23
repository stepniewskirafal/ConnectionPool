/*
 * This file is generated by jOOQ.
 */
package database.tables.records;


import database.tables.ConnectionPoolTest;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ConnectionPoolTestRecord extends TableRecordImpl<ConnectionPoolTestRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>connectionpool.connection_pool_test.id</code>.
     */
    public ConnectionPoolTestRecord setId(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>connectionpool.connection_pool_test.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>connectionpool.connection_pool_test.test</code>.
     */
    public ConnectionPoolTestRecord setTest(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>connectionpool.connection_pool_test.test</code>.
     */
    public String getTest() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return ConnectionPoolTest.CONNECTION_POOL_TEST.ID;
    }

    @Override
    public Field<String> field2() {
        return ConnectionPoolTest.CONNECTION_POOL_TEST.TEST;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getTest();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getTest();
    }

    @Override
    public ConnectionPoolTestRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public ConnectionPoolTestRecord value2(String value) {
        setTest(value);
        return this;
    }

    @Override
    public ConnectionPoolTestRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ConnectionPoolTestRecord
     */
    public ConnectionPoolTestRecord() {
        super(ConnectionPoolTest.CONNECTION_POOL_TEST);
    }

    /**
     * Create a detached, initialised ConnectionPoolTestRecord
     */
    public ConnectionPoolTestRecord(String id, String test) {
        super(ConnectionPoolTest.CONNECTION_POOL_TEST);

        setId(id);
        setTest(test);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised ConnectionPoolTestRecord
     */
    public ConnectionPoolTestRecord(database.tables.pojos.ConnectionPoolTest value) {
        super(ConnectionPoolTest.CONNECTION_POOL_TEST);

        if (value != null) {
            setId(value.getId());
            setTest(value.getTest());
            resetChangedOnNotNull();
        }
    }
}