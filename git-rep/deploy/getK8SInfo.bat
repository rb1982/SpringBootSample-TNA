@echo off
echo ** DISPATCH SERVICES **
echo **    PODS INFO      **     
kubectl get pods -n %K8S_NAMESPACE% -o wide || goto :error
echo **   SERVICES INFO   **
kubectl get services -n %K8S_NAMESPACE% -o wide || goto :error
echo **   INGRESS INFO    **
kubectl get ing -n %K8S_NAMESPACE% || goto :error
goto :EOF
:error
echo Failed with error #%errorlevel%.
exit /b %errorlevel%