package com.testcountriesapp.general.extension.krealmextensions

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.RealmModule
import timber.log.Timber

class RealmConfigStore {

    companion object {

        private var configMap: MutableMap<Class<out RealmModel>, RealmConfiguration> = HashMap()

        /**
         * Initialize realm configuration for class
         */
        fun <T : RealmModel> init(modelClass: Class<T>, realmCfg: RealmConfiguration) {
            Timber.d("Adding class $modelClass to realm ${realmCfg.realmFileName}")
            if (!configMap.containsKey(modelClass)) {
                configMap.put(modelClass, realmCfg)
            }
        }

        fun <T : Any> initModule(cls: Class<T>, realmCfg: RealmConfiguration) {
            // check if class of the module
            val annotation = cls.annotations.filter { it.annotationClass.java.name == RealmModule::class.java.name }
                .firstOrNull()

            @Suppress("unchecked_cast")
            if (annotation != null) {
                Timber.i("Got annotation in module $annotation")
                val moduleAnnotation = annotation as RealmModule
                moduleAnnotation.classes.filter {
                    it.java.interfaces.contains(RealmModel::class.java)
                }.forEach {
                    init(it.java as Class<RealmModel>, realmCfg)
                }
                moduleAnnotation.classes.filter {
                    it.java.superclass == RealmObject::class.java
                }.forEach {
                    init(it.java as Class<RealmObject>, realmCfg)
                }
            }
        }

        /**
         * Fetches realm configuration for class.
         */
        fun <T : RealmModel> fetchConfiguration(modelClass: Class<T>): RealmConfiguration? {
            return configMap[modelClass]
        }
    }
}

fun <T : RealmModel> T.getRealmInstance(): Realm {
    return RealmConfigStore.fetchConfiguration(this::class.java)?.realm() ?: Realm.getDefaultInstance()
}

fun <T : RealmModel> getRealmInstance(clazz: Class<T>): Realm {
    return RealmConfigStore.fetchConfiguration(clazz)?.realm() ?: Realm.getDefaultInstance()
}

inline fun <reified D : RealmModel, T : Collection<D>> T.getRealmInstance(): Realm {
    return RealmConfigStore.fetchConfiguration(D::class.java)?.realm() ?: Realm.getDefaultInstance()
}

inline fun <reified T : RealmModel> getRealmInstance(): Realm {
    return RealmConfigStore.fetchConfiguration(T::class.java)?.realm() ?: Realm.getDefaultInstance()
}

inline fun <reified D : RealmModel> Array<D>.getRealmInstance(): Realm {
    return RealmConfigStore.fetchConfiguration(D::class.java)?.realm() ?: Realm.getDefaultInstance()
}

fun RealmConfiguration.realm(): Realm {
    return Realm.getInstance(this)
}
