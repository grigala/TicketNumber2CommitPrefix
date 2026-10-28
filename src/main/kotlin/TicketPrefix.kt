package io.github.grigala

/**
 * Pure logic for extracting a ticket key from a branch name and applying it to a commit message.
 */
object TicketPrefix {
    // Project key (uppercase letter followed by uppercase letters/digits/underscores), dash, number.
    private val TICKET_REGEX = Regex("""(?<![A-Za-z0-9])([A-Z][A-Z0-9_]+-\d+)(?![0-9])""")

    const val SEPARATOR = ": "

    /** `feature/TEST-123-implementation` -> `TEST-123`, or `null` if the branch contains no ticket key. */
    fun extractTicket(branchName: String?): String? =
        branchName?.let { TICKET_REGEX.find(it)?.groupValues?.get(1) }

    /** Prepends `TICKET: ` to [message] unless it already starts with the ticket key. */
    fun applyPrefix(ticket: String, message: String): String =
        if (message.trimStart().startsWith(ticket)) message else "$ticket$SEPARATOR$message"
}
