# SCREEN 1: SPLASH SCREEN (নমস্কাৰ / স্বাগতম)

---

### 1. FILE STRUCTURE
- **Screen File**: `mobile-app/src/screens/auth/SplashScreen.tsx`
- **Styles**: `mobile-app/src/screens/auth/styles/SplashScreen.styles.ts`
- **Hooks**: `mobile-app/src/hooks/useAppBootstrap.ts`, `mobile-app/src/hooks/useReducedMotion.ts`
- **i18n Keys**: `mobile-app/src/locales/{en,as,mn,br,hi}/splash.json`
- **Unit & Snapshot Tests**: `mobile-app/src/screens/auth/__tests__/SplashScreen.test.tsx`

---

### 2. COMPONENT HIERARCHY (React Native JSX Tree)
```jsx
<SafeAreaView style={styles.safeArea}>
  <StatusBar barStyle="light-content" backgroundColor="#121212" />
  <View 
    style={styles.container}
    accessible={true}
    accessibilityRole="none"
    accessibilityLabel="SmritiSetu Application Loading Screen"
  >
    {/* Decorative Regional Background Gradient Layer */}
    <LinearGradient
      colors={['#121212', '#1B2E1E', '#121212']}
      style={styles.backgroundGradient}
      accessible={false}
      importantForAccessibility="no"
    />

    {/* Center Brand Identity Container */}
    <View style={styles.brandingContainer}>
      <Animated.View 
        style={[styles.logoWrapper, { opacity: logoOpacity, transform: [{ scale: logoScale }] }]}
        accessible={true}
        accessibilityRole="image"
        accessibilityLabel="SmritiSetu Emblem: Two stylized hands supporting a memory leaf in traditional Assamese and Manipuri handloom motifs"
      >
        <Image
          source={require('@assets/images/branding/smritisetu_logo_emblem.png')}
          style={styles.logoImage}
          resizeMode="contain"
          accessible={false}
        />
      </Animated.View>

      <Text 
        style={styles.titleIndic}
        accessibilityRole="header"
        accessibilityLabel="SmritiSetu"
      >
        স্মৃতিসেতু
      </Text>

      <Text 
        style={styles.titleMeitei}
        accessibilityLabel="SmritiSetu in Meitei Mayek script"
      >
        ꯁ꯭ꯃ꯭ꯔꯤꯇꯤ ꯁꯦꯇꯨ
      </Text>

      <Text 
        style={styles.titleLatin}
        accessibilityLabel="SmritiSetu in English"
      >
        SmritiSetu
      </Text>

      <Text 
        style={styles.tagline}
        accessibilityRole="text"
        accessibilityLabel="Cognitive Rehabilitation and Memory Assistance Platform for North East India"
      >
        জ্ঞানমূলক পুনৰুদ্ধাৰ আৰু স্মৃতি সহায়ক মঞ্চ
      </Text>
    </View>

    {/* Accessibility Notice & Loading Status Bar */}
    <View 
      style={styles.statusContainer}
      accessible={true}
      accessibilityRole="text"
      accessibilityLiveRegion="polite"
      accessibilityLabel={bootstrapStatusText}
    >
      <ActivityIndicator 
        size="large" 
        color="#FFD700" 
        style={styles.spinner} 
        accessibilityLabel="Loading application data from local storage"
      />
      <Text style={styles.statusText}>{bootstrapStatusText}</Text>
    </View>

    {/* Offline Mode Security Pill Indicator */}
    <View 
      style={styles.offlinePillContainer}
      accessible={true}
      accessibilityRole="text"
      accessibilityLabel="Operating in 100% Offline Secured Mode using SQLCipher 256-bit encryption"
    >
      <Text style={styles.offlinePillIcon}>🟢</Text>
      <Text style={styles.offlinePillText}>সম্পূৰ্ণ অফলাইন সুৰক্ষিত (100% Offline Secured)</Text>
    </View>

    {/* Ministry Sponsorship & Legal Footer */}
    <View style={styles.footerContainer}>
      <Text style={styles.footerMinistryText}>
        Ministry of Development of North Eastern Region (MDoNER)
      </Text>
      <Text style={styles.footerComplianceText}>
        Government of India • DPDA 2023 Healthcare Compliant
      </Text>
    </View>
  </View>
</SafeAreaView>
```

---

### 3. STATE MANAGEMENT (Redux Slice + Local State)
* **Global State Read (Redux)**:
  * `state.auth.isAuthenticated`: Boolean checking active session.
  * `state.auth.role`: `'PATIENT' | 'CAREGIVER' | 'DOCTOR' | null`.
  * `state.auth.activePatientId`: UUID string of active patient profile.
  * `state.settings.prefersReducedMotion`: OS accessibility motion token.
* **Local State (`useState`, `useRef`)**:
  * `const [bootstrapStatusText, setBootstrapStatusText] = useState('পৰীক্ষা চলিছে... (Verifying secure storage...)')`
  * `const logoOpacity = useRef(new Animated.Value(0)).current`
  * `const logoScale = useRef(new Animated.Value(0.95)).current`
* **Mount / Unmount Lifecycle**:
  * On mount: Launches `useAppBootstrap` hook:
    1. Initializes hardware-backed Master Key via `expo-secure-store`.
    2. Opens local SQLCipher database (`PRAGMA key = ...; PRAGMA journal_mode = WAL;`).
    3. Verifies schema migration tables (`patients`, `game_sessions`, `sync_queue`).
    4. Pre-warms ONNX Runtime Mobile session in background worker.
    5. Reads cached caregiver/patient credentials.
  * On unmount: Cleans up animation timers and unregisters bootstrap listeners.
* **Offline Behavior with Expired Token**:
  * If the device is offline and the JWT token has expired, SmritiSetu **does not lock the user out**.
  * It verifies the local PIN hash from `expo-secure-store` and transitions directly to the role home screen with local data access.

---

### 4. NAVIGATION PARAMS & ROUTING
* **Route Name**: `'Splash'`
* **Params Received**: `{}` (None)
* **Params Passed to Next Screen**:
  * If unauthenticated: `navigation.replace('RoleSelection', { source: 'splash' })`
  * If authenticated as Caregiver: `navigation.replace('CaregiverDashboard', { caregiverId })`
  * If authenticated as Patient: `navigation.replace('PatientHome', { patientId })`
* **Navigation Action**: `navigation.replace()` (Destroys Splash from history stack so elderly users pressing the hardware Android Back button cannot accidentally return to Splash).
* **Deep Linking**: `sih26003://splash` -> Handled by routing directly to bootstrap logic.

---

### 5. ACCESSIBILITY SPECIFICATION (MANDATORY SECTION)

```typescript
export const splashAccessibilityConfig = {
  container: {
    accessibilityLabel: "SmritiSetu আৰম্ভ হৈছে। অনুগ্ৰহ কৰি অপেক্ষা কৰক। (SmritiSetu is loading. Please wait.)",
    accessibilityRole: 'none' as const,
    importantForAccessibility: 'yes' as const,
  },
  logo: {
    accessibilityLabel: "স্মৃতিসেতু চিহ্ন। (SmritiSetu Emblem with traditional handloom motifs)",
    accessibilityRole: 'image' as const,
    accessibilityHint: "Visual logo representing culturally adapted dementia cognitive therapy.",
  },
  statusText: {
    accessibilityRole: 'text' as const,
    accessibilityLiveRegion: 'polite' as const,
    accessibilityLabel: "স্থানীয় তথ্য পৰীক্ষা কৰা হৈছে। (Verifying local storage.)",
  },
  offlineBadge: {
    accessibilityRole: 'text' as const,
    accessibilityLabel: "অফলাইন সুৰক্ষিত ম'ড সক্ৰিয়। (Offline secured mode active. No internet required.)",
  }
};
```

* **Focus Order (Tab Sequence)**:
  1. Main Title Header (`titleIndic`)
  2. Subtitle Tagline (`tagline`)
  3. Status Live Region (`statusContainer`)
  4. Offline Security Badge (`offlinePillContainer`)
* **Screen Reader TalkBack Announcement**:
  * When screen mounts: *"স্মৃতিসেতু আৰম্ভ হৈছে। অনুগ্ৰহ কৰি অপেক্ষা কৰক। SmritiSetu is loading local healthcare records safely. Please wait."*
* **Dynamic Content Changes**:
  * Status updates announce via `accessibilityLiveRegion="polite"` so screen reader finishes reading prior clauses without abrupt stutter.
* **Touch Target Sizes**:
  * Splash is non-interactive; entire viewport acts as an informative announcement canvas. Minimum virtual hit-box: $48 \times 48\text{ px}$.

---

### 6. OFFLINE BEHAVIOR SPECIFICATION (CRITICAL)
* **Render without internet**: Renders with 100% full fidelity in $0.0\text{ Kbps}$ Airplane Mode. Zero network requests are initiated.
* **User actions while offline**: Fully autonomous. Transitions to `RoleSelection` or local dashboard in $<1200\text{ms}$.
* **Error states**: If SQLite database fails to open, renders calming fallback message:
  * *"আপোনাৰ ফোনৰ মেমৰি সুৰক্ষিত কৰা হৈছে। এপটো পুনৰ আৰম্ভ কৰক। (Your storage is secured. Please restart the app.)"*
  * No frightening red banners or technical stack traces.
* **Data persistence**: Checks `PRAGMA integrity_check` on local SQLite database file.
* **Sync indicator**: Prominent pill badge in top-center:
  * 🟢 **"সম্পূৰ্ণ অফলাইন সুৰক্ষিত (100% Offline Secured - SQLCipher AES-256)"**
* **Graceful degradation**: If ONNX neural model fails to warm up in RAM, logs warning to SQLite and falls back to pre-rendered acoustic phoneme prompts without blocking app boot.

---

### 7. I18N (INTERNATIONALIZATION) KEYS

```json
{
  "en": {
    "splash": {
      "appName": "SmritiSetu",
      "tagline": "Cognitive Rehabilitation & Memory Assistance Platform",
      "loading": "Securing local clinical vault...",
      "offlineSecured": "100% Offline Secured (AES-256)",
      "ministrySubtitle": "Ministry of Development of North Eastern Region (MDoNER)",
      "legalNotice": "Government of India • DPDA 2023 Compliant"
    }
  },
  "as": {
    "splash": {
      "appName": "স্মৃতিসেতু",
      "tagline": "জ্ঞানমূলক পুনৰুদ্ধাৰ আৰু স্মৃতি সহায়ক মঞ্চ",
      "loading": "স্থানীয় স্বাস্থ্য তথ্য সুৰক্ষিত কৰা হৈছে...",
      "offlineSecured": "সম্পূৰ্ণ অফলাইন সুৰক্ষিত (AES-256)",
      "ministrySubtitle": "উত্তৰ-পূব অঞ্চল উন্নয়ন মন্ত্ৰালয় (MDoNER)",
      "legalNotice": "ভাৰত চৰকাৰ • DPDA ২০২৩ নীতি অনুসৰি সুৰক্ষিত"
    }
  },
  "mn": {
    "splash": {
      "appName": "ꯁ꯭ꯃ꯭ꯔꯤꯇꯤ ꯁꯦꯇꯨ",
      "tagline": "ꯋꯥꯈꯜ ꯑꯃꯁꯨꯡ ꯅꯤꯡꯁꯤꯡ ꯄꯥꯉ꯭ꯒꯜ ꯍꯦꯅꯒꯠꯍꯟꯅꯕ ꯃꯇꯦꯡ",
      "loading": "ꯂꯣꯀꯦꯜ ꯗꯦꯇꯥ ꯊꯨꯝꯖꯤꯜꯂꯤ...",
      "offlineSecured": "ꯑꯣꯐꯂꯥꯏꯟ ꯑꯣꯏꯅꯥ ꯆꯠꯊꯕ (AES-256)",
      "ministrySubtitle": "ꯑꯋꯥꯡ ꯅꯣꯡꯄꯣꯛ ꯂꯃꯗꯝ ꯆꯥꯎꯈꯠ-ꯊꯧꯔꯥꯡ ꯃꯟꯠꯔꯥꯂꯌ (MDoNER)",
      "legalNotice": "ꯚꯥꯔꯠ ꯁꯔꯀꯥꯔ • DPDA 2023"
    }
  },
  "br": {
    "splash": {
      "appName": "स्मृतिसेतु",
      "tagline": "गोसो आरो गोसोखां गोहो बांहोग्रा प्लेतफर्म",
      "loading": "गावनि देथाखौ रैखा खालामगासिनो...",
      "offlineSecured": "गासै अफलाइन रैखा (AES-256)",
      "ministrySubtitle": "सा-सानजा ओनसोल जौगाथाय मन्त्रालय (MDoNER)",
      "legalNotice": "भारत सरकार • DPDA 2023"
    }
  },
  "hi": {
    "splash": {
      "appName": "स्मृतिसेतु",
      "tagline": "संज्ञानात्मक पुनर्वास एवं स्मृति सहायता मंच",
      "loading": "स्थानीय स्वास्थ्य डेटा सुरक्षित किया जा रहा है...",
      "offlineSecured": "100% ऑफ़लाइन सुरक्षित (AES-256)",
      "ministrySubtitle": "उत्तर पूर्वी क्षेत्र विकास मंत्रालय (MDoNER)",
      "legalNotice": "भारत सरकार • DPDA 2023 द्वारा अनुपालित"
    }
  }
}
```

---

### 8. STYLESHEET (`SplashScreen.styles.ts`)

```typescript
import { StyleSheet, Platform, Dimensions } from 'react-native';

const { width, height } = Dimensions.get('window');

export const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#121212', // Pure high-contrast dark background
  },
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 24,
    paddingVertical: 32,
  },
  backgroundGradient: {
    position: 'absolute',
    left: 0,
    right: 0,
    top: 0,
    bottom: 0,
  },
  brandingContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: height * 0.12,
  },
  logoWrapper: {
    width: 140,
    height: 140,
    borderRadius: 28,
    backgroundColor: '#1E1E1E',
    borderWidth: 3,
    borderColor: '#FFD700', // Gold border - high contrast
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 24,
    ...Platform.select({
      android: { elevation: 6 },
      ios: {
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 4 },
        shadowOpacity: 0.3,
        shadowRadius: 8,
      },
    }),
  },
  logoImage: {
    width: 100,
    height: 100,
  },
  titleIndic: {
    fontSize: 36,
    fontWeight: '800',
    color: '#FFD700', // Pure Gold (Contrast 13.8:1 against #121212)
    fontFamily: Platform.select({ android: 'NotoSansBengali-Bold', ios: 'System' }),
    textAlign: 'center',
    marginBottom: 6,
  },
  titleMeitei: {
    fontSize: 22,
    fontWeight: '600',
    color: '#FFFFFF', // Pure White (Contrast 18.5:1 against #121212)
    textAlign: 'center',
    marginBottom: 6,
  },
  titleLatin: {
    fontSize: 24,
    fontWeight: '700',
    color: '#E0E0E0',
    letterSpacing: 2,
    textAlign: 'center',
    marginBottom: 12,
  },
  tagline: {
    fontSize: 18,
    fontWeight: '500',
    color: '#E0E0E0',
    textAlign: 'center',
    lineHeight: 28,
    maxWidth: width * 0.85,
    fontFamily: Platform.select({ android: 'NotoSansBengali-Regular', ios: 'System' }),
  },
  statusContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    marginVertical: 20,
    minHeight: 64,
  },
  spinner: {
    marginBottom: 12,
  },
  statusText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#FFD700',
    textAlign: 'center',
  },
  offlinePillContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1B5E20', // Dark Green
    borderWidth: 1.5,
    borderColor: '#4CAF50',
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 24,
    minHeight: 48,
  },
  offlinePillIcon: {
    fontSize: 14,
    marginRight: 8,
  },
  offlinePillText: {
    fontSize: 14,
    fontWeight: '700',
    color: '#FFFFFF',
  },
  footerContainer: {
    alignItems: 'center',
    paddingTop: 16,
  },
  footerMinistryText: {
    fontSize: 13,
    fontWeight: '600',
    color: '#BDBDBD',
    textAlign: 'center',
    marginBottom: 4,
  },
  footerComplianceText: {
    fontSize: 11,
    color: '#757575',
    textAlign: 'center',
  },
});
```

---

### 9. LIFECYCLE METHODS & SIDE EFFECTS
* **`useEffect` Bootstrap Execution**:
  ```typescript
  useEffect(() => {
    let isMounted = true;

    // Check OS reduced motion preference
    if (AccessibilityInfo.isReduceMotionEnabled) {
      AccessibilityInfo.isReduceMotionEnabled().then(enabled => {
        if (!enabled && isMounted) {
          Animated.parallel([
            Animated.timing(logoOpacity, { toValue: 1, duration: 800, useNativeDriver: true }),
            Animated.spring(logoScale, { toValue: 1, tension: 20, friction: 7, useNativeDriver: true }),
          ]).start();
        } else {
          logoOpacity.setValue(1);
          logoScale.setValue(1);
        }
      });
    }

    // Run async database and credentials bootstrap
    const executeBootstrap = async () => {
      try {
        await initializeDatabase();
        setBootstrapStatusText('স্থানীয় তথ্য সক্ৰিয় হৈছে... (Storage ready)');
        await new Promise(resolve => setTimeout(resolve, 800)); // Minimum perceptible hold for elderly
        
        const cachedRole = await AsyncStorage.getItem('user_role');
        if (isMounted) {
          if (cachedRole) {
            navigation.replace(cachedRole === 'PATIENT' ? 'PatientHome' : 'CaregiverDashboard');
          } else {
            navigation.replace('RoleSelection');
          }
        }
      } catch (err) {
        setBootstrapStatusText('স্থানীয় সুৰক্ষা সক্ৰিয় হৈছে (Fallback mode)');
        setTimeout(() => navigation.replace('RoleSelection'), 1000);
      }
    };

    executeBootstrap();

    return () => {
      isMounted = false;
    };
  }, []);
  ```

---

### 10. TEST SCENARIOS (Manual QA Checklist)
1. **Happy Path (Unauthenticated)**: Fresh install boots in $<1500\text{ms}$, shows Assamese/Manipuri title, and smoothly replaces screen with `RoleSelection`.
2. **Happy Path (Authenticated Patient)**: Device with cached patient profile boots directly to `PatientHome` without flashing `RoleSelection`.
3. **Happy Path (Authenticated Caregiver)**: Device with saved caregiver PIN boots directly to `CaregiverDashboard`.
4. **Airplane Mode Stress Test**: Run app with Airplane Mode ON. Splash must not show any network warning or freeze.
5. **Slow Device Emulation (MediaTek Helio G35 / 2GB RAM)**: Logo animation does not drop frames below 55 FPS.
6. **OS Reduced Motion Enabled**: Verify logo appears instantly without fade/spring animations.
7. **TalkBack Screen Reader**: Focus hits title immediately; announces *"SmritiSetu আৰম্ভ হৈছে..."*.
8. **Font Scaling (200% Android System Font)**: Layout must not overlap; text wraps without cutting off.
9. **Corrupt Database Recovery**: Simulate corrupted SQLite page; verify app triggers emergency `.recover` and boots without crashing.
10. **Hardware Back Button Press**: Tapping Android physical Back button during Splash must NOT exit the app or crash the navigation container.

---

### 11. DATA MODEL IMPACT
* **Tables Affected**: `system_metadata` (Read-only during splash).
* **Columns Checked**: `schema_version`, `last_successful_wal_checkpoint`.
* **Encryption**: Validates Master Key derivation via `expo-secure-store` before unlocking `patient_records.db`.
* **Sync Queue**: Does not generate sync actions.
