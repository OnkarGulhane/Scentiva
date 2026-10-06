package com.scentiva.modules.notification.provider;

import com.scentiva.modules.notification.provider.dto.EmailPayload;

/**
 * Pluggable SPI for transactional and marketing email delivery providers (Resend, Gmail SMTP, Mock, etc.).
 */
public interface EmailProvider {

    /**
     * Dispatch an email payload to the recipient.
     *
     * @param payload Structured email data including recipient, subject, and HTML body
     * @return true if successfully dispatched, false otherwise
     */
    boolean sendEmail(EmailPayload payload);

    /**
     * @return unique identifier for this provider (e.g., "RESEND", "GMAIL_SMTP", "MOCK")
     */
    String getProviderName();

    /**
     * @return whether this provider is currently configured and operational
     */
    boolean isAvailable();
}
