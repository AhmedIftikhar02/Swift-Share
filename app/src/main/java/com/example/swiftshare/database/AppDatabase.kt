package com.example.swiftshare.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.swiftshare.database.dao.TransferSessionDao
import com.example.swiftshare.database.entity.FileTransferEntity
import com.example.swiftshare.database.entity.TransferSessionEntity

@Database(
    entities = [TransferSessionEntity::class, FileTransferEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transferSessionDao(): TransferSessionDao

    companion object {
        /**
         * Phase 8: adds FileTransferEntity.sourceLastModified, used by SourceFileValidator
         * to detect a source file changing since it was queued. Additive-only, defaults to
         * 0 ("unknown") for every row written by Phase 6/7 builds — never destructive.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE file_transfers ADD COLUMN sourceLastModified INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}