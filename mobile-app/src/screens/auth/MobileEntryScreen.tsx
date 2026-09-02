import React, { useState } from 'react';
import {
  StyleSheet,
  View,
  Text,
  SafeAreaView,
  StatusBar,
  TextInput,
  TouchableOpacity,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';

export default function MobileEntryScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const role = route.params?.role || 'CAREGIVER';

  const [phoneNumber, setPhoneNumber] = useState('');
  const [formattedPhone, setFormattedPhone] = useState('');
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const handlePhoneChange = (text: string) => {
    const cleaned = text.replace(/[^0-9]/g, '');
    setPhoneNumber(cleaned);

    if (cleaned.length > 5) {
      setFormattedPhone(`${cleaned.slice(0, 5)} ${cleaned.slice(5, 10)}`);
    } else {
      setFormattedPhone(cleaned);
    }

    if (cleaned.length > 0 && !['6', '7', '8', '9'].includes(cleaned[0])) {
      setErrorMsg('ভাৰতীয় নম্বৰ ৬, ৭, ৮ বা ৯ ৰে আৰম্ভ হয়। (Indian numbers start with 6-9)');
    } else {
      setErrorMsg(null);
    }
  };

  const handleProceed = () => {
    if (phoneNumber.length === 10) {
      navigation.navigate('OTPVerification', { role, phoneNumber });
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" backgroundColor="#121212" />
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.container}
      >
        {/* Header Row */}
        <View style={styles.topRow}>
          <TouchableOpacity
            style={styles.backBtn}
            accessibilityRole="button"
            accessibilityLabel="উভতি যাওক (Go Back)"
            onPress={() => navigation.goBack()}
          >
            <Text style={styles.backArrow}>←</Text>
            <Text style={styles.backLabel}>উভতি যাওক</Text>
          </TouchableOpacity>

          <View style={styles.speakerBtn}>
            <Text style={styles.speakerIcon}>🔊</Text>
          </View>
        </View>

        {/* Title */}
        <View style={styles.titleWrapper}>
          <Text style={styles.title} accessibilityRole="header">
            আপোনাৰ ম'বাইল নম্বৰ দিয়ক
          </Text>
          <Text style={styles.subtitle}>
            Enter your 10-digit phone number for secure login
          </Text>
          <View style={styles.roleBadge}>
            <Text style={styles.roleBadgeText}>
              {role === 'CAREGIVER' ? '🤝 যত্ন লওঁতা একাউণ্ট' : '🩺 চিকিৎসক একাউণ্ট'}
            </Text>
          </View>
        </View>

        {/* Phone Input Box */}
        <View style={styles.inputCard}>
          <Text style={styles.fieldLabel}>ম'বাইল নম্বৰ (Mobile Number)</Text>
          
          <View style={[styles.inputRow, errorMsg ? styles.inputRowError : null]}>
            <View style={styles.countryCodeBox}>
              <Text style={styles.flagEmoji}>🇮🇳</Text>
              <Text style={styles.countryCodeText}>+91</Text>
            </View>

            <TextInput
              style={styles.textInput}
              value={formattedPhone}
              onChangeText={handlePhoneChange}
              keyboardType="number-pad"
              maxLength={11}
              placeholder="98765 43210"
              placeholderTextColor="#757575"
              accessibilityLabel="দহটা সংখ্যাৰ ফোন নম্বৰ লিখক"
              autoFocus={true}
            />
          </View>

          {errorMsg ? (
            <View style={styles.errorBox}>
              <Text style={styles.errorText}>💡 {errorMsg}</Text>
            </View>
          ) : (
            <Text style={styles.helperText}>
              আপোনাৰ নম্বৰলৈ এটা ৬-অংকৰ গোপন কোড (OTP) প্ৰেৰণ কৰা হ'ব।
            </Text>
          )}
        </View>

        {/* Offline Callout */}
        <View style={styles.offlineBox}>
          <Text style={styles.offlineText}>
            📴 অফলাইন ম'ড: পূৰ্বৰ পিন (PIN) ব্যৱহাৰ কৰি ইন্টাৰনেট নোহোৱাকৈ প্ৰৱেশ কৰিব পাৰিব।
          </Text>
        </View>

        {/* Submit Button */}
        <View style={styles.bottomWrapper}>
          <TouchableOpacity
            style={[
              styles.submitBtn,
              phoneNumber.length < 10 && styles.submitBtnDisabled
            ]}
            disabled={phoneNumber.length < 10}
            accessibilityRole="button"
            accessibilityLabel="অ'টিপি ক'ড প্ৰেৰণ কৰক"
            onPress={handleProceed}
            activeOpacity={0.8}
          >
            <Text style={styles.submitBtnText}>অ'টিপি ক'ড প্ৰেৰণ কৰক (Send OTP) ➔</Text>
          </TouchableOpacity>
          <Text style={styles.dpdaText}>
            🔒 DPDA ২০২৩ অনুসৰি আপোনাৰ তথ্য গোপনে সুৰক্ষিত থাকে।
          </Text>
        </View>
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
    paddingHorizontal: 20,
    paddingVertical: 16,
    justifyContent: 'space-between',
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
  backLabel: {
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
    marginBottom: 10,
  },
  roleBadge: {
    backgroundColor: '#0D273D',
    borderWidth: 1,
    borderColor: '#1E88E5',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 12,
    alignSelf: 'flex-start',
  },
  roleBadgeText: {
    fontSize: 14,
    fontWeight: '700',
    color: '#64B5F6',
  },
  inputCard: {
    backgroundColor: '#1E1E1E',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 18,
    padding: 18,
    marginBottom: 16,
  },
  fieldLabel: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFFFFF',
    marginBottom: 10,
  },
  inputRow: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#121212',
    borderWidth: 2,
    borderColor: '#FFD700',
    borderRadius: 14,
    paddingHorizontal: 14,
    minHeight: 68,
  },
  inputRowError: {
    borderColor: '#FFA000',
  },
  countryCodeBox: {
    flexDirection: 'row',
    alignItems: 'center',
    borderRightWidth: 1.5,
    borderRightColor: '#424242',
    paddingRight: 10,
    marginRight: 10,
  },
  flagEmoji: {
    fontSize: 20,
    marginRight: 4,
  },
  countryCodeText: {
    fontSize: 20,
    fontWeight: '800',
    color: '#FFFFFF',
  },
  textInput: {
    flex: 1,
    fontSize: 22,
    fontWeight: '800',
    color: '#FFD700',
    letterSpacing: 2,
  },
  helperText: {
    fontSize: 14,
    color: '#BDBDBD',
    marginTop: 10,
  },
  errorBox: {
    backgroundColor: '#332400',
    borderWidth: 1,
    borderColor: '#FFB300',
    borderRadius: 10,
    padding: 10,
    marginTop: 10,
  },
  errorText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#FFE082',
  },
  offlineBox: {
    backgroundColor: '#1C2E20',
    borderWidth: 1,
    borderColor: '#81C784',
    borderRadius: 14,
    padding: 12,
    marginBottom: 16,
  },
  offlineText: {
    fontSize: 14,
    color: '#E8F5E9',
    lineHeight: 18,
  },
  bottomWrapper: {
    paddingTop: 8,
  },
  submitBtn: {
    backgroundColor: '#FFD700',
    borderRadius: 18,
    paddingVertical: 18,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 64,
  },
  submitBtnDisabled: {
    backgroundColor: '#424242',
  },
  submitBtnText: {
    fontSize: 18,
    fontWeight: '800',
    color: '#121212',
  },
  dpdaText: {
    fontSize: 12,
    color: '#757575',
    textAlign: 'center',
    marginTop: 10,
  },
});
