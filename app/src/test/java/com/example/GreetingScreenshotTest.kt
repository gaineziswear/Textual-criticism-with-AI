package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.Text
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test

class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun applicationThemeRenders() {
    composeTestRule.setContent {
      MyApplicationTheme {
        Text("Glossa")
      }
    }

    composeTestRule.onNodeWithText("Glossa").assertIsDisplayed()
  }
}
