package io.github.grigala

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.Change
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager

/**
 * Finds the ticket for the current Git branch.
 */
object BranchTicketResolver {

    sealed interface Result {
        data class Found(val ticket: String) : Result
        data class NoTicketInBranch(val branchName: String) : Result
        data object NoBranch : Result
        data object NoRepository : Result
    }

    fun resolveTicket(project: Project, changes: Collection<Change> = emptyList()): String? =
        (resolve(project, changes) as? Result.Found)?.ticket

    /**
     * Uses the repository the [changes] belong to when known; otherwise falls back to the
     * project's single repository (or the first one in multi-repo projects).
     */
    fun resolve(project: Project, changes: Collection<Change> = emptyList()): Result {
        val repository = findRepository(project, changes) ?: return Result.NoRepository
        val branchName = repository.currentBranch?.name ?: return Result.NoBranch
        val ticket = TicketPrefix.extractTicket(branchName) ?: return Result.NoTicketInBranch(branchName)
        return Result.Found(ticket)
    }

    private fun findRepository(project: Project, changes: Collection<Change>): GitRepository? {
        val manager = GitRepositoryManager.getInstance(project)
        val fromChanges = changes.asSequence()
            .mapNotNull { change -> (change.afterRevision ?: change.beforeRevision)?.file }
            .mapNotNull { manager.getRepositoryForFileQuick(it) }
            .firstOrNull()
        return fromChanges ?: manager.repositories.firstOrNull()
    }
}
