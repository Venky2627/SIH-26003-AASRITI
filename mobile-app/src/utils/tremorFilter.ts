/**
 * SmritiSetu Tremor & Dwell Time Filter
 * Protects elderly patients with essential tremors and Parkinsonism from accidental double taps.
 */

export interface TremorFilterConfig {
  debounceMs?: number;
  minDwellMs?: number;
}

export function createTremorResistantPressHandler(
  onValidPress: () => void,
  config: TremorFilterConfig = {}
) {
  const debounceMs = config.debounceMs ?? 600;
  const minDwellMs = config.minDwellMs ?? 80;

  let lastPressTime = 0;
  let touchStartTime = 0;

  return {
    onTouchStart: () => {
      touchStartTime = Date.now();
    },
    onTouchEnd: () => {
      const touchDuration = Date.now() - touchStartTime;
      const now = Date.now();

      // Rule 1: Reject accidental brush touches
      if (touchDuration < minDwellMs) {
        return;
      }

      // Rule 2: Reject rapid duplicate taps
      if (now - lastPressTime > debounceMs) {
        lastPressTime = now;
        onValidPress();
      }
    },
  };
}
