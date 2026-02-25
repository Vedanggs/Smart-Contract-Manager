# Set Java
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# Set Environment Variables
$env:EMAIL_HOST = "smtp.gmail.com"
$env:EMAIL_PORT = "587"
$env:EMAIL_USERNAME = "test@example.com"
$env:EMAIL_PASSWORD = "testpassword"
$env:EMAIL_DOMAIN = "test@example.com"
$env:CLOUDINARY_API_KEY = "739649774718216"
$env:CLOUDINARY_API_SECRET = "dJN_DrutbRXfApRkrFQccRTGFNE"
$env:CLOUDINARY_NAME = "test"
$env:GOOGLE_CLIENT_ID = "test"
$env:GOOGLE_CLIENT_SECRET = "test"
$env:GITHUB_CLIENT_ID = "test"
$env:GITHUB_CLIENT_SECRET = "test"

# Check if JAR exists, if not build it
$jarPath = "target/scm2.0-0.0.1-SNAPSHOT.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "JAR file not found. Building project first..." -ForegroundColor Yellow
    .\mvnw clean package -DskipTests
    if (-not (Test-Path $jarPath)) {
        Write-Host "Build failed! Please check for errors above." -ForegroundColor Red
        exit 1
    }
    Write-Host "Build successful!" -ForegroundColor Green
}

# Run Application
Write-Host "Starting SCM Application on http://localhost:8081 ..." -ForegroundColor Green
java -jar $jarPath