package com.scentiva.modules.observability.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrelationIdFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    @DisplayName("doFilterInternal should generate correlation ID if header absent")
    void shouldGenerateCorrelationIdWhenAbsent() throws ServletException, IOException {
        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setHeader(eq(CorrelationIdFilter.CORRELATION_ID_HEADER), anyString());
        verify(filterChain, times(1)).doFilter(request, response);
        // MDC must be cleaned up after filter completion
        assertThat(MDC.get(CorrelationIdFilter.MDC_CORRELATION_ID_KEY)).isNull();
    }

    @Test
    @DisplayName("doFilterInternal should preserve incoming correlation ID")
    void shouldPreserveIncomingCorrelationId() throws ServletException, IOException {
        String existingCorrelationId = "test-corr-id-12345";
        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn(existingCorrelationId);

        filter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, existingCorrelationId);
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(MDC.get(CorrelationIdFilter.MDC_CORRELATION_ID_KEY)).isNull();
    }
}
