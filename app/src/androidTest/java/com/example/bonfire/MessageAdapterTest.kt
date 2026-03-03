package com.example.bonfire

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class MessageAdapterTest {

    private lateinit var adapter: MessageAdapter

    @Before
    fun setUp() {
        // MessageAdapter instantiates Helper which is an Activity, so it needs to be on Main thread
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            adapter = MessageAdapter(arrayListOf(), false, "uid")
        }
    }

    @Test
    fun testFormatTimestampToString() {
        val calendar = Calendar.getInstance()
        calendar.set(2023, Calendar.OCTOBER, 10, 14, 30) // 2:30 PM
        val timestamp = Timestamp(calendar.time)
        
        val formatted = adapter.formatTimestampToString(timestamp)
        assertEquals("02:30 PM", formatted)
    }

    @Test
    fun testItemCount() {
        val data = arrayListOf<Map<String, Any>?>(
            mapOf("text" to "Hello"),
            mapOf("text" to "World")
        )
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val adapterWithData = MessageAdapter(data, false, "uid")
            assertEquals(2, adapterWithData.itemCount)
        }
    }
}
