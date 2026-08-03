package com.example.swiftshare.database

/**
 * Room scaffold - NOT actively used by the sample Home feature (that uses network +
 * in-memory state only, per the boilerplate brief). When you need local persistence/caching:
 *
 * 1. Create an @Entity data class, e.g. database/entity/ProductEntity.kt
 * 2. Create a @Dao interface, e.g. database/dao/ProductDao.kt
 * 3. Add the entity to `entities = [...]` below and bump `version`
 * 4. Add an abstract fun for the new Dao below
 * 5. Provide it from di/DatabaseModule.kt (already wired - just add the @Provides for your Dao)
 *
 * That's it - DI, the database instance, and the build dependency (Room + KSP) are already
 * set up and ready, exactly so you can "quickly use it when needed" as you asked.
 */
//@Database(
//    entities = [], // add @Entity classes here as you create them
//    version = 1,
//    exportSchema = false
//)
//abstract class AppDatabase : RoomDatabase() {
//    // example once you add an entity:
//    // abstract fun productDao(): ProductDao
//}
