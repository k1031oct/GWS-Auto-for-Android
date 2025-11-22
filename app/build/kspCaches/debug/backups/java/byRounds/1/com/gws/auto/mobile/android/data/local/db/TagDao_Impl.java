package com.gws.auto.mobile.android.data.local.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.gws.auto.mobile.android.domain.model.Tag;
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
public final class TagDao_Impl implements TagDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Tag> __insertionAdapterOfTag;

  private final EntityDeletionOrUpdateAdapter<Tag> __deletionAdapterOfTag;

  private final EntityDeletionOrUpdateAdapter<Tag> __updateAdapterOfTag;

  public TagDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTag = new EntityInsertionAdapter<Tag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tags` (`name`) VALUES (?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tag entity) {
        statement.bindString(1, entity.getName());
      }
    };
    this.__deletionAdapterOfTag = new EntityDeletionOrUpdateAdapter<Tag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tags` WHERE `name` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tag entity) {
        statement.bindString(1, entity.getName());
      }
    };
    this.__updateAdapterOfTag = new EntityDeletionOrUpdateAdapter<Tag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tags` SET `name` = ? WHERE `name` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tag entity) {
        statement.bindString(1, entity.getName());
        statement.bindString(2, entity.getName());
      }
    };
  }

  @Override
  public Object insertTag(final Tag tag, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTag.insert(tag);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTag(final Tag tag, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTag.handle(tag);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTag(final Tag tag, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTag.handle(tag);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Tag>> getAllTags() {
    final String _sql = "SELECT * FROM tags ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tags"}, new Callable<List<Tag>>() {
      @Override
      @NonNull
      public List<Tag> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final List<Tag> _result = new ArrayList<Tag>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Tag _item;
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            _item = new Tag(_tmpName);
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
