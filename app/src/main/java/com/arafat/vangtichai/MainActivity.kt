package com.arafat.vangtichai

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    /**
     * The amount typed so far, stored as text so a pressed digit can simply be
     * added on the right. "" means nothing has been entered yet.
     */
    private var amountText = ""

    private lateinit var amountView: TextView
    private lateinit var totalNotesView: TextView
    /** Same order as ChangeCalculator.NOTES. */
    private lateinit var countViews: Array<TextView>
    private lateinit var tileViews: Array<View>

    /**
     * What is currently on screen (-1 / null = nothing yet). updateScreen() compares
     * against these and only touches views whose value actually changed.
     */
    private var shownAmount: String? = null
    private val shownCounts = LongArray(ChangeCalculator.NOTES.size) { -1L }
    private var shownTotal = -1L

    private var limitToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Draw behind the system bars with dark icons on the light background; the
        // insets listener below keeps the content clear of the bars and display cutout.
        val darkScrim = ContextCompat.getColor(this, R.color.bar_scrim_dark)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, darkScrim),
            navigationBarStyle = SystemBarStyle.light(
                ContextCompat.getColor(this, R.color.bar_scrim_light), darkScrim
            )
        )
        super.onCreate(savedInstanceState)
        // Android picks layout/ or layout-land/ for the current orientation.
        setContentView(R.layout.activity_main)
        applySystemBarInsets()

        // Look every view up once; updateScreen() only uses these references.
        amountView = findViewById(R.id.tvAmount)
        totalNotesView = findViewById(R.id.tvTotalNotes)
        countViews = Array(NOTE_COUNT_IDS.size) { findViewById(NOTE_COUNT_IDS[it]) }
        tileViews = Array(NOTE_TILE_IDS.size) { findViewById(NOTE_TILE_IDS[it]) }

        // One listener per key, set once per Activity instance. DIGIT_BUTTON_IDS[d] is digit d.
        DIGIT_BUTTON_IDS.forEachIndexed { digit, id ->
            findViewById<View>(id).setOnClickListener { onDigitPressed(digit) }
        }
        findViewById<View>(R.id.btnBackspace).setOnClickListener { onBackspacePressed() }
        findViewById<View>(R.id.btnClear).setOnClickListener { onClearPressed() }

        // Rotation destroys and recreates this Activity. Restore the amount that
        // onSaveInstanceState stored; the change table is recalculated from it.
        amountText = ChangeCalculator.sanitize(savedInstanceState?.getString(KEY_AMOUNT))
        updateScreen()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_AMOUNT, amountText)
    }

    /** Digits and Backspace typed on a hardware keyboard (or the emulator's) work like the keypad. */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.hasNoModifiers()) {
            if (event.repeatCount == 0) onBackspacePressed()
            return true
        }
        val digit = when (keyCode) {
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> keyCode - KeyEvent.KEYCODE_0
            in KeyEvent.KEYCODE_NUMPAD_0..KeyEvent.KEYCODE_NUMPAD_9 -> keyCode - KeyEvent.KEYCODE_NUMPAD_0
            else -> return super.onKeyDown(keyCode, event)
        }
        if (!event.hasNoModifiers()) return super.onKeyDown(keyCode, event)
        // Holding a key down must not flood the amount with repeats.
        if (event.repeatCount == 0) onDigitPressed(digit)
        return true
    }

    private fun onDigitPressed(digit: Int) {
        if (amountText.length >= ChangeCalculator.MAX_DIGITS) {
            showLimitMessage()
            return
        }
        amountText = ChangeCalculator.appendDigit(amountText, digit)
        updateScreen()
    }

    /** Removes the last digit; the change table is recalculated for the shorter amount. */
    private fun onBackspacePressed() {
        limitToast?.cancel()
        amountText = ChangeCalculator.deleteLastDigit(amountText)
        updateScreen()
    }

    private fun onClearPressed() {
        limitToast?.cancel()
        amountText = ""
        updateScreen()
    }

    /**
     * Shows the current amount and its change. Called after every change to amountText.
     * The arithmetic is a few integer divisions, so it runs directly on the main thread;
     * only views whose value changed are updated, and every updated view has a fixed
     * size, so a tap redraws a few views instead of re-measuring the whole screen.
     */
    private fun updateScreen() {
        if (amountText != shownAmount) {
            shownAmount = amountText
            amountView.text = amountText
        }

        val counts = ChangeCalculator.calculateChange(ChangeCalculator.parseAmount(amountText))
        for (i in counts.indices) {
            val count = counts[i]
            if (count == shownCounts[i]) continue
            shownCounts[i] = count
            countViews[i].text = getString(R.string.note_count, count)
            tileViews[i].isActivated = count > 0
            tileViews[i].contentDescription = resources.getQuantityString(
                R.plurals.note_description, count.toInt(), ChangeCalculator.NOTES[i], count
            )
        }

        val total = ChangeCalculator.totalNotes(counts)
        if (total != shownTotal) {
            shownTotal = total
            // At most ~2 million notes (for 999,999,999), so the quantity fits in an Int.
            totalNotesView.text =
                resources.getQuantityString(R.plurals.total_notes, total.toInt(), total)
        }
    }

    private fun showLimitMessage() {
        limitToast?.cancel()
        limitToast = Toast.makeText(
            this,
            getString(R.string.max_digits_reached, ChangeCalculator.MAX_DIGITS),
            Toast.LENGTH_SHORT
        ).also { it.show() }
    }

    /**
     * The window is drawn edge-to-edge on every Android version (see onCreate), so pad
     * the screen by the status bar, navigation bar and display cutout sizes.
     */
    private fun applySystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private companion object {
        const val KEY_AMOUNT = "amount"

        /** Index = digit. */
        val DIGIT_BUTTON_IDS = intArrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        /** Same order as ChangeCalculator.NOTES. */
        val NOTE_COUNT_IDS = intArrayOf(
            R.id.tvNote500, R.id.tvNote100, R.id.tvNote50, R.id.tvNote20,
            R.id.tvNote10, R.id.tvNote5, R.id.tvNote2, R.id.tvNote1
        )
        val NOTE_TILE_IDS = intArrayOf(
            R.id.tile500, R.id.tile100, R.id.tile50, R.id.tile20,
            R.id.tile10, R.id.tile5, R.id.tile2, R.id.tile1
        )
    }
}
