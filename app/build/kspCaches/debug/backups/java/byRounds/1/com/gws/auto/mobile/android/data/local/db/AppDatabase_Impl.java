package com.gws.auto.mobile.android.data.local.db;

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
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile WorkflowDao _workflowDao;

  private volatile SearchHistoryDao _searchHistoryDao;

  private volatile WorkflowFolderDao _workflowFolderDao;

  private volatile TagDao _tagDao;

  private volatile HistoryDao _historyDao;

  private volatile ScheduleDao _scheduleDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(9) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `workflows` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `modules` TEXT NOT NULL, `status` TEXT NOT NULL, `trigger` TEXT NOT NULL, `tags` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL, `order` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `modules` (`id` TEXT NOT NULL, `type` TEXT NOT NULL, `parameters` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `search_history` (`query` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`query`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `workflow_folders` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `workflowIds` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`name` TEXT NOT NULL, PRIMARY KEY(`name`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `execution_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `workflowId` TEXT NOT NULL, `workflowName` TEXT NOT NULL, `executedAt` INTEGER NOT NULL, `status` TEXT NOT NULL, `logs` TEXT NOT NULL, `durationMs` INTEGER NOT NULL, `isBookmarked` INTEGER NOT NULL, `triggerType` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `schedules` (`id` TEXT NOT NULL, `workflowId` TEXT NOT NULL, `workflowName` TEXT NOT NULL, `scheduleType` TEXT NOT NULL, `hourlyInterval` INTEGER, `time` TEXT, `weeklyDays` TEXT, `monthlyDays` TEXT, `yearlyMonth` INTEGER, `yearlyDayOfMonth` INTEGER, `lastRun` INTEGER, `nextRun` INTEGER, `skipHolidays` INTEGER NOT NULL, `isEnabled` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4c28b0e92ef054d664fafb460cf17a17')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `workflows`");
        db.execSQL("DROP TABLE IF EXISTS `modules`");
        db.execSQL("DROP TABLE IF EXISTS `search_history`");
        db.execSQL("DROP TABLE IF EXISTS `workflow_folders`");
        db.execSQL("DROP TABLE IF EXISTS `tags`");
        db.execSQL("DROP TABLE IF EXISTS `execution_history`");
        db.execSQL("DROP TABLE IF EXISTS `schedules`");
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
        final HashMap<String, TableInfo.Column> _columnsWorkflows = new HashMap<String, TableInfo.Column>(9);
        _columnsWorkflows.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("modules", new TableInfo.Column("modules", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("trigger", new TableInfo.Column("trigger", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("tags", new TableInfo.Column("tags", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflows.put("order", new TableInfo.Column("order", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkflows = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWorkflows = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWorkflows = new TableInfo("workflows", _columnsWorkflows, _foreignKeysWorkflows, _indicesWorkflows);
        final TableInfo _existingWorkflows = TableInfo.read(db, "workflows");
        if (!_infoWorkflows.equals(_existingWorkflows)) {
          return new RoomOpenHelper.ValidationResult(false, "workflows(com.gws.auto.mobile.android.domain.model.Workflow).\n"
                  + " Expected:\n" + _infoWorkflows + "\n"
                  + " Found:\n" + _existingWorkflows);
        }
        final HashMap<String, TableInfo.Column> _columnsModules = new HashMap<String, TableInfo.Column>(4);
        _columnsModules.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("parameters", new TableInfo.Column("parameters", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("isEnabled", new TableInfo.Column("isEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysModules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesModules = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoModules = new TableInfo("modules", _columnsModules, _foreignKeysModules, _indicesModules);
        final TableInfo _existingModules = TableInfo.read(db, "modules");
        if (!_infoModules.equals(_existingModules)) {
          return new RoomOpenHelper.ValidationResult(false, "modules(com.gws.auto.mobile.android.domain.model.Module).\n"
                  + " Expected:\n" + _infoModules + "\n"
                  + " Found:\n" + _existingModules);
        }
        final HashMap<String, TableInfo.Column> _columnsSearchHistory = new HashMap<String, TableInfo.Column>(2);
        _columnsSearchHistory.put("query", new TableInfo.Column("query", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSearchHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSearchHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSearchHistory = new TableInfo("search_history", _columnsSearchHistory, _foreignKeysSearchHistory, _indicesSearchHistory);
        final TableInfo _existingSearchHistory = TableInfo.read(db, "search_history");
        if (!_infoSearchHistory.equals(_existingSearchHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "search_history(com.gws.auto.mobile.android.domain.model.SearchHistory).\n"
                  + " Expected:\n" + _infoSearchHistory + "\n"
                  + " Found:\n" + _existingSearchHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsWorkflowFolders = new HashMap<String, TableInfo.Column>(3);
        _columnsWorkflowFolders.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflowFolders.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkflowFolders.put("workflowIds", new TableInfo.Column("workflowIds", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkflowFolders = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWorkflowFolders = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWorkflowFolders = new TableInfo("workflow_folders", _columnsWorkflowFolders, _foreignKeysWorkflowFolders, _indicesWorkflowFolders);
        final TableInfo _existingWorkflowFolders = TableInfo.read(db, "workflow_folders");
        if (!_infoWorkflowFolders.equals(_existingWorkflowFolders)) {
          return new RoomOpenHelper.ValidationResult(false, "workflow_folders(com.gws.auto.mobile.android.domain.model.WorkflowFolder).\n"
                  + " Expected:\n" + _infoWorkflowFolders + "\n"
                  + " Found:\n" + _existingWorkflowFolders);
        }
        final HashMap<String, TableInfo.Column> _columnsTags = new HashMap<String, TableInfo.Column>(1);
        _columnsTags.put("name", new TableInfo.Column("name", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTags = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTags = new TableInfo("tags", _columnsTags, _foreignKeysTags, _indicesTags);
        final TableInfo _existingTags = TableInfo.read(db, "tags");
        if (!_infoTags.equals(_existingTags)) {
          return new RoomOpenHelper.ValidationResult(false, "tags(com.gws.auto.mobile.android.domain.model.Tag).\n"
                  + " Expected:\n" + _infoTags + "\n"
                  + " Found:\n" + _existingTags);
        }
        final HashMap<String, TableInfo.Column> _columnsExecutionHistory = new HashMap<String, TableInfo.Column>(9);
        _columnsExecutionHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExecutionHistory.put("workflowId", new TableInfo.Column("workflowId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExecutionHistory.put("workflowName", new TableInfo.Column("workflowName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExecutionHistory.put("executedAt", new TableInfo.Column("executedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExecutionHistory.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExecutionHistory.put("logs", new TableInfo.Column("logs", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExecutionHistory.put("durationMs", new TableInfo.Column("durationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExecutionHistory.put("isBookmarked", new TableInfo.Column("isBookmarked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExecutionHistory.put("triggerType", new TableInfo.Column("triggerType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExecutionHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExecutionHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExecutionHistory = new TableInfo("execution_history", _columnsExecutionHistory, _foreignKeysExecutionHistory, _indicesExecutionHistory);
        final TableInfo _existingExecutionHistory = TableInfo.read(db, "execution_history");
        if (!_infoExecutionHistory.equals(_existingExecutionHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "execution_history(com.gws.auto.mobile.android.domain.model.History).\n"
                  + " Expected:\n" + _infoExecutionHistory + "\n"
                  + " Found:\n" + _existingExecutionHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsSchedules = new HashMap<String, TableInfo.Column>(14);
        _columnsSchedules.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("workflowId", new TableInfo.Column("workflowId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("workflowName", new TableInfo.Column("workflowName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("scheduleType", new TableInfo.Column("scheduleType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("hourlyInterval", new TableInfo.Column("hourlyInterval", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("time", new TableInfo.Column("time", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("weeklyDays", new TableInfo.Column("weeklyDays", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("monthlyDays", new TableInfo.Column("monthlyDays", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("yearlyMonth", new TableInfo.Column("yearlyMonth", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("yearlyDayOfMonth", new TableInfo.Column("yearlyDayOfMonth", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("lastRun", new TableInfo.Column("lastRun", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("nextRun", new TableInfo.Column("nextRun", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("skipHolidays", new TableInfo.Column("skipHolidays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("isEnabled", new TableInfo.Column("isEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSchedules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSchedules = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSchedules = new TableInfo("schedules", _columnsSchedules, _foreignKeysSchedules, _indicesSchedules);
        final TableInfo _existingSchedules = TableInfo.read(db, "schedules");
        if (!_infoSchedules.equals(_existingSchedules)) {
          return new RoomOpenHelper.ValidationResult(false, "schedules(com.gws.auto.mobile.android.domain.model.Schedule).\n"
                  + " Expected:\n" + _infoSchedules + "\n"
                  + " Found:\n" + _existingSchedules);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4c28b0e92ef054d664fafb460cf17a17", "a10e9e55452acca7d98e0fc496941fec");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "workflows","modules","search_history","workflow_folders","tags","execution_history","schedules");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `workflows`");
      _db.execSQL("DELETE FROM `modules`");
      _db.execSQL("DELETE FROM `search_history`");
      _db.execSQL("DELETE FROM `workflow_folders`");
      _db.execSQL("DELETE FROM `tags`");
      _db.execSQL("DELETE FROM `execution_history`");
      _db.execSQL("DELETE FROM `schedules`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
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
    _typeConvertersMap.put(WorkflowDao.class, WorkflowDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SearchHistoryDao.class, SearchHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WorkflowFolderDao.class, WorkflowFolderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TagDao.class, TagDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HistoryDao.class, HistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ScheduleDao.class, ScheduleDao_Impl.getRequiredConverters());
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
  public WorkflowDao workflowDao() {
    if (_workflowDao != null) {
      return _workflowDao;
    } else {
      synchronized(this) {
        if(_workflowDao == null) {
          _workflowDao = new WorkflowDao_Impl(this);
        }
        return _workflowDao;
      }
    }
  }

  @Override
  public SearchHistoryDao searchHistoryDao() {
    if (_searchHistoryDao != null) {
      return _searchHistoryDao;
    } else {
      synchronized(this) {
        if(_searchHistoryDao == null) {
          _searchHistoryDao = new SearchHistoryDao_Impl(this);
        }
        return _searchHistoryDao;
      }
    }
  }

  @Override
  public WorkflowFolderDao workflowFolderDao() {
    if (_workflowFolderDao != null) {
      return _workflowFolderDao;
    } else {
      synchronized(this) {
        if(_workflowFolderDao == null) {
          _workflowFolderDao = new WorkflowFolderDao_Impl(this);
        }
        return _workflowFolderDao;
      }
    }
  }

  @Override
  public TagDao tagDao() {
    if (_tagDao != null) {
      return _tagDao;
    } else {
      synchronized(this) {
        if(_tagDao == null) {
          _tagDao = new TagDao_Impl(this);
        }
        return _tagDao;
      }
    }
  }

  @Override
  public HistoryDao historyDao() {
    if (_historyDao != null) {
      return _historyDao;
    } else {
      synchronized(this) {
        if(_historyDao == null) {
          _historyDao = new HistoryDao_Impl(this);
        }
        return _historyDao;
      }
    }
  }

  @Override
  public ScheduleDao scheduleDao() {
    if (_scheduleDao != null) {
      return _scheduleDao;
    } else {
      synchronized(this) {
        if(_scheduleDao == null) {
          _scheduleDao = new ScheduleDao_Impl(this);
        }
        return _scheduleDao;
      }
    }
  }
}
