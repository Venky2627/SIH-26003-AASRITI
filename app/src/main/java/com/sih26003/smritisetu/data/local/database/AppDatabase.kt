package com.sih26003.smritisetu.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sih26003.smritisetu.data.local.dao.DoctorAccessDao
import com.sih26003.smritisetu.data.local.dao.GameSessionDao
import com.sih26003.smritisetu.data.local.dao.PatientDao
import com.sih26003.smritisetu.data.local.dao.RelationshipDao
import com.sih26003.smritisetu.data.local.dao.ReminderDao
import com.sih26003.smritisetu.data.local.dao.SyncQueueDao
import com.sih26003.smritisetu.data.local.dao.UserDao
import com.sih26003.smritisetu.data.local.dao.CareLogDao
import com.sih26003.smritisetu.data.local.entities.CareLogEntity
import com.sih26003.smritisetu.data.local.entities.DoctorAccessEntity
import com.sih26003.smritisetu.data.local.entities.GameSessionEntity
import com.sih26003.smritisetu.data.local.entities.PatientEntity
import com.sih26003.smritisetu.data.local.entities.RelationshipEntity
import com.sih26003.smritisetu.data.local.entities.ReminderEntity
import com.sih26003.smritisetu.data.local.entities.SyncQueueEntity
import com.sih26003.smritisetu.data.local.entities.UserEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `care_logs` (
                `id` TEXT NOT NULL,
                `patientId` TEXT NOT NULL,
                `authorRole` TEXT NOT NULL,
                `category` TEXT NOT NULL,
                `severity` TEXT NOT NULL,
                `notes` TEXT NOT NULL,
                `timestamp` INTEGER NOT NULL,
                `isSynced` INTEGER NOT NULL,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`patientId`) REFERENCES `patients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_care_logs_patientId` ON `care_logs` (`patientId`)")
    }
}

@Database(
    entities = [
        UserEntity::class,
        PatientEntity::class,
        RelationshipEntity::class,
        GameSessionEntity::class,
        ReminderEntity::class,
        DoctorAccessEntity::class,
        SyncQueueEntity::class,
        CareLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun patientDao(): PatientDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun gameSessionDao(): GameSessionDao
    abstract fun reminderDao(): ReminderDao
    abstract fun doctorAccessDao(): DoctorAccessDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun careLogDao(): CareLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smritisetu.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
