package com.testcountriesapp.repository.local

import com.testcountriesapp.general.extension.krealmextensions.*
import com.testcountriesapp.repository.local.dbModel.CountryDbModel
import com.testcountriesapp.repository.model.Country
import io.realm.Realm

interface LocalDataSource {

    fun saveCountries(countries: ArrayList<Country>)

    fun findCountries(codes: ArrayList<String>): ArrayList<Country>
    fun getCountries(): ArrayList<Country>
    fun isCountriesExistInDb(): Boolean
    fun getBorderCountries(id: Long): ArrayList<Country>
}

class AppLocalDataSource(private val realm: Realm) : LocalDataSource {


    override fun isCountriesExistInDb(): Boolean {
        return CountryDbModel().queryFirst() != null
    }

    override fun getCountries(): ArrayList<Country> {
        return CountryDbModel().wrapAllToModel(CountryDbModel().queryAll())
    }

    override fun getBorderCountries(id: Long): ArrayList<Country> {
        val countryDb: CountryDbModel? = CountryDbModel().queryFirst { equalTo("id", id) }
        val listBorders = ArrayList<Country>()
        countryDb?.let {
            for (value in countryDb.getBorderArrayList()) {
                val country: CountryDbModel? = CountryDbModel().queryFirst { equalTo("countryCode", value) }
                if (country != null) {
                    listBorders.add(country.wrapToModel())
                }
            }
        }
        return listBorders
    }

    override fun saveCountries(countries: ArrayList<Country>) =
        CountryDbModel().wrapAllToDb(countries).saveAll()


    override fun findCountries(codes: ArrayList<String>): ArrayList<Country> {
        val listCountries = ArrayList<Country>()
        for (value in codes) {
            val item = CountryDbModel().queryFirst { equalTo("countryCode", value) }?.wrapToModel()
            item?.let { listCountries.add(it) }
        }
        return listCountries
    }
}