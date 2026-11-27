# MinesWeeper for Android

A Minesweeper game for Android. Players sign in with Google or email, and every win is saved
to their account so their best times follow them across devices.

The iOS app lives in [MinesWeeper-iOS](https://github.com/mikeescom/MinesWeeper-iOS) and
plays by the same rules.

## Features

- **Gameplay**
  - The board fills the screen.
  - Tap a square to open it; long-press to place or remove a flag.
  - The first tap is never a mine and opens an area.
  - You win by opening every safe square or by flagging every mine.
- **Header:** a mines counter, a face button that restarts the game, a timer, and an account
  menu.
- **Custom difficulty:** set the number of mines (1 to the number of squares minus 1, at most
  999) from the account menu.
- **Accounts:** Sign in with Google (Credential Manager) or with email and password, including
  account creation and password reset. Players can delete their account and all their records
  from Settings, as Google Play requires.
- **Records:** every win is saved to Firebase Realtime Database and cached on the device. The
  Records screen ranks them by mines per square, then by time.

## Tech stack

| Area | Library |
| --- | --- |
| Language | Kotlin 2.0, Java 17 |
| Build | Android Gradle Plugin 8.13, Gradle 8.13, KSP |
| UI | Views, Fragments, Navigation component |
| Dependency injection | Hilt |
| Local storage | Room, SharedPreferences |
| Backend | Firebase Auth, Realtime Database, Crashlytics, Analytics |
| Sign-in | Credential Manager with Sign in with Google |
| Images | Glide |

Min SDK 26 (Android 8.0), target SDK 36 (Android 16).

## Project structure

```
app/src/main/java/com/msmikeescom/minesweeper/
├── game/          Board (all game rules, no Android code) and RecordRanking
├── di/            Hilt module for the Room database and FirebaseAuth
├── repository/    MainRepository, plus Room, SharedPreferences and Realtime Database access
├── viewmodel/     GameBoardViewModel (board, timer, records) and LogInViewModel
├── ui/            Activities, fragments, custom views (mines counter, timer) and adapters
└── utilities/     Dialogs, constants and full-screen setup
```

`Board` holds every game rule and has no Android dependencies, so it is unit tested directly.
The fragments only draw the board and pass taps to it. The board and timer live in
`GameBoardViewModel`, so a game survives configuration changes.

## Getting started

1. Open the project in Android Studio. You need JDK 17.
2. `app/google-services.json` is committed and points at the existing Firebase project. To use
   your own Firebase project, replace it with yours.
3. Run the `app` configuration.

### Firebase setup

- **Authentication:** enable the **Google** and **Email/Password** sign-in providers. Register
  your debug and release SHA-1 fingerprints in the Firebase console, or Google sign-in will fail.
- **Realtime Database:** records are stored at `users/<uid>/<record id>`. The rules in
  `database.rules.json` let a signed-in user read and write only their own records, and they
  validate each record's fields. Deploy them with:

  ```sh
  firebase deploy --only database
  ```

## Building and testing

```sh
./gradlew test             # unit tests
./gradlew assembleDebug    # debug APK
./gradlew assembleRelease  # release APK, shrunk with R8
./gradlew build            # everything CI runs: build, unit tests and lint
```

The unit tests cover the game rules (`BoardTest`), record ranking (`RecordRankingTest`) and
record conversion (`RecordItemMappingTest`).

Release builds use R8 code and resource shrinking. `app/proguard-rules.pro` keeps the parts the
app reaches by reflection: the Firebase record model and Credential Manager's Google provider.
R8 problems only appear when a release build runs, so try a release build on a device before
you publish.

## Continuous integration

GitHub Actions (`.github/workflows/android.yml`) runs `./gradlew build` on JDK 17 for every
push to `master` and every pull request into it.

## Privacy

See [Privacy_Policy.md](Privacy_Policy.md).
