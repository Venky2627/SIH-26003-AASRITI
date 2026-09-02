# SCREEN 4: OTP VERIFICATION SCREEN (অ'টিপি সত্যাপন / ꯑꯣꯇꯤꯄꯤ ꯆꯦꯛ ꯇꯧꯕ)

---

### 1. FILE STRUCTURE
- **Screen File**: `mobile-app/src/screens/auth/OTPVerificationScreen.tsx`
- **Styles**: `mobile-app/src/screens/auth/styles/OTPVerificationScreen.styles.ts`
- **Components**: `mobile-app/src/components/common/OTPInputBoxes.tsx`, `mobile-app/src/components/common/ResendCountdownTimer.tsx`
- **Hooks**: `mobile-app/src/hooks/useSMSListener.ts`, `mobile-app/src/hooks/useOTPTimer.ts`
- **i18n Keys**: `mobile-app/src/locales/{en,as,mn,br,hi}/otpVerification.json`
- **Unit & Snapshot Tests**: `mobile-app/src/screens/auth/__tests__/OTPVerificationScreen.test.tsx`

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
      
      {/* Top Bar with Back Arrow and Change Number Shortcut */}
      <View style={styles.topBar}>
        <TouchableOpacity
          style={styles.backButton}
          accessibilityRole="button"
          accessibilityLabel="উভতি যাওক (Go Back)"
          onPress={() => navigation.goBack()}
        >
          <Text style={styles.backArrow}>←</Text>
          <Text style={styles.backText}>উভতি যাওক</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.speakerButton}
          accessibilityRole="button"
          accessibilityLabel="নিৰ্দেশনা শুনক (Listen to OTP instructions in Assamese)"
          onPress={handlePlayVoicePrompt}
        >
          <Text style={styles.speakerIcon}>🔊</Text>
        </TouchableOpacity>
      </View>

      {/* Screen Title & Target Phone Info */}
      <View style={styles.headerContainer}>
        <Text style={styles.title} accessibilityRole="header">
          গোপন ক'ড (OTP) লিখক
        </Text>
        <Text style={styles.subtitle}>
          Enter the 6-digit verification code sent to your phone
        </Text>

        <View style={styles.targetPhoneBadge}>
          <Text style={styles.targetPhoneText}>+91 {maskedPhoneNumber}</Text>
          <TouchableOpacity
            style={styles.editNumberBtn}
            accessibilityRole="button"
            accessibilityLabel="ফোন নম্বৰ সলনি কৰক (Change phone number)"
            accessibilityHint="Takes you back to edit the phone number"
            onPress={() => navigation.goBack()}
          >
            <Text style={styles.editNumberText}>সলনি কৰক (Edit)</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* 6-Digit OTP Box Grid (Large, 56x64 px boxes for easy viewing) */}
      <View style={styles.otpGridCard}>
        <View 
          style={styles.otpBoxesRow}
          accessible={true}
          accessibilityRole="none"
          accessibilityLabel={`Entered OTP: ${otpCode.split('').join(' ')}`}
        >
          {[0, 1, 2, 3, 4, 5].map((index) => {
            const digit = otpCode[index] || '';
            const isFocused = otpCode.length === index;
            return (
              <View 
                key={index}
                style={[
                  styles.otpDigitBox,
                  isFocused && styles.otpDigitBoxFocused,
                  digit ? styles.otpDigitBoxFilled : null,
                  hasError ? styles.otpDigitBoxError : null
                ]}
              >
                <Text style={styles.otpDigitText}>{digit}</Text>
              </View>
            );
          })}
        </View>

        {/* Hidden Master TextInput for Native/Accessible Keyboard Handling */}
        <TextInput
          ref={hiddenInputRef}
          value={otpCode}
          onChangeText={handleOtpChange}
          keyboardType="number-pad"
          maxLength={6}
          style={styles.hiddenTextInput}
          autoFocus={true}
          accessible={true}
          accessibilityLabel="৬-অংকৰ গোপন কোড লিখক (Six digit verification code input)"
        />

        {/* Friendly Error Banner */}
        {hasError && (
          <View style={styles.errorBox} accessible={true} accessibilityRole="alert">
            <Text style={styles.errorIcon}>💡</Text>
            <Text style={styles.errorText}>
              কোডটো নিমিলিল। অনুগ্ৰহ কৰি পুনৰ পৰীক্ষা কৰক। (Code did not match. Please re-check.)
            </Text>
          </View>
        )}

        {/* Extended Elderly-Friendly Timer Notice (10-Minute Expiry) */}
        <View style={styles.timerRow}>
          <Text style={styles.timerNotice}>
            ⏳ কোডৰ ম্যাদ: <Text style={styles.timerHighlight}>{formatTimer(timerSeconds)}</Text>
          </Text>
        </View>
      </View>

      {/* Resend Code Options */}
      <View style={styles.resendContainer}>
        {canResend ? (
          <TouchableOpacity
            style={styles.resendActiveBtn}
            accessibilityRole="button"
            accessibilityLabel="কোড পুনৰ প্ৰেৰণ কৰক (Resend verification code)"
            onPress={handleResendCode}
          >
            <Text style={styles.resendActiveText}>🔄 কোড পুনৰ প্ৰেৰণ কৰক (Resend Code)</Text>
          </TouchableOpacity>
        ) : (
          <Text style={styles.resendDisabledText}>
            পুনৰ কোড পাবলৈ অপেক্ষা কৰক: {timerSeconds}s
          </Text>
        )}

        <TouchableOpacity
          style={styles.callSupportBtn}
          accessibilityRole="button"
          accessibilityLabel="কণ্ঠযোগে কোড শুনক (Receive code via voice call)"
          onPress={handleRequestVoiceCallOTP}
        >
          <Text style={styles.callSupportText}>📞 ফোন কলযোগে কোড শুনক (Voice Call OTP)</Text>
        </TouchableOpacity>
      </View>

      {/* Primary Verification Button */}
      <View style={styles.footerContainer}>
        <TouchableOpacity
          style={[
            styles.verifyBtn,
            otpCode.length < 6 && styles.verifyBtnDisabled
          ]}
          disabled={otpCode.length < 6 || isVerifying}
          accessibilityRole="button"
          accessibilityLabel="কোড সত্যাপন কৰক আৰু আগবাঢ়ক (Verify Code and Proceed)"
          onPress={handleVerifyOTP}
          activeOpacity={0.8}
        >
          {isVerifying ? (
            <ActivityIndicator color="#121212" size="small" />
          ) : (
            <Text style={styles.verifyBtnText}>সত্যাপন কৰক (Verify & Continue)</Text>
          )}
        </TouchableOpacity>
      </View>

    </View>
  </KeyboardAvoidingView>
</SafeAreaView>
```

---

### 3. STATE MANAGEMENT (Redux Slice + Local State)
* **Global State (Redux `authSlice`)**:
  * Action: `dispatch(verifySuccess({ token, caregiverId }))`.
* **Local State (`useState`)**:
  * `const [otpCode, setOtpCode] = useState<string>('')`
  * `const [timerSeconds, setTimerSeconds] = useState<number>(600)` (10-minute timer for elderly users who type with slow motor speed).
  * `const [canResend, setCanResend] = useState<boolean>(false)`
  * `const [hasError, setHasError] = useState<boolean>(false)`
  * `const [isVerifying, setIsVerifying] = useState<boolean>(false)`
* **Mount / Unmount Behavior**:
  * On mount: Starts 10-minute countdown interval timer.
  * In Android production: Hooks native SMS Retriever API (`useSMSListener`) to auto-fill code upon incoming SMS without user manual entry.
  * On unmount: Clears interval timer and unregisters SMS receiver.

---

### 4. NAVIGATION PARAMS & ROUTING
* **Route Name**: `'OTPVerification'`
* **Params Received**:
  ```typescript
  interface OTPRouteParams {
    role: 'CAREGIVER' | 'DOCTOR';
    phoneNumber: string;
    isOfflineFallback?: boolean;
  }
  ```
* **Params Passed to Next Screen**:
  * If Caregiver is new: `navigation.replace('CaregiverProfile', { phoneNumber, role })`
  * If Caregiver already has profile: `navigation.replace('CaregiverDashboard', { caregiverId })`
* **Navigation Action**: `navigation.replace()` (Destroys OTP screen from back stack so user cannot return to it after authenticating).

---

### 5. ACCESSIBILITY SPECIFICATION (MANDATORY SECTION)

```typescript
export const otpAccessibilityConfig = {
  header: {
    accessibilityRole: 'header' as const,
    accessibilityLabel: "অ'টিপি সত্যাপন পৃষ্ঠা। Enter 6-digit verification code.",
  },
  otpBoxGroup: {
    accessibilityRole: 'none' as const,
    accessibilityLiveRegion: 'polite' as const,
  },
  verifyButton: {
    accessibilityRole: 'button' as const,
    accessibilityLabel: "সত্যাপন কৰক। Verify and continue to profile.",
  }
};
```

* **Focus Order (Tab Sequence)**:
  1. Back Button (`backButton`)
  2. Voice Instructions (`speakerButton`)
  3. Change Phone Number Link (`editNumberBtn`)
  4. OTP Digit Input (`hiddenTextInput`)
  5. Resend Code Button (`resendActiveBtn`)
  6. Verify Action Button (`verifyBtn`)
* **Screen Reader TalkBack Announcements**:
  * On Load: *"গোপন কোড লিখক। ৬-টা খালী ঘৰ আছে। সংখ্যা টাইপ কৰক।"*
  * On Auto-fill: *"কোড সফলভাৱে চিনাক্ত হ'ল। সত্যাপন বুটামত টিপক।"*
* **Touch Target Sizes**:
  * Individual OTP Digit Display Boxes: $56\text{ px width} \times 68\text{ px height}$ with $8\text{ px}$ margins.
  * Change Number Link: $110 \times 48\text{ px}$.
  * Verify Button: Height $64\text{ px}$, width $100\%$.

---

### 6. OFFLINE BEHAVIOR SPECIFICATION (CRITICAL)
* **Render without internet**: Renders normally.
* **Offline Authentication Fallback (Local PIN Mode)**:
  * In zero-connectivity mode, screen transforms title: *"স্থানীয় অফলাইন পিন দিয়ক (Enter Local PIN)"*.
  * Compares entered 6 digits against PBKDF2 hash stored inside `expo-secure-store`.
  * If hash matches: Grants immediate access to local SQLite records with zero network calls.
* **Error states**: Non-blaming message:
  * *"কোড নিমিলিল। আন এটা কোড বিচাৰিব পাৰে। (Code did not match. You can request another code.)"*
* **Data persistence**: Authenticated state sets `auth_status = 'AUTHENTICATED'` in `expo-secure-store`.

---

### 7. I18N (INTERNATIONALIZATION) KEYS

```json
{
  "en": {
    "otp": {
      "title": "Enter Verification Code",
      "subtitle": "Enter the 6-digit code sent to your phone",
      "changeNumber": "Change",
      "timerLabel": "Code expires in:",
      "resend": "Resend Code",
      "voiceCall": "Get Code via Voice Call",
      "verify": "Verify & Continue",
      "errMismatch": "The code you entered does not match. Please re-check.",
      "offlinePinTitle": "Enter Local 6-Digit PIN"
    }
  },
  "as": {
    "otp": {
      "title": "গোপন ক'ড (OTP) লিখক",
      "subtitle": "আপোনাৰ ফোনলৈ প্ৰেৰণ কৰা ৬-অংকৰ ক'ডটো লিখক",
      "changeNumber": "সলনি কৰক",
      "timerLabel": "কোডৰ ম্যাদ আছে:",
      "resend": "কোড পুনৰ প্ৰেৰণ কৰক",
      "voiceCall": "ফোন কলযোগে কোড শুনক",
      "verify": "সত্যাপন কৰক",
      "errMismatch": "কোডটো নিমিলিল। অনুগ্ৰহ কৰি পুনৰ পৰীক্ষা কৰক।",
      "offlinePinTitle": "স্থানীয় ৬-অংকৰ পিন লিখক"
    }
  },
  "mn": {
    "otp": {
      "title": "ꯑꯣꯇꯤꯄꯤ (OTP) ꯀꯣꯗ ꯊꯥꯕꯤꯌꯨ",
      "subtitle": "ꯅꯍꯥꯛꯀꯤ ꯐꯣꯟꯗꯥ ꯊꯥꯔꯛꯄꯥ ꯑꯣꯇꯤꯄꯤ ꯀꯣꯗ ꯊꯥꯕꯤꯌꯨ",
      "changeNumber": "ꯍꯣꯡꯗꯣꯛꯎ",
      "timerLabel": "ꯀꯣꯗ ꯃꯇꯝ ꯂꯩꯔꯤ:",
      "resend": "ꯀꯣꯗ ꯑꯃꯨꯛ ꯍꯟꯅꯥ ꯊꯥꯕꯤꯌꯨ",
      "voiceCall": "ꯀꯣꯜ ꯇꯧꯗꯨꯅꯥ ꯇꯥꯕꯤꯌꯨ",
      "verify": "ꯆꯦꯛ ꯇꯧꯔꯒꯥ ꯃꯈꯥ ꯆꯠꯊꯕꯤꯌꯨ",
      "errMismatch": "ꯀꯣꯗ ꯃꯥꯟꯅꯗꯦ। ꯑꯃꯨꯛ ꯍꯟꯅꯥ ꯌꯦꯡꯕꯤꯌꯨ।",
      "offlinePinTitle": "ꯑꯣꯐꯂꯥꯏꯟ ꯄꯤꯟ ꯊꯥꯕꯤꯌꯨ"
    }
  },
  "br": {
    "otp": {
      "title": "OTP कोडखौ सो",
      "subtitle": "नोंथांनि मबाइलाव थांनाय 6-अनजिमानि कोडखौ सो",
      "changeNumber": "सोलाय",
      "timerLabel": "कोडनि सम दं:",
      "resend": "कोडखौ फिन दैथायहर",
      "voiceCall": "कल खालामनानै खनासं",
      "verify": "आनजाद खालाम",
      "errMismatch": "कोडआ गोरोबथियाखै। फिन नायफिन।",
      "offlinePinTitle": "थावनि 6-अनजिमानि पिन सो"
    }
  },
  "hi": {
    "otp": {
      "title": "सत्यापन कोड (OTP) दर्ज करें",
      "subtitle": "आपके फोन पर भेजा गया 6 अंकों का कोड दर्ज करें",
      "changeNumber": "बदलें",
      "timerLabel": "कोड की वैधता:",
      "resend": "कोड पुनः भेजें",
      "voiceCall": "वॉइस कॉल से कोड प्राप्त करें",
      "verify": "सत्यापित करें और आगे बढ़ें",
      "errMismatch": "कोड मेल नहीं खाया। कृपया पुनः जाँच करें।",
      "offlinePinTitle": "स्थानीय 6-अंकीय पिन दर्ज करें"
    }
  }
}
```

---

### 8. STYLESHEET (`OTPVerificationScreen.styles.ts`)

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
  topBar: {
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
  },
  backArrow: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#FFD700',
    marginRight: 8,
  },
  backText: {
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
  headerContainer: {
    marginBottom: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: '800',
    color: '#FFD700',
    marginBottom: 6,
    fontFamily: Platform.select({ android: 'NotoSansBengali-Bold', ios: 'System' }),
  },
  subtitle: {
    fontSize: 16,
    color: '#E0E0E0',
    lineHeight: 22,
    marginBottom: 12,
  },
  targetPhoneBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1E1E1E',
    borderWidth: 1.5,
    borderColor: '#64B5F6',
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 14,
    alignSelf: 'flex-start',
  },
  targetPhoneText: {
    fontSize: 16,
    fontWeight: '800',
    color: '#FFFFFF',
    marginRight: 12,
    letterSpacing: 1,
  },
  editNumberBtn: {
    backgroundColor: '#0D47A1',
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 8,
  },
  editNumberText: {
    fontSize: 13,
    fontWeight: '700',
    color: '#90CAF9',
  },
  otpGridCard: {
    backgroundColor: '#1E1E1E',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 20,
    padding: 20,
    alignItems: 'center',
  },
  otpBoxesRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '100%',
    marginBottom: 16,
  },
  otpDigitBox: {
    width: 46,
    height: 64,
    borderRadius: 12,
    borderWidth: 2,
    borderColor: '#616161',
    backgroundColor: '#121212',
    alignItems: 'center',
    justifyContent: 'center',
  },
  otpDigitBoxFocused: {
    borderColor: '#FFD700',
    backgroundColor: '#1A1800',
  },
  otpDigitBoxFilled: {
    borderColor: '#00E676',
  },
  otpDigitBoxError: {
    borderColor: '#FFA000',
  },
  otpDigitText: {
    fontSize: 28,
    fontWeight: '800',
    color: '#FFD700',
  },
  hiddenTextInput: {
    position: 'absolute',
    opacity: 0,
    width: 1,
    height: 1,
  },
  errorBox: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#332400',
    borderWidth: 1.5,
    borderColor: '#FFB300',
    borderRadius: 12,
    padding: 12,
    marginBottom: 12,
    width: '100%',
  },
  errorIcon: {
    fontSize: 18,
    marginRight: 8,
  },
  errorText: {
    flex: 1,
    fontSize: 14,
    fontWeight: '600',
    color: '#FFE082',
    lineHeight: 18,
  },
  timerRow: {
    marginTop: 6,
  },
  timerNotice: {
    fontSize: 15,
    color: '#BDBDBD',
  },
  timerHighlight: {
    fontWeight: '800',
    color: '#FFD700',
  },
  resendContainer: {
    alignItems: 'center',
    marginVertical: 12,
  },
  resendActiveBtn: {
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 12,
    backgroundColor: '#262626',
    borderWidth: 1,
    borderColor: '#FFD700',
    marginBottom: 10,
    minHeight: 48,
    justifyContent: 'center',
  },
  resendActiveText: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFD700',
  },
  resendDisabledText: {
    fontSize: 14,
    color: '#757575',
    marginBottom: 10,
  },
  callSupportBtn: {
    paddingVertical: 6,
  },
  callSupportText: {
    fontSize: 14,
    color: '#90CAF9',
    textDecorationLine: 'underline',
  },
  footerContainer: {
    paddingTop: 10,
  },
  verifyBtn: {
    backgroundColor: '#FFD700',
    borderRadius: 20,
    paddingVertical: 18,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 64,
  },
  verifyBtnDisabled: {
    backgroundColor: '#424242',
  },
  verifyBtnText: {
    fontSize: 20,
    fontWeight: '800',
    color: '#121212',
  },
});
```

---

### 9. LIFECYCLE METHODS & SIDE EFFECTS
* **10-Minute Timer & Auto-Submit**:
  ```typescript
  useEffect(() => {
    const timer = setInterval(() => {
      setTimerSeconds(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          setCanResend(true);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const handleOtpChange = (code: string) => {
    const numericOnly = code.replace(/[^0-9]/g, '');
    setOtpCode(numericOnly);
    setHasError(false);

    // Auto-trigger verification when 6th digit entered
    if (numericOnly.length === 6) {
      triggerVerification(numericOnly);
    }
  };
  ```

---

### 10. TEST SCENARIOS (Manual QA Checklist)
1. **Auto-Submit on 6 Digits**: Entering 6th digit triggers verification automatically.
2. **Incorrect Code**: Entering wrong digits renders friendly amber card; does not clear input completely so user can fix last digit.
3. **Change Number Button**: Tapping "Edit" returns to `MobileEntry` with phone number preserved in input field.
4. **Resend Timer Expiry**: Resend button enables after timer finishes; tapping it restarts 10-min countdown.
5. **Slow Typing (3+ Minutes)**: Code does not expire prematurely during slow typing.
6. **Android SMS Auto-Read**: Incoming SMS automatically populates the 6 boxes via SMS Retriever.
7. **Offline Mode Fallback**: Entering 6-digit local PIN unlocks without network.
8. **TalkBack Readout**: Screen reader announces: *"৬-অংকৰ কোড লিখক।"*.
9. **Backspace Handling**: Pressing delete removes previous digit cleanly.
10. **Voice Call Fallback**: Tapping Voice Call requests telephony TTS trigger.

---

### 11. DATA MODEL IMPACT
* **Tables Affected**: `caregivers` (Updates `is_active = 1`).
* **Tokens**: JWT access token cached in `expo-secure-store`.
* **Sync Queue**: Does not generate sync actions.
