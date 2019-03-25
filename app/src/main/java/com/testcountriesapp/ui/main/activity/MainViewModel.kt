package com.testcountriesapp.ui.main.activity

import androidx.lifecycle.MutableLiveData
import com.testcountriesapp.architecture.BaseViewModel
import com.testcountriesapp.general.extension.bind
import com.testcountriesapp.general.liveData.SingleLiveEvent
import com.testcountriesapp.general.rx.SchedulerProvider
import com.testcountriesapp.repository.Repository
import com.testcountriesapp.util.NetworkObservableStrategy
import greyfox.rxnetwork.RxNetwork

class MainViewModel(
    val repository: Repository,
    val schedulerProvider: SchedulerProvider,
    val rxNetwork: RxNetwork
) : BaseViewModel() {

    val openScreenEvent = SingleLiveEvent<MainActivity.Screen>()
    val isKeyboardOpened = MutableLiveData<Boolean>()

    init {
        initNetworkListener()
    }

    private fun initNetworkListener() {
        rxNetwork.observeInternetAccess(NetworkObservableStrategy.getStrategy())
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe {
                if (isCountriesExistInDb().not()){
                    hasInternetConnectionEvent.postValue(it)
                }
            }.bind(disposables)
    }


    private fun isCountriesExistInDb(): Boolean {
        return repository.isCountriesExistInDb()
    }
}