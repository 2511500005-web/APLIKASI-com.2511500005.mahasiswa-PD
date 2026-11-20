package com._2511500005.mahasiswa.model

// Kontrak umum untuk semua respons server
interface ApiResponse {
    val status: Boolean
    val message: String
}

// GET semua / pencarian: data berbentuk ARRAY
data class MahasiswaResponse(
    override val status: Boolean,
    override val message: String,
    val data: List<Mahasiswa>?
) : ApiResponse

// GET by ID: data berbentuk OBJECT tunggal
data class MahasiswaDetailResponse(
    override val status: Boolean,
    override val message: String,
    val data: Mahasiswa?
) : ApiResponse

data class Mahasiswa(
    val id: Int,
    val nim: String,
    val nama: String,
    val jurusan: String
)

// Body untuk tambah & ubah
data class MahasiswaRequest(
    val nim: String,
    val nama: String,
    val jurusan: String
)