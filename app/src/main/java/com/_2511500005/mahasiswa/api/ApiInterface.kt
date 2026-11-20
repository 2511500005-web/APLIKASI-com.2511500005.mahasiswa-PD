package com._2511500005.mahasiswa.api

import com._2511500005.mahasiswa.model.MahasiswaDetailResponse
import com._2511500005.mahasiswa.model.MahasiswaRequest
import com._2511500005.mahasiswa.model.MahasiswaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiInterface {

    // search = null -> semua data; search diisi -> hasil pencarian (data ARRAY)
    @GET("mahasiswa.php")
    suspend fun getDaftarMahasiswa(
        @Query("search") search: String?
    ): Response<MahasiswaResponse>

    // GET by ID -> data OBJECT tunggal
    @GET("mahasiswa.php")
    suspend fun getDetailMahasiswa(
        @Query("id") id: Int
    ): Response<MahasiswaDetailResponse>

    @POST("mahasiswa.php")
    suspend fun tambahMahasiswa(
        @Body body: MahasiswaRequest
    ): Response<MahasiswaResponse>

    @PUT("mahasiswa.php")
    suspend fun ubahMahasiswa(
        @Query("id") id: Int,
        @Body body: MahasiswaRequest
    ): Response<MahasiswaResponse>

    @DELETE("mahasiswa.php")
    suspend fun hapusMahasiswa(
        @Query("id") id: Int
    ): Response<MahasiswaResponse>
}