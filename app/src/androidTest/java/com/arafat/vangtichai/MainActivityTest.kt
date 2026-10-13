package com.arafat.vangtichai

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    private val digitIds = intArrayOf(
        R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
        R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
    )

    /** Count views, same order as ChangeCalculator.NOTES (500 ... 1). */
    private val noteIds = intArrayOf(
        R.id.tvNote500, R.id.tvNote100, R.id.tvNote50, R.id.tvNote20,
        R.id.tvNote10, R.id.tvNote5, R.id.tvNote2, R.id.tvNote1
    )

    private fun type(number: String) = number.forEach { onView(withId(digitIds[it - '0'])).perform(click()) }

    private fun clear() = onView(withId(R.id.btnClear)).perform(click())

    private fun backspace(times: Int = 1) = repeat(times) { onView(withId(R.id.btnBackspace)).perform(click()) }

    private fun checkAmount(text: String) = onView(withId(R.id.tvAmount)).check(matches(withText(text)))

    /** A tile shows its count as e.g. "× 13". */
    private fun checkNote(id: Int, count: Long) = onView(withId(id)).check(matches(withText("× $count")))

    /** Checks all 8 counts, in the order 500, 100, 50, 20, 10, 5, 2, 1. */
    private fun checkAllNotes(vararg counts: Long) {
        assertEquals(8, counts.size)
        noteIds.forEachIndexed { i, id -> checkNote(id, counts[i]) }
    }

    private fun checkTotal(text: String) = onView(withId(R.id.tvTotalNotes)).check(matches(withText(text)))

    @After
    fun unlockOrientation() {
        rule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }

    @Test
    fun startsEmptyWithZeroCounts() {
        checkAmount("")
        checkAllNotes(0, 0, 0, 0, 0, 0, 0, 0)
        checkTotal("0 notes")
    }

    @Test
    fun typing688ShowsOneOfEachNote() {
        type("688")
        checkAmount("688")
        checkAllNotes(1, 1, 1, 1, 1, 1, 1, 1)
        checkTotal("8 notes")
    }

    // The example from the assignment PDF: 500x13 100x3 50x1 20x1 10x1 5x0 2x0 1x0.
    @Test
    fun amount6880MatchesAssignment() {
        type("6880")
        checkAmount("6880")
        checkAllNotes(13, 3, 1, 1, 1, 0, 0, 0)
        checkTotal("19 notes")
    }

    @Test
    fun eachDenominationOnItsOwn() {
        ChangeCalculator.NOTES.forEachIndexed { i, note ->
            clear()
            type(note.toString())
            checkAmount(note.toString())
            noteIds.forEachIndexed { j, id -> checkNote(id, if (j == i) 1 else 0) }
            checkTotal("1 note")
        }
    }

    @Test
    fun everyDigitKeyAddsItsDigit() {
        for (d in 1..9) {
            clear()
            type("$d$d")                    // same key twice in a row
            checkAmount("$d$d")
        }
        clear()
        type("1234567890".take(ChangeCalculator.MAX_DIGITS))
        checkAmount("123456789")
        clear()
        type("90")                          // the 0 key appends too
        checkAmount("90")
    }

    @Test
    fun leadingZeroIsReplaced() {
        type("0")
        checkAmount("0")
        checkAllNotes(0, 0, 0, 0, 0, 0, 0, 0)
        type("0")
        checkAmount("0")
        type("5")
        checkAmount("5")
        checkAllNotes(0, 0, 0, 0, 0, 1, 0, 0)
    }

    @Test
    fun digitLimitKeepsLargestAmountCorrect() {
        type("9".repeat(ChangeCalculator.MAX_DIGITS + 3))
        checkAmount("999999999")
        checkAllNotes(1_999_999, 4, 1, 2, 0, 1, 2, 0)
        checkTotal("2000009 notes")
    }

    @Test
    fun clearAfterTypingAndAfterResult() {
        clear()                             // CLEAR on an empty amount is harmless
        checkAmount("")
        type("12")
        clear()
        checkAmount("")
        checkAllNotes(0, 0, 0, 0, 0, 0, 0, 0)
        type("6880")
        checkAllNotes(13, 3, 1, 1, 1, 0, 0, 0)
        clear()
        checkAmount("")
        checkAllNotes(0, 0, 0, 0, 0, 0, 0, 0)
        checkTotal("0 notes")
        type("5")                           // works normally again after CLEAR
        checkAmount("5")
        checkAllNotes(0, 0, 0, 0, 0, 1, 0, 0)
    }

    @Test
    fun backspaceDeletesOneDigitAndRecalculates() {
        type("12345")
        checkAllNotes(24, 3, 0, 2, 0, 1, 0, 0)
        backspace(); checkAmount("1234"); checkAllNotes(2, 2, 0, 1, 1, 0, 2, 0)
        backspace(); checkAmount("123");  checkAllNotes(0, 1, 0, 1, 0, 0, 1, 1)
        backspace(); checkAmount("12");   checkAllNotes(0, 0, 0, 0, 1, 0, 1, 0)
        backspace(); checkAmount("1");    checkAllNotes(0, 0, 0, 0, 0, 0, 0, 1)
        backspace(); checkAmount("")                // empty: the "0" hint is shown
        checkAllNotes(0, 0, 0, 0, 0, 0, 0, 0)
        checkTotal("0 notes")
    }

    @Test
    fun backspaceOnEmptyOrZeroIsHarmless() {
        backspace(5)                                // repeated presses on nothing
        checkAmount("")
        checkAllNotes(0, 0, 0, 0, 0, 0, 0, 0)
        type("0")
        backspace(3)
        checkAmount("")
        type("68")                                  // typing works normally again
        checkAmount("68")
        checkAllNotes(0, 0, 1, 0, 1, 1, 1, 1)
    }

    @Test
    fun backspaceThenTypeThenClear() {
        type("6880")
        backspace()
        checkAmount("688")
        checkAllNotes(1, 1, 1, 1, 1, 1, 1, 1)
        type("5")
        checkAmount("6885")
        checkAllNotes(13, 3, 1, 1, 1, 1, 0, 0)
        clear()                                     // CLEAR still wipes everything
        checkAmount("")
        checkAllNotes(0, 0, 0, 0, 0, 0, 0, 0)
        backspace()
        checkAmount("")
    }

    @Test
    fun backspaceBelowDigitLimitAllowsTypingAgain() {
        type("9".repeat(ChangeCalculator.MAX_DIGITS))
        backspace()
        type("1")
        checkAmount("999999991")
    }

    /** Real rotation, Backspace in both layouts. */
    @Test
    fun backspaceWorksAcrossRotation() {
        rotateTo(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)
        type("68805")
        backspace()
        rotateTo(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        checkAmount("6880")
        checkAllNotes(13, 3, 1, 1, 1, 0, 0, 0)
        backspace()                                 // landscape ⌫ key
        checkAmount("688")
        checkAllNotes(1, 1, 1, 1, 1, 1, 1, 1)
        rotateTo(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)
        checkAmount("688")
        backspace(3)
        checkAmount("")
        checkAllNotes(0, 0, 0, 0, 0, 0, 0, 0)
    }

    @Test
    fun amountSurvivesActivityRecreation() {
        type("688")
        rule.scenario.recreate()   // same lifecycle path as a rotation
        checkAmount("688")
        checkNote(R.id.tvNote500, 1)

        type("0")
        rule.scenario.recreate()
        checkAmount("6880")
        checkAllNotes(13, 3, 1, 1, 1, 0, 0, 0)
    }

    @Test
    fun clearResetsAndStaysClearedAfterRecreation() {
        type("688")
        clear()
        checkAmount("")
        checkNote(R.id.tvNote500, 0)
        rule.scenario.recreate()
        checkAmount("")
        checkAllNotes(0, 0, 0, 0, 0, 0, 0, 0)
    }

    /** Real rotation: portrait -> landscape (layout-land) -> portrait. */
    @Test
    fun rotationKeepsAmountAndResult() {
        rotateTo(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)
        type("688")
        rotateTo(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        checkAmount("688")
        checkAllNotes(1, 1, 1, 1, 1, 1, 1, 1)

        type("0")                           // keys work in landscape too
        checkAmount("6880")
        rotateTo(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)
        checkAmount("6880")
        checkAllNotes(13, 3, 1, 1, 1, 0, 0, 0)
        checkTotal("19 notes")
    }

    @Test
    fun everyKeyIsVisibleWithAtLeast48dpTouchTarget() {
        (digitIds + R.id.btnBackspace + R.id.btnClear).forEach { id ->
            onView(withId(id)).check(matches(isDisplayed()))
            rule.scenario.onActivity {
                val key = it.findViewById<View>(id)
                val min = 48 * it.resources.displayMetrics.density
                assertTrue("key ${it.resources.getResourceEntryName(id)} too small", key.width >= min && key.height >= min)
            }
        }
    }

    /**
     * Regression test for the lost-tap bug. The first tap's delayed "unpress" runs after
     * the second tap's finger-down but before its finger-up (what happens on a busy main
     * thread). A plain Button then drops the second click; KeypadButton must not.
     */
    @Test
    fun quickRepeatedTapIsNotLostWhenPreviousUnpressRunsLate() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        lateinit var key: View
        rule.scenario.onActivity { key = it.findViewById(R.id.btn5) }

        instrumentation.runOnMainSync {
            touch(key, MotionEvent.ACTION_DOWN)
            touch(key, MotionEvent.ACTION_UP)       // tap 1: posts its click + its unpress
            touch(key, MotionEvent.ACTION_DOWN)     // tap 2 starts before those have run
        }
        instrumentation.waitForIdleSync()           // tap 1's click and stale unpress run now
        instrumentation.runOnMainSync { touch(key, MotionEvent.ACTION_UP) }
        instrumentation.waitForIdleSync()

        checkAmount("55")
    }

    /** Sliding the finger off a key before lifting it must still cancel the tap. */
    @Test
    fun slidingOffAKeyCancelsTheTap() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        lateinit var key: View
        rule.scenario.onActivity { key = it.findViewById(R.id.btn7) }

        instrumentation.runOnMainSync {
            touch(key, MotionEvent.ACTION_DOWN)
            touch(key, MotionEvent.ACTION_MOVE, x = -key.width * 2f)
            touch(key, MotionEvent.ACTION_UP, x = -key.width * 2f)
        }
        instrumentation.waitForIdleSync()

        checkAmount("")
    }

    private fun touch(view: View, action: Int, x: Float = view.width / 2f, y: Float = view.height / 2f) {
        val now = SystemClock.uptimeMillis()
        val event = MotionEvent.obtain(now, now, action, x, y, 0)
        event.source = InputDevice.SOURCE_TOUCHSCREEN
        view.dispatchTouchEvent(event)
        event.recycle()
    }

    /** Requests [requested] and waits until the recreated Activity has [expected] orientation. */
    private fun rotateTo(requested: Int, expected: Int) {
        rule.scenario.onActivity { it.requestedOrientation = requested }
        val deadline = SystemClock.uptimeMillis() + 15_000
        var orientation = -1
        while (SystemClock.uptimeMillis() < deadline) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            rule.scenario.onActivity { orientation = it.resources.configuration.orientation }
            if (orientation == expected) return
            SystemClock.sleep(200)
        }
        assertEquals("orientation after rotation", expected, orientation)
    }
}
