import { Platform } from 'react-native';

export const theme = {
  colors: {
    primary: '#2E7D32',          // Forest Green
    primaryDark: '#1B5E20',      // Deep Foliage Green
    primaryLight: '#4CAF50',     // Vibrant Leaf Green
    primaryHighlight: '#FFD700', // Pure Gold (13.8:1 contrast on #121212)

    secondary: '#1565C0',        // Medical Royal Blue
    secondaryDark: '#0D47A1',    // Deep Navy
    secondaryLight: '#64B5F6',   // Calming Sky Blue

    background: '#121212',       // High-Contrast Dark Surface (Eliminates Glare)
    surface: '#1E1E1E',          // Elevated Card Surface
    surfaceElevated: '#262626',  // Secondary Card / Selected State
    surfaceBorder: '#424242',    // Tactile Outline

    error: '#FFA000',            // Soothing Warm Amber
    errorBackground: '#332400',  // Dark Amber Container
    errorText: '#FFE082',        // High Contrast Pale Amber
    success: '#00E676',          // Reassuring Emerald Light
    successBackground: '#102A16',// Dark Emerald Container

    text: {
      primary: '#FFFFFF',        // 18.5:1 contrast against #121212
      secondary: '#E0E0E0',      // 14.1:1 contrast
      muted: '#BDBDBD',          // 8.9:1 contrast
      accent: '#FFD700',         // 13.8:1 contrast
      inverse: '#121212',        // Text on Gold buttons
    },

    regional: {
      assamMuga: '#E5A93C',
      manipurSangai: '#A0522D',
      nagalandBead: '#D32F2F',
      bodoHandloom: '#2E7D32',
    },
  },

  typography: {
    fontFamily: {
      regular: 'NotoSansBengali-Regular',
      medium: 'NotoSansBengali-Medium',
      bold: 'NotoSansBengali-Bold',
      system: Platform.select({ android: 'Roboto', ios: 'System' }),
    },
    fontSize: {
      xs: 16,
      sm: 18,
      md: 20,
      lg: 24,
      xl: 28,
      xxl: 36,
    },
    lineHeight: {
      tight: 1.3,
      normal: 1.6,
      relaxed: 1.8,
    },
  },

  spacing: {
    xs: 4,
    sm: 8,
    md: 16,
    lg: 20,
    xl: 24,
    xxl: 32,
    huge: 48,
  },

  borderRadius: {
    sm: 10,
    md: 14,
    lg: 20,
    pill: 9999,
  },

  touchTarget: {
    minSize: 48,
    standardButton: 56,
    primaryAction: 64,
  },
};
