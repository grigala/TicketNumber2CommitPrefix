package io.github.grigala

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsDataKeys
import io.github.grigala.BranchTicketResolver.Result

/**
 * Commit message toolbar button that (re)applies the ticket prefix on demand,
 * e.g. after switching branches or when a draft message was already present.
 * Shows a notification when no ticket key can be extracted from the current branch.
 */
class InsertTicketPrefixAction : DumbAwareAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null &&
            e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val commitMessage = e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL) ?: return
        val document = e.getData(VcsDataKeys.COMMIT_MESSAGE_DOCUMENT)

        when (val result = BranchTicketResolver.resolve(project)) {
            is Result.Found ->
                commitMessage.setCommitMessage(TicketPrefix.applyPrefix(result.ticket, document?.text.orEmpty()))
            is Result.NoTicketInBranch ->
                notifyWarning(project, MessageBundle.message("notification.no.ticket.in.branch", result.branchName))
            Result.NoBranch ->
                notifyWarning(project, MessageBundle.message("notification.no.branch"))
            Result.NoRepository ->
                notifyWarning(project, MessageBundle.message("notification.no.repository"))
        }
    }

    private fun notifyWarning(project: Project, content: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification(MessageBundle.message("notification.title"), content, NotificationType.WARNING)
            .notify(project)
    }

    companion object {
        const val NOTIFICATION_GROUP_ID = "TicketNumber2CommitPrefix"
    }
}
