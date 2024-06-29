package com.mec.mec.model

data class Task(
    val taskId: Int,
    val taskDate: String,
    val taskTime: String,
    val subject: String,
    val details: String,
    val done: Boolean,
    val employeeNotes: String,
    val managerNotes: String,
    val approved: Boolean,
    val customer: Customer
)