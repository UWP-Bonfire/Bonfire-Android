package com.example.bonfire

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WelcomeActivityTest {
    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            onView(withId(R.id.welcome_signin_button)).check(matches(isDisplayed()))
            onView(withId(R.id.welcome_signup_button)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testNavigateToSignIn() {
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            onView(withId(R.id.welcome_signin_button)).perform(click())
            // Verifying no crash during transition
        }
    }

    @Test
    fun testNavigateToSignUp() {
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            onView(withId(R.id.welcome_signup_button)).perform(click())
            // Verifying no crash during transition
        }
    }
}
