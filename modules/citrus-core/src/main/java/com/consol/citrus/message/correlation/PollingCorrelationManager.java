/*
 * Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.message.correlation;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.endpoint.PollableEndpointConfiguration;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.messaging.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of default correlation manager adds polling mechanism for find operation on object store.
 * In case object is not found in store retry is automatically performed. Polling interval and overall retry timeout
 * is usually defined in endpoint configuration.
 *
 * @author Christoph Deppisch
 * @since 2.1
 */
public class PollingCorrelationManager<T> extends DefaultCorrelationManager<T> {

    private String retryLogMessage = "Correlated object not found yet";

    private final PollableEndpointConfiguration endpointConfiguration;

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(PollingCorrelationManager.class);

    /** Retry logger */
    private static final Logger RETRY_LOG = LoggerFactory.getLogger("com.consol.citrus.RetryLogger");

    /**
     * Constructor using fields.
     * @param endpointConfiguration
     * @param retryLogMessage
     */
    public PollingCorrelationManager(PollableEndpointConfiguration endpointConfiguration, String retryLogMessage) {
        this.retryLogMessage = retryLogMessage;
        this.endpointConfiguration = endpointConfiguration;
    }

    /**
     * Convenience method for using default timeout settings of endpoint configuration.
     * @param correlationKey
     * @return
     */
    public T find(String correlationKey) {
        return find(correlationKey, endpointConfiguration.getTimeout());
    }

    @Override
    public String getCorrelationKey(Consumer consumer, TestContext context) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Get correlation key for '%s'", getCorrelationKeyName(consumer)));
        }

        String correlationKey = null;
        if (context.getVariables().containsKey(getCorrelationKeyName(consumer))) {
            correlationKey = context.getVariable(getCorrelationKeyName(consumer));
        }

        long timeLeft = 1000L;
        long pollingInterval = 300L;
        while (correlationKey == null && timeLeft > 0) {
            timeLeft -= pollingInterval;

            if (RETRY_LOG.isDebugEnabled()) {
                RETRY_LOG.debug("Correlation key not available yet - retrying in " + (timeLeft > 0 ? pollingInterval : pollingInterval + timeLeft) + "ms");
            }

            try {
                Thread.sleep(timeLeft > 0 ? pollingInterval : pollingInterval + timeLeft);
            } catch (InterruptedException e) {
                RETRY_LOG.warn("Thread interrupted while waiting for retry", e);
            }

            if (context.getVariables().containsKey(getCorrelationKeyName(consumer))) {
                correlationKey = context.getVariable(getCorrelationKeyName(consumer));
            }
        }

        if (correlationKey == null) {
            throw new CitrusRuntimeException(String.format("Failed to get correlation key for '%s'", getCorrelationKeyName(consumer)));
        }

        return correlationKey;
    }

    @Override
    public T find(String correlationKey, long timeout) {
        long timeLeft = timeout;
        long pollingInterval = endpointConfiguration.getPollingInterval();

        T stored = super.find(correlationKey, timeLeft);

        while (stored == null && timeLeft > 0) {
            timeLeft -= pollingInterval;

            if (RETRY_LOG.isDebugEnabled()) {
                RETRY_LOG.debug(retryLogMessage + " - retrying in " + (timeLeft > 0 ? pollingInterval : pollingInterval + timeLeft) + "ms");
            }

            try {
                Thread.sleep(timeLeft > 0 ? pollingInterval : pollingInterval + timeLeft);
            } catch (InterruptedException e) {
                RETRY_LOG.warn("Thread interrupted while waiting for retry", e);
            }

            stored = super.find(correlationKey, timeLeft);
        }

        return stored;
    }

    /**
     * Gets the retry log message
     * @return
     */
    public String getRetryLogMessage() {
        return retryLogMessage;
    }

    /**
     * Sets the retry log message.
     * @param retryLogMessage
     */
    public void setRetryLogMessage(String retryLogMessage) {
        this.retryLogMessage = retryLogMessage;
    }
}