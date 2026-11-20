<?php
declare(strict_types=1);

require_once __DIR__ . '/../lib/db.php';

$pdo = getDb();

try {
    switch ($_SERVER['REQUEST_METHOD'] ?? 'GET') {
        case 'GET':
            // ?id=... -> detail satu data (data berbentuk OBJECT)
            // tanpa id -> daftar / pencarian (data berbentuk ARRAY)
            if (isset($_GET['id'])) {
                ambilDetail($pdo, ambilId());
            } else {
                ambilSemua($pdo);
            }
            break;
        case 'POST':
            tambahData($pdo);
            break;
        case 'PUT':
            ubahData($pdo);
            break;
        case 'DELETE':
            hapusData($pdo);
            break;
        default:
            kirimJson(false, 'Metode tidak didukung', null, 405);
    }
} catch (PDOException $e) {
    kirimJson(false, 'Kesalahan database: ' . $e->getMessage(), null, 500);
}

/* ---------- GET: daftar / cari mahasiswa ---------- */
function ambilSemua(PDO $pdo): void
{
    $cari = trim((string) ($_GET['search'] ?? ''));

    if ($cari !== '') {
        $stmt = $pdo->prepare(
            'SELECT id, nim, nama, jurusan FROM mahasiswa
             WHERE nama LIKE :q OR nim LIKE :q ORDER BY nama'
        );
        $stmt->execute([':q' => '%' . $cari . '%']);
    } else {
        $stmt = $pdo->query('SELECT id, nim, nama, jurusan FROM mahasiswa ORDER BY nama');
    }

    $rows = array_map(static fn(array $r): array => formatMhs($r), $stmt->fetchAll());
    kirimJson(true, 'Berhasil mengambil ' . count($rows) . ' data', $rows);
}

/* ---------- GET by ID: detail satu mahasiswa ---------- */
function ambilDetail(PDO $pdo, int $id): void
{
    $stmt = $pdo->prepare('SELECT id, nim, nama, jurusan FROM mahasiswa WHERE id = ?');
    $stmt->execute([$id]);
    $row = $stmt->fetch();

    if ($row === false) {
        kirimJson(false, "Data dengan id $id tidak ditemukan", null, 404);
    }
    kirimJson(true, 'Berhasil mengambil detail data', formatMhs($row));
}

/* ---------- POST: tambah mahasiswa ---------- */
function tambahData(PDO $pdo): void
{
    [$nim, $nama, $jurusan] = validasi(bacaBody());

    try {
        $stmt = $pdo->prepare('INSERT INTO mahasiswa (nim, nama, jurusan) VALUES (?, ?, ?)');
        $stmt->execute([$nim, $nama, $jurusan]);
    } catch (PDOException $e) {
        cekDuplikat($e, $nim);
    }

    kirimJson(true, 'Data berhasil ditambahkan', null, 201);
}

/* ---------- PUT: ubah mahasiswa berdasarkan id ---------- */
function ubahData(PDO $pdo): void
{
    $id = ambilId();
    [$nim, $nama, $jurusan] = validasi(bacaBody());

    try {
        $stmt = $pdo->prepare('UPDATE mahasiswa SET nim = ?, nama = ?, jurusan = ? WHERE id = ?');
        $stmt->execute([$nim, $nama, $jurusan, $id]);
    } catch (PDOException $e) {
        cekDuplikat($e, $nim);
    }

    if ($stmt->rowCount() === 0) {
        kirimJson(false, "Data dengan id $id tidak ditemukan", null, 404);
    }

    kirimJson(true, 'Data berhasil diubah');
}

/* ---------- DELETE: hapus mahasiswa berdasarkan id ---------- */
function hapusData(PDO $pdo): void
{
    $id = ambilId();

    $stmt = $pdo->prepare('DELETE FROM mahasiswa WHERE id = ?');
    $stmt->execute([$id]);

    if ($stmt->rowCount() === 0) {
        kirimJson(false, "Data dengan id $id tidak ditemukan", null, 404);
    }

    kirimJson(true, 'Data berhasil dihapus');
}

/* ---------- Fungsi bantu ---------- */
function formatMhs(array $r): array
{
    return [
        'id'      => (int) $r['id'],
        'nim'     => (string) $r['nim'],
        'nama'    => (string) $r['nama'],
        'jurusan' => (string) $r['jurusan'],
    ];
}

function bacaBody(): array
{
    $raw = file_get_contents('php://input');
    $data = json_decode($raw ?: '', true);
    if (!is_array($data)) {
        kirimJson(false, 'Body harus berupa JSON yang valid', null, 400);
    }
    return $data;
}

function validasi(array $input): array
{
    $nim     = trim((string) ($input['nim'] ?? ''));
    $nama    = trim((string) ($input['nama'] ?? ''));
    $jurusan = trim((string) ($input['jurusan'] ?? ''));

    if ($nim === '' || $nama === '' || $jurusan === '') {
        kirimJson(false, 'NIM, nama, dan jurusan wajib diisi', null, 422);
    }
    return [$nim, $nama, $jurusan];
}

function ambilId(): int
{
    $id = (int) ($_GET['id'] ?? 0);
    if ($id <= 0) {
        kirimJson(false, 'Parameter id wajib diisi dan harus angka positif', null, 400);
    }
    return $id;
}

function cekDuplikat(PDOException $e, string $nim): void
{
    if (stripos($e->getMessage(), 'UNIQUE') !== false) {
        kirimJson(false, "NIM $nim sudah terdaftar", null, 409);
    }
    throw $e;
}