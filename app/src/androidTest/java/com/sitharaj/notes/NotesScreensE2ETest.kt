package com.sitharaj.notes

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E Compose UI tests for Notes screens.
 */
@RunWith(AndroidJUnit4::class)
class NotesScreensE2ETest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule(MainActivity::class.java)

    @Test
    fun notesListScreen_showsTitleAndEmptyState() {
        // Check the top bar title
        composeTestRule.onNodeWithText("Notes").assertIsDisplayed()
        // Check the empty state message
        composeTestRule.onNodeWithText("No notes yet. Tap + to add one.").assertIsDisplayed()
        // Check the add button
        composeTestRule.onNodeWithContentDescription("Add Note").assertIsDisplayed()
    }

    @Test
    fun addNote_navigatesToNoteEditScreen_and_showsFields() {
        // Tap the add button
        composeTestRule.onNodeWithContentDescription("Add Note").performClick()
        // Check for Add Note title
        composeTestRule.onNodeWithText("Add Note").assertIsDisplayed()
        // Check for title and content fields
        composeTestRule.onAllNodes(isEditable())[0].assertIsDisplayed()
        composeTestRule.onAllNodes(isEditable())[1].assertIsDisplayed()
        // Check for back button
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }
}

