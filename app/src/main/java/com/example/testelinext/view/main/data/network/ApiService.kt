package com.example.testelinext.view.main.data.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("{width}/{height}")
    fun getPhoto(
        @Path("width") width: Int,
        @Path("height") height: Int
    ): Call<ResponseBody>
}