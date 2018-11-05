@echo off
echo Deploying...
%JAVA_HOME%\bin\java -jar %APP_PROJECT_DIR%\target\CommonUtilsAll.jar %APP_PROJECT_DIR% || goto :error
kubectl delete -f %APP_PROJECT_DIR%\target\.tmp\deployment.yaml
kubectl create -f %APP_PROJECT_DIR%\target\.tmp\deployment.yaml || goto :error
echo STEP 1 of 2: Deployment done.
kubectl apply -f %APP_PROJECT_DIR%\target\.tmp\service.yaml -f %APP_PROJECT_DIR%\target\.tmp\ingress.yaml || goto :error
echo STEP 2 of 2: Service and ingress configuration done.
goto :EOF
:error
echo Failed with error #%errorlevel%.
exit /b %errorlevel%