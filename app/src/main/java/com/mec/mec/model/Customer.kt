package com.mec.mec.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Customer(
    val customerName: String,
    val customerLocation: String,
    val customerElevatorType: String,
    val customerPanelElevator: String
) : Parcelable

