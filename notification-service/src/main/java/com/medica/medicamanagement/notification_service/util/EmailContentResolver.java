package com.medica.medicamanagement.notification_service.util;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * The type Email content resolver.
 */
public final class EmailContentResolver {

    private EmailContentResolver() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    /**
     * Gets html body for appointment status.
     *
     * @param templateEngine      the template engine
     * @param status              the status
     * @param scheduledTemplate   the scheduled template
     * @param rescheduledTemplate the rescheduled template
     * @param approvedTemplate    the approved template
     * @param rejectedTemplate    the rejected template
     * @param canceledTemplate    the canceled template
     * @param context             the context
     * @return the html body for appointment status
     */
    public static String getHtmlBodyForAppointmentStatus(SpringTemplateEngine templateEngine, String status, String scheduledTemplate, String rescheduledTemplate, String approvedTemplate,
                                     String rejectedTemplate, String canceledTemplate, Context context) {
        return switch (status) {
            case "SCHEDULED" -> templateEngine.process(scheduledTemplate, context);
            case "RESCHEDULED" -> templateEngine.process(rescheduledTemplate, context);
            case "APPROVED" -> approvedTemplate != null ? templateEngine.process(approvedTemplate, context) : "";
            case "REJECTED" -> templateEngine.process(rejectedTemplate, context);
            case "CANCELED" -> templateEngine.process(canceledTemplate, context);
            default -> "";
        };
    }

    /**
     * Gets html body for password change.
     *
     * @param templateEngine         the template engine
     * @param passwordChangeTemplate the password change template
     * @param context                the context
     * @return the html body for password change
     */
    public static String getHtmlBodyForPasswordChange(SpringTemplateEngine templateEngine, String passwordChangeTemplate, Context context) {
        return templateEngine.process(passwordChangeTemplate, context);
    }
}
