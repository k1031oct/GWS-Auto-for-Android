package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.data.local.db.TagDao
import com.gws.auto.mobile.android.domain.model.Tag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(private val tagDao: TagDao) {

    fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()

    suspend fun addTag(tag: Tag) {
        tagDao.insertTag(tag)
    }

    suspend fun deleteTag(tag: Tag) {
        tagDao.deleteTag(tag)
    }

    suspend fun updateTag(tag: Tag) {
        tagDao.updateTag(tag)
    }
}
