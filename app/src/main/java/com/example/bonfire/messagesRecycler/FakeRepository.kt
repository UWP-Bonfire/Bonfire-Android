package com.example.bonfire.messagesRecycler

import com.example.bonfire.R

object FakeRepository {
    val messageUserName = mapOf(
        MessageId.MESSAGE1 to "amaice",
        MessageId.MESSAGE2 to "user",
        MessageId.MESSAGE3 to "amaice",
        MessageId.MESSAGE4 to "user",
        MessageId.MESSAGE5 to "amaice",
        MessageId.MESSAGE6 to "user"
    )

    val messageContent = mapOf(
        MessageId.MESSAGE1 to "Hello there!",
        MessageId.MESSAGE2 to "hi fellow tester",
        MessageId.MESSAGE3 to "Lorem ipsum is a dummy or placeholder text commonly used in graphic design, publishing, and web development. Lorem ipsum is typically a corrupted version of De finibus bonorum et malorum, a 1st-century BC text by the Roman statesman and philosopher Cicero, with words altered, added, and removed to make it nonsensical and improper Latin. Its purpose is to permit a page layout to be designed, independently of the copy that will subsequently populate it, or to demonstrate various fonts of a typeface without meaningful text that could be distractin… ",
        MessageId.MESSAGE4 to "i dont think thats how lorem ipsum works",
        MessageId.MESSAGE5 to "If it's dummy text does it really matter?",
        MessageId.MESSAGE6 to "i suppose not really"
    )

    val messageProfile = mapOf(
        MessageId.MESSAGE1 to R.drawable.amaice_profile_picture,
        MessageId.MESSAGE2 to R.drawable._d_avatar_1,
        MessageId.MESSAGE3 to R.drawable.amaice_profile_picture,
        MessageId.MESSAGE4 to R.drawable._d_avatar_1,
        MessageId.MESSAGE5 to R.drawable.amaice_profile_picture,
        MessageId.MESSAGE6 to R.drawable._d_avatar_1
    )
}