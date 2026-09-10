/**
 * AASRITI Universal Navigation & Router Engine
 * Authoritative screen navigation based on docs/screen-flow.md
 */

const AASRITI_SCREENS = {
  1: { id: 1, file: 'screen1.html', title: 'Open / Welcome', category: 'Onboarding' },
  2: { id: 2, file: 'screen2.html', title: 'Cultural Theme', category: 'Onboarding' },
  3: { id: 3, file: 'screen3.html', title: 'PIN Authentication', category: 'Auth' },
  4: { id: 4, file: 'screen4.html', title: 'Language Selection', category: 'Onboarding' },
  5: { id: 5, file: 'screen5.html', title: 'Accessibility / Personalisation', category: 'Onboarding' },
  6: { id: 6, file: 'screen6.html', title: 'Consent & Safety Assent', category: 'Onboarding' },
  7: { id: 7, file: 'screen7.html', title: 'Patient Home', category: 'Patient' },
  8: { id: 8, file: 'screen8.html', title: 'Family Trivia', category: 'Patient' },
  9: { id: 9, file: 'screen9.html', title: 'Voice Cue Card', category: 'Patient' },
  10: { id: 10, file: 'screen10.html', title: 'Sequencing', category: 'Patient' },
  11: { id: 11, file: 'screen11.html', title: 'Categorisation', category: 'Patient' },
  12: { id: 12, file: 'screen12.html', title: 'Village Market', category: 'Patient' },
  13: { id: 13, file: 'screen13.html', title: 'Pattern Matching', category: 'Patient' },
  14: { id: 14, file: 'screen14.html', title: 'Shared Game Feedback', category: 'Patient' },
  15: { id: 15, file: 'screen15.html', title: 'Memory Album', category: 'Patient' },
  16: { id: 16, file: 'screen16.html', title: 'SOS Confirm / Call Path', category: 'Patient' },
  17: { id: 17, file: 'screen17.html', title: 'Patient Reminder State', category: 'Patient' },
  18: { id: 18, file: 'screen18.html', title: 'Caregiver Dashboard', category: 'Caregiver / ASHA' },
  19: { id: 19, file: 'screen19.html', title: 'Patient Switching', category: 'Caregiver / ASHA' },
  20: { id: 20, file: 'screen20.html', title: 'Quick Log', category: 'Caregiver / ASHA' },
  21: { id: 21, file: 'screen21.html', title: "Today's Priority", category: 'Caregiver / ASHA' },
  22: { id: 22, file: 'screen22.html', title: 'Reminders', category: 'Caregiver / ASHA' },
  23: { id: 23, file: 'screen23.html', title: 'Memory & Media', category: 'Caregiver / ASHA' },
  24: { id: 24, file: 'screen24.html', title: 'Doctor Link / Access', category: 'Caregiver / ASHA' },
  25: { id: 25, file: 'screen25.html', title: 'SOS Follow-up', category: 'Caregiver / ASHA' },
  26: {
    id: 26,
    file: 'screen26a.html',
    altFile: 'screen26b.html',
    title: 'ASHA Field Visit & Workflow',
    category: 'Caregiver / ASHA'
  },
  27: { id: 27, file: 'screen27.html', title: 'My Patients', category: 'Doctor' },
  28: { id: 28, file: 'screen28.html', title: 'Patient Snapshot', category: 'Doctor' },
  29: { id: 29, file: 'screen29.html', title: 'Since Last Visit', category: 'Doctor' },
  30: { id: 30, file: 'screen30.html', title: 'Trends', category: 'Doctor' },
  31: { id: 31, file: 'screen31.html', title: 'Clinical Assessments', category: 'Doctor' },
  32: { id: 32, file: 'screen32.html', title: 'Medication & Caregiver Summary', category: 'Doctor' },
  33: { id: 33, file: 'screen33.html', title: 'Care Plan & Follow-up', category: 'Doctor' },
  34: { id: 34, file: 'screen34.html', title: 'PDF Export / Clinician Summary', category: 'Doctor' }
};

window.AASRITI_SCREENS = AASRITI_SCREENS;

/**
 * Global Navigation Function
 * @param {number|string} screenId - Target screen number (1..34, or '26a', '26b')
 * @param {object} options - Optional navigation params e.g. { subView: 'assigned-patient' }
 */
function navigateToScreen(screenId, options = {}) {
  // Normalize screenId
  let targetNum = parseInt(screenId, 10);
  let subView = options.subView || null;

  if (typeof screenId === 'string') {
    if (screenId.includes('26a')) { targetNum = 26; subView = 'field-visit'; }
    if (screenId.includes('26b')) { targetNum = 26; subView = 'assigned-patient'; }
  }



  const screenMeta = AASRITI_SCREENS[targetNum];
  if (!screenMeta) {
    console.error('Unknown screen ID:', screenId);
    showToast('Navigation target not found: ' + screenId, 'error');
    return;
  }

  let targetFile = screenMeta.file;
  if (targetNum === 26) {
    if (subView === 'assigned-patient') {
      targetFile = screenMeta.altFile;
      if (window.AasritiStore) window.AasritiStore.setScreen26View('assigned-patient');
    } else {
      targetFile = screenMeta.file;
      if (window.AasritiStore) window.AasritiStore.setScreen26View('field-visit');
    }
  }

  // If in iframe, broadcast message to parent shell
  if (window.self !== window.top) {
    window.parent.postMessage({
      type: 'AASRITI_NAVIGATE',
      screenId: targetNum,
      subView: subView,
      targetFile: targetFile
    }, '*');
    
    // Also update current frame in case standalone
    setTimeout(() => {
      if (window.location.pathname.endsWith(targetFile) === false) {
        window.location.href = targetFile;
      }
    }, 50);
  } else {
    // If in parent window, update hash and iframe src
    const iframe = document.getElementById('app-viewport');
    if (iframe) {
      const fullPath = 'screens/' + targetFile;
      iframe.src = fullPath;
      window.location.hash = '#screen-' + targetNum + (subView ? '-' + subView : '');
      if (typeof window.updateShellScreenInfo === 'function') {
        window.updateShellScreenInfo(targetNum, subView);
      }
    } else {
      window.location.href = targetFile;
    }
  }
}

window.navigateToScreen = navigateToScreen;

/**
 * User-friendly Prototype Toast Notification
 */
function showToast(message, type = 'info') {
  let toastContainer = document.getElementById('aasriti-toast-container');
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.id = 'aasriti-toast-container';
    toastContainer.style.cssText = `
      position: fixed;
      bottom: 84px;
      left: 50%;
      transform: translateX(-50%);
      z-index: 99999;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      pointer-events: none;
      width: 90%;
      max-width: 360px;
    `;
    document.body.appendChild(toastContainer);
  }

  const toast = document.createElement('div');
  const bg = type === 'success' ? '#274133' : type === 'error' ? '#ba1a1a' : '#720227';
  toast.style.cssText = `
    background-color: ${bg};
    color: #FAF4ED;
    padding: 10px 18px;
    border-radius: 9999px;
    font-family: 'Plus Jakarta Sans', sans-serif;
    font-size: 13px;
    font-weight: 600;
    box-shadow: 0 4px 16px rgba(43,24,16,0.25);
    display: flex;
    align-items: center;
    gap: 8px;
    transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    opacity: 0;
    transform: translateY(12px) scale(0.96);
  `;
  toast.innerHTML = `<span>${message}</span>`;
  toastContainer.appendChild(toast);

  requestAnimationFrame(() => {
    toast.style.opacity = '1';
    toast.style.transform = 'translateY(0) scale(1)';
  });

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(-8px) scale(0.96)';
    setTimeout(() => {
      if (toast.parentNode) toast.parentNode.removeChild(toast);
    }, 300);
  }, 2400);
}

window.showToast = showToast;

/**
 * Simulate Phone Call (for SOS)
 */
function simulatePhoneCall(name, number, onResolved) {
  const modal = document.createElement('div');
  modal.id = 'call-simulation-modal';
  modal.style.cssText = `
    position: fixed;
    inset: 0;
    z-index: 100000;
    background: rgba(42, 29, 21, 0.75);
    backdrop-filter: blur(8px);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
  `;
  modal.innerHTML = `
    <div style="background: #FAF4ED; border: 2px solid #CB8067; border-radius: 24px; padding: 24px; width: 100%; max-width: 320px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.3); font-family: 'Plus Jakarta Sans', sans-serif;">
      <div style="width: 72px; height: 72px; background: #CB8067; border-radius: 50%; margin: 0 auto 16px; display: flex; align-items: center; justify-content: center; color: white; animation: pulse 1.5s infinite;">
        <span class="material-symbols-outlined" style="font-size: 36px;">call</span>
      </div>
      <h3 style="font-family: 'Literata', serif; font-size: 20px; font-weight: 700; color: #2A1D15; margin: 0 0 4px;">Simulated Call</h3>
      <p style="font-size: 14px; color: #865304; font-weight: 600; margin: 0 0 8px;">${name || 'Assigned ASHA'}</p>
      <p style="font-size: 13px; color: #574144; margin: 0 0 20px;">Connecting to ${number || '108 / Frontline Worker'}...</p>
      <div style="display: flex; flex-direction: column; gap: 8px;">
        <button id="endCallBtn" style="width: 100%; height: 48px; background: #720227; color: white; border: none; border-radius: 12px; font-weight: 700; font-size: 14px; cursor: pointer;">
          End Call & Follow Up
        </button>
      </div>
    </div>
  `;
  document.body.appendChild(modal);

  modal.querySelector('#endCallBtn').addEventListener('click', () => {
    document.body.removeChild(modal);
    if (typeof onResolved === 'function') onResolved();
  });
}

window.simulatePhoneCall = simulatePhoneCall;

/**
 * Simulate PDF Generation & Download
 */
function simulatePdfExport(patientName = 'Bhaben Barua') {
  showToast('Generating Clinician Summary PDF...', 'info');
  setTimeout(() => {
    showToast(`PDF Summary exported for ${patientName}`, 'success');
  }, 1000);
}

window.simulatePdfExport = simulatePdfExport;
