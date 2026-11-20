<?php
declare(strict_types=1);

/**
 * Koneksi database SQLite.
 * Tanpa MySQL, tanpa phpMyAdmin, tanpa import SQL.
 * File database dibuat otomatis di backend/data/mahasiswa.sqlite saat pertama kali diakses.
 */
function getDb(): PDO
{
    $dir = __DIR__ . '/../data';
    if (!is_dir($dir) && !mkdir($dir, 0775, true) && !is_dir($dir)) {
        kirimJson(false, 'Folder data tidak bisa dibuat. Periksa izin folder backend.', null, 500);
    }

    $pdo = new PDO('sqlite:' . $dir . '/mahasiswa.sqlite');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);

    $pdo->exec('CREATE TABLE IF NOT EXISTS mahasiswa (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        nim TEXT NOT NULL UNIQUE,
        nama TEXT NOT NULL,
        jurusan TEXT NOT NULL
    )');

    // Isi data contoh hanya jika tabel masih kosong
    if ((int) $pdo->query('SELECT COUNT(*) FROM mahasiswa')->fetchColumn() === 0) {
        $awal = [
            ['2511500001', 'Andi Pratama', 'Teknik Informatika'],
            ['2511500002', 'Siti Nurhaliza', 'Sistem Informasi'],
            ['2511500003', 'Budi Santoso', 'Teknik Informatika'],
            ['2511500004', 'Dewi Lestari', 'Sistem Informasi'],
        ];
        $stmt = $pdo->prepare('INSERT INTO mahasiswa (nim, nama, jurusan) VALUES (?, ?, ?)');
        foreach ($awal as $baris) {
            $stmt->execute($baris);
        }
    }

    return $pdo;
}

/**
 * Mengirim respons JSON dengan format: {"status", "message", "data"}
 */
function kirimJson(bool $status, string $message, $data = null, int $kode = 200): void
{
    http_response_code($kode);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(
        ['status' => $status, 'message' => $message, 'data' => $data],
        JSON_UNESCAPED_UNICODE
    );
    exit;
}
