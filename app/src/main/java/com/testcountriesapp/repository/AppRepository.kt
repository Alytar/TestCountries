package com.testcountriesapp.repository
import com.testcountriesapp.repository.local.LocalDataSource
import com.testcountriesapp.repository.model.Country
import com.testcountriesapp.repository.remote.RemoteDateSource
import com.testcountriesapp.util.NetworkConnectivity
import io.reactivex.Flowable

interface Repository {

    /**
     * Remote
     */
    fun loadAllCountries(): Flowable<ArrayList<Country>>


    /**
     * Local
     */
    fun saveCountries(countries: ArrayList<Country>)
    fun getCountries(): ArrayList<Country>
    fun getBorderCountries(id: Long): ArrayList<Country>
    fun isCountriesExistInDb(): Boolean
}

class AppRepository constructor(
    private val remoteDataSource: RemoteDateSource,
    private val localDataSource: LocalDataSource
) : Repository {


    /**
     * Remote
     */

    override fun loadAllCountries(): Flowable<ArrayList<Country>> =
        NetworkConnectivity.getStateFlowable(true)
            .flatMap {
                remoteDataSource.loadAllCountries()
            }

    /**
     * Local
     */

    override fun saveCountries(countries: ArrayList<Country>) {
        localDataSource.saveCountries(countries)
    }

    override fun getCountries(): ArrayList<Country> {
        return localDataSource.getCountries()
    }

    override fun isCountriesExistInDb(): Boolean {
        return localDataSource.isCountriesExistInDb()
    }

    override fun getBorderCountries(id: Long): ArrayList<Country> {
        return localDataSource.getBorderCountries(id)
    }
}
