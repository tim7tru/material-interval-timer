package com.timmytruong.materialintervaltimer

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertThrows

inline fun <reified T : Throwable> assertThrows(
    noinline executable: suspend () -> Unit
) = assertThrows(T::class.java) { runBlocking { executable() } }