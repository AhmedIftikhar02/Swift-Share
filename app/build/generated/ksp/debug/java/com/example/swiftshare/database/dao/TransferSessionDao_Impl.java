package com.example.swiftshare.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.swiftshare.database.entity.FileTransferEntity;
import com.example.swiftshare.database.entity.TransferSessionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TransferSessionDao_Impl implements TransferSessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TransferSessionEntity> __insertionAdapterOfTransferSessionEntity;

  private final EntityInsertionAdapter<FileTransferEntity> __insertionAdapterOfFileTransferEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSession;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFileStatus;

  public TransferSessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTransferSessionEntity = new EntityInsertionAdapter<TransferSessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transfer_sessions` (`sessionId`,`deviceEndpointId`,`deviceName`,`direction`,`startedAt`,`endedAt`,`status`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransferSessionEntity entity) {
        statement.bindString(1, entity.getSessionId());
        statement.bindString(2, entity.getDeviceEndpointId());
        statement.bindString(3, entity.getDeviceName());
        statement.bindString(4, entity.getDirection());
        statement.bindLong(5, entity.getStartedAt());
        if (entity.getEndedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getEndedAt());
        }
        statement.bindString(7, entity.getStatus());
      }
    };
    this.__insertionAdapterOfFileTransferEntity = new EntityInsertionAdapter<FileTransferEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `file_transfers` (`fileTransferId`,`sessionId`,`fileName`,`mimeType`,`totalBytes`,`transferredBytes`,`uri`,`status`,`checksum`,`errorCode`,`sourceLastModified`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FileTransferEntity entity) {
        statement.bindString(1, entity.getFileTransferId());
        statement.bindString(2, entity.getSessionId());
        statement.bindString(3, entity.getFileName());
        statement.bindString(4, entity.getMimeType());
        statement.bindLong(5, entity.getTotalBytes());
        statement.bindLong(6, entity.getTransferredBytes());
        statement.bindString(7, entity.getUri());
        statement.bindString(8, entity.getStatus());
        if (entity.getChecksum() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getChecksum());
        }
        if (entity.getErrorCode() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getErrorCode());
        }
        statement.bindLong(11, entity.getSourceLastModified());
      }
    };
    this.__preparedStmtOfDeleteSession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transfer_sessions WHERE sessionId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateFileStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE file_transfers SET status = ?, errorCode = ? WHERE fileTransferId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSession(final TransferSessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTransferSessionEntity.insert(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertFiles(final List<FileTransferEntity> files,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFileTransferEntity.insert(files);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSessionWithFiles(final TransferSessionEntity session,
      final List<FileTransferEntity> files, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> TransferSessionDao.DefaultImpls.insertSessionWithFiles(TransferSessionDao_Impl.this, session, files, __cont), $completion);
  }

  @Override
  public Object deleteSession(final String sessionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSession.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, sessionId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteSession.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateFileStatus(final String fileTransferId, final String status,
      final String errorCode, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateFileStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        if (errorCode == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, errorCode);
        }
        _argIndex = 3;
        _stmt.bindString(_argIndex, fileTransferId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateFileStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SessionWithFiles>> observeAllSessionsWithFiles() {
    final String _sql = "SELECT * FROM transfer_sessions ORDER BY startedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"file_transfers",
        "transfer_sessions"}, new Callable<List<SessionWithFiles>>() {
      @Override
      @NonNull
      public List<SessionWithFiles> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
            final int _cursorIndexOfDeviceEndpointId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceEndpointId");
            final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceName");
            final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
            final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
            final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final ArrayMap<String, ArrayList<FileTransferEntity>> _collectionFiles = new ArrayMap<String, ArrayList<FileTransferEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfSessionId);
              if (!_collectionFiles.containsKey(_tmpKey)) {
                _collectionFiles.put(_tmpKey, new ArrayList<FileTransferEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipfileTransfersAscomExampleSwiftshareDatabaseEntityFileTransferEntity(_collectionFiles);
            final List<SessionWithFiles> _result = new ArrayList<SessionWithFiles>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final SessionWithFiles _item;
              final TransferSessionEntity _tmpSession;
              final String _tmpSessionId;
              _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
              final String _tmpDeviceEndpointId;
              _tmpDeviceEndpointId = _cursor.getString(_cursorIndexOfDeviceEndpointId);
              final String _tmpDeviceName;
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
              final String _tmpDirection;
              _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
              final long _tmpStartedAt;
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
              final Long _tmpEndedAt;
              if (_cursor.isNull(_cursorIndexOfEndedAt)) {
                _tmpEndedAt = null;
              } else {
                _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
              }
              final String _tmpStatus;
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
              _tmpSession = new TransferSessionEntity(_tmpSessionId,_tmpDeviceEndpointId,_tmpDeviceName,_tmpDirection,_tmpStartedAt,_tmpEndedAt,_tmpStatus);
              final ArrayList<FileTransferEntity> _tmpFilesCollection;
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfSessionId);
              _tmpFilesCollection = _collectionFiles.get(_tmpKey_1);
              _item = new SessionWithFiles(_tmpSession,_tmpFilesCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getSessionWithFiles(final String sessionId,
      final Continuation<? super SessionWithFiles> $completion) {
    final String _sql = "SELECT * FROM transfer_sessions WHERE sessionId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<SessionWithFiles>() {
      @Override
      @Nullable
      public SessionWithFiles call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
            final int _cursorIndexOfDeviceEndpointId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceEndpointId");
            final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceName");
            final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
            final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
            final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final ArrayMap<String, ArrayList<FileTransferEntity>> _collectionFiles = new ArrayMap<String, ArrayList<FileTransferEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfSessionId);
              if (!_collectionFiles.containsKey(_tmpKey)) {
                _collectionFiles.put(_tmpKey, new ArrayList<FileTransferEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipfileTransfersAscomExampleSwiftshareDatabaseEntityFileTransferEntity(_collectionFiles);
            final SessionWithFiles _result;
            if (_cursor.moveToFirst()) {
              final TransferSessionEntity _tmpSession;
              final String _tmpSessionId;
              _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
              final String _tmpDeviceEndpointId;
              _tmpDeviceEndpointId = _cursor.getString(_cursorIndexOfDeviceEndpointId);
              final String _tmpDeviceName;
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
              final String _tmpDirection;
              _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
              final long _tmpStartedAt;
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
              final Long _tmpEndedAt;
              if (_cursor.isNull(_cursorIndexOfEndedAt)) {
                _tmpEndedAt = null;
              } else {
                _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
              }
              final String _tmpStatus;
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
              _tmpSession = new TransferSessionEntity(_tmpSessionId,_tmpDeviceEndpointId,_tmpDeviceName,_tmpDirection,_tmpStartedAt,_tmpEndedAt,_tmpStatus);
              final ArrayList<FileTransferEntity> _tmpFilesCollection;
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfSessionId);
              _tmpFilesCollection = _collectionFiles.get(_tmpKey_1);
              _result = new SessionWithFiles(_tmpSession,_tmpFilesCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getSessionIdForFile(final String fileTransferId,
      final Continuation<? super String> $completion) {
    final String _sql = "SELECT sessionId FROM file_transfers WHERE fileTransferId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fileTransferId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<String>() {
      @Override
      @Nullable
      public String call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final String _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getString(0);
            }
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipfileTransfersAscomExampleSwiftshareDatabaseEntityFileTransferEntity(
      @NonNull final ArrayMap<String, ArrayList<FileTransferEntity>> _map) {
    final Set<String> __mapKeySet = _map.keySet();
    if (__mapKeySet.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchArrayMap(_map, true, (map) -> {
        __fetchRelationshipfileTransfersAscomExampleSwiftshareDatabaseEntityFileTransferEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `fileTransferId`,`sessionId`,`fileName`,`mimeType`,`totalBytes`,`transferredBytes`,`uri`,`status`,`checksum`,`errorCode`,`sourceLastModified` FROM `file_transfers` WHERE `sessionId` IN (");
    final int _inputSize = __mapKeySet.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : __mapKeySet) {
      _stmt.bindString(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "sessionId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfFileTransferId = 0;
      final int _cursorIndexOfSessionId = 1;
      final int _cursorIndexOfFileName = 2;
      final int _cursorIndexOfMimeType = 3;
      final int _cursorIndexOfTotalBytes = 4;
      final int _cursorIndexOfTransferredBytes = 5;
      final int _cursorIndexOfUri = 6;
      final int _cursorIndexOfStatus = 7;
      final int _cursorIndexOfChecksum = 8;
      final int _cursorIndexOfErrorCode = 9;
      final int _cursorIndexOfSourceLastModified = 10;
      while (_cursor.moveToNext()) {
        final String _tmpKey;
        _tmpKey = _cursor.getString(_itemKeyIndex);
        final ArrayList<FileTransferEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final FileTransferEntity _item_1;
          final String _tmpFileTransferId;
          _tmpFileTransferId = _cursor.getString(_cursorIndexOfFileTransferId);
          final String _tmpSessionId;
          _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
          final String _tmpFileName;
          _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
          final String _tmpMimeType;
          _tmpMimeType = _cursor.getString(_cursorIndexOfMimeType);
          final long _tmpTotalBytes;
          _tmpTotalBytes = _cursor.getLong(_cursorIndexOfTotalBytes);
          final long _tmpTransferredBytes;
          _tmpTransferredBytes = _cursor.getLong(_cursorIndexOfTransferredBytes);
          final String _tmpUri;
          _tmpUri = _cursor.getString(_cursorIndexOfUri);
          final String _tmpStatus;
          _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
          final String _tmpChecksum;
          if (_cursor.isNull(_cursorIndexOfChecksum)) {
            _tmpChecksum = null;
          } else {
            _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
          }
          final String _tmpErrorCode;
          if (_cursor.isNull(_cursorIndexOfErrorCode)) {
            _tmpErrorCode = null;
          } else {
            _tmpErrorCode = _cursor.getString(_cursorIndexOfErrorCode);
          }
          final long _tmpSourceLastModified;
          _tmpSourceLastModified = _cursor.getLong(_cursorIndexOfSourceLastModified);
          _item_1 = new FileTransferEntity(_tmpFileTransferId,_tmpSessionId,_tmpFileName,_tmpMimeType,_tmpTotalBytes,_tmpTransferredBytes,_tmpUri,_tmpStatus,_tmpChecksum,_tmpErrorCode,_tmpSourceLastModified);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
