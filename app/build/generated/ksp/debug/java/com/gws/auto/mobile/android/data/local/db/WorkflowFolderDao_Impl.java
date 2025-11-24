package com.gws.auto.mobile.android.data.local.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.gws.auto.mobile.android.domain.model.WorkflowFolder;
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
public final class WorkflowFolderDao_Impl implements WorkflowFolderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkflowFolder> __insertionAdapterOfWorkflowFolder;

  private final ListConverter __listConverter = new ListConverter();

  private final EntityDeletionOrUpdateAdapter<WorkflowFolder> __updateAdapterOfWorkflowFolder;

  private final SharedSQLiteStatement __preparedStmtOfDeleteWorkflowFolder;

  public WorkflowFolderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkflowFolder = new EntityInsertionAdapter<WorkflowFolder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `workflow_folders` (`id`,`name`,`workflowIds`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkflowFolder entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        final String _tmp = __listConverter.fromList(entity.getWorkflowIds());
        statement.bindString(3, _tmp);
      }
    };
    this.__updateAdapterOfWorkflowFolder = new EntityDeletionOrUpdateAdapter<WorkflowFolder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `workflow_folders` SET `id` = ?,`name` = ?,`workflowIds` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkflowFolder entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        final String _tmp = __listConverter.fromList(entity.getWorkflowIds());
        statement.bindString(3, _tmp);
        statement.bindString(4, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteWorkflowFolder = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM workflow_folders WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertWorkflowFolder(final WorkflowFolder folder,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkflowFolder.insert(folder);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateWorkflowFolder(final WorkflowFolder folder,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWorkflowFolder.handle(folder);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteWorkflowFolder(final String folderId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteWorkflowFolder.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, folderId);
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
          __preparedStmtOfDeleteWorkflowFolder.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WorkflowFolder>> getAllWorkflowFolders() {
    final String _sql = "SELECT * FROM workflow_folders";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workflow_folders"}, new Callable<List<WorkflowFolder>>() {
      @Override
      @NonNull
      public List<WorkflowFolder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfWorkflowIds = CursorUtil.getColumnIndexOrThrow(_cursor, "workflowIds");
          final List<WorkflowFolder> _result = new ArrayList<WorkflowFolder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkflowFolder _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final List<String> _tmpWorkflowIds;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfWorkflowIds);
            _tmpWorkflowIds = __listConverter.fromString(_tmp);
            _item = new WorkflowFolder(_tmpId,_tmpName,_tmpWorkflowIds);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
