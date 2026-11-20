# Aplikasi Data Mahasiswa (Android + PHP REST API)

Package: `com._2511500005.mahasiswa`
Backend memakai SQLite, jadi tidak perlu MySQL, phpMyAdmin, ataupun import SQL.

## Struktur

```
MahasiswaApp/
├── app/                  Project Android (buka folder MahasiswaApp di Android Studio)
├── backend/
│   ├── api/mahasiswa.php Endpoint REST API
│   ├── lib/db.php        Koneksi SQLite + helper JSON
│   └── data/             Dibuat otomatis: mahasiswa.sqlite
├── jalankan_server.bat   Menjalankan server API (Windows)
├── TROUBLESHOOTING.md    Panduan masalah umum
└── README.md
```

## 1. Jalankan server API

1. Pastikan PHP tersedia. Jika memakai XAMPP, PHP ada di `C:\xampp\php\php.exe`. Ekstensi `pdo_sqlite` biasanya sudah aktif.
2. Double-click `jalankan_server.bat`. Biarkan jendelanya terbuka.
3. Tes di browser laptop: `http://localhost:8080/mahasiswa.php`. Jika muncul JSON berisi daftar mahasiswa, server sudah siap.

Jika `php` tidak dikenali, jalankan dengan path lengkap, misalnya:
`C:\xampp\php\php.exe -S 0.0.0.0:8080 -t api` (dari dalam folder `backend`).

## 2. Buka project di Android Studio

1. File > Open > pilih folder `MahasiswaApp`.
2. Tunggu Gradle sync selesai. Jika muncul error terkait `gradle-wrapper.jar`, buka Terminal di Android Studio lalu jalankan `gradle wrapper`, atau pilih Gradle lokal di File > Settings > Build, Execution, Deployment > Build Tools > Gradle.

## 3. Atur alamat server

File: `app/src/main/java/com/_2511500005/mahasiswa/api/ApiConfig.kt`

- Emulator Android Studio: `BASE_URL = "http://10.0.2.2:8080/"` (sudah diatur).
- HP fisik: cek IP laptop dengan perintah `ipconfig`, lalu ganti BASE_URL, misalnya `"http://192.168.1.10:8080/"`. HP dan laptop harus satu Wi-Fi.

## 4. Jalankan aplikasi

Pilih emulator (disarankan Pixel 6, API 35) atau HP fisik, lalu tekan Run. Tekan tombol **Muat Ulang / Uji Koneksi**. Status berwarna hijau berarti koneksi berhasil.

## Fitur

| Fitur | Cara |
|---|---|
| Lihat daftar | Otomatis saat aplikasi dibuka, atau tombol Muat Ulang |
| Cari nama / NIM | Isi kolom pencarian lalu tekan Cari |
| Tambah | Tombol + di pojok kanan bawah |
| Ubah | Tap item mahasiswa |
| Hapus | Tekan lama item mahasiswa |

## Endpoint API

| Method | URL | Keterangan |
|---|---|---|
| GET | `/mahasiswa.php` | Semua data |
| GET | `/mahasiswa.php?search=teks` | Cari nama atau NIM |
| POST | `/mahasiswa.php` | Tambah (body JSON: nim, nama, jurusan) |
| PUT | `/mahasiswa.php?id=1` | Ubah (body JSON: nim, nama, jurusan) |
| DELETE | `/mahasiswa.php?id=1` | Hapus |

Format respons: `{"status": true, "message": "...", "data": ...}`
