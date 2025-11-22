package com.gws.auto.mobile.android.data.local.db

import androidx.room.*
import com.gws.auto.mobile.android.domain.model.Tag
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Update
    suspend fun updateTag(tag: Tag)
}
