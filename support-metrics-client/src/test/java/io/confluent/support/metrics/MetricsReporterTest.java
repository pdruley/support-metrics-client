/**
 * Copyright 2015 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.confluent.support.metrics;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Properties;
import io.confluent.support.metrics.utils.KafkaServerUtils;
import kafka.Kafka;
import kafka.server.KafkaServer;
import kafka.zk.EmbeddedZookeeper;

import static org.assertj.core.api.Assertions.fail;

public class MetricsReporterTest  {

  private static EmbeddedZookeeper zookeeper = null;
  private static KafkaServer server = null;

  @BeforeClass
  public static void startCluster() {
    zookeeper = KafkaServerUtils.startZookeeper();
    server = KafkaServerUtils.startServer(zookeeper);
  }

  @AfterClass
  public static void stopCluster() {
    KafkaServerUtils.stopServer(server);
    KafkaServerUtils.stopZookeeper(zookeeper);
  }

  @Test
  public void testInvalidArgumentsForConstructorNullServer() {
    // Given
    Properties props = new Properties();
    Runtime serverRuntime = Runtime.getRuntime();

    // When/Then
    try {
      new MetricsReporter(null, props, serverRuntime);
      fail("IllegalArgumentException expected because server is null");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("some arguments are null");
    }
  }

  @Test
  public void testInvalidArgumentsForConstructorNullProps() {
    // Given
    Properties props = null;
    Runtime serverRuntime = Runtime.getRuntime();

    // When/Then
    try {
      new MetricsReporter(server, props, serverRuntime);
      fail("IllegalArgumentException expected because props is null");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("some arguments are null");
    }
  }

  @Test
  public void testInvalidArgumentsForConstructorNullRuntime() {
    // Given
    Properties props = new Properties();
    Runtime serverRuntime = null;

    // When/Then
    try {
      new MetricsReporter(server, props, serverRuntime);
      fail("IllegalArgumentException expected because serverRuntime is null");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("some arguments are null");
    }
  }

  @Test
  public void testValidConstructor() throws IOException {
    // Given
    Runtime serverRuntime = Runtime.getRuntime();
    Properties serverProps;
    serverProps = Kafka.getPropsFromArgs(new String[]{KafkaServerUtils.prepareDefaultConfig()});

    // When/Then
    MetricsReporter reporter = new MetricsReporter(server, serverProps, serverRuntime);
    assertThat(reporter.reportingEnabled()).isEqualTo(true);
    assertThat(reporter.sendToKafkaEnabled()).isEqualTo(true);
  }
}