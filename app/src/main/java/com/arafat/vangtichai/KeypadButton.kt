package com.arafat.vangtichai

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.appcompat.widget.AppCompatButton

/**
 * Keypad key that never loses a quick repeated tap.
 *
 * Why this exists: after a tap, a normal Button posts a delayed "unpress" for itself.
 * When the same key is tapped again quickly and the main thread is a little behind,
 * that old unpress can run *between* the new tap's finger-down and finger-up. The
 * button then thinks the new tap was cancelled and silently skips the click
 * (seen on the emulator as 9 rapid taps on "5" giving only 6-8 digits).
 *
 * Fix: while a finger is down on this key, ignore requests to un-press it, except
 * the ones that really end the touch (finger lifted, gesture cancelled, or finger
 * slid off the key), which still work exactly as in a normal Button.
 */
class KeypadButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    /** True from finger-down until the touch ends (lift, cancel or slide off the key). */
    private var fingerOnKey = false

    // super.onTouchEvent() still detects the click and calls performClick() itself.
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> fingerOnKey = true
            // Cleared before super so the framework's own un-press for these is not blocked.
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> fingerOnKey = false
            MotionEvent.ACTION_MOVE -> if (!isInsideWithSlop(event.x, event.y)) fingerOnKey = false
        }
        return super.onTouchEvent(event)
    }

    override fun setPressed(pressed: Boolean) {
        // A stale "unpress" from the previous tap: keep the current tap alive.
        if (!pressed && fingerOnKey) return
        super.setPressed(pressed)
    }

    // Clicks still go through the standard path (and accessibility services).
    override fun performClick(): Boolean = super.performClick()

    /** Same test View uses to decide the finger has left the key. */
    private fun isInsideWithSlop(x: Float, y: Float): Boolean =
        x >= -touchSlop && y >= -touchSlop && x < width + touchSlop && y < height + touchSlop
}
