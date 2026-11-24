# R8 rules for release builds. Libraries (Firebase, Room, Glide, AndroidX) ship their own
# consumer rules; these cover what reflection in this app's own code needs.

# Firebase Realtime Database reads and writes RemoteRecordItem by reflection
# (getValue(RemoteRecordItem::class.java) / setValue(item)), so its no-arg constructor and
# property names must survive. Signature keeps generic types for the database mapper.
-keepattributes Signature
-keepclassmembers class com.msmikeescom.minesweeper.repository.remote.realtimedatabase.model.** {
    *;
}

# Credential Manager finds its Play Services provider by reflection.
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}

# Readable Crashlytics stack traces: keep line numbers (Crashlytics uploads the mapping file
# for release builds) and the names of exception classes.
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
