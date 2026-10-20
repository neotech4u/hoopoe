<div align="center">
  <img width="200" height="200" alt="Hoopoe Launcher Logo" src="./hoopoe_logo.png" />
  <h1>Hoopoe Launcher</h1>
  <p><strong>A personalized, lightweight, offline-first Android launcher engineered for aftermarket car head units.</strong></p>

  [![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
  [![Latest Release](https://img.shields.io/github/v/release/neotech4u/hoopoe?color=blue&label=Release)](https://github.com/neotech4u/hoopoe/releases/latest)
  [![Download APK](https://img.shields.io/badge/Download-APK-success?logo=android)](https://github.com/neotech4u/hoopoe/releases/download/v1.0.0/hoopoe-launcher-v1.0.0.apk)
  [![Platform: Android](https://img.shields.io/badge/Platform-Android%2010%2B%20%28API%2029%2B%29-green.svg)](https://developer.android.com)
  [![UI: Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-purple.svg)](https://developer.android.com/jetpack/compose)
  [![Optimized for 2GB RAM](https://img.shields.io/badge/RAM-2GB%20Optimized-orange.svg)](#-optimizations-for-2-gb-ram--aftermarket-socs)
</div>

---

## 📖 Table of Contents
- [Overview & Philosophy](#-overview--philosophy)
- [Latest Release Highlights](#-latest-release-highlights)
- [Core Features](#-core-features)
- [Optimizations for 2 GB RAM & Aftermarket SoCs](#-optimizations-for-2-gb-ram--aftermarket-socs)
- [Hardware & Compatibility Profile](#-hardware--compatibility-profile)
- [Installation Guide](#-installation-guide)
- [Building from Source](#-building-from-source)
- [License & Attribution](#-license--attribution)

---

## 🧠 Overview & Philosophy

Most modern aftermarket head units are running underpowered, resource-constrained automotive chips (e.g. Allwinner, Rockchip, MediaTek) with 1–2 GB RAM. Furthermore, most drivers rely on wireless CarPlay or Android Auto, meaning the underlying Android head unit remains **offline** most of the time.

**Hoopoe Launcher** is engineered around this reality:
1. **OEM+ Automotive Aesthetics:** Clean, distraction-free dashboard typography and dark theme by default, eliminating cluttered launcher interfaces.
2. **Offline-First Resilience:** Fully functional without an active Wi-Fi connection. GPS gauges, trip meters, clocks, and widgets operate completely offline without blank screens or network hangs.
3. **Hardware Preservation:** Keeps factory applications accessible (Radio tuner, Bluetooth phone, reversing camera, factory settings) without requiring root access or MCU tampering.
4. **Lean Memory Footprint:** Tuned to run smoothly within 2 GB RAM without aggressive background churn or memory leaks.

---

## 🚀 Latest Release Highlights

### 🦅 Official Hoopoe Rebranding
- **Application ID & Namespace:** Refactored to `com.hoopoe.launcher`.
- **Branded App Icons:** Clean multi-density mipmaps (`mdpi` through `xxxhdpi`) in square and rounded formats.
- **Redesigned Onboarding:** Modern, high-resolution first-run onboarding screen.

### 🎬 Startup Logon Video Animation
- **Hardware-Accelerated Boot Animation:** Integrated custom boot animation (`hoopoe_logon.mp4`) running via native hardware-accelerated video playback.
- **Driver Distraction Safeguard:** Smart session tracking ensures the boot video runs **only on cold boot**. Returning to the Home screen while driving or switching apps bypasses the video instantly.
- **Tap-to-Skip & Error Resilient:** Instant tap-to-skip support with fallback protection if the media engine encounters hardware decoder delays.
- **Configurable in Settings:** Toggle the animation on/off at will, or use the **Preview Logon Animation** button in Settings to inspect playback.

### 🚗 Ergonomics & Automotive Dashboard Customization
- **Right-Hand Drive (RHD) Ready:** Default sidebar positioned on the **RIGHT** for intuitive driver reach in RHD markets (India, UK, Japan, Australia, etc.), with full toggle support for Left and Bottom configurations.
- **Vehicle Customization:** Default car title preset to `DZIRE` with full custom name configuration.
- **Expanded Quick Navigation:** Added dedicated **Navi** shortcut alongside Phone, Music, and Settings.
- **Intelligent Fallback Role Dispatcher:** Seamlessly detects and routes app launches for Navigation, Music, Bluetooth Phone, Reversing Camera, Radio, and Android System Settings across diverse OEM ROMs.

---

## ✨ Core Features

### 🧩 Modular Widget Grid
The dashboard features a drag-and-drop, resize-capable grid. Reposition, scale, and stack panes freely to fit your head unit's aspect ratio.

### 🎛️ Instrument Panel Widgets
- **Smart Music Player:** Pulls track metadata, album art, and playback controls from local players and streaming services (Spotify, YouTube Music, etc.).
- **Digital Speedometer:** GPS-based speed readout with configurable smoothing.
- **Trip Meter & 0–100 km/h Timer:** Rolling odometer and elapsed time tracking with an integrated acceleration timer.
- **Digital Clock & Date:** Large touch-friendly clock widget with battery-friendly minute-boundary updating.
- **Head Unit Vitals:** Real-time CPU usage, memory pressure, and temperature diagnostics.
- **Dynamic Weather:** Automatically checks network status—displays weather when connected, hides cleanly when offline.
- **Altimeter & GPS Compass:** Live altitude and directional heading derived directly from offline satellite NMEA data.

### 🗂️ App Drawer & Factory Integration
- Discovers all installed apps, including OEM vehicle apps (CarPlay/Android Auto receivers, Radio, Reversing Camera, MCU settings) often hidden by stock Android launchers.
- Searchable, touch-friendly grid optimized for landscape screens.

### 🌗 Smart Day/Night Theme Engine
- **Dark Mode by Default:** Optimized for evening driving and OLED/IPS car panels.
- **Sunset Mode:** Switches between Day and Night palettes automatically based on local offline sunset/sunrise astronomical calculations.
- **System Sync:** Follows vehicle headlight triggers when supported by the head unit's illumination wire.

---

## ⚡ Optimizations for 2 GB RAM & Aftermarket SoCs

To ensure peak responsiveness on budget quad-core SoCs:
* **Minute-Boundary Clock Updates:** The digital clock calculates delay until the next minute boundary rather than ticking every second, eliminating 59 unnecessary Jetpack Compose recompositions and CPU wakeups every minute.
* **Bitmap Clamping:** Media album art received from `MediaSession` notifications is clamped to a maximum resolution of 512×512, avoiding sudden OOM crashes during heavy music playback.
* **Safe Storage Access Framework (SAF):** Wallpaper selection handles non-standard AOSP document pickers gracefully without crashing on stripped-down car ROMs.
* **Low Background Churn:** Zero background telemetry, analytics, or unnecessary background polling services.

---

## 📟 Hardware & Compatibility Profile

| Parameter | Specifications / Validated Target |
| :--- | :--- |
| **Primary Target Vehicle** | 2020 Maruti Suzuki Dzire |
| **SoC / Board** | Allwinner `sun50iw10p1` (Quad-core ARM Cortex-A53 up to 1.8 GHz) |
| **GPU** | PowerVR GE8300 |
| **RAM** | 2 GB |
| **Storage** | 32 GB Internal Storage |
| **Android Version** | Android 10 (API level 29) & upwards |
| **Display** | Landscape Touchscreen (720p / 1080p compatible) |
| **Root Required?** | **No** (installs cleanly as a standard non-root Android application) |

---

## 📲 Installation Guide

### Method 1: USB Flash Drive (Recommended)
1. Download the release APK directly from GitHub:
   - **[Download hoopoe-launcher-v1.0.0.apk](https://github.com/neotech4u/hoopoe/releases/download/v1.0.0/hoopoe-launcher-v1.0.0.apk)**
2. Copy the APK onto a USB flash drive (FAT32 format).
3. Plug the drive into your car's USB port.
4. Open the head unit's **File Manager** or **ApkInstaller** app.
5. Select `hoopoe-launcher-debug.apk` and tap **Install**.
6. Press the **Home** button on your unit, select **Hoopoe Launcher**, and select **"Just Once"** to verify stability before setting as default.

### Method 2: ADB (Android Debug Bridge)
If your head unit has USB debugging or Wi-Fi debugging enabled:
```bash
adb install -r hoopoe-launcher-debug.apk
```

---

## 🛠️ Building from Source

### Prerequisites
- **JDK:** OpenJDK 17
- **Android SDK:** Platform 34, Build-Tools 34.0.0
- **Gradle:** 9.4.1 (configured via wrapper)

### Build Command
Run the standard Gradle wrapper command:
```bash
# Windows
.\gradlew.bat assembleDebug

# Linux / macOS
./gradlew assembleDebug
```

The compiled APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License & Attribution

This project is licensed under the **[MIT License](LICENSE)**.

### Attribution
Hoopoe Launcher builds upon the architecture and open-source foundations of [openlauncher](https://github.com/dw2lam/openlauncher) by **dw2lam**, customized and maintained by **Mohamed Usman** ([@neotech4u](https://github.com/neotech4u)) for aftermarket automotive head units.
