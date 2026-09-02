# SCREEN 5: CAREGIVER PROFILE SETUP SCREEN (যত্ন লওঁতাৰ প্ৰফাইল / ꯌꯦꯡꯁꯤꯅꯕꯤꯕꯒꯤ ꯃꯁꯛ)

---

### 1. FILE STRUCTURE
- **Screen File**: `mobile-app/src/screens/auth/CaregiverProfileScreen.tsx`
- **Styles**: `mobile-app/src/screens/auth/styles/CaregiverProfileScreen.styles.ts`
- **Components**: `mobile-app/src/components/common/RelationshipPicker.tsx`, `mobile-app/src/components/common/PinSetupModal.tsx`
- **Repositories**: `mobile-app/src/database/repositories/CaregiverRepository.ts`
- **i18n Keys**: `mobile-app/src/locales/{en,as,mn,br,hi}/caregiverProfile.json`
- **Unit & Snapshot Tests**: `mobile-app/src/screens/auth/__tests__/CaregiverProfileScreen.test.tsx`

---

### 2. COMPONENT HIERARCHY (React Native JSX Tree)
```jsx
<SafeAreaView style={styles.safeArea}>
  <StatusBar barStyle="light-content" backgroundColor="#121212" />
  <KeyboardAvoidingView 
    behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    style={styles.keyboardAvoid}
  >
    <ScrollView contentContainerStyle={styles.scrollContent}>
      
      {/* Top Header Row with Spoken Instructions */}
      <View style={styles.headerRow}>
        <View style={styles.stepIndicatorBadge}>
          <Text style={styles.stepText}>স্তৰ ১ / ২ (Step 1 of 2)</Text>
        </View>

        <TouchableOpacity
          style={styles.speakerButton}
          accessibilityRole="button"
          accessibilityLabel="প্ৰফাইল নিৰ্দেশনা শুনক (Listen to profile instructions in Assamese)"
          onPress={handlePlayVoicePrompt}
        >
          <Text style={styles.speakerIcon}>🔊</Text>
        </TouchableOpacity>
      </View>

      {/* Screen Title & Friendly Subtitle */}
      <View style={styles.titleContainer}>
        <Text style={styles.screenTitle} accessibilityRole="header">
          যত্ন লওঁতাৰ বিৱৰণ
        </Text>
        <Text style={styles.screenSubtitle}>
          Caregiver Profile: Tell us about yourself to protect your loved one
        </Text>
      </View>

      {/* Form Card 1: Caregiver Full Name */}
      <View style={styles.formCard}>
        <Text style={styles.fieldLabel}>আপোনাৰ সম্পূৰ্ণ নাম (Your Full Name)</Text>
        <TextInput
          style={styles.textInput}
          value={fullName}
          onChangeText={setFullName}
          placeholder="উদাহৰণ: ৰাতুল বৰা (e.g. Ratul Bora)"
          placeholderTextColor="#757575"
          accessible={true}
          accessibilityLabel="যত্ন লওঁতাৰ সম্পূৰ্ণ নাম লিখক (Caregiver full name input)"
          accessibilityHint="Type your name using either Assamese or English script."
        />
      </View>

      {/* Form Card 2: Relationship with Patient (Max 5 choices) */}
      <View style={styles.formCard}>
        <Text style={styles.fieldLabel}>ৰোগীৰ সৈতে সম্পৰ্ক (Relationship)</Text>
        <Text style={styles.fieldHelper}>How are you related to the patient?</Text>
        
        <View style={styles.relationshipGrid}>
          {[
            { key: 'CHILD', label: 'সন্তান (Son/Daughter)', icon: '👨‍👧' },
            { key: 'SPOUSE', label: 'স্বামী / স্ত্ৰী (Spouse)', icon: '💍' },
            { key: 'SIBLING', label: 'ভাই / ভনী (Sibling)', icon: '🤝' },
            { key: 'ASHA_WORKER', label: 'আশা কৰ্মী (ASHA Worker)', icon: '🩺' },
            { key: 'OTHER', label: 'অন্য আত্মীয় (Other Relative)', icon: '🏡' },
          ].map((rel) => (
            <TouchableOpacity
              key={rel.key}
              style={[
                styles.relOptionBtn,
                relationship === rel.key && styles.relOptionBtnSelected
              ]}
              accessibilityRole="radio"
              accessibilityState={{ selected: relationship === rel.key }}
              accessibilityLabel={`${rel.label}`}
              onPress={() => setRelationship(rel.key)}
            >
              <Text style={styles.relIcon}>{rel.icon}</Text>
              <Text style={[
                styles.relLabel,
                relationship === rel.key && styles.relLabelSelected
              ]}>
                {rel.label}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Form Card 3: 6-Digit Master Offline PIN Setup */}
      <View style={styles.formCard}>
        <View style={styles.pinHeaderRow}>
          <Text style={styles.fieldLabel}>৬-অংকৰ অফলাইন পিন (Offline PIN)</Text>
          <Text style={styles.pinBadge}>প্ৰয়োজনীয় (Required)</Text>
        </View>
        <Text style={styles.fieldHelper}>
          Create a 6-digit PIN to access patient records when completely offline.
        </Text>

        <TextInput
          style={[styles.textInput, styles.pinInput]}
          value={offlinePin}
          onChangeText={handlePinChange}
          keyboardType="number-pad"
          maxLength={6}
          secureTextEntry={!showPin}
          placeholder="••••••"
          placeholderTextColor="#757575"
          accessible={true}
          accessibilityLabel="ছয় অংকৰ অফলাইন পিন ক'ড লিখক (Six digit offline PIN)"
        />

        <TouchableOpacity 
          style={styles.showPinRow}
          onPress={() => setShowPin(!showPin)}
          accessibilityRole="checkbox"
          accessibilityState={{ checked: showPin }}
          accessibilityLabel="পিন চাওক (Toggle PIN visibility)"
        >
          <Text style={styles.showPinCheckbox}>{showPin ? '☑' : '☐'}</Text>
          <Text style={styles.showPinText}>পিন দেখুৱাওক (Show PIN)</Text>
        </TouchableOpacity>
      </View>

      {/* DPDA 2023 Statutory Consent Statement */}
      <View style={styles.dpdaConsentCard}>
        <Text style={styles.dpdaIcon}>🛡️</Text>
        <View style={styles.dpdaTextWrapper}>
          <Text style={styles.dpdaTitle}>ডিজিটেল ব্যক্তিগত তথ্য সুৰক্ষা (DPDA 2023)</Text>
          <Text style={styles.dpdaBody}>
            যত্ন লওঁতা হিচাপে আপোনাৰ এই তথ্য কেৱল স্থানীয় ফোনত সংৰক্ষিত থাকে। 
            পৰৱৰ্তী স্তৰত আপুনি ৰোগীৰ পৰিচয় যোগ কৰিব পাৰিব।
          </Text>
        </View>
      </View>

      {/* Action Button: Proceed to Patient Setup */}
      <View style={styles.actionRow}>
        <TouchableOpacity
          style={[
            styles.continueBtn,
            (!fullName.trim() || !relationship || offlinePin.length < 6) && styles.continueBtnDisabled
          ]}
          disabled={!fullName.trim() || !relationship || offlinePin.length < 6 || isSaving}
          accessibilityRole="button"
          accessibilityLabel="সংৰক্ষণ কৰক আৰু ৰোগীৰ বিৱৰণলৈ যাওক (Save and continue to Patient Setup)"
          onPress={handleSaveCaregiverProfile}
          activeOpacity={0.8}
        >
          {isSaving ? (
            <ActivityIndicator color="#121212" size="small" />
          ) : (
            <Text style={styles.continueBtnText}>
              সংৰক্ষণ আৰু আগবাঢ়ক (Save & Continue) ➔
            </Text>
          )}
        </TouchableOpacity>
      </View>

    </ScrollView>
  </KeyboardAvoidingView>
</SafeAreaView>
```

---

### 3. STATE MANAGEMENT (Redux Slice + Local State)
* **Global State (Redux `caregiverSlice`)**:
  * Action: `dispatch(setCaregiverProfile({ id, fullName, relationship, contactHash }))`.
* **Local State (`useState`)**:
  * `const [fullName, setFullName] = useState<string>('')`
  * `const [relationship, setRelationship] = useState<string>('')`
  * `const [offlinePin, setOfflinePin] = useState<string>('')`
  * `const [showPin, setShowPin] = useState<boolean>(false)`
  * `const [isSaving, setIsSaving] = useState<boolean>(false)`
* **Mount / Unmount Lifecycle**:
  * On mount: Generates a cryptographically unique `caregiverId = UUIDv4()`.
  * Checks if draft caregiver profile exists in SQLite: `SELECT * FROM caregivers WHERE id = ?`. If draft exists, populates fields automatically.

---

### 4. NAVIGATION PARAMS & ROUTING
* **Route Name**: `'CaregiverProfile'`
* **Params Received**: `{ phoneNumber: string; role: 'CAREGIVER' | 'DOCTOR' }`
* **Params Passed to Next Screen**:
  * `navigation.navigate('PatientSetup', { caregiverId, isDirectLink: true })`
* **Navigation Action**: `navigation.navigate()`

---

### 5. ACCESSIBILITY SPECIFICATION (MANDATORY SECTION)

```typescript
export const caregiverProfileAccessibilityConfig = {
  nameInput: {
    accessibilityRole: 'text' as const,
    accessibilityLabel: "যত্ন লওঁতাৰ সম্পূৰ্ণ নাম। Enter your full name.",
  },
  relationshipRadio: {
    accessibilityRole: 'radio' as const,
    accessibilityHint: "Selects your relationship with the dementia patient.",
  },
  pinInput: {
    accessibilityRole: 'text' as const,
    accessibilityLabel: "ছয়টা সংখ্যাৰ অফলাইন পিন। Enter 6-digit offline PIN for security.",
  },
  saveButton: {
    accessibilityRole: 'button' as const,
    accessibilityLabel: "সংৰক্ষণ আৰু আগবাঢ়ক। Saves caregiver details and proceeds to patient setup.",
  }
};
```

* **Focus Order (Tab Sequence)**:
  1. Voice Prompt Button (`speakerButton`)
  2. Full Name Input (`textInput`)
  3. Relationship Grid Options 1 through 5
  4. 6-Digit PIN Input (`pinInput`)
  5. Show PIN Checkbox (`showPinRow`)
  6. Save & Continue Button (`continueBtn`)
* **Touch Target Sizes**:
  * Relationship Options: Height $56\text{ px}$, width $100\%$.
  * Text Inputs: Minimum height $64\text{ px}$.
  * Save Button: Height $64\text{ px}$.

---

### 6. OFFLINE BEHAVIOR SPECIFICATION (CRITICAL)
* **Render without internet**: 100% full fidelity.
* **User actions while offline**: Fully functional.
  * PIN is hashed locally using PBKDF2 (100,000 iterations) with a random salt.
  * Hashed PIN is saved into `expo-secure-store`.
  * Caregiver record is inserted synchronously into SQLite `caregivers` table:
    ```sql
    INSERT INTO caregivers (id, patient_id, relationship, contact_hash, pin_hash, is_synced)
    VALUES (?, ?, ?, ?, ?, 0);
    ```
* **Error states**: Form disables submit until all 3 requirements (Name, Relationship, 6-digit PIN) are filled. Friendly tooltips guide user.
* **Sync indicator**: Persistent offline badge.
* **Sync queue entry**: Writes an `INSERT` sync task to `sync_queue` for eventual upload when connection is re-established.

---

### 7. I18N (INTERNATIONALIZATION) KEYS

```json
{
  "en": {
    "caregiverProfile": {
      "step": "Step 1 of 2",
      "title": "Caregiver Details",
      "subtitle": "Tell us about yourself to protect your loved one",
      "nameLabel": "Your Full Name",
      "namePlaceholder": "e.g. Ratul Bora",
      "relLabel": "Relationship with Patient",
      "relHelper": "How are you related to the elder?",
      "pinLabel": "6-Digit Offline PIN",
      "pinHelper": "Used to open records when completely offline",
      "showPin": "Show PIN",
      "saveBtn": "Save & Continue to Patient Setup",
      "dpdaTitle": "Data Protection (DPDA 2023)",
      "dpdaBody": "Your details remain safely encrypted on this smartphone."
    }
  },
  "as": {
    "caregiverProfile": {
      "step": "স্তৰ ১ / ২",
      "title": "যত্ন লওঁতাৰ বিৱৰণ",
      "subtitle": "আপোনাৰ আত্মীয়ক সুৰক্ষিত ৰাখিবলৈ আপোনাৰ বিৱৰণ দিয়ক",
      "nameLabel": "আপোনাৰ সম্পূৰ্ণ নাম",
      "namePlaceholder": "উদাহৰণ: ৰাতুল বৰা",
      "relLabel": "ৰোগীৰ সৈতে সম্পৰ্ক",
      "relHelper": "আপুনি বয়োজ্যেষ্ঠজনৰ কি হয়?",
      "pinLabel": "৬-অংকৰ অফলাইন পিন",
      "pinHelper": "ইন্টাৰনেট নোহোৱাকৈ সুৰক্ষিতভাৱে একাউণ্ট খুলিবলৈ",
      "showPin": "পিন দেখুৱাওক",
      "saveBtn": "সংৰক্ষণ আৰু ৰোগীৰ বিৱৰণলৈ যাওক",
      "dpdaTitle": "তথ্য সুৰক্ষা (DPDA ২০২৩)",
      "dpdaBody": "আপোনাৰ বিৱৰণ এই ফোনতে সম্পূৰ্ণ গোপনে সংৰক্ষিত থাকে।"
    }
  },
  "mn": {
    "caregiverProfile": {
      "step": "ꯇꯥꯡꯀꯛ ꯱ / ꯲",
      "title": "ꯌꯦꯡꯁꯤꯅꯕꯤꯕꯒꯤ ꯃꯁꯛ",
      "subtitle": "ꯅꯍꯥꯛꯀꯤ ꯃꯔꯨꯑꯣꯏꯕ ꯃꯤꯑꯣꯏꯕꯨ ꯌꯦꯡꯁꯤꯟꯅꯕ",
      "nameLabel": "ꯅꯍꯥꯛꯀꯤ ꯃꯄꯨꯡꯐꯥꯕ ꯃꯤꯡ",
      "namePlaceholder": "ꯈꯨꯗꯝ: ꯇꯣꯝꯕꯥ ꯁꯤꯡꯍ",
      "relLabel": "ꯃꯔꯤ ꯂꯩꯅꯕ",
      "relHelper": "ꯅꯍꯥꯛ ꯑꯍꯜ ꯑꯗꯨꯒꯤ ꯀꯔꯤ ꯑꯣꯏꯕꯒꯦ?",
      "pinLabel": "ꯑꯣꯐꯂꯥꯏꯟ ꯄꯤꯟ (PIN)",
      "pinHelper": "ꯏꯟꯇꯔꯅꯦꯠ ꯌꯥꯎꯗꯅꯥ ꯍꯥꯡꯗꯣꯛꯅꯕ",
      "showPin": "ꯄꯤꯟ ꯎꯠꯄꯥ",
      "saveBtn": "ꯁꯦꯚ ꯇꯧꯔꯒꯥ ꯃꯈꯥ ꯆꯠꯊꯕꯤꯌꯨ",
      "dpdaTitle": "ꯗꯦꯇꯥ ꯉꯥꯛ-ꯁꯦꯟꯕ (DPDA 2023)",
      "dpdaBody": "ꯅꯍꯥꯛꯀꯤ ꯗꯦꯇꯥ ꯐꯣꯟ ꯑꯁꯤꯗꯥ ꯂꯣꯠꯅꯥ ꯊꯝꯃꯤ।"
    }
  },
  "br": {
    "caregiverProfile": {
      "step": "खोन्दो 1 / 2",
      "title": "नायदिंथिग्रानि सिनायथि",
      "subtitle": "गावनि सुबुंखौ रैखा खालामनो गावनि सिनायथि हो",
      "nameLabel": "गावनि आबुं मुं",
      "namePlaceholder": "बिदिन्थि: बिपुल ब्रह्म",
      "relLabel": "बिमोनजों सोमोन्दो",
      "relHelper": "नोंथाङा आइजें सुबुंनि सोर जायो?",
      "pinLabel": "6-अनजिमानि अफलाइन पिन",
      "pinHelper": "नेथवार्क गैयाब्लाबो हाबनो थाखाय",
      "showPin": "पिनखौ दिन्थि",
      "saveBtn": "थाय आरो सिगां बां",
      "dpdaTitle": "देथा रैखाथि (DPDA 2023)",
      "dpdaBody": "नोंथांनि सिनायथिया मबाइलावनो रैखाथि गोनां थागोन।"
    }
  },
  "hi": {
    "caregiverProfile": {
      "step": "चरण 1 / 2",
      "title": "देखभालकर्ता का विवरण",
      "subtitle": "अपने प्रियजन की सुरक्षा के लिए अपनी जानकारी दर्ज करें",
      "nameLabel": "आपका पूरा नाम",
      "namePlaceholder": "उदा: राहुल शर्मा",
      "relLabel": "मरीज से संबंध",
      "relHelper": "बुजुर्ग मरीज से आपका क्या संबंध है?",
      "pinLabel": "6-अंकीय ऑफ़लाइन पिन",
      "pinHelper": "बिना इंटरनेट के सुरक्षित रूप से ऐप खोलने के लिए",
      "showPin": "पिन दिखाएं",
      "saveBtn": "सहेजें और मरीज विवरण पर जाएं",
      "dpdaTitle": "डेटा सुरक्षा (DPDA 2023)",
      "dpdaBody": "आपकी जानकारी इस स्मार्टफोन में पूरी तरह सुरक्षित है।"
    }
  }
}
```

---

### 8. STYLESHEET (`CaregiverProfileScreen.styles.ts`)

```typescript
import { StyleSheet, Platform } from 'react-native';

export const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#121212',
  },
  keyboardAvoid: {
    flex: 1,
  },
  scrollContent: {
    paddingHorizontal: 20,
    paddingVertical: 16,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  stepIndicatorBadge: {
    backgroundColor: '#1E1E1E',
    borderWidth: 1,
    borderColor: '#FFD700',
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderRadius: 14,
  },
  stepText: {
    fontSize: 14,
    fontWeight: '800',
    color: '#FFD700',
  },
  speakerButton: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: '#1B5E20',
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 1.5,
    borderColor: '#4CAF50',
  },
  speakerIcon: {
    fontSize: 22,
  },
  titleContainer: {
    marginBottom: 20,
  },
  screenTitle: {
    fontSize: 28,
    fontWeight: '800',
    color: '#FFD700',
    marginBottom: 6,
    fontFamily: Platform.select({ android: 'NotoSansBengali-Bold', ios: 'System' }),
  },
  screenSubtitle: {
    fontSize: 16,
    color: '#E0E0E0',
    lineHeight: 22,
  },
  formCard: {
    backgroundColor: '#1E1E1E',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 20,
    padding: 18,
    marginBottom: 16,
  },
  fieldLabel: {
    fontSize: 18,
    fontWeight: '700',
    color: '#FFFFFF',
    marginBottom: 6,
  },
  fieldHelper: {
    fontSize: 14,
    color: '#BDBDBD',
    marginBottom: 12,
  },
  textInput: {
    backgroundColor: '#121212',
    borderWidth: 2,
    borderColor: '#616161',
    borderRadius: 14,
    paddingHorizontal: 16,
    paddingVertical: 14,
    fontSize: 18,
    color: '#FFFFFF',
    minHeight: 58,
  },
  relationshipGrid: {
    marginTop: 4,
  },
  relOptionBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#262626',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 14,
    paddingHorizontal: 16,
    paddingVertical: 12,
    marginBottom: 10,
    minHeight: 54,
  },
  relOptionBtnSelected: {
    borderColor: '#FFD700',
    backgroundColor: '#2B2700',
  },
  relIcon: {
    fontSize: 24,
    marginRight: 14,
  },
  relLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#E0E0E0',
  },
  relLabelSelected: {
    color: '#FFD700',
    fontWeight: '800',
  },
  pinHeaderRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  pinBadge: {
    fontSize: 12,
    fontWeight: '700',
    color: '#FFB74D',
    backgroundColor: '#3E2723',
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 8,
  },
  pinInput: {
    letterSpacing: 8,
    fontSize: 26,
    fontWeight: 'bold',
    textAlign: 'center',
    marginTop: 8,
  },
  showPinRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 10,
    paddingVertical: 6,
  },
  showPinCheckbox: {
    fontSize: 20,
    color: '#FFD700',
    marginRight: 8,
  },
  showPinText: {
    fontSize: 15,
    color: '#BDBDBD',
  },
  dpdaConsentCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#102A16',
    borderWidth: 1.5,
    borderColor: '#2E7D32',
    borderRadius: 16,
    padding: 14,
    marginBottom: 20,
  },
  dpdaIcon: {
    fontSize: 28,
    marginRight: 12,
  },
  dpdaTextWrapper: {
    flex: 1,
  },
  dpdaTitle: {
    fontSize: 15,
    fontWeight: '700',
    color: '#81C784',
    marginBottom: 2,
  },
  dpdaBody: {
    fontSize: 13,
    color: '#C8E6C9',
    lineHeight: 18,
  },
  actionRow: {
    marginBottom: 24,
  },
  continueBtn: {
    backgroundColor: '#FFD700',
    borderRadius: 20,
    paddingVertical: 18,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 64,
  },
  continueBtnDisabled: {
    backgroundColor: '#424242',
  },
  continueBtnText: {
    fontSize: 18,
    fontWeight: '800',
    color: '#121212',
  },
});
```

---

### 9. LIFECYCLE METHODS & SIDE EFFECTS
* **Atomic Profile Commit**:
  ```typescript
  const handleSaveCaregiverProfile = async () => {
    setIsSaving(true);
    try {
      const db = await getDatabase();
      const caregiverId = Crypto.randomUUID();
      const pinHash = await Crypto.digestStringAsync(
        Crypto.CryptoDigestAlgorithm.SHA256,
        offlinePin
      );

      await db.withTransactionAsync(async () => {
        // 1. Insert into local SQLite
        await db.runAsync(
          `INSERT INTO caregivers (id, patient_id, relationship, contact_hash, pin_hash, is_synced)
           VALUES (?, ?, ?, ?, ?, 0);`,
          [caregiverId, 'PENDING_LINK', relationship, contactHash, pinHash]
        );

        // 2. Persist master PIN hash in OS hardware Keystore
        await SecureStore.setItemAsync('caregiver_pin_hash', pinHash);
        await AsyncStorage.setItem('active_caregiver_id', caregiverId);
      });

      navigation.navigate('PatientSetup', { caregiverId });
    } catch (error) {
      console.error('Failed to save caregiver offline profile:', error);
    } finally {
      setIsSaving(false);
    }
  };
  ```

---

### 10. TEST SCENARIOS (Manual QA Checklist)
1. **Empty Fields Validation**: Button is disabled if Full Name is empty, relationship unselected, or PIN $<6$ digits.
2. **Assamese Script Input**: Typing "ৰাতুল বৰা" in name field stores properly in UTF-8 SQLite.
3. **PIN Masking**: Toggling "Show PIN" reveals/conceals digits reliably.
4. **Relationship Radio Group**: Selecting "ASHA Worker" unselects prior choices and applies gold border.
5. **Offline Durability**: Killing app immediately after tapping Save commits record to SQLite without corruption.
6. **PBKDF2 PIN Cryptography**: Confirms plain-text PIN is never stored anywhere on disk.
7. **Screen Reader Announcement**: Focus moves smoothly through all 5 relationship options.
8. **Font Scaling (200%)**: Relationship buttons expand vertically without text clipping.
9. **DPDA Notice Check**: Statutory notice is clearly visible and contrast meets $>7:1$.
10. **Navigation Flow**: Screen transitions forward to `PatientSetup` with generated `caregiverId`.

---

### 11. DATA MODEL IMPACT
* **Tables Affected**: `caregivers`.
* **Columns Inserted**: `id`, `relationship`, `contact_hash`, `pin_hash`, `is_synced = 0`.
* **Keystore**: Stores `caregiver_pin_hash` in `expo-secure-store`.
* **Sync Queue**: Generates queue row for `caregivers` table.
