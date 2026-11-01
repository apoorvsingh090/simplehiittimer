# Simple HIIT Timer

A minimal, offline-first Android interval timer. Set work and rest durations and a round count,
then follow a full-screen ring countdown that beeps at every phase change.

## Download

| File | Description |
| --- | --- |
| [`simplehiittimer.apk`](simplehiittimer.apk) | Installable build (95 KB) |
| [`simplehiittimer-source.zip`](simplehiittimer-source.zip) | Java sources, resources and build scripts |

**APK SHA-256**

```text
142bd0c1f149f5dec9697ab75eb29eeadffff4b8aeb29671336a66d42fb674f4
```

**Requirements:** Android 7.0 (API 24) or newer.

**Install:** transfer the APK to the device, enable *Install unknown apps* for your file manager
(Settings → Apps → Special access → Install unknown apps), then open the APK.

## Features

- Configurable work seconds, rest seconds and number of rounds.
- 5-second "GET READY" countdown, then automatic WORK → REST → next round.
- Full-screen ring progress (`RingView`), tap anywhere to pause/resume, tap to finish.
- Optional sound beeps at phase transitions.
- Workout guide screen with tips.

## Contents

```text
simplehiittimer.apk
simplehiittimer-source.zip
└── hiit/
    ├── AndroidManifest.xml
    ├── java/com/simplehiittimer/app/   # MainActivity, WorkoutActivity, GuideActivity, RingView
    └── res/                            # layouts, drawables, strings, launcher icons
└── scripts/
    ├── build_apk.sh                    # aapt2 -> ecj -> d8 -> zipalign -> apksigner
    └── gen_icons.py                    # regenerates the launcher icons
```

## App details

| | |
| --- | --- |
| Package | `com.simplehiittimer.app` |
| Version | 1.0 (versionCode 1) |
| minSdk / targetSdk | 24 / 34 |
| Language | Java |
| Internet | Used only by the workout guide, which loads from `simplehiittimer.com` |

## Build from source

The project deliberately builds without Gradle. You need:

- Android SDK **build-tools 34.0.0**
- `platforms/android-34/android.jar`
- A Java compiler (`ecj.jar` is used by the script)

```bash
unzip simplehiittimer-source.zip -d .
# edit the SDK / PROJ / OUT paths at the top of scripts/build_apk.sh
bash scripts/build_apk.sh
```

The script runs `aapt2 compile/link`, compiles the Java sources, dexes with `d8`, aligns and signs
the result with `apksigner`.

## Signing

The signing keystore (`scripts/hiit.keystore`) is **not** published. `build_apk.sh` generates a new
one on first run using the placeholder password `android`. Replace it with your own keystore and a
strong password before distributing builds, and never commit it.

## License

No license file is included; the source is published as-is, which means all rights are reserved by
default. Open an issue if you would like to discuss reuse.
