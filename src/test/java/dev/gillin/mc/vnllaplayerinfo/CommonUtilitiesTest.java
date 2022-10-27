package dev.gillin.mc.vnllaplayerinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CommonUtilitiesTest {
    /**
     * Method under test: {@link CommonUtilities#isValidUUID(String)}
     */
    @Test
    void testIsValidUUID() {
        assertTrue(CommonUtilities.isValidUUID("01234567-89AB-CDEF-FEDC-BA9876543210"));
        assertFalse(CommonUtilities.isValidUUID("foo"));
        assertFalse(
                CommonUtilities.isValidUUID("01234567-89AB-CDEF-FEDC-BA987654321001234567-89AB-CDEF-FEDC-BA9876543210"));
    }

    /**
     * Method under test: {@link CommonUtilities#makeTimeReadable(long, boolean)}
     */
    @Test
    void testMakeTimeReadable() {
        assertEquals("0 second(s)", CommonUtilities.makeTimeReadable(10L, true));
        assertEquals("2562047788015 hour(s)", CommonUtilities.makeTimeReadable(Long.MAX_VALUE, true));
        assertEquals("3 minute(s)", CommonUtilities.makeTimeReadable(216000L, true));
        assertEquals("106751991167 day(s)", CommonUtilities.makeTimeReadable(Long.MAX_VALUE, false));
    }
}

