package com.testcountriesapp.di

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.testcountriesapp.di.DIProperties.BASE_DATE_TIME_FORMAT
import com.testcountriesapp.di.DIProperties.CONNECT_TIMEOUT
import com.testcountriesapp.di.DIProperties.DEBUG
import com.testcountriesapp.di.DIProperties.HTTP_BASE_URL
import com.testcountriesapp.di.DIProperties.READ_TIMEOUT
import com.testcountriesapp.di.DIProperties.SERVER_DATE_TIME_FORMAT
import com.testcountriesapp.di.DIProperties.TRUST_SSL_REQUIRED
import com.testcountriesapp.di.DIProperties.WRITE_TIMEOUT
import com.testcountriesapp.repository.remote.ApiService
import com.testcountriesapp.repository.remote.serializer.DateTimeDeserializer
import com.testcountriesapp.repository.remote.serializer.DateTimeSerializer
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

const val HTTP = "http"
val remoteDataSourceModule = module {

    fun <T> makeAsyncApiService(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        gson: Gson,
        serviceClass: Class<T>
    ): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(serviceClass)
    }

    fun addSSLSocketFactory(builder: OkHttpClient.Builder) {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun makeHttpClient(
        interceptors: ArrayList<Interceptor>?,
        connectTimeout: Long,
        writeTimeout: Long,
        readTimeout: Long,
        trustSSLRequired: Boolean
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        interceptors?.forEach { builder.addInterceptor(it) }
        if (trustSSLRequired) addSSLSocketFactory(builder)
        return builder
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    fun makeGson(
        baseDateTimeFormat: String,
        serverDateTimeFormat: String
    ): Gson {
        val gsonBuilder = GsonBuilder()

        // set Date serialization\deserialization
        if (baseDateTimeFormat.isEmpty()) {
            gsonBuilder.registerTypeAdapter(Date::class.java, DateTimeDeserializer(serverDateTimeFormat))
            gsonBuilder.registerTypeAdapter(Date::class.java, DateTimeSerializer(serverDateTimeFormat))
        } else {
            gsonBuilder.setDateFormat(baseDateTimeFormat) // Set serialize\deserialize base format
        }

        return gsonBuilder.create()
    }

    fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = if (isDebug) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return loggingInterceptor
    }

    fun getInterceptors(): ArrayList<Interceptor> {
        val interceptors: ArrayList<Interceptor> = ArrayList()
        with(interceptors) {
            add(makeLoggingInterceptor(getProperty(DEBUG)))
            // TODO add interceptors if needed
        }
        return interceptors
    }

    // provide ApiService
    single {
        makeAsyncApiService(
            getProperty(HTTP_BASE_URL),
            get(name = HTTP),
            get(),
            ApiService::class.java
        )
    }

    // provide GsonConverter
    single {
        makeGson(
            getProperty(BASE_DATE_TIME_FORMAT),
            getProperty(SERVER_DATE_TIME_FORMAT)
        )
    }

    // provide OkHttpClient for HTTP connection
    single(name = HTTP) {
        makeHttpClient(
            getInterceptors(),
            getProperty(CONNECT_TIMEOUT),
            getProperty(WRITE_TIMEOUT),
            getProperty(READ_TIMEOUT),
            getProperty(TRUST_SSL_REQUIRED)
        )
    }

}
