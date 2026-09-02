import React, { useState } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, SafeAreaView, StatusBar } from 'react-native';

export default function App() {
  const [offlineStatus, setOfflineStatus] = useState<string>('অফলাইন সুৰক্ষিত (Offline Secured)');
  const [selectedLanguage, setSelectedLanguage] = useState<string>('as');

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="#121212" />
      
      {/* Offline Status Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>স্মৃতিসেতু (SmritiSetu)</Text>
        <View style={styles.badge}>
          <Text style={styles.badgeText}>🟢 {offlineStatus}</Text>
        </View>
      </View>

      {/* Main Content Area */}
      <View style={styles.content}>
        <Text style={styles.welcomeText}>
          জ্ঞানমূলক খেল আৰু স্মৃতি পুনৰুদ্ধাৰ
        </Text>
        <Text style={styles.subText}>
          Cognitive Gaming & Memory Rehabilitation Platform (SIH26003)
        </Text>

        {/* Game Selection Cards */}
        <TouchableOpacity
          style={styles.card}
          activeOpacity={0.8}
          accessibilityLabel="Speed Match Game"
          accessibilityRole="button"
        >
          <Text style={styles.cardTitle}>⚡ ১. প্ৰক্ৰিয়া বেগ (Speed Match)</Text>
          <Text style={styles.cardSubtitle}>Visual Processing & Selective Attention</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.card}
          activeOpacity={0.8}
          accessibilityLabel="Story Weaver Game"
          accessibilityRole="button"
        >
          <Text style={styles.cardTitle}>📖 ২. সাধুকথা স্মৃতি (Story Weaver)</Text>
          <Text style={styles.cardSubtitle}>Narrative Recall via On-Device ASR (IndicConformer)</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.card}
          activeOpacity={0.8}
          accessibilityLabel="Picture Naming Game"
          accessibilityRole="button"
        >
          <Text style={styles.cardTitle}>🖼️ ৩. ছবি চিনাক্তকৰণ (Picture Naming)</Text>
          <Text style={styles.cardSubtitle}>Semantic Confrontation with NER Cultural Icons</Text>
        </TouchableOpacity>
      </View>

      {/* Footer Info */}
      <View style={styles.footer}>
        <Text style={styles.footerText}>
          Ministry of Development of North Eastern Region (MDoNER) • DPDA 2023 Verified
        </Text>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#121212',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#262626',
  },
  headerTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#FFD700', // High-contrast gold
  },
  badge: {
    backgroundColor: '#1B5E20',
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 12,
  },
  badgeText: {
    color: '#FFFFFF',
    fontSize: 12,
    fontWeight: '600',
  },
  content: {
    flex: 1,
    padding: 20,
    justifyContent: 'center',
  },
  welcomeText: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 6,
    textAlign: 'center',
  },
  subText: {
    fontSize: 14,
    color: '#E0E0E0',
    marginBottom: 30,
    textAlign: 'center',
  },
  card: {
    backgroundColor: '#1E1E1E',
    borderWidth: 2,
    borderColor: '#FFD700',
    borderRadius: 16,
    padding: 20,
    marginBottom: 16,
    minHeight: 80,
    justifyContent: 'center',
  },
  cardTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFD700',
    marginBottom: 4,
  },
  cardSubtitle: {
    fontSize: 14,
    color: '#E0E0E0',
  },
  footer: {
    padding: 16,
    alignItems: 'center',
    borderTopWidth: 1,
    borderTopColor: '#262626',
  },
  footerText: {
    fontSize: 12,
    color: '#888888',
  },
});
