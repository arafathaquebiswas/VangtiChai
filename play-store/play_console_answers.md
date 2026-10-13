# VangtiChai — answers for Play Console forms (draft)

Based on the release build checked on 13 Oct 2026: no INTERNET permission, no
dangerous permissions, no ads/analytics/crash SDKs, no native code, nothing written to storage.
Re-check these answers if you ever add a library or permission.

## App content → Privacy policy
URL of the hosted play-store/privacy_policy.html (must be public, not a PDF download).

## App content → Ads
"No, my app does not contain ads."

## App content → App access
"All functionality is available without special access" (no login).

## App content → Data safety
- Does your app collect or share any of the required user data types? **No**
- (The form then skips the data-type questions.)
- Is all user data encrypted in transit? Not applicable (no data leaves the device).
- Do you provide a way for users to request that their data is deleted? Not applicable.

Note: the manifest contains `com.arafat.vangtichai.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`.
It is added automatically by AndroidX Core, is signature-protected and private to the app;
it is not a user-facing permission and needs no declaration.

## App content → Content rating (IARC questionnaire)
- Category: "All other app types" (utility / productivity)
- Violence, sexuality, language, controlled substances, gambling: **No** to all
- Users can interact or exchange content: **No**
- Shares user location: **No**
- Allows digital purchases: **No**
- Expected rating: Everyone / PEGI 3 / IARC 3+

## App content → Target audience and content
- Target age groups: 13–15, 16–17, 18+ (choosing only these keeps the app out of the
  Families programme requirements; selecting under-13 is allowed but adds Families policy checks)
- Appeals to children? **No**

## App content → News app
No.

## App content → Government app / Financial features
- Government app: No
- Financial features declaration: "My app doesn't provide any financial features"
  (it is a calculator only: no banking, loans, payments, crypto or trading).

## App content → Health apps
No.
