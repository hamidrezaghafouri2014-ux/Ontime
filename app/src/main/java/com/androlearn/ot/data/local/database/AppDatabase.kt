package com.androlearn.ot.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.androlearn.ot.data.local.dao.TaskDao
import com.androlearn.ot.data.local.entity.TaskEntity
import com.androlearn.ot.data.local.entity.TaskCompletionEntity

@Database(
    entities = [
        TaskEntity::class,
        TaskCompletionEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS task_completions (
                        taskId INTEGER NOT NULL,
                        date TEXT NOT NULL,
                        isCompleted INTEGER NOT NULL,
                        PRIMARY KEY(taskId, date)
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_task_completions_date
                    ON task_completions(date)
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    """
                    ALTER TABLE tasks
                    ADD COLUMN priority INTEGER NOT NULL DEFAULT 2
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    ALTER TABLE tasks
                    ADD COLUMN deadlineMillis INTEGER
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    ALTER TABLE tasks
                    ADD COLUMN isMovable INTEGER NOT NULL DEFAULT 1
                    """.trimIndent()
                )
            }
        }
    }
}