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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.gws.auto.mobile.android.domain.model.Module;
import com.gws.auto.mobile.android.domain.model.Workflow;
import java.lang.Class;
import java.lang.Exception;
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
public final class WorkflowDao_Impl implements WorkflowDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Workflow> __insertionAdapterOfWorkflow;

  private final ModuleListConverter __moduleListConverter = new ModuleListConverter();

  private final ListConverter __listConverter = new ListConverter();

  private final EntityDeletionOrUpdateAdapter<Workflow> __deletionAdapterOfWorkflow;

  private final EntityDeletionOrUpdateAdapter<Workflow> __updateAdapterOfWorkflow;

  public WorkflowDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkflow = new EntityInsertionAdapter<Workflow>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `workflows` (`id`,`name`,`description`,`modules`,`status`,`trigger`,`tags`,`isFavorite`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Workflow entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        final String _tmp = __moduleListConverter.fromList(entity.getModules());
        statement.bindString(4, _tmp);
        statement.bindString(5, entity.getStatus());
        statement.bindString(6, entity.getTrigger());
        final String _tmp_1 = __listConverter.fromList(entity.getTags());
        statement.bindString(7, _tmp_1);
        final int _tmp_2 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
      }
    };
    this.__deletionAdapterOfWorkflow = new EntityDeletionOrUpdateAdapter<Workflow>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `workflows` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Workflow entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfWorkflow = new EntityDeletionOrUpdateAdapter<Workflow>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `workflows` SET `id` = ?,`name` = ?,`description` = ?,`modules` = ?,`status` = ?,`trigger` = ?,`tags` = ?,`isFavorite` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Workflow entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        final String _tmp = __moduleListConverter.fromList(entity.getModules());
        statement.bindString(4, _tmp);
        statement.bindString(5, entity.getStatus());
        statement.bindString(6, entity.getTrigger());
        final String _tmp_1 = __listConverter.fromList(entity.getTags());
        statement.bindString(7, _tmp_1);
        final int _tmp_2 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        statement.bindString(9, entity.getId());
      }
    };
  }

  @Override
  public Object insertWorkflow(final Workflow workflow,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkflow.insert(workflow);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteWorkflow(final Workflow workflow,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWorkflow.handle(workflow);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateWorkflow(final Workflow workflow,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWorkflow.handle(workflow);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Workflow>> getAllWorkflows() {
    final String _sql = "SELECT * FROM workflows";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workflows"}, new Callable<List<Workflow>>() {
      @Override
      @NonNull
      public List<Workflow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfModules = CursorUtil.getColumnIndexOrThrow(_cursor, "modules");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTrigger = CursorUtil.getColumnIndexOrThrow(_cursor, "trigger");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final List<Workflow> _result = new ArrayList<Workflow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Workflow _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final List<Module> _tmpModules;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfModules);
            _tmpModules = __moduleListConverter.fromString(_tmp);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpTrigger;
            _tmpTrigger = _cursor.getString(_cursorIndexOfTrigger);
            final List<String> _tmpTags;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __listConverter.fromString(_tmp_1);
            final boolean _tmpIsFavorite;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_2 != 0;
            _item = new Workflow(_tmpId,_tmpName,_tmpDescription,_tmpModules,_tmpStatus,_tmpTrigger,_tmpTags,_tmpIsFavorite);
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
  public Object getWorkflowById(final String id, final Continuation<? super Workflow> $completion) {
    final String _sql = "SELECT * FROM workflows WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Workflow>() {
      @Override
      @Nullable
      public Workflow call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfModules = CursorUtil.getColumnIndexOrThrow(_cursor, "modules");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTrigger = CursorUtil.getColumnIndexOrThrow(_cursor, "trigger");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final Workflow _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final List<Module> _tmpModules;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfModules);
            _tmpModules = __moduleListConverter.fromString(_tmp);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpTrigger;
            _tmpTrigger = _cursor.getString(_cursorIndexOfTrigger);
            final List<String> _tmpTags;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __listConverter.fromString(_tmp_1);
            final boolean _tmpIsFavorite;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_2 != 0;
            _result = new Workflow(_tmpId,_tmpName,_tmpDescription,_tmpModules,_tmpStatus,_tmpTrigger,_tmpTags,_tmpIsFavorite);
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
