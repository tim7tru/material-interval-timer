package com.timmytruong.materialintervaltimer

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode

object TestConfig : AbstractProjectConfig() {
    override val isolationMode: IsolationMode
        get() = IsolationMode.InstancePerLeaf
}