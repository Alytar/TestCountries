package com.testcountriesapp.di
import com.testcountriesapp.general.rx.ApplicationSchedulerProvider
import com.testcountriesapp.general.rx.SchedulerProvider
import com.testcountriesapp.repository.AppRepository
import com.testcountriesapp.repository.Repository
import com.testcountriesapp.repository.local.AppLocalDataSource
import com.testcountriesapp.repository.local.LocalDataSource
import com.testcountriesapp.repository.remote.AppRemoteDataSource
import com.testcountriesapp.repository.remote.RemoteDateSource
import com.testcountriesapp.ui.main.activity.MainViewModel
import com.testcountriesapp.ui.main.fragment.borderList.BordersFragmentViewModel
import com.testcountriesapp.ui.main.fragment.mainList.MainFragmentViewModel
import greyfox.rxnetwork.RxNetwork
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { MainFragmentViewModel(get(), get()) }
    viewModel { BordersFragmentViewModel(get()) }
}

val repositoryModule = module {
    // provide Repository implementation
    single { AppRepository(get(), get()) as Repository }

    // provide AppRemoteDataSource
    single { AppRemoteDataSource(get()) as RemoteDateSource }

    // provide AppLocalDataSource
    single { AppLocalDataSource(get()) as LocalDataSource }
}

val rxModule = module {

    fun makeRxNetwork(context: android.content.Context, schedulerProvider: SchedulerProvider): RxNetwork {
        return RxNetwork.builder().defaultScheduler(schedulerProvider.io()).init(context)
    }

    // provided ScheduleProvider
    single { ApplicationSchedulerProvider() as SchedulerProvider }

    // provided RxNetwork observer
    single { makeRxNetwork(get(), get()) }
}