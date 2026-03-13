# Secure Image Gallery Setup Guide

## 1. Required Gradle Dependencies

Add the following to your `build.gradle.kts` (app level) inside the `dependencies` block:

```gradle
dependencies {
    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database:20.3.0")
    
    // Firebase Storage
    implementation("com.google.firebase:firebase-storage:20.3.0")

    // Core Firebase (Usually added during setup)
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    
    // Glide Library (For Image Loading)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0") // Java
    // OR ksp("com.github.bumptech.glide:compiler:4.16.0") // Kotlin
    
    // Material Design for FAB
    implementation("com.google.android.material:material:1.11.0")
}
```

## 2. Permissions Required

Add these permissions to your `AndroidManifest.xml` inside `<manifest>` but before `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />

<!-- For Android 12 and below to pick images -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

<!-- For Android 13+ (Though picking via intent usually works without generic storage perms now) -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

## 3. Firebase Database Structure Example

### Firebase Database Structure (`Gallery` Node)
```json
{
  "Gallery": {
    "imageId_xyz_123": {
      "imageId": "imageId_xyz_123",
      "imageUrl": "https://firebasestorage.googleapis.com/v0/b/YOUR-PROJECT/o/gallery_images%2Fimage1.jpg?alt=media&token=..."
    },
    "imageId_abc_456": {
      "imageId": "imageId_abc_456",
      "imageUrl": "https://firebasestorage.googleapis.com/v0/b/YOUR-PROJECT/o/gallery_images%2Fimage2.jpg?alt=media&token=..."
    }
  }
}
```

### Firebase Storage Structure
```
Firebase Storage Root
 └── gallery_images/
      ├── e8a72c1c-dc31.jpg
      ├── a2b123fc-ab42.jpg
```

## 4. Admin Role Handling
Look at the `checkUserRole()` method inside the `GalleryActivity.java` file. You will need to implement your app's specific admin-check logic there. When `isAdmin` is true, the `fabUploadImage` (upload button) is visible and allows them to pick and upload images to Firebase. If `isAdmin` is false, it uses `View.GONE` to hide it.

## Important Note
The user requested not to commit these changes to GitHub right now. So everything exists in this standalone `GalleryFeature` folder. You can copy these files into your package structure whenever you're ready!
