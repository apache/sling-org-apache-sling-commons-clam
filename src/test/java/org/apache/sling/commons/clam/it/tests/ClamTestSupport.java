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
import java.util.Arrays;

import org.apache.sling.testing.paxexam.TestSupport;
import org.jetbrains.annotations.NotNull;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;

import static org.apache.sling.testing.paxexam.SlingOptions.scr;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

public abstract class ClamTestSupport extends TestSupport {

    @Configuration
    public Option[] configuration() {
        return new Option[]{
            baseConfiguration(),
            // Sling Commons Clam
            testBundle("bundle.filename"),
            mavenBundle().groupId("commons-io").artifactId("commons-io").versionAsInProject(),
            scr(),
            // testing
            mavenBundle().groupId("org.apache.servicemix.bundles").artifactId("org.apache.servicemix.bundles.hamcrest").versionAsInProject(),
            junitBundles(),
        };
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
