package com.mec.mec.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable
