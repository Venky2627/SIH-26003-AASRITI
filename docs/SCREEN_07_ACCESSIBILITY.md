# SCREEN 7: ACCESSIBILITY SETTINGS SCREEN (সুচলতা আৰু দৃষ্টি সহায় / ꯑꯦꯛꯁꯦꯁꯤꯕꯤꯂꯤꯇꯤ)

---

### 1. FILE STRUCTURE
- **Screen File**: `mobile-app/src/screens/settings/AccessibilityScreen.tsx`
- **Styles**: `mobile-app/src/screens/settings/styles/AccessibilityScreen.styles.ts`
- **Components**: `mobile-app/src/components/accessibility/FontScaleSelector.tsx`, `mobile-app/src/components/accessibility/TremorFilterToggle.tsx`, `mobile-app/src/components/accessibility/AudioSpeedSelector.tsx`
- **Context/Hooks**: `mobile-app/src/context/AccessibilityContext.tsx`, `mobile-app/src/hooks/useTremorFilter.ts`
- **i18n Keys**: `mobile-app/src/locales/{en,as,mn,br,hi}/accessibility.json`
- **Unit & Snapshot Tests**: `mobile-app/src/screens/settings/__tests__/AccessibilityScreen.test.tsx`

---

### 2. COMPONENT HIERARCHY (React Native JSX Tree)
```jsx
<SafeAreaView style={styles.safeArea}>
  <StatusBar barStyle="light-content" backgroundColor="#121212" />
  <ScrollView contentContainerStyle={styles.scrollContainer}>
    
    {/* Screen Header & Voice Demonstration */}
    <View style={styles.topHeader}>
      <TouchableOpacity
        style={styles.backButton}
        accessibilityRole="button"
        accessibilityLabel="উভতি যাওক (Go Back)"
        onPress={() => navigation.goBack()}
      >
        <Text style={styles.backArrow}>←</Text>
        <Text style={styles.backLabel}>উভতি যাওক</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.speakerButton}
        accessibilityRole="button"
        accessibilityLabel="সুচলতা নিৰ্দেশনা শুনক (Listen to accessibility guide in Assamese)"
        onPress={handlePlayVoicePrompt}
      >
        <Text style={styles.speakerIcon}>🔊</Text>
      </TouchableOpacity>
    </View>

    <View style={styles.titleContainer}>
      <Text style={styles.screenTitle} accessibilityRole="header">
        সুচলতা আৰু সুবিধা
      </Text>
      <Text style={styles.screenSubtitle}>
        Accessibility Settings: Customize text size, contrast, and touch for elderly comfort
      </Text>
    </View>

    {/* SECTION 1: VISION ASSISTANCE (দৃষ্টি সুবিধা) */}
    <View style={styles.settingsSectionCard}>
      <View style={styles.sectionHeaderRow}>
        <Text style={styles.sectionIcon}>👁️</Text>
        <Text style={styles.sectionTitle}>১. দৃষ্টি সুবিধা (Vision Assistance)</Text>
      </View>

      {/* Sub-item A: Font Size Scaler */}
      <View style={styles.subItemContainer}>
        <Text style={styles.subItemLabel}>আখৰৰ আকাৰ (Font Size)</Text>
        <Text style={styles.subItemHelper}>Adjust text size for clearer reading without eye strain.</Text>
        
        <View style={styles.fontScaleButtonRow}>
          {[
            { key: 'STANDARD', label: 'সাধাৰণ (Normal 18px)', sizeMultiplier: 1.0 },
            { key: 'LARGE', label: 'ডাঙৰ (Large 22px)', sizeMultiplier: 1.25 },
            { key: 'EXTRA_LARGE', label: 'অতি ডাঙৰ (Huge 26px)', sizeMultiplier: 1.5 },
          ].map((item) => (
            <TouchableOpacity
              key={item.key}
              style={[
                styles.fontScaleBtn,
                fontScale === item.key && styles.fontScaleBtnSelected
              ]}
              accessibilityRole="radio"
              accessibilityState={{ selected: fontScale === item.key }}
              accessibilityLabel={`${item.label}`}
              onPress={() => setFontScale(item.key)}
            >
              <Text style={[
                styles.fontScaleBtnText,
                fontScale === item.key && styles.fontScaleBtnTextSelected
              ]}>
                {item.label}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Sub-item B: Cataract High Contrast Mode */}
      <View style={styles.toggleRowContainer}>
        <View style={styles.toggleTextWrapper}>
          <Text style={styles.toggleTitle}>ছানি পৰা চকুৰ মোড (Cataract High Contrast)</Text>
          <Text style={styles.toggleDescription}>
            ক'লা আৰু উজ্জ্বল হালধীয়া ৰং (Pure Black & Gold > 13:1 contrast).
          </Text>
        </View>
        <Switch
          value={highContrast}
          onValueChange={setHighContrast}
          trackColor={{ false: '#424242', true: '#FFD700' }}
          thumbColor={highContrast ? '#121212' : '#BDBDBD'}
          accessibilityRole="switch"
          accessibilityLabel="ছানি পৰা চকুৰ উচ্চ বৈপৰীত্য মোড (Cataract High Contrast Switch)"
        />
      </View>
    </View>

    {/* SECTION 2: MOTOR & TREMOR DAMPING (হাত কঁপনি প্ৰতিৰোধ) */}
    <View style={styles.settingsSectionCard}>
      <View style={styles.sectionHeaderRow}>
        <Text style={styles.sectionIcon}>🖐️</Text>
        <Text style={styles.sectionTitle}>২. স্পৰ্শ আৰু কঁপনি প্ৰতিৰোধ (Motor Tremor Guard)</Text>
      </View>

      {/* Sub-item A: Steady Touch Damping Switch */}
      <View style={styles.toggleRowContainer}>
        <View style={styles.toggleTextWrapper}>
          <Text style={styles.toggleTitle}>হাতৰ কঁপনি ফিল্টাৰ (Tremor Touch Filter)</Text>
          <Text style={styles.toggleDescription}>
            ভুলবশতঃ দুবাৰ টিপা পৰিলে দ্বিতীয়টো স্পৰ্শ বাতিল কৰে (Debounce accidental double-taps).
          </Text>
        </View>
        <Switch
          value={tremorDamping}
          onValueChange={setTremorDamping}
          trackColor={{ false: '#424242', true: '#00E676' }}
          thumbColor={tremorDamping ? '#121212' : '#BDBDBD'}
          accessibilityRole="switch"
          accessibilityLabel="হাতৰ কঁপনি ফিল্টাৰ ছুইচ (Tremor Touch Filter Switch)"
        />
      </View>

      {/* Sub-item B: Minimum Touch Dwell Time */}
      <View style={styles.subItemContainer}>
        <Text style={styles.subItemLabel}>স্পৰ্শৰ সময়সীমা (Touch Dwell Time)</Text>
        <Text style={styles.subItemHelper}>Requires finger to rest on screen for 80ms to avoid accidental brushing.</Text>
        <View style={styles.dwellBadge}>
          <Text style={styles.dwellBadgeText}>৮০ মিল্লিছেকেণ্ড (80ms Protected Hold)</Text>
        </View>
      </View>
    </View>

    {/* SECTION 3: AUDIO & VOICE ASSISTANCE (কণ্ঠ আৰু শব্দ) */}
    <View style={styles.settingsSectionCard}>
      <View style={styles.sectionHeaderRow}>
        <Text style={styles.sectionIcon}>🔊</Text>
        <Text style={styles.sectionTitle}>৩. মাত আৰু নিৰ্দেশনা (Voice & Audio Prompts)</Text>
      </View>

      {/* Sub-item A: Audio Speech Rate */}
      <View style={styles.subItemContainer}>
        <Text style={styles.subItemLabel}>কথা কোৱাৰ গতি (Spoken Prompt Speed)</Text>
        <View style={styles.speedButtonRow}>
          {[
            { key: '0.75x', label: 'ধীৰ (Slow 0.75x)' },
            { key: '1.0x', label: 'স্বাভাবিক (Normal 1.0x)' },
          ].map((sp) => (
            <TouchableOpacity
              key={sp.key}
              style={[
                styles.speedBtn,
                audioSpeed === sp.key && styles.speedBtnSelected
              ]}
              accessibilityRole="radio"
              accessibilityState={{ selected: audioSpeed === sp.key }}
              accessibilityLabel={`${sp.label}`}
              onPress={() => setAudioSpeed(sp.key)}
            >
              <Text style={[
                styles.speedBtnText,
                audioSpeed === sp.key && styles.speedBtnTextSelected
              ]}>
                {sp.label}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Sub-item B: Voice Readout Test Button */}
      <TouchableOpacity
        style={styles.testVoiceBtn}
        accessibilityRole="button"
        accessibilityLabel="মাতৰ নমুনা পৰীক্ষা কৰক (Test Spoken Audio Sample)"
        onPress={handleTestVoiceSample}
      >
        <Text style={styles.testVoiceIcon}>▶️</Text>
        <Text style={styles.testVoiceText}>মাতৰ নমুনা শুনক (Test Voice Output)</Text>
      </TouchableOpacity>
    </View>

    {/* Save & Finish Configuration Button */}
    <View style={styles.actionContainer}>
      <TouchableOpacity
        style={styles.saveSettingsBtn}
        accessibilityRole="button"
        accessibilityLabel="সুচলতা সংৰক্ষণ কৰক আৰু খেল আৰম্ভ কৰক (Save Preferences & Enter Platform)"
        onPress={handleSaveAccessibilitySettings}
        activeOpacity={0.8}
      >
        <Text style={styles.saveSettingsBtnText}>
          সুচলতা সংৰক্ষণ আৰু খেল আৰম্ভ (Save & Start Games) ➔
        </Text>
      </TouchableOpacity>
    </View>

  </ScrollView>
</SafeAreaView>
```

---

### 3. STATE MANAGEMENT (Redux Slice + Local State)
* **Global State (Redux `accessibilitySlice` & Context Provider)**:
  * `state.accessibility.fontScale`: `'STANDARD' | 'LARGE' | 'EXTRA_LARGE'`.
  * `state.accessibility.highContrast`: `boolean` (Default: `true`).
  * `state.accessibility.tremorDamping`: `boolean` (Default: `true`).
  * `state.accessibility.touchDwellMs`: `number` (Default: `80`).
  * `state.accessibility.audioSpeed`: `'0.75x' | '1.0x'`.
* **Local State (`useState`)**:
  * Form draft mirrors global defaults until explicit commit.
* **Mount / Unmount Lifecycle**:
  * On mount: Reads saved preferences from `AsyncStorage.getItem('accessibility_preferences')`.
  * Context applies typography multipliers to global `theme` object.

---

### 4. NAVIGATION PARAMS & ROUTING
* **Route Name**: `'AccessibilityScreen'`
* **Params Received**: `{ patientId?: string; isInitialOnboarding?: boolean }`
* **Params Passed to Next Screen**:
  * If onboarding: `navigation.replace('PatientHome', { patientId })`
  * If accessed from Caregiver Settings menu: `navigation.goBack()`
* **Navigation Action**: `navigation.replace()` on first onboarding to seal auth pathway.

---

### 5. ACCESSIBILITY SPECIFICATION (MANDATORY SECTION)

```typescript
export const accessibilityScreenA11y = {
  fontScaleRadios: {
    accessibilityRole: 'radio' as const,
    accessibilityHint: "Changes application-wide font scale immediately.",
  },
  contrastSwitch: {
    accessibilityRole: 'switch' as const,
    accessibilityLabel: "ছানি পৰা চকুৰ উচ্চ বৈপৰীত্য ছুইচ। Toggles ultra-high contrast gold-on-black theme.",
  },
  tremorSwitch: {
    accessibilityRole: 'switch' as const,
    accessibilityLabel: "হাতৰ কঁপনি ফিল্টাৰ ছুইচ। Toggles motor tremor absorption.",
  }
};
```

* **Focus Order (Tab Sequence)**:
  1. Back Button (`backButton`)
  2. Spoken Audio Guide (`speakerButton`)
  3. Font Scale Radios (Standard, Large, Huge)
  4. Cataract High Contrast Toggle
  5. Tremor Guard Toggle
  6. Audio Speed Radios (0.75x, 1.0x)
  7. Test Voice Sample Button
  8. Save Settings Button
* **Touch Target Sizes**:
  * Font Scale Buttons: Minimum height $56\text{ px}$.
  * Switches: Hit box $64 \times 48\text{ px}$.
  * Save Button: Height $64\text{ px}$.

---

### 6. OFFLINE BEHAVIOR SPECIFICATION (CRITICAL)
* **Render without internet**: 100% offline.
* **User actions while offline**:
  * Preferences are persisted directly into `AsyncStorage`:
    ```typescript
    await AsyncStorage.setItem('accessibility_preferences', JSON.stringify({
      fontScale,
      highContrast,
      tremorDamping,
      audioSpeed,
      touchDwellMs: 80
    }));
    ```
  * Active app session adopts changes dynamically via React Context re-render.
* **Data persistence**: Zero cloud dependency.

---

### 7. I18N (INTERNATIONALIZATION) KEYS

```json
{
  "en": {
    "accessibility": {
      "title": "Accessibility Settings",
      "subtitle": "Customize text, contrast, and touch comfort",
      "visionTitle": "1. Vision Assistance",
      "fontLabel": "Font Size",
      "normalFont": "Normal (18px)",
      "largeFont": "Large (22px)",
      "hugeFont": "Huge (26px)",
      "cataractTitle": "Cataract High Contrast",
      "cataractDesc": "Pure Black & Ultra-Gold (>13:1 ratio)",
      "motorTitle": "2. Motor & Tremor Guard",
      "tremorTitle": "Tremor Touch Filter",
      "tremorDesc": "Absorbs accidental duplicate taps from hand shakes",
      "dwellLabel": "Protected Touch Dwell Time: 80ms",
      "audioTitle": "3. Spoken Prompts & Audio",
      "speedLabel": "Voice Prompt Speed",
      "slowSpeed": "Slow (0.75x)",
      "normalSpeed": "Normal (1.0x)",
      "testVoice": "Test Voice Output",
      "saveBtn": "Save Preferences & Start Games"
    }
  },
  "as": {
    "accessibility": {
      "title": "সুচলতা আৰু সুবিধা",
      "subtitle": "বয়োজ্যেষ্ঠজনৰ সুবিধা অনুসৰি আখৰ আৰু মাত সজাওক",
      "visionTitle": "১. দৃষ্টি সুবিধা",
      "fontLabel": "আখৰৰ আকাৰ",
      "normalFont": "সাধাৰণ (১৮px)",
      "largeFont": "ডাঙৰ (২২px)",
      "hugeFont": "অতি ডাঙৰ (২৬px)",
      "cataractTitle": "ছানি পৰা চকুৰ মোড",
      "cataractDesc": "ক'লা আৰু সোণালী ৰং (>১৩:১ বৈপৰীত্য)",
      "motorTitle": "২. স্পৰ্শ আৰু কঁপনি প্ৰতিৰোধ",
      "tremorTitle": "হাতৰ কঁপনি ফিল্টাৰ",
      "tremorDesc": "হাত কঁপনিৰ ফলত হোৱা ভুল স্পৰ্শ বাতিল কৰে",
      "dwellLabel": "সুৰক্ষিত স্পৰ্শ সময়: ৮০ মিল্লিছেকেণ্ড",
      "audioTitle": "৩. মাত আৰু নিৰ্দেশনা",
      "speedLabel": "কথা কোৱাৰ গতি",
      "slowSpeed": "ধীৰ (০.৭৫x)",
      "normalSpeed": "স্বাভাবিক (১.০x)",
      "testVoice": "মাতৰ নমুনা শুনক",
      "saveBtn": "সুচলতা সংৰক্ষণ আৰু খেল আৰম্ভ"
    }
  },
  "mn": {
    "accessibility": {
      "title": "ꯑꯦꯛꯁꯦꯁꯤꯕꯤꯂꯤꯇꯤ",
      "subtitle": "ꯑꯍꯜ ꯑꯣꯏꯔꯕꯁꯤꯡꯒꯤ ꯈꯨꯗꯣꯡꯆꯥꯕ",
      "visionTitle": "꯱. ꯃꯤꯠ ꯌꯦꯡꯕꯒꯤ ꯃꯇꯦꯡ",
      "fontLabel": "ꯃꯌꯦꯛ ꯆꯥꯎꯕ",
      "normalFont": "ꯆꯥꯡ ꯃꯥꯟꯅꯕ (18px)",
      "largeFont": "ꯆꯥꯎꯕ (22px)",
      "hugeFont": "ꯌꯥꯝꯅꯥ ꯆꯥꯎꯕ (26px)",
      "cataractTitle": "ꯃꯤꯠ ꯎꯗꯕ ꯑꯃꯁꯨꯡ ꯀꯟꯠꯔꯥꯁ",
      "cataractDesc": "ꯑꯃꯨꯕ ꯑꯃꯁꯨꯡ ꯁꯅꯥꯃꯆꯨ (>13:1)",
      "motorTitle": "꯲. ꯈꯨꯠ ꯈꯠꯄꯥ ꯊꯤꯡꯕ",
      "tremorTitle": "ꯈꯨꯠ ꯈꯠꯄꯒꯤ ꯐꯤꯜꯇꯔ",
      "tremorDesc": "ꯑꯁꯣꯏꯕ ꯇꯥꯞ ꯊꯤꯡꯕ",
      "dwellLabel": "ꯇꯥꯞ ꯇꯧꯕꯒꯤ ꯃꯇꯝ: 80ms",
      "audioTitle": "꯳. ꯈꯣꯟꯊꯣꯛ ꯑꯃꯁꯨꯡ ꯂꯥꯎꯊꯣꯛꯄ",
      "speedLabel": "ꯋꯥ ꯉꯥꯡꯕꯒꯤ ꯌꯥꯡꯕ",
      "slowSpeed": "ꯇꯞꯅꯥ (0.75x)",
      "normalSpeed": "ꯆꯥꯡ ꯃꯥꯟꯅꯕ (1.0x)",
      "testVoice": "ꯈꯣꯟꯊꯣꯛ ꯆꯦꯛ ꯇꯧꯕꯤꯌꯨ",
      "saveBtn": "ꯁꯦꯚ ꯇꯧꯔꯒꯥ ꯁꯥꯟꯅꯕ ꯍꯧꯕꯤꯌꯨ"
    }
  },
  "br": {
    "accessibility": {
      "title": "मदद आरो राहा",
      "subtitle": "आइजें सुबुंनि सुबिदा बादियै मिलाय",
      "visionTitle": "1. नुनायनि राहा",
      "fontLabel": "हांखोनि महर",
      "normalFont": "गेजेरनि (18px)",
      "largeFont": "गेदेर (22px)",
      "hugeFont": "जोबोद गेदेर (26px)",
      "cataractTitle": "मेगननि मोद",
      "cataractDesc": "गोसोम आरो गोलोम महर (>13:1)",
      "motorTitle": "2. आखाय सोमावनाय होबथानाय",
      "tremorTitle": "सोमावनायनि थाखाय फिल्तार",
      "tremorDesc": "गोरोन्थिजों खेबनै थुनायखौ होबथायो",
      "dwellLabel": "थुनाय सम: 80ms",
      "audioTitle": "3. राव आरो सोदोब",
      "speedLabel": "बुंनायनि गोख्रैथि",
      "slowSpeed": "लासै (0.75x)",
      "normalSpeed": "गेजेरनि (1.0x)",
      "testVoice": "रावखौ आनजाद खालाम",
      "saveBtn": "थाय आरो गेलेनो जागाय"
    }
  },
  "hi": {
    "accessibility": {
      "title": "सुलभता एवं सुविधा सेटिंग्स",
      "subtitle": "बुजुर्गों की सुविधा के अनुसार टेक्स्ट और टच कस्टमाइज़ करें",
      "visionTitle": "1. दृष्टि सहायता",
      "fontLabel": "फ़ॉन्ट का आकार",
      "normalFont": "सामान्य (18px)",
      "largeFont": "बड़ा (22px)",
      "hugeFont": "बहुत बड़ा (26px)",
      "cataractTitle": "मोतियाबिंद उच्च कंट्रास्ट",
      "cataractDesc": "काला और सुनहरा रंग (>13:1 अनुपात)",
      "motorTitle": "2. कंपन एवं स्पर्श सुरक्षा",
      "tremorTitle": "कंपन टच फ़िल्टर",
      "tremorDesc": "हाथ कांपने से होने वाले आकस्मिक डबल-टैप को रोकता है",
      "dwellLabel": "सुरक्षित स्पर्श समय: 80ms",
      "audioTitle": "3. वॉइस और ध्वनि",
      "speedLabel": "बोलने की गति",
      "slowSpeed": "धीमी (0.75x)",
      "normalSpeed": "सामान्य (1.0x)",
      "testVoice": "आवाज का नमूना सुनें",
      "saveBtn": "सहेजें और खेल शुरू करें"
    }
  }
}
```

---

### 8. STYLESHEET (`AccessibilityScreen.styles.ts`)

```typescript
import { StyleSheet, Platform } from 'react-native';

export const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#121212',
  },
  scrollContainer: {
    paddingHorizontal: 20,
    paddingVertical: 16,
  },
  topHeader: {
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
  backLabel: {
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
  settingsSectionCard: {
    backgroundColor: '#1E1E1E',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 20,
    padding: 18,
    marginBottom: 16,
  },
  sectionHeaderRow: {
    flexDirection: 'row',
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: '#333333',
    paddingBottom: 10,
    marginBottom: 14,
  },
  sectionIcon: {
    fontSize: 24,
    marginRight: 10,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '800',
    color: '#FFD700',
  },
  subItemContainer: {
    marginBottom: 16,
  },
  subItemLabel: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  subItemHelper: {
    fontSize: 13,
    color: '#BDBDBD',
    marginBottom: 10,
    lineHeight: 18,
  },
  fontScaleButtonRow: {
    flexDirection: 'column',
  },
  fontScaleBtn: {
    backgroundColor: '#262626',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 14,
    paddingVertical: 14,
    paddingHorizontal: 16,
    marginBottom: 8,
    minHeight: 54,
    justifyContent: 'center',
  },
  fontScaleBtnSelected: {
    borderColor: '#FFD700',
    backgroundColor: '#2B2700',
  },
  fontScaleBtnText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#E0E0E0',
  },
  fontScaleBtnTextSelected: {
    color: '#FFD700',
    fontWeight: '800',
  },
  toggleRowContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
  },
  toggleTextWrapper: {
    flex: 0.8,
  },
  toggleTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFFFFF',
    marginBottom: 2,
  },
  toggleDescription: {
    fontSize: 13,
    color: '#BDBDBD',
    lineHeight: 18,
  },
  dwellBadge: {
    backgroundColor: '#1B5E20',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 10,
    alignSelf: 'flex-start',
  },
  dwellBadgeText: {
    fontSize: 13,
    fontWeight: '700',
    color: '#FFFFFF',
  },
  speedButtonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 14,
  },
  speedBtn: {
    flex: 0.48,
    backgroundColor: '#262626',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 12,
    paddingVertical: 12,
    alignItems: 'center',
    minHeight: 50,
  },
  speedBtnSelected: {
    borderColor: '#64B5F6',
    backgroundColor: '#0D273D',
  },
  speedBtnText: {
    fontSize: 14,
    fontWeight: '700',
    color: '#B0BEC5',
  },
  speedBtnTextSelected: {
    color: '#90CAF9',
  },
  testVoiceBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#0D47A1',
    borderRadius: 14,
    paddingVertical: 14,
    minHeight: 52,
  },
  testVoiceIcon: {
    fontSize: 18,
    marginRight: 8,
  },
  testVoiceText: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFFFFF',
  },
  actionContainer: {
    marginVertical: 20,
  },
  saveSettingsBtn: {
    backgroundColor: '#FFD700',
    borderRadius: 20,
    paddingVertical: 18,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 64,
  },
  saveSettingsBtnText: {
    fontSize: 18,
    fontWeight: '800',
    color: '#121212',
  },
});
```

---

### 9. LIFECYCLE METHODS & SIDE EFFECTS
* **Global Context Broadcast**:
  ```typescript
  const handleSaveAccessibilitySettings = async () => {
    const prefs = {
      fontScale,
      highContrast,
      tremorDamping,
      audioSpeed,
      touchDwellMs: 80,
    };

    await AsyncStorage.setItem('accessibility_preferences', JSON.stringify(prefs));
    accessibilityContext.updatePreferences(prefs);

    if (isInitialOnboarding) {
      navigation.replace('PatientHome', { patientId });
    } else {
      navigation.goBack();
    }
  };
  ```

---

### 10. TEST SCENARIOS (Manual QA Checklist)
1. **Dynamic Font Rescaling**: Switching to "Huge (26px)" expands headings across all subsequent screens.
2. **Cataract Switch Toggle**: Toggling high-contrast applies Pure Black/Pure Gold theme immediately.
3. **Tremor Damping Active**: Verifies that rapid double taps on buttons ($<600\text{ms}$) execute only once.
4. **Dwell Time Assertion**: Taps lasting $<80\text{ms}$ are filtered out as accidental brushes.
5. **Voice Speed Test**: Tapping "Test Voice Output" plays audio at 0.75x tempo.
6. **AsyncStorage Persistence**: Restarting app preserves all accessibility settings.
7. **TalkBack Readout**: Voice guide clearly announces switch toggles and radio selections.
8. **Contrast Ratio Audit**: Confirms all text/card pairs exceed $7:1$ contrast ratio.
9. **Navigation Exit**: Onboarding flag navigates directly to `PatientHome`.
10. **Touch Target Measurement**: Confirms every interactive element meets or exceeds $54\text{ px}$.

---

### 11. DATA MODEL IMPACT
* **Storage**: Non-sensitive UI preference persisted in `AsyncStorage`.
* **Sync Queue**: Does not generate sync actions.
