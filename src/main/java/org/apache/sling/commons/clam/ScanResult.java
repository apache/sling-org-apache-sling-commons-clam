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

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Scan result contains the message from Clam, the parsed status and additional metadata.
 */
@ProviderType
public class ScanResult {

    private final long timestamp = System.currentTimeMillis();

    private final Status status;

    private final String message;

    private final long started;

    private final long size;

    /**
     * Creates a new <code>ScanResult</code> with the given initial parameters.
     *
     * @param status  The parsed status from Clam
     * @param message The message from Clam
     * @param started The start time of the scan
     * @param size    The number of bytes sent to Clam for scanning
     */
    public ScanResult(@NotNull final Status status, @NotNull final String message, long started, long size) {
        this.status = status;
        this.message = message;
        this.started = started;
        this.size = size;
    }

    /**
     * Returns the timestamp of the scan result.
     *
     * @return The time in milliseconds the scan result was created
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the parsed status from Clam.
     *
     * @return The parsed status from Clam
     */
    public @NotNull Status getStatus() {
        return status;
    }

    /**
     * Returns the message from Clam.
     *
     * @return The message from Clam
     */
    public @NotNull String getMessage() {
        return message;
    }

    /**
     * Returns the start time of the scan.
     *
     * @return The time in milliseconds the scan was started
     */
    public long getStarted() {
        return started;
    }

    /**
     * Returns the size of data sent to Clam for scanning.
     *
     * @return The number of bytes sent to Clam
     */
    public long getSize() {
        return size;
    }

    /**
     * Returns <code>true</code> when scanning was successful and no malware was found by Clam in sent data.
     *
     * @return <code>true</code> if status is <code>OK</code>, otherwise <code>false</code>
     */
    public boolean isOk() {
        return Status.OK.equals(status);
    }

    /**
     * Status based on reply message from Clam (<code>OK</code>, <code>FOUND</code>, <code>ERROR</code>) or <code>UNKNOWN</code> if parsing reply message failed.
     */
    public enum Status {
        OK,
        FOUND,
        ERROR,
        UNKNOWN
    }

}
