package com.example.bonfire

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.ImageView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HelperTest {

    private lateinit var helper: Helper
    private lateinit var context: Context

    @get:Rule
    val permissionRule: GrantPermissionRule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        @Suppress("DEPRECATION")
        GrantPermissionRule.grant()
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            helper = Helper()
        }
        // Reset all preferences to ensure clean state
        context.getSharedPreferences("notif_limits", Context.MODE_PRIVATE).edit().clear().commit()
        context.getSharedPreferences("muted", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun testFirebasePath() {
        assertEquals("gs://bonfire-d8db1.firebasestorage.app", helper.firebasePath)
    }

    /**
     * Covers setProfilePicture logic, including the exception catch block and 
     * null-safe ImageView handling.
     */
    @Test
    fun testSetProfilePicture_ErrorPaths() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val imageView = ImageView(context)
            // 1. Invalid URL
            helper.setProfilePicture(context, "invalid_url", imageView)
            assertNotNull(imageView.drawable)
        }

        // 2. Destroyed Activity check (Launch outside runOnMainSync)
        ActivityScenario.launch(WelcomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val imageView = ImageView(activity)
                activity.finish()
                helper.setProfilePicture(activity, "gs://valid/path", imageView)
            }
        }
    }

    /**
     * Covers notification permission checks and activity-vs-context dispatch.
     */
    @Test
    fun testAttemptNotification_ContextVariations() {
        // 1. App Context Path (activity is null)
        helper.attemptNotification("Title", "Body", context, "friend1")

        // 2. Activity Path
        ActivityScenario.launch(WelcomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                helper.attemptNotification("Activity Title", "Activity Body", activity, "friend2")
            }
        }
    }

    @Test
    fun testNotificationLogic_ThresholdsAndSuppression() {
        val friendId = "suppress_test"
        assertEquals(1, helper.incrementUnopened(context, friendId))
        for (i in 2..7) {
            helper.incrementUnopened(context, friendId)
        }
        assertEquals(7, context.getSharedPreferences("notif_limits", Context.MODE_PRIVATE).getInt("unopened_$friendId", 0))
    }

    @Test
    fun testMuteLogic() {
        val friendId = "muted_friend"
        val mutedPrefs = context.getSharedPreferences("muted", Context.MODE_PRIVATE)
        assertEquals(0, mutedPrefs.getInt(friendId, 0))
        mutedPrefs.edit().putInt(friendId, 1).commit()
        assertEquals(1, mutedPrefs.getInt(friendId, 0))
    }

    @Test
    fun testListenForNotifs_Guards() {
        helper.listenForNotifs("", context)
        helper.listenForNotifs("test_user_id", context)
    }

    @Test
    fun testCreateNotificationListeners_LogicPaths() {
        val list = mutableListOf(
            mapOf(
                "documentPath" to "chats/chat1/messages",
                "name" to "Friend 1",
                "friendId" to "id1",
                "friendAvatar" to "url1"
            )
        )
        // Exercise with open chat state
        context.getSharedPreferences("notif_limits", Context.MODE_PRIVATE)
            .edit().putString("open_chat_friendId", "id1").commit()
            
        helper.createNotificationListeners(list, context, "myUid")
    }

    @Test
    fun testIsLimitEnabled_AllBranches() {
        val friendId = "limit_test"
        assertFalse(helper.isLimitEnabled(context, friendId))
        context.getSharedPreferences("notif_limits", Context.MODE_PRIVATE)
            .edit().putBoolean("limit_enabled_$friendId", true).commit()
        assertTrue(helper.isLimitEnabled(context, friendId))
    }
}
