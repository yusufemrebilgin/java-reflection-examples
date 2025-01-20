package ch08.external.impl;

import ch08.external.DatabaseReader;

public class DatabaseReaderImpl implements DatabaseReader {

    @Override
    public int countRowsInTable(String tableName) throws InterruptedException {
        System.out.println("DatabaseReaderImpl - counting rows in table " + tableName);

        Thread.sleep(1000);
        return 50;
    }

    @Override
    public String[] readRow(String sqlQuery) throws InterruptedException {
        System.out.println("DatabaseReaderImpl - Executing SQL query: " + sqlQuery);

        Thread.sleep(1500);
        return new String[] {"column1", "column2", "column3"};
    }

}
