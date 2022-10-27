package dev.gillin.mc.vnllaplayerinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ErrorsTest {
    /**
     * Method under test: {@link Errors#sqlConnectionExecute()}
     */
    @Test
    void testSqlConnectionExecute() {
        assertEquals("Couldn't execute MySQL statement: ", Errors.sqlConnectionExecute());
    }

    /**
     * Method under test: {@link Errors#sqlConnectionClose()}
     */
    @Test
    void testSqlConnectionClose() {
        assertEquals("Failed to close MySQL connection: ", Errors.sqlConnectionClose());
    }

    /**
     * Method under test: {@link Errors#noSQLConnection()}
     */
    @Test
    void testNoSQLConnection() {
        assertEquals("Unable to retrieve MYSQL connection: ", Errors.noSQLConnection());
    }

    /**
     * Method under test: {@link Errors#noTableFound()}
     */
    @Test
    void testNoTableFound() {
        assertEquals("Database Error: No Table Found", Errors.noTableFound());
    }
}

