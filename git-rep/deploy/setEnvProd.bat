@echo off
IF "%JAVA_HOME%"=="" goto :javanotset
set APP_PROJECT_DIR=%~dp0..
set APP_NAME=technonavailability
set APP_IMAGE_TAG=TODO
set APP_SPACE=prod
set SPRING_PROFILES_ACTIVE=prod
set APP_REPLICAS=2
set APP_MEMORY_MB=1024
set ING_K8S_DOMAIN=kubeodc.corp.intranet
set K8S_NAMESPACE=dispatch-services
set JMX_OPTS= 
echo Deployment environment set for PROD.
goto :EOF
:javanotset
echo JAVA_HOME not set. Please set it first and re-execute.
exit /b