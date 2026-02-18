package com.example.bonfire

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for notification logic in Helper.
 */
@RunWith(AndroidJUnit4::class)
class HelperNotificationTest {

    private lateinit var helper: Helper
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        
        // Helper inherits from AppCompatActivity, so it must be instantiated on the main thread
        // to avoid the "Can't create handler inside thread..." error.
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            helper = Helper()
        }
        
        // Clear shared preferences before each test to ensure isolation
        val prefs = context.getSharedPreferences("notif_limits", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun testIncrementUnopened_Initial() {
        val friendId = "testFriend"
        val count = helper.incrementUnopened(context, friendId)
        assertEquals("First increment should be 1", 1, count)
    }

    @Test
    fun testIncrementUnopened_Multiple() {
        val friendId = "testFriend"
        helper.incrementUnopened(context, friendId)
        helper.incrementUnopened(context, friendId)
        val count = helper.incrementUnopened(context, friendId)
        assertEquals("Third increment should be 3", 3, count)
    }

    @Test
    fun testIncrementUnopened_SeparateFriends() {
        val friend1 = "friend1"
        val friend2 = "friend2"
        
        helper.incrementUnopened(context, friend1)
        helper.incrementUnopened(context, friend1)
        val count2 = helper.incrementUnopened(context, friend2)
        
        assertEquals("Friend 2 count should be 1", 1, count2)
        
        val prefs = context.getSharedPreferences("notif_limits", Context.MODE_PRIVATE)
        assertEquals("Friend 1 count should remain 2", 2, prefs.getInt("unopened_$friend1", 0))
    }

    @Test
    fun testIsLimitEnabled_Default() {
        assertFalse("Limit should be disabled by default", helper.isLimitEnabled(context, "anyFriend"))
    }

    @Test
    fun testIsLimitEnabled_SetTrue() {
        val friendId = "limitedFriend"
        val prefs = context.getSharedPreferences("notif_limits", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("limit_enabled_$friendId", true).commit()
        
        assertTrue("Limit should be enabled after setting it to true", helper.isLimitEnabled(context, friendId))
    }

    @Test
    fun testIsLimitEnabled_SetFalse() {
        val friendId = "unlimitedFriend"
        val prefs = context.getSharedPreferences("notif_limits", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("limit_enabled_$friendId", false).commit()
        
        assertFalse("Limit should be disabled after setting it to false", helper.isLimitEnabled(context, friendId))
    }

    @Test
    fun testGetOpenChatFriendId_Default() {
        assertNull("Open chat friend ID should be null by default", helper.getOpenChatFriendId(context))
    }

    @Test
    fun testGetOpenChatFriendId_Set() {
        val friendId = "activeChat"
        val prefs = context.getSharedPreferences("notif_limits", Context.MODE_PRIVATE)
        prefs.edit().putString("open_chat_friendId", friendId).commit()
        
        assertEquals("Should return the correct open chat friend ID", friendId, helper.getOpenChatFriendId(context))
    }

    @Test
    fun testSendNotification_SmokeTest() {
        // Verifies that sendNotification can be called with an ApplicationContext without crashing.
        try {
            helper.sendNotification("Title", "Body", context, "friend123")
        } catch (e: Exception) {
            org.junit.Assert.fail("sendNotification crashed with ApplicationContext: ${e.message}")
        }
    }

    @Test
    fun testAttemptNotification_WithAppContext() {
        // attemptNotification has logic to handle Activity vs non-Activity contexts.
        try {
            helper.attemptNotification("Title", "Body", context, "friend123")
        } catch (e: Exception) {
            org.junit.Assert.fail("attemptNotification crashed with ApplicationContext: ${e.message}")
        }
    }
}
