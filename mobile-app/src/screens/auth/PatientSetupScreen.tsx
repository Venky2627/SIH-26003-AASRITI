import React, { useState } from 'react';
import {
  StyleSheet,
  View,
  Text,
  SafeAreaView,
  StatusBar,
  TextInput,
  TouchableOpacity,
  ScrollView,
} from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';
import { getDatabase } from '@/database/connection';

export default function PatientSetupScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const caregiverId = route.params?.caregiverId;

  const [language, setLanguage] = useState('as');
  const [pseudonym, setPseudonym] = useState('AS-KAM-2025-0042');
  const [birthYear, setBirthYear] = useState('1956');
  const [gender, setGender] = useState('M');
  const [stage, setStage] = useState('MCI');

  const languages = [
    { code: 'as', name: 'অসমীয়া (Assamese)', region: 'Assam' },
    { code: 'mn', name: 'ꯃꯤꯇꯩꯂꯣꯟ (Manipuri)', region: 'Manipur' },
    { code: 'br', name: 'बर’ (Bodo)', region: 'Bodoland' },
    { code: 'hi', name: 'हिन्दी (Hindi)', region: 'Secondary' },
    { code: 'en', name: 'English', region: 'Medical' },
  ];

  const handleFinish = async () => {
    if (birthYear.length === 4) {
      try {
        const db = await getDatabase();
        const patientId = `pt-${Date.now()}`;
        await db.runAsync(
          `INSERT INTO patients (id, pseudonym_code, birth_year, gender, primary_language, clinical_stage, is_synced)
           VALUES (?, ?, ?, ?, ?, ?, 0);`,
          [patientId, pseudonym, parseInt(birthYear, 10), gender, language, stage]
        );
        navigation.replace('AccessibilityScreen', { patientId, isInitialOnboarding: true });
      } catch (err) {
        navigation.replace('AccessibilityScreen', { patientId: 'pt-local', isInitialOnboarding: true });
      }
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" backgroundColor="#121212" />
      <ScrollView contentContainerStyle={styles.scrollContent}>
        
        {/* Header */}
        <View style={styles.topRow}>
          <View style={styles.stepBadge}>
            <Text style={styles.stepText}>স্তৰ ২ / ২ (Step 2 of 2)</Text>
          </View>
          <View style={styles.speakerBtn}>
            <Text style={styles.speakerIcon}>🔊</Text>
          </View>
        </View>

        <View style={styles.titleWrapper}>
          <Text style={styles.title} accessibilityRole="header">
            ৰোগীৰ পৰিচয় আৰু ভাষা
          </Text>
          <Text style={styles.subtitle}>
            Set up the elderly patient profile and preferred mother tongue
          </Text>
        </View>

        {/* 1. Language */}
        <View style={styles.card}>
          <Text style={styles.heading}>১. মাতৃভাষা নিৰ্বাচন (Native Language)</Text>
          <Text style={styles.helper}>Games and speech recognition will use this dialect.</Text>
          
          {languages.map(l => (
            <TouchableOpacity
              key={l.code}
              style={[styles.langBtn, language === l.code && styles.langBtnSelected]}
              onPress={() => setLanguage(l.code)}
            >
              <Text style={[styles.langName, language === l.code && styles.langNameSelected]}>
                {l.name}
              </Text>
              <Text style={styles.langRegion}>{l.region}</Text>
            </TouchableOpacity>
          ))}
        </View>

        {/* 2. Pseudonym Code */}
        <View style={styles.card}>
          <Text style={styles.heading}>২. ৰোগীৰ গোপন সংকেত (Pseudonym)</Text>
          <Text style={styles.helper}>
            DPDA ২০২৩ সুৰক্ষা: নামৰ সলনি গোপন সংকেত ব্যৱহাৰ কৰা হয়।
          </Text>
          <TextInput
            style={styles.pseudonymText}
            value={pseudonym}
            editable={false}
          />
        </View>

        {/* 3. Birth Year & Gender */}
        <View style={styles.card}>
          <Text style={styles.heading}>৩. জন্মৰ বৰ্ষ আৰু লিংগ (Demographics)</Text>
          <View style={styles.demoRow}>
            <View style={styles.birthBox}>
              <Text style={styles.subLabel}>জন্ম বৰ্ষ (Year)</Text>
              <TextInput
                style={styles.birthInput}
                value={birthYear}
                onChangeText={setBirthYear}
                keyboardType="number-pad"
                maxLength={4}
              />
            </View>
            <View style={styles.genderBox}>
              <Text style={styles.subLabel}>লিংগ (Gender)</Text>
              <View style={styles.genderRow}>
                {['M', 'F', 'O'].map(g => (
                  <TouchableOpacity
                    key={g}
                    style={[styles.genderBtn, gender === g && styles.genderBtnSelected]}
                    onPress={() => setGender(g)}
                  >
                    <Text style={[styles.genderText, gender === g && styles.genderTextSelected]}>
                      {g === 'M' ? 'পুৰুষ' : g === 'F' ? 'মহিলা' : 'অন্য'}
                    </Text>
                  </TouchableOpacity>
                ))}
              </View>
            </View>
          </View>
        </View>

        {/* 4. Stage */}
        <View style={styles.card}>
          <Text style={styles.heading}>৪. জ্ঞানমূলক স্তৰ (Cognitive Stage)</Text>
          {[
            { key: 'MCI', title: 'মৃদু স্মৃতি বিভ্ৰাট (Mild Cognitive Impairment)' },
            { key: 'MILD_DEMENTIA', title: 'প্ৰাৰম্ভিক ডিমেনচিয়া (Early Dementia)' },
            { key: 'PRECLINICAL', title: 'সুস্থ / প্ৰতিৰোধমূলক (Healthy Wellness)' },
          ].map(s => (
            <TouchableOpacity
              key={s.key}
              style={[styles.stageBtn, stage === s.key && styles.stageBtnSelected]}
              onPress={() => setStage(s.key)}
            >
              <Text style={[styles.stageText, stage === s.key && styles.stageTextSelected]}>
                {s.title}
              </Text>
            </TouchableOpacity>
          ))}
        </View>

        {/* Finish CTA */}
        <View style={styles.actionWrapper}>
          <TouchableOpacity
            style={styles.finishBtn}
            accessibilityRole="button"
            accessibilityLabel="প্ৰফাইল সম্পূৰ্ণ কৰক"
            onPress={handleFinish}
            activeOpacity={0.8}
          >
            <Text style={styles.finishBtnText}>প্ৰফাইল সম্পূৰ্ণ কৰক (Complete Setup) ➔</Text>
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
  stepBadge: {
    backgroundColor: '#1E1E1E',
    borderWidth: 1,
    borderColor: '#FFD700',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 12,
  },
  stepText: {
    fontSize: 14,
    fontWeight: '800',
    color: '#FFD700',
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
    fontWeight: '700',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  helper: {
    fontSize: 13,
    color: '#BDBDBD',
    marginBottom: 10,
  },
  langBtn: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#262626',
    borderWidth: 1.5,
    borderColor: '#424242',
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 12,
    marginBottom: 8,
  },
  langBtnSelected: {
    borderColor: '#FFD700',
    backgroundColor: '#2A2600',
  },
  langName: {
    fontSize: 16,
    fontWeight: '700',
    color: '#E0E0E0',
  },
  langNameSelected: {
    color: '#FFD700',
  },
  langRegion: {
    fontSize: 12,
    color: '#9E9E9E',
  },
  pseudonymText: {
    backgroundColor: '#121212',
    borderWidth: 2,
    borderColor: '#00E676',
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 10,
    fontSize: 18,
    color: '#00E676',
    fontWeight: 'bold',
    letterSpacing: 1,
  },
  demoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  birthBox: {
    flex: 0.45,
  },
  genderBox: {
    flex: 0.52,
  },
  subLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#E0E0E0',
    marginBottom: 6,
  },
  birthInput: {
    backgroundColor: '#121212',
    borderWidth: 2,
    borderColor: '#616161',
    borderRadius: 10,
    paddingHorizontal: 10,
    paddingVertical: 10,
    fontSize: 18,
    color: '#FFFFFF',
    textAlign: 'center',
  },
  genderRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  genderBtn: {
    backgroundColor: '#262626',
    borderWidth: 1.5,
    borderColor: '#616161',
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 8,
    minWidth: 42,
    alignItems: 'center',
  },
  genderBtnSelected: {
    borderColor: '#FFD700',
    backgroundColor: '#2A2600',
  },
  genderText: {
    fontSize: 13,
    fontWeight: '700',
    color: '#BDBDBD',
  },
  genderTextSelected: {
    color: '#FFD700',
  },
  stageBtn: {
    backgroundColor: '#262626',
    borderWidth: 1.5,
    borderColor: '#424242',
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 12,
    marginBottom: 8,
  },
  stageBtnSelected: {
    borderColor: '#64B5F6',
    backgroundColor: '#0F263B',
  },
  stageText: {
    fontSize: 15,
    fontWeight: '600',
    color: '#E0E0E0',
  },
  stageTextSelected: {
    color: '#90CAF9',
    fontWeight: '700',
  },
  actionWrapper: {
    marginVertical: 16,
  },
  finishBtn: {
    backgroundColor: '#FFD700',
    borderRadius: 18,
    paddingVertical: 18,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 64,
  },
  finishBtnText: {
    fontSize: 18,
    fontWeight: '800',
    color: '#121212',
  },
});
