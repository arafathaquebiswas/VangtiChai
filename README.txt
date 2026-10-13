VangtiChai - CSE 489 Assignment 1
=================================

Student name : Arafat Haque Biswas
Student ID   : 24241325
Course       : CSE 489 - Mobile Application Development
Assignment   : Assignment 1
App name     : VangtiChai
Language     : Kotlin (XML layouts with ConstraintLayout, AndroidX AppCompat)
Min SDK      : API 21 (Android 5.0 Lollipop)
Target SDK   : API 36 (compileSdk 36)
Build tools  : Android Gradle Plugin 9.1.0 (built-in Kotlin), Gradle 9.3.1 (wrapper), JDK 17


1. WHAT THE APP DOES
--------------------
- A numeric keypad (digits 0-9 and CLEAR) built from Buttons (KeypadButton, a small
  Button subclass); the system keyboard is never used. Digits typed on a hardware
  keyboard (or the emulator's) also work.
- Every digit pressed is added to the right of the amount: 2 -> 23 -> 234.
- The amount is shown at the top in a navy card under the "Taka:" label.
- Eight tiles (৳500, ৳100, ৳50, ৳20, ৳10, ৳5, ৳2, ৳1) show how many of each note
  make up the amount, e.g. "৳500  × 13". Tiles with at least one note are
  highlighted in gold, and the total number of notes is shown above them. Everything
  updates immediately after every key press.
- CLEAR empties the amount and resets every count to 0.
- Rotating the device keeps the amount and the change table, and switches to a
  different layout for landscape.

Input rules (my own choices; the PDF does not specify them):
- Leading zeros: pressing 0 on an empty amount shows "0"; pressing another digit
  after a lone "0" replaces it (0, 5 -> "5"), so the amount never shows "05".
- The amount is limited to 9 digits (max 999,999,999). Extra key presses are
  ignored and a short "Digit limit reached (9)" message is shown. The amount is
  held as a Long, so it can never overflow or become negative.


2. HOW THE CHANGE IS CALCULATED
-------------------------------
Greedy algorithm (ChangeCalculator.calculateChange), notes from largest to smallest:

    remaining = amount
    for note in [500, 100, 50, 20, 10, 5, 2, 1]:
        count[note] = remaining / note     (integer division)
        remaining   = remaining % note

Examples:   688 -> 500:1 100:1 50:1 20:1 10:1 5:1 2:1 1:1
           6880 -> 500:13 100:3 50:1 20:1 10:1 5:0 2:0 1:0  (same as the PDF screenshot)


3. PROJECT STRUCTURE (important files)
--------------------------------------
VangtiChai/
  VangtiChai_Finance_Calculator_Icon.png original logo (source of all icon files)
  settings.gradle.kts, build.gradle.kts, gradle.properties
  gradle/libs.versions.toml              dependency versions
  gradle/wrapper/                        Gradle wrapper (gradlew / gradlew.bat)
  app/build.gradle.kts                   app module (minSdk 21, applicationId com.arafat.vangtichai)
  app/src/main/AndroidManifest.xml
  app/src/main/java/com/arafat/vangtichai/
      MainActivity.kt                    keypad handling, screen updates, saving state on rotation
      ChangeCalculator.kt                greedy calculation + digit-append rules (pure Kotlin)
      KeypadButton.kt                    Button that never loses a quick repeated tap (see 7)
  app/src/main/res/
      layout/activity_main.xml           PORTRAIT layout (phone + tablet)
      layout-land/activity_main.xml      LANDSCAPE layout (phone + tablet)
      values/sizes.xml                   sizes for phone portrait
      values-land/sizes.xml              sizes for phone landscape
      values-sw600dp/sizes.xml           sizes for tablet portrait
      values-sw600dp-land/sizes.xml      sizes for tablet landscape
      values/dimens.xml                  corner radii, 48dp minimum key size (all configurations)
      values/strings.xml, colors.xml, themes.xml (palette, styles for keys and tiles)
      drawable/bg_*.xml, color/*.xml     key/tile/card backgrounds and their states
      mipmap-*/ic_launcher*.png,
      mipmap-anydpi-v26/ic_launcher*.xml launcher icons made from the logo (adaptive on 8.0+)
      drawable-nodpi/vangtichai_logo.png small copy of the logo for the header
  app/src/test/.../ChangeCalculatorTest.kt      JVM unit tests
  app/src/androidTest/.../MainActivityTest.kt   Espresso tests (run on emulator)


4. LAYOUTS FOR THE FOUR REQUIRED CONFIGURATIONS
-----------------------------------------------
Design: light background, navy amount card, white keys, gold accents taken from the
logo. Keys are flat rounded rectangles with a ripple when pressed, a blue ring when
focused with a keyboard/D-pad, and a grey disabled state. CLEAR is soft gold so it
stands apart from the digits. The note tiles are flat (no ripple) because they show
results and are not buttons.

Portrait  (layout/):      header (logo + name), amount card, 4 x 2 note tiles,
                          keypad 1 2 3 / 4 5 6 / 7 8 9 / 0 CLEAR filling the rest.
Landscape (layout-land/): left half: header, amount card, 4 x 2 note tiles;
                          right half: keypad 1 2 3 4 / 5 6 7 8 / 9 0 CLEAR.

Phone vs tablet: the same two layouts are used, and Android picks the matching
sizes.xml through resource qualifiers:

    Pixel XL  portrait  (411 x 731 dp)  -> layout/       + values/sizes.xml
    Pixel XL  landscape (731 x 411 dp)  -> layout-land/  + values-land/sizes.xml
    Nexus 10  portrait  (800 x 1280 dp) -> layout/       + values-sw600dp/sizes.xml
    Nexus 10  landscape (1280 x 800 dp) -> layout-land/  + values-sw600dp-land/sizes.xml

Keys use weights, so they grow to fill the space on phones; on tablets the content
width and keypad height are capped (content_max_width, keypad_max_height) and the
column is centred. Every key is at least 48 x 48 dp.

No dp/sp values are hard-coded in the layout files: every text size, margin,
padding and key size is a @dimen (the only literal is "0dp", which means "match
constraints" / "use the weight", not a size).

The app draws edge-to-edge on every Android version and pads the screen by the
status bar, navigation bar and display-cutout insets, so nothing sits under them.


5. KEEPING STATE ON ROTATION
----------------------------
Rotation destroys and recreates MainActivity, so Android can load the other layout.
- onSaveInstanceState() stores the amount text in the Bundle (key "amount").
- onCreate() reads it back from savedInstanceState and recalculates the table.
The orientation is NOT locked and android:configChanges is NOT used.


6. OPEN, BUILD AND RUN
----------------------
Android Studio:
  1. File > Open... and select the VangtiChai folder (the one containing settings.gradle.kts).
  2. Wait for Gradle sync to finish. Android Studio creates local.properties with your SDK path.
  3. Pick an emulator (e.g. Pixel XL or Nexus 10) and press Run.

Command line (macOS):
  cd VangtiChai
  ./gradlew assembleDebug                 (APK: app/build/outputs/apk/debug/app-debug.apk)
  ./gradlew testDebugUnitTest             (unit tests)
  ./gradlew connectedDebugAndroidTest     (Espresso tests; needs a running emulator)
  If the SDK is not found, set ANDROID_HOME (e.g. ~/Library/Android/sdk) or create local.properties.

Testing rotation: in the emulator, enter an amount, then use the rotate buttons in the
emulator toolbar (or Ctrl+Left / Ctrl+Right). Turn on auto-rotate in the quick settings
if the screen does not turn.


7. TESTING DONE
---------------
Bug fixed: keys sometimes ignored a tap
  On the emulator, 9 quick taps on the same key gave only 6-8 digits (e.g. 6-8-8-0
  gave "680"). Logging showed every finger-down and finger-up reached the key, but
  one click was missing: after a tap, Android's Button posts a delayed "unpress" for
  itself, and when the next tap on the same key came quickly while the main thread
  was busy, that old unpress ran between the new tap's down and up, so the Button
  treated the new tap as cancelled. KeypadButton ignores such a stale unpress while a
  finger is on the key (sliding off a key still cancels the tap). The regression test
  quickRepeatedTapIsNotLostWhenPreviousUnpressRunsLate fails with a plain Button
  ("5" instead of "55") and passes with KeypadButton.

Slow interface
  Each tap used to re-measure the whole screen (the amount view was wrap_content) and
  run a button elevation animation. Now every view that changes has a fixed size,
  only views whose value changed are updated, and keys are flat with a ripple.
  Same 19-tap session on the Pixel XL emulator (2 runs each):
      measure + layout:        old 55-63 ms   new 5-6 ms
      main-thread total:       old 98-100 ms  new 52-80 ms
  Most of the visible lag on this Mac came from the emulators themselves: they
  render on the CPU (SwiftShader), and with two emulators on 8 GB of RAM they
  slowed down to 0.6-0.9 s per frame and were eventually killed by macOS.

Automated (2026-10-13, Android 14 / API 34 emulators):
  - 13 JVM unit tests (ChangeCalculatorTest): 0, each single note, 68, 688, 6880,
    999999999, digit appending, leading zeros, 9-digit limit, parsing, restoring
    saved text (sanitize), total notes.                                    PASSED 13/13
  - 14 Espresso tests (MainActivityTest): empty start, 688, 6880, each of the 8
    notes alone, every digit key (also twice in a row), leading zeros, 9-digit limit
    with 999999999, CLEAR (empty, after typing, after a result), recreation,
    real rotation portrait -> landscape -> portrait, 48 dp key size, the lost-tap
    regression test, sliding off a key.
        Pixel XL (phone)  PASSED 14/14      Nexus 10 (tablet)  PASSED 14/14
  - Android Lint: 0 errors. Remaining warnings: OldTargetApi (not upgraded on
    purpose) and MonochromeLauncherIcon (would need a redrawn logo).

Manual checks over adb (real rotation with the emulator rotation setting):
  - Pixel XL portrait and landscape: 9 quick taps on every key 0-9 and 5 on CLEAR,
    all registered; 6880 -> 500x13 100x3 50x1 20x1 10x1 5x0 2x0 1x0, 19 notes;
    6880 kept through portrait -> landscape -> portrait; keys work in landscape.
  - Nexus 10 portrait and landscape: 6880 kept through rotation, CLEAR and typing
    in landscape, value kept after rotating back; screenshots checked.
  - Launcher icon checked on the Nexus 10 home screen (circle mask, logo not clipped).

Not tested:
  - Real (physical) devices.
  - Android 15+ (API 35/36), where edge-to-edge is enforced (the app now draws
    edge-to-edge on every version and pads by the insets), and Android 5.0-13:
    only API 34 emulator images are installed.


8. KNOWN LIMITATIONS
--------------------
- Amounts are limited to 9 digits.
- Text is English only (the ৳ sign is shown on the note tiles).
- The adaptive icon has no monochrome layer, so Android 13+ "themed icons" show the
  normal colour logo (a monochrome version would need a redrawn logo).
