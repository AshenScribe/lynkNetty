package org.example.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

    @Test
    void testTypeValues() {
        assertEquals(2, Type.values().length);
        assertSame(Type.BASIC, Type.valueOf("BASIC"));
        assertSame(Type.TOKEN, Type.valueOf("TOKEN"));
    }

    @Test
    void testInvalidTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Type.valueOf("INVALID");
        });
    }

    @Test
    void testTypeOrdinals() {
        assertEquals(0, Type.BASIC.ordinal());
        assertEquals(1, Type.TOKEN.ordinal());
    }

    @Test
    void testTypeNames() {
        assertEquals("BASIC", Type.BASIC.name());
        assertEquals("TOKEN", Type.TOKEN.name());
    }
}