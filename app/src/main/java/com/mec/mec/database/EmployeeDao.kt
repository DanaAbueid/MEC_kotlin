package com.mec.mec.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EmployeeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployee(employees: List<EmployeeEntity>): List<Long>

    @Query("SELECT * FROM employee WHERE firstName = :firstName AND lastName = :lastName")
    suspend fun getEmployeeByName(firstName: String, lastName: String): EmployeeEntity?

    @Query("SELECT * FROM employee")
    suspend fun getAllEmployees(): List<EmployeeEntity>
}