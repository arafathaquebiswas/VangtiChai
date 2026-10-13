# Publishing VangtiChai from a personal Google Play developer account

Sources checked on 13 Oct 2026:
- Target API: https://developer.android.com/google/play/requirements/target-sdk
- Testing requirement: https://support.google.com/googleplay/android-developer/answer/14151465
Always re-check Play Console itself; it shows the exact tasks your account still has open.

## 1. Account verification (Play Console → Home / Settings → Account details)
Personal accounts must finish these before publishing; Play Console lists any that are open:
- **Identity verification**: your legal name and address, checked against an official ID.
  The name on the store page is your *developer name*; your legal address is not shown
  publicly for personal accounts without monetisation.
- **Contact email and phone verification** (codes sent to you).
- **Android device verification**: if asked, install the Play Console app on a real Android
  phone and sign in with the developer account.

## 2. Internal testing (optional, fast)
- Testing → Internal testing → Create release → upload the signed .aab.
- Up to 100 testers by email list; available within minutes; no full review.
- Use it to check the Play-delivered build installs and opens on your own phone.
- It does **not** count towards the 12-tester requirement below.

## 3. Closed testing (required for your account type)
Applies to personal accounts created after 13 November 2023:
- At least **12 testers** must be **opted in to a closed test** for at least
  **14 continuous days** before you can apply for production.
- A tester who opts out restarts the count for that person; keep a few extra (e.g. 15–20).
- Steps: Testing → Closed testing → Create track (or use "Alpha") → Testers: add a
  Google Group or email list → Create release → upload .aab → Review → Start rollout.
- Send testers the opt-in link from the track page; they must accept it and install from Play.
- Ask testers to really use the app and send feedback — Google asks about this later.
- The closed-test release goes through app review (usually hours to a few days).

## 4. Apply for production access
After 14 days with 12+ opted-in testers: Dashboard → **Apply for production**. You answer:
- About your closed test: how you found testers, how they used it, what feedback you got.
- About your app: target audience, the app's value, expected installs in year one.
- About production readiness: what you changed after testing and why it is ready.
Google usually replies within about 7 days.

## 5. First production release
1. Make sure every App content task is complete (privacy policy, ads, app access,
   data safety, content rating, target audience, financial features, etc.).
2. Production → Countries/regions → add Bangladesh (and any others).
3. Production → Create new release → upload the signed .aab (or promote the tested release).
4. Release name "1.0", paste the release notes from store_listing.md.
5. Next → fix any errors/warnings shown → Save → **Send for review** (Publishing overview).
6. After approval, choose managed publishing or let it go live automatically.

## 6. Every later update
- Increase `versionCode` in app/build.gradle.kts (1 → 2 → 3 …) and usually `versionName`.
- Sign with the **same upload key** and upload a new .aab.
- Keep targetSdk at the level Play requires (API 36 for new apps/updates since 31 Aug 2026).
