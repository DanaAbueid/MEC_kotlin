package com.mec.mec.generic

import android.content.Context
import org.json.JSONObject


class Lookups(context: Context) {

    fun getTaskType(): List<String> {
        return listOf<String>(
            "عطل",
            "صيانة",
            "تفقد ",
            "تحصيل"
        )
    }
}