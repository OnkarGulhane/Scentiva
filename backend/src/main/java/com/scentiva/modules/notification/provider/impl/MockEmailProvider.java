package com.scentiva.modules.notification.provider.impl;

import com.scentiva.modules.notification.provider.EmailProvider;
import com.scentiva.modules.notification.provider.dto.EmailPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mock Email Provider for testing, staging, and development environments.
 * Logs dispatched emails and records them in memory.
 */
@Slf4j
@Component("mockEmailProvider")
public class MockEmailProvider implements EmailProvider {

    private final List<EmailPayload> sentEmails = Collections.synchronizedList(new ArrayList<>());

    @Override
    public boolean sendEmail(EmailPayload payload) {
        log.info("[MOCK EMAIL] Simulating email delivery to='{}', subject='{}', size={} bytes",
                payload.getTo(), payload.getSubject(),
                payload.getHtmlContent() != null ? payload.getHtmlContent().length() : 0);
        sentEmails.add(payload);
        return true;
    }

    @Override
    public String getProviderName() {
        return "MOCK";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    public List<EmailPayload> getSentEmails() {
        return Collections.unmodifiableList(sentEmails);
    }

    public void clear() {
        sentEmails.clear();
    }
}
