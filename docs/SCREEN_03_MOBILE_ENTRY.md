# SCREEN 3: MOBILE NUMBER ENTRY SCREEN (ম'বাইল নম্বৰ দিয়ক / ꯃꯣꯕꯥꯏꯜ ꯅꯝꯕꯔ)

---

### 1. FILE STRUCTURE
- **Screen File**: `mobile-app/src/screens/auth/MobileEntryScreen.tsx`
- **Styles**: `mobile-app/src/screens/auth/styles/MobileEntryScreen.styles.ts`
- **Components**: `mobile-app/src/components/common/LargeNumericPad.tsx`, `mobile-app/src/components/common/FriendlyErrorBanner.tsx`
- **Hooks**: `mobile-app/src/hooks/usePhoneValidation.ts`
- **i18n Keys**: `mobile-app/src/locales/{en,as,mn,br,hi}/mobileEntry.json`
- **Unit & Snapshot Tests**: `mobile-app/src/screens/auth/__tests__/MobileEntryScreen.test.tsx`

---

### 2. COMPONENT HIERARCHY (React Native JSX Tree)
```jsx
<SafeAreaView style={styles.safeArea}>
  <StatusBar barStyle="light-content" backgroundColor="#121212" />
  <KeyboardAvoidingView 
    behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    style={styles.keyboardAvoid}
  >
    <View style={styles.container}>
      
      {/* Top Navigation Header with Back Arrow & Spoken Instructions */}
      <View style={styles.headerRow}>
        <TouchableOpacity
          style={styles.backButton}
          accessibilityRole="button"
          accessibilityLabel="উভতি যাওক (Go Back)"
          accessibilityHint="Returns to the role selection screen"
          onPress={() => navigation.goBack()}
        >
          <Text style={styles.backArrowText}>←</Text>
          <Text style={styles.backButtonLabel}>উভতি যাওক</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.speakerButton}
          accessibilityRole="button"
          accessibilityLabel="নিৰ্দেশনা শুনক (Listen to phone instructions)"
          onPress={handlePlayVoicePrompt}
        >
          <Text style={styles.speakerIcon}>🔊</Text>
        </TouchableOpacity>
      </View>

      {/* Instructional Title Container */}
      <View style={styles.instructionsContainer}>
        <Text style={styles.screenTitle} accessibilityRole="header">
          আপোনাৰ ম'বাইল নম্বৰ দিয়ক
        </Text>
        <Text style={styles.screenSubtitle}>
          Enter your 10-digit mobile number for secure sign in
        </Text>
        <Text style={styles.roleContextBadge}>
          {role === 'CAREGIVER' ? '🤝 যত্ন লওঁতা একাউণ্ট (Caregiver)' : '🩺 চিকিৎসক একাউণ্ট (Doctor)'}
        </Text>
      </View>

      {/* Large High-Contrast Phone Input Container */}
      <View style={styles.inputCard}>
        <Text style={styles.inputFieldLabel}>ম'বাইল নম্বৰ (Mobile Number)</Text>
        
        <View style={[styles.phoneInputRow, inputError ? styles.inputRowError : null]}>
          <View style={styles.countryCodeContainer}>
            <Text style={styles.flagEmoji}>🇮🇳</Text>
            <Text style={styles.countryCodeText}>+91</Text>
          </View>
          
          <TextInput
            style={styles.phoneTextInput}
            value={formattedPhoneNumber}
            onChangeText={handlePhoneChange}
            keyboardType="number-pad"
            maxLength={11} // "XXXXX XXXXX" (with space)
            placeholder="98765 43210"
            placeholderTextColor="#757575"
            accessible={true}
            accessibilityLabel="দহটা সংখ্যাৰ ম'বাইল নম্বৰ লিখক (Ten digit mobile number)"
            accessibilityHint="Type your ten digit phone number. Starts with 6, 7, 8, or 9."
            autoFocus={true}
            selectTextOnFocus={true}
          />
          
          {phoneNumber.length > 0 && (
            <TouchableOpacity
              style={styles.clearButton}
              accessibilityRole="button"
              accessibilityLabel="নম্বৰ মচি পেলাওক (Clear entered number)"
              onPress={handleClearInput}
            >
              <Text style={styles.clearButtonText}>✕</Text>
            </TouchableOpacity>
          )}
        </View>

        {/* Friendly Error Banner (Non-agitating green/amber, never harsh red) */}
        {inputError ? (
          <View 
            style={styles.friendlyErrorBox}
            accessible={true}
            accessibilityRole="alert"
            accessibilityLiveRegion="assertive"
          >
            <Text style={styles.errorIcon}>💡</Text>
            <Text style={styles.friendlyErrorText}>{inputError}</Text>
          </View>
        ) : (
          <Text style={styles.helperText}>
            আপোনাৰ নম্বৰলৈ এটা ৬-অংকৰ গোপন কোড (OTP) প্ৰেৰণ কৰা হ'ব।
          </Text>
        )}
      </View>

      {/* Offline Status Callout Container */}
      {!isOnline && (
        <View style={styles.offlineNoticeCard}>
          <Text style={styles.offlineIcon}>📴</Text>
          <View style={styles.offlineTextWrapper}>
            <Text style={styles.offlineTitle}>অফলাইন ম'ড সক্ৰিয় (Offline Mode)</Text>
            <Text style={styles.offlineDescription}>
              যদি আপোনাৰ ওচৰত আগৰ পিন (PIN) আছে, ইন্টাৰনেট নোহোৱাকৈ প্ৰৱেশ কৰিব পাৰিব।
            </Text>
          </View>
        </View>
      )}

      {/* Primary Action Button (Minimum 64px tall, high contrast) */}
      <View style={styles.actionContainer}>
        <TouchableOpacity
          style={[
            styles.submitButton,
            phoneNumber.length < 10 && styles.submitButtonDisabled
          ]}
          disabled={phoneNumber.length < 10 || isSubmitting}
          accessibilityRole="button"
          accessibilityLabel="অ'টিপি ক'ড প্ৰেৰণ কৰক (Send OTP Code)"
          accessibilityHint="Double tap to request a verification code to this phone number"
          onPress={handleRequestOTP}
          activeOpacity={0.8}
        >
          {isSubmitting ? (
            <ActivityIndicator color="#121212" size="small" />
          ) : (
            <Text style={styles.submitButtonText}>
              {isOnline ? "অ'টিপি ক'ড প্ৰেৰণ কৰক (Send OTP)" : "অফলাইন পিন ব্যৱহাৰ কৰক (Use Local PIN)"}
            </Text>
          )}
        </TouchableOpacity>
        
        <Text style={styles.privacyAssuranceText}>
          🔒 DPDA ২০২৩ অনুসৰি আপোনাৰ ফোন নম্বৰ কেতিয়াও ৰাজহুৱা কৰা নহয়।
        </Text>
      </View>

    </View>
  </KeyboardAvoidingView>
</SafeAreaView>
```

---

### 3. STATE MANAGEMENT (Redux Slice + Local State)
* **Global State (Redux `authSlice`)**:
  * `state.auth.phoneNumber`: Stores sanitized 10-digit number.
  * `state.auth.countryCode`: `'+91'`.
* **Local State (`useState`)**:
  * `const [phoneNumber, setPhoneNumber] = useState<string>('')`
  * `const [formattedPhoneNumber, setFormattedPhoneNumber] = useState<string>('')`
  * `const [inputError, setInputError] = useState<string | null>(null)`
  * `const [isSubmitting, setIsSubmitting] = useState<boolean>(false)`
* **Mount / Unmount Lifecycle**:
  * On mount: Subscribes to `NetInfo` to detect cellular/Wi-Fi reachability.
  * Checks if local encrypted PIN exists in `expo-secure-store` for this hardware. If a local PIN exists, displays secondary option: *"Use existing offline PIN"*.
* **Offline Handling**:
  * If the device is offline when the user taps "Send OTP", the app does not freeze. It presents:
  * *"ইন্টাৰনেট পোৱা নাই। আপুনি আপোনাৰ ৬-অংকৰ স্থানীয় পিন দি একাউণ্ট খুলিব পাৰে।"* (No internet detected. Enter your local 6-digit PIN to proceed.)

---

### 4. NAVIGATION PARAMS & ROUTING
* **Route Name**: `'MobileEntry'`
* **Params Received**: `{ role: 'CAREGIVER' | 'DOCTOR' }`
* **Params Passed to Next Screen**:
  * `navigation.navigate('OTPVerification', { role, phoneNumber: sanitizedNumber, isOfflineFallback: !isOnline })`
* **Navigation Action**: `navigation.navigate()` (Retains back stack so caregiver can return to role selection).
* **Deep Linking**: `sih26003://auth/mobile-entry?role=caregiver`

---

### 5. ACCESSIBILITY SPECIFICATION (MANDATORY SECTION)

```typescript
export const mobileEntryAccessibilityConfig = {
  backButton: {
    accessibilityRole: 'button' as const,
    accessibilityLabel: "উভতি যাওক। Go back to role selection.",
  },
  phoneInput: {
    accessibilityRole: 'text' as const,
    accessibilityLabel: "ম'বাইল নম্বৰ ইনপুট। Enter 10-digit Indian phone number without plus 91.",
    accessibilityHint: "Numeric keypad will open. Enter digits slowly.",
  },
  errorAlert: {
    accessibilityRole: 'alert' as const,
    accessibilityLiveRegion: 'assertive' as const,
  },
  submitButton: {
    accessibilityRole: 'button' as const,
    accessibilityLabel: "অ'টিপি ক'ড প্ৰেৰণ কৰক। Proceed to verification code screen.",
  }
};
```

* **Focus Order (Tab Sequence)**:
  1. Back Button (`backButton`)
  2. Voice Instructions Button (`speakerButton`)
  3. Header Title (`screenTitle`)
  4. Phone Number Input (`phoneTextInput`)
  5. Clear Input Button (if visible)
  6. Submit Action Button (`submitButton`)
* **Screen Reader TalkBack Announcements**:
  * On Load: *"আপোনাৰ ম'বাইল নম্বৰ দিয়ক। ম'বাইল ইনপুট ফিল্ডত ফ'কাচ হৈছে।"*
  * On Error: TalkBack immediately reads friendly error: *"অনুগ্ৰহ কৰি সঠিক ১০-টা সংখ্যাৰ নম্বৰ দিয়ক।"*
* **Touch Target Sizes**:
  * Back Button: $120 \times 52\text{ px}$.
  * Phone Input Row: Height $72\text{ px}$ (Enormous hit target for arthritic fingers).
  * Submit Button: Height $64\text{ px}$, width $100\%$ ($>320\text{ px}$).

---

### 6. OFFLINE BEHAVIOR SPECIFICATION (CRITICAL)
* **Render without internet**: Renders 100% normally.
* **User actions while offline**:
  * If the user previously set a Caregiver PIN on this device: Bypasses cloud SMS gateway and transitions to local PIN challenge.
  * If a first-time user has no internet: App queues registration intent in `sync_queue` and permits local offline onboarding via a 6-digit local PIN.
* **Error states**: Soft amber alert banner explaining offline behavior:
  * *"ইন্টাৰনেট সংযোগ নাই। আপুনি অফলাইন পিনেৰে কাম আৰম্ভ কৰিব পাৰিব। (No network connection. You can start using local offline PIN.)"*
* **Data persistence**: Phone number is hashed with SHA-256 and stored in volatile memory until OTP completes. Never written to logs in plain text.
* **Sync indicator**: Prominent status indicator card when offline.

---

### 7. I18N (INTERNATIONALIZATION) KEYS

```json
{
  "en": {
    "mobileEntry": {
      "back": "Back",
      "title": "Enter your Mobile Number",
      "subtitle": "Enter your 10-digit number for secure sign in",
      "caregiverBadge": "Caregiver Account",
      "doctorBadge": "Doctor Account",
      "phoneLabel": "Mobile Number",
      "helperText": "A 6-digit verification code (OTP) will be sent to this number.",
      "sendOtp": "Send OTP Code",
      "usePin": "Use Local Offline PIN",
      "errInvalidLength": "Please enter a complete 10-digit phone number.",
      "errInvalidPrefix": "Indian mobile numbers start with 6, 7, 8, or 9.",
      "offlineNotice": "Offline Mode: Enter your local PIN if previously registered.",
      "privacyNotice": "Under DPDA 2023, your number is stored with encryption."
    }
  },
  "as": {
    "mobileEntry": {
      "back": "উভতি যাওক",
      "title": "আপোনাৰ ম'বাইল নম্বৰ দিয়ক",
      "subtitle": "সুৰক্ষিত প্ৰৱেশৰ বাবে ১০-টা সংখ্যাৰ নম্বৰ দিয়ক",
      "caregiverBadge": "যত্ন লওঁতা একাউণ্ট",
      "doctorBadge": "চিকিৎসক একাউণ্ট",
      "phoneLabel": "ম'বাইল নম্বৰ",
      "helperText": "আপোনাৰ নম্বৰলৈ এটা ৬-অংকৰ গোপন কোড (OTP) প্ৰেৰণ কৰা হ'ব।",
      "sendOtp": "অ'টিপি ক'ড প্ৰেৰণ কৰক",
      "usePin": "অফলাইন পিন ব্যৱহাৰ কৰক",
      "errInvalidLength": "অনুগ্ৰহ কৰি সম্পূৰ্ণ ১০-টা সংখ্যাৰ ম'বাইল নম্বৰ দিয়ক।",
      "errInvalidPrefix": "ম'বাইল নম্বৰ সাধাৰণতে ৬, ৭, ৮ বা ৯ ৰে আৰম্ভ হয়।",
      "offlineNotice": "অফলাইন ম'ড: পূৰ্বৰ পিন ব্যৱহাৰ কৰি প্ৰৱেশ কৰক।",
      "privacyNotice": "DPDA ২০২৩ নীতি অনুসৰি আপোনাৰ ফোন নম্বৰ গোপনে সংৰক্ষিত থাকে।"
    }
  },
  "mn": {
    "mobileEntry": {
      "back": "ꯍꯟꯖꯤꯜꯂꯨ",
      "title": "ꯅꯍꯥꯛꯀꯤ ꯃꯣꯕꯥꯏꯜ ꯅꯝꯕꯔ ꯊꯥꯕꯤꯌꯨ",
      "subtitle": "ꯅꯝꯕꯔ ꯱꯰ ꯌꯥꯎꯕ ꯃꯣꯕꯥꯏꯜ ꯅꯝꯕꯔ ꯊꯥꯕꯤꯌꯨ",
      "caregiverBadge": "ꯌꯦꯡꯁꯤꯅꯕꯤꯕꯒꯤ ꯑꯦꯀꯥꯎꯟꯇ",
      "doctorBadge": "ꯗꯥꯛꯇꯔ ꯑꯦꯀꯥꯎꯟꯇ",
      "phoneLabel": "ꯃꯣꯕꯥꯏꯜ ꯅꯝꯕꯔ",
      "helperText": "ꯑꯣꯇꯤꯄꯤ (OTP) ꯀꯣꯗ ꯊꯥꯔꯛꯀꯅꯤ।",
      "sendOtp": "ꯑꯣꯇꯤꯄꯤ ꯊꯥꯕꯤꯌꯨ",
      "usePin": "ꯑꯣꯐꯂꯥꯏꯟ ꯄꯤꯟ ꯁꯤꯖꯤꯟꯅꯕꯤꯌꯨ",
      "errInvalidLength": "ꯆꯥꯅꯕꯤꯗꯨꯅꯥ ꯅꯝꯕꯔ ꯱꯰ ꯃꯄꯨꯡ ꯐꯥꯅꯥ ꯊꯥꯕꯤꯌꯨ।",
      "errInvalidPrefix": "ꯃꯣꯕꯥꯏꯜ ꯅꯝꯕꯔ ꯶, ꯷, ꯸ ꯅꯠꯇ꯭ꯔꯒ ꯹ ꯗꯒꯤ ꯍꯧꯒꯗꯕꯅꯤ।",
      "offlineNotice": "ꯑꯣꯐꯂꯥꯏꯟ ꯃꯣꯗ: ꯄꯤꯟ ꯊꯥꯗꯨꯅꯥ ꯆꯪꯕꯤꯌꯨ।",
      "privacyNotice": "DPDA 2023 ꯃꯇꯨꯡ ꯏꯟꯅꯥ ꯅꯍꯥꯛꯀꯤ ꯅꯝꯕꯔ ꯂꯣꯠꯅꯥ ꯊꯝꯃꯤ।"
    }
  },
  "br": {
    "mobileEntry": {
      "back": "गाहाय आव थां",
      "title": "नोंथांनि मबाइल नम्बरखौ सो",
      "subtitle": "रैखाथि गोनां हाबनो थाखाय जि-अनजिमानि नम्बर सो",
      "caregiverBadge": "नायदिंथिग्रा एकाउन्ट",
      "doctorBadge": "डाक्टर एकाउन्ट",
      "phoneLabel": "मबाइल नम्बर",
      "helperText": "मोनसे 6-अनजिमानि OTP नम्बर दैथायहरगोन।",
      "sendOtp": "OTP दैथायहर",
      "usePin": "अफलाइन पिन बाहाय",
      "errInvalidLength": "अननानै गासै 10 अनजिमानि नम्बर सो।",
      "errInvalidPrefix": "मबाइल नम्बरा 6, 7, 8 एबा 9 जों जागायो।",
      "offlineNotice": "अफलाइन राहा: सिगांनि पिन बाहायना हाब।",
      "privacyNotice": "DPDA 2023 नि बादियै नोंनि नम्बरा रैखाथि गोनां।"
    }
  },
  "hi": {
    "mobileEntry": {
      "back": "वापस जाएं",
      "title": "अपना मोबाइल नंबर दर्ज करें",
      "subtitle": "सुरक्षित प्रवेश के लिए 10 अंकों का नंबर दर्ज करें",
      "caregiverBadge": "देखभालकर्ता खाता",
      "doctorBadge": "डॉक्टर खाता",
      "phoneLabel": "मोबाइल नंबर",
      "helperText": "आपके नंबर पर 6 अंकों का गुप्त कोड (OTP) भेजा जाएगा।",
      "sendOtp": "ओटीपी कोड भेजें",
      "usePin": "स्थानीय पिन का उपयोग करें",
      "errInvalidLength": "कृपया पूरा 10 अंकों का मोबाइल नंबर दर्ज करें।",
      "errInvalidPrefix": "भारतीय मोबाइल नंबर 6, 7, 8 या 9 से शुरू होते हैं।",
      "offlineNotice": "ऑफ़लाइन मोड: अपने स्थानीय पिन का उपयोग करके प्रवेश करें।",
      "privacyNotice": "DPDA 2023 के तहत आपका नंबर पूरी तरह सुरक्षित है।"
    }
  }
}
```

---

### 8. STYLESHEET (`MobileEntryScreen.styles.ts`)

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
  container: {
    flex: 1,
    paddingHorizontal: 20,
    paddingVertical: 16,
    justifyContent: 'space-between',
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  backButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#262626',
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 20,
    minHeight: 48,
    borderWidth: 1,
    borderColor: '#424242',
  },
  backArrowText: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#FFD700',
    marginRight: 8,
  },
  backButtonLabel: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFFFFF',
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
  instructionsContainer: {
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
    fontSize: 17,
    color: '#E0E0E0',
    lineHeight: 24,
    marginBottom: 10,
  },
  roleContextBadge: {
    fontSize: 14,
    fontWeight: '700',
    color: '#64B5F6',
    backgroundColor: '#0D273D',
    alignSelf: 'flex-start',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#1E88E5',
  },
  inputCard: {
    backgroundColor: '#1E1E1E',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 20,
    padding: 20,
    marginBottom: 16,
  },
  inputFieldLabel: {
    fontSize: 18,
    fontWeight: '700',
    color: '#FFFFFF',
    marginBottom: 12,
  },
  phoneInputRow: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#121212',
    borderWidth: 2,
    borderColor: '#FFD700',
    borderRadius: 16,
    paddingHorizontal: 16,
    minHeight: 72, // Generous height for arthritic touch
  },
  inputRowError: {
    borderColor: '#FFA000', // Amber error border
  },
  countryCodeContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    borderRightWidth: 1.5,
    borderRightColor: '#424242',
    paddingRight: 12,
    marginRight: 12,
  },
  flagEmoji: {
    fontSize: 24,
    marginRight: 6,
  },
  countryCodeText: {
    fontSize: 22,
    fontWeight: '800',
    color: '#FFFFFF',
  },
  phoneTextInput: {
    flex: 1,
    fontSize: 24,
    fontWeight: '800',
    color: '#FFD700',
    letterSpacing: 2,
  },
  clearButton: {
    padding: 8,
    justifyContent: 'center',
    alignItems: 'center',
  },
  clearButtonText: {
    fontSize: 20,
    color: '#9E9E9E',
    fontWeight: 'bold',
  },
  helperText: {
    fontSize: 15,
    color: '#BDBDBD',
    marginTop: 12,
    lineHeight: 22,
  },
  friendlyErrorBox: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#332400',
    borderWidth: 1.5,
    borderColor: '#FFB300',
    borderRadius: 12,
    padding: 12,
    marginTop: 12,
  },
  errorIcon: {
    fontSize: 18,
    marginRight: 8,
  },
  friendlyErrorText: {
    flex: 1,
    fontSize: 15,
    fontWeight: '600',
    color: '#FFE082',
    lineHeight: 20,
  },
  offlineNoticeCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1C2E20',
    borderWidth: 1.5,
    borderColor: '#81C784',
    borderRadius: 16,
    padding: 14,
    marginBottom: 16,
  },
  offlineIcon: {
    fontSize: 28,
    marginRight: 12,
  },
  offlineTextWrapper: {
    flex: 1,
  },
  offlineTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: '#A5D6A7',
    marginBottom: 2,
  },
  offlineDescription: {
    fontSize: 14,
    color: '#E8F5E9',
    lineHeight: 18,
  },
  actionContainer: {
    paddingTop: 10,
  },
  submitButton: {
    backgroundColor: '#FFD700', // Gold Primary CTA
    borderRadius: 20,
    paddingVertical: 18,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 64,
  },
  submitButtonDisabled: {
    backgroundColor: '#424242',
  },
  submitButtonText: {
    fontSize: 20,
    fontWeight: '800',
    color: '#121212',
  },
  privacyAssuranceText: {
    fontSize: 12,
    color: '#757575',
    textAlign: 'center',
    marginTop: 12,
    lineHeight: 16,
  },
});
```

---

### 9. LIFECYCLE METHODS & SIDE EFFECTS
* **Input Formatting & Validation**:
  ```typescript
  const handlePhoneChange = (text: string) => {
    // Strip non-numeric characters
    const cleaned = text.replace(/[^0-9]/g, '');
    setPhoneNumber(cleaned);

    // Format with space: "XXXXX XXXXX" for ease of reading by elderly eyes
    if (cleaned.length > 5) {
      setFormattedPhoneNumber(`${cleaned.slice(0, 5)} ${cleaned.slice(5, 10)}`);
    } else {
      setFormattedPhoneNumber(cleaned);
    }

    // Dynamic error prevention
    if (cleaned.length > 0 && !['6', '7', '8', '9'].includes(cleaned[0])) {
      setInputError('ভাৰতীয় ম'বাইল নম্বৰ ৬, ৭, ৮ বা ৯ ৰে আৰম্ভ হয়। (Indian numbers start with 6-9)');
    } else if (cleaned.length > 0 && cleaned.length < 10) {
      setInputError(null); // Silent while user is typing
    } else {
      setInputError(null);
    }
  };
  ```

---

### 10. TEST SCENARIOS (Manual QA Checklist)
1. **Valid Number Entry**: Typing "9876543210" formats to "98765 43210", enables button.
2. **Invalid Prefix**: Typing "123..." displays friendly helper note explaining 6-9 prefix.
3. **Pasting Number**: Pasting "+91 98765 43210" or "09876543210" strips symbols cleanly to 10 digits.
4. **Offline Mode**: Airplane mode active; button changes label to "Use Local PIN".
5. **Clear Button**: Tapping "✕" wipes input and resets formatting cleanly.
6. **Voice Instruction**: Speaker button speaks phone instructions in chosen dialect.
7. **Motor Tremor Tap**: Rapid duplicate taps on Submit button trigger only one network/OTP dispatch.
8. **TalkBack Screen Reader**: Reads country code +91 followed by entered digits.
9. **Font Scaling (200%)**: Input text scales up without overflowing container bounds.
10. **Keyboard Dismiss**: Tapping background dismisses keyboard cleanly.

---

### 11. DATA MODEL IMPACT
* **Tables Affected**: `caregivers` (Prepared for authentication).
* **Privacy**: Plain-text phone number is **never saved to SQLite**. Only `contact_hash = SHA256("+91" + phoneNumber)` is persisted.
* **Sync Queue**: If offline, writes `SEND_OTP_REQUEST` to `sync_queue` with priority 5.
