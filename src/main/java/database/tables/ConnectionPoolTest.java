/*
 * This file is generated by jOOQ.
 */
package database.tables;


import database.Connectionpool;
import database.tables.records.ConnectionPoolTestRecord;

import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ConnectionPoolTest extends TableImpl<ConnectionPoolTestRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of
     * <code>connectionpool.connection_pool_test</code>
     */
    public static final ConnectionPoolTest CONNECTION_POOL_TEST = new ConnectionPoolTest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ConnectionPoolTestRecord> getRecordType() {
        return ConnectionPoolTestRecord.class;
    }

    /**
     * The column <code>connectionpool.connection_pool_test.id</code>.
     */
    public final TableField<ConnectionPoolTestRecord, String> ID = createField(DSL.name("id"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>connectionpool.connection_pool_test.test</code>.
     */
    public final TableField<ConnectionPoolTestRecord, String> TEST = createField(DSL.name("test"), SQLDataType.VARCHAR(255), this, "");

    private ConnectionPoolTest(Name alias, Table<ConnectionPoolTestRecord> aliased) {
        this(alias, aliased, null);
    }

    private ConnectionPoolTest(Name alias, Table<ConnectionPoolTestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>connectionpool.connection_pool_test</code> table
     * reference
     */
    public ConnectionPoolTest(String alias) {
        this(DSL.name(alias), CONNECTION_POOL_TEST);
    }

    /**
     * Create an aliased <code>connectionpool.connection_pool_test</code> table
     * reference
     */
    public ConnectionPoolTest(Name alias) {
        this(alias, CONNECTION_POOL_TEST);
    }

    /**
     * Create a <code>connectionpool.connection_pool_test</code> table reference
     */
    public ConnectionPoolTest() {
        this(DSL.name("connection_pool_test"), null);
    }

    public <O extends Record> ConnectionPoolTest(Table<O> child, ForeignKey<O, ConnectionPoolTestRecord> key) {
        super(child, key, CONNECTION_POOL_TEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Connectionpool.CONNECTIONPOOL;
    }

    @Override
    public ConnectionPoolTest as(String alias) {
        return new ConnectionPoolTest(DSL.name(alias), this);
    }

    @Override
    public ConnectionPoolTest as(Name alias) {
        return new ConnectionPoolTest(alias, this);
    }

    @Override
    public ConnectionPoolTest as(Table<?> alias) {
        return new ConnectionPoolTest(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public ConnectionPoolTest rename(String name) {
        return new ConnectionPoolTest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ConnectionPoolTest rename(Name name) {
        return new ConnectionPoolTest(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public ConnectionPoolTest rename(Table<?> name) {
        return new ConnectionPoolTest(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
