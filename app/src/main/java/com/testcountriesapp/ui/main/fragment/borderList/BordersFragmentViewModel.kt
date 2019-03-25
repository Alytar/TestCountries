package com.testcountriesapp.ui.main.fragment.borderList

import androidx.lifecycle.MutableLiveData
import com.testcountriesapp.architecture.BaseViewModel
import com.testcountriesapp.repository.Repository
import com.testcountriesapp.repository.model.Country

class BordersFragmentViewModel(
    val repository: Repository
) : BaseViewModel() {

    val countriesLiveData = MutableLiveData<ArrayList<Country>>()

    fun loadBorderCountries(id: Long) {
        showLoading()
        countriesLiveData.postValue(repository.getBorderCountries(id))
        hideLoading()
    }
}