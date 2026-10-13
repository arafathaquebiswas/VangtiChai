package com.arafat.vangtichai

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class ChangeCalculatorTest {

    // Order: 500, 100, 50, 20, 10, 5, 2, 1
    private fun change(amount: Long) = ChangeCalculator.calculateChange(amount)

    @Test fun zeroGivesNoNotes() = assertArrayEquals(longArrayOf(0, 0, 0, 0, 0, 0, 0, 0), change(0))

    @Test fun singleNotes() {
        ChangeCalculator.NOTES.forEachIndexed { i, note ->
            val expected = LongArray(8).also { it[i] = 1 }
            assertArrayEquals("amount $note", expected, change(note.toLong()))
        }
    }

    @Test fun amount688UsesOneOfEachNote() = assertArrayEquals(longArrayOf(1, 1, 1, 1, 1, 1, 1, 1), change(688))

    // Matches the PDF screenshot: 6500 + 300 + 50 + 20 + 10 = 6880, so no 5/2/1 notes.
    @Test fun amount6880() = assertArrayEquals(longArrayOf(13, 3, 1, 1, 1, 0, 0, 0), change(6880))

    @Test fun amount68() = assertArrayEquals(longArrayOf(0, 0, 1, 0, 1, 1, 1, 1), change(68))

    @Test fun largestAllowedAmount() {
        val counts = change(999_999_999)
        assertArrayEquals(longArrayOf(1_999_999, 4, 1, 2, 0, 1, 2, 0), counts)
        val total = counts.indices.sumOf { counts[it] * ChangeCalculator.NOTES[it] }
        assertEquals(999_999_999L, total)
    }

    @Test fun digitsAreAddedOnTheRight() {
        var text = ""
        text = ChangeCalculator.appendDigit(text, 2); assertEquals("2", text)
        text = ChangeCalculator.appendDigit(text, 3); assertEquals("23", text)
        text = ChangeCalculator.appendDigit(text, 4); assertEquals("234", text)
    }

    @Test fun leadingZeroIsReplaced() {
        assertEquals("0", ChangeCalculator.appendDigit("", 0))
        assertEquals("0", ChangeCalculator.appendDigit("0", 0))
        assertEquals("5", ChangeCalculator.appendDigit("0", 5))
        assertEquals("50", ChangeCalculator.appendDigit("5", 0))
    }

    @Test fun inputStopsAtMaxDigits() {
        var text = ""
        repeat(20) { text = ChangeCalculator.appendDigit(text, 9) }
        assertEquals("9".repeat(ChangeCalculator.MAX_DIGITS), text)
    }

    @Test fun backspaceRemovesLastDigitDownToEmpty() {
        var text = "12345"
        for (expected in listOf("1234", "123", "12", "1", "", "")) {
            text = ChangeCalculator.deleteLastDigit(text)
            assertEquals(expected, text)
        }
        assertEquals(0L, ChangeCalculator.parseAmount(text))       // empty shows as 0
        assertEquals("7", ChangeCalculator.appendDigit(text, 7))    // typing works again
        assertEquals("", ChangeCalculator.deleteLastDigit("0"))
    }

    @Test fun emptyTextParsesAsZero() {
        assertEquals(0L, ChangeCalculator.parseAmount(""))
        assertEquals(688L, ChangeCalculator.parseAmount("688"))
    }

    @Test fun sanitizeKeepsKeypadText() {
        assertEquals("", ChangeCalculator.sanitize(""))
        assertEquals("0", ChangeCalculator.sanitize("0"))
        assertEquals("6880", ChangeCalculator.sanitize("6880"))
        assertEquals("999999999", ChangeCalculator.sanitize("999999999"))
    }

    @Test fun sanitizeRejectsInvalidText() {
        assertEquals("", ChangeCalculator.sanitize(null))
        assertEquals("", ChangeCalculator.sanitize("abc"))
        assertEquals("", ChangeCalculator.sanitize("-5"))
        assertEquals("", ChangeCalculator.sanitize("12.5"))
        assertEquals("", ChangeCalculator.sanitize(" 12"))
        assertEquals("", ChangeCalculator.sanitize("007"))
        assertEquals("", ChangeCalculator.sanitize("1234567890"))   // 10 digits
    }

    @Test fun totalNotes() {
        assertEquals(0L, ChangeCalculator.totalNotes(change(0)))
        assertEquals(8L, ChangeCalculator.totalNotes(change(688)))
        assertEquals(19L, ChangeCalculator.totalNotes(change(6880)))
        assertEquals(2_000_009L, ChangeCalculator.totalNotes(change(999_999_999)))
    }
}
