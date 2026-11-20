# Troubleshooting (panduan sendiri)

Setiap pesan error di aplikasi sudah disertai petunjuk dari `Troubleshooter.kt`. Tabel ini adalah rangkuman lengkapnya.

| Gejala / pesan di aplikasi | Penyebab yang paling mungkin | Solusi |
|---|---|---|
| Tidak bisa tersambung ke server | Server PHP belum berjalan, atau BASE_URL salah | Jalankan `jalankan_server.bat`. Pastikan BASE_URL `http://10.0.2.2:8080/` untuk emulator |
| Server tidak merespons tepat waktu (timeout) | Firewall memblokir port 8080, HP dan laptop beda Wi-Fi, atau IP salah | Izinkan port 8080 di Windows Firewall. Samakan jaringan. Cek ulang IP dengan `ipconfig` |
| Alamat API tidak ditemukan (404) | Server dijalankan dari folder yang salah, atau nama file salah | Jalankan `jalankan_server.bat`, bukan dari folder lain. Nama file harus `mahasiswa.php` |
| Respons server bukan JSON | Ada error PHP (biasanya typo atau ekstensi PDO tidak aktif) | Buka `http://localhost:8080/mahasiswa.php` di browser dan baca pesan errornya |
| Server error (500) | Error saat akses database | Lihat jendela terminal server. Pastikan folder `backend` bisa ditulis |
| Data bentrok (409) / NIM sudah terdaftar | NIM yang dimasukkan sudah ada | Ini perilaku normal. Gunakan NIM lain |
| Data yang dikirim tidak valid (422) | Kolom NIM, nama, atau jurusan kosong | Isi semua kolom |
| Data tidak berubah setelah ubah atau hapus | Daftar belum dimuat ulang | Tekan Muat Ulang / Uji Koneksi |
| `php` tidak dikenali di terminal | PHP belum masuk PATH | Pakai path lengkap, misalnya `C:\xampp\php\php.exe` |
| Gradle sync gagal | Gradle wrapper atau koneksi internet | Jalankan `gradle wrapper` di Terminal Android Studio, atau pakai Gradle lokal |
| Build error "expecting identifier" pada nama package | Segmen package diawali angka, misalnya `com.2511500005` | Gunakan `com._2511500005.mahasiswa` (sudah diterapkan di project ini) |
| Ingin menguji dengan HP fisik tidak tersambung | BASE_URL masih 10.0.2.2 | Ganti dengan IP laptop, misalnya `http://192.168.1.10:8080/`, lalu Run ulang |
