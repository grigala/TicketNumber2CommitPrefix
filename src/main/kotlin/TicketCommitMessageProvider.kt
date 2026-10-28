package io.github.grigala

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.LocalChangeList
import com.intellij.openapi.vcs.changes.ui.CommitMessageProvider

/**
 * Supplies the initial commit message for the commit tool window / dialog,
 * prefixed with the ticket key from the current branch.
 */
class TicketCommitMessageProvider : CommitMessageProvider {
    override fun getCommitMessage(forChangelist: LocalChangeList, project: Project): String? {
        val ticket = BranchTicketResolver.resolveTicket(project, forChangelist.changes) ?: return null
        return TicketPrefix.applyPrefix(ticket, forChangelist.comment.orEmpty())
    }
}
