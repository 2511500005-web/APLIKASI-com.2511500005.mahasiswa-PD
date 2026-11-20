@echo off
title Server API Data Mahasiswa
cd /d "%~dp0backend"
echo Menjalankan server di port 8080 (folder backend\api)
echo Tes di browser laptop : http://localhost:8080/mahasiswa.php
echo Emulator memakai      : http://10.0.2.2:8080/mahasiswa.php
echo Biarkan jendela ini tetap terbuka saat aplikasi dijalankan.
echo.
php -S 0.0.0.0:8080 -t api
pause
