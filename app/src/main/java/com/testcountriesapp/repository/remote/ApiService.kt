package com.testcountriesapp.repository.remote

import com.testcountriesapp.repository.model.Country
import io.reactivex.Flowable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiService {

    @GET("/rest/v2/all")
    fun loadAllCountries(): Flowable<ArrayList<Country>>

}