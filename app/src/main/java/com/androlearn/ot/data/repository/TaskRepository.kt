package com.androlearn.ot.data.repository

import com.androlearn.ot.data.local.dao.TaskDao
import com.androlearn.ot.data.local.entity.TaskCompletionEntity
import com.androlearn.ot.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksByDay(day: String): Flow<List<TaskEntity>> = taskDao.getTasksByDay(day)

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTaskCompletions(task.id)
        taskDao.deleteTask(task)
    }

    suspend fun getTaskById(id: Int) = taskDao.getTaskById(id)

    fun getCompletionsForDate(
        date: String
    ): Flow<List<TaskCompletionEntity>> =
        taskDao.getCompletionsForDate(date)

    suspend fun getTaskCompletion(
        taskId: Int,
        date: String
    ): TaskCompletionEntity? =
        taskDao.getTaskCompletion(taskId, date)

    suspend fun saveTaskCompletion(
        completion: TaskCompletionEntity
    ) = taskDao.saveTaskCompletion(completion)

    suspend fun getTasksByDayOnce(
        day: String
    ): List<TaskEntity> = taskDao.getTasksByDayOnce(day)

    suspend fun getTaskCompletionsForDateOnce(
        date: String
    ): List<TaskCompletionEntity> =
        taskDao.getCompletionsForDateOnce(date)
}
