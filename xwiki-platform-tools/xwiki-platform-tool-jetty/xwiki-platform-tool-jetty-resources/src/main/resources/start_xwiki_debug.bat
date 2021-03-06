@echo off
REM -------------------------------------------------------------------------
REM See the NOTICE file distributed with this work for additional
REM information regarding copyright ownership.
REM
REM This is free software; you can redistribute it and/or modify it
REM under the terms of the GNU Lesser General Public License as
REM published by the Free Software Foundation; either version 2.1 of
REM the License, or (at your option) any later version.
REM
REM This software is distributed in the hope that it will be useful,
REM but WITHOUT ANY WARRANTY; without even the implied warranty of
REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
REM Lesser General Public License for more details.
REM
REM You should have received a copy of the GNU Lesser General Public
REM License along with this software; if not, write to the Free
REM Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
REM 02110-1301 USA, or see the FSF site: http://www.fsf.org.
REM -------------------------------------------------------------------------


REM -------------------------------------------------------------------------
REM Optional ENV vars
REM -----------------
REM   START_OPTS - parameters passed to the Java VM when running Jetty
REM     e.g. to increase the memory allocated to the JVM to 1GB, use
REM       set START_OPTS=-Xmx1024m
REM   JETTY_PORT - the port on which to start Jetty, 8080 by default
REM   JETTY_STOP_PORT - the port on which Jetty listens for a Stop command, 8079 by default
REM -------------------------------------------------------------------------

setlocal EnableDelayedExpansion

set JETTY_HOME=jetty
if not defined XWIKI_OPTS set XWIKI_OPTS=-Xmx512m -XX:MaxPermSize=196m
set XWIKI_OPTS=%XWIKI_OPTS% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005

REM The port on which to start Jetty can be defined in an enviroment variable called JETTY_PORT
if not defined JETTY_PORT (
  REM Alternatively, it can be passed to this script as the first argument
  set JETTY_PORT=%1
  if not defined JETTY_PORT (
    set JETTY_PORT=8080
  )
)

REM The port on which Jetty listens for a Stop command can be defined in an enviroment variable called JETTY_STOP_PORT
if not defined JETTY_STOP_PORT (
  set JETTY_STOP_PORT=8079
)

echo Starting Jetty on port %JETTY_PORT%, please wait...

REM For enabling YourKit Profiling.
REM %3 must the path where Yourkit can find the agent.
REM For example: "C:\PROGRA~1\YOURKI~1.11\bin\win32"
REM No spaces are allowed in this path, 8.3 DOS path format should be used.
REM Use the "dir /X" command (in command prompt) within a directory to get 8.3 DOS path
if "%2"=="profiler" (
  set OLD_PATH=%PATH%
  set PATH=%3;%PATH%
  set JAVA_TOOL_OPTIONS=-agentlib:yjpagent
)

REM Get javaw.exe from the latest properly installed JRE
for /f tokens^=2^ delims^=^" %%i in ('reg query HKEY_CLASSES_ROOT\jarfile\shell\open\command /ve') do set JAVAW_PATH=%%i
set JAVA_PATH=%JAVAW_PATH:\javaw.exe=%\java.exe
if "%JAVA_PATH%"=="" set JAVA_PATH=java

REM Location where XWiki stores generated data and where database files are.
set XWIKI_DATA_DIR=${xwikiDataDir}
set XWIKI_OPTS=%XWIKI_OPTS% -Dxwiki.data.dir=%XWIKI_DATA_DIR%

REM Ensure the data directory exists so that XWiki can use it for storing permanent data.
if not exist %XWIKI_DATA_DIR% mkdir %XWIKI_DATA_DIR%

REM Ensure the logs directory exists as otherwise Jetty reports an error
if not exist %XWIKI_DATA_DIR%\logs mkdir %XWIKI_DATA_DIR%\logs

REM Specify port on which HTTP requests will be handled
set XWIKI_OPTS=%XWIKI_OPTS% -Djetty.port=%JETTY_PORT%

REM Specify Jetty's home directory
set XWIKI_OPTS=%XWIKI_OPTS% -Djetty.home=%JETTY_HOME%

REM Specify port and key to stop a running Jetty instance
set XWIKI_OPTS=%XWIKI_OPTS% -DSTOP.KEY=xwiki -DSTOP.PORT=%JETTY_STOP_PORT%

REM Specify the encoding to use
set XWIKI_OPTS=%XWIKI_OPTS% -Dfile.encoding=UTF8

REM In order to avoid getting a "java.lang.IllegalStateException: Form too large" error
REM when editing large page in XWiki we need to tell Jetty to allow for large content
REM since by default it only allows for 20K. We do this by passing the
REM org.eclipse.jetty.server.Request.maxFormContentSize property.
REM Note that setting this value too high can leave your server vulnerable to denial of
REM service attacks.
set XWIKI_OPTS=%XWIKI_OPTS% -Dorg.eclipse.jetty.server.Request.maxFormContentSize=1000000

set JETTY_CONFIGURATION_FILES=
for /r %%i in (%JETTY_HOME%\etc\jetty-*.xml) do set JETTY_CONFIGURATION_FILES=!JETTY_CONFIGURATION_FILES! "%%i"

"%JAVA_PATH%" %XWIKI_OPTS% %4 %5 %6 %7 %8 %9 -jar %JETTY_HOME%/start.jar %JETTY_HOME%/etc/jetty.xml %JETTY_CONFIGURATION_FILES%

if %2==profiler set PATH=%OLD_PATH%

REM Pause so that the command window used to run this script doesn't close automatically in case of problem
REM (like when the JDK/JRE is not installed)
PAUSE