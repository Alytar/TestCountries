package com.testcountriesapp.ui.main.fragment.mainList

import android.content.DialogInterface
import androidx.lifecycle.MutableLiveData
import com.testcountriesapp.architecture.BaseViewModel
import com.testcountriesapp.general.extension.bind
import com.testcountriesapp.general.liveData.SingleLiveEvent
import com.testcountriesapp.general.rx.SchedulerProvider
import com.testcountriesapp.repository.Repository
import com.testcountriesapp.repository.model.Country
import com.testcountriesapp.repository.remote.error.GeneralErrorHandler
import io.reactivex.functions.Consumer

class MainFragmentViewModel(
    val repository: Repository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val countriesLiveData = MutableLiveData<ArrayList<Country>>()
    val scrollLiveData = SingleLiveEvent<Pair<Int, Int>>()

    fun loadCountries() {
        showLoading()
        if (isCountriesExistInDb().not()) {
            repository.loadAllCountries()
                .observeOn(schedulerProvider.ui())
                .doOnEach { if (it.isOnError || it.isOnNext) hideLoading() }
                .subscribe(
                    Consumer {
                        loadCountriesSuccess(true, it)

                    },
                    GeneralErrorHandler {
                        if (it.connectionError) {
                            showError(it.message, retryListener = {
                                DialogInterface.OnClickListener { _, _ -> loadCountries() }
                            })
                        } else {
                            showError(it.message)
                        }
                    }
                ).bind(disposables)
        } else {
            loadCountriesSuccess(false, repository.getCountries())
        }
    }

    fun scrollToItem(position : Int, offset: Int){
        scrollLiveData.postValue(Pair(position, offset))
    }

    private fun loadCountriesSuccess(needAddToDb: Boolean, countriesList: ArrayList<Country>) {
        if (needAddToDb) {
            repository.saveCountries(countriesList)
        }
        countriesLiveData.postValue(repository.getCountries())
        hideLoading()
    }

    fun isCountriesExistInDb(): Boolean {
        return repository.isCountriesExistInDb()
    }

}