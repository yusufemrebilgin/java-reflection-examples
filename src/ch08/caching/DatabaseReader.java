package ch08.caching;

import java.io.IOException;

interface DatabaseReader {

    void connectToDatabase();

    @Cacheable
    String readCustomerIdByName(String firstName, String lastName) throws IOException;

    int countRowsInCustomersTable();

    void addCustomer(String id, String firstName, String lastName) throws IOException;


}
