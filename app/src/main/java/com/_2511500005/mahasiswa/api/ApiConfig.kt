package com._2511500005.mahasiswa.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {

    // Emulator Android Studio: 10.0.2.2 adalah alamat localhost laptop.
    // HP fisik: ganti dengan IP laptop (lihat ipconfig), contoh "http://192.168.1.10:8080/"
    // Wajib diakhiri tanda "/"
    const val BASE_URL = "http://10.0.2.2:8080/"

    val service: ApiInterface by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}
