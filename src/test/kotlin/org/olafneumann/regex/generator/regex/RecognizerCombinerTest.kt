package org.olafneumann.regex.generator.regex

import kotlin.test.Test
import kotlin.test.assertEquals

class RecognizerCombinerTest {
    companion object {
        private val recognizer = EchoRecognizer("dummy", "dummy")
        private const val inputText = "abc123abc"
        private val selectedMatches = listOf(
            RecognizerMatch(
                patterns = listOf("[a-z]+"),
                ranges = listOf(IntRange(0, 2)),
                recognizer = recognizer,
                title = "dummy"
            ),
            RecognizerMatch(
                patterns = listOf("[a-z]+"),
                ranges = listOf(IntRange(6, 8)),
                recognizer = recognizer,
                title = "dummy"
            )
        )

    }

    @Test
    fun testMatchCombinationWithoutOnlyPatterns() {
        val expected = "[a-z]+123[a-z]+"

        val actual = RecognizerCombiner.combineMatches(
            inputText = inputText,
            selectedMatches = selectedMatches,
            options = RecognizerCombiner.Options(onlyPatterns = false)
        ).pattern

        assertEquals(expected = expected, actual = actual)
    }

    @Test
    fun testMatchCombinationWithOnlyPatterns() {
        val expected = "[a-z]+.*[a-z]+"

        val actual = RecognizerCombiner.combineMatches(
            inputText = inputText,
            selectedMatches = selectedMatches,
            options = RecognizerCombiner.Options(onlyPatterns = true)
        ).pattern

        assertEquals(expected = expected, actual = actual)
    }
}
