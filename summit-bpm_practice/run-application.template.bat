@title DEPLOYMENT - summit-bpm

set M2_HOME=C:\Program Files\Maven\apache-maven-3.3.9
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set PATH=%M2_HOME%\bin;%JAVA_HOME%\bin;%PATH%

:::::::::::::::::::::::::::::::::::::
:: GET MAVEN VERSION FROM POM FILE ::
:::::::::::::::::::::::::::::::::::::
call mvn -s ../settings.xml --batch-mode org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version > temp.out1
findstr /v /L "[" temp.out1 > temp.out2
set /p VERSION=<temp.out2
del temp.out1 temp.out2
echo [VERSION] = %VERSION%

::::::::::::::::::::::::
:: DEPLOY APPLICATION ::
::::::::::::::::::::::::
IF NOT EXIST target/summit-bpm-%VERSION%-exec.jar (
    echo.
    echo ************************************************************************
    echo ***** Cannot find deployable artifact for [summit-bpm] -- exiting! *****
    echo ***** Please make sure you build [summit-bpm] before running this! *****
    echo ************************************************************************
    pause
    exit /b
) ELSE (
    java -jar target/summit-bpm-%VERSION%-exec.jar
)

::  --spring.config.location=src/main/resources/application.properties