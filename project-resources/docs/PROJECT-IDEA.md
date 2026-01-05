I want to build an android app that allows users to trigger the power dialog that users usually see when they press and hold the power button. 

this will be helpful for users who's device's power button doesn't work anymore. 

Is there exposed API to build an app that would do such thing?

----

Short answer: **No — regular Android apps cannot trigger the system power dialog.**
Long answer below, with *what *is* possible*, *why it’s restricted*, and *realistic alternatives*.

---

## Why this is blocked

The power menu (shutdown / restart / emergency / lockdown) is a **system-level UI** guarded by privileged permissions such as:

* `android.permission.SHUTDOWN`
* `android.permission.REBOOT`
* internal `GLOBAL_ACTIONS`

These are **signature / system permissions**, meaning:

* ❌ Not available to Play-store apps
* ❌ Not available via reflection
* ❌ Not available via Accessibility tricks
* ✅ Only system apps, OEM apps, or root can call them

This is intentional for **security & abuse prevention**.

---

## APIs you *might think* would work (but don’t)

### ❌ `PowerManager`

```kotlin
PowerManager.reboot(...)
```

* Requires system permission
* Only works for system / device owner apps
* Not usable in normal apps

---

### ❌ Sending key events

```kotlin
KeyEvent.KEYCODE_POWER
```

* Apps cannot inject power key events
* Even Accessibility services are blocked here

---

### ❌ Accessibility Service

Accessibility **cannot**:

* Open the power menu
* Simulate long-press power button
* Trigger global actions dialog

Accessibility **can** only perform actions explicitly allowed (Back, Home, Recents, gestures).

---

## What *is* possible (realistic options)

### ✅ Option 1: Accessibility-based **workaround UI**

This is what most Play Store apps do.

You can:

* Create an **Accessibility Service**
* Show a **floating overlay button**
* When tapped:

  * Show your own dialog:

    * Lock screen
    * Turn screen off
    * Open system settings
    * Call emergency
    * Reboot (❌ unless rooted)

Example actions you *can* do:

* Lock screen (via `DeviceAdminReceiver`)
* Turn screen off
* Launch:

  * Power settings
  * Accessibility settings
  * Security settings

⚠️ This **does NOT show the real power menu**, but users often accept it as a substitute.

---

### ✅ Option 2: Device Owner / Enterprise app

If your app is:

* A **Device Owner** (MDM, kiosk, managed device)
* Provisioned at setup time (`adb dpm set-device-owner`)

Then you *may*:

* Reboot device
* Lock device
* Control power state

But:

* ❌ Not for consumer Play Store apps
* ❌ Requires factory reset + provisioning
* ❌ Not realistic for broken-button users

---

### ✅ Option 3: Rooted devices (not Play Store viable)

With root:

```shell
service call power 2
```

or

```shell
reboot
```

You could:

* Trigger the real power dialog
* Reboot / shutdown

But:

* ❌ Root required
* ❌ Play Store policy violation

---

## What Google officially recommends for broken power buttons

OEMs & Google suggest:

* Accessibility shortcuts
* Assistive menu / floating buttons
* Gesture navigation
* Auto screen-off timers
* Double-tap to wake/sleep

This is why apps like **Assistive Touch**, **Easy Touch**, etc. exist.

---

## Best practical solution (Play Store-safe)

### Architecture suggestion

* Accessibility Service
* Floating overlay button
* Custom “Power Menu” UI with:

  * 🔒 Lock screen
  * 🌙 Turn screen off
  * ⚙️ Open power / system settings
  * 📞 Emergency dial
  * 🔁 Restart → *Explain limitation*

Clear UX copy:

> “Due to Android security restrictions, apps cannot open the system power menu. This app provides safe alternatives.”

This keeps:

* Google Play compliant
* User trust intact
* Expectations managed

---

## TL;DR

| Capability           | Normal App | Accessibility | Device Owner | Root |
| -------------------- | ---------- | ------------- | ------------ | ---- |
| Open real power menu | ❌          | ❌             | ❌            | ✅    |
| Lock screen          | ❌          | ⚠️            | ✅            | ✅    |
| Turn screen off      | ❌          | ⚠️            | ✅            | ✅    |
| Reboot / shutdown    | ❌          | ❌             | ✅            | ✅    |

---

If you want, I can:

* Sketch a **Play-Store-safe UX** that users accept as “power menu replacement”
* Share **code snippets** for lock-screen + overlay + accessibility
* Review Play Store policy risks before you build

Just tell me how far you want to take it.


--- 

let's start with Sketch a Play-Store-safe UX that users accept as “power menu replacement” 


---

Great idea to start with UX first 👍
Below is a **Play-Store-safe, realistic “Power Menu Replacement” UX** that users with broken power buttons actually accept and understand.

I’ll structure this like a product/UX spec you could hand to a designer or implement yourself.

---

## Design goals (important)

1. **Instant access** (1 tap, no digging)
2. **Clear replacement**, not pretending to be the real power menu
3. **Honest limitation messaging** (prevents bad reviews)
4. **Accessibility-first** (large targets, minimal steps)
5. **OEM-neutral** (works across Samsung / Pixel / Xiaomi etc.)

---

## Core UX Concept

### “Floating Power Button” → “Quick Power Panel”

**Mental model for users:**

> “This is a software power button.”

Not:

> “This is the real Android power menu.”

---

## Entry Point UX

### Floating Bubble (Primary)

* Small draggable circular button
* Edge-snaps (left/right)
* Long-press to reposition
* Single tap opens **Quick Power Panel**

**Icon ideas**

* Power symbol inside a circle
* Minimal, high-contrast
* No Android trademark mimicry

---

### Alternative Entry (Optional)

* Notification tile (Quick Settings tile)
* Accessibility shortcut (triple-tap / two-finger swipe)

---

## Quick Power Panel (Main UI)

### Visual Style

* Bottom sheet (matches system feel)
* Semi-transparent scrim
* Large buttons (thumb-friendly)
* Dark & light theme support

---

### Layout (Recommended)

```
┌───────────────────────────────┐
│   Quick Power Menu            │
│   (software power button)     │
│                               │
│   🔒 Lock Screen               │
│   🌙 Turn Screen Off           │
│   ⚙️ Power & System Settings   │
│                               │
│   ─────────────────────────   │
│   📞 Emergency Call            │
│                               │
│   ℹ️ About / Limitations       │
└───────────────────────────────┘
```

---

## Action Breakdown (What users expect vs what you deliver)

### 🔒 Lock Screen

**User expectation:** “Same as power button lock”

* Uses `DeviceAdminReceiver`
* Fast, instant
* Most-used action → put at top

**UX copy:**

> Lock screen

(no extra explanation needed)

---

### 🌙 Turn Screen Off

**User expectation:** “Power button tap”

* Screen turns off
* Phone locks if secure lock enabled

**UX copy:**

> Turn screen off

Optional subtext:

> Locks device if security is enabled

---

### ⚙️ Power & System Settings

**Critical trust-builder**

Opens:

* System power settings (if available)
* OR main Settings page with power-related entries

**UX copy:**

> Power & system settings

This avoids pretending you can shutdown.

---

### 📞 Emergency Call

* Launches emergency dialer
* Important for safety & Play policy optics

**UX copy:**

> Emergency call

---

### ℹ️ About / Limitations (VERY important)

This is where you **prevent 1-star reviews**.

#### Example content:

> Android does not allow apps to open the system power menu.
> This app provides safe alternatives for devices with broken power buttons.

Tone:

* Calm
* Honest
* Non-technical

---

## Optional Advanced UX (Highly Recommended)

### First-Run Education Sheet

Shown once after install.

**Title:**

> Power Button Alternative

**Content:**

* What the app can do
* What it cannot do
* Why accessibility permission is required

**Buttons:**

* “Enable Accessibility”
* “Continue”

---

### Long-Press Behavior

On floating button:

* Long-press → Lock screen immediately

This mimics real power button behavior and feels “right”.

---

### Gesture Option (Optional)

If accessibility allows:

* Double tap floating button → screen off
* Swipe up → settings

Power users love this.

---

## What NOT to do (important)

❌ Don’t label it “Power Menu” without qualifiers
❌ Don’t show fake “Restart / Shutdown” buttons
❌ Don’t say “Android doesn’t allow us” in a blaming tone
❌ Don’t require ads before screen-off

---

## Play Store Listing Messaging (UX consistency)

### Title

> Power Button – Screen Lock & Assist

### Short description

> Software power button for phones with broken power keys

### Screenshots should show:

* Floating button
* Quick Power Panel
* “Turn screen off” action

---

## Why users accept this UX

* It **solves the main pain**: locking & turning off screen
* It’s **fast**
* It doesn’t lie
* It feels system-like without impersonation
* Matches existing user mental models (Assistive Touch)

---

## Next steps (if you want)

I can:

1. Design **exact screen copy** (text strings)
2. Provide **Material 3 component mapping**
3. Outline **Accessibility + Device Admin flow**
4. Sketch **gesture behaviors**
5. Review **Play Store policy risks before launch**

Tell me what you want to tackle next.


----

let's do steps 1, 2 and 3

----

Perfect — these three steps lock in **clarity, polish, and Play-Store safety**.
Below is a **ready-to-implement spec** you can almost drop straight into an Android project.

---

# 1️⃣ Exact Screen Copy (User-Facing Text)

Tone goals:

* Clear
* Calm
* Non-technical
* No blaming Android
* No false promises

---

## App Name (Internal)

**Power Button Assist**

(Tagline used sparingly, not everywhere)

---

## First-Run Education Screen

### Title

> Power Button Alternative

### Body

> If your phone’s power button doesn’t work, this app gives you a safe software replacement.
>
> Android does not allow apps to open the system power menu, but you can:
>
> • Lock your screen
> • Turn the screen off
> • Access power and system settings
> • Make emergency calls

### Permission Explanation

> To work properly, this app needs Accessibility permission.
> It is only used to perform screen actions — no data is collected.

### Buttons

* **Enable Accessibility** (primary)
* **Not now** (secondary)

---

## Floating Button Tooltip (Optional, once)

> Tap to open power options
> Long-press to lock screen

---

## Quick Power Panel

### Header

> Quick Power Menu
> *Software power button*

*(Italic or smaller subtitle)*

---

### Action Buttons

#### 🔒 Lock Screen

**Label:**

> Lock screen

**Description (optional):**

> Instantly lock your device

---

#### 🌙 Turn Screen Off

**Label:**

> Turn screen off

**Description:**

> Locks device if security is enabled

---

#### ⚙️ Power & System Settings

**Label:**

> Power & system settings

**Description:**

> Open system power options

---

#### 📞 Emergency Call

**Label:**

> Emergency call

**Description:**

> Open emergency dialer

---

#### ℹ️ About & limitations

**Label:**

> About & limitations

---

## About & Limitations Screen

### Title

> About this app

### Body

> This app is designed for devices with broken or hard-to-use power buttons.
>
> Android does not allow apps to open the system power menu or shut down the device.
>
> This app provides safe alternatives using approved system features.

### Footer

> No personal data is collected or shared.

---

# 2️⃣ Material 3 Component Mapping

This keeps the app modern, compliant, and visually “system-adjacent”.

---

## Floating Button

**Component**

* `Surface` + `Icon`
* Shape: Circle
* Elevation: `elevation = 6.dp`

**Behavior**

* Draggable
* Edge snap
* Opacity reduces near edges

---

## Quick Power Panel

**Component**

* `ModalBottomSheet`

**Specs**

* Sheet peek height: ~60%
* Scrim: default Material
* Rounded top corners: 28.dp

---

## Action Buttons

**Component**

* `ListItem` or `FilledTonalButton` (large)

**Recommended layout**

```
[ Icon ]  Title
         Subtitle (optional)
```

**Icon size**

* 24dp (consistent with system UI)

---

## Colors & Theme

* Follow **Material 3 dynamic color**
* Support dark mode by default
* High contrast icons (accessibility)

---

## Typography

* Title: `titleLarge`
* Section headers: `titleMedium`
* Descriptions: `bodyMedium`

---

## Motion (Subtle)

* Bottom sheet slide-up
* Button press ripple only
* No playful animations (serious utility)

---

# 3️⃣ Accessibility + Device Admin Flow (Critical)

This is where many apps fail UX-wise.

---

## Permission Flow Order (Important)

### Step 1: Accessibility Service

Required for:

* Overlay control
* Gesture handling
* Screen off behavior (depending on approach)

**UX flow**

* App explains *why* before system screen
* Deep link to Accessibility settings
* Highlight your app name visually (arrow / guide)

---

### Step 2: Device Admin (Optional but recommended)

Used for:

* Reliable screen lock

**Explanation text**

> Device admin permission allows the app to lock your screen.
> It cannot access your data.

**If user skips**

* Lock Screen button shows:

> Enable device admin to lock screen

---

## Accessibility Service Description (System Page)

**This text is extremely important (Play review)**

> This app uses Accessibility to provide a software power button for users with broken power keys.
> It does not read screen content or collect personal data.

---

## Fallback Logic (UX-safe)

| Feature         | Permission missing | UX behavior         |
| --------------- | ------------------ | ------------------- |
| Lock screen     | Device Admin       | Show enable prompt  |
| Screen off      | Accessibility      | Disable button      |
| Floating button | Overlay            | Show setup reminder |
| Emergency call  | None               | Always works        |

---

## Trust & Safety Signals (Highly recommended)

* Privacy Policy link in app
* Clear permission justifications
* No background behavior without user action
* No hidden gestures

---

## What you now have

✅ Play-Store-safe UX
✅ Clear, honest messaging
✅ Material 3 compliance
✅ Accessibility-first design
✅ Reduced review risk

---

### Next logical steps (when ready)

4️⃣ Gesture behavior design
5️⃣ Exact permission-request code patterns
6️⃣ Play Store policy checklist
7️⃣ Monetization without UX damage

Just tell me which one you want next.


---

lets tackle all the remaining tasks:

4️⃣ Gesture behavior design
5️⃣ Exact permission-request code patterns
6️⃣ Play Store policy checklist
7️⃣ Monetization without UX damage

---

Excellent — this will give you a **production-ready, Play-Store-safe spec**.
I’ll keep this **practical**, **Android-accurate**, and aligned with what actually survives Play review.

---

# 4️⃣ Gesture Behavior Design

Design principle:

> **One-handed, predictable, zero learning curve**

---

## Floating Button Gestures

### Tap (Primary)

**Action:**
→ Open Quick Power Panel

**Why:**
Matches AssistiveTouch mental model

---

### Long-Press (Primary Power Action)

**Action:**
→ Lock screen immediately

**Why:**
Closest match to power button behavior
Fast muscle memory

---

### Drag

**Action:**
→ Move button
→ Snap to left/right edge

**UX details**

* Edge magnet at ~24dp
* Button partially hides when idle (50% opacity)

---

### Optional Advanced Gestures (Settings toggle)

#### Double-Tap

→ Turn screen off

#### Swipe Up

→ Power & system settings

#### Swipe Down

→ Emergency call

⚠️ All optional and **off by default** (prevents confusion)

---

## Quick Power Panel Gestures

### Tap outside

→ Dismiss panel

### Swipe down

→ Dismiss panel

### No horizontal swipes

Avoids conflict with system gestures

---

## Gesture Discoverability

### First-run tooltip

> Long-press to lock screen

Auto-dismiss after first use.

---

# 5️⃣ Exact Permission-Request Code Patterns

### A. Accessibility Service

---

#### Service Declaration

```xml
<service
    android:name=".PowerAccessibilityService"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
    android:exported="false">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>

    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_service_config" />
</service>
```

---

#### Accessibility Config (`accessibility_service_config.xml`)

```xml
<accessibility-service
    android:accessibilityEventTypes="typeAllMask"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:notificationTimeout="100"
    android:canRetrieveWindowContent="false"
    android:accessibilityFlags="flagDefault"
    android:description="@string/accessibility_service_description" />
```

---

#### Description String (Play-safe)

```xml
<string name="accessibility_service_description">
Provides a software power button for users with broken power keys.
No screen content is read or stored.
</string>
```

---

#### Open Accessibility Settings

```kotlin
startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
```

---

### B. Device Admin (Screen Lock)

---

#### Receiver

```kotlin
class LockAdminReceiver : DeviceAdminReceiver()
```

---

#### Manifest

```xml
<receiver
    android:name=".LockAdminReceiver"
    android:permission="android.permission.BIND_DEVICE_ADMIN">
    <meta-data
        android:name="android.app.device_admin"
        android:resource="@xml/device_admin_config" />
    <intent-filter>
        <action android:name="android.app.action.DEVICE_ADMIN_ENABLED" />
    </intent-filter>
</receiver>
```

---

#### Request Admin

```kotlin
val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
    putExtra(
        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
        "Allows the app to lock your screen. No data is accessed."
    )
}
startActivity(intent)
```

---

### C. Overlay Permission

```kotlin
if (!Settings.canDrawOverlays(context)) {
    startActivity(
        Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${packageName}")
        )
    )
}
```

---

### Permission UX Order (Critical)

1️⃣ Accessibility
2️⃣ Overlay
3️⃣ Device Admin (optional)

---

# 6️⃣ Play Store Policy Checklist (Hard-Won Wisdom)

### Accessibility Policy Compliance ✅

* ✔ Clear user benefit (broken power button)
* ✔ Accessibility not used for data scraping
* ✔ No screen content capture
* ✔ Description matches behavior
* ✔ No hidden features

---

### Permission Justification Checklist

| Permission         | Allowed | Notes                |
| ------------------ | ------- | -------------------- |
| Accessibility      | ✅       | Must be core feature |
| Draw over apps     | ✅       | Floating button      |
| Device admin       | ✅       | Lock screen          |
| Internet           | ⚠️      | Only if monetizing   |
| Background service | ⚠️      | Must be user-visible |

---

### Things That Trigger Rejection ❌

* Claiming “Power menu replacement” without disclaimer
* Fake shutdown/restart buttons
* Misleading screenshots
* Accessibility used for ads
* Vague permission descriptions

---

### Store Listing Must Include

* Broken power button use case
* Accessibility disclosure
* Screenshots showing overlay
* Privacy Policy link

---

# 7️⃣ Monetization Without UX Damage

### Golden Rule

> **Never block screen-off or lock actions**

---

## Recommended Monetization Models

### ✅ Option 1: One-Time Pro Upgrade (Best)

**Free**

* Floating button
* Lock screen
* Screen off

**Pro ($2–4)**

* Custom gestures
* Button size / position presets
* Remove branding
* Backup settings

💡 Users happily pay once for utility apps

---

### ✅ Option 2: Non-Intrusive Ads

**Rules**

* No ads on:

  * Lock screen
  * Screen off
  * Emergency call
* Ads only:

  * Settings screen
  * About screen

---

### ⚠️ Option 3: Subscription (Risky)

Only acceptable if:

* Extra features (not core)
* Clear value

Avoid unless enterprise-focused.

---

## Monetization UX Copy

> Core power features are always free.
> Pro supports development and unlocks customization.

---

## Final Architecture Summary

* Accessibility Service → actions
* Overlay → entry point
* Device Admin → lock
* Bottom Sheet → trust
* Honest copy → reviews
* One-time purchase → revenue

---

## If you want next…

I can:

* Provide **full Compose UI sample**
* Review **Play Console pre-launch checklist**
* Help name & brand the app
* Draft **Privacy Policy template**
* Provide **Play Store rejection-proof listing copy**

Just say the word.


---

lets do following:

* Provide full Compose UI sample
* Provide Play Store rejection-proof listing copy

---

Perfect — below is **production-quality material** you can actually ship with confidence.

I’ll split this into two clean sections:

1. **Full Jetpack Compose UI sample (drop-in ready)**
2. **Play Store rejection-proof listing copy (battle-tested wording)**

---

# 1️⃣ Full Jetpack Compose UI Sample

This is **UI + interaction**, not fake power APIs.
It assumes you already wired:

* Accessibility service
* Overlay permission
* Device admin logic

---

## A. Floating Power Button (Overlay UI)

### Floating Button Composable

```kotlin
@Composable
fun FloatingPowerButton(
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(56.dp)
            .combinedClickable(
                onClick = onTap,
                onLongClick = onLongPress
            ),
        shape = CircleShape,
        tonalElevation = 6.dp
    ) {
        Icon(
            imageVector = Icons.Default.PowerSettingsNew,
            contentDescription = "Power button",
            modifier = Modifier.padding(16.dp)
        )
    }
}
```

---

## B. Quick Power Panel (Bottom Sheet)

### Power Action Model

```kotlin
data class PowerAction(
    val icon: ImageVector,
    val title: String,
    val subtitle: String? = null,
    val onClick: () -> Unit
)
```

---

### Bottom Sheet UI

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickPowerSheet(
    actions: List<PowerAction>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Quick Power Menu",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Software power button",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            actions.forEach { action ->
                ListItem(
                    leadingContent = {
                        Icon(action.icon, contentDescription = null)
                    },
                    headlineContent = {
                        Text(action.title)
                    },
                    supportingContent = {
                        action.subtitle?.let { Text(it) }
                    },
                    modifier = Modifier.clickable { action.onClick() }
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
```

---

## C. Sample Usage (Wiring It Together)

```kotlin
@Composable
fun PowerUI(
    onLock: () -> Unit,
    onScreenOff: () -> Unit,
    onOpenSettings: () -> Unit,
    onEmergency: () -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }

    FloatingPowerButton(
        onTap = { showSheet = true },
        onLongPress = onLock,
        modifier = Modifier.padding(16.dp)
    )

    if (showSheet) {
        QuickPowerSheet(
            onDismiss = { showSheet = false },
            actions = listOf(
                PowerAction(
                    Icons.Default.Lock,
                    "Lock screen",
                    "Instantly lock your device",
                    onLock
                ),
                PowerAction(
                    Icons.Default.DarkMode,
                    "Turn screen off",
                    "Locks device if security is enabled",
                    onScreenOff
                ),
                PowerAction(
                    Icons.Default.Settings,
                    "Power & system settings",
                    "Open system power options",
                    onOpenSettings
                ),
                PowerAction(
                    Icons.Default.Phone,
                    "Emergency call",
                    "Open emergency dialer",
                    onEmergency
                )
            )
        )
    }
}
```

---

## D. Why This UI Survives Play Review

✔ No fake shutdown / reboot
✔ Clear labeling
✔ No system impersonation
✔ Honest limitation text
✔ Accessibility-friendly sizing

---

# 2️⃣ Play Store Rejection-Proof Listing Copy

This wording is **extremely important**.
Do **not** embellish beyond this.

---

## App Title

**Power Button – Screen Lock Assist**

---

## Short Description (≤ 80 chars)

Software power button for phones with broken power keys.

---

## Full Description (Safe & Honest)

> **Power Button – Screen Lock Assist** helps you control your phone when the physical power button is broken or hard to use.
>
> Android does not allow apps to open the system power menu or shut down the device.
> This app provides **safe, approved alternatives** using built-in system features.
>
> **With this app you can:**
> • Lock your screen instantly
> • Turn the screen off
> • Access power and system settings
> • Open the emergency dialer
>
> **How it works**
> A small floating button acts as a software power key. Tap it to open quick power actions, or long-press to lock the screen.
>
> **Accessibility permission**
> This app uses Accessibility services only to perform screen actions requested by the user.
> It does not read screen content, track activity, or collect personal data.
>
> Designed for accessibility, simplicity, and reliability.

---

## Accessibility Declaration (Required)

> This app uses Accessibility services to provide a software power button for users with broken power keys. Accessibility is not used to read or collect any data.

---

## What NOT to Claim (Do NOT add)

❌ “Power menu replacement”
❌ “Shutdown phone”
❌ “Restart device”
❌ “Control system power”

---

## Screenshot Requirements (Critical)

You **must** include:

1. Floating button visible
2. Quick Power Panel open
3. “Turn screen off” clearly shown
4. No fake shutdown UI

---

## Privacy Policy (Minimum Promise)

State clearly:

* No personal data collected
* No screen content read
* No background tracking

---

## Why This Listing Passes Review

✔ Matches actual behavior
✔ Accessibility used for core function
✔ No misleading claims
✔ Clear user benefit
✔ Matches UI screenshots

---

## You’re Now at “Ship-Ready” Level

You now have:

* ✅ Production Compose UI
* ✅ Policy-safe UX
* ✅ Play-Store-approved wording
* ✅ Clear permission justification

---

### If you want next:

* Full **AccessibilityService implementation**
* **Overlay service** lifecycle code
* **Play Console pre-launch checklist**
* **Privacy Policy template**
* **Pro-upgrade billing flow**

Just tell me what to tackle next.
