/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.commons.clam.it.tests;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;

import org.apache.sling.testing.paxexam.TestSupport;
import org.jetbrains.annotations.NotNull;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.testcontainers.containers.GenericContainer;

import static org.apache.sling.testing.paxexam.SlingOptions.scr;
import static org.apache.sling.testing.paxexam.SlingOptions.testcontainers;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.cm.ConfigurationAdminOptions.newConfiguration;

public abstract class ClamTestSupport extends TestSupport {

    private static GenericContainer clamContainer;

    private static final String CLAM_CONTAINER_IMAGE_NAME = "mk0x/docker-clamav:alpine";

    @Configuration
    public Option[] configuration() {
        final boolean testcontainer = Boolean.parseBoolean(System.getProperty("clamd.testcontainer", "true"));
        final String host;
        final Integer port;
        if (testcontainer) {
            clamContainer = new GenericContainer<>(CLAM_CONTAINER_IMAGE_NAME)
                .withExposedPorts(3310)
                .withStartupTimeout(Duration.ofMinutes(5));
            clamContainer.start();
            host = clamContainer.getContainerIpAddress();
            port = clamContainer.getFirstMappedPort();
        } else {
            host = System.getProperty("clamd.host", "localhost");
            port = Integer.parseInt(System.getProperty("clamd.port", "3310"));
        }
        return options(
            baseConfiguration(),
            // Sling Commons Clam
            newConfiguration("org.apache.sling.commons.clam.internal.ClamdService")
                .put("clamd.host", host)
                .put("clamd.port", port)
                .asOption(),
            testBundle("bundle.filename"),
            mavenBundle().groupId("commons-io").artifactId("commons-io").versionAsInProject(),
            scr(),
            // testing
            testcontainers(),
            junitBundles()
        );
    }

    protected class InfiniteInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            return 0;
        }

        @Override
        public int read(@NotNull byte[] bytes) throws IOException {
            Arrays.fill(bytes, (byte) 1);
            return bytes.length;
        }

    }

}
