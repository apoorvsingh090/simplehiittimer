# Simple HIIT Timer

[![License: MIT](https://img.shields.io/github/license/apoorvsingh090/simplehiittimer)](LICENSE)

A minimal, offline-first Android HIIT interval timer. Set work and rest durations and a
round count, then follow a full-screen countdown that beeps at phase changes.

## Features

- Configurable work seconds, rest seconds and number of rounds.
- 5-second "GET READY" countdown, then automatic WORK → REST → next round.
- Full-screen interval display, tap to pause/resume, STOP to exit.
- Sound beeps at phase transitions (3-2-1 and on finish).
- Optional voice coach ("work" / "rest" / workout complete) via TextToSpeech.
- Timer pauses automatically when the app is backgrounded; progress survives rotation/recreation.

## Build

Standard Gradle Android project (Android SDK 34, Java 17, Kotlin, ViewBinding):

```bash
./gradlew assembleDebug       # app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest   # unit tests
```

## Contents

```text
app/src/main/java/com/example/hiittimer/
    MainActivity.kt     # configuration screen
    TimerActivity.kt    # countdown engine (pause/resume/lifecycle, TTS, ToneGenerator)
    TimerLogic.kt       # pure session state machine (unit-tested)
.github/workflows/      # CI: unit tests + assembleDebug on push
```

Requires Android 7.0+ (minSdk 21). MIT licensed.
