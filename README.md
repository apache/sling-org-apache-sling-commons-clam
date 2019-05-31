[<img src="https://sling.apache.org/res/logos/sling.png"/>](https://sling.apache.org)

 [![Build Status](https://builds.apache.org/buildStatus/icon?job=Sling/sling-org-apache-sling-commons-clam/master)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-commons-clam/job/master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.commons.clam/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.commons.clam%22) [![JavaDocs](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.commons.clam.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.commons.clam) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Commons Clam

This module is part of the [Apache Sling](https://sling.apache.org) project.

Scans data for malware using [ClamAV](https://www.clamav.net).

Integration tests require a running Clam daemon and are not enabled by default.

Please see [Apache Sling Clam](https://github.com/apache/sling-org-apache-sling-clam) for an integration into Apache Sling to scan data in JCR.

## EICAR

[EICAR](http://www.eicar.org) provides anti-malware [test files](http://www.eicar.org/85-0-Download.html) which are used by this module. Read carefully about [intended use](http://www.eicar.org/86-0-Intended-use.html).

The test files are split to prevent alarms on development and build systems.

    split -b 154 eicarcom2.zip
