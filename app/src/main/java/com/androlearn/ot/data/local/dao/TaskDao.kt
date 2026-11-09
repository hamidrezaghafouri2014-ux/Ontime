package com.androlearn.ot.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.androlearn.ot.data.local.entity.TaskCompletionEntity
import com.androlearn.ot.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY startHour ASC, startMinute ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE ',' || selectedDays || ',' LIKE '%,' || :day || ',%'
        ORDER BY startHour ASC, startMinute ASC
        """
    )
    fun getTasksByDay(day: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskEntity?



    @Query("""
    SELECT * FROM task_completions
    WHERE taskId = :taskId AND date = :date
""")
    suspend fun getTaskCompletion(
        taskId: Int,
        date: String
    ): TaskCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTaskCompletion(
        completion: TaskCompletionEntity
    )

    @Query("""
    DELETE FROM task_completions
    WHERE taskId = :taskId
""")
    suspend fun deleteTaskCompletions(taskId: Int)

    @Query("""
    SELECT * FROM task_completions
    WHERE date = :date
""")
    fun getCompletionsForDate(
        date: String
    ): Flow<List<TaskCompletionEntity>>

    @Query("""
    SELECT * FROM tasks
    WHERE ',' || selectedDays || ',' LIKE '%,' || :day || ',%'
    ORDER BY startHour ASC, startMinute ASC
""")
    suspend fun getTasksByDayOnce(day: String): List<TaskEntity>

    @Query("""
    SELECT * FROM task_completions
    WHERE date = :date
""")
    suspend fun getCompletionsForDateOnce(
        date: String
    ): List<TaskCompletionEntity>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int

    @androidx.room.Transaction
    suspend fun insertTaskWithFreeLimit(task: TaskEntity): Long {
        if (getTaskCount() >= 3) {
            return -1L
        }
        return insertTask(task)
    }
}