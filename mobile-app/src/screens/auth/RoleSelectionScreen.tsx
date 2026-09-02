import React, { useState } from 'react';
import {
  StyleSheet,
  View,
  Text,
  SafeAreaView,
  StatusBar,
  TouchableOpacity,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';

export default function RoleSelectionScreen() {
  const navigation = useNavigation<any>();
  const [selectedRole, setSelectedRole] = useState<'PATIENT' | 'CAREGIVER' | 'DOCTOR' | null>(null);

  const handleSelectRole = (role: 'PATIENT' | 'CAREGIVER' | 'DOCTOR') => {
    setSelectedRole(role);
    if (role === 'PATIENT') {
      navigation.navigate('PatientSetup', { isNew: true });
    } else {
      navigation.navigate('MobileEntry', { role });
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" backgroundColor="#121212" />
      <View style={styles.container}>
        
        {/* Top Header */}
        <View style={styles.topBar}>
          <TouchableOpacity
            style={styles.spokenAudioBtn}
            accessibilityRole="button"
            accessibilityLabel="শুনক (Listen to instructions in Assamese)"
          >
            <Text style={styles.spokenAudioIcon}>🔊</Text>
            <Text style={styles.spokenAudioText}>শুনক (Listen)</Text>
          </TouchableOpacity>

          <View style={styles.langBadge}>
            <Text style={styles.langText}>অসমীয়া ▾</Text>
          </View>
        </View>

        {/* Title */}
        <View style={styles.headerContainer}>
          <Text style={styles.title} accessibilityRole="header">
            আপুনি কোন হয় বাছক
          </Text>
          <Text style={styles.subtitle}>
            Choose your role to personalize the experience
          </Text>
        </View>

        {/* Cards */}
        <View style={styles.cardListContainer}>
          {/* Patient Card */}
          <TouchableOpacity
            style={[styles.roleCard, styles.patientCard]}
            accessibilityRole="button"
            accessibilityLabel="মই নিজে খেলিম, বয়োজ্যেষ্ঠ খেলুৱৈ। I am the Patient."
            onPress={() => handleSelectRole('PATIENT')}
            activeOpacity={0.8}
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
            <Text style={styles.cardArrow}>➔</Text>
          </TouchableOpacity>

          {/* Caregiver Card */}
          <TouchableOpacity
            style={[styles.roleCard, styles.caregiverCard]}
            accessibilityRole="button"
            accessibilityLabel="মই যত্ন লওঁতা, পৰিয়ালৰ সদস্য। I am a Caregiver."
            onPress={() => handleSelectRole('CAREGIVER')}
            activeOpacity={0.8}
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
            <Text style={styles.cardArrow}>➔</Text>
          </TouchableOpacity>

          {/* Doctor Card */}
          <TouchableOpacity
            style={[styles.roleCard, styles.doctorCard]}
            accessibilityRole="button"
            accessibilityLabel="চিকিৎসক বা আশা কৰ্মী। Doctor or Health Worker."
            onPress={() => handleSelectRole('DOCTOR')}
            activeOpacity={0.8}
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
                Clinical summaries & cognitive trends
              </Text>
            </View>
            <Text style={styles.cardArrow}>➔</Text>
          </TouchableOpacity>
        </View>

        {/* Footer */}
        <View style={styles.footerContainer}>
          <Text style={styles.offlineNoticeText}>
            🔒 কোনো ইন্টাৰনেটৰ প্ৰয়োজন নাই (No Internet Required)
          </Text>
        </View>

      </View>
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
    backgroundColor: '#1B5E20',
    borderWidth: 2,
    borderColor: '#4CAF50',
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 24,
    minHeight: 48,
  },
  spokenAudioIcon: {
    fontSize: 18,
    marginRight: 6,
  },
  spokenAudioText: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFFFFF',
  },
  langBadge: {
    backgroundColor: '#262626',
    borderWidth: 1.5,
    borderColor: '#FFD700',
    paddingHorizontal: 14,
    paddingVertical: 10,
    borderRadius: 20,
  },
  langText: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFD700',
  },
  headerContainer: {
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
  cardListContainer: {
    flex: 1,
    justifyContent: 'center',
  },
  roleCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1E1E1E',
    borderWidth: 2,
    borderRadius: 18,
    padding: 16,
    marginBottom: 14,
    minHeight: 110,
  },
  patientCard: {
    borderColor: '#00E676',
    backgroundColor: '#152618',
  },
  caregiverCard: {
    borderColor: '#64B5F6',
    backgroundColor: '#122130',
  },
  doctorCard: {
    borderColor: '#FFB74D',
    backgroundColor: '#261E14',
  },
  cardIconWrapper: {
    width: 56,
    height: 56,
    borderRadius: 28,
    backgroundColor: '#1B5E20',
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 14,
  },
  caregiverIconBg: {
    backgroundColor: '#0D47A1',
  },
  doctorIconBg: {
    backgroundColor: '#E65100',
  },
  cardEmoji: {
    fontSize: 28,
  },
  cardContent: {
    flex: 1,
  },
  cardTitle: {
    fontSize: 20,
    fontWeight: '800',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  cardDescription: {
    fontSize: 14,
    color: '#E0E0E0',
    lineHeight: 18,
  },
  cardEnglishSub: {
    fontSize: 12,
    color: '#90A4AE',
    marginTop: 2,
  },
  cardArrow: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#FFD700',
    marginLeft: 8,
  },
  footerContainer: {
    alignItems: 'center',
    paddingVertical: 8,
  },
  offlineNoticeText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#81C784',
  },
});
