package io.github.grigala

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TicketPrefixTest {

    @Test
    fun extractsTicketFromBranchNames() {
        assertEquals("TEST-123", TicketPrefix.extractTicket("feature/TEST-123-implementation"))
        assertEquals("TEST-123", TicketPrefix.extractTicket("TEST-123"))
        assertEquals("AB2-7", TicketPrefix.extractTicket("bugfix/AB2-7_fix"))
        assertEquals("MY_PROJ-42", TicketPrefix.extractTicket("hotfix/MY_PROJ-42"))
    }

    @Test
    fun returnsNullWithoutTicket() {
        assertNull(TicketPrefix.extractTicket("main"))
        assertNull(TicketPrefix.extractTicket("feature/some-thing"))
        assertNull(TicketPrefix.extractTicket("feature/tic-123-lowercase"))
        assertNull(TicketPrefix.extractTicket(null))
    }

    @Test
    fun prefixesMessage() {
        assertEquals("TEST-123: ", TicketPrefix.applyPrefix("TEST-123", ""))
        assertEquals("TEST-123: Fix bug", TicketPrefix.applyPrefix("TEST-123", "Fix bug"))
    }

    @Test
    fun doesNotDuplicatePrefix() {
        assertEquals("TEST-123: Fix bug", TicketPrefix.applyPrefix("TEST-123", "TEST-123: Fix bug"))
    }
}

