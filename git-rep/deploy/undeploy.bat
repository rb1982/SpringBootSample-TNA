@echo off
echo Un-deploying...
kubectl delete deployment %APP_NAME%-%APP_SPACE% -n %K8S_NAMESPACE% || goto :error
echo STEP 1 of 3: Deployment deleted.
kubectl delete service %APP_NAME%-%APP_SPACE%-service -n %K8S_NAMESPACE% || goto :error
echo STEP 2 of 3: Service deleted.
kubectl delete ing %APP_NAME%-%APP_SPACE%-ing -n %K8S_NAMESPACE% || goto :error
echo STEP 3 of 3: Ingress configuration deleted.
goto :EOF
:error
echo Failed with error #%errorlevel%.
exit /b %errorlevel%