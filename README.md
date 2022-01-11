[![Apache Sling](https://sling.apache.org/res/logos/sling.png)](https://sling.apache.org)

&#32;[![Build Status](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-clam/job/master/badge/icon)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-clam/job/master/)&#32;[![Test Status](https://img.shields.io/jenkins/tests.svg?jobUrl=https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-clam/job/master/)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-clam/job/master/test/?width=800&height=600)&#32;[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-commons-clam&metric=coverage)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-commons-clam)&#32;[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-commons-clam&metric=alert_status)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-commons-clam)&#32;[![JavaDoc](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.commons.clam.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.commons.clam)&#32;[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.commons.clam/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.commons.clam%22) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Commons Clam

This module is part of the [Apache Sling](https://sling.apache.org) project.

Scans data for malware using [ClamAV](https://www.clamav.net).

Please see [Apache Sling Clam](https://github.com/apache/sling-org-apache-sling-clam) for an integration into Apache Sling to scan data in JCR.


## Integration Tests

Integration tests require a running Clam daemon. By default a Docker container is started via [Testcontainers](https://www.testcontainers.org/) and local [Docker](https://www.docker.com/) Engine to provide the Clam daemon.


### Use external Clam daemon

To disable *Testcontainers* and use an external Clam daemon set `clamd.testcontainer` to `false`:

    mvn clean install -Dclamd.testcontainer=false

To override default Clam daemon host `localhost` and port `3310` set `clamd.host` and `clamd.port`:

    mvn clean install -Dclamd.testcontainer=false -Dclamd.host=localhost -Dclamd.port=3310


## EICAR

[EICAR](http://www.eicar.org) provides anti-malware [test files](http://www.eicar.org/85-0-Download.html) which are used by this module. Read carefully about [intended use](http://www.eicar.org/86-0-Intended-use.html).

The test files are split to prevent alarms on development and build systems and concatenated in memory during test execution.

    split -b 154 eicarcom2.zip
