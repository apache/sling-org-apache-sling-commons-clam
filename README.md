# Apache Sling Commons Clam

This module is part of the [Apache Sling](https://sling.apache.org) project.

Scans data for malware using [ClamAV](https://www.clamav.net).

Integration tests require a running clam daemon and are not enabled by default.

## EICAR

[EICAR](http://www.eicar.org) provides anti-malware [test files](http://www.eicar.org/85-0-Download.html) which are used by this module. Read carefully about [intended use](http://www.eicar.org/86-0-Intended-use.html).

The test files are split to prevent alarms on development and build systems.

    split -b 154 eicarcom2.zip
