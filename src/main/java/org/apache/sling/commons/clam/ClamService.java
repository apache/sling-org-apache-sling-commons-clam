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
package org.apache.sling.commons.clam;

import java.io.IOException;
import java.io.InputStream;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface ClamService {

    /**
     * @param data The data to scan for malware
     * @return The scan result from Clam
     * @throws IOException if scanning data or parsing reply fails
     */
    @NotNull
    ScanResult scan(@NotNull final InputStream data) throws IOException;

}
