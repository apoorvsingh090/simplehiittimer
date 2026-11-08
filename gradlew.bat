@rem
@rem Copyright (c) 2015-2023 by the Gradle project
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      http://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@echo off
setlocal local

set "APP_HOME=%~dp0"

set APP_NAME=gradlew
set "DATA_DIR=%APP_HOME%gradle"

set "JAVA_OPTS=%JAVA_OPTS% -Xmx4096m"
set "JAVA_HOME=%JAVA_HOME%"

if "%OS%"=="Windows_NT" set "DIRNAME=%~dp0"
if "%DIRNAME%" == "" set "DIRNAME=. "

set "WRAPPER_JAR=%DIRNAME%gradle\wrapper\gradle-wrapper.jar"

if not exist "%WRAPPER_JAR%" (
    echo.
    echo :: WARNING: Base Directory does not contain the Gradle wrapper! ::
    echo The gradlew command needs the gradle-wrapper.jar file to run.
    echo Please run 'gradle wrapper' to generate it, or open this project in Android Studio.
    exit /b 1
)

java %JAVA_OPTS% -cp "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
