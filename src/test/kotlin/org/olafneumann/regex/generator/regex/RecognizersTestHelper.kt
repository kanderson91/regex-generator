package org.olafneumann.regex.generator.regex

import kotlin.test.assertEquals
import kotlin.test.assertSame

object RecognizersTestHelper {
    private fun assertMatchEquals(expected: RecognizerMatch, actual: RecognizerMatch) {
        for (index: Int in 0  until expected.ranges.size) {
            assertEquals(
                expected.getPattern(index = index, includeCapturingGroup = false),
                actual.getPattern(index = index, includeCapturingGroup = false),
                "Pattern $index"
            )
        }
        assertEquals(expected.ranges, actual.ranges, "Ranges")
        assertSame(expected.recognizer, actual.recognizer, "Recognizer")
        assertEquals(expected.title, actual.title, "Title")
    }

    fun assertMatchesEqual(expected: Collection<RecognizerMatch>, actual: Collection<RecognizerMatch>) {
        assertEquals(expected.size, actual.size, message = "Number of RecognizerMatches")
        expected.zip(actual)
            .forEach { assertMatchEquals(expected = it.first, actual = it.second) }
    }
}
