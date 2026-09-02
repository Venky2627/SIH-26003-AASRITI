import React, { useState, useEffect } from 'react';
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

export default function OTPVerificationScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const { role, phoneNumber } = route.params || { role: 'CAREGIVER', phoneNumber: '9876543210' };

  const [otpCode, setOtpCode] = useState('');
  const [timerSeconds, setTimerSeconds] = useState(600); // 10 minutes for elderly comfort
  const [hasError, setHasError] = useState(false);

  useEffect(() => {
    const timer = setInterval(() => {
      setTimerSeconds(prev => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const handleOtpChange = (text: string) => {
    const numeric = text.replace(/[^0-9]/g, '');
    setOtpCode(numeric);
    setHasError(false);

    if (numeric.length === 6) {
      // Direct verify
      handleVerify(numeric);
    }
  };

  const handleVerify = (code: string) => {
    if (code.length === 6) {
      navigation.replace('CaregiverProfile', { phoneNumber, role });
    } else {
      setHasError(true);
    }
  };

  const formatTimer = (sec: number) => {
    const m = Math.floor(sec / 60);
    const s = sec % 60;
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" backgroundColor="#121212" />
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.container}
      >
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

        {/* Header */}
        <View style={styles.headerBox}>
          <Text style={styles.title} accessibilityRole="header">
            গোপন ক'ড (OTP) লিখক
          </Text>
          <Text style={styles.subtitle}>
            Enter the 6-digit code sent to your phone
          </Text>

          <View style={styles.phoneBadge}>
            <Text style={styles.phoneText}>+91 {phoneNumber}</Text>
            <TouchableOpacity onPress={() => navigation.goBack()}>
              <Text style={styles.editText}>সলনি (Edit)</Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* OTP Input Card */}
        <View style={styles.otpCard}>
          <View style={styles.boxesRow}>
            {[0, 1, 2, 3, 4, 5].map(i => {
              const d = otpCode[i] || '';
              const isFocused = otpCode.length === i;
              return (
                <View
                  key={i}
                  style={[
                    styles.digitBox,
                    isFocused && styles.digitBoxFocused,
                    d ? styles.digitBoxFilled : null,
                  ]}
                >
                  <Text style={styles.digitText}>{d}</Text>
                </View>
              );
            })}
          </View>

          <TextInput
            value={otpCode}
            onChangeText={handleOtpChange}
            keyboardType="number-pad"
            maxLength={6}
            style={styles.hiddenInput}
            autoFocus={true}
          />

          {hasError && (
            <View style={styles.errorBox}>
              <Text style={styles.errorText}>
                💡 কোডটো নিমিলিল। পুনৰ পৰীক্ষা কৰক।
              </Text>
            </View>
          )}

          <Text style={styles.timerText}>
            ⏳ কোডৰ ম্যাদ আছে: <Text style={styles.timerHighlight}>{formatTimer(timerSeconds)}</Text>
          </Text>
        </View>

        {/* Resend & Voice Help */}
        <View style={styles.resendWrapper}>
          <TouchableOpacity style={styles.resendBtn}>
            <Text style={styles.resendText}>🔄 কোড পুনৰ প্ৰেৰণ কৰক (Resend Code)</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.voiceCallBtn}>
            <Text style={styles.voiceCallText}>📞 ফোন কলযোগে কোড শুনক (Voice Call OTP)</Text>
          </TouchableOpacity>
        </View>

        {/* Verify CTA */}
        <View style={styles.bottomWrapper}>
          <TouchableOpacity
            style={[
              styles.verifyBtn,
              otpCode.length < 6 && styles.verifyBtnDisabled
            ]}
            disabled={otpCode.length < 6}
            accessibilityRole="button"
            accessibilityLabel="সত্যাপন কৰক"
            onPress={() => handleVerify(otpCode)}
            activeOpacity={0.8}
          >
            <Text style={styles.verifyBtnText}>সত্যাপন কৰক (Verify & Continue) ➔</Text>
          </TouchableOpacity>
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
  headerBox: {
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
  phoneBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1E1E1E',
    borderWidth: 1.5,
    borderColor: '#64B5F6',
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 12,
    alignSelf: 'flex-start',
  },
  phoneText: {
    fontSize: 16,
    fontWeight: '800',
    color: '#FFFFFF',
    marginRight: 10,
  },
  editText: {
    fontSize: 13,
    fontWeight: '700',
    color: '#90CAF9',
    textDecorationLine: 'underline',
  },
  otpCard: {
    backgroundColor: '#1E1E1E',
    borderWidth: 2,
    borderColor: '#424242',
    borderRadius: 18,
    padding: 20,
    alignItems: 'center',
  },
  boxesRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '100%',
    marginBottom: 16,
  },
  digitBox: {
    width: 44,
    height: 60,
    borderRadius: 10,
    borderWidth: 2,
    borderColor: '#616161',
    backgroundColor: '#121212',
    alignItems: 'center',
    justifyContent: 'center',
  },
  digitBoxFocused: {
    borderColor: '#FFD700',
    backgroundColor: '#1A1800',
  },
  digitBoxFilled: {
    borderColor: '#00E676',
  },
  digitText: {
    fontSize: 26,
    fontWeight: '800',
    color: '#FFD700',
  },
  hiddenInput: {
    position: 'absolute',
    opacity: 0,
    width: 1,
    height: 1,
  },
  errorBox: {
    backgroundColor: '#332400',
    borderWidth: 1,
    borderColor: '#FFB300',
    borderRadius: 10,
    padding: 10,
    marginBottom: 10,
    width: '100%',
  },
  errorText: {
    fontSize: 14,
    color: '#FFE082',
    fontWeight: '600',
  },
  timerText: {
    fontSize: 15,
    color: '#BDBDBD',
  },
  timerHighlight: {
    fontWeight: '800',
    color: '#FFD700',
  },
  resendWrapper: {
    alignItems: 'center',
    marginVertical: 10,
  },
  resendBtn: {
    backgroundColor: '#262626',
    borderWidth: 1,
    borderColor: '#FFD700',
    paddingHorizontal: 18,
    paddingVertical: 10,
    borderRadius: 12,
    marginBottom: 8,
  },
  resendText: {
    fontSize: 15,
    fontWeight: '700',
    color: '#FFD700',
  },
  voiceCallBtn: {
    paddingVertical: 4,
  },
  voiceCallText: {
    fontSize: 14,
    color: '#90CAF9',
    textDecorationLine: 'underline',
  },
  bottomWrapper: {
    paddingTop: 8,
  },
  verifyBtn: {
    backgroundColor: '#FFD700',
    borderRadius: 18,
    paddingVertical: 18,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 64,
  },
  verifyBtnDisabled: {
    backgroundColor: '#424242',
  },
  verifyBtnText: {
    fontSize: 18,
    fontWeight: '800',
    color: '#121212',
  },
});
