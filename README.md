[<img src="https://sling.apache.org/res/logos/sling.png"/>](https://sling.apache.org)

 [![Build Status](https://builds.apache.org/buildStatus/icon?job=Sling/sling-org-apache-sling-commons-clam/master)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-commons-clam/job/master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.commons.clam/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.commons.clam%22) [![JavaDocs](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.commons.clam.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.commons.clam) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Commons Clam

This module is part of the [Apache Sling](https://sling.apache.org) project.

Scans data for malware using [ClamAV](https://www.clamav.net).

Please see [Apache Sling Clam](https://github.com/apache/sling-org-apache-sling-clam) for an integration into Apache Sling to scan data in JCR.


## Integration Tests

Integration tests require a running Clam daemon and are not enabled by default.


### Use [Testcontainers](https://www.testcontainers.org/) and local [Docker](https://www.docker.com/) Engine

Enable the `it` profile to run integration tests with Docker container:

    mvn clean install -Pit


### Use external Clam daemon

To disable *Testcontainers* and use an external Clam daemon set `clamd.testcontainer` to `false`:

    mvn clean install -Pit -Dclamd.testcontainer=false

To override default Clam daemon host `localhost` and port `3310` set `clamd.host` and `clamd.port`:

    mvn clean install -Pit -Dclamd.testcontainer=false -Dclamd.host=localhost -Dclamd.port=3310


## EICAR

[EICAR](http://www.eicar.org) provides anti-malware [test files](http://www.eicar.org/85-0-Download.html) which are used by this module. Read carefully about [intended use](http://www.eicar.org/86-0-Intended-use.html).

The test files are split to prevent alarms on development and build systems and concatenated in memory during test execution.

    split -b 154 eicarcom2.zip
