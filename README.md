# 🎬 VidoPlay

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.7.0-green.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Media3](https://img.shields.io/badge/Media3-1.4.0-orange.svg?logo=android)](https://developer.android.com/media/media3)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

**VidoPlay** is a modern, feature-rich video player for Android built with Jetpack Compose and Media3 ExoPlayer. It supports local video playback, network streaming, advanced audio/video controls, and a beautiful glassmorphic UI.

<p align="center">
  <img src="screenshots/player_portrait.png" width="30%" alt="Player Portrait" />
  <img src="screenshots/player_landscape.png" width="30%" alt="Player Landscape" />
  <img src="screenshots/stream_url.png" width="30%" alt="Stream URL Screen" />
</p>

---

## ✨ Features

### 🎥 Video Playback
- Play local videos from device storage
- Stream videos via HTTP/HTTPS/RTMP/RTSP URLs
- Picture-in-Picture (PiP) mode
- Resume playback from where you left off
- Background audio playback (Audio-only mode)
- Adjustable playback speed (0.25x – 3.0x)

### 🎛️ Audio & Subtitle Controls
- Built-in 10‑band equalizer with presets (Normal, Rock, Pop, Jazz, Custom)
- Audio track selection (when multiple tracks available)
- Subtitle support with track selection and external subtitle loading

### 🎨 Video Enhancements
- Brightness, Contrast, Saturation, Sharpness, Hue adjustments
- Video filters (Grayscale, Sepia, Negative, Blur, Sharpen, Edge, Emboss)
- Aspect ratio control (Fit, Fill, Stretch, Zoom, 16:9, 4:3, 21:9, Original)
- Rotation and flip (horizontal/vertical)
- Zoom presets and pinch-to-zoom

### 🧩 Advanced Features
- Bookmark important moments
- Sleep timer
- Gesture controls: double‑tap to seek ±10s, long‑press for temporary 2x speed
- Drag left/right edge for volume/brightness control
- Screen lock to prevent accidental touches
- Screenshot capture (saved to gallery)

### 🎯 User Interface
- Fully built with Jetpack Compose
- Glassmorphic design with dynamic theming (light/dark mode)
- Smooth animations and transitions
- Bottom navigation with File, Search, Playlists, and Settings tabs

---

## 🛠️ Tech Stack

| Category            | Technology                                 |
|---------------------|---------------------------------------------|
| UI Toolkit          | Jetpack Compose                             |
| Media Player        | Media3 ExoPlayer                            |
| Dependency Injection| Koin                                        |
| Navigation          | Custom back‑stack + Compose Navigation      |
| Image Loading       | Coil3                                       |
| Architecture        | MVVM + Use Cases                            |
| Coroutines          | Kotlin Coroutines & Flow                    |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2.1+
- Android SDK 24+
- Gradle 8.0+


