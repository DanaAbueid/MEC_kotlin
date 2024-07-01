package com.mec.mec.model

data class UpdateNotes (
    val taskId: Long,
    val note: String
)

data class UpdateApproval (
    val taskId: Long,
    val approval: Boolean
)