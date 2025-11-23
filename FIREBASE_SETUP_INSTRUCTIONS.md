# Firebase Authentication Setup Instructions

## Overview
This document provides step-by-step instructions to integrate Firebase Authentication into your RunTrack Android app.

## Prerequisites
- Android Studio installed
- Google account for Firebase Console access
- RunTrack project open in Android Studio

---

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"** or **"Create a project"**
3. Enter project name: `RunTrack` (or your preferred name)
4. (Optional) Enable Google Analytics if desired
5. Click **"Create project"**

---

## Step 2: Add Android App to Firebase Project

1. In Firebase Console, click the **Android icon** to add an Android app
2. Enter your package name: `week11.st269142.RunTrack`
   - Find this in your `app/build.gradle.kts` file under `applicationId`
3. (Optional) Enter app nickname: `RunTrack`
4. (Optional) Add SHA-1 certificate fingerprint (required for Google Sign-In, optional for Email/Password)
   - To get SHA-1, run in terminal:
     ```bash
     ./gradlew signingReport
     ```
   - Copy the SHA-1 from the debug variant
5. Click **"Register app"**

---

## Step 3: Download google-services.json

1. Download the `google-services.json` file from Firebase Console
2. Move the file to your project's `app/` directory
   - Path should be: `RunTrack/app/google-services.json`
3. **Important:** Verify the file is in the correct location

---

## Step 4: Add Firebase SDK to Your Project

### 4.1 Update Project-level build.gradle.kts

Open `build.gradle.kts` (Project level) and add the Google services plugin:

```kotlin
plugins {
    // ... existing plugins
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

### 4.2 Update App-level build.gradle.kts

Open `app/build.gradle.kts` and make the following changes:

#### Add plugin at the top:
```kotlin
plugins {
    // ... existing plugins
    id("com.google.gms.google-services")
}
```

#### Add Firebase dependencies:
```kotlin
dependencies {
    // ... existing dependencies
    
    // Firebase BOM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")
    
    // Coroutines support for Firebase
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}
```

### 4.3 Sync Project

Click **"Sync Now"** in Android Studio to download the Firebase dependencies.

---

## Step 5: Enable Email/Password Authentication in Firebase

1. In Firebase Console, go to **Authentication** section
2. Click **"Get started"** if this is your first time
3. Go to **"Sign-in method"** tab
4. Click on **"Email/Password"**
5. Toggle **"Enable"** switch to ON
6. Click **"Save"**

---

## Step 6: Configure ProGuard (Optional but Recommended)

If you're using ProGuard/R8 for release builds, add these rules to `proguard-rules.pro`:

```proguard
# Firebase Authentication
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
```

---

## Step 7: Test the Integration

1. **Build and Run** your app
2. Try registering a new account:
   - Go to Register screen
   - Enter name, email, and password
   - Click "Sign Up"
3. Check Firebase Console:
   - Go to **Authentication > Users** tab
   - You should see the newly created user

---

## Verification Checklist

- [ ] Firebase project created
- [ ] Android app added to Firebase project
- [ ] `google-services.json` file in `app/` directory
- [ ] Google services plugin added to project-level `build.gradle.kts`
- [ ] Google services plugin applied in app-level `build.gradle.kts`
- [ ] Firebase BOM and Auth dependencies added
- [ ] Project synced successfully
- [ ] Email/Password authentication enabled in Firebase Console
- [ ] App builds without errors
- [ ] Test user registration successful
- [ ] User appears in Firebase Console

---

## Troubleshooting

### Build Error: "google-services.json is missing"
- Ensure `google-services.json` is in the `app/` directory
- Sync project again

### Build Error: "Plugin with id 'com.google.gms.google-services' not found"
- Check that you added the plugin to project-level `build.gradle.kts`
- Verify version number is correct

### Authentication Fails with "Network Error"
- Check internet connection
- Verify Firebase project is active
- Check that Email/Password auth is enabled in Firebase Console

### User Not Appearing in Firebase Console
- Wait a few seconds and refresh the page
- Check that you're looking at the correct Firebase project
- Verify the app is using the correct `google-services.json`

---

## Additional Features (Optional)

### Email Verification
To require email verification before users can access the app:

```kotlin
// In AuthRepository.kt, after user creation:
firebaseUser.sendEmailVerification().await()
```

### Password Requirements
Firebase enforces minimum 6 characters. To add custom requirements, modify validation in ViewModels.

### Account Deletion
Add this method to `AuthRepository.kt`:

```kotlin
suspend fun deleteAccount(): AuthResult<Unit> {
    return try {
        currentUser?.delete()?.await()
        AuthResult.Success(Unit)
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: "Failed to delete account")
    }
}
```

---

## Security Best Practices

1. **Never commit `google-services.json` to public repositories**
   - Add to `.gitignore` if needed
   
2. **Use Firebase Security Rules** for Firestore/Realtime Database
   
3. **Implement rate limiting** to prevent abuse
   
4. **Enable App Check** for additional security (optional)

---

## Next Steps

After successful Firebase integration:

1. Test all authentication flows (Login, Register, Forgot Password)
2. Add additional Firebase services (Firestore, Storage, etc.)
3. Implement proper error handling for network issues
4. Add loading states and user feedback
5. Consider adding social authentication (Google, Facebook, etc.)

---

## Support Resources

- [Firebase Documentation](https://firebase.google.com/docs)
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Firebase Authentication Guide](https://firebase.google.com/docs/auth/android/start)
- [Firebase Console](https://console.firebase.google.com/)

---

## Summary

Your app is now ready for Firebase Authentication! The code is already implemented with:
- ✅ MVVM Architecture
- ✅ Repository Pattern
- ✅ State Management with StateFlow
- ✅ Input Validation
- ✅ Error Handling
- ✅ Loading States
- ✅ Navigation with Auth Guards
- ✅ Reusable UI Components

Just follow the steps above to connect to Firebase and you're good to go!
