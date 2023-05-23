/*
 * This file is generated by jOOQ.
 */
package database;


import database.tables.ConnectionPoolTest;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Connectionpool extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>connectionpool</code>
     */
    public static final Connectionpool CONNECTIONPOOL = new Connectionpool();

    /**
     * The table <code>connectionpool.connection_pool_test</code>.
     */
    public final ConnectionPoolTest CONNECTION_POOL_TEST = ConnectionPoolTest.CONNECTION_POOL_TEST;

    /**
     * No further instances allowed
     */
    private Connectionpool() {
        super("connectionpool", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            ConnectionPoolTest.CONNECTION_POOL_TEST
        );
    }
}
