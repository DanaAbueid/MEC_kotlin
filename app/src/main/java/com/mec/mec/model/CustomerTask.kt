package com.mec.mec.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

data class CustomerTask(
    val customerId: Long,
   val customerName: String,
  val customerLocation: String,
) : Parcelable
