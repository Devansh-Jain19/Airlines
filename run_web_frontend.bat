@echo off
title AeroSkyline Web Frontend
echo Starting AeroSkyline Web Frontend on http://localhost:3000 ...
cd /d "%~dp0web-frontend"

where python >nul 2>nul
if %ERRORLEVEL% equ 0 (
    python -m http.server 3000
) else (
    npx -y serve -p 3000 .
)
pause
