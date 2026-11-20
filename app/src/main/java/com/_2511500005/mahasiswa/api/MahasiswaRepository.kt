package com._2511500005.mahasiswa.api

import com._2511500005.mahasiswa.model.Mahasiswa
import com._2511500005.mahasiswa.model.MahasiswaRequest
import com._2511500005.mahasiswa.model.MahasiswaResponse
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import retrofit2.Response

sealed class HasilApi<out T> {
    data class Sukses<out T>(val data: T, val pesan: String) : HasilApi<T>()
    data class Gagal(val pesan: String) : HasilApi<Nothing>()
}

object MahasiswaRepository {

    suspend fun ambilSemua(keyword: String): HasilApi<List<Mahasiswa>> =
        panggil({ ApiConfig.service.getDaftarMahasiswa(keyword.takeIf { it.isNotBlank() }) }) {
            it.data ?: emptyList()
        }

    suspend fun tambah(req: MahasiswaRequest): HasilApi<Unit> =
        panggil({ ApiConfig.service.tambahMahasiswa(req) }) { }

    suspend fun ubah(id: Int, req: MahasiswaRequest): HasilApi<Unit> =
        panggil({ ApiConfig.service.ubahMahasiswa(id, req) }) { }

    suspend fun hapus(id: Int): HasilApi<Unit> =
        panggil({ ApiConfig.service.hapusMahasiswa(id) }) { }

    private suspend fun <T> panggil(
        blok: suspend () -> Response<MahasiswaResponse>,
        ambil: (MahasiswaResponse) -> T
    ): HasilApi<T> {
        return try {
            val response = blok()
            val body = response.body()

            if (response.isSuccessful && body != null && body.status) {
                HasilApi.Sukses(ambil(body), body.message)
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
            Gson().fromJson(raw, MahasiswaResponse::class.java)?.message
        } catch (e: Exception) {
            null
        }
    }
}
