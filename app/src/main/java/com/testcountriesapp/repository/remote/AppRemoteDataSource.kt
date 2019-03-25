package com.testcountriesapp.repository.remote

import com.testcountriesapp.repository.model.Country
import io.reactivex.Flowable

interface RemoteDateSource : ApiService

class AppRemoteDataSource(private val service: ApiService) : RemoteDateSource {

    override fun loadAllCountries(): Flowable<ArrayList<Country>> {
        return service.loadAllCountries()
    }
}