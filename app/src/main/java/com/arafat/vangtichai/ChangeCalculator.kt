package com.arafat.vangtichai

/**
 * Pure logic for VangtiChai, kept separate from the Activity so it can be unit tested.
 */
object ChangeCalculator {

    /** Taka notes, largest first. The order matters for the greedy algorithm. */
    val NOTES = intArrayOf(500, 100, 50, 20, 10, 5, 2, 1)

    /**
     * Longest amount the keypad accepts. 9 digits (up to 999,999,999) fits easily in
     * a Long, so the amount can never overflow or wrap around to a negative number.
     */
    const val MAX_DIGITS = 9

    /**
     * Returns the new amount text after [digit] is pressed: the digit is added on
     * the right. A lone leading "0" is replaced instead of kept, so "0" then "5"
     * gives "5", not "05". If [current] is already [MAX_DIGITS] long it is returned unchanged.
     */
    fun appendDigit(current: String, digit: Int): String {
        require(digit in 0..9) { "digit must be 0-9, was $digit" }
        if (current.length >= MAX_DIGITS) return current
        return if (current == "0") digit.toString() else current + digit
    }

    /**
     * Returns the new amount text after Backspace is pressed: the last digit is removed.
     * Deleting the only digit gives "" (shown as 0); on "" it does nothing.
     */
    fun deleteLastDigit(current: String): String = current.dropLast(1)

    /**
     * Greedy change calculation: for each note from largest to smallest, take as many
     * of that note as fit into what is left, then continue with the remainder.
     *
     * Returns one count per entry of [NOTES], in the same order.
     * Example: 688 -> 500x1, 100x1, 50x1, 20x1, 10x1, 5x1, 2x1, 1x1.
     */
    fun calculateChange(amount: Long): LongArray {
        require(amount >= 0) { "amount must not be negative, was $amount" }
        var remaining = amount
        return LongArray(NOTES.size) { i ->
            val count = remaining / NOTES[i]
            remaining %= NOTES[i]
            count
        }
    }

    /** Converts the keypad text to a number; empty text counts as 0. */
    fun parseAmount(text: String): Long = text.toLongOrNull() ?: 0L

    /**
     * Returns [text] if the keypad could have produced it (empty, "0", or up to [MAX_DIGITS]
     * digits without a leading zero), otherwise "". Used when restoring saved state, so a
     * damaged or unexpected value can never put the screen into an impossible state.
     */
    fun sanitize(text: String?): String = when {
        text == null || text.length > MAX_DIGITS -> ""
        !text.all { it in '0'..'9' } -> ""
        text.length > 1 && text[0] == '0' -> ""
        else -> text
    }

    /** Total number of notes in a [calculateChange] result. */
    fun totalNotes(counts: LongArray): Long = counts.sum()
}
