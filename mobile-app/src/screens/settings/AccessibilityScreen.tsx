import React, { useState } from 'react';
import {
  StyleSheet,
  View,
  Text,
  SafeAreaView,
  StatusBar,
  TouchableOpacity,
  Switch,
  ScrollView,
} from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function AccessibilityScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const isInitialOnboarding = route.params?.isInitialOnboarding;

  const [fontScale, setFontScale] = useState('STANDARD');
  const [highContrast, setHighContrast] = useState(true);
  const [tremorDamping, setTremorDamping] = useState(true);
  const [audioSpeed, setAudioSpeed] = useState('1.0x');

  const handleSave = async () => {
    const prefs = {
      fontScale,
      highContrast,
      tremorDamping,
      audioSpeed,
      touchDwellMs: 80,
    };

    try {
      await AsyncStorage.setItem('accessibility_preferences', JSON.stringify(prefs));
    } catch (err) {
      // Non-fatal
    }

    if (isInitialOnboarding) {
      // In production, navigate to PatientHome or GameSelection
      navigation.replace('RoleSelection'); // Or PatientHome
    } else {
      navigation.goBack();
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" backgroundColor="#121212" />
      <ScrollView contentContainerStyle={styles.scrollContent}>
        
        {/* Top Bar */}
        <View style={styles.topRow}>
          <TouchableOpacity
            style={styles.backBtn}
            accessibilityRole="button"
            accessibilityLabel="উভতি যাওক"
            onPress={() => navigation.goBack()}
          >
            <Text style={styles.backArrow}>←</Text>
            <Text style={styles.backText}>উভতি যাওক</Text>
          </TouchableOpacity>

          <View style={styles.speakerBtn}>
            <Text style={styles.speakerIcon}>🔊</Text>
          </View>
        </View>

        <View style={styles.titleWrapper}>
          <Text style={styles.title} accessibilityRole="header">
            সুচলতা আৰু সুবিধা
          </Text>
          <Text style={styles.subtitle}>
            Customize text size, contrast, and touch for elderly comfort
          </Text>
        </View>

        {/* 1. Vision */}
        <View style={styles.card}>
          <Text style={styles.heading}>👁️ ১. দৃষ্টি সুবিধা (Vision Assistance)</Text>
          
          <Text style={styles.subLabel}>আখৰৰ আকাৰ (Font Size)</Text>
          {[
            { key: 'STANDARD', label: 'সাধাৰণ (Normal 18px)' },
            { key: 'LARGE', label: 'ডাঙৰ (Large 22px)' },
            { key: 'EXTRA_LARGE', label: 'অতি ডাঙৰ (Huge 26px)' },
          ].map(f => (
            <TouchableOpacity
              key={f.key}
              style={[styles.choiceBtn, fontScale === f.key && styles.choiceBtnSelected]}
              onPress={() => setFontScale(f.key)}
            >
              <Text style={[styles.choiceText, fontScale === f.key && styles.choiceTextSelected]}>
                {f.label}
              </Text>
            </TouchableOpacity>
          ))}

          <View style={styles.toggleRow}>
            <View style={styles.toggleTextWrapper}>
              <Text style={styles.toggleTitle}>ছানি পৰা চকুৰ মোড (Cataract Contrast)</Text>
              <Text style={styles.toggleDesc}>Pure Black & Gold (>13:1 ratio)</Text>
            </View>
            <Switch
              value={highContrast}
              onValueChange={setHighContrast}
              trackColor={{ false: '#424242', true: '#FFD700' }}
              thumbColor={highContrast ? '#121212' : '#BDBDBD'}
            />
          </View>
        </View>

        {/* 2. Tremor Guard */}
        <View style={styles.card}>
          <Text style={styles.heading}>🖐️ ২. কঁপনি প্ৰতিৰোধ (Tremor Guard)</Text>
          
          <View style={styles.toggleRow}>
            <View style={styles.toggleTextWrapper}>
              <Text style={styles.toggleTitle}>হাতৰ কঁপনি ফিল্টাৰ (Tremor Touch Filter)</Text>
              <Text style={styles.toggleDesc}>
                ভুলবশতঃ দুবাৰ টিপা পৰিলে দ্বিতীয়টো স্পৰ্শ বাতিল কৰে (Debounce).
              </Text>
            </View>
            <Switch
              value={tremorDamping}
              onValueChange={setTremorDamping}
              trackColor={{ false: '#424242', true: '#00E676' }}
              thumbColor={tremorDamping ? '#121212' : '#BDBDBD'}
            />
          </View>

          <View style={styles.dwellBox}>
            <Text style={styles.dwellText}>৮০ মিল্লিছেকেণ্ড সুৰক্ষিত স্পৰ্শ (80ms Dwell Protection)</Text>
          </View>
        </View>

        {/* 3. Audio & Voice */}
        <View style={styles.card}>
          <Text style={styles.heading}>🔊 ৩. মাত আৰু নিৰ্দেশনা (Voice Speed)</Text>
          
          <View style={styles.speedRow}>
            {[
              { key: '0.75x', label: 'ধীৰ (Slow 0.75x)' },
              { key: '1.0x', label: 'স্বাভাবিক (Normal 1.0x)' },
            ].map(sp => (
              <TouchableOpacity
                key={sp.key}
                style={[styles.speedBtn, audioSpeed === sp.key && styles.speedBtnSelected]}
                onPress={() => setAudioSpeed(sp.key)}
              >
                <Text style={[styles.speedText, audioSpeed === sp.key && styles.speedTextSelected]}>
                  {sp.label}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* Save CTA */}
        <View style={styles.actionWrapper}>
          <TouchableOpacity
            style={styles.saveBtn}
            accessibilityRole="button"
            accessibilityLabel="সুচলতা সংৰক্ষণ আৰু খেল আৰম্ভ"
            onPress={handleSave}
            activeOpacity={0.8}
          >
            <Text style={styles.saveBtnText}>
              সুচলতা সংৰক্ষণ আৰু আৰম্ভ (Save & Continue) ➔
            </Text>
          </TouchableOpacity>
        </View>

      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#121212',
  },
  scrollContent: {
    paddingHorizontal: 20,
    paddingVertical: 16,
  },
  topRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  backBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#262626',
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 20,
    minHeight: 48,
  },
  backArrow: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFD700',
    marginRight: 6,
  },
  backText: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFFFFF',
  },
  speakerBtn: {
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
    fontSize: 20,
  },
  titleWrapper: {
    marginBottom: 16,
  },
  title: {
    fontSize: 28,
    fontWeight: '800',
    color: '#FFD700',
    marginBottom: 6,
  },
  subtitle: {
    fontSize: 16,
    color: '#E0E0E0',
    lineHeight: 22,
  },
  card: {
    backgroundColor: '#1E1E1E',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 18,
    padding: 16,
    marginBottom: 16,
  },
  heading: {
    fontSize: 17,
    fontWeight: '800',
    color: '#FFD700',
    marginBottom: 12,
  },
  subLabel: {
    fontSize: 15,
    fontWeight: '600',
    color: '#E0E0E0',
    marginBottom: 8,
  },
  choiceBtn: {
    backgroundColor: '#262626',
    borderWidth: 1.5,
    borderColor: '#424242',
    borderRadius: 12,
    paddingVertical: 12,
    paddingHorizontal: 14,
    marginBottom: 8,
  },
  choiceBtnSelected: {
    borderColor: '#FFD700',
    backgroundColor: '#2B2700',
  },
  choiceText: {
    fontSize: 15,
    color: '#E0E0E0',
    fontWeight: '600',
  },
  choiceTextSelected: {
    color: '#FFD700',
    fontWeight: '800',
  },
  toggleRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 8,
    paddingTop: 8,
    borderTopWidth: 1,
    borderTopColor: '#333333',
  },
  toggleTextWrapper: {
    flex: 0.8,
  },
  toggleTitle: {
    fontSize: 15,
    fontWeight: '700',
    color: '#FFFFFF',
    marginBottom: 2,
  },
  toggleDesc: {
    fontSize: 13,
    color: '#BDBDBD',
  },
  dwellBox: {
    backgroundColor: '#1B5E20',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 8,
    alignSelf: 'flex-start',
    marginTop: 10,
  },
  dwellText: {
    fontSize: 13,
    fontWeight: '700',
    color: '#FFFFFF',
  },
  speedRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  speedBtn: {
    flex: 0.48,
    backgroundColor: '#262626',
    borderWidth: 1.5,
    borderColor: '#424242',
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
  },
  speedBtnSelected: {
    borderColor: '#64B5F6',
    backgroundColor: '#0D273D',
  },
  speedText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#B0BEC5',
  },
  speedTextSelected: {
    color: '#90CAF9',
    fontWeight: '800',
  },
  actionWrapper: {
    marginVertical: 16,
  },
  saveBtn: {
    backgroundColor: '#FFD700',
    borderRadius: 18,
    paddingVertical: 18,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 64,
  },
  saveBtnText: {
    fontSize: 18,
    fontWeight: '800',
    color: '#121212',
  },
});
