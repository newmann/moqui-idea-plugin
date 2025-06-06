package org.moqui.idea.plugin.util;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.moqui.idea.plugin.MyBundle;

import java.util.Objects;

public final class CustomNotifier {
    private CustomNotifier() {
        throw new UnsupportedOperationException();
    }

    /**
     * info 级别通知，不弹窗，只在 Event Log 窗口打印
     */
    private static final NotificationGroup NOTIFICATION_GROUP;
    static {
        NOTIFICATION_GROUP = NotificationGroupManager.getInstance().getNotificationGroup(MyBundle.message("util.CustomNotifier.notificationGroup"));
    }

    /**
     * info 级别通知输出
     * <p>
     * 不弹窗，只在 Event Log 窗口打印
     *
     * @param project 当前 Project
     * @param content 通知内容
     */
    public static void info(Project project, String content) {
        if (Objects.isNull(project)) {
            System.out.println(MyBundle.message("util.CustomNotifier.currentProjectIsNull"));
            return;
        }

        NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION).notify(project);
    }

    /**
     * warn 级别通知输出
     * <p>
     * 弹窗后自动消失，且在 Event Log 窗口打印
     *
     * @param project 当前 Project
     * @param content 通知内容
     */
    public static void warn(Project project, String content) {
        if (Objects.isNull(project)) {
            System.out.println(MyBundle.message("util.CustomNotifier.currentProjectIsNull"));
            return;
        }

        NOTIFICATION_GROUP.createNotification(content, NotificationType.WARNING).notify(project);
    }

    /**
     * error 级别通知输出
     * <p>
     * 弹窗不消失，且在 Event Log 窗口打印
     *
     * @param project 当前 Project
     * @param content 通知内容
     */
    public static void error(Project project, String content) {
        if (Objects.isNull(project)) {
            System.out.println(MyBundle.message("util.CustomNotifier.currentProjectIsNull"));
            return;
        }

        NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR).notify(project);
    }

}
