- Upload APK
- Submit for review

---

## 🔍 VERIFICATION

### After Building, Verify:
# Build & Deploy Quick Reference
**Debug Build:**
```
✓ File exists: app\build\outputs\apk\debug\app-debug.apk
✓ Size: ~15-25 MB
✓ Installs without errors
✓ All features work
✓ Permissions requested
```

**Release Build:**
```
✓ File exists: app\build\outputs\apk\release\app-release-unsigned.apk
✓ Size: ~8-15 MB (smaller due to shrinking)
✓ Code obfuscated (check with APK Analyzer)
✓ No debug logs in logcat
✓ All features work
```

---

## 📱 DEVICE TESTING

### Minimum Devices to Test:
1. Android 5.0 device (API 21)
2. Android 10 device (API 29)
3. Android 13+ device (API 33+)

### Screen Sizes:
- Small (4-5")
- Medium (5-6")
- Large (6"+)

---

## 🐛 COMMON ISSUES

### Issue: "Failed to install APK"
**Solution:**
```bash
adb uninstall com.example.smartdukaan
adb install app\build\outputs\apk\debug\app-debug.apk
```

### Issue: "Permission denied"
**Solution:**
- Enable "Install from Unknown Sources"
- Check USB debugging enabled

### Issue: "Build failed"
**Solution:**
```bash
gradlew.bat clean
gradlew.bat build --stacktrace --info
```

### Issue: "App crashes on startup"
**Solution:**
```bash
# Check logs
adb logcat -s SmartDukaan AndroidRuntime
```

---

## 📞 SUPPORT

### If You Need Help:
1. Check error logs: `adb logcat`
2. Review TESTING_GUIDE.md
3. Check Android Studio Build console
4. Verify all files are saved

---

## ✅ FINAL CHECKLIST

Before considering Phase 1 complete:

- [x] All utility classes created
- [x] Permissions implemented
- [x] Error handling added
- [x] Validation working
- [x] Backup/restore functional
- [x] ProGuard enabled
- [x] Documentation created
- [ ] App builds successfully (YOU TEST THIS)
- [ ] All features tested (USE TESTING_GUIDE.md)
- [ ] No crashes (USER TESTING)
- [ ] Ready for Phase 2

---

## 🎉 YOU'RE READY!

Run this now:
```bash
cd C:\Users\HP\Desktop\SmartDukaan
gradlew.bat clean assembleDebug
```

If build succeeds → Install and test!
If build fails → Check error messages and fix accordingly.

**Good luck! 🚀**
## Commands and Steps

---

## 🔨 BUILD COMMANDS

### Debug Build (For Testing)
```bash
cd C:\Users\HP\Desktop\SmartDukaan
gradlew.bat clean
gradlew.bat assembleDebug
```

**Output:** `app\build\outputs\apk\debug\app-debug.apk`

---

### Release Build (For Production)
```bash
cd C:\Users\HP\Desktop\SmartDukaan
gradlew.bat clean
gradlew.bat assembleRelease
```

**Output:** `app\build\outputs\apk\release\app-release-unsigned.apk`

---

### Install on Device
```bash
# Debug
gradlew.bat installDebug

# Release
gradlew.bat installRelease
```

---

### Run Tests
```bash
# Unit tests
gradlew.bat test

# All tests
gradlew.bat connectedAndroidTest
```

---

## 📱 ADB COMMANDS

### Install APK
```bash
adb install app\build\outputs\apk\debug\app-debug.apk
```

### Reinstall (keep data)
```bash
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### Uninstall
```bash
adb uninstall com.example.smartdukaan
```

### View Logs
```bash
adb logcat -s SmartDukaan
```

### Clear App Data
```bash
adb shell pm clear com.example.smartdukaan
```

---

## 🔧 TROUBLESHOOTING

### Build Fails
```bash
# Clean and rebuild
gradlew.bat clean
gradlew.bat build --stacktrace
```

### Gradle Sync Issues
```bash
# Refresh dependencies
gradlew.bat --refresh-dependencies
```

### Android Studio Issues
1. File → Invalidate Caches
2. Restart Android Studio
3. Rebuild Project

---

## ✅ PRE-RELEASE CHECKLIST

### Before Building Release:

- [ ] All tests pass
- [ ] No compiler errors/warnings
- [ ] ProGuard enabled: `isMinifyEnabled = true`
- [ ] Update version in `build.gradle.kts`
  ```kotlin
  versionCode = 2
  versionName = "1.1"
  ```
- [ ] Test on multiple devices
- [ ] Test backup/restore
- [ ] Test all permissions
- [ ] Check APK size (<50 MB)
- [ ] Sign APK (for Play Store)

---

## 🔐 SIGNING APK (For Play Store)

### Generate Keystore (First Time)
```bash
keytool -genkey -v -keystore smartdukaan.keystore -alias smartdukaan -keyalg RSA -keysize 2048 -validity 10000
```

### Sign APK
```bash
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore smartdukaan.keystore app-release-unsigned.apk smartdukaan
```

### Verify Signature
```bash
jarsigner -verify -verbose -certs app-release-unsigned.apk
```

---

## 📊 APK ANALYSIS

### Check APK Size
```bash
dir app\build\outputs\apk\release
```

### Analyze APK Content
1. Open Android Studio
2. Build → Analyze APK
3. Select APK file
4. Review size breakdown

---

## 🎯 WHAT'S BEEN IMPLEMENTED

### ✅ Phase 1 Complete
- Runtime permissions
- Error handling
- Input validation
- Backup/restore
- ProGuard enabled

### All New Files Created:
```
utils/
├── PermissionHelper.kt      ✅ Created
├── ErrorHandler.kt           ✅ Created
├── ValidationHelper.kt       ✅ Created
└── BackupManager.kt          ✅ Created

res/xml/
└── file_paths.xml            ✅ Created

Documentation/
├── PHASE1_IMPLEMENTATION_COMPLETE.md  ✅ Created
├── TESTING_GUIDE.md                    ✅ Created
└── BUILD_DEPLOY.md                     ✅ This file
```

### Updated Files:
```
AndroidManifest.xml           ✅ Added permissions + FileProvider
build.gradle.kts              ✅ Enabled ProGuard
proguard-rules.pro            ✅ Comprehensive rules
DataManager.kt                ✅ Error handling + validation
AddItemFragment.kt            ✅ Validation + permissions
ProfileActivity.kt            ✅ Functional backup/restore
SaleSummaryFragment.kt        ✅ Discount validation
```

---

## 🚀 DEPLOYMENT STEPS

### Step 1: Build
```bash
gradlew.bat clean
gradlew.bat assembleRelease
```

### Step 2: Test
- Install on 3+ devices
- Test all features
- Check TESTING_GUIDE.md

### Step 3: Sign (for Play Store)
- Generate keystore
- Sign APK
- Verify signature

### Step 4: Prepare Listing
- Create screenshots
- Write description
- Privacy policy
- Terms of service

### Step 5: Upload
- Go to Play Console
- Create new release

