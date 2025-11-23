package com.gws.auto.mobile.android.data.local.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.gws.auto.mobile.android.domain.model.Schedule;
import com.gws.auto.mobile.android.domain.model.ScheduleType;
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
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ScheduleDao_Impl implements ScheduleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Schedule> __insertionAdapterOfSchedule;

  private final ScheduleTypeConverter __scheduleTypeConverter = new ScheduleTypeConverter();

  private final ListConverter __listConverter = new ListConverter();

  private final IntListConverter __intListConverter = new IntListConverter();

  private final EntityDeletionOrUpdateAdapter<Schedule> __updateAdapterOfSchedule;

  private final SharedSQLiteStatement __preparedStmtOfDeleteScheduleById;

  public ScheduleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSchedule = new EntityInsertionAdapter<Schedule>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `schedules` (`id`,`workflowId`,`workflowName`,`scheduleType`,`hourlyInterval`,`time`,`weeklyDays`,`monthlyDays`,`yearlyMonth`,`yearlyDayOfMonth`,`lastRun`,`nextRun`,`skipHolidays`,`isEnabled`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Schedule entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getWorkflowId());
        statement.bindString(3, entity.getWorkflowName());
        final String _tmp = __scheduleTypeConverter.fromScheduleType(entity.getScheduleType());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        if (entity.getHourlyInterval() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getHourlyInterval());
        }
        if (entity.getTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTime());
        }
        final String _tmp_1;
        if (entity.getWeeklyDays() == null) {
          _tmp_1 = null;
        } else {
          _tmp_1 = __listConverter.fromList(entity.getWeeklyDays());
        }
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_1);
        }
        final String _tmp_2;
        if (entity.getMonthlyDays() == null) {
          _tmp_2 = null;
        } else {
          _tmp_2 = __intListConverter.toString(entity.getMonthlyDays());
        }
        if (_tmp_2 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_2);
        }
        if (entity.getYearlyMonth() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getYearlyMonth());
        }
        if (entity.getYearlyDayOfMonth() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getYearlyDayOfMonth());
        }
        if (entity.getLastRun() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getLastRun());
        }
        if (entity.getNextRun() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getNextRun());
        }
        final int _tmp_3 = entity.getSkipHolidays() ? 1 : 0;
        statement.bindLong(13, _tmp_3);
        final int _tmp_4 = entity.isEnabled() ? 1 : 0;
        statement.bindLong(14, _tmp_4);
      }
    };
    this.__updateAdapterOfSchedule = new EntityDeletionOrUpdateAdapter<Schedule>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `schedules` SET `id` = ?,`workflowId` = ?,`workflowName` = ?,`scheduleType` = ?,`hourlyInterval` = ?,`time` = ?,`weeklyDays` = ?,`monthlyDays` = ?,`yearlyMonth` = ?,`yearlyDayOfMonth` = ?,`lastRun` = ?,`nextRun` = ?,`skipHolidays` = ?,`isEnabled` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Schedule entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getWorkflowId());
        statement.bindString(3, entity.getWorkflowName());
        final String _tmp = __scheduleTypeConverter.fromScheduleType(entity.getScheduleType());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        if (entity.getHourlyInterval() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getHourlyInterval());
        }
        if (entity.getTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTime());
        }
        final String _tmp_1;
        if (entity.getWeeklyDays() == null) {
          _tmp_1 = null;
        } else {
          _tmp_1 = __listConverter.fromList(entity.getWeeklyDays());
        }
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_1);
        }
        final String _tmp_2;
        if (entity.getMonthlyDays() == null) {
          _tmp_2 = null;
        } else {
          _tmp_2 = __intListConverter.toString(entity.getMonthlyDays());
        }
        if (_tmp_2 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_2);
        }
        if (entity.getYearlyMonth() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getYearlyMonth());
        }
        if (entity.getYearlyDayOfMonth() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getYearlyDayOfMonth());
        }
        if (entity.getLastRun() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getLastRun());
        }
        if (entity.getNextRun() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getNextRun());
        }
        final int _tmp_3 = entity.getSkipHolidays() ? 1 : 0;
        statement.bindLong(13, _tmp_3);
        final int _tmp_4 = entity.isEnabled() ? 1 : 0;
        statement.bindLong(14, _tmp_4);
        statement.bindString(15, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteScheduleById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM schedules WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSchedule(final Schedule schedule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSchedule.insert(schedule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSchedule(final Schedule schedule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSchedule.handle(schedule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteScheduleById(final String scheduleId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteScheduleById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, scheduleId);
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
          __preparedStmtOfDeleteScheduleById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Schedule>> getAllSchedules() {
    final String _sql = "SELECT * FROM schedules";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"schedules"}, new Callable<List<Schedule>>() {
      @Override
      @NonNull
      public List<Schedule> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfWorkflowName = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowName");
          final int _cursorIndexOfScheduleType = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduleType");
          final int _cursorIndexOfHourlyInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "hourlyInterval");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfWeeklyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "weeklyDays");
          final int _cursorIndexOfMonthlyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyDays");
          final int _cursorIndexOfYearlyMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "yearlyMonth");
          final int _cursorIndexOfYearlyDayOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "yearlyDayOfMonth");
          final int _cursorIndexOfLastRun = CursorUtil.getColumnIndexOrThrow(_cursor, "lastRun");
          final int _cursorIndexOfNextRun = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRun");
          final int _cursorIndexOfSkipHolidays = CursorUtil.getColumnIndexOrThrow(_cursor, "skipHolidays");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final List<Schedule> _result = new ArrayList<Schedule>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Schedule _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getString(_cursorIndexOfWorkflowId);
            final String _tmpWorkflowName;
            _tmpWorkflowName = _cursor.getString(_cursorIndexOfWorkflowName);
            final ScheduleType _tmpScheduleType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfScheduleType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfScheduleType);
            }
            final ScheduleType _tmp_1 = __scheduleTypeConverter.toScheduleType(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.gws.auto.mobile.android.domain.model.ScheduleType', but it was NULL.");
            } else {
              _tmpScheduleType = _tmp_1;
            }
            final Integer _tmpHourlyInterval;
            if (_cursor.isNull(_cursorIndexOfHourlyInterval)) {
              _tmpHourlyInterval = null;
            } else {
              _tmpHourlyInterval = _cursor.getInt(_cursorIndexOfHourlyInterval);
            }
            final String _tmpTime;
            if (_cursor.isNull(_cursorIndexOfTime)) {
              _tmpTime = null;
            } else {
              _tmpTime = _cursor.getString(_cursorIndexOfTime);
            }
            final List<String> _tmpWeeklyDays;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfWeeklyDays)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfWeeklyDays);
            }
            if (_tmp_2 == null) {
              _tmpWeeklyDays = null;
            } else {
              _tmpWeeklyDays = __listConverter.fromString(_tmp_2);
            }
            final List<Integer> _tmpMonthlyDays;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfMonthlyDays)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfMonthlyDays);
            }
            if (_tmp_3 == null) {
              _tmpMonthlyDays = null;
            } else {
              _tmpMonthlyDays = __intListConverter.fromString(_tmp_3);
            }
            final Integer _tmpYearlyMonth;
            if (_cursor.isNull(_cursorIndexOfYearlyMonth)) {
              _tmpYearlyMonth = null;
            } else {
              _tmpYearlyMonth = _cursor.getInt(_cursorIndexOfYearlyMonth);
            }
            final Integer _tmpYearlyDayOfMonth;
            if (_cursor.isNull(_cursorIndexOfYearlyDayOfMonth)) {
              _tmpYearlyDayOfMonth = null;
            } else {
              _tmpYearlyDayOfMonth = _cursor.getInt(_cursorIndexOfYearlyDayOfMonth);
            }
            final Long _tmpLastRun;
            if (_cursor.isNull(_cursorIndexOfLastRun)) {
              _tmpLastRun = null;
            } else {
              _tmpLastRun = _cursor.getLong(_cursorIndexOfLastRun);
            }
            final Long _tmpNextRun;
            if (_cursor.isNull(_cursorIndexOfNextRun)) {
              _tmpNextRun = null;
            } else {
              _tmpNextRun = _cursor.getLong(_cursorIndexOfNextRun);
            }
            final boolean _tmpSkipHolidays;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfSkipHolidays);
            _tmpSkipHolidays = _tmp_4 != 0;
            final boolean _tmpIsEnabled;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp_5 != 0;
            _item = new Schedule(_tmpId,_tmpWorkflowId,_tmpWorkflowName,_tmpScheduleType,_tmpHourlyInterval,_tmpTime,_tmpWeeklyDays,_tmpMonthlyDays,_tmpYearlyMonth,_tmpYearlyDayOfMonth,_tmpLastRun,_tmpNextRun,_tmpSkipHolidays,_tmpIsEnabled);
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
  public Object getScheduleById(final String scheduleId,
      final Continuation<? super Schedule> $completion) {
    final String _sql = "SELECT * FROM schedules WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, scheduleId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Schedule>() {
      @Override
      @Nullable
      public Schedule call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkflowId = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowId");
          final int _cursorIndexOfWorkflowName = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowName");
          final int _cursorIndexOfScheduleType = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduleType");
          final int _cursorIndexOfHourlyInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "hourlyInterval");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfWeeklyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "weeklyDays");
          final int _cursorIndexOfMonthlyDays = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyDays");
          final int _cursorIndexOfYearlyMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "yearlyMonth");
          final int _cursorIndexOfYearlyDayOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "yearlyDayOfMonth");
          final int _cursorIndexOfLastRun = CursorUtil.getColumnIndexOrThrow(_cursor, "lastRun");
          final int _cursorIndexOfNextRun = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRun");
          final int _cursorIndexOfSkipHolidays = CursorUtil.getColumnIndexOrThrow(_cursor, "skipHolidays");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final Schedule _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpWorkflowId;
            _tmpWorkflowId = _cursor.getString(_cursorIndexOfWorkflowId);
            final String _tmpWorkflowName;
            _tmpWorkflowName = _cursor.getString(_cursorIndexOfWorkflowName);
            final ScheduleType _tmpScheduleType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfScheduleType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfScheduleType);
            }
            final ScheduleType _tmp_1 = __scheduleTypeConverter.toScheduleType(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.gws.auto.mobile.android.domain.model.ScheduleType', but it was NULL.");
            } else {
              _tmpScheduleType = _tmp_1;
            }
            final Integer _tmpHourlyInterval;
            if (_cursor.isNull(_cursorIndexOfHourlyInterval)) {
              _tmpHourlyInterval = null;
            } else {
              _tmpHourlyInterval = _cursor.getInt(_cursorIndexOfHourlyInterval);
            }
            final String _tmpTime;
            if (_cursor.isNull(_cursorIndexOfTime)) {
              _tmpTime = null;
            } else {
              _tmpTime = _cursor.getString(_cursorIndexOfTime);
            }
            final List<String> _tmpWeeklyDays;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfWeeklyDays)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfWeeklyDays);
            }
            if (_tmp_2 == null) {
              _tmpWeeklyDays = null;
            } else {
              _tmpWeeklyDays = __listConverter.fromString(_tmp_2);
            }
            final List<Integer> _tmpMonthlyDays;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfMonthlyDays)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfMonthlyDays);
            }
            if (_tmp_3 == null) {
              _tmpMonthlyDays = null;
            } else {
              _tmpMonthlyDays = __intListConverter.fromString(_tmp_3);
            }
            final Integer _tmpYearlyMonth;
            if (_cursor.isNull(_cursorIndexOfYearlyMonth)) {
              _tmpYearlyMonth = null;
            } else {
              _tmpYearlyMonth = _cursor.getInt(_cursorIndexOfYearlyMonth);
            }
            final Integer _tmpYearlyDayOfMonth;
            if (_cursor.isNull(_cursorIndexOfYearlyDayOfMonth)) {
              _tmpYearlyDayOfMonth = null;
            } else {
              _tmpYearlyDayOfMonth = _cursor.getInt(_cursorIndexOfYearlyDayOfMonth);
            }
            final Long _tmpLastRun;
            if (_cursor.isNull(_cursorIndexOfLastRun)) {
              _tmpLastRun = null;
            } else {
              _tmpLastRun = _cursor.getLong(_cursorIndexOfLastRun);
            }
            final Long _tmpNextRun;
            if (_cursor.isNull(_cursorIndexOfNextRun)) {
              _tmpNextRun = null;
            } else {
              _tmpNextRun = _cursor.getLong(_cursorIndexOfNextRun);
            }
            final boolean _tmpSkipHolidays;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfSkipHolidays);
            _tmpSkipHolidays = _tmp_4 != 0;
            final boolean _tmpIsEnabled;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp_5 != 0;
            _result = new Schedule(_tmpId,_tmpWorkflowId,_tmpWorkflowName,_tmpScheduleType,_tmpHourlyInterval,_tmpTime,_tmpWeeklyDays,_tmpMonthlyDays,_tmpYearlyMonth,_tmpYearlyDayOfMonth,_tmpLastRun,_tmpNextRun,_tmpSkipHolidays,_tmpIsEnabled);
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
}
