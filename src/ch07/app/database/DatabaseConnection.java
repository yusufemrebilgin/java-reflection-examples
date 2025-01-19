package ch07.app.database;

import ch07.annotation.InitializerClass;
import ch07.annotation.InitializerMethod;
import ch07.annotation.RetryOperation;

import java.io.IOException;

@InitializerClass
public class DatabaseConnection {

    // To simulate retrying attempts
    private int failCounter = 5;

    @InitializerMethod
    @RetryOperation(
            numberOfRetries = 10,
            durationBetweenRetriesInMs = 1_000,
            retryExceptions = IOException.class,
            failureMessage = "Connecting to H2 database is failed after retry operation"
    )
    public void connectToH2() throws IOException {
        System.out.println("Connecting to H2 database");
        if (failCounter > 0) {
            failCounter--;
            throw new IOException("Connection failed");
        }

        System.out.println("Connecting to H2 database is succeed");
    }

    @InitializerMethod
    public void connectToMySQL() {
        System.out.println("Connection to MySQL database");
    }

}
