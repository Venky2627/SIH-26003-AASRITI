import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { NavigationContainer } from '@react-navigation/native';

import SplashScreen from '@/screens/auth/SplashScreen';
import RoleSelectionScreen from '@/screens/auth/RoleSelectionScreen';
import MobileEntryScreen from '@/screens/auth/MobileEntryScreen';
import OTPVerificationScreen from '@/screens/auth/OTPVerificationScreen';
import CaregiverProfileScreen from '@/screens/auth/CaregiverProfileScreen';
import PatientSetupScreen from '@/screens/auth/PatientSetupScreen';
import AccessibilityScreen from '@/screens/settings/AccessibilityScreen';

export type RootStackParamList = {
  Splash: undefined;
  RoleSelection: undefined;
  MobileEntry: { role: 'CAREGIVER' | 'DOCTOR' };
  OTPVerification: { role: 'CAREGIVER' | 'DOCTOR'; phoneNumber: string };
  CaregiverProfile: { phoneNumber: string; role: 'CAREGIVER' | 'DOCTOR' };
  PatientSetup: { caregiverId?: string; isNew?: boolean };
  AccessibilityScreen: { patientId?: string; isInitialOnboarding?: boolean };
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function RootNavigator() {
  return (
    <NavigationContainer>
      <Stack.Navigator
        initialRouteName="Splash"
        screenOptions={{
          headerShown: false,
          animation: 'fade',
          contentStyle: { backgroundColor: '#121212' },
        }}
      >
        <Stack.Screen name="Splash" component={SplashScreen} />
        <Stack.Screen name="RoleSelection" component={RoleSelectionScreen} />
        <Stack.Screen name="MobileEntry" component={MobileEntryScreen} />
        <Stack.Screen name="OTPVerification" component={OTPVerificationScreen} />
        <Stack.Screen name="CaregiverProfile" component={CaregiverProfileScreen} />
        <Stack.Screen name="PatientSetup" component={PatientSetupScreen} />
        <Stack.Screen name="AccessibilityScreen" component={AccessibilityScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
