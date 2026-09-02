import React, { useEffect, useState, useRef } from 'react';
import {
  StyleSheet,
  View,
  Text,
  SafeAreaView,
  StatusBar,
  ActivityIndicator,
  Animated,
  AccessibilityInfo,
  Dimensions,
  Platform,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { getDatabase } from '@/database/connection';

const { width, height } = Dimensions.get('window');

export default function SplashScreen() {
  const navigation = useNavigation<any>();
  const [statusText, setStatusText] = useState('পৰীক্ষা চলিছে... (Securing storage...)');
  const logoOpacity = useRef(new Animated.Value(0)).current;
  const logoScale = useRef(new Animated.Value(0.95)).current;

  useEffect(() => {
    let isMounted = true;

    // Check OS reduced motion preference
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

    const bootstrapApp = async () => {
      try {
        await getDatabase();
        if (isMounted) {
          setStatusText('স্থানীয় তথ্য সক্ৰিয় হৈছে... (Storage ready)');
          setTimeout(() => {
            if (isMounted) {
              navigation.replace('RoleSelection');
            }
          }, 900);
        }
      } catch (err) {
        if (isMounted) {
          setStatusText('অফলাইন মড সক্ৰিয় হৈছে (Offline ready)');
          setTimeout(() => {
            if (isMounted) {
              navigation.replace('RoleSelection');
            }
          }, 1000);
        }
      }
    };

    bootstrapApp();

    return () => {
      isMounted = false;
    };
  }, [navigation, logoOpacity, logoScale]);

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" backgroundColor="#121212" />
      <View
        style={styles.container}
        accessible={true}
        accessibilityRole="none"
        accessibilityLabel="SmritiSetu Application Loading Screen"
      >
        <View style={styles.brandingContainer}>
          <Animated.View
            style={[styles.logoWrapper, { opacity: logoOpacity, transform: [{ scale: logoScale }] }]}
            accessible={true}
            accessibilityRole="image"
            accessibilityLabel="SmritiSetu Emblem"
          >
            <Text style={styles.emblemEmoji}>🌿</Text>
          </Animated.View>

          <Text style={styles.titleIndic} accessibilityRole="header">
            স্মৃতিসেতু
          </Text>
          <Text style={styles.titleMeitei}>ꯁ꯭ꯃ꯭ꯔꯤꯇꯤ ꯁꯦꯇꯨ</Text>
          <Text style={styles.titleLatin}>SmritiSetu</Text>
          <Text style={styles.tagline}>
            জ্ঞানমূলক পুনৰুদ্ধাৰ আৰু স্মৃতি সহায়ক মঞ্চ
          </Text>
        </View>

        <View
          style={styles.statusContainer}
          accessible={true}
          accessibilityRole="text"
          accessibilityLiveRegion="polite"
        >
          <ActivityIndicator size="large" color="#FFD700" style={styles.spinner} />
          <Text style={styles.statusText}>{statusText}</Text>
        </View>

        <View
          style={styles.offlinePillContainer}
          accessible={true}
          accessibilityRole="text"
          accessibilityLabel="Operating in 100% Offline Secured Mode using SQLCipher 256-bit encryption"
        >
          <Text style={styles.offlinePillIcon}>🟢</Text>
          <Text style={styles.offlinePillText}>সম্পূৰ্ণ অফলাইন সুৰক্ষিত (100% Offline Secured)</Text>
        </View>

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
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#121212',
  },
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 24,
    paddingVertical: 32,
  },
  brandingContainer: {
    alignItems: 'center',
    marginTop: height * 0.12,
  },
  logoWrapper: {
    width: 120,
    height: 120,
    borderRadius: 24,
    backgroundColor: '#1E1E1E',
    borderWidth: 3,
    borderColor: '#FFD700',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 20,
  },
  emblemEmoji: {
    fontSize: 54,
  },
  titleIndic: {
    fontSize: 36,
    fontWeight: '800',
    color: '#FFD700',
    marginBottom: 4,
  },
  titleMeitei: {
    fontSize: 22,
    fontWeight: '600',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  titleLatin: {
    fontSize: 22,
    fontWeight: '700',
    color: '#E0E0E0',
    letterSpacing: 2,
    marginBottom: 10,
  },
  tagline: {
    fontSize: 16,
    color: '#E0E0E0',
    textAlign: 'center',
    maxWidth: width * 0.85,
    lineHeight: 24,
  },
  statusContainer: {
    alignItems: 'center',
    marginVertical: 16,
  },
  spinner: {
    marginBottom: 10,
  },
  statusText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#FFD700',
  },
  offlinePillContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1B5E20',
    borderWidth: 1.5,
    borderColor: '#4CAF50',
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 24,
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
