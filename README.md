> [!NOTE]
> **This is an unofficial fork** of [jellyfin/jellyfin-androidtv](https://github.com/jellyfin/jellyfin-androidtv).
> It is not affiliated with or supported by the Jellyfin project. For the official app, use the store links further down.

## What is different in this fork: list view for libraries

The official Android TV app only shows libraries as a wall of posters, thumbnails or banners. That makes it hard to scroll
through and find things in libraries with many files that have no proper artwork, such as music videos, concert
recordings or home videos.

This fork adds a **List** option next to Poster, Thumbnail and Banner:

- One full-width row per item with a small 16:9 thumbnail, the name, the runtime, a watched mark and a progress bar.
- Folders show how many items are still unwatched.
- The existing **Image size** setting controls the row height, from compact to large.
- Sub folders opened from a list open as a list as well, unless they have their own setting.
- Sorting by name and the jump-to-letter button work as usual.

Turn it on per library: open the library, press the **settings** (gear) button in the toolbar and choose
**Image type → List**.

### Installing
There are no store releases of this fork. Every push to `master` is built by GitHub Actions (**Actions → App / Build**,
artifact `build-artifacts`, download requires a GitHub login). Unzip it and sideload the APK, for example with a file
manager such as X-plore, with *Downloader*, or with `adb install`.

- The build is a debug build named **Jellyfin Debug** (`org.jellyfin.androidtv.debug`), so it installs next to the
  official app instead of replacing it.
- Each build is signed with a different temporary key, so uninstall the previous version before installing a new one.
  Your library display settings are stored on the Jellyfin server and are kept.

---

<h1 align="center">Jellyfin for Android TV</h1>
<h3 align="center">Part of the <a href="https://jellyfin.org">Jellyfin Project</a></h3>

---

<p align="center">
<img alt="Logo banner" src="https://raw.githubusercontent.com/jellyfin/jellyfin-ux/master/branding/SVG/banner-logo-solid.svg?sanitize=true"/>
<br/><br/>
<a href="https://github.com/jellyfin/jellyfin-androidtv">
<img alt="GPL 2.0 License" src="https://img.shields.io/github/license/jellyfin/jellyfin-androidtv.svg"/>
</a>
<a href="https://github.com/jellyfin/jellyfin-androidtv/releases">
<img alt="Current Release" src="https://img.shields.io/github/release/jellyfin/jellyfin-androidtv.svg"/>
</a>
<a href="https://translate.jellyfin.org/projects/jellyfin-android/jellyfin-androidtv/">
<img alt="Translation Status" src="https://translate.jellyfin.org/widgets/jellyfin-android/-/jellyfin-androidtv/svg-badge.svg"/>
</a>
<br/>
<a href="https://opencollective.com/jellyfin">
<img alt="Donate" src="https://img.shields.io/opencollective/all/jellyfin.svg?label=backers"/>
</a>
<a href="https://features.jellyfin.org">
<img alt="Feature Requests" src="https://img.shields.io/badge/fider-vote%20on%20features-success.svg"/>
</a>
<a href="https://matrix.to/#/+jellyfin:matrix.org">
<img alt="Chat on Matrix" src="https://img.shields.io/matrix/jellyfin:matrix.org.svg?logo=matrix"/>
</a>
<br/>
<a href="https://play.google.com/store/apps/details?id=org.jellyfin.androidtv">
<img width="153" alt="Jellyfin on Google Play" src="https://jellyfin.org/images/store-icons/google-play.png"/>
</a>
<a href="https://www.amazon.com/gp/aw/d/B07TX7Z725">
<img width="153" alt="Jellyfin on Amazon Appstore" src="https://jellyfin.org/images/store-icons/amazon.png"/>
</a>
<a href="https://f-droid.org/en/packages/org.jellyfin.androidtv/">
<img width="153" alt="Jellyfin on F-Droid" src="https://jellyfin.org/images/store-icons/fdroid.png"/>
</a>
<br/>
<a href="https://repo.jellyfin.org/releases/client/androidtv/">Download archive</a>
</p>

Jellyfin for Android TV is a Jellyfin client for Android TV, Nvidia Shield, and Amazon Fire TV devices. We welcome all contributions and pull
requests! If you have a larger feature in mind please open an issue so we can discuss the implementation before you start. 

## Building

The app uses Gradle and requires the Android SDK. We recommend using Android Studio, which includes all required dependencies, for
development and building. For manual building without Android Studio make sure a compatible JDK and Android SDK are installed and in your
PATH, then use the Gradle wrapper (`./gradlew`) to build the project with the `assembleDebug` Gradle task to generate an apk file:

```shell
./gradlew assembleDebug
```

The task will create an APK file in the `/app/build/outputs/apk/debug` directory. This APK file uses a different app-id from our stable
builds and can be manually installed to your device.

## Branching

The `master` branch is the primary development branch and the target for all pull requests. It is **unstable** and may contain breaking
changes or unresolved bugs. For production deployments and forks, always use the latest `release-x.y.z` branch. Do not base production work
or long-lived forks on `master`.

Release branches are created at the start of a beta cycle and are kept up to date with each published release. Maintainers will cherry-pick
selected changes into release branches as needed for backports. These branches are reused for subsequent patch releases.

## Translating

Translations can be improved very easily from our [Weblate](https://translate.jellyfin.org/projects/jellyfin-android/jellyfin-androidtv)
instance. Look through the following graphic to see if your native language could use some work! We cannot accept changes to translation
files via pull requests.

<p align="center">
<a href="https://translate.jellyfin.org/engage/jellyfin-android/">
<img alt="Detailed Translation Status" src="https://translate.jellyfin.org/widgets/jellyfin-android/-/jellyfin-androidtv/multi-auto.svg"/>
</a>
</p>
