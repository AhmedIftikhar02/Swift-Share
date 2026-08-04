package com.example.swiftshare.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.swiftshare.database.dao.TransferSessionDao
import com.example.swiftshare.database.entity.FileTransferEntity
import com.example.swiftshare.database.entity.TransferSessionEntity
@Database(
    entities = [TransferSessionEntity::class, FileTransferEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transferSessionDao(): TransferSessionDao
}