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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.sling.commons.clam.ClamService;
import org.apache.sling.commons.clam.ScanResult;
import org.apache.sling.commons.clam.ScanResult.Status;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    property = {
        Constants.SERVICE_DESCRIPTION + "=Sling Commons Clamd Service",
        Constants.SERVICE_VENDOR + "=The Apache Software Foundation"
    }
)
@Designate(
    ocd = ClamdServiceConfiguration.class
)
public class ClamdService implements ClamService {

    private ClamdServiceConfiguration configuration;

    private static final byte[] PING_COMMAND = "nPING\n".getBytes(StandardCharsets.US_ASCII);

    private static final byte[] PONG_REPLY = "PONG\n".getBytes(StandardCharsets.US_ASCII);

    private static final byte[] INSTREAM_COMMAND = "nINSTREAM\n".getBytes(StandardCharsets.US_ASCII);

    private static final String OK_REPLY_PATTERN = "stream: OK";

    private static final String FOUND_REPLY_PATTERN = "stream: .+ FOUND";

    private static final String INSTREAM_SIZE_LIMIT_EXCEEDED_PATTERN = "INSTREAM size limit exceeded. ERROR";

    private final Logger logger = LoggerFactory.getLogger(ClamdService.class);

    public ClamdService() {
    }

    @Activate
    private void activate(final ClamdServiceConfiguration configuration) {
        logger.debug("activating");
        configure(configuration);
    }

    @Modified
    private void modified(final ClamdServiceConfiguration configuration) {
        logger.debug("modifying");
        configure(configuration);
    }

    @Deactivate
    private void deactivate() {
        logger.debug("deactivating");
    }

    private void configure(final ClamdServiceConfiguration configuration) {
        this.configuration = configuration;
        playPingPong();
    }

    @Override
    @NotNull
    public ScanResult scan(@NotNull final InputStream inputStream) throws IOException {
        try {
            return doInstream(inputStream);
        } catch (InstreamSizeLimitExceededException e) {
            logger.error("doing INSTREAM failed", e);
            return new ScanResult(ScanResult.Status.ERROR, e.getMessage(), e.getStarted(), e.getSize());
        }
    }

    private byte[] doPing() throws IOException {
        logger.info("pinging clam daemon at {}:{}", configuration.clamd_host(), configuration.clamd_port());
        try (final Socket socket = new Socket(configuration.clamd_host(), configuration.clamd_port());
             final OutputStream out = new BufferedOutputStream(socket.getOutputStream());
             final InputStream in = socket.getInputStream()) {

            socket.setSoTimeout(configuration.connection_timeout());

            // send command
            out.write(PING_COMMAND);
            out.flush();

            return IOUtils.toByteArray(in);
        }
    }

    private void playPingPong() {
        try {
            final byte[] reply = doPing();
            if (Arrays.equals(reply, PONG_REPLY)) {
                logger.info("clam daemon replied with PONG");
            } else {
                final String message = new String(reply, StandardCharsets.US_ASCII);
                logger.error("clam daemon replied with unknown message: {}", message);
            }
        } catch (IOException e) {
            logger.error("pinging clam daemon failed: {}", e.getMessage());
        }
    }

    /**
     * man (8) clamd
     * INSTREAM
     * It is mandatory to prefix this command with n or z.
     * Scan a stream of data. The stream is sent to clamd in chunks, after INSTREAM, on the same socket on which the
     * command was sent.  This avoids the overhead of establishing new TCP connections and problems with NAT.
     * The format  of the chunk is: '<length><data>' where <length> is the size of the following data in bytes
     * expressed as a 4 byte unsigned integer in network byte order and <data> is the actual chunk.
     * Streaming is terminated by sending a zero-length chunk.
     * Note: do not exceed StreamMaxLength as defined in clamd.conf, otherwise clamd will reply with INSTREAM size
     * limit exceeded and close the connection.
     *
     * @param inputStream data sent to clamd in chunks
     * @return scan result from clamd
     */
    private ScanResult doInstream(final InputStream inputStream) throws IOException, InstreamSizeLimitExceededException {
        logger.info("connecting to clam daemon at {}:{} for scanning", configuration.clamd_host(), configuration.clamd_port());
        final long started = System.currentTimeMillis();
        try (final Socket socket = new Socket(configuration.clamd_host(), configuration.clamd_port());
             final OutputStream out = new BufferedOutputStream(socket.getOutputStream());
             final InputStream in = socket.getInputStream()) {

            socket.setSoTimeout(configuration.connection_timeout());

            // send command
            out.write(INSTREAM_COMMAND);
            out.flush();

            // send data in chunks
            byte[] data = new byte[configuration.chunk_length()];
            long total = 0;
            int read = inputStream.read(data);
            while (read >= 0) {
                logger.trace("current chunk length: {}", read);
                total = total + read;
                final byte[] length = ByteBuffer.allocate(4).putInt(read).array();

                out.write(length);
                out.write(data, 0, read);

                // handle premature reply
                if (in.available() > 0) {
                    logger.info("total bytes sent: {}", total);
                    final byte[] reply = IOUtils.toByteArray(in);
                    throw new InstreamSizeLimitExceededException(reply, started, total);
                }

                read = inputStream.read(data);
            }

            logger.info("total bytes sent: {}", total);

            // terminate by sending a zero-length chunk
            out.write(new byte[]{0, 0, 0, 0});
            out.flush();

            // return reply on complete
            final byte[] reply = IOUtils.toByteArray(in);
            return parseClamdReply(reply, started, total);
        }
    }

    private ScanResult parseClamdReply(final byte[] reply, final long started, final long size) {
        final String message = new String(reply, StandardCharsets.US_ASCII).trim();
        logger.info("reply message from clam daemon: '{}'", message);
        if (message.matches(OK_REPLY_PATTERN)) {
            return new ScanResult(Status.OK, message, started, size);
        } else if (message.matches(FOUND_REPLY_PATTERN)) {
            return new ScanResult(Status.FOUND, message, started, size);
        } else if (message.matches(INSTREAM_SIZE_LIMIT_EXCEEDED_PATTERN)) {
            return new ScanResult(Status.ERROR, message, started, size);
        } else {
            return new ScanResult(Status.UNKNOWN, message, started, size);
        }
    }

}
