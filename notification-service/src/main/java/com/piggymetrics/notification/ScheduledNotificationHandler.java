package com.piggymetrics.notification;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.piggymetrics.notification.service.NotificationService;
import java.util.Map;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/** EventBridge Scheduler target. Invoked directly, never exposed through API Gateway. */
public class ScheduledNotificationHandler implements RequestHandler<Map<String, String>, String> {
    private static final ConfigurableApplicationContext CONTEXT = new SpringApplicationBuilder(
            NotificationServiceApplication.class).web(WebApplicationType.NONE)
            // The HTTP adapter's server factory requires a servlet application context.
            .properties("spring.autoconfigure.exclude="
                    + "org.springframework.cloud.function.serverless.web.ServerlessAutoConfiguration")
            .run();

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        NotificationService service = CONTEXT.getBean(NotificationService.class);
        String job = event == null ? "" : event.getOrDefault("job", "");
        switch (job) {
            case "BACKUP" -> service.sendBackupNotifications();
            case "REMIND" -> service.sendRemindNotifications();
            default -> throw new IllegalArgumentException("Unknown scheduled job");
        }
        return job + " notifications sent";
    }
}
