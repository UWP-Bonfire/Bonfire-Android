package com.example.bonfire

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupChatListActivityTest {

    private lateinit var activity: GroupChatListActivity

    @Before
    fun setUp() {
        // Instantiate on main thread for logic tests
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            activity = GroupChatListActivity()
        }
    }

    @Test
    fun testGetChatIdWithFriend() {
        val friendId = "friend123"
        val chatId = activity.getChatIdWithFriend(friendId)
        
        // The chatId should be "chats/sorted_uids/messages"
        // Since uid is null during unit test instantiation, we check the structure
        assertTrue("Chat ID should start with chats/", chatId.startsWith("chats/"))
        assertTrue("Chat ID should end with /messages", chatId.endsWith("/messages"))
    }

    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(GroupChatListActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertNotNull(activity)
            }
        }
    }
}

private fun assertTrue(message: String, condition: Boolean) {
    org.junit.Assert.assertTrue(message, condition)
}
