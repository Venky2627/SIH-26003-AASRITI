# SCREEN 2: ROLE SELECTION SCREEN (ভূমিকা বাছনি / ꯊꯧꯗꯥꯡ ꯈꯅꯕ)

---

### 1. FILE STRUCTURE
- **Screen File**: `mobile-app/src/screens/auth/RoleSelectionScreen.tsx`
- **Styles**: `mobile-app/src/screens/auth/styles/RoleSelectionScreen.styles.ts`
- **Components**: `mobile-app/src/components/common/RoleCard.tsx`, `mobile-app/src/components/common/SpokenAudioButton.tsx`
- **i18n Keys**: `mobile-app/src/locales/{en,as,mn,br,hi}/roleSelection.json`
- **Unit & Snapshot Tests**: `mobile-app/src/screens/auth/__tests__/RoleSelectionScreen.test.tsx`

---

### 2. COMPONENT HIERARCHY (React Native JSX Tree)
```jsx
<SafeAreaView style={styles.safeArea}>
  <StatusBar barStyle="light-content" backgroundColor="#121212" />
  <View style={styles.container}>
    
    {/* Universal Accessible Top Bar with Audio Speaker & Language Picker */}
    <View style={styles.topBar}>
      <TouchableOpacity
        style={styles.spokenAudioBtn}
        accessibilityRole="button"
        accessibilityLabel="Listen to this screen instructions in Assamese"
        accessibilityHint="Double tap to hear the role options spoken aloud in your language"
        onPress={handlePlayVoicePrompt}
      >
        <Text style={styles.spokenAudioIcon}>🔊</Text>
        <Text style={styles.spokenAudioText}>শুনক (Listen)</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.langPickerBtn}
        accessibilityRole="button"
        accessibilityLabel="Change language: Current language is Assamese"
        accessibilityHint="Opens language selection modal"
        onPress={handleOpenLangModal}
      >
        <Text style={styles.langPickerText}>অসমীয়া ▾</Text>
      </TouchableOpacity>
    </View>

    {/* Primary Prompt Header */}
    <View style={styles.headerContainer}>
      <Text 
        style={styles.title}
        accessibilityRole="header"
        accessibilityLabel="আপুনি কোন হয় বাছক? Who are you? Select your role."
      >
        আপুনি কোন হয় বাছক
      </Text>
      <Text style={styles.subtitle}>
        Choose your role to personalize the experience
      </Text>
    </View>

    {/* Role Cards List (Max 3 items to comply with Miller's Law) */}
    <View style={styles.cardListContainer}>
      
      {/* 1. PATIENT ROLE CARD (Large, Green, Most Prominent) */}
      <TouchableOpacity
        style={[
          styles.roleCard, 
          styles.patientCard,
          selectedRole === 'PATIENT' && styles.cardSelected
        ]}
        accessibilityRole="button"
        accessibilityLabel="মই নিজে খেলিম, বয়োজ্যেষ্ঠ খেলুৱৈ। I am the Patient. Large text, voice games, and simple memory activities."
        accessibilityHint="Selects patient mode and takes you directly to daily activities."
        onPress={() => handleSelectRole('PATIENT')}
        activeOpacity={0.7}
      >
        <View style={styles.cardIconWrapper}>
          <Text style={styles.cardEmoji}>🧓</Text>
        </View>
        <View style={styles.cardContent}>
          <Text style={styles.cardTitle}>মই খেলিম (Patient)</Text>
          <Text style={styles.cardDescription}>
            সহজ খেল, ডাঙৰ আখৰ আৰু কণ্ঠৰ দ্বাৰা স্মৃতি অনুশীলন।
          </Text>
          <Text style={styles.cardEnglishSub}>
            Simple games, large buttons & voice exercises
          </Text>
        </View>
        <View style={styles.cardArrowWrapper}>
          <Text style={styles.cardArrow}>➔</Text>
        </View>
      </TouchableOpacity>

      {/* 2. CAREGIVER ROLE CARD (Calming Trustworthy Blue) */}
      <TouchableOpacity
        style={[
          styles.roleCard, 
          styles.caregiverCard,
          selectedRole === 'CAREGIVER' && styles.cardSelected
        ]}
        accessibilityRole="button"
        accessibilityLabel="মই পৰিয়ালৰ সদস্য বা যত্ন লওঁতা। I am a Family Caregiver. Monitor memory progress and set alerts."
        accessibilityHint="Selects caregiver mode and proceeds to phone number login."
        onPress={() => handleSelectRole('CAREGIVER')}
        activeOpacity={0.7}
      >
        <View style={[styles.cardIconWrapper, styles.caregiverIconBg]}>
          <Text style={styles.cardEmoji}>🤝</Text>
        </View>
        <View style={styles.cardContent}>
          <Text style={styles.cardTitle}>মই যত্ন লওঁতা (Caregiver)</Text>
          <Text style={styles.cardDescription}>
            পৰিয়ালৰ সদস্য: অগ্ৰগতি নিৰীক্ষণ আৰু সতৰ্কবাৰ্তা ব্যৱস্থাপনা।
          </Text>
          <Text style={styles.cardEnglishSub}>
            Family member: track progress & health alerts
          </Text>
        </View>
        <View style={styles.cardArrowWrapper}>
          <Text style={styles.cardArrow}>➔</Text>
        </View>
      </TouchableOpacity>

      {/* 3. DOCTOR / HEALTH WORKER CARD (Clinical Amber/Teal) */}
      <TouchableOpacity
        style={[
          styles.roleCard, 
          styles.doctorCard,
          selectedRole === 'DOCTOR' && styles.cardSelected
        ]}
        accessibilityRole="button"
        accessibilityLabel="চিকিৎসক বা স্বাস্থ্যকৰ্মী। I am a Doctor or ASHA Health Worker. Clinical assessment metrics and cohort trends."
        accessibilityHint="Selects clinical mode and proceeds to medical verification."
        onPress={() => handleSelectRole('DOCTOR')}
        activeOpacity={0.7}
      >
        <View style={[styles.cardIconWrapper, styles.doctorIconBg]}>
          <Text style={styles.cardEmoji}>🩺</Text>
        </View>
        <View style={styles.cardContent}>
          <Text style={styles.cardTitle}>চিকিৎসক / আশা কৰ্মী (Doctor)</Text>
          <Text style={styles.cardDescription}>
            ৰোগীৰ ক্লিনিকেল ৰিপ'ৰ্ট আৰু জ্ঞানমূলক বিশ্লেষণ।
          </Text>
          <Text style={styles.cardEnglishSub}>
            Clinical summaries & cognitive deviation charts
          </Text>
        </View>
        <View style={styles.cardArrowWrapper}>
          <Text style={styles.cardArrow}>➔</Text>
        </View>
      </TouchableOpacity>

    </View>

    {/* Bottom Status & Emergency Offline Assistance */}
    <View style={styles.footerContainer}>
      <Text style={styles.offlineNoticeText}>
        🔒 কোনো ইন্টাৰনেটৰ প্ৰয়োজন নাই (No Internet Required)
      </Text>
    </View>

  </View>
</SafeAreaView>
```

---

### 3. STATE MANAGEMENT (Redux Slice + Local State)
* **Global State (Redux `authSlice`)**:
  * Action: `dispatch(setRole(selectedRole))` updates `state.auth.role`.
* **Local State (`useState`)**:
  * `const [selectedRole, setSelectedRole] = useState<'PATIENT' | 'CAREGIVER' | 'DOCTOR' | null>(null)`
  * `const [isAudioPlaying, setIsAudioPlaying] = useState<boolean>(false)`
  * `const [isLangModalOpen, setIsLangModalOpen] = useState<boolean>(false)`
* **Mount / Unmount Behavior**:
  * On mount: Reads local language preference from `AsyncStorage`. If no preference exists, defaults to `'as'` (Assamese).
  * Automatically checks if a prior patient profile was created on this hardware.
* **Edge Case Handling (Role Switching)**:
  * If user started as Patient, killed app, and restarted wanting to be Caregiver:
  * Calling `handleSelectRole('CAREGIVER')` purges any volatile un-saved patient draft state from Redux, ensuring zero cross-contamination of permissions.

---

### 4. NAVIGATION PARAMS & ROUTING
* **Route Name**: `'RoleSelection'`
* **Params Received**: `{ source?: 'splash' | 'logout' }`
* **Params Passed to Next Screen**:
  * If **PATIENT** selected: `navigation.navigate('PatientSetup', { role: 'PATIENT', isNew: true })`
  * If **CAREGIVER** selected: `navigation.navigate('MobileEntry', { role: 'CAREGIVER' })`
  * If **DOCTOR** selected: `navigation.navigate('MobileEntry', { role: 'DOCTOR' })`
* **Navigation Action**: `navigation.navigate()` (Preserves history with back stack capability so caregiver can change their mind).
* **Deep Linking**: `sih26003://auth/role-selection`

---

### 5. ACCESSIBILITY SPECIFICATION (MANDATORY SECTION)

```typescript
export const roleAccessibilityConfig = {
  voicePrompt: {
    accessibilityRole: 'button' as const,
    accessibilityLabel: "নিৰ্দেশনা শুনক। Listen to screen instructions in Assamese.",
    accessibilityHint: "Plays pre-recorded spoken explanation of the three role choices.",
  },
  patientCard: {
    accessibilityRole: 'button' as const,
    accessibilityLabel: "মই খেলিম, ৰোগীৰ বাবে। Patient mode. Large icons, voice games, zero complex menus.",
    accessibilityHint: "Double tap to enter simplified patient mode.",
  },
  caregiverCard: {
    accessibilityRole: 'button' as const,
    accessibilityLabel: "মই যত্ন লওঁতা, পৰিয়ালৰ সদস্যৰ বাবে। Caregiver mode. Monitor memory progress and set alerts.",
    accessibilityHint: "Double tap to proceed to caregiver phone login.",
  },
  doctorCard: {
    accessibilityRole: 'button' as const,
    accessibilityLabel: "চিকিৎসক বা আশা কৰ্মীৰ বাবে। Doctor mode. View clinical diagnostic charts.",
    accessibilityHint: "Double tap to enter healthcare worker portal.",
  }
};
```

* **Focus Order (Tab Sequence)**:
  1. Audio Instructions Button (`spokenAudioBtn`)
  2. Language Selector (`langPickerBtn`)
  3. Header Title (`title`)
  4. Patient Role Card (`patientCard`)
  5. Caregiver Role Card (`caregiverCard`)
  6. Doctor Role Card (`doctorCard`)
* **Screen Reader TalkBack Announcement**:
  * On Screen Load: *"ভূমিকা বাছনি। আপুনি কোন হয় বাছক? তিনিটা বিকল্প উপলব্ধ আছে: মই খেলিম, মই যত্ন লওঁতা, আৰু চিকিৎসক।"*
* **Touch Target Sizes**:
  * Spoken Audio Button: $140 \times 52\text{ px}$ (Exceeds WCAG 44px minimum).
  * Role Cards: Minimum height $112\text{ px}$, width $100\%$ of content container ($>320\text{ px}$).

---

### 6. OFFLINE BEHAVIOR SPECIFICATION (CRITICAL)
* **Render without internet**: 100% normal appearance. Role choices and regional audio prompts reside in local assets.
* **User actions while offline**:
  * Selecting **PATIENT** proceeds directly to local SQLite profile or game catalog without needing any network.
  * Selecting **CAREGIVER** proceeds to `MobileEntry`, which uses local PIN verification if previously registered, or queues OTP dispatch if new.
* **Error states**: None. Screen generates zero network sockets.
* **Data persistence**: Writes chosen role to `AsyncStorage` (`user_role: 'CAREGIVER'`).
* **Sync indicator**: Bottom footer shows: 🔒 **"কোনো ইন্টাৰনেটৰ প্ৰয়োজন নাই (No Internet Required)"**.
* **Graceful degradation**: If audio playback of instructions fails, visual high-contrast text and icons remain fully legible.

---

### 7. I18N (INTERNATIONALIZATION) KEYS

```json
{
  "en": {
    "roleSelection": {
      "listen": "Listen",
      "title": "Who are you?",
      "subtitle": "Choose your role to get started",
      "patientTitle": "I am the Patient",
      "patientDesc": "Simple games, large buttons & voice exercises",
      "caregiverTitle": "I am a Caregiver",
      "caregiverDesc": "Family member: track progress & alerts",
      "doctorTitle": "Doctor / Health Worker",
      "doctorDesc": "Clinical summaries & cognitive trends",
      "offlineNotice": "Works 100% Offline • No Internet Needed"
    }
  },
  "as": {
    "roleSelection": {
      "listen": "শুনক",
      "title": "আপুনি কোন হয় বাছক",
      "subtitle": "আগলৈ যাবলৈ আপোনাৰ ভূমিকা নিৰ্বাচন কৰক",
      "patientTitle": "মই খেলিম (Patient)",
      "patientDesc": "সহজ খেল, ডাঙৰ আখৰ আৰু কণ্ঠৰ অনুশীলন",
      "caregiverTitle": "মই যত্ন লওঁতা (Caregiver)",
      "caregiverDesc": "পৰিয়ালৰ সদস্য: স্মৃতিৰ অগ্ৰগতি পৰীক্ষা কৰক",
      "doctorTitle": "চিকিৎসক / আশা কৰ্মী",
      "doctorDesc": "ক্লিনিকেল ৰিপ'ৰ্ট আৰু বিশ্লেষণমূলক তথ্য",
      "offlineNotice": "১০০% অফলাইন কাৰ্যক্ষম • ইন্টাৰনেটৰ প্ৰয়োজন নাই"
    }
  },
  "mn": {
    "roleSelection": {
      "listen": "ꯇꯥꯕꯤꯌꯨ",
      "title": "ꯅꯍꯥꯛ ꯀꯅꯥꯅꯣ ꯈꯅꯕꯤꯌꯨ",
      "subtitle": "ꯃꯈꯥ ꯆꯠꯊꯅꯕ ꯅꯍꯥꯛꯀꯤ ꯊꯧꯗꯥꯡ ꯈꯅꯕꯤꯌꯨ",
      "patientTitle": "ꯑꯩꯍꯥꯛ ꯁꯥꯟꯅꯒꯅꯤ (Patient)",
      "patientDesc": "ꯂꯥꯏꯕ ꯁꯥꯟꯅꯄꯣꯠ ꯑꯃꯁꯨꯡ ꯋꯥꯍꯩꯒꯤ ꯑꯦꯛꯁꯔꯁꯥꯏꯖ",
      "caregiverTitle": "ꯑꯩꯍꯥꯛ ꯌꯦꯡꯁꯤꯅꯕꯤꯕꯅꯤ (Caregiver)",
      "caregiverDesc": "ꯏꯃꯨꯡꯒꯤ ꯃꯤ: ꯅꯤꯡꯁꯤꯡ ꯄꯥꯉ꯭ꯒꯜ ꯌꯦꯡꯁꯤꯅꯕ",
      "doctorTitle": "ꯗꯥꯛꯇꯔ / ꯑꯥꯁꯥ ꯋꯥꯔꯀꯔ",
      "doctorDesc": "ꯀ꯭ꯂꯤꯅꯤꯀꯦꯜ ꯔꯤꯄꯣꯔꯠ ꯑꯃꯁꯨꯡ ꯇꯦꯂꯤꯃꯦꯇ꯭ꯔꯤ",
      "offlineNotice": "ꯑꯣꯐꯂꯥꯏꯟ ꯑꯣꯏꯅꯥ ꯊꯕꯛ ꯇꯧꯕ"
    }
  },
  "br": {
    "roleSelection": {
      "listen": "खनासं",
      "title": "नोंथाङा सोर सायख",
      "subtitle": "सिगां बांनो गावनि बिबानखौ सायख",
      "patientTitle": "आं गेलेगोन (Patient)",
      "patientDesc": "गोरलै गेलेमु आरो सोदोबनि बिजाथाय",
      "caregiverTitle": "आं नायदिंथिग्रा (Caregiver)",
      "caregiverDesc": "नखरनि सुबुं: गोसोखां गोहो नायग्रोब",
      "doctorTitle": "डाक्टर / आशा मन्थ्री",
      "doctorDesc": "थावनि नायबिजिरनाय आरो दिन्थिथि",
      "offlineNotice": "गासै अफलाइन रैखा • दावबायनायनि गोनांथि गैया"
    }
  },
  "hi": {
    "roleSelection": {
      "listen": "सुनिए",
      "title": "आप कौन हैं? चुनें",
      "subtitle": "आगे बढ़ने के लिए अपनी भूमिका चुनें",
      "patientTitle": "मैं मरीज हूँ (Patient)",
      "patientDesc": "सरल खेल, बड़े अक्षर और आवाज आधारित अभ्यास",
      "caregiverTitle": "मैं देखभालकर्ता हूँ (Caregiver)",
      "caregiverDesc": "परिवार का सदस्य: याददाश्त की प्रगति देखें",
      "doctorTitle": "डॉक्टर / स्वास्थ्य कार्यकर्ता",
      "doctorDesc": "नैदानिक रिपोर्ट और विश्लेषणात्मक डेटा",
      "offlineNotice": "100% ऑफ़लाइन कार्यक्षम • इंटरनेट की आवश्यकता नहीं"
    }
  }
}
```

---

### 8. STYLESHEET (`RoleSelectionScreen.styles.ts`)

```typescript
import { StyleSheet, Platform, Dimensions } from 'react-native';

const { width } = Dimensions.get('window');

export const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#121212',
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
  spokenAudioBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1B5E20', // Green AAA compliant
    borderWidth: 2,
    borderColor: '#4CAF50',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderRadius: 28,
    minHeight: 52,
    minWidth: 130,
  },
  spokenAudioIcon: {
    fontSize: 20,
    marginRight: 8,
  },
  spokenAudioText: {
    fontSize: 18,
    fontWeight: '700',
    color: '#FFFFFF',
  },
  langPickerBtn: {
    backgroundColor: '#262626',
    borderWidth: 1.5,
    borderColor: '#FFD700',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderRadius: 24,
    minHeight: 52,
    justifyContent: 'center',
  },
  langPickerText: {
    fontSize: 18,
    fontWeight: '700',
    color: '#FFD700',
  },
  headerContainer: {
    marginBottom: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: '800',
    color: '#FFD700', // Contrast ratio 13.8:1
    marginBottom: 8,
    fontFamily: Platform.select({ android: 'NotoSansBengali-Bold', ios: 'System' }),
  },
  subtitle: {
    fontSize: 18,
    color: '#E0E0E0',
    lineHeight: 26,
  },
  cardListContainer: {
    flex: 1,
    justifyContent: 'center',
  },
  roleCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1E1E1E',
    borderWidth: 2,
    borderRadius: 20,
    padding: 18,
    marginBottom: 16,
    minHeight: 116, // Well exceeds 48px minimum
    ...Platform.select({
      android: { elevation: 4 },
      ios: {
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.25,
        shadowRadius: 4,
      },
    }),
  },
  patientCard: {
    borderColor: '#00E676', // Energetic accessible green
    backgroundColor: '#162A1B',
  },
  caregiverCard: {
    borderColor: '#64B5F6', // Trustworthy light blue
    backgroundColor: '#132333',
  },
  doctorCard: {
    borderColor: '#FFB74D', // Soft clinical amber
    backgroundColor: '#2B2317',
  },
  cardSelected: {
    borderWidth: 4,
    borderColor: '#FFD700', // Gold highlight
  },
  cardIconWrapper: {
    width: 64,
    height: 64,
    borderRadius: 32,
    backgroundColor: '#1B5E20',
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 16,
  },
  caregiverIconBg: {
    backgroundColor: '#0D47A1',
  },
  doctorIconBg: {
    backgroundColor: '#E65100',
  },
  cardEmoji: {
    fontSize: 32,
  },
  cardContent: {
    flex: 1,
  },
  cardTitle: {
    fontSize: 22,
    fontWeight: '800',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  cardDescription: {
    fontSize: 16,
    fontWeight: '500',
    color: '#E0E0E0',
    lineHeight: 22,
    marginBottom: 2,
  },
  cardEnglishSub: {
    fontSize: 13,
    color: '#B0BEC5',
  },
  cardArrowWrapper: {
    marginLeft: 10,
    padding: 6,
  },
  cardArrow: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#FFD700',
  },
  footerContainer: {
    alignItems: 'center',
    paddingVertical: 10,
  },
  offlineNoticeText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#81C784',
    textAlign: 'center',
  },
});
```

---

### 9. LIFECYCLE METHODS & SIDE EFFECTS
* **Spoken Prompt Playback**:
  ```typescript
  const soundRef = useRef<Audio.Sound | null>(null);

  const handlePlayVoicePrompt = async () => {
    try {
      if (soundRef.current) {
        await soundRef.current.unloadAsync();
      }
      const { sound } = await Audio.Sound.createAsync(
        require('@assets/audio/prompts_as/role_selection_prompt.m4a')
      );
      soundRef.current = sound;
      await sound.playAsync();
      setIsAudioPlaying(true);
      sound.setOnPlaybackStatusUpdate(status => {
        if (status.isLoaded && status.didJustFinish) {
          setIsAudioPlaying(false);
        }
      });
    } catch (error) {
      console.warn('Audio prompt failed, fallback to visual readout');
    }
  };

  useEffect(() => {
    return () => {
      if (soundRef.current) {
        soundRef.current.unloadAsync();
      }
    };
  }, []);
  ```

---

### 10. TEST SCENARIOS (Manual QA Checklist)
1. **Selection (Patient)**: Tapping "মই খেলিম" navigates directly to `PatientSetup`.
2. **Selection (Caregiver)**: Tapping "মই যত্ন লওঁতা" navigates to `MobileEntry` with `role='CAREGIVER'`.
3. **Selection (Doctor)**: Tapping "চিকিৎসক" navigates to `MobileEntry` with `role='DOCTOR'`.
4. **Audio Button Tap**: Plays Assamese voice prompt without cutting off or crashing.
5. **Language Change**: Tapping language picker updates cards instantly to Manipuri or Bodo.
6. **Double-Tap Tremor Resilience**: Rapid duplicate taps on a role card must only dispatch a single navigation action.
7. **Screen Reader (TalkBack)**: Swiping right navigates in logical order from audio button to Doctor card.
8. **Contrast Audit**: Verifies text contrast is $>7:1$ across all 3 cards using WCAG luminance matrix.
9. **Touch Target Measurement**: Confirms every card is at least $116\text{ px}$ tall ($>2.4\times$ standard).
10. **State Isolation**: Switching from Doctor to Patient and back does not retain partial input states.

---

### 11. DATA MODEL IMPACT
* **Tables Affected**: None (Role choice stored in memory/AsyncStorage until profile creation).
* **Storage**: `AsyncStorage.setItem('pending_role', role)`.
* **Sync Queue**: Does not generate sync actions.
