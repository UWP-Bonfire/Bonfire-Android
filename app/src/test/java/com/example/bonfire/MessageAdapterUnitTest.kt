package com.example.bonfire

/**
 * Intentionally left without @Test methods.
 *
 * NOTE: MessageAdapter currently constructs Helper (an AppCompatActivity), which is not safe to
 * instantiate in pure JVM unit tests without Robolectric.
 *
 * MessageAdapter behavior is covered via instrumented tests instead.
 */
class MessageAdapterUnitTest
