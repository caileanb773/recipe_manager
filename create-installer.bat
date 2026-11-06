
@echo off
setlocal

set "PROJECT_DIR=%~dp0"
set "INPUT_DIR=%PROJECT_DIR%target"
set "OUTPUT_DIR=%PROJECT_DIR%dist"
set "APP_IMAGE_DIR=%OUTPUT_DIR%\PrepLedger"

cd /d "%PROJECT_DIR%"

echo ========================================
echo       PrepLedger Release Builder
echo ========================================

echo.
echo Reading Maven project configuration...

for /f "delims=" %%V in ('powershell -NoProfile -Command "[xml]$pom = Get-Content '%PROJECT_DIR%pom.xml'; $pom.project.version"') do set "APP_VERSION=%%V"

for /f "delims=" %%A in ('powershell -NoProfile -Command "[xml]$pom = Get-Content '%PROJECT_DIR%pom.xml'; $pom.project.artifactId"') do set "ARTIFACT_ID=%%A"

set "JAR_NAME=%ARTIFACT_ID%-%APP_VERSION%"

if not defined APP_VERSION (
    echo ERROR: Could not determine project version.
    exit /b 1
)

if not defined JAR_NAME (
    echo ERROR: Could not determine JAR filename.
    exit /b 1
)

echo Version: %APP_VERSION%
echo JAR: %JAR_NAME%.jar

echo.
echo [1/4] Building Maven project...

call mvn clean package

if errorlevel 1 (
    echo ERROR: Maven build failed.
    exit /b 1
)

if not exist "%INPUT_DIR%\%JAR_NAME%.jar" (
    echo ERROR: Application JAR not found.
    exit /b 1
)

if not exist "%INPUT_DIR%\lib" (
    echo ERROR: Dependency directory not found.
    exit /b 1
)

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"
if exist "%APP_IMAGE_DIR%" rmdir /s /q "%APP_IMAGE_DIR%"

echo.
echo [2/4]Detecting required Java modules...

for /f "delims=" %%M in ('jdeps --multi-release 17 --ignore-missing-deps --recursive --print-module-deps --module-path "%INPUT_DIR%\lib" --add-modules javafx.controls,javafx.fxml "%INPUT_DIR%\%JAR_NAME%.jar"') do set "DETECTED_MODULES=%%M"

if not defined DETECTED_MODULES (
    echo ERROR: Could not detect required Java modules.
    exit /b 1
)

set "DETECTED_MODULES=%DETECTED_MODULES%,javafx.controls,javafx.fxml,jdk.crypto.ec"

echo Modules: %DETECTED_MODULES%

echo.
echo [3/4] Creating application image...

jpackage ^
    --type app-image ^
    --name PrepLedger ^
    --app-version "%APP_VERSION%" ^
    --input "%INPUT_DIR%" ^
    --main-jar "%JAR_NAME%.jar" ^
    --main-class ca.prepledger.app.Main ^
    --module-path "%INPUT_DIR%\lib" ^
	--add-modules "%DETECTED_MODULES%" ^
	--win-console ^
    --dest "%OUTPUT_DIR%"

if errorlevel 1 (
    echo ERROR: Application image creation failed.
    exit /b 1
)

echo.
echo [4/4] Creating Windows installer...

if exist "%OUTPUT_DIR%\PrepLedger-%APP_VERSION%.exe" (
    del /q "%OUTPUT_DIR%\PrepLedger-%APP_VERSION%.exe"
)

jpackage ^
    --type exe ^
    --name PrepLedger ^
    --app-version "%APP_VERSION%" ^
    --app-image "%APP_IMAGE_DIR%" ^
    --win-shortcut ^
    --win-menu ^
    --win-dir-chooser ^
    --dest "%OUTPUT_DIR%"

if errorlevel 1 (
    echo ERROR: Installer creation failed.
    exit /b 1
)

echo.
echo ========================================
echo       BUILD COMPLETED SUCCESSFULLY
echo ========================================
echo.
echo Version: %APP_VERSION%
echo Application image: %APP_IMAGE_DIR%
echo Installer: %OUTPUT_DIR%\PrepLedger-%APP_VERSION%.exe

endlocal
