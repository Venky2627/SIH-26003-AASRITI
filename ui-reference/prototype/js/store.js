/**
 * AASRITI Reactive Mock Store
 * Persists application state in localStorage for the frontend prototype.
 */

const STORAGE_KEY = 'aasriti_mock_state_v1';

const initialStoreState = {
  role: 'patient', // 'patient' | 'caregiver' | 'doctor'
  theme: 'assam', // 'assam' | 'manipur' | 'meghalaya'
  language: 'en', // 'en' | 'as' | 'mni'
  accessibility: {
    textSize: 'xlarge', // 'large' | 'xlarge'
    voiceEnabled: true,
    highClarity: true
  },
  consent: {
    granted: true,
    proxyType: 'elder', // 'elder' | 'proxy'
    timestamp: new Date().toLocaleDateString()
  },
  patient: {
    id: 'PT-7821',
    name: 'Bhaben Barua',
    preferredName: 'Deuta',
    age: 74,
    condition: 'Mild Cognitive Impairment (Amnestic)',
    ashaWorker: 'Rina Gogoi (Sector 4)',
    doctor: 'Dr. Barua (Neurologist)',
    lastActiveGame: 'Family Trivia',
    streakDays: 4,
    reminders: [
      { id: 'rem-1', category: 'Hydration', label: 'Lukewarm Water', time: '11:00 AM', status: 'pending' },
      { id: 'rem-2', category: 'Daily Activity', label: 'Courtyard Walk', time: '04:00 PM', status: 'pending' },
      { id: 'rem-3', category: 'Medicine', label: 'Evening Blood Pressure Tablet', time: '08:00 PM', status: 'pending' },
      { id: 'rem-4', category: 'Medical Appointment', label: 'Dr. Barua Follow-up', time: 'Thursday 10:00 AM', status: 'scheduled' }
    ],
    games: {
      trivia: { played: true, score: 100, lastPlayed: 'Today' },
      voiceCue: { played: false, score: 90, lastPlayed: 'Yesterday' },
      sequencing: { played: false, score: 85, lastPlayed: '2 days ago' },
      categorisation: { played: false, score: 95, lastPlayed: '3 days ago' },
      villageMarket: { played: false, score: 100, lastPlayed: 'Yesterday' },
      pattern: { played: false, score: 90, lastPlayed: 'Yesterday' }
    },
    memories: [
      { id: 'mem-1', title: 'Bihu Festival 1984', tag: 'Family', date: 'Spring 1984', audio: true },
      { id: 'mem-2', title: 'Tezpur River Bank Tea Garden', tag: 'Places', date: 'Autumn 1992', audio: false }
    ],
    sosTriggered: false,
    lastSOSResolution: null
  },
  caregiver: {
    selectedPatientId: 'PT-7821',
    selectedPatientName: 'Bhaben Barua',
    quickLogs: [
      { id: 'log-1', type: 'Mood', label: 'Calm & Cheerful', notes: 'Enjoyed morning herbal tea on veranda', time: '09:30 AM' },
      { id: 'log-2', type: 'Medication', label: 'Morning Tablet Verified', notes: 'Taken with breakfast', time: '08:15 AM' }
    ],
    syncState: {
      synced: true,
      lastSyncTime: 'Just now',
      pendingCount: 0
    }
  },
  doctor: {
    selectedPatientId: 'PT-7821',
    doctorLinked: true,
    doctorAccessGranted: true,
    clinicalAssessment: {
      scale: 'ACE-III / MoCA',
      score: '78 / 100',
      attention: '15/18',
      memory: '19/26',
      fluency: '11/14',
      language: '23/26',
      visuospatial: '10/16',
      baselineComparison: 'Stable (+1 pt from 6-month baseline)'
    },
    carePlan: {
      status: 'Stable', // 'Stable' | 'Monitor' | 'Earlier Review'
      reviewPeriod: '4 Weeks',
      nextFollowUpDate: '2026-10-15',
      doctorNotes: 'Preserve bilingual cognitive cues and daily hydration routines. Routine review in 4 weeks.'
    }
  },
  screen26View: 'field-visit' // 'field-visit' | 'assigned-patient'
};

class AasritiStore {
  constructor() {
    this.state = this.load();
    this.listeners = [];
  }

  load() {
    try {
      const data = localStorage.getItem(STORAGE_KEY);
      if (data) {
        return Object.assign({}, initialStoreState, JSON.parse(data));
      }
    } catch (e) {
      console.warn('Failed to load store from localStorage', e);
    }
    return JSON.parse(JSON.stringify(initialStoreState));
  }

  save() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(this.state));
    } catch (e) {
      console.warn('Failed to save store to localStorage', e);
    }
    this.notify();
  }

  get() {
    return this.state;
  }

  set(updater) {
    if (typeof updater === 'function') {
      this.state = updater(this.state);
    } else {
      this.state = Object.assign({}, this.state, updater);
    }
    this.save();
  }

  reset() {
    this.state = JSON.parse(JSON.stringify(initialStoreState));
    this.save();
    return this.state;
  }

  subscribe(listener) {
    this.listeners.push(listener);
    return () => {
      this.listeners = this.listeners.filter(l => l !== listener);
    };
  }

  notify() {
    this.listeners.forEach(l => {
      try { l(this.state); } catch (e) { console.error(e); }
    });
  }

  // Domain Actions
  setRole(role) {
    this.set(s => ({ ...s, role }));
  }

  setTheme(theme) {
    this.set(s => ({ ...s, theme }));
  }

  setLanguage(language) {
    this.set(s => ({ ...s, language }));
  }

  setAccessibility(accessibility) {
    this.set(s => ({ ...s, accessibility: { ...s.accessibility, ...accessibility } }));
  }

  addQuickLog(type, label, notes) {
    const newLog = {
      id: 'log-' + Date.now(),
      type: type || 'Mood',
      label: label || 'Calm',
      notes: notes || '',
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };
    this.set(s => ({
      ...s,
      caregiver: {
        ...s.caregiver,
        quickLogs: [newLog, ...s.caregiver.quickLogs]
      }
    }));
    return newLog;
  }

  updateReminderStatus(id, status) {
    this.set(s => ({
      ...s,
      patient: {
        ...s.patient,
        reminders: s.patient.reminders.map(r => r.id === id ? { ...r, status } : r)
      }
    }));
  }

  addReminder(reminder) {
    const newRem = {
      id: 'rem-' + Date.now(),
      category: reminder.category || 'Medicine',
      label: reminder.label || 'New Reminder',
      time: reminder.time || '12:00 PM',
      status: 'pending'
    };
    this.set(s => ({
      ...s,
      patient: {
        ...s.patient,
        reminders: [...s.patient.reminders, newRem]
      }
    }));
    return newRem;
  }

  recordGameCompletion(gameKey, score) {
    this.set(s => ({
      ...s,
      patient: {
        ...s.patient,
        lastActiveGame: gameKey,
        games: {
          ...s.patient.games,
          [gameKey]: { played: true, score: score || 100, lastPlayed: 'Just now' }
        }
      }
    }));
  }

  triggerSOS() {
    this.set(s => ({
      ...s,
      patient: {
        ...s.patient,
        sosTriggered: true
      }
    }));
  }

  resolveSOS(resolutionNotes) {
    this.set(s => ({
      ...s,
      patient: {
        ...s.patient,
        sosTriggered: false,
        lastSOSResolution: {
          resolvedAt: new Date().toLocaleTimeString(),
          notes: resolutionNotes || 'Resolved with ASHA visit'
        }
      }
    }));
  }

  setScreen26View(view) {
    this.set(s => ({ ...s, screen26View: view }));
  }

  triggerSync(callback) {
    this.set(s => ({
      ...s,
      caregiver: {
        ...s.caregiver,
        syncState: { synced: false, lastSyncTime: 'Syncing...', pendingCount: 0 }
      }
    }));

    setTimeout(() => {
      this.set(s => ({
        ...s,
        caregiver: {
          ...s.caregiver,
          syncState: { synced: true, lastSyncTime: 'Just now', pendingCount: 0 }
        }
      }));
      if (callback) callback();
    }, 900);
  }

  updateDoctorCarePlan(plan) {
    this.set(s => ({
      ...s,
      doctor: {
        ...s.doctor,
        carePlan: { ...s.doctor.carePlan, ...plan }
      }
    }));
  }
}

// Attach globally
window.AasritiStore = new AasritiStore();
