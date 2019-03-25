package com.testcountriesapp.repository.local

import io.realm.DynamicRealm
import io.realm.RealmMigration
import timber.log.Timber

class RealmDbMigration : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        // TODO: implement migration if needed
        Timber.i("migration from %d to %d", oldVersion, newVersion)

//        // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
//        // with the same object creation and query capabilities.
//        // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
//        // renamed.
//
//        // Access the Realm schema in order to create, modify or delete classes and their fields.
//        val schema = realm.schema
//
//        /************************************************
//         * // Version 0
//         * class Person
//         * @Required
//         * String firstName;
//         * @Required
//         * String lastName;
//         * int    age;
//         * // Version 1
//         * class Person
//         * @Required
//         * String fullName;            // combine firstName and lastName into single field.
//         * int age;
//         */
//        // Migrate from version 0 to version 1
//        if (oldVersion == 0L) {
//            val personSchema = schema.get("Person")
//
//            // Combine 'firstName' and 'lastName' in a new field called 'fullName'
//            personSchema!!
//                    .addField("fullName", String::class.java, FieldAttribute.REQUIRED)
//                    .transform { obj -> obj.set("fullName", obj.getString("firstName") + " " + obj.getString("lastName")) }
//                    .removeField("firstName")
//                    .removeField("lastName")
//            oldVersion++
//        }
//
//        /************************************************
//         * // Version 2
//         * class Pet                   // add a new model class
//         * @Required
//         * String name;
//         * @Required
//         * String type;
//         * class Person
//         * @Required
//         * String fullName;
//         * int age;
//         * RealmList<Pet> pets;    // add an array property
//        </Pet> */
//        // Migrate from version 1 to version 2
//        if (oldVersion == 1L) {
//
//            // Create a new class
//            val petSchema = schema.create("Pet")
//                    .addField("name", String::class.java, FieldAttribute.REQUIRED)
//                    .addField("type", String::class.java, FieldAttribute.REQUIRED)
//
//            // Add a new field to an old class and populate it with initial data
//            schema.get("Person")!!
//                    .addRealmListField("pets", petSchema)
//                    .transform { obj ->
//                        if (obj.getString("fullName") == "JP McDonald") {
//                            val pet = realm.createObject("Pet")
//                            pet.setString("name", "Jimbo")
//                            pet.setString("type", "dog")
//                            obj.getList("pets").add(pet)
//                        }
//                    }
//            oldVersion++
//        }
//
//        /************************************************
//         * // Version 3
//         * class Pet
//         * @Required
//         * String name;
//         * int type;               // type becomes int
//         * class Person
//         * String fullName;        // fullName is nullable now
//         * RealmList<Pet> pets;    // age and pets re-ordered (no action needed)
//         * int age;
//        </Pet> */
//        // Migrate from version 2 to version 3
//        if (oldVersion == 2L) {
//            val personSchema = schema.get("Person")
//            personSchema!!.setNullable("fullName", true) // fullName is nullable now.
//
//            // Change type from String to int
//            schema.get("Pet")!!
//                    .addField("type_tmp", Int::class.javaPrimitiveType)
//                    .transform { obj ->
//                        val oldType = obj.getString("type")
//                        if (oldType == "dog") {
//                            obj.setLong("type_tmp", 1)
//                        } else if (oldType == "cat") {
//                            obj.setInt("type_tmp", 2)
//                        } else if (oldType == "hamster") {
//                            obj.setInt("type_tmp", 3)
//                        }
//                    }
//                    .removeField("type")
//                    .renameField("type_tmp", "type")
//            oldVersion++
//        }
    }
}