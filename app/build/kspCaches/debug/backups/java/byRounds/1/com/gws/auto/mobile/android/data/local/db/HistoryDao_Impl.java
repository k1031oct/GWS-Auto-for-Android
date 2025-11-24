package com.gws.auto.mobile.android.data.local.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.gws.auto.mobile.android.domain.model.History;
import com.gws.auto.mobile.android.ui.dashboard.StatsSummary;
import com.gws.auto.mobile.android.ui.dashboard.WorkflowExecutionCount;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class HistoryDao_Impl implements HistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<History> __insertionAdapterOfHistory;

  private final DateConverter __dateConverter = new DateConverter();

  private final SharedSQLiteStatement __preparedStmtOfDeleteHistoryById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllHistory;

  public HistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHistory = new EntityInsertionAdapter<History>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `execution_history` (`id`,`workflowId`,`workflowName`,`executedAt`,`status`,`logs`,`durationMs`,`isBookmarked`,`triggerType`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final History entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getWorkflowId());
        statement.bindString(3, entity.getWorkflowName());
        final Long _tmp = __dateConverter.dateToTimestamp(entity.getExecutedAt());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp);
        }
        statement.bindString(5, entity.getStatus());
        statement.bindString(6, entity.getLogs());
        statement.bindLong(7, entity.getDurationMs());
        final int _tmp_1 = entity.isBookmarked() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindString(9, entity.getTriggerType());
      }
    };
    this.__preparedStmtOfDeleteHistoryById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM execution_history WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllHistory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM execution_history";
        return _query;
      }
    };
  }

  @Override
  public Object insertHistory(final History history, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfHistory.insert(history);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteHistoryById(final long historyId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteHistoryById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, historyId);
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
          __preparedStmtOfDeleteHistoryById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllHistory(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllHistory.acquire();
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
          __preparedStmtOfDeleteAllHistory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<History>> getAllHistory() {
    final String _sql = "SELECT * FROM execution_history ORDER BY executedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"execution_history"}, new Callable<List<History>>() {
      @Override
      @NonNull
      public List<History> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfWorkflowName = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowName");
          final int _cursorIndexOfExecutedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "executedAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfLogs = CursorUtil.getColumnIndexOrThrow(_cursor, "logs");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfTriggerType = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerType");
          final List<History> _result = new ArrayList<History>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final History _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getString(_cursorIndexOfWorkflowId);
            final String _tmpWorkflowName;
            _tmpWorkflowName = _cursor.getString(_cursorIndexOfWorkflowName);
            final Date _tmpExecutedAt;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfExecutedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfExecutedAt);
            }
            final Date _tmp_1 = __dateConverter.fromTimestamp(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.Date', but it was NULL.");
            } else {
              _tmpExecutedAt = _tmp_1;
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpLogs;
            _tmpLogs = _cursor.getString(_cursorIndexOfLogs);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final boolean _tmpIsBookmarked;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp_2 != 0;
            final String _tmpTriggerType;
            _tmpTriggerType = _cursor.getString(_cursorIndexOfTriggerType);
            _item = new History(_tmpId,_tmpWorkflowId,_tmpWorkflowName,_tmpExecutedAt,_tmpStatus,_tmpLogs,_tmpDurationMs,_tmpIsBookmarked,_tmpTriggerType);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getTotalCount() {
    final String _sql = "SELECT COUNT(*) FROM execution_history";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"execution_history"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getErrorCount() {
    final String _sql = "SELECT COUNT(*) FROM execution_history WHERE status = 'Failure'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"execution_history"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<WorkflowExecutionCount>> getWorkflowExecutionCounts() {
    final String _sql = "SELECT workflowName, COUNT(*) as executionCount FROM execution_history GROUP BY workflowName ORDER BY executionCount DESC LIMIT 10";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"execution_history"}, new Callable<List<WorkflowExecutionCount>>() {
      @Override
      @NonNull
      public List<WorkflowExecutionCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfWorkflowName = 0;
          final int _cursorIndexOfExecutionCount = 1;
          final List<WorkflowExecutionCount> _result = new ArrayList<WorkflowExecutionCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkflowExecutionCount _item;
            final String _tmpWorkflowName;
            _tmpWorkflowName = _cursor.getString(_cursorIndexOfWorkflowName);
            final int _tmpExecutionCount;
            _tmpExecutionCount = _cursor.getInt(_cursorIndexOfExecutionCount);
            _item = new WorkflowExecutionCount(_tmpWorkflowName,_tmpExecutionCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<StatsSummary> getStatsForPeriod(final long startTime, final long endTime) {
    final String _sql = "SELECT COUNT(*) as total_count, COUNT(CASE WHEN status = 'Failure' THEN 1 END) as error_count, SUM(durationMs) as total_duration FROM execution_history WHERE executedAt BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"execution_history"}, new Callable<StatsSummary>() {
      @Override
      @NonNull
      public StatsSummary call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTotalCount = 0;
          final int _cursorIndexOfErrorCount = 1;
          final int _cursorIndexOfTotalDuration = 2;
          final StatsSummary _result;
          if (_cursor.moveToFirst()) {
            final int _tmpTotalCount;
            _tmpTotalCount = _cursor.getInt(_cursorIndexOfTotalCount);
            final int _tmpErrorCount;
            _tmpErrorCount = _cursor.getInt(_cursorIndexOfErrorCount);
            final long _tmpTotalDuration;
            _tmpTotalDuration = _cursor.getLong(_cursorIndexOfTotalDuration);
            _result = new StatsSummary(_tmpTotalCount,_tmpErrorCount,_tmpTotalDuration);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
