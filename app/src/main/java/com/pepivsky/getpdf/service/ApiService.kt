package com.pepivsky.getpdf.service

import com.pepivsky.getpdf.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("members/requirements/upload")
    fun uploadFile(
            @Part file: MultipartBody.Part,
            @Header("Authorization") token: String,
            @Part("id") id: RequestBody


    ): Call<UploadResponse>


}