package com.coconutmkii.nestjsidea.services

import com.coconutmkii.nestjsidea.NestJSBundle
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service
object NestJSNotificationService {
    @JvmStatic
    fun notifyNestCLIWasNotFound(project: Project) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("NestCLINotFound")
            .createNotification(
                NestJSBundle.message("notification.group.nestjs.cli.not.found"),
                NotificationType.WARNING
            )
            .notify(project)
    }
}