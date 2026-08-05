package com.example.swiftshare.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.swiftshare.database.dao.TransferSessionDao;
import com.example.swiftshare.database.dao.TransferSessionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile TransferSessionDao _transferSessionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `transfer_sessions` (`sessionId` TEXT NOT NULL, `deviceEndpointId` TEXT NOT NULL, `deviceName` TEXT NOT NULL, `direction` TEXT NOT NULL, `startedAt` INTEGER NOT NULL, `endedAt` INTEGER, `status` TEXT NOT NULL, PRIMARY KEY(`sessionId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `file_transfers` (`fileTransferId` TEXT NOT NULL, `sessionId` TEXT NOT NULL, `fileName` TEXT NOT NULL, `mimeType` TEXT NOT NULL, `totalBytes` INTEGER NOT NULL, `transferredBytes` INTEGER NOT NULL, `uri` TEXT NOT NULL, `status` TEXT NOT NULL, `checksum` TEXT, `errorCode` TEXT, `sourceLastModified` INTEGER NOT NULL, PRIMARY KEY(`fileTransferId`), FOREIGN KEY(`sessionId`) REFERENCES `transfer_sessions`(`sessionId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_file_transfers_sessionId` ON `file_transfers` (`sessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c3c1fcda050bb6c3d30a636f90e26fb1')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `transfer_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `file_transfers`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsTransferSessions = new HashMap<String, TableInfo.Column>(7);
        _columnsTransferSessions.put("sessionId", new TableInfo.Column("sessionId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransferSessions.put("deviceEndpointId", new TableInfo.Column("deviceEndpointId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransferSessions.put("deviceName", new TableInfo.Column("deviceName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransferSessions.put("direction", new TableInfo.Column("direction", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransferSessions.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransferSessions.put("endedAt", new TableInfo.Column("endedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransferSessions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransferSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTransferSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTransferSessions = new TableInfo("transfer_sessions", _columnsTransferSessions, _foreignKeysTransferSessions, _indicesTransferSessions);
        final TableInfo _existingTransferSessions = TableInfo.read(db, "transfer_sessions");
        if (!_infoTransferSessions.equals(_existingTransferSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "transfer_sessions(com.example.swiftshare.database.entity.TransferSessionEntity).\n"
                  + " Expected:\n" + _infoTransferSessions + "\n"
                  + " Found:\n" + _existingTransferSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsFileTransfers = new HashMap<String, TableInfo.Column>(11);
        _columnsFileTransfers.put("fileTransferId", new TableInfo.Column("fileTransferId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTransfers.put("sessionId", new TableInfo.Column("sessionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTransfers.put("fileName", new TableInfo.Column("fileName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTransfers.put("mimeType", new TableInfo.Column("mimeType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTransfers.put("totalBytes", new TableInfo.Column("totalBytes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTransfers.put("transferredBytes", new TableInfo.Column("transferredBytes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTransfers.put("uri", new TableInfo.Column("uri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTransfers.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTransfers.put("checksum", new TableInfo.Column("checksum", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTransfers.put("errorCode", new TableInfo.Column("errorCode", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTransfers.put("sourceLastModified", new TableInfo.Column("sourceLastModified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFileTransfers = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysFileTransfers.add(new TableInfo.ForeignKey("transfer_sessions", "CASCADE", "NO ACTION", Arrays.asList("sessionId"), Arrays.asList("sessionId")));
        final HashSet<TableInfo.Index> _indicesFileTransfers = new HashSet<TableInfo.Index>(1);
        _indicesFileTransfers.add(new TableInfo.Index("index_file_transfers_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        final TableInfo _infoFileTransfers = new TableInfo("file_transfers", _columnsFileTransfers, _foreignKeysFileTransfers, _indicesFileTransfers);
        final TableInfo _existingFileTransfers = TableInfo.read(db, "file_transfers");
        if (!_infoFileTransfers.equals(_existingFileTransfers)) {
          return new RoomOpenHelper.ValidationResult(false, "file_transfers(com.example.swiftshare.database.entity.FileTransferEntity).\n"
                  + " Expected:\n" + _infoFileTransfers + "\n"
                  + " Found:\n" + _existingFileTransfers);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "c3c1fcda050bb6c3d30a636f90e26fb1", "fa06016cc79de8ca54c1619a2c52374c");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "transfer_sessions","file_transfers");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `transfer_sessions`");
      _db.execSQL("DELETE FROM `file_transfers`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TransferSessionDao.class, TransferSessionDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TransferSessionDao transferSessionDao() {
    if (_transferSessionDao != null) {
      return _transferSessionDao;
    } else {
      synchronized(this) {
        if(_transferSessionDao == null) {
          _transferSessionDao = new TransferSessionDao_Impl(this);
        }
        return _transferSessionDao;
      }
    }
  }
}
