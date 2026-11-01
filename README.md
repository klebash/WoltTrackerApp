# Wolt Partner Stats 🚴‍♂️📊

A modern, minimalist, 100% offline Android application designed for **Wolt couriers** in Denmark to track their daily and monthly shifts, gross earnings, tips, hours, and delivery efficiency in Danish Krone (`kr.`).

---

## ✨ Features

- **📱 Offline Room Database:** 100% local storage. Your data remains private, secure, and accessible without internet connection.
- **📸 Wolt Screenshot OCR Scanner:** Seamlessly import shifts directly from Wolt courier app screenshots using Google ML Kit on-device text recognition.
- **⚡ Quick Manual Shift Logging:** Log shifts in seconds — start/end time, number of orders, base pay, and tips (`kr.`).
- **💰 Earnings & Tips Breakdown:** Clear distinction between base courier fee and customer tips with real-time totals.
- **📈 Comprehensive Reports & Analytics:**
  - **Monthly View:** Filter and browse shift statistics by month with quick navigation (`‹` `›`).
  - **All-Time View:** Aggregate historical metrics across all logged shifts.
  - **Efficiency Metrics:** Income per hour (`kr./h`), income per order (`kr.`), average shift duration, and orders delivered.
- **🎨 Minimalist Dark Wolt Theme:** Sleek dark-mode interface with Wolt Cyan accents, custom full-bleed vector adaptive launcher icons, and minimalist outline line navigation.

---

## 🛠️ Tech Stack & Architecture

- **Language:** [Kotlin 2.0+](https://kotlinlang.org/)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3
- **Architecture:** MVVM + Clean Architecture / Repository pattern
- **Database:** [Room Database](https://developer.android.com/training/data-storage/room) (SQLite) with Coroutines Flow
- **On-Device OCR:** [Google ML Kit Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition/android)
- **Navigation:** [Jetpack Navigation 3](https://developer.android.com/guide/navigation)
- **Build System:** Gradle Version Catalogs (`libs.versions.toml`)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or newer
- JDK 17+
- Android SDK 35 (minSdk 24, compileSdk 35, targetSdk 35)

### Build & Install

Clone the repository and build the debug APK:

```bash
git clone https://github.com/klebash/Wolt-Tracker.git
cd Wolt-Tracker
./gradlew assembleDebug
```

The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

To install directly to a connected device:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
