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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.sling.commons.clam.ClamService;
import org.apache.sling.commons.clam.ScanResult;
import org.apache.sling.commons.content.analyzing.ContentAnalyzer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.util.PathUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ClamdServiceIT extends ClamTestSupport {

    @Inject
    private ClamService clamService;

    @Inject
    @Filter(value = "(&(content.analyzer.operation=malware detection)(content.analyzer.provider=clamd))")
    private ContentAnalyzer contentAnalyzer;

    private static final String INSTREAM_SIZE_LIMIT_EXCEEDED_ERROR_MESSAGE = "INSTREAM size limit exceeded. ERROR";

    private byte[] readEicarFile() throws IOException {
        final byte[] xaa = Files.readAllBytes(Paths.get(PathUtils.getBaseDir(), "src/test/resources/eicar/eicarcom2.zip/xaa"));
        final byte[] xab = Files.readAllBytes(Paths.get(PathUtils.getBaseDir(), "src/test/resources/eicar/eicarcom2.zip/xab"));
        return ByteBuffer.allocate(xaa.length + xab.length).put(xaa).put(xab).array();
    }

    // ClamService

    @Test
    public void testClamService() {
        assertThat(clamService, notNullValue());
    }

    @Test
    public void testScan_ok() throws Exception {
        final String data = "ok – no malware here";
        try (final InputStream inputStream = IOUtils.toInputStream(data, StandardCharsets.UTF_8)) {
            final ScanResult result = clamService.scan(inputStream);
            assertThat(result.getStatus(), is(ScanResult.Status.OK));
        }
    }

    @Test
    public void testScan_eicarcom2_zip() throws Exception {
        final byte[] eicarcom2_zip = readEicarFile();
        try (final InputStream fileInputStream = new ByteArrayInputStream(eicarcom2_zip)) {
            final ScanResult result = clamService.scan(fileInputStream);
            assertThat(result.getStatus(), is(ScanResult.Status.FOUND));
        }
    }

    @Test
    public void testScan_infiniteStream() throws Exception {
        try (final InputStream inputStream = new InfiniteInputStream()) {
            final ScanResult result = clamService.scan(inputStream);
            assertThat(result.getStatus(), is(ScanResult.Status.ERROR));
            assertThat(result.getMessage(), is(INSTREAM_SIZE_LIMIT_EXCEEDED_ERROR_MESSAGE));
        }
    }

    // ContentAnalyzer

    @Test
    public void testContentAnalyzer() {
        assertThat(contentAnalyzer, notNullValue());
    }

    @Test
    public void testAnalyze_ok() throws Exception {
        final String data = "ok – no malware here";
        final Map<String, Object> report = new HashMap<>();
        try (final InputStream inputStream = IOUtils.toInputStream(data, StandardCharsets.UTF_8)) {
            contentAnalyzer.analyze(inputStream, null, report).get();
            assertThat(report.get("sling.commons.clam.scanresult.status"), is(ScanResult.Status.OK));
        }
    }

    @Test
    public void testAnalyze_eicarcom2_zip() throws Exception {
        final byte[] eicarcom2_zip = readEicarFile();
        final Map<String, Object> report = new HashMap<>();
        try (final InputStream inputStream = new ByteArrayInputStream(eicarcom2_zip)) {
            contentAnalyzer.analyze(inputStream, null, report).get();
            assertThat(report.get("sling.commons.clam.scanresult.status"), is(ScanResult.Status.FOUND));
        }
    }

    @Test
    public void testAnalyze_infiniteStream() throws Exception {
        final Map<String, Object> report = new HashMap<>();
        try (final InputStream inputStream = new InfiniteInputStream()) {
            contentAnalyzer.analyze(inputStream, null, report).get();
            assertThat(report.get("sling.commons.clam.scanresult.message"), is(INSTREAM_SIZE_LIMIT_EXCEEDED_ERROR_MESSAGE));
        }
    }

}
