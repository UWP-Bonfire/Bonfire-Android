package com.example.bonfire

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendAddActivityTest {
    
    private lateinit var activity: FriendAddActivity

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            activity = FriendAddActivity()
        }
    }

    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(FriendAddActivity::class.java).use {
            onView(withId(R.id.friend_add_edit)).check(matches(isDisplayed()))
            onView(withId(R.id.friend_add_search_button)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testFriendRequestValid_Empty() {
        assertFalse(activity.friendRequestValid(emptyMap(), ""))
    }

    @Test
    fun testFriendRequestValid_Self() {
        val data = mapOf("friends" to listOf<String>())
        assertFalse(activity.friendRequestValid(data, activity.uid ?: ""))
    }

    @Test
    fun testSearch_Empty() {
        ActivityScenario.launch(FriendAddActivity::class.java).use {
            onView(withId(R.id.friend_add_edit)).perform(typeText(""), closeSoftKeyboard())
            onView(withId(R.id.friend_add_search_button)).perform(click())
        }
    }

    @Test
    fun testNavButtons() {
        ActivityScenario.launch(FriendAddActivity::class.java).use {
            onView(withId(R.id.menu_button_chat)).check(matches(isDisplayed()))
            onView(withId(R.id.menu_button_account)).check(matches(isDisplayed()))
            onView(withId(R.id.menu_button_logout)).check(matches(isDisplayed()))
        }
    }
}
