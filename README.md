# Power Button Assist

> ⚠️ **This app is actively under development and not ready for use yet.**  
> See the [Project Plan](project-resources/docs/PROJECT-PLAN.md) for implementation progress.

---

A Play Store-safe Android app that provides a **software power button replacement** for users with broken or hard-to-use physical power buttons.

## 🎯 What It Does

- 🔘 **Floating Power Button** - Draggable overlay button accessible from any screen
- 🔒 **Lock Screen** - Instantly lock your device
- 🌙 **Turn Screen Off** - Turn off display with one tap
- ⚙️ **Power Settings** - Quick access to system power settings
- 📞 **Emergency Call** - One-tap access to emergency dialer

## ⚠️ Important Limitation

Android does not allow apps to open the real power menu, shutdown, or restart the device. This is an Android platform security restriction. This app provides **approved alternatives** for the most common power button uses.

## 🛠️ Tech Stack

- ⚡️ [Circuit](https://github.com/slackhq/circuit) - UI architecture (UDF pattern)
- 🏗️ [Metro](https://zacsweers.github.io/metro/) - Dependency Injection
- 🎨 [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- 📱 [Material 3](https://m3.material.io/) - Material You design system

## 📱 Requirements

- Android 9.0+ (API 28)
- Accessibility Service permission
- Draw Over Other Apps permission
- Device Admin (optional, for reliable lock)

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [Project Overview](project-resources/docs/PROJECT-OVERVIEW.md) | What this app does |
| [Project Plan](project-resources/docs/PROJECT-PLAN.md) | Implementation roadmap |
| [Project Idea](project-resources/docs/PROJECT-IDEA.md) | Original concept & UX specs |
| [Play Store Listing](project-resources/google-play/GOOGLE-PLAY-LISTING.md) | Store submission details |

## 🚀 Getting Started

1. Clone the repository
2. Open in Android Studio
3. Build and run on device (API 28+)

```bash
./gradlew assembleDebug
```

## 🏗️ Building

```bash
# Format code
./gradlew formatKotlin

# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Hossain Khan**

