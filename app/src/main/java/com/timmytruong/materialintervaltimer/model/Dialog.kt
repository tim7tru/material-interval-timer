package com.timmytruong.materialintervaltimer.model

import com.timmytruong.materialintervaltimer.ui.reusable.DialogClicks

data class Dialog(
    var title: String = "",
    var message: String? = "",
    var negativeMessage: String = "",
    var positiveMessage: String = "",
    var callback: DialogClicks? = null
)