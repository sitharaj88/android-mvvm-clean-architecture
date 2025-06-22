package com.sitharaj.notes.common

import android.os.Build
import android.util.Log
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], manifest = Config.NONE)
class AndroidLoggerTest {
    @Before
    fun setUp() {
        mockkStatic(Log::class)
    }

    @Test
    fun `log debug calls Log d`() {
        val logger = AndroidLogger()
        logger.d("TAG", "debug message")
        verify { Log.d("TAG", "debug message") }
    }

    @Test
    fun `log info calls Log i`() {
        val logger = AndroidLogger()
        logger.i("TAG", "info message")
        verify { Log.i("TAG", "info message") }
    }

    @Test
    fun `log warn calls Log w`() {
        val logger = AndroidLogger()
        logger.w("TAG", "warn message", null)
        verify { Log.w("TAG", "warn message", null) }
    }

    @Test
    fun `log error calls Log e`() {
        val logger = AndroidLogger()
        logger.e("TAG", "error message", null)
        verify { Log.e("TAG", "error message", null) }
    }
}
