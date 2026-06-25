package edu.hebbible.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RepoTest {

    private val repo = Repo()

    @Test
    fun init() {
        repo.init()
        val store = repo.store
        assertEquals(23_204, store.size)

        assertTrue(store.first().text.contains("בראשית ברא"))

        val last = repo.store.last()
        assertEquals(36, last.perek)
        assertEquals(23, last.pasuk)
        assertEquals("דברי הימים ב", last.book)
        assertEquals(39, last.bookNo)
    }
}
