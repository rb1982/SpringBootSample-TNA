@echo off
IF "%JAVA_HOME%"=="" goto :javanotset
set APP_PROJECT_DIR=%~dp0..
set APP_NAME=technonavailability
set APP_IMAGE_TAG=TODO
set APP_SPACE=dev2
set SPRING_PROFILES_ACTIVE=dev2
set APP_REPLICAS=1
set APP_MEMORY_MB=1024
set ING_K8S_DOMAIN=kubeodc.corp.intranet
set K8S_NAMESPACE=dispatch-services-dev
set JMX_OPTS=-Djava.rmi.server.hostname=127.0.0.1 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=5000 -Dcom.sun.management.jmxremote.rmi.port=5000
echo Deployment environment set for DEV2.
goto :EOF
:javanotset
echo JAVA_HOME not set. Please set it first and re-execute.
exit /b