# Power Button Assist

A Play Store-safe Android app that provides a software power button replacement for users with broken or hard-to-use physical power buttons.

---

## 🎯 Problem Statement

Many Android users have devices with malfunctioning power buttons due to:
- Physical wear and tear
- Water damage
- Manufacturing defects
- Accessibility needs

These users struggle to:
- Lock their screen
- Turn off the display
- Access power-related settings

## 💡 Solution

**Power Button Assist** provides a floating software button that gives users quick access to power-related actions without needing the physical power button.

---

## ✨ Features

### Core Features
| Feature | Description |
|---------|-------------|
| 🔘 **Floating Power Button** | Draggable overlay button accessible from any screen |
| 🔒 **Lock Screen** | Instantly lock the device |
| 🌙 **Turn Screen Off** | Turn off display (locks if security enabled) |
| ⚙️ **Power Settings** | Quick access to system power settings |
| 📞 **Emergency Call** | One-tap access to emergency dialer |

### User Experience
- **One-tap access** - No digging through menus
- **Draggable button** - Position anywhere on screen
- **Edge-snap** - Button snaps to screen edges
- **Long-press shortcut** - Long-press to instantly lock
- **Customizable** - Button size and actions configurable

---

## ⚠️ Important Limitations

> **Android Security Restriction:** Apps cannot open the real system power menu, shutdown, or restart the device. This is an Android platform limitation for security reasons, not a limitation of this app.

### What This App **CAN** Do
- ✅ Lock the screen
- ✅ Turn off the display
- ✅ Open system settings
- ✅ Open emergency dialer
- ✅ Provide floating button overlay

### What This App **CANNOT** Do
- ❌ Open the real Android power menu
- ❌ Shut down the device
- ❌ Restart the device
- ❌ Access hardware power controls

---

## 🔐 Permissions Required

| Permission | Purpose | Required? |
|------------|---------|-----------|
| **Accessibility Service** | Perform screen lock and screen-off actions | ✅ Required |
| **Draw Over Other Apps** | Display floating button overlay | ✅ Required |
| **Device Admin** | Reliable screen lock on older devices | ⚡ Optional |

### Privacy Commitment
- 🔒 No personal data collected
- 🔒 No screen content read
- 🔒 No usage tracking
- 🔒 Accessibility used only for power actions

---

## 🏗️ Technical Architecture

### Stack
- **UI:** Jetpack Compose with Material 3 / Material You
- **Architecture:** Circuit UDF (Unidirectional Data Flow)
- **DI:** Metro (Dependency Injection)
- **Services:** AccessibilityService, Foreground Service

### Key Components
```
app/
├── circuit/           # UI screens using Circuit pattern
│   ├── home/          # Main dashboard
│   ├── onboarding/    # Permission setup wizard
│   ├── powerpanel/    # Quick power actions sheet
│   └── settings/      # App configuration
├── service/
│   ├── PowerAccessibilityService   # Core power actions
│   └── FloatingButtonService       # Overlay button
├── admin/
│   └── LockAdminReceiver           # Device admin for lock
└── data/
    └── PermissionRepository        # Permission state management
```

---

## 📱 Target Audience

- Users with broken power buttons
- Users with physical disabilities affecting button use
- Users who prefer on-screen controls
- Elderly users who find physical buttons difficult

---

## 🎨 Design Philosophy

### Material You
- Dynamic color theming
- Follows system light/dark mode
- Accessible touch targets (48dp minimum)

### Honest UX
- Clear about capabilities and limitations
- No fake shutdown/restart buttons
- Transparent permission explanations

### Accessibility First
- Large, easy-to-tap buttons
- High contrast icons
- Screen reader compatible

---

## 📊 Platform Requirements

| Requirement | Value |
|-------------|-------|
| Minimum Android | 9.0 (API 28) |
| Target Android | Latest stable |
| Form Factors | Phone, Tablet |

> **Note:** API 28+ required for `GLOBAL_ACTION_LOCK_SCREEN` accessibility action.

---

## 🔗 Related Resources

- [PROJECT-IDEA.md](PROJECT-IDEA.md) - Original concept and detailed UX specifications
- [PROJECT-PLAN.md](PROJECT-PLAN.md) - Implementation plan and task tracking
- [GitHub Issues](https://github.com/hossain-khan/android-soft-power/issues) - Detailed task breakdown

---

## 📄 License

MIT

## 👤 Author

- Hossain Khan
