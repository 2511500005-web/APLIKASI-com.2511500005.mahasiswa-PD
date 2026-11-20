package com._2511500005.mahasiswa.api

import com._2511500005.mahasiswa.model.ApiResponse
import com._2511500005.mahasiswa.model.Mahasiswa
import com._2511500005.mahasiswa.model.MahasiswaRequest
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import retrofit2.Response

sealed class HasilApi<out T> {
    data class Sukses<out T>(val data: T, val pesan: String) : HasilApi<T>()
    data class Gagal(val pesan: String) : HasilApi<Nothing>()
}

object MahasiswaRepository {

    // GET semua data / pencarian
    suspend fun ambilSemua(keyword: String): HasilApi<List<Mahasiswa>> =
        panggil({ ApiConfig.service.getDaftarMahasiswa(keyword.takeIf { it.isNotBlank() }) }) {
            it.data ?: emptyList()
        }

    // GET by ID
    suspend fun ambilDetail(id: Int): HasilApi<Mahasiswa> =
        panggil({ ApiConfig.service.getDetailMahasiswa(id) }) { it.data }

    suspend fun tambah(req: MahasiswaRequest): HasilApi<Unit> =
        panggil({ ApiConfig.service.tambahMahasiswa(req) }) { Unit }

    suspend fun ubah(id: Int, req: MahasiswaRequest): HasilApi<Unit> =
        panggil({ ApiConfig.service.ubahMahasiswa(id, req) }) { Unit }

    suspend fun hapus(id: Int): HasilApi<Unit> =
        panggil({ ApiConfig.service.hapusMahasiswa(id) }) { Unit }

    private suspend fun <B : ApiResponse, T> panggil(
        blok: suspend () -> Response<B>,
        ambil: (B) -> T?
    ): HasilApi<T> {
        return try {
            val response = blok()
            val body = response.body()

            if (response.isSuccessful && body != null && body.status) {
                val hasil = ambil(body)
                if (hasil != null) {
                    HasilApi.Sukses(hasil, body.message)
                } else {
                    HasilApi.Gagal("Data dari server kosong.\nCek isi respons di Logcat (OkHttp).")
                }
            } else {
                val pesanServer = body?.message ?: pesanDariErrorBody(response.errorBody()?.string())
                HasilApi.Gagal(Troubleshooter.dariHttp(response.code(), pesanServer))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            HasilApi.Gagal(Troubleshooter.dariException(e))
        }
    }

    private fun pesanDariErrorBody(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return try {
            Gson().fromJson(raw, PesanError::class.java)?.message
        } catch (e: Exception) {
            null
        }
    }
}

// Hanya untuk membaca field "message" dari respons error
internal class PesanError(val message: String?)