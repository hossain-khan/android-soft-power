# Power Button Assist - Implementation Plan

This document outlines the complete implementation plan for building the Power Button Assist app from start to Play Store release.

---

## 📋 Epic Overview

**Goal:** Build and ship a Play Store-compliant software power button replacement app.

**Tracking Issue:** [#21 - EPIC: Power Button Assist App](https://github.com/hossain-khan/android-soft-power/issues/21)

**Estimated Timeline:** ~2 weeks

---

## 🏗️ Implementation Phases

### Phase 1: Foundation & Infrastructure
> Establish the groundwork for permissions and configuration

| Issue | Task | Status | Dependencies |
|-------|------|--------|--------------|
| [#6](https://github.com/hossain-khan/android-soft-power/issues/6) | Setup XML configurations for Accessibility Service and Device Admin | ⬜ Todo | None |
| [#7](https://github.com/hossain-khan/android-soft-power/issues/7) | Create PermissionRepository for tracking permission states | ⬜ Todo | #6 |

**Deliverables:**
- `res/xml/accessibility_service_config.xml`
- `res/xml/device_admin_config.xml`
- `PermissionRepository` interface and implementation
- Permission state model (`PermissionState`)

---

### Phase 2: Core Services
> Build the Android services that power the app's functionality

| Issue | Task | Status | Dependencies |
|-------|------|--------|--------------|
| [#8](https://github.com/hossain-khan/android-soft-power/issues/8) | Create PowerAccessibilityService skeleton | ⬜ Todo | #6 |
| [#9](https://github.com/hossain-khan/android-soft-power/issues/9) | Create LockAdminReceiver for Device Admin lock screen | ⬜ Todo | #6 |
| [#12](https://github.com/hossain-khan/android-soft-power/issues/12) | Create FloatingButtonService with draggable overlay | ⬜ Todo | #7 |

**Deliverables:**
- `PowerAccessibilityService` - handles lock screen and screen-off
- `LockAdminReceiver` - device admin for reliable locking
- `FloatingButtonService` - foreground service with overlay
- `FloatingPowerButton` composable
- Dragging and edge-snap behavior

---

### Phase 3: UI Screens (Circuit)
> Build all user-facing screens using Circuit UDF pattern

| Issue | Task | Status | Dependencies |
|-------|------|--------|--------------|
| [#10](https://github.com/hossain-khan/android-soft-power/issues/10) | Create Onboarding Screen with permission setup wizard | ⬜ Todo | #7 |
| [#11](https://github.com/hossain-khan/android-soft-power/issues/11) | Create Home Screen with status dashboard | ⬜ Todo | #7, #10 |
| [#13](https://github.com/hossain-khan/android-soft-power/issues/13) | Create Quick Power Panel bottom sheet | ⬜ Todo | #8, #9 |
| [#14](https://github.com/hossain-khan/android-soft-power/issues/14) | Create Settings Screen with preferences | ⬜ Todo | #7 |
| [#15](https://github.com/hossain-khan/android-soft-power/issues/15) | Create About & Limitations Screen | ⬜ Todo | None |

**Deliverables:**
- `OnboardingScreen` - step-by-step permission wizard
- `HomeScreen` - main dashboard with status
- `PowerPanelScreen` - bottom sheet with power actions
- `SettingsScreen` - app preferences
- `AboutScreen` - limitations disclosure

**Screen Flow:**
```
App Launch
    │
    ├── Not Configured → OnboardingScreen
    │                         │
    │                         └── Complete → HomeScreen
    │
    └── Configured → HomeScreen
                         │
                         ├── Floating Button Tap → PowerPanelScreen
                         ├── Settings → SettingsScreen → AboutScreen
                         └── Fix Permissions → OnboardingScreen
```

---

### Phase 4: Integration & Navigation
> Wire everything together

| Issue | Task | Status | Dependencies |
|-------|------|--------|--------------|
| [#16](https://github.com/hossain-khan/android-soft-power/issues/16) | Setup Circuit navigation and update MainActivity | ⬜ Todo | #10, #11, #13, #14, #15 |
| [#17](https://github.com/hossain-khan/android-soft-power/issues/17) | Integrate floating button with power actions | ⬜ Todo | #8, #9, #12, #13 |

**Deliverables:**
- Complete navigation graph
- `PowerActionExecutor` - unified action handling
- Service-to-UI communication
- Haptic feedback integration

---

### Phase 5: Quality & Testing
> Ensure reliability and catch regressions

| Issue | Task | Status | Dependencies |
|-------|------|--------|--------------|
| [#18](https://github.com/hossain-khan/android-soft-power/issues/18) | Add unit tests and UI tests | ⬜ Todo | All previous |

**Deliverables:**
- Unit tests for `PermissionRepository`
- Presenter tests for all screens
- `PowerActionExecutor` tests
- Circuit test integration

---

### Phase 6: Release Preparation
> Prepare for Play Store submission

| Issue | Task | Status | Dependencies |
|-------|------|--------|--------------|
| [#19](https://github.com/hossain-khan/android-soft-power/issues/19) | Prepare Play Store listing assets and content | ⬜ Todo | #18 |
| [#20](https://github.com/hossain-khan/android-soft-power/issues/20) | Prepare release build and signing | ⬜ Todo | #19 |

**Deliverables:**
- App icon (adaptive)
- Feature graphic (1024x500)
- Screenshots (6+)
- Store listing copy
- Privacy policy (hosted)
- Accessibility declaration
- Signed release APK/AAB

---

## 🔗 Dependency Graph

```
#6 (XML Configs)
 │
 ├──► #7 (PermissionRepository)
 │     │
 │     ├──► #10 (Onboarding Screen)
 │     │     │
 │     │     └──► #11 (Home Screen)
 │     │
 │     ├──► #14 (Settings Screen)
 │     │
 │     └──► #12 (FloatingButtonService)
 │           │
 │           └──► #17 (Integration)
 │
 ├──► #8 (AccessibilityService)
 │     │
 │     └──► #13 (Power Panel)
 │           │
 │           └──► #17 (Integration)
 │
 └──► #9 (DeviceAdmin)
       │
       └──► #13 (Power Panel)

#15 (About Screen) ──► #14 (Settings)

#10, #11, #13, #14, #15 ──► #16 (Navigation)

All ──► #18 (Testing) ──► #19 (Assets) ──► #20 (Release)
```

---

## ⏱️ Estimated Timeline

| Phase | Tasks | Duration |
|-------|-------|----------|
| Phase 1: Foundation | #6, #7 | 1-2 days |
| Phase 2: Core Services | #8, #9, #12 | 2-3 days |
| Phase 3: UI Screens | #10, #11, #13, #14, #15 | 3-4 days |
| Phase 4: Integration | #16, #17 | 1-2 days |
| Phase 5: Testing | #18 | 1-2 days |
| Phase 6: Release | #19, #20 | 1-2 days |
| **Total** | **15 issues** | **~10-15 days** |

---

## ✅ Definition of Done

### Per-Issue Criteria
- [ ] Code implemented and compiles
- [ ] Follows project code style (run `./gradlew formatKotlin`)
- [ ] Unit tests added (where applicable)
- [ ] Manual testing completed
- [ ] PR reviewed and merged

### Project Completion Criteria
- [ ] All 15 issues completed
- [ ] App builds without errors (`./gradlew assembleDebug`)
- [ ] All tests passing (`./gradlew test`)
- [ ] Tested on physical devices (multiple manufacturers)
- [ ] Tested on multiple Android versions (9, 10, 11, 12, 13, 14)
- [ ] Play Store listing ready
- [ ] Privacy policy hosted online
- [ ] Accessibility declaration prepared
- [ ] Release APK/AAB generated and signed

---

## 🚦 Risk Mitigation

| Risk | Impact | Mitigation |
|------|--------|------------|
| Play Store rejection for accessibility misuse | High | Clear documentation, honest limitations |
| Service killed by battery optimization | Medium | Foreground service, user education |
| Overlay not working on some OEMs | Medium | Test on multiple manufacturers |
| Lock screen not working without device admin | Low | Fallback to accessibility action (API 28+) |

---

## 📝 Development Guidelines

### Before Starting Any Issue
1. Read the issue description completely
2. Check dependencies are completed
3. Create feature branch from `main`

### During Development
1. Follow Circuit UDF pattern for screens
2. Use Material 3 components only
3. Never hardcode colors - use `MaterialTheme.colorScheme`
4. Add Metro DI annotations

### Before PR
1. Run `./gradlew formatKotlin`
2. Run `./gradlew assembleDebug`
3. Test on physical device if possible
4. Update issue status

---

## 🔗 Related Documents

- [PROJECT-OVERVIEW.md](PROJECT-OVERVIEW.md) - What this app does
- [PROJECT-IDEA.md](PROJECT-IDEA.md) - Original concept and UX specifications
- [GitHub Epic #21](https://github.com/hossain-khan/android-soft-power/issues/21) - Master tracking issue
