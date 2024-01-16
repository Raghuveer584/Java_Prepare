set M2_HOME=C:\D\softwares\apache-maven-3.3.9\apache-maven-3.3.9
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_45
set PATH=%M2_HOME%\bin;%JAVA_HOME%\bin;%PATH%

:: ----------

call mvn -s ../settings.xml clean install