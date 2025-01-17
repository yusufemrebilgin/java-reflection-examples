package ch05.polymorphism.database;

public class DatabaseClient {
    public boolean storeData(String data) {
        System.out.printf("Data '%s' was successfully stored in the database\n", data);
        return true;
    }
}
