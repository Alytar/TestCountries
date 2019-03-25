package com.testcountriesapp

import android.app.Application
import android.content.Context
import android.os.Looper
import com.akaita.java.rxjava2debug.RxJava2Debug
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import com.testcountriesapp.di.*
import com.testcountriesapp.general.CrashlyticsCrashReportingTree
import com.testcountriesapp.util.LocalizedContextProvider
import greyfox.rxnetwork.RxNetwork
import io.fabric.sdk.android.Fabric
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.realm.Realm
import org.koin.android.ext.android.setProperty
import org.koin.android.ext.android.startKoin
import org.koin.core.Koin
import org.koin.standalone.KoinComponent
import timber.log.Timber


/**
 * Created by Oleg Vovk on 3/17/19.
 */
class TestCountriesApp : Application(), KoinComponent {

    private val modules = listOf(
        repositoryModule,
        viewModelModule,
        remoteDataSourceModule,
        localDataSourceModule,
        rxModule
    )

    init {
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        var localizedContext = base
        base?.let {
            localizedContext = LocalizedContextProvider.getLocalizedContext(it)
        }

        super.attachBaseContext(localizedContext)
    }

    override fun onCreate() {
        super.onCreate()

        // todo replace for release version
//        val core = CrashlyticsCore.Builder()
//            .disabled(BuildConfig.DEBUG)
//            .build()
//        Fabric.with(
//            this,
//            Crashlytics.Builder()
//                .core(core)
//                .build()
//        )
        Fabric.with(this, Crashlytics())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsCrashReportingTree())
        }
        Koin.logger = KoinLogger()

        RxNetwork.init(this)
        Realm.init(this)

        Fresco.initialize(this)


        RxJava2Debug.enableRxJava2AssemblyTracking(arrayOf(BuildConfig.APPLICATION_ID))

        val asyncMainThreadScheduler = AndroidSchedulers.from(Looper.getMainLooper(), true)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { asyncMainThreadScheduler }

        RxJavaPlugins.setErrorHandler { Timber.e(it, "RxJava error handler") }

        // start Koin and configure properties
        startKoin(this, modules)
        initKoinProperties()
    }

    private fun initKoinProperties() {
        val apiScheme = "https"
        val apiHost = "restcountries.eu"
        val apiBaseUrl = "$apiScheme://$apiHost/"
        val formattedAppName = getString(R.string.app_name).toLowerCase().replace(" ", "_")

        // put isDebug property
        setProperty(DIProperties.DEBUG, BuildConfig.DEBUG)
        // put DB properties
        setProperty(DIProperties.DB_NAME, "${formattedAppName}DB")
        setProperty(DIProperties.DB_VERSION, 1L)
        // put SharedPreferences name
        setProperty(DIProperties.SHARED_PREFS_NAME, "${formattedAppName}SP")
        // put network properties
        setProperty(DIProperties.HTTP_BASE_URL, apiBaseUrl)
        setProperty(DIProperties.READ_TIMEOUT, 30L)
        setProperty(DIProperties.CONNECT_TIMEOUT, 60L)
        setProperty(DIProperties.WRITE_TIMEOUT, 120L)
        // fill one of the formats: BASE_DATE_TIME_FORMAT for automatic serialization/deserialization or SERVER_DATE_TIME_FORMAT for serialization (leave second empty). Leave both empty for send time as Integer
        setProperty(DIProperties.BASE_DATE_TIME_FORMAT, "")
        setProperty(DIProperties.SERVER_DATE_TIME_FORMAT, "")
        setProperty(
            DIProperties.TRUST_SSL_REQUIRED,
            false
        ) // for some servers need to ignore SSLHandshake when no certificate provided
    }

    companion object {

        private var instance: TestCountriesApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun diComponent(): KoinComponent = instance!!
    }
}