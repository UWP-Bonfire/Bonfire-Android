package com.example.bonfire

import com.example.bonfire.ui.theme.Pink40
import com.example.bonfire.ui.theme.Pink80
import com.example.bonfire.ui.theme.Purple40
import com.example.bonfire.ui.theme.Purple80
import com.example.bonfire.ui.theme.PurpleGrey40
import com.example.bonfire.ui.theme.PurpleGrey80
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ThemeUnitTest {

    @Test
    fun themeColorConstants_areDifferentBetweenLightAndDark() {
        // Very basic sanity tests that also add line coverage in ui/theme
        assertNotEquals(Purple80, Purple40)
        assertNotEquals(PurpleGrey80, PurpleGrey40)
        assertNotEquals(Pink80, Pink40)
    }
}
