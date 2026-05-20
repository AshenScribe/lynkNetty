package org.example.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterObjectTest {

    @Test
    void testRegisterObjectCreation() {
        RegisterObject obj = new RegisterObject("john", "password123");

        assertEquals("john", obj.username());
        assertEquals("password123", obj.password());
    }

    @Test
    void testRegisterObjectEquality() {
        RegisterObject obj1 = new RegisterObject("user", "pass");
        RegisterObject obj2 = new RegisterObject("user", "pass");
        RegisterObject obj3 = new RegisterObject("user", "different");

        assertEquals(obj1, obj2);
        assertNotEquals(obj1, obj3);
    }

    @Test
    void testRegisterObjectWithEmptyStrings() {
        RegisterObject obj = new RegisterObject("", "");

        assertEquals("", obj.username());
        assertEquals("", obj.password());
    }

    @Test
    void testRegisterObjectWithNull() {
        RegisterObject obj = new RegisterObject(null, null);

        assertNull(obj.username());
        assertNull(obj.password());
    }

    @Test
    void testRegisterObjectHashCode() {
        RegisterObject obj1 = new RegisterObject("user", "pass");
        RegisterObject obj2 = new RegisterObject("user", "pass");

        assertEquals(obj1.hashCode(), obj2.hashCode());
    }

    @Test
    void testRegisterObjectToString() {
        RegisterObject obj = new RegisterObject("user", "pass");
        String str = obj.toString();

        assertTrue(str.contains("user"));
        assertTrue(str.contains("pass"));
    }
}