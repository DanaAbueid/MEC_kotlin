package com.mec.mec.model

data class NewTaskRequest(
    val taskDate: String,
    val taskTime: String,
    val subject: String,
    val details: String,
    val employeeNotes: String,
    val managerNotes: String,
    val approved: Boolean,
    val customerName: String,
    val employeeFirstName: String,
    val employeeLastName: String,
    val done: Boolean
)