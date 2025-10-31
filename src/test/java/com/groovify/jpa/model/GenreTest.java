package com.groovify.jpa.model;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
    void testNoArgsConstructor() {
        Genre genre = new Genre();
        assertNull(genre.getId());
        assertNull(genre.getName());
        assertNull(genre.getClient());
    }

    @Test
    void testAllArgsConstructor() {
        Genre genre = new Genre("Jazz");
        assertEquals("Jazz", genre.getName());
        assertNull(genre.getId());
        assertNull(genre.getClient());
    }

    @Test
    void testSettersAndGetters() {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Pop");

        assertEquals(1L, genre.getId());
        assertEquals("Pop", genre.getName());
    }

    @Test
    void testClientAssociation() {
        Genre genre = new Genre("Rock");

        Client client1 = new Client();
        Client client2 = new Client();

        List<Client> clients = List.of(client1, client2);
        genre.setClients(clients);

        assertEquals(2, genre.getClient().size());
        assertTrue(genre.getClient().contains(client1));
        assertTrue(genre.getClient().contains(client2));
    }

    @Test
    void testToStringOrEqualsBehavior() {
        Genre g1 = new Genre("Reggae");
        Genre g2 = new Genre("Reggae");

        // Since no equals() override exists, they should not be equal
        assertNotEquals(g1, g2);
    }
}