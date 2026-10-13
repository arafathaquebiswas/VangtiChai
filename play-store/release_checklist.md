# VangtiChai — Google Play release checklist

Status as of 13 Oct 2026. [x] = done and verified, [ ] = still to do.

## A. Build and code (done on the Mac, no emulator)
- [x] Backup created: cselab/VangtiChai_backup_stage1_20261013-1915.tar.gz
- [x] applicationId `com.arafat.vangtichai` (permanent once published)
- [x] Git commit backup of the working app (3a25bd6, 337c1c8)
- [x] minSdk 21, targetSdk 36, compileSdk 36 — Play requires API 36 for new apps and
      updates since 31 Aug 2026 (checked on developer.android.com, 13 Oct 2026)
- [x] Only one exported activity (MainActivity, the launcher — must be exported)
- [x] No debug logging, StrictMode, cleartext traffic or dev-only config in app code
- [x] Launcher, round and adaptive icons all use the custom VangtiChai logo (no default
      Android icon); logo stays inside the visible circle used by round masks and the
      Android 12+ splash screen
- [x] versionCode 1, versionName "1.0" (increase versionCode for every upload)
- [x] Release build is not debuggable
- [x] No INTERNET or dangerous permissions; no ads/analytics SDKs
- [x] No native libraries (Play 16 KB page-size rule does not apply)
- [x] 14/14 unit tests pass
- [x] Lint (release): 0 errors; 10 warnings, none blocking (see report)
- [x] Release APK and Android App Bundle (.aab) build successfully (unsigned)
- [x] Keystore files excluded from git (.gitignore)

## B. Needs your approval (Stage 7)
- [ ] Emulator check of the release build (one emulator only)
- [ ] Phone screenshots (2–8) — from the emulator or your own phone
- [ ] Create upload keystore + signed bundle:
      Android Studio → Build → Generate Signed App Bundle or APK → Android App Bundle →
      Create new… → save the .jks OUTSIDE the project folder → Build variant: release
- [ ] Back up the .jks file and both passwords in two safe places
      (with Play App Signing, a lost upload key can be reset via Google support,
      but it takes time)

## C. Play Console (in your browser)
- [ ] Developer account (one-time US$25 fee, identity verification)
- [ ] Create app: name, default language English, App, Free
- [ ] Host privacy_policy.html publicly (e.g. GitHub Pages or Google Sites) and replace
      YOUR_SUPPORT_EMAIL in it first
- [ ] Main store listing: copy text from store_listing.md, upload store_icon_512.png,
      feature graphic (1024×500), screenshots
- [ ] App content forms: use play_console_answers.md
- [ ] Enrol in Play App Signing (default for new apps)
- [ ] New personal developer account: closed test with at least 12 testers opted in
      for 14 continuous days before you can apply for Production
- [ ] Production → Create release → upload app-release.aab → release notes → Review → Roll out
- [ ] After approval: install from Play on a real phone and check it opens and calculates
