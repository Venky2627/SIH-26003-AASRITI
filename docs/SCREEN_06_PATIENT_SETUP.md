# SCREEN 6: PATIENT SETUP SCREEN (ৰোগীৰ পৰিচয় আৰু ভাষা / ꯑꯅꯥꯕꯒꯤ ꯃꯁꯛ)

---

### 1. FILE STRUCTURE
- **Screen File**: `mobile-app/src/screens/auth/PatientSetupScreen.tsx`
- **Styles**: `mobile-app/src/screens/auth/styles/PatientSetupScreen.styles.ts`
- **Components**: `mobile-app/src/components/common/LanguageSelectorCards.tsx`, `mobile-app/src/components/common/ClinicalStagePicker.tsx`
- **Repositories**: `mobile-app/src/database/repositories/PatientRepository.ts`
- **i18n Keys**: `mobile-app/src/locales/{en,as,mn,br,hi}/patientSetup.json`
- **Unit & Snapshot Tests**: `mobile-app/src/screens/auth/__tests__/PatientSetupScreen.test.tsx`

---

### 2. COMPONENT HIERARCHY (React Native JSX Tree)
```jsx
<SafeAreaView style={styles.safeArea}>
  <StatusBar barStyle="light-content" backgroundColor="#121212" />
  <ScrollView contentContainerStyle={styles.scrollContainer}>
    
    {/* Step Header & Audio Assistance Button */}
    <View style={styles.topHeader}>
      <View style={styles.stepBadge}>
        <Text style={styles.stepBadgeText}>স্তৰ ২ / ২ (Step 2 of 2)</Text>
      </View>

      <TouchableOpacity
        style={styles.speakerButton}
        accessibilityRole="button"
        accessibilityLabel="ৰোগী নিৰ্বাচনৰ নিৰ্দেশনা শুনক (Listen to patient setup instructions in Assamese)"
        onPress={handlePlayVoicePrompt}
      >
        <Text style={styles.speakerIcon}>🔊</Text>
      </TouchableOpacity>
    </View>

    {/* Header Title & Subtitle */}
    <View style={styles.titleContainer}>
      <Text style={styles.screenTitle} accessibilityRole="header">
        ৰোগীৰ পৰিচয় আৰু ভাষা
      </Text>
      <Text style={styles.screenSubtitle}>
        Set up the elderly patient profile and preferred mother tongue
      </Text>
    </View>

    {/* Section 1: Regional Mother Tongue Selection (Assamese, Manipuri, Bodo, Hindi, English) */}
    <View style={styles.cardSection}>
      <Text style={styles.sectionHeading}>১. মাতৃভাষা নিৰ্বাচন (Select Native Language)</Text>
      <Text style={styles.sectionHelper}>All games and voice prompts will adapt to this dialect.</Text>
      
      <View style={styles.languageGrid}>
        {[
          { code: 'as', name: 'অসমীয়া (Assamese)', region: 'Assam / Brahmaputra Valley' },
          { code: 'mn', name: 'ꯃꯤꯇꯩꯂꯣꯟ (Manipuri)', region: 'Manipur / Imphal Valley' },
          { code: 'br', name: 'बर’ (Bodo)', region: 'Bodoland Territorial Region' },
          { code: 'hi', name: 'हिन्दी (Hindi)', region: 'National / Secondary' },
          { code: 'en', name: 'English', region: 'Medical Standard' },
        ].map((lang) => (
          <TouchableOpacity
            key={lang.code}
            style={[
              styles.langCard,
              selectedLanguage === lang.code && styles.langCardSelected
            ]}
            accessibilityRole="radio"
            accessibilityState={{ selected: selectedLanguage === lang.code }}
            accessibilityLabel={`${lang.name} for ${lang.region}`}
            onPress={() => setSelectedLanguage(lang.code)}
          >
            <View style={styles.langRadioCircle}>
              {selectedLanguage === lang.code && <View style={styles.langRadioInner} />}
            </View>
            <View style={styles.langTextWrapper}>
              <Text style={[
                styles.langName,
                selectedLanguage === lang.code && styles.langNameSelected
              ]}>
                {lang.name}
              </Text>
              <Text style={styles.langRegion}>{lang.region}</Text>
            </View>
          </TouchableOpacity>
        ))}
      </View>
    </View>

    {/* Section 2: Pseudonymous Clinical Identification (DPDA Privacy Guard) */}
    <View style={styles.cardSection}>
      <Text style={styles.sectionHeading}>২. ৰোগীৰ গোপন সংকেত (Pseudonym Code)</Text>
      <Text style={styles.sectionHelper}>
        DPDA ২০২৩ সুৰক্ষা: ৰোগীৰ প্ৰকৃত নামৰ সলনি এটা গোপন সংকেত ব্যৱহাৰ কৰা হয়।
      </Text>
      
      <View style={styles.pseudonymRow}>
        <TextInput
          style={styles.pseudonymInput}
          value={pseudonymCode}
          editable={false} // Auto-generated for security
          accessible={true}
          accessibilityLabel={`Generated Pseudonym: ${pseudonymCode}`}
        />
        <TouchableOpacity
          style={styles.refreshCodeBtn}
          accessibilityRole="button"
          accessibilityLabel="নতুন সংকেত তৈয়াৰ কৰক (Regenerate pseudonym code)"
          onPress={handleRegenerateCode}
        >
          <Text style={styles.refreshCodeText}>🔄 সলনি (Regen)</Text>
        </TouchableOpacity>
      </View>
    </View>

    {/* Section 3: Birth Year & Gender */}
    <View style={styles.cardSection}>
      <Text style={styles.sectionHeading}>৩. জন্মৰ বৰ্ষ আৰু লিংগ (Birth Year & Gender)</Text>
      <Text style={styles.sectionHelper}>Used strictly to calibrate cognitive age norms.</Text>

      <View style={styles.demographicRow}>
        <View style={styles.birthYearContainer}>
          <Text style={styles.subFieldLabel}>জন্মৰ বৰ্ষ (Year of Birth)</Text>
          <TextInput
            style={styles.birthYearInput}
            value={birthYear}
            onChangeText={setBirthYear}
            keyboardType="number-pad"
            maxLength={4}
            placeholder="1955"
            placeholderTextColor="#757575"
            accessible={true}
            accessibilityLabel="ৰোগীৰ জন্ম বৰ্ষ চাৰিটা সংখ্যাত লিখক (Four digit birth year)"
          />
        </View>

        <View style={styles.genderContainer}>
          <Text style={styles.subFieldLabel}>লিংগ (Gender)</Text>
          <View style={styles.genderButtonRow}>
            {['M', 'F', 'O'].map((g) => (
              <TouchableOpacity
                key={g}
                style={[
                  styles.genderBtn,
                  gender === g && styles.genderBtnSelected
                ]}
                accessibilityRole="radio"
                accessibilityState={{ selected: gender === g }}
                accessibilityLabel={g === 'M' ? 'পুৰুষ (Male)' : g === 'F' ? 'মহিলা (Female)' : 'অন্যান্য (Other)'}
                onPress={() => setGender(g)}
              >
                <Text style={[styles.genderBtnText, gender === g && styles.genderBtnTextSelected]}>
                  {g === 'M' ? 'পুৰুষ' : g === 'F' ? 'মহিলা' : 'অন্য'}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
      </View>
    </View>

    {/* Section 4: Clinical Stage Classification */}
    <View style={styles.cardSection}>
      <Text style={styles.sectionHeading}>৪. জ্ঞানমূলক স্তৰ (Cognitive Stage)</Text>
      <Text style={styles.sectionHelper}>Select based on prior clinical assessment if known.</Text>

      <View style={styles.stageGrid}>
        {[
          { key: 'MCI', title: 'মৃদু স্মৃতি বিভ্ৰাট (Mild Cognitive Impairment - MCI)', desc: 'স্মৃতিৰ সামান্য হ্ৰাস কিন্তু দৈনন্দিন কাম নিজে কৰিব পাৰে।' },
          { key: 'MILD_DEMENTIA', title: 'প্ৰাৰম্ভিক ডিমেনচিয়া (Early Dementia)', desc: 'সঘনাই পাহৰি যোৱা, শব্দ বিচাৰি নোপোৱা আৰু দিশ হেৰুওৱা।' },
          { key: 'PRECLINICAL', title: 'সুস্থ / প্ৰতিৰোধমূলক (Healthy / Wellness)', desc: 'মগজু সতেজ ৰাখিবলৈ স্মৃতি ব্যায়াম।' },
        ].map((stage) => (
          <TouchableOpacity
            key={stage.key}
            style={[
              styles.stageCard,
              clinicalStage === stage.key && styles.stageCardSelected
            ]}
            accessibilityRole="radio"
            accessibilityState={{ selected: clinicalStage === stage.key }}
            accessibilityLabel={`${stage.title}`}
            onPress={() => setClinicalStage(stage.key)}
          >
            <Text style={[
              styles.stageTitle,
              clinicalStage === stage.key && styles.stageTitleSelected
            ]}>
              {stage.title}
            </Text>
            <Text style={styles.stageDesc}>{stage.desc}</Text>
          </TouchableOpacity>
        ))}
      </View>
    </View>

    {/* Complete Setup Action Button */}
    <View style={styles.actionContainer}>
      <TouchableOpacity
        style={[
          styles.completeBtn,
          (!birthYear || birthYear.length < 4 || !clinicalStage) && styles.completeBtnDisabled
        ]}
        disabled={!birthYear || birthYear.length < 4 || !clinicalStage || isSubmitting}
        accessibilityRole="button"
        accessibilityLabel="প্ৰফাইল সম্পূৰ্ণ কৰক আৰু খেল আৰম্ভ কৰক (Complete Setup & Start Games)"
        onPress={handleCompletePatientSetup}
        activeOpacity={0.8}
      >
        {isSubmitting ? (
          <ActivityIndicator color="#121212" size="small" />
        ) : (
          <Text style={styles.completeBtnText}>
            প্ৰফাইল সম্পূৰ্ণ কৰক (Complete Setup) ➔
          </Text>
        )}
      </TouchableOpacity>
    </View>

  </ScrollView>
</SafeAreaView>
```

---

### 3. STATE MANAGEMENT (Redux Slice + Local State)
* **Global State (Redux `patientSlice`)**:
  * Action: `dispatch(setActivePatient(newPatientRecord))`.
* **Local State (`useState`)**:
  * `const [selectedLanguage, setSelectedLanguage] = useState<string>('as')`
  * `const [pseudonymCode, setPseudonymCode] = useState<string>('AS-KAM-2025-0042')`
  * `const [birthYear, setBirthYear] = useState<string>('1956')`
  * `const [gender, setGender] = useState<string>('M')`
  * `const [clinicalStage, setClinicalStage] = useState<string>('MCI')`
  * `const [isSubmitting, setIsSubmitting] = useState<boolean>(false)`
* **Mount / Unmount Lifecycle**:
  * On mount: Auto-generates regional pseudonym based on device location (e.g. `AS` for Assam, `MN` for Manipur, `MZ` for Mizoram).
  * Auto-selects regional language matching device locale.

---

### 4. NAVIGATION PARAMS & ROUTING
* **Route Name**: `'PatientSetup'`
* **Params Received**: `{ caregiverId?: string; isNew?: boolean }`
* **Params Passed to Next Screen**:
  * `navigation.replace('AccessibilityScreen', { patientId, isInitialOnboarding: true })`
* **Navigation Action**: `navigation.replace()`

---

### 5. ACCESSIBILITY SPECIFICATION (MANDATORY SECTION)

```typescript
export const patientSetupAccessibilityConfig = {
  langRadio: {
    accessibilityRole: 'radio' as const,
    accessibilityHint: "Selects mother tongue for on-device ASR and voice gameplay.",
  },
  birthYearInput: {
    accessibilityRole: 'text' as const,
    accessibilityLabel: "ৰোগীৰ জন্ম বৰ্ষ। Enter 4-digit birth year between 1920 and 1970.",
  },
  completeButton: {
    accessibilityRole: 'button' as const,
    accessibilityLabel: "প্ৰফাইল সম্পূৰ্ণ কৰক। Finishes setup and opens accessibility adjustments.",
  }
};
```

* **Focus Order (Tab Sequence)**:
  1. Voice Instructions (`speakerButton`)
  2. Language Options 1 through 5
  3. Pseudonym Regeneration Button (`refreshCodeBtn`)
  4. Birth Year Input (`birthYearInput`)
  5. Gender Buttons (Male, Female, Other)
  6. Clinical Stage Radio Cards (MCI, Early Dementia, Healthy)
  7. Complete Setup Button (`completeBtn`)
* **Touch Target Sizes**:
  * Language Cards: Minimum height $64\text{ px}$.
  * Gender Buttons: $96 \times 52\text{ px}$.
  * Complete Button: Height $64\text{ px}$.

---

### 6. OFFLINE BEHAVIOR SPECIFICATION (CRITICAL)
* **Render without internet**: 100% offline.
* **User actions while offline**:
  * Inserts patient profile into SQLite `patients` table:
    ```sql
    INSERT INTO patients (id, pseudonym_code, birth_year, gender, primary_language, clinical_stage, is_synced)
    VALUES (?, ?, ?, ?, ?, ?, 0);
    ```
  * If a caregiver ID was passed, links caregiver to patient atomically:
    ```sql
    UPDATE caregivers SET patient_id = ? WHERE id = ?;
    ```
* **Error states**: Birth year validation ensures year is between 1920 and 2005. If invalid, displays friendly notice.
* **Data persistence**: Direct ACID commit to SQLCipher database.

---

### 7. I18N (INTERNATIONALIZATION) KEYS

```json
{
  "en": {
    "patientSetup": {
      "step": "Step 2 of 2",
      "title": "Patient Setup & Language",
      "subtitle": "Set up the elder's profile and mother tongue",
      "langHeading": "1. Select Native Language",
      "pseudonymHeading": "2. Patient Pseudonym Code",
      "pseudonymHelper": "Under DPDA 2023, names are replaced with secure codes.",
      "regen": "Regen",
      "demographicHeading": "3. Birth Year & Gender",
      "yearLabel": "Year of Birth",
      "genderLabel": "Gender",
      "male": "Male",
      "female": "Female",
      "other": "Other",
      "stageHeading": "4. Cognitive Stage",
      "completeBtn": "Complete Setup & Customize Display"
    }
  },
  "as": {
    "patientSetup": {
      "step": "স্তৰ ২ / ২",
      "title": "ৰোগীৰ পৰিচয় আৰু ভাষা",
      "subtitle": "বয়োজ্যেষ্ঠজনৰ প্ৰফাইল আৰু মাতৃভাষা বাছক",
      "langHeading": "১. মাতৃভাষা নিৰ্বাচন",
      "pseudonymHeading": "২. ৰোগীৰ গোপন সংকেত",
      "pseudonymHelper": "DPDA ২০২৩ সুৰক্ষা: নামৰ সলনি গোপন সংকেত ব্যৱহাৰ কৰা হয়।",
      "regen": "সলনি",
      "demographicHeading": "৩. জন্মৰ বৰ্ষ আৰু লিংগ",
      "yearLabel": "জন্মৰ বৰ্ষ",
      "genderLabel": "লিংগ",
      "male": "পুৰুষ",
      "female": "মহিলা",
      "other": "অন্যান্য",
      "stageHeading": "৪. জ্ঞানমূলক স্তৰ",
      "completeBtn": "প্ৰফাইল সম্পূৰ্ণ কৰক আৰু আগবাঢ়ক"
    }
  },
  "mn": {
    "patientSetup": {
      "step": "ꯇꯥꯡꯀꯛ ꯲ / ꯲",
      "title": "ꯑꯅꯥꯕꯒꯤ ꯃꯁꯛ ꯑꯃꯁꯨꯡ ꯂꯣꯟ",
      "subtitle": "ꯑꯍꯜ ꯑꯗꯨꯒꯤ ꯄ꯭ꯔꯣꯐꯥꯏꯜ ꯁꯦꯝꯕꯤꯌꯨ",
      "langHeading": "꯱. ꯏꯃꯥꯂꯣꯟ ꯈꯅꯕꯤꯌꯨ",
      "pseudonymHeading": "꯲. ꯑꯅꯥꯕꯒꯤ ꯂꯣꯠꯁꯤꯜꯂꯕ ꯀꯣꯗ",
      "pseudonymHelper": "DPDA 2023 ꯃꯇꯨꯡ ꯏꯟꯅꯥ ꯃꯤꯡꯒꯤ ꯃꯍꯨꯠꯇꯥ ꯀꯣꯗ ꯁꯤꯖꯤꯟꯅꯩ।",
      "regen": "ꯍꯣꯡꯗꯣꯛꯎ",
      "demographicHeading": "꯳. ꯄꯣꯛꯈꯤꯕ ꯆꯍꯤ ꯑꯃꯁꯨꯡ ꯂꯤꯡꯒ",
      "yearLabel": "ꯄꯣꯛꯈꯤꯕ ꯆꯍꯤ",
      "genderLabel": "ꯂꯤꯡꯒ",
      "male": "ꯅꯨꯄꯥ",
      "female": "ꯅꯨꯄꯤ",
      "other": "ꯑꯇꯣꯞꯄꯥ",
      "stageHeading": "꯴. ꯋꯥꯈꯜꯒꯤ ꯊꯥꯛ",
      "completeBtn": "ꯃꯄꯨꯡ ꯐꯥꯅꯥ ꯁꯦꯝꯕꯤꯌꯨ"
    }
  },
  "br": {
    "patientSetup": {
      "step": "खोन्दो 2 / 2",
      "title": "बिमोननि सिनायथि आरो राव",
      "subtitle": "आइजें सुबुंनि सिनायथि आरो गावनि राव सायख",
      "langHeading": "1. गावनि राव सायख",
      "pseudonymHeading": "2. बिमोननि गोसोखां कोड",
      "pseudonymHelper": "DPDA 2023 नि बादियै मुंनि सोलाय कोड बाहायनाय जायो।",
      "regen": "सोलाय",
      "demographicHeading": "3. जोनोम बोसोर आरो लिंग",
      "yearLabel": "जोनोम बोसोर",
      "genderLabel": "लिंग",
      "male": "हौवा",
      "female": "हिनजाव",
      "other": "गुबुन",
      "stageHeading": "4. गोसोखां थाखो",
      "completeBtn": "गासै आबुं खालाम"
    }
  },
  "hi": {
    "patientSetup": {
      "step": "चरण 2 / 2",
      "title": "मरीज की पहचान और भाषा",
      "subtitle": "बुजुर्ग मरीज का प्रोफाइल और मातृभाषा चुनें",
      "langHeading": "1. मातृभाषा का चयन",
      "pseudonymHeading": "2. मरीज का छद्म नाम (Pseudonym Code)",
      "pseudonymHelper": "DPDA 2023 के तहत वास्तविक नाम के स्थान पर कोड का उपयोग होता है।",
      "regen": "नया कोड",
      "demographicHeading": "3. जन्म वर्ष और लिंग",
      "yearLabel": "जन्म वर्ष",
      "genderLabel": "लिंग",
      "male": "पुरुष",
      "female": "महिला",
      "other": "अन्य",
      "stageHeading": "4. संज्ञानात्मक अवस्था",
      "completeBtn": "प्रोफाइल पूर्ण करें और आगे बढ़ें"
    }
  }
}
```

---

### 8. STYLESHEET (`PatientSetupScreen.styles.ts`)

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
  stepBadge: {
    backgroundColor: '#1E1E1E',
    borderWidth: 1,
    borderColor: '#FFD700',
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderRadius: 14,
  },
  stepBadgeText: {
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
  cardSection: {
    backgroundColor: '#1E1E1E',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 20,
    padding: 18,
    marginBottom: 16,
  },
  sectionHeading: {
    fontSize: 18,
    fontWeight: '700',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  sectionHelper: {
    fontSize: 14,
    color: '#BDBDBD',
    marginBottom: 14,
    lineHeight: 20,
  },
  languageGrid: {
    marginTop: 4,
  },
  langCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#262626',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 14,
    paddingHorizontal: 16,
    paddingVertical: 12,
    marginBottom: 10,
    minHeight: 56,
  },
  langCardSelected: {
    borderColor: '#FFD700',
    backgroundColor: '#2A2600',
  },
  langRadioCircle: {
    width: 24,
    height: 24,
    borderRadius: 12,
    borderWidth: 2,
    borderColor: '#FFD700',
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 14,
  },
  langRadioInner: {
    width: 12,
    height: 12,
    borderRadius: 6,
    backgroundColor: '#FFD700',
  },
  langTextWrapper: {
    flex: 1,
  },
  langName: {
    fontSize: 18,
    fontWeight: '700',
    color: '#E0E0E0',
  },
  langNameSelected: {
    color: '#FFD700',
    fontWeight: '800',
  },
  langRegion: {
    fontSize: 13,
    color: '#9E9E9E',
  },
  pseudonymRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  pseudonymInput: {
    flex: 1,
    backgroundColor: '#121212',
    borderWidth: 2,
    borderColor: '#00E676',
    borderRadius: 12,
    paddingHorizontal: 16,
    paddingVertical: 12,
    fontSize: 18,
    fontWeight: 'bold',
    color: '#00E676',
    letterSpacing: 2,
  },
  refreshCodeBtn: {
    marginLeft: 10,
    backgroundColor: '#262626',
    paddingHorizontal: 14,
    paddingVertical: 14,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#FFD700',
  },
  refreshCodeText: {
    fontSize: 14,
    fontWeight: '700',
    color: '#FFD700',
  },
  demographicRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  birthYearContainer: {
    flex: 0.46,
  },
  genderContainer: {
    flex: 0.5,
  },
  subFieldLabel: {
    fontSize: 15,
    fontWeight: '600',
    color: '#E0E0E0',
    marginBottom: 8,
  },
  birthYearInput: {
    backgroundColor: '#121212',
    borderWidth: 2,
    borderColor: '#616161',
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 18,
    color: '#FFFFFF',
    textAlign: 'center',
    minHeight: 52,
  },
  genderButtonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  genderBtn: {
    backgroundColor: '#262626',
    borderWidth: 1.5,
    borderColor: '#616161',
    borderRadius: 10,
    paddingVertical: 12,
    paddingHorizontal: 10,
    minWidth: 46,
    alignItems: 'center',
  },
  genderBtnSelected: {
    borderColor: '#FFD700',
    backgroundColor: '#2A2600',
  },
  genderBtnText: {
    fontSize: 14,
    fontWeight: '700',
    color: '#BDBDBD',
  },
  genderBtnTextSelected: {
    color: '#FFD700',
  },
  stageGrid: {
    marginTop: 4,
  },
  stageCard: {
    backgroundColor: '#262626',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 14,
    padding: 14,
    marginBottom: 10,
  },
  stageCardSelected: {
    borderColor: '#64B5F6',
    backgroundColor: '#0F263B',
  },
  stageTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  stageTitleSelected: {
    color: '#90CAF9',
  },
  stageDesc: {
    fontSize: 13,
    color: '#B0BEC5',
    lineHeight: 18,
  },
  actionContainer: {
    marginVertical: 20,
  },
  completeBtn: {
    backgroundColor: '#FFD700',
    borderRadius: 20,
    paddingVertical: 18,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 64,
  },
  completeBtnDisabled: {
    backgroundColor: '#424242',
  },
  completeBtnText: {
    fontSize: 18,
    fontWeight: '800',
    color: '#121212',
  },
});
```

---

### 9. LIFECYCLE METHODS & SIDE EFFECTS
* **Regional Language Switch & Database Commit**:
  ```typescript
  const handleCompletePatientSetup = async () => {
    setIsSubmitting(true);
    try {
      const db = await getDatabase();
      const patientId = Crypto.randomUUID();

      await db.withTransactionAsync(async () => {
        // 1. Insert patient
        await db.runAsync(
          `INSERT INTO patients (id, pseudonym_code, birth_year, gender, primary_language, clinical_stage, is_synced)
           VALUES (?, ?, ?, ?, ?, ?, 0);`,
          [patientId, pseudonymCode, parseInt(birthYear, 10), gender, selectedLanguage, clinicalStage]
        );

        // 2. Link to caregiver if exists
        if (caregiverId) {
          await db.runAsync(`UPDATE caregivers SET patient_id = ? WHERE id = ?;`, [patientId, caregiverId]);
        }

        // 3. Set globally active patient profile
        await AsyncStorage.setItem('active_patient_id', patientId);
        await AsyncStorage.setItem('active_language', selectedLanguage);
      });

      // Navigate to accessibility customization
      navigation.replace('AccessibilityScreen', { patientId, isInitialOnboarding: true });
    } catch (error) {
      console.error('Failed to complete patient setup:', error);
    } finally {
      setIsSubmitting(false);
    }
  };
  ```

---

### 10. TEST SCENARIOS (Manual QA Checklist)
1. **Language Switching**: Selecting Manipuri instantly changes all screen labels to Meitei Mayek text.
2. **Pseudonym Regeneration**: Tapping refresh produces new random code (e.g. `MN-IMP-2025-0104`).
3. **Birth Year Validation**: Typing "1850" or "2024" flags out-of-range helper alert.
4. **Gender Radio Group**: Selecting Female highlights button and updates internal state.
5. **Stage Card Selection**: MCI card selection highlights with blue accessibility border.
6. **Caregiver Linking Verification**: Checks SQLite that `caregivers.patient_id` matches newly inserted `patients.id`.
7. **Offline SQLite ACID Execution**: App process kill mid-transaction causes zero corrupt partial records.
8. **TalkBack Navigation**: Screen reader announces language choices clearly with regional metadata.
9. **Font Scaling (200%)**: Radio buttons and text fields expand without horizontal truncation.
10. **Navigation Gate**: Successfully replaces route stack with `AccessibilityScreen`.

---

### 11. DATA MODEL IMPACT
* **Tables Affected**: `patients` (Inserted), `caregivers` (Updated).
* **Privacy**: Strictly complies with DPDA 2023: Zero real names, zero precise dates of birth (birth year only).
* **Sync Queue**: Generates queue row for `patients` table.
