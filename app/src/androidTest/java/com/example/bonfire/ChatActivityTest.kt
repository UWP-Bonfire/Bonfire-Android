package com.example.bonfire

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatActivityTest {

    private lateinit var chatActivity: ChatActivity

    @Before
    fun setUp() {
        // Instantiate ChatActivity on the main thread for logic tests
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            chatActivity = ChatActivity()
        }
    }

    @Test
    fun testIsPrivateChat_WithNull() {
        assertFalse("Null friendId should not be a private chat", chatActivity.isPrivateChat(null))
    }

    @Test
    fun testIsPrivateChat_WithId() {
        assertTrue("Non-null friendId should be a private chat", chatActivity.isPrivateChat("some_uid"))
    }

    @Test
    fun testActivityLaunch_WithExtras() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), ChatActivity::class.java).apply {
            putExtra("id", "test_friend_id")
        }
        ActivityScenario.launch<ChatActivity>(intent).use { scenario ->
            // Verify activity launches without crashing
            scenario.onActivity { activity ->
                assertTrue(activity != null)
            }
        }
    }
}
