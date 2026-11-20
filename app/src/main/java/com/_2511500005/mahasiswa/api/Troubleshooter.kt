package com._2511500005.mahasiswa.api

import com.google.gson.JsonParseException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// Troubleshooting buatan sendiri: mengubah error teknis menjadi pesan yang bisa langsung ditindaklanjuti.
object Troubleshooter {

    fun dariException(e: Throwable): String = when (e) {
        is SocketTimeoutException ->
            "Server tidak merespons tepat waktu.\n" +
                "Cek: (1) server PHP masih berjalan di laptop, " +
                "(2) HP/emulator dan laptop satu jaringan Wi-Fi, " +
                "(3) port 8080 diizinkan firewall."

        is ConnectException ->
            "Tidak bisa tersambung ke server.\n" +
                "Cek: (1) jendela server PHP sudah berjalan, " +
                "(2) BASE_URL di ApiConfig.kt sesuai (emulator 10.0.2.2, HP fisik IP laptop)."

        is UnknownHostException ->
            "Alamat server tidak dikenali.\nPeriksa penulisan BASE_URL di ApiConfig.kt."

        is JsonParseException ->
            "Respons server bukan JSON yang valid.\n" +
                "Buka http://localhost:8080/mahasiswa.php di browser laptop untuk melihat pesan error PHP."

        is IOException ->
            "Gagal membaca data dari jaringan.\nPastikan Wi-Fi atau koneksi laptop aktif."

        else ->
            "Terjadi kesalahan tak terduga: ${e.localizedMessage ?: e.javaClass.simpleName}"
    }

    fun dariHttp(kode: Int, pesanServer: String?): String {
        val detail = pesanServer?.let { "\nPesan server: $it" } ?: ""
        return when (kode) {
            400, 422 -> "Data yang dikirim belum lengkap atau tidak valid ($kode).$detail"
            404 -> "Alamat API tidak ditemukan (404).\n" +
                "Cek BASE_URL di ApiConfig.kt dan pastikan server dijalankan dengan " +
                "'php -S 0.0.0.0:8080 -t api' dari folder backend.$detail"
            405 -> "Metode permintaan tidak didukung server (405).$detail"
            409 -> "Data bentrok dengan data yang sudah ada (409).$detail"
            500 -> "Server mengalami error internal (500).\n" +
                "Lihat jendela terminal tempat server PHP berjalan.$detail"
            else -> "Server menolak permintaan (HTTP $kode).$detail"
        }
    }
}
