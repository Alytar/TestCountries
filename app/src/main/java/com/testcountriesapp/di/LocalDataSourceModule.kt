package com.testcountriesapp.di

import com.testcountriesapp.di.DIProperties.DB_NAME
import com.testcountriesapp.di.DIProperties.DB_VERSION
import com.testcountriesapp.di.DIProperties.DEBUG
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.dsl.module.module
import timber.log.Timber

val localDataSourceModule = module {

    fun makeRealm(realmConfiguration: RealmConfiguration): Realm {
        Realm.setDefaultConfiguration(realmConfiguration)
        return try {
            Realm.getDefaultInstance()
        } catch (e: Exception) {
            Timber.e(e, "Realm default instance error")
            Realm.deleteRealm(realmConfiguration)
            Realm.setDefaultConfiguration(realmConfiguration)
            Realm.getDefaultInstance()
        }
    }

    fun makeRealmConfiguration(dbName: String, dbVersion: Long, isDebug: Boolean): RealmConfiguration {
        var builder: RealmConfiguration.Builder = RealmConfiguration.Builder()
            .name(dbName)
            .schemaVersion(dbVersion)

        builder = if (isDebug) {
            builder.deleteRealmIfMigrationNeeded()
        } else {
            builder.deleteRealmIfMigrationNeeded()
            //builder.migration(RealmDbMigration()); // TODO: implement migration if needed
        }
        return builder.build()
    }

    // provide Realm instance
    single { makeRealm(get()) }

    // provide RealmConfiguration
    single { makeRealmConfiguration(getProperty(DB_NAME), getProperty(DB_VERSION), getProperty(DEBUG)) }
}