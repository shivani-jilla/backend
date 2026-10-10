@echo off
if not defined JAVA_HOME (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
)
set "MAVEN_OPTS=-Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8"
"%~dp0..\tools\apache-maven-3.9.6\bin\mvn.cmd" %*
