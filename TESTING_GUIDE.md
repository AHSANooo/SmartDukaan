# Testing Guide - Phase 1 Implementation
## How to Test All New Features

---

## 🧪 TEST SUITE

### Test 1: Runtime Permissions - Camera
**Feature:** Barcode Scanner Permission

**Steps:**
1. Open app → Home → Add Item
2. Click the barcode scanner icon (📷)
3. **Expected:** Permission dialog appears
4. Click "Deny"
   - **Expected:** Toast message "Camera permission denied. You can still enter barcode manually."
5. Click barcode icon again
6. **Expected:** Permission rationale dialog
7. Click "Grant Permission"
8. Click "Allow" in system dialog
   - **Expected:** Toast "Barcode scanning feature coming soon!"

**Pass Criteria:** ✅ No crashes, proper dialogs shown

---

### Test 2: Runtime Permissions - Storage
**Feature:** Data Export Permission

**Steps:**
1. Open app → Profile → Backup
2. Click "Export Data"
3. **Expected:** 
   - Android 10+: Directly creates backup
   - Android < 10: Permission dialog
4. Grant permission (if asked)
5. **Expected:** "Backup Created Successfully" dialog with info

**Pass Criteria:** ✅ Backup file created in app's external storage

---

### Test 3: Input Validation - Item Name
**Feature:** Comprehensive field validation

**Test Cases:**
```
Test 3.1: Empty Name
- Leave name blank → Click Save
- Expected: "Item name is required"

Test 3.2: Too Short
- Enter "A" → Click Save
- Expected: "Item name too short (minimum 2 characters)"

Test 3.3: Too Long
- Enter 101+ characters → Click Save
- Expected: "Item name too long (maximum 100 characters)"

Test 3.4: Valid Name
- Enter "Test Item" → Should accept
```

**Pass Criteria:** ✅ All validation messages shown correctly

---

### Test 4: Input Validation - Prices
**Feature:** Price validation and relationship

**Test Cases:**
```
Test 4.1: Negative Buying Price
- Buying: -100 → Click Save
- Expected: "Buying price cannot be negative"

Test 4.2: Zero Selling Price
- Selling: 0 → Click Save
- Expected: "Selling price must be greater than zero"

Test 4.3: Selling < Buying
- Buying: 100, Selling: 50 → Click Save
- Expected: "Selling price (Rs 50) should be higher than buying price (Rs 100)"

Test 4.4: Valid Prices
- Buying: 80, Selling: 100 → Should accept
```

**Pass Criteria:** ✅ All price validations work

---

### Test 5: Input Validation - Barcode
**Feature:** Barcode format validation

**Test Cases:**
```
Test 5.1: Too Short
- Barcode: "123" → Click Save
- Expected: "Barcode too short (minimum 8 digits)"

Test 5.2: Letters in Barcode
- Barcode: "ABC12345" → Click Save
- Expected: "Barcode must contain only numbers"

Test 5.3: Valid Barcode
- Barcode: "12345678" → Should accept

Test 5.4: Empty Barcode
- Leave blank → Should accept (optional field)
```

**Pass Criteria:** ✅ Barcode validation works

---

### Test 6: Duplicate Barcode Prevention
**Feature:** Prevent duplicate barcodes

**Steps:**
1. Add Item 1:
   - Name: "Rice"
   - Barcode: "12345678"
   - Save → Success
2. Add Item 2:
   - Name: "Sugar"
   - Barcode: "12345678" (same)
   - Click Save
3. **Expected:** "Item with barcode '12345678' already exists: Rice"

**Pass Criteria:** ✅ Duplicate prevented, helpful message shown

---

### Test 7: Data Backup - Export
**Feature:** Export data to JSON

**Steps:**
1. Add 3-5 test items
2. Make 2-3 test sales
3. Go to Profile → Backup
4. Click "Export Data"
5. **Expected:** Dialog with:
   ```
   ✓ Backup Created Successfully
   
   Backup Date: [timestamp]
   Items: 5
   Sales: 3
   Version: 1.0.0
   Device: [manufacturer model]
   Size: [X] KB
   ```
6. Click "Share Backup"
7. **Expected:** Android share sheet opens
8. Select WhatsApp/Email
9. **Expected:** JSON file attached

**Pass Criteria:** ✅ File created, shareable, contains data

---

### Test 8: Data Backup - Import
**Feature:** Restore from backup

**Steps:**
1. Create backup (Test 7)
2. Add 2 more items (not in backup)
3. Go to Profile → Backup
4. Click "Import Data"
5. **Expected:** Warning dialog
6. Click "Yes, Import"
7. Select previously exported JSON file
8. **Expected:** 
   - "Data Restored Successfully"
   - "App will restart"
9. Click OK
10. **Expected:** App restarts
11. Check data
12. **Expected:** Only items from backup exist

**Pass Criteria:** ✅ Data restored correctly, new items removed

---

### Test 9: Error Handling - Corrupted Data
**Feature:** Graceful recovery from bad data

**Steps:**
1. Export backup
2. Open JSON file in text editor
3. Delete random characters (corrupt it)
4. Try to import
5. **Expected:** "Failed to restore data. Invalid backup file."

**Pass Criteria:** ✅ No crash, error message shown

---

### Test 10: Error Handling - Sale Validation
**Feature:** Prevent overselling

**Steps:**
1. Add item: "Rice" with Qty: 10
2. Go to Sale
3. Select Rice
4. Enter Qty: 15 (more than available)
5. Try to add to cart
6. **Expected:** "Not enough stock! Only 10 available"

**Pass Criteria:** ✅ Sale prevented, stock protected

---

### Test 11: Sale Discount Validation
**Feature:** Validate discount amount

**Steps:**
1. Create sale with items totaling Rs 100
2. Go to summary
3. Enter discount: 150 (more than total)
4. Click Complete Sale
5. **Expected:** "Discount cannot exceed subtotal"

**Pass Criteria:** ✅ Sale prevented with validation message

---

### Test 12: ProGuard Build
**Feature:** Release build optimization

**Steps:**
1. Run: `gradlew.bat assembleRelease`
2. **Expected:** Build succeeds
3. Check APK size in `app/release/`
4. **Expected:** Smaller than debug build
5. Install release APK
6. **Expected:** App works normally

**Pass Criteria:** ✅ Release build works, code obfuscated

---

## 📱 DEVICE COMPATIBILITY TESTS

### Test on Multiple Android Versions:

**Android 5.0-6.0 (API 21-23):**
- [ ] Permissions work
- [ ] Backup works
- [ ] All features functional

**Android 7.0-9.0 (API 24-28):**
- [ ] FileProvider works for sharing
- [ ] Storage permissions requested
- [ ] No crashes

**Android 10-14 (API 29-34):**
- [ ] Scoped storage works
- [ ] No storage permissions needed
- [ ] All features work

---

## 🔍 STRESS TESTS

### Test 13: Large Data Set
**Steps:**
1. Add 100+ items
2. Create 50+ sales
3. Export data
4. **Expected:** File created successfully
5. Check file size
6. Import data
7. **Expected:** All data restored

**Pass Criteria:** ✅ Handles large datasets

---

### Test 14: Rapid Operations
**Steps:**
1. Quickly add 10 items (fast clicking)
2. **Expected:** All saved correctly
3. Delete 5 items rapidly
4. **Expected:** All deleted correctly

**Pass Criteria:** ✅ No race conditions, no data loss

---

### Test 15: App Restart Recovery
**Steps:**
1. Add items
2. Force close app (don't crash, just close)
3. Reopen app
4. **Expected:** All data present
5. Check for any corruption
6. **Expected:** Data intact

**Pass Criteria:** ✅ Data persists across sessions

---

## 🐛 EDGE CASES

### Test 16: Network Unavailable
- Airplane mode ON
- Try all features
- **Expected:** Everything works (offline-first)

### Test 17: Low Storage
- Device almost full
- Try to export
- **Expected:** Graceful error message

### Test 18: No File Manager
- Uninstall file manager apps
- Try to import
- **Expected:** "No file manager app found"

### Test 19: Permission Permanently Denied
- Deny permission twice
- **Expected:** Directs to app settings

### Test 20: Rapid Activity Switching
- Switch between activities quickly using nav bar
- **Expected:** No crashes, smooth transitions

---

## 📊 REGRESSION TESTS

### Existing Features (Should Still Work):

- [ ] Add items
- [ ] View stock
- [ ] Filter stock (All, Low, Out, In)
- [ ] Search items
- [ ] Edit items
- [ ] Delete items
- [ ] Make sales
- [ ] View cart
- [ ] Complete sales
- [ ] View reports
- [ ] Today's sales summary
- [ ] Low stock alerts
- [ ] Bottom navigation
- [ ] All UI interactions

---

## ✅ ACCEPTANCE CRITERIA

### Must Pass All:
1. ✅ No crashes during normal usage
2. ✅ All permissions work correctly
3. ✅ Validation catches invalid input
4. ✅ Backup/restore works end-to-end
5. ✅ Error messages are user-friendly
6. ✅ Data integrity maintained
7. ✅ Release build compiles
8. ✅ App size reasonable (<50 MB)
9. ✅ No data loss scenarios
10. ✅ All existing features still work

---

## 🎯 PRIORITY TESTS

### Critical (Must Test):
- Permission handling
- Input validation
- Backup/restore
- Error recovery
- Data integrity

### Important (Should Test):
- Edge cases
- Multiple devices
- Different Android versions
- Stress tests

### Nice to Have (Can Test):
- UI polish
- Animation smoothness
- Battery usage
- Memory usage

---

## 📝 BUG REPORTING FORMAT

If you find issues, report like this:

```
Bug: [Short description]
Steps to Reproduce:
1. 
2. 
3. 

Expected: [What should happen]
Actual: [What actually happened]
Device: [Manufacturer Model]
Android: [Version]
Severity: [Critical/High/Medium/Low]
```

---

## 🚀 READY TO TEST!

Follow tests in order for best results. Mark each test as you complete it.
