package com.mec.mec.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee")
data class EmployeeEntity(
    @PrimaryKey  val userID: Long,
    val firstName: String,
    val lastName: String
)
