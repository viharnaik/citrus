/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.ssh.server;

import com.consol.citrus.endpoint.AbstractEndpointBuilder;
import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.ssh.message.SshMessageConverter;

/**
 * @author Christoph Deppisch
 * @since 2.5
 */
public class SshServerBuilder extends AbstractEndpointBuilder<SshServer> {

    /** Endpoint target */
    private SshServer endpoint = new SshServer();

    @Override
    protected SshServer getEndpoint() {
        return endpoint;
    }

    /**
     * Sets the port property.
     * @param port
     * @return
     */
    public SshServerBuilder port(int port) {
        endpoint.setPort(port);
        endpoint.getEndpointConfiguration().setPort(port);
        return this;
    }

    /**
     * Sets the user property.
     * @param user
     * @return
     */
    public SshServerBuilder user(String user) {
        endpoint.setUser(user);
        endpoint.getEndpointConfiguration().setUser(user);
        return this;
    }

    /**
     * Sets the client password.
     * @param password
     * @return
     */
    public SshServerBuilder password(String password) {
        endpoint.setPassword(password);
        endpoint.getEndpointConfiguration().setPassword(password);
        return this;
    }

    /**
     * Sets the hostKeyPath property.
     * @param hostKeyPath
     * @return
     */
    public SshServerBuilder hostKeyPath(String hostKeyPath) {
        endpoint.setHostKeyPath(hostKeyPath);
        return this;
    }

    /**
     * Sets the allowedKeyPath property.
     * @param allowedKeyPath
     * @return
     */
    public SshServerBuilder allowedKeyPath(String allowedKeyPath) {
        endpoint.setAllowedKeyPath(allowedKeyPath);
        return this;
    }

    /**
     * Sets the message converter.
     * @param messageConverter
     * @return
     */
    public SshServerBuilder messageConverter(SshMessageConverter messageConverter) {
        endpoint.setMessageConverter(messageConverter);
        endpoint.getEndpointConfiguration().setMessageConverter(messageConverter);
        return this;
    }

    /**
     * Sets the polling interval.
     * @param pollingInterval
     * @return
     */
    public SshServerBuilder pollingInterval(int pollingInterval) {
        endpoint.getEndpointConfiguration().setPollingInterval(pollingInterval);
        return this;
    }

    /**
     * Sets the endpoint adapter.
     * @param endpointAdapter
     * @return
     */
    public SshServerBuilder endpointAdapter(EndpointAdapter endpointAdapter) {
        endpoint.setEndpointAdapter(endpointAdapter);
        return this;
    }

    /**
     * Sets the default timeout.
     * @param timeout
     * @return
     */
    public SshServerBuilder timeout(long timeout) {
        endpoint.getEndpointConfiguration().setTimeout(timeout);
        return this;
    }

    /**
     * Sets the autoStart property.
     * @param autoStart
     * @return
     */
    public SshServerBuilder autoStart(boolean autoStart) {
        endpoint.setAutoStart(autoStart);
        return this;
    }
}
