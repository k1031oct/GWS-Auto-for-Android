package com.gws.auto.mobile.android.domain.service

import com.google.api.client.util.DateTime
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

import com.google.api.services.tasks.model.TaskList

/**
 * Service for interacting with the Google Tasks API.
 * Provides methods to create tasks and manage task lists.
 */
@Singleton
class TasksApiService @Inject constructor(
    private val authorizer: GoogleApiAuthorizer
) {

    /**
     * Creates a new task in the specified task list.
     *
     * @param taskListId The ID of the task list to add the task to. Defaults to "@default".
     * @param title The title of the task.
     * @param notes Notes or description for the task.
     * @param dueDate The due date of the task (RFC 3339 timestamp).
     * @return The created Task object.
     * @throws IOException If the API call fails.
     */
    suspend fun createTask(
        taskListId: String = "@default",
        title: String,
        notes: String? = null,
        dueDate: DateTime? = null
    ): Task = withContext(Dispatchers.IO) {
        val service = getService()

        val task = Task().apply {
            this.title = title
            if (!notes.isNullOrEmpty()) {
                this.notes = notes
            }
            if (dueDate != null) {
                this.due = dueDate.toStringRfc3339()
            }
        }

        service.tasks().insert(taskListId, task).execute()
    }

    /**
     * Retrieves all authenticated user's task lists.
     *
     * @return A list of [TaskList] objects.
     * @throws IOException If the API call fails.
     */
    suspend fun getTaskLists(): List<TaskList> = withContext(Dispatchers.IO) {
        val service = getService()
        val response = service.tasklists().list().execute()
        response.items ?: emptyList()
    }

    /**
     * Creates a new task list.
     *
     * @param title The title of the new task list.
     * @return The created [TaskList] object.
     * @throws IOException If the API call fails.
     */
    suspend fun createTaskList(title: String): TaskList = withContext(Dispatchers.IO) {
        val service = getService()
        val taskList = TaskList().setTitle(title)
        service.tasklists().insert(taskList).execute()
    }

    private suspend fun getService(): Tasks {
        val credential = authorizer.getCredential(listOf(Scope.TasksFullAccess.scopeUri))
            ?: throw IOException("Failed to obtain credentials for Google Tasks API")

        return Tasks.Builder(
            authorizer.httpTransport,
            authorizer.jsonFactory,
            credential
        )
            .setApplicationName("GWS Auto for Android")
            .build()
    }
}
