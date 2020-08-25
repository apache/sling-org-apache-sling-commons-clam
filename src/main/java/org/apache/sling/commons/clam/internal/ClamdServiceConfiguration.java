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
package org.apache.sling.commons.clam.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "Apache Sling Commons Clamd Service",
    description = "Service for scanning data with Clam daemon"
)
@SuppressWarnings("java:S100")
@interface ClamdServiceConfiguration {

    @AttributeDefinition(
        name = "clamd host",
        description = "host where Clam daemon is running"
    )
    String clamd_host() default "localhost";

    @AttributeDefinition(
        name = "clamd port",
        description = "port where Clam daemon will listen on"
    )
    int clamd_port() default 3310;

    @AttributeDefinition(
        name = "connection timeout",
        description = "timeout in milliseconds until connection expires"
    )
    int connection_timeout() default 1000;

    @AttributeDefinition(
        name = "chunk length",
        description = "length of chunks in bytes sending to Clam daemon"
    )
    int chunk_length() default 2048;

}
