/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Schibsted Products & Technology AS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.schibsted.security.strongbox.cli.viewmodel.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author torarvid
 */
public class ProxyInformation {
    public final Optional<String> username;
    public final Optional<String> password;
    public final List<String> nonProxyHosts;
    public final String host;
    public final int port;

    private static final Logger log = LoggerFactory.getLogger(ProxyInformation.class);

    public ProxyInformation(String rawProxy, Optional<String> nonProxyHosts) {
        try {
            URL proxyUrl = new URL(rawProxy);
            String userInfo = proxyUrl.getUserInfo();
            if (userInfo != null && !userInfo.isEmpty()) {
                String[] parts = userInfo.split(":");
                if (parts.length != 2) {
                    throw new IllegalArgumentException(String.format("Malformed proxy URL '%s'", rawProxy));
                }
                this.username = Optional.of(parts[0]);
                this.password = Optional.of(parts[1]);
            } else {
                this.username = Optional.empty();
                this.password = Optional.empty();
            }
            this.host = proxyUrl.getHost();
            this.port = proxyUrl.getPort();

            // Turn "foo, .other.com" into ["foo", "*.other.com"]
            this.nonProxyHosts = nonProxyHosts.map(
                s -> Arrays.stream(s.split(","))
                    .map(host -> host.trim().replaceAll("^\\.", "*."))
                    .collect(Collectors.toList())
                ).orElseGet(ArrayList::new);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("Malformed proxy URL '%s'", rawProxy), e);
        }
    }

    private static Optional<ProxyInformation> parse(String rawProxy, String nonProxyHosts) {
        if (rawProxy == null || rawProxy.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new ProxyInformation(rawProxy, Optional.ofNullable(nonProxyHosts)));
    }

    public static Optional<ProxyInformation> fromEnvironment() {
        String proxyLower = System.getenv("https_proxy");
        String proxyUpper = System.getenv("HTTPS_PROXY");
        String noProxyLower = System.getenv("no_proxy");
        String noProxyUpper = System.getenv("NO_PROXY");
        if (proxyLower != null && proxyUpper != null && !proxyLower.equals(proxyUpper)) {
            log.warn("Ignoring environment variable 'HTTPS_PROXY', 'https_proxy' has precedence");
        }
        Optional<ProxyInformation> proxyInfo = ProxyInformation.parse(proxyLower, noProxyLower);
        if (!proxyInfo.isPresent()) {
            proxyInfo = ProxyInformation.parse(proxyUpper, noProxyLower);
        }
        if (!proxyInfo.isPresent()) {
            if (System.getenv("http_proxy") != null || System.getenv("HTTP_PROXY") != null) {
                log.warn("Ignoring environment variable 'http_proxy'. No 'https_proxy' environment variable was set");
            }
        } else {
            if (noProxyLower != null && noProxyUpper != null && !noProxyLower.equals(noProxyUpper)) {
                log.warn("Ignoring environment variable 'NO_PROXY', 'no_proxy' has precedence");
            }
        }
        return proxyInfo;
    }
}
