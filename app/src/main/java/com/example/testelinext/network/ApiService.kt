package com.example.testelinext.network

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("{width}/{height}")
    fun getPhoto(
        @Path("width") width: Int,
        @Path("height") height: Int
    ): Single<Response<ResponseBody>>
}