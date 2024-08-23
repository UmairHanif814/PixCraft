package com.example.pixcraft.api

import com.example.pixcraft.models.PixCraftModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface PixCraftApi {
    @GET("/v1/curated?per_page=100")
    @Headers("Authorization: JU57RmUpSMum6qWruiVCg3C4fSvvhOZszSq4zHQkDZjynqBF0T0hODGx")
    suspend fun getImages():Response<PixCraftModel?>
}