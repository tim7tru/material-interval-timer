package com.timmytruong.materialintervaltimer.model

import com.timmytruong.materialintervaltimer.ui.interfaces.OnClickListeners

data class Dialog(
    var title: String = "",
    var message: String? = "",
    var negativeMessage: String = "",
    var positiveMessage: String = "",
    var callback: OnClickListeners.DialogCallback? = null
)