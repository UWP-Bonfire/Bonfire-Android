package com.example.bonfire

import android.content.Context
import android.widget.ImageView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalArgumentException

@RunWith(AndroidJUnit4::class)
class HelperTest {

    private lateinit var helper: Helper
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            helper = Helper()
        }
    }

    @Test
    fun testFirebasePath() {
        assertEquals("gs://bonfire-d8db1.firebasestorage.app", helper.firebasePath)
    }

    @Test
    fun testSetProfilePicture_InvalidUrl() {
        // This should trigger the catch block and set the default pfp
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val imageView = ImageView(context)
            try {
                helper.setProfilePicture(context, "invalid_url", imageView)
                // If it doesn't crash, we verify the placeholder is handled by checking logic path
                // Actual image verification requires more complex tools, but we test the exception handling here
            } catch (e: Exception) {
                org.junit.Assert.fail("setProfilePicture should handle invalid URLs gracefully")
            }
        }
    }

    @Test
    fun testIncrementUnopened_Persistence() {
        val friendId = "persist_test"
        helper.incrementUnopened(context, friendId)
        
        val prefs = context.getSharedPreferences("notif_limits", Context.MODE_PRIVATE)
        assertEquals(1, prefs.getInt("unopened_$friendId", 0))
        
        helper.incrementUnopened(context, friendId)
        assertEquals(2, prefs.getInt("unopened_$friendId", 0))
    }
}
