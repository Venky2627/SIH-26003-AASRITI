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
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';
import { getDatabase } from '@/database/connection';

export default function CaregiverProfileScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const { phoneNumber } = route.params || { phoneNumber: '9876543210' };

  const [fullName, setFullName] = useState('');
  const [relationship, setRelationship] = useState('CHILD');
  const [offlinePin, setOfflinePin] = useState('');
  const [showPin, setShowPin] = useState(false);

  const relationships = [
    { key: 'CHILD', label: 'সন্তান (Son/Daughter)', icon: '👨‍👧' },
    { key: 'SPOUSE', label: 'স্বামী / স্ত্ৰী (Spouse)', icon: '💍' },
    { key: 'SIBLING', label: 'ভাই / ভনী (Sibling)', icon: '🤝' },
    { key: 'ASHA_WORKER', label: 'আশা কৰ্মী (ASHA Worker)', icon: '🩺' },
    { key: 'OTHER', label: 'অন্য আত্মীয় (Other Relative)', icon: '🏡' },
  ];

  const handleSave = async () => {
    if (fullName.trim() && offlinePin.length === 6) {
      try {
        const db = await getDatabase();
        const caregiverId = `cg-${Date.now()}`;
        await db.runAsync(
          `INSERT INTO caregivers (id, patient_id, relationship, contact_hash, pin_hash, is_synced)
           VALUES (?, ?, ?, ?, ?, 0);`,
          [caregiverId, 'PENDING_LINK', relationship, `hash-${phoneNumber}`, `pin-${offlinePin}`]
        );
        navigation.navigate('PatientSetup', { caregiverId });
      } catch (err) {
        // Fallback navigate
        navigation.navigate('PatientSetup', { caregiverId: 'cg-offline' });
      }
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" backgroundColor="#121212" />
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.container}
      >
        <ScrollView contentContainerStyle={styles.scrollContent}>
          {/* Header */}
          <View style={styles.topRow}>
            <View style={styles.stepBadge}>
              <Text style={styles.stepText}>স্তৰ ১ / ২ (Step 1 of 2)</Text>
            </View>
            <View style={styles.speakerBtn}>
              <Text style={styles.speakerIcon}>🔊</Text>
            </View>
          </View>

          <View style={styles.titleWrapper}>
            <Text style={styles.title} accessibilityRole="header">
              যত্ন লওঁতাৰ বিৱৰণ
            </Text>
            <Text style={styles.subtitle}>
              Caregiver Profile: Tell us about yourself to protect your loved one
            </Text>
          </View>

          {/* Full Name */}
          <View style={styles.card}>
            <Text style={styles.label}>আপোনাৰ সম্পূৰ্ণ নাম (Your Full Name)</Text>
            <TextInput
              style={styles.textInput}
              value={fullName}
              onChangeText={setFullName}
              placeholder="উদাহৰণ: ৰাতুল বৰা"
              placeholderTextColor="#757575"
              accessibilityLabel="যত্ন লওঁতাৰ সম্পূৰ্ণ নাম"
            />
          </View>

          {/* Relationship */}
          <View style={styles.card}>
            <Text style={styles.label}>ৰোগীৰ সৈতে সম্পৰ্ক (Relationship)</Text>
            <Text style={styles.helper}>How are you related to the elder?</Text>

            {relationships.map(r => (
              <TouchableOpacity
                key={r.key}
                style={[
                  styles.relBtn,
                  relationship === r.key && styles.relBtnSelected
                ]}
                onPress={() => setRelationship(r.key)}
              >
                <Text style={styles.relIcon}>{r.icon}</Text>
                <Text style={[styles.relLabel, relationship === r.key && styles.relLabelSelected]}>
                  {r.label}
                </Text>
              </TouchableOpacity>
            ))}
          </View>

          {/* 6-Digit Master Offline PIN */}
          <View style={styles.card}>
            <Text style={styles.label}>৬-অংকৰ অফলাইন পিন (Offline PIN)</Text>
            <Text style={styles.helper}>
              Create a 6-digit PIN to access records when completely offline.
            </Text>

            <TextInput
              style={styles.pinInput}
              value={offlinePin}
              onChangeText={text => setOfflinePin(text.replace(/[^0-9]/g, ''))}
              keyboardType="number-pad"
              maxLength={6}
              secureTextEntry={!showPin}
              placeholder="••••••"
              placeholderTextColor="#757575"
            />

            <TouchableOpacity
              style={styles.showPinRow}
              onPress={() => setShowPin(!showPin)}
            >
              <Text style={styles.showPinText}>{showPin ? '☑ পিন লুকুৱাওক' : '☐ পিন দেখুৱাওক (Show PIN)'}</Text>
            </TouchableOpacity>
          </View>

          {/* Save Button */}
          <View style={styles.actionWrapper}>
            <TouchableOpacity
              style={[
                styles.saveBtn,
                (!fullName.trim() || offlinePin.length < 6) && styles.saveBtnDisabled
              ]}
              disabled={!fullName.trim() || offlinePin.length < 6}
              accessibilityRole="button"
              accessibilityLabel="সংৰক্ষণ আৰু আগবাঢ়ক"
              onPress={handleSave}
              activeOpacity={0.8}
            >
              <Text style={styles.saveBtnText}>সংৰক্ষণ আৰু আগবাঢ়ক (Save & Continue) ➔</Text>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#121212',
  },
  container: {
    flex: 1,
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
  label: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFFFFF',
    marginBottom: 6,
  },
  helper: {
    fontSize: 14,
    color: '#BDBDBD',
    marginBottom: 12,
    lineHeight: 18,
  },
  textInput: {
    backgroundColor: '#121212',
    borderWidth: 2,
    borderColor: '#616161',
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 18,
    color: '#FFFFFF',
    minHeight: 52,
  },
  relBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#262626',
    borderWidth: 1.5,
    borderColor: '#424242',
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 12,
    marginBottom: 8,
  },
  relBtnSelected: {
    borderColor: '#FFD700',
    backgroundColor: '#2A2600',
  },
  relIcon: {
    fontSize: 22,
    marginRight: 12,
  },
  relLabel: {
    fontSize: 15,
    fontWeight: '600',
    color: '#E0E0E0',
  },
  relLabelSelected: {
    color: '#FFD700',
    fontWeight: '800',
  },
  pinInput: {
    backgroundColor: '#121212',
    borderWidth: 2,
    borderColor: '#FFD700',
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 24,
    color: '#FFD700',
    textAlign: 'center',
    letterSpacing: 8,
    fontWeight: 'bold',
  },
  showPinRow: {
    marginTop: 10,
    paddingVertical: 4,
  },
  showPinText: {
    fontSize: 14,
    color: '#BDBDBD',
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
  saveBtnDisabled: {
    backgroundColor: '#424242',
  },
  saveBtnText: {
    fontSize: 18,
    fontWeight: '800',
    color: '#121212',
  },
});
