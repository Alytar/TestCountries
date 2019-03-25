package com.testcountriesapp.repository.local.dbModel

import com.testcountriesapp.repository.RepositoryDefaultParams
import com.testcountriesapp.repository.model.Country
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CountryDbModel(
    @PrimaryKey var id: Long? = null,
    var countryName: String? = null,
    var countryCode: String? = null,
    var region: String? = null,
    var flagPictureLink: String? = null,
    var borders: String = RepositoryDefaultParams.DEFAULT_STRING
) : RealmObject() {

    companion object {
        var idCount: Long = 0
    }

     fun getBorderArrayList(): ArrayList<String> {
        return borders.split(",").toCollection(ArrayList())
    }

    fun wrapToDb(country: Country) = this.apply {
        countryName = country.countryName
        countryCode = country.countryCode
        region = country.region
        flagPictureLink = country.flagPictureLink
        borders = country.getBordersText()
    }

    fun wrapAllToDb(countries: ArrayList<Country>): ArrayList<CountryDbModel> {
        val listDb = ArrayList<CountryDbModel>()
        for (value in countries) {
            val countryDbModel = CountryDbModel(
                idCount++, value.countryName,
                value.countryCode, value.region, value.flagPictureLink, value.getBordersText()
            )
            listDb.add(countryDbModel)
        }
        return listDb
    }

    fun wrapToModel(): Country = Country(
        id,
        countryName,
        countryCode,
        region,
        getBorderArrayList(),
        flagPictureLink
    )

    fun wrapAllToModel(listDb: List<CountryDbModel>): ArrayList<Country> {
        val list = ArrayList<Country>()
        for (value in listDb) {
            list.add(value.wrapToModel())
        }
        return list
    }
}