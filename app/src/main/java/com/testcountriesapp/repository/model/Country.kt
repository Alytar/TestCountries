package com.testcountriesapp.repository.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Country(
    var id: Long? = null,
    @SerializedName("name") var countryName: String? = null,
    @SerializedName("alpha3Code") var countryCode: String? = null,
    @SerializedName("region") var region: String? = null,
    @SerializedName("borders") var borders: ArrayList<String>? = null,
    @SerializedName("flag") var flagPictureLink: String? = null
) : Serializable {

    fun getBordersText(): String {
        borders?.let {
            return it.joinToString(separator = ",")
        } ?: run {
            return ""
        }
    }
}
