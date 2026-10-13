@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
pushd "%SCRIPT_DIR%" >nul

if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" goto javaFound

if exist "%LOCALAPPDATA%\CampusMarketplace\tools\jdk-17.0.20.1+1\bin\javac.exe" set "JAVA_HOME=%LOCALAPPDATA%\CampusMarketplace\tools\jdk-17.0.20.1+1"

for %%D in ("%LOCALAPPDATA%\CampusMarketplace\tools\jdk-*" "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-*" "%LOCALAPPDATA%\Programs\Microsoft\jdk-*" "%ProgramFiles%\Eclipse Adoptium\jdk-*" "%ProgramFiles%\Microsoft\jdk-*" "%ProgramFiles%\Java\jdk-*" "%ProgramFiles(x86)%\Java\jdk-*") do (
  if exist "%%~D\bin\javac.exe" set "JAVA_HOME=%%~D"
)

if not defined JAVA_HOME if defined USERPROFILE (
  for /d %%E in ("%USERPROFILE%\.vscode\extensions\redhat.java-*") do (
    for /d %%R in ("%%~E\jre\*") do (
      if exist "%%~R\bin\javac.exe" set "JAVA_HOME=%%~R"
    )
  )
)

if not defined JAVA_HOME (
  for /f "delims=" %%I in ('where javac 2^>nul') do (
    set "JAVA_HOME=%%~dpI.."
    for %%J in ("!JAVA_HOME!") do set "JAVA_HOME=%%~fJ"
    goto javaFound
  )
)

:javaFound
if not defined JAVA_HOME (
  echo A Java JDK 17 or newer is required to build the backend.
  echo Install a JDK and set JAVA_HOME, then reopen this workspace.
  exit /b 1
)

if not exist "%JAVA_HOME%\bin\javac.exe" (
  echo JAVA_HOME points to a Java runtime without the compiler:
  echo   %JAVA_HOME%
  echo Install a full JDK 17 or newer and set JAVA_HOME to its folder.
  exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"

for /f "tokens=3" %%V in ('java -version 2^>^&1 ^| findstr /i "version"') do set "JAVA_VERSION=%%~V"
for /f "tokens=1 delims=." %%V in ("!JAVA_VERSION!") do set "JAVA_MAJOR=%%V"
if "!JAVA_MAJOR!"=="1" for /f "tokens=2 delims=." %%V in ("!JAVA_VERSION!") do set "JAVA_MAJOR=%%V"
if not defined JAVA_MAJOR (
  echo Unable to determine Java version from %JAVA_HOME%\bin\java.exe.
  exit /b 1
)
if !JAVA_MAJOR! LSS 17 (
  echo Java 17 or newer is required. Found Java !JAVA_MAJOR!.
  exit /b 1
)

set "MAVEN_CMD="
if exist "%LOCALAPPDATA%\CampusMarketplace\tools\apache-maven-3.10.0\bin\mvn.cmd" set "MAVEN_CMD=%LOCALAPPDATA%\CampusMarketplace\tools\apache-maven-3.10.0\bin\mvn.cmd"
for %%D in ("%LOCALAPPDATA%\CampusMarketplace\tools\apache-maven-*" "%ProgramFiles%\apache-maven-*" "%ProgramFiles%\Apache\Maven" "%ProgramFiles(x86)%\Apache\Maven") do (
  if exist "%%~D\bin\mvn.cmd" set "MAVEN_CMD=%%~D\bin\mvn.cmd"
)
if not defined MAVEN_CMD for /f "delims=" %%I in ('where mvn.cmd 2^>nul') do set "MAVEN_CMD=%%~I"
if defined MAVEN_CMD (
  call "%MAVEN_CMD%" %*
  set "MAVEN_EXIT=!ERRORLEVEL!"
  exit /b !MAVEN_EXIT!
)

if not exist ".mvn\wrapper\maven-wrapper.properties" (
  echo Maven Wrapper configuration is missing from .mvn\wrapper.
  exit /b 1
)

if not exist ".mvn\wrapper\maven-wrapper.jar" (
  echo Maven Wrapper bootstrap jar is missing from .mvn\wrapper.
  exit /b 1
)

"%JAVA_HOME%\bin\java.exe" -classpath ".mvn\wrapper\maven-wrapper.jar" "-Dmaven.multiModuleProjectDirectory=%SCRIPT_DIR:~0,-1%" org.apache.maven.wrapper.MavenWrapperMain %*
exit /b %ERRORLEVEL%
