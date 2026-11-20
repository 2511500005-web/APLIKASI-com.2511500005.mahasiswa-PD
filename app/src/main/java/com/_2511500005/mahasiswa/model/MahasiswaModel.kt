package com._2511500005.mahasiswa.model

// Bentuk respons umum dari server: {"status":..., "message":..., "data":...}
data class MahasiswaResponse(
    val status: Boolean,
    val message: String,
    val data: List<Mahasiswa>?
)

data class Mahasiswa(
    val id: Int,
    val nim: String,
    val nama: String,
    val jurusan: String
)

// Body yang dikirim saat tambah & ubah data
data class MahasiswaRequest(
    val nim: String,
    val nama: String,
    val jurusan: String
)
