# Improved screen processor - regenerate all screen wiring with robust selectors
$rawDir = "c:\Users\Dell\OneDrive\Desktop\SIH TeamName\Aastriti\raw_screens"
$outDir = "c:\Users\Dell\OneDrive\Desktop\SIH TeamName\Aastriti\screens"

if (!(Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}

$files = Get-ChildItem -Path $rawDir -Filter "*.html"
Write-Host "Processing $($files.Count) screens..."

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    $fileName = $file.Name
    $screenNum = $fileName.Replace(".html", "")

    # Inject store.js and nav.js into </head>
    $scriptsHead = "<script src=`"../js/store.js`"></script>`n<script src=`"../js/nav.js`"></script>"
    if ($content -match "</head>") {
        $content = $content -replace "</head>", "$scriptsHead`n</head>"
    }

    # Per-screen wiring (all scripts use DOMContentLoaded and robust selectors)
    $wiringScript = ""

    switch ($screenNum) {
        "screen1" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // ENTER button (first button in the page that has crimson bg)
  var allBtns = document.querySelectorAll('button[type="button"]');
  allBtns.forEach(function(btn) {
    var text = btn.textContent.trim();
    if (text.includes('ENTER') || btn.classList.contains('bg-\\[\\#720227\\]') || btn.style.backgroundColor) {
      // Use first match that says ENTER
    }
  });
  
  // Find ENTER button by text content
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('ENTER')) {
      btn.style.cursor = 'pointer';
      btn.addEventListener('click', function() {
        window.AasritiStore.setRole('patient');
        window.navigateToScreen(2);
      });
    } else if (txt.includes('ASHA') || txt.includes('Caregiver')) {
      btn.style.cursor = 'pointer';
      btn.addEventListener('click', function() {
        window.AasritiStore.setRole('caregiver');
        window.navigateToScreen(18);
      });
    } else if (txt.includes('Doctor')) {
      btn.style.cursor = 'pointer';
      btn.addEventListener('click', function() {
        window.AasritiStore.setRole('doctor');
        window.navigateToScreen(27);
      });
    }
  });

  // Language pill area -> Screen 4
  var langArea = document.querySelector('[aria-label="Language selection"]');
  if (langArea) {
    langArea.style.cursor = 'pointer';
    langArea.addEventListener('click', function() { window.navigateToScreen(4); });
  }
});
</script>
'@
        }
        "screen2" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('CONTINUE') || txt.includes('Continue')) {
      btn.addEventListener('click', function() {
        var sel = document.querySelector('[aria-checked="true"]');
        var themeId = sel ? sel.id.replace('card-', '') : 'assam';
        window.AasritiStore.setTheme(themeId);
        window.navigateToScreen(4);
      });
    }
  });
  // Double-tap theme card to quick-continue
  ['assam','manipur','meghalaya'].forEach(function(t) {
    var card = document.getElementById('card-' + t);
    if (card) {
      card.addEventListener('dblclick', function() {
        if (typeof selectTheme === 'function') selectTheme(t);
        window.AasritiStore.setTheme(t);
        window.navigateToScreen(4);
      });
    }
  });
});
</script>
'@
        }
        "screen4" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Continue') || txt.includes('CONTINUE')) {
      btn.addEventListener('click', function() {
        var asCard = document.getElementById('lang-as');
        var lang = (asCard && asCard.getAttribute('aria-checked') === 'true') ? 'as' : 'en';
        window.AasritiStore.setLanguage(lang);
        window.navigateToScreen(5);
      });
    } else if (txt.includes('Back') || txt.includes('back')) {
      btn.addEventListener('click', function() { window.navigateToScreen(2); });
    }
  });
});
</script>
'@
        }
        "screen5" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Header back button
  var headerBack = document.querySelector('header button');
  if (headerBack) {
    headerBack.addEventListener('click', function(e) {
      e.preventDefault(); e.stopPropagation();
      window.navigateToScreen(4);
    });
  }
  // Footer buttons
  document.querySelectorAll('footer button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Confirm') || txt.includes('confirm') || txt.includes('Save')) {
      btn.addEventListener('click', function(e) {
        e.preventDefault(); e.stopPropagation();
        window.navigateToScreen(6);
      });
    } else if (txt.includes('Back') || txt.includes('back')) {
      btn.addEventListener('click', function(e) {
        e.preventDefault(); e.stopPropagation();
        window.navigateToScreen(4);
      });
    }
  });
  // Override confirmSettings
  window.confirmSettings = function() { window.navigateToScreen(6); };
});
</script>
'@
        }
        "screen6" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Override continueBtn
  var continueBtn = document.getElementById('continueBtn');
  if (continueBtn) {
    var newBtn = continueBtn.cloneNode(true);
    continueBtn.parentNode.replaceChild(newBtn, continueBtn);
    newBtn.addEventListener('click', function(e) {
      e.preventDefault();
      window.showToast('Assent recorded. Welcome to AASRITI.', 'success');
      setTimeout(function() { window.navigateToScreen(7); }, 500);
    });
  }

  // Add withdraw + back links at bottom
  var mainDiv = document.querySelector('main > div');
  if (mainDiv) {
    var bottomRow = document.createElement('div');
    bottomRow.style.cssText = 'display:flex;justify-content:space-between;padding:8px 4px 16px;font-size:12px;';
    bottomRow.innerHTML = '<button id="s6Withdraw" style="color:#ba1a1a;font-weight:600;background:none;border:none;cursor:pointer;">I don\'t want to proceed</button>' +
      '<button id="s6Back" style="color:#574144;font-weight:600;background:none;border:none;cursor:pointer;">Back to Settings</button>';
    mainDiv.appendChild(bottomRow);
    document.getElementById('s6Withdraw').addEventListener('click', function() { window.navigateToScreen(1); });
    document.getElementById('s6Back').addEventListener('click', function() { window.navigateToScreen(5); });
  }
});
</script>
'@
        }
        "screen7" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Play / activity hero button -> Screen 8
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('PLAY') || txt.includes('Play')) {
      btn.addEventListener('click', function() { window.navigateToScreen(8); });
    }
  });

  // Reminder cards -> Screen 17
  document.querySelectorAll('section.grid > div, section [class*="grid"] > div').forEach(function(card) {
    card.style.cursor = 'pointer';
    card.addEventListener('click', function() { window.navigateToScreen(17); });
  });

  // SOS button -> Screen 16
  document.querySelectorAll('a, button').forEach(function(el) {
    var txt = el.textContent.trim();
    if (txt.includes('SOS') || txt.includes('Emergency') || txt.includes('emergency')) {
      el.style.cursor = 'pointer';
      el.addEventListener('click', function(e) {
        e.preventDefault();
        window.navigateToScreen(16);
      });
    }
  });

  // Bottom nav
  document.querySelectorAll('nav a').forEach(function(a) {
    var txt = a.textContent.toLowerCase();
    a.addEventListener('click', function(e) {
      e.preventDefault();
      if (txt.includes('today') || txt.includes('home')) window.navigateToScreen(7);
      else if (txt.includes('memor')) window.navigateToScreen(15);
      else if (txt.includes('care')) window.navigateToScreen(21);
    });
  });

  // Quick activities bar
  var mainDiv = document.querySelector('main > div') || document.querySelector('main');
  if (mainDiv) {
    var bar = document.createElement('div');
    bar.style.cssText = 'background:#FFFDF9;border:1px solid #EDDCC5;border-radius:12px;padding:12px;margin-top:8px;';
    bar.innerHTML = '<div style="display:flex;justify-content:space-between;margin-bottom:8px;"><span style="font-weight:700;font-size:11px;color:#274133;text-transform:uppercase;letter-spacing:0.05em;">Cognitive Activities</span></div>' +
      '<div style="display:grid;grid-template-columns:repeat(3,1fr);gap:8px;text-align:center;">' +
      '<button id="g8" style="padding:8px;border-radius:8px;border:1px solid #EDDCC5;background:#FAF4ED;font-size:11px;font-weight:600;cursor:pointer;"><span style="display:block;font-size:20px;">🧩</span>Trivia</button>' +
      '<button id="g9" style="padding:8px;border-radius:8px;border:1px solid #EDDCC5;background:#FAF4ED;font-size:11px;font-weight:600;cursor:pointer;"><span style="display:block;font-size:20px;">🔊</span>Voice Cue</button>' +
      '<button id="g10" style="padding:8px;border-radius:8px;border:1px solid #EDDCC5;background:#FAF4ED;font-size:11px;font-weight:600;cursor:pointer;"><span style="display:block;font-size:20px;">📋</span>Sequencing</button>' +
      '<button id="g11" style="padding:8px;border-radius:8px;border:1px solid #EDDCC5;background:#FAF4ED;font-size:11px;font-weight:600;cursor:pointer;"><span style="display:block;font-size:20px;">🗂️</span>Categories</button>' +
      '<button id="g12" style="padding:8px;border-radius:8px;border:1px solid #EDDCC5;background:#FAF4ED;font-size:11px;font-weight:600;cursor:pointer;"><span style="display:block;font-size:20px;">🛒</span>Market</button>' +
      '<button id="g13" style="padding:8px;border-radius:8px;border:1px solid #EDDCC5;background:#FAF4ED;font-size:11px;font-weight:600;cursor:pointer;"><span style="display:block;font-size:20px;">🔷</span>Pattern</button>' +
      '</div>';
    mainDiv.appendChild(bar);
    document.getElementById('g8').onclick = function() { window.navigateToScreen(8); };
    document.getElementById('g9').onclick = function() { window.navigateToScreen(9); };
    document.getElementById('g10').onclick = function() { window.navigateToScreen(10); };
    document.getElementById('g11').onclick = function() { window.navigateToScreen(11); };
    document.getElementById('g12').onclick = function() { window.navigateToScreen(12); };
    document.getElementById('g13').onclick = function() { window.navigateToScreen(13); };
  }

  // Reminder quick link
  var reminderSection = document.querySelector('section');
  if (reminderSection) {
    var btn = document.createElement('button');
    btn.textContent = '⏰ View All Reminders';
    btn.style.cssText = 'width:100%;padding:10px;margin-top:8px;border-radius:10px;border:1px solid #EDDCC5;background:#FAF4ED;font-weight:600;font-size:13px;cursor:pointer;color:#274133;';
    btn.onclick = function() { window.navigateToScreen(17); };
    reminderSection.after(btn);
  }

  // Memory Album link
  var memBtn = document.createElement('button');
  memBtn.textContent = '📸 Memory Album';
  memBtn.style.cssText = 'width:100%;padding:10px;margin-top:4px;border-radius:10px;border:1px solid #EDDCC5;background:#FAF4ED;font-weight:600;font-size:13px;cursor:pointer;color:#274133;';
  memBtn.onclick = function() { window.navigateToScreen(15); };
  if (mainDiv) mainDiv.appendChild(memBtn);
});
</script>
'@
        }
        "screen8" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Continue') || txt.includes('Correct') || txt.includes('Finish') || txt.includes('Next') || txt.includes('Submit') || txt.includes('SUBMIT')) {
      btn.addEventListener('click', function() {
        window.AasritiStore.recordGameCompletion('trivia', 100);
        window.navigateToScreen(14);
      });
    } else if (txt.includes('Try Again') || txt.includes('Retry')) {
      btn.addEventListener('click', function() { window.navigateToScreen(8); });
    } else if (txt.includes('Home') || txt.includes('Back')) {
      btn.addEventListener('click', function() { window.navigateToScreen(7); });
    }
  });
  // Header back
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(7); });
});
</script>
'@
        }
        "screen9" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Continue') || txt.includes('Answer') || txt.includes('Submit') || txt.includes('Next') || txt.includes('Finish')) {
      btn.addEventListener('click', function() {
        window.AasritiStore.recordGameCompletion('voiceCue', 90);
        window.navigateToScreen(14);
      });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(7); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(7); });
});
</script>
'@
        }
        "screen10" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Check') || txt.includes('Continue') || txt.includes('Submit') || txt.includes('Finish') || txt.includes('Done')) {
      btn.addEventListener('click', function() {
        window.AasritiStore.recordGameCompletion('sequencing', 85);
        window.navigateToScreen(14);
      });
    } else if (txt.includes('Try Again') || txt.includes('Retry')) {
      btn.addEventListener('click', function() { window.navigateToScreen(10); });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(7); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(7); });
});
</script>
'@
        }
        "screen11" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Check') || txt.includes('Continue') || txt.includes('Submit') || txt.includes('Finish') || txt.includes('Done')) {
      btn.addEventListener('click', function() {
        window.AasritiStore.recordGameCompletion('categorisation', 95);
        window.navigateToScreen(14);
      });
    } else if (txt.includes('Try Again') || txt.includes('Retry')) {
      btn.addEventListener('click', function() { window.navigateToScreen(11); });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(7); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(7); });
});
</script>
'@
        }
        "screen12" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Check') || txt.includes('Continue') || txt.includes('Submit') || txt.includes('Match') || txt.includes('Done') || txt.includes('Finish')) {
      btn.addEventListener('click', function() {
        window.AasritiStore.recordGameCompletion('villageMarket', 100);
        window.navigateToScreen(14);
      });
    } else if (txt.includes('Try Again') || txt.includes('Retry')) {
      btn.addEventListener('click', function() { window.navigateToScreen(12); });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(7); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(7); });
});
</script>
'@
        }
        "screen13" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button, [role="button"]').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Same') || txt.includes('Rotated') || txt.includes('Mirror') || txt.includes('Check') || txt.includes('Continue')) {
      btn.addEventListener('click', function() {
        window.AasritiStore.recordGameCompletion('pattern', 90);
        window.navigateToScreen(14);
      });
    } else if (txt.includes('Try Again') || txt.includes('Retry')) {
      btn.addEventListener('click', function() { window.navigateToScreen(13); });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(7); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(7); });
});
</script>
'@
        }
        "screen14" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Play Again') || txt.includes('Retry')) {
      btn.addEventListener('click', function() { window.navigateToScreen(8); });
    } else if (txt.includes('Continue') || txt.includes('Next') || txt.includes('Finish') || txt.includes('Save') || txt.includes('Home') || txt.includes('Done')) {
      btn.addEventListener('click', function() { window.navigateToScreen(7); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(7); });
});
</script>
'@
        }
        "screen15" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Memory cards - internal interaction
  document.querySelectorAll('main .rounded-xl, main .rounded-2xl, main [class*="card"]').forEach(function(c) {
    c.style.cursor = 'pointer';
    c.addEventListener('click', function(e) {
      if (!e.target.closest('button')) {
        window.showToast('Memory playing: Archival family recording', 'info');
      }
    });
  });
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Save')) {
      btn.addEventListener('click', function() { window.showToast('Memory Album saved', 'success'); });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(7); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(7); });
});
</script>
'@
        }
        "screen16" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button, a').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Call') || txt.includes('Confirm') || txt.includes('SOS') || txt.includes('Yes')) {
      btn.addEventListener('click', function(e) {
        e.preventDefault();
        window.AasritiStore.triggerSOS();
        window.simulatePhoneCall('Rina Gogoi (ASHA)', '108', function() {
          window.navigateToScreen(25);
        });
      });
    } else if (txt.includes('Cancel') || txt.includes('Back') || txt.includes('No')) {
      btn.addEventListener('click', function(e) {
        e.preventDefault();
        window.navigateToScreen(7);
      });
    }
  });
});
</script>
'@
        }
        "screen17" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Taken') || txt.includes('Done') || txt.includes('Acknowledge') || txt.includes('Yes') || txt.includes('Confirm')) {
      btn.addEventListener('click', function() {
        window.AasritiStore.updateReminderStatus('rem-1', 'taken');
        window.showToast('Reminder marked done', 'success');
        setTimeout(function() { window.navigateToScreen(7); }, 400);
      });
    } else if (txt.includes('Missed') || txt.includes('Skip')) {
      btn.addEventListener('click', function() {
        window.AasritiStore.updateReminderStatus('rem-1', 'missed');
        window.showToast('Recorded as missed', 'info');
        setTimeout(function() { window.navigateToScreen(7); }, 400);
      });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(7); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(7); });
});
</script>
'@
        }
        "screen18" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Quick Log button
  var qlBtn = document.getElementById('quickLogBtn');
  if (qlBtn) {
    // Remove old handlers
    var newQl = qlBtn.cloneNode(true);
    qlBtn.parentNode.replaceChild(newQl, qlBtn);
    newQl.addEventListener('click', function() { window.navigateToScreen(20); });
  }

  // Scheduled Reminders section
  document.querySelectorAll('h3, h2').forEach(function(h) {
    var txt = h.textContent.trim();
    if (txt.includes('Reminder') || txt.includes('Scheduled')) {
      var card = h.closest('div[class*="rounded"]');
      if (card) {
        card.style.cursor = 'pointer';
        card.addEventListener('click', function(e) {
          if (!e.target.closest('button')) window.navigateToScreen(22);
        });
      }
    }
  });

  // Doctor Link card
  document.querySelectorAll('[class*="rounded"]').forEach(function(c) {
    if (c.textContent.includes('Doctor Link') || c.textContent.includes('Dr. Barua')) {
      c.style.cursor = 'pointer';
      c.addEventListener('click', function(e) {
        if (!e.target.closest('button')) window.navigateToScreen(24);
      });
    }
    if (c.textContent.includes("Today's Priority") || c.textContent.includes('Review Needed')) {
      c.style.cursor = 'pointer';
      c.addEventListener('click', function(e) {
        if (!e.target.closest('button')) window.navigateToScreen(21);
      });
    }
  });

  // Mark Given button
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Mark Given') || txt.includes('Snooze')) {
      btn.addEventListener('click', function() { window.showToast('Care action recorded', 'success'); });
    }
  });

  // Bottom nav
  document.querySelectorAll('nav a').forEach(function(a) {
    var txt = a.textContent.toLowerCase();
    a.addEventListener('click', function(e) {
      e.preventDefault();
      if (txt.includes('dashboard')) window.navigateToScreen(18);
      else if (txt.includes('log')) window.navigateToScreen(20);
      else if (txt.includes('reminder')) window.navigateToScreen(22);
      else if (txt.includes('circle') || txt.includes('family') || txt.includes('care')) window.navigateToScreen(19);
    });
  });

  // Patient selector in header
  document.querySelectorAll('header button').forEach(function(btn) {
    if (btn.textContent.includes('Mother') || btn.textContent.includes('Aita') || btn.textContent.includes('Bhaben')) {
      btn.addEventListener('click', function() { window.navigateToScreen(19); });
    }
  });

  // Caregiver Hub action bar
  var mainDiv = document.querySelector('main > div');
  if (mainDiv) {
    var hubBar = document.createElement('div');
    hubBar.style.cssText = 'background:#fff1ec;border:1px solid #ddbfc2;border-radius:12px;padding:12px;margin-top:8px;';
    hubBar.innerHTML = '<div style="font-size:11px;font-weight:700;color:#865304;text-transform:uppercase;letter-spacing:0.05em;margin-bottom:8px;">Quick Navigation</div>' +
      '<div style="display:grid;grid-template-columns:repeat(4,1fr);gap:8px;text-align:center;">' +
      '<button id="h19" style="padding:8px 4px;border-radius:8px;border:1px solid #ddbfc2;background:#fff8f6;font-size:11px;cursor:pointer;font-weight:600;">👥<br>Patients (19)</button>' +
      '<button id="h26" style="padding:8px 4px;border-radius:8px;border:1px solid #ddbfc2;background:#fff8f6;font-size:11px;cursor:pointer;font-weight:600;">🏥<br>ASHA Visit (26)</button>' +
      '<button id="h23" style="padding:8px 4px;border-radius:8px;border:1px solid #ddbfc2;background:#fff8f6;font-size:11px;cursor:pointer;font-weight:600;">📸<br>Media (23)</button>' +
      '<button id="h30" style="padding:8px 4px;border-radius:8px;border:1px solid #ddbfc2;background:#fff8f6;font-size:11px;cursor:pointer;font-weight:600;">📊<br>Trends (30)</button>' +
      '</div>';
    mainDiv.appendChild(hubBar);
    document.getElementById('h19').onclick = function() { window.navigateToScreen(19); };
    document.getElementById('h26').onclick = function() { window.navigateToScreen(26); };
    document.getElementById('h23').onclick = function() { window.navigateToScreen(23); };
    document.getElementById('h30').onclick = function() { window.navigateToScreen(30); };
  }
});
</script>
'@
        }
        "screen19" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Patient cards
  document.querySelectorAll('main [class*="rounded"]').forEach(function(card) {
    if (card.querySelector('img') || card.querySelector('h3')) {
      card.style.cursor = 'pointer';
      card.addEventListener('click', function() {
        window.showToast('Active patient switched', 'success');
        setTimeout(function() { window.navigateToScreen(18); }, 300);
      });
    }
  });
  // Enter Patient Mode button
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Patient Mode') || txt.includes('Elder Mode') || txt.includes('Enter as') || txt.includes('Switch to')) {
      btn.addEventListener('click', function() { window.navigateToScreen(7); });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(18); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(18); });

  // Add Enter Patient Mode button
  var mainDiv = document.querySelector('main > div') || document.querySelector('main');
  if (mainDiv) {
    var epBtn = document.createElement('button');
    epBtn.textContent = '🌿 Enter Elder / Patient Mode';
    epBtn.style.cssText = 'width:100%;padding:14px;margin-top:12px;border-radius:12px;border:2px solid #720227;background:#720227;color:#FAF4ED;font-weight:700;font-size:14px;cursor:pointer;';
    epBtn.onclick = function() { window.navigateToScreen(7); };
    mainDiv.appendChild(epBtn);
  }
});
</script>
'@
        }
        "screen20" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Category selections
  document.querySelectorAll('button[class*="filter"], .filter-pill, [role="radio"], [role="tab"]').forEach(function(btn) {
    btn.addEventListener('click', function() {
      document.querySelectorAll('[data-selected]').forEach(function(b) { delete b.dataset.selected; });
      btn.dataset.selected = true;
    });
  });
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Save') || txt.includes('Log Care') || txt.includes('Submit') || txt.includes('Confirm')) {
      btn.addEventListener('click', function(e) {
        e.preventDefault();
        window.AasritiStore.addQuickLog('Care', 'Routine Care Log', 'Noted and recorded');
        window.showToast('Care log recorded', 'success');
        setTimeout(function() { window.navigateToScreen(21); }, 400);
      });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(18); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(18); });
});
</script>
'@
        }
        "screen21" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Log') || txt.includes('Record')) {
      btn.addEventListener('click', function() { window.navigateToScreen(20); });
    } else if (txt.includes('Done') || txt.includes('Acknowledge') || txt.includes('Finish') || txt.includes('Confirm')) {
      btn.addEventListener('click', function() {
        window.showToast('Priorities acknowledged', 'success');
        setTimeout(function() { window.navigateToScreen(18); }, 300);
      });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(18); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(18); });
});
</script>
'@
        }
        "screen22" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Save') || txt.includes('Update')) {
      btn.addEventListener('click', function() {
        window.showToast('Reminders schedule saved', 'success');
        setTimeout(function() { window.navigateToScreen(18); }, 400);
      });
    } else if (txt.includes('Add') || txt.includes('New')) {
      btn.addEventListener('click', function() { window.showToast('New reminder added', 'info'); });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(18); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(18); });
});
</script>
'@
        }
        "screen23" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Album') || txt.includes('Save to')) {
      btn.addEventListener('click', function() {
        window.showToast('Memory saved to Album', 'success');
        setTimeout(function() { window.navigateToScreen(15); }, 400);
      });
    } else if (txt.includes('Photo') || txt.includes('Voice') || txt.includes('Add')) {
      btn.addEventListener('click', function() { window.showToast('Media captured', 'info'); });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(18); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(18); });
});
</script>
'@
        }
        "screen24" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('PDF') || txt.includes('Summary') || txt.includes('Export')) {
      btn.addEventListener('click', function() { window.navigateToScreen(34); });
    } else if (txt.includes('Verify') || txt.includes('Grant') || txt.includes('Link')) {
      btn.addEventListener('click', function() { window.showToast('Doctor access verified and active', 'success'); });
    } else if (txt.includes('Revoke')) {
      btn.addEventListener('click', function() { window.showToast('Doctor access revoked', 'info'); });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(18); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(18); });
});
</script>
'@
        }
        "screen25" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Call') || txt.includes('Phone')) {
      btn.addEventListener('click', function() {
        window.simulatePhoneCall('Rina Gogoi (ASHA)', '108');
      });
    } else if (txt.includes('Resolved') || txt.includes('Confirm') || txt.includes('Save')) {
      btn.addEventListener('click', function() {
        window.AasritiStore.resolveSOS('Stabilized by ASHA worker visit');
        window.showToast('Incident marked resolved', 'success');
        setTimeout(function() { window.navigateToScreen(18); }, 400);
      });
    } else if (txt.includes('Follow-up') || txt.includes('Priority')) {
      btn.addEventListener('click', function() {
        window.showToast('Flagged for Today Priority review', 'info');
        setTimeout(function() { window.navigateToScreen(21); }, 400);
      });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(18); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(18); });
});
</script>
'@
        }
        "screen26a" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Inject sub-view tab switcher
  var header = document.querySelector('header');
  if (header) {
    var tabDiv = document.createElement('div');
    tabDiv.style.cssText = 'background:#ffe9e2;padding:6px 12px;display:flex;align-items:center;justify-content:center;gap:8px;border-bottom:1px solid #ddbfc2;';
    tabDiv.innerHTML = '<button id="tab26a" style="padding:5px 14px;border-radius:20px;border:none;background:#720227;color:#fff;font-weight:700;font-size:12px;cursor:pointer;">Field Visit</button>' +
      '<button id="tab26b" style="padding:5px 14px;border-radius:20px;border:1px solid #ddbfc2;background:#fff8f6;color:#574144;font-weight:600;font-size:12px;cursor:pointer;">Assigned-Patient Workflow</button>';
    header.parentNode.insertBefore(tabDiv, header.nextSibling);
    document.getElementById('tab26b').onclick = function() { window.navigateToScreen(26, { subView: 'assigned-patient' }); };
  }

  // Record New Visit
  var recordBtn = document.getElementById('recordVisitBtn');
  if (recordBtn) {
    recordBtn.addEventListener('click', function() { window.navigateToScreen(20); });
  }

  // Patient cards -> Screen 19
  document.querySelectorAll('main [class*="rounded"]').forEach(function(c) {
    if (c.querySelector('img') && c.querySelector('h3')) {
      c.style.cursor = 'pointer';
      c.addEventListener('click', function() { window.navigateToScreen(19); });
    }
  });

  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Log') || txt.includes('Observation')) {
      btn.addEventListener('click', function() { window.navigateToScreen(20); });
    } else if (txt.includes('Sync') || txt.includes('sync')) {
      btn.addEventListener('click', function() {
        window.AasritiStore.triggerSync(function() { window.showToast('All visits synced with PHC', 'success'); });
      });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(18); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(18); });
});
</script>
'@
        }
        "screen26b" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Inject sub-view tab switcher
  var header = document.querySelector('header');
  if (header) {
    var tabDiv = document.createElement('div');
    tabDiv.style.cssText = 'background:#ffe9e2;padding:6px 12px;display:flex;align-items:center;justify-content:center;gap:8px;border-bottom:1px solid #ddbfc2;';
    tabDiv.innerHTML = '<button id="tab26a" style="padding:5px 14px;border-radius:20px;border:1px solid #ddbfc2;background:#fff8f6;color:#574144;font-weight:600;font-size:12px;cursor:pointer;">Field Visit</button>' +
      '<button id="tab26b" style="padding:5px 14px;border-radius:20px;border:none;background:#720227;color:#fff;font-weight:700;font-size:12px;cursor:pointer;">Assigned-Patient Workflow</button>';
    header.parentNode.insertBefore(tabDiv, header.nextSibling);
    document.getElementById('tab26a').onclick = function() { window.navigateToScreen(26, { subView: 'field-visit' }); };
  }

  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Log') || txt.includes('Vital')) {
      btn.addEventListener('click', function() { window.navigateToScreen(20); });
    } else if (txt.includes('Priority')) {
      btn.addEventListener('click', function() { window.navigateToScreen(21); });
    } else if (txt.includes('SOS') || txt.includes('Emergency')) {
      btn.addEventListener('click', function() { window.navigateToScreen(25); });
    } else if (txt.includes('Patient') || txt.includes('patient')) {
      btn.addEventListener('click', function() { window.navigateToScreen(19); });
    } else if (txt.includes('Back') || txt.includes('Home')) {
      btn.addEventListener('click', function() { window.navigateToScreen(18); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(18); });
});
</script>
'@
        }
        "screen27" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Patient cards -> Screen 28
  document.querySelectorAll('.patient-card, main [class*="rounded"]').forEach(function(c) {
    if (c.querySelector('h3') || c.querySelector('img')) {
      c.style.cursor = 'pointer';
      c.addEventListener('click', function() {
        window.AasritiStore.setRole('doctor');
        window.navigateToScreen(28);
      });
    }
  });

  // View patient buttons
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('View') || txt.includes('Open') || txt.includes('Snapshot')) {
      btn.addEventListener('click', function() { window.navigateToScreen(28); });
    } else if (txt.includes('Back') || txt.includes('Logout')) {
      btn.addEventListener('click', function() { window.navigateToScreen(1); });
    }
  });

  // Add Logout button if not present
  var header = document.querySelector('header');
  if (header && !header.querySelector('#logoutBtn')) {
    var logoutBtn = document.createElement('button');
    logoutBtn.id = 'logoutBtn';
    logoutBtn.textContent = 'Logout';
    logoutBtn.style.cssText = 'padding:6px 12px;border-radius:8px;border:1px solid #ddbfc2;background:#fff8f6;font-size:12px;font-weight:600;cursor:pointer;color:#574144;';
    logoutBtn.onclick = function() { window.navigateToScreen(1); };
    var headerInner = header.querySelector('div');
    if (headerInner) headerInner.appendChild(logoutBtn);
  }
});
</script>
'@
        }
        "screen28" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Navigation cards
  document.querySelectorAll('main [class*="rounded"], main button').forEach(function(el) {
    var txt = el.textContent.trim();
    if (txt.includes('Since Last Visit') || txt.includes('Last Visit')) {
      el.style.cursor = 'pointer';
      el.addEventListener('click', function() { window.navigateToScreen(29); });
    } else if (txt.includes('Trends') || txt.includes('Cognitive Trajectory') || txt.includes('trajectory')) {
      el.style.cursor = 'pointer';
      el.addEventListener('click', function() { window.navigateToScreen(30); });
    } else if (txt.includes('Assessment') || txt.includes('ACE') || txt.includes('MoCA')) {
      el.style.cursor = 'pointer';
      el.addEventListener('click', function() { window.navigateToScreen(31); });
    } else if (txt.includes('Medication') || txt.includes('Caregiver Summary')) {
      el.style.cursor = 'pointer';
      el.addEventListener('click', function() { window.navigateToScreen(32); });
    } else if (txt.includes('Care Plan') || txt.includes('Follow-up')) {
      el.style.cursor = 'pointer';
      el.addEventListener('click', function() { window.navigateToScreen(33); });
    } else if (txt.includes('Export') || txt.includes('PDF')) {
      el.style.cursor = 'pointer';
      el.addEventListener('click', function() { window.navigateToScreen(34); });
    }
  });

  // Back button -> Screen 27
  document.querySelectorAll('button, header button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('My Patients') || txt.includes('Back')) {
      btn.addEventListener('click', function() { window.navigateToScreen(27); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(27); });

  // Navigation quick bar
  var mainDiv = document.querySelector('main > div') || document.querySelector('main');
  if (mainDiv) {
    var navBar = document.createElement('div');
    navBar.style.cssText = 'background:#fff1ec;border:1px solid #ddbfc2;border-radius:12px;padding:12px;margin-top:12px;';
    navBar.innerHTML = '<div style="font-size:11px;font-weight:700;color:#865304;text-transform:uppercase;letter-spacing:0.05em;margin-bottom:8px;">Clinical Navigation</div>' +
      '<div style="display:grid;grid-template-columns:repeat(3,1fr);gap:8px;text-align:center;">' +
      '<button id="nb29" style="padding:8px 4px;border-radius:8px;border:1px solid #ddbfc2;background:#fff8f6;font-size:11px;cursor:pointer;font-weight:600;">📋<br>Last Visit (29)</button>' +
      '<button id="nb30" style="padding:8px 4px;border-radius:8px;border:1px solid #ddbfc2;background:#fff8f6;font-size:11px;cursor:pointer;font-weight:600;">📈<br>Trends (30)</button>' +
      '<button id="nb31" style="padding:8px 4px;border-radius:8px;border:1px solid #ddbfc2;background:#fff8f6;font-size:11px;cursor:pointer;font-weight:600;">🧪<br>Assess (31)</button>' +
      '<button id="nb32" style="padding:8px 4px;border-radius:8px;border:1px solid #ddbfc2;background:#fff8f6;font-size:11px;cursor:pointer;font-weight:600;">💊<br>Meds (32)</button>' +
      '<button id="nb33" style="padding:8px 4px;border-radius:8px;border:1px solid #ddbfc2;background:#fff8f6;font-size:11px;cursor:pointer;font-weight:600;">📝<br>Care Plan (33)</button>' +
      '<button id="nb34" style="padding:8px 4px;border-radius:8px;border:1px solid #ddbfc2;background:#720227;color:#fff;font-size:11px;cursor:pointer;font-weight:600;">📄<br>PDF (34)</button>' +
      '</div>';
    mainDiv.appendChild(navBar);
    document.getElementById('nb29').onclick = function() { window.navigateToScreen(29); };
    document.getElementById('nb30').onclick = function() { window.navigateToScreen(30); };
    document.getElementById('nb31').onclick = function() { window.navigateToScreen(31); };
    document.getElementById('nb32').onclick = function() { window.navigateToScreen(32); };
    document.getElementById('nb33').onclick = function() { window.navigateToScreen(33); };
    document.getElementById('nb34').onclick = function() { window.navigateToScreen(34); };
  }
});
</script>
'@
        }
        "screen29" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button, a').forEach(function(el) {
    var txt = el.textContent.trim();
    if (txt.includes('Trends')) {
      el.addEventListener('click', function() { window.navigateToScreen(30); });
    } else if (txt.includes('Assessment')) {
      el.addEventListener('click', function() { window.navigateToScreen(31); });
    } else if (txt.includes('Medication') || txt.includes('Caregiver')) {
      el.addEventListener('click', function() { window.navigateToScreen(32); });
    } else if (txt.includes('Care Plan')) {
      el.addEventListener('click', function() { window.navigateToScreen(33); });
    } else if (txt.includes('Back') || txt.includes('Snapshot')) {
      el.addEventListener('click', function() { window.navigateToScreen(28); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(28); });
});
</script>
'@
        }
        "screen30" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Period filter pills
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('7') || txt.includes('30') || txt.includes('90') || txt.includes('days') || txt.includes('Days')) {
      btn.addEventListener('click', function() {
        document.querySelectorAll('button[data-period]').forEach(function(b) {
          b.dataset.period = null;
        });
        btn.dataset.period = 'active';
        window.showToast('Period updated: ' + txt, 'info');
      });
    } else if (txt.includes('Assessment')) {
      btn.addEventListener('click', function() { window.navigateToScreen(31); });
    } else if (txt.includes('Back') || txt.includes('Snapshot')) {
      btn.addEventListener('click', function() { window.navigateToScreen(28); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(28); });
});
</script>
'@
        }
        "screen31" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Export') || txt.includes('Summary')) {
      btn.addEventListener('click', function() { window.navigateToScreen(34); });
    } else if (txt.includes('Save') || txt.includes('Record') || txt.includes('Add')) {
      btn.addEventListener('click', function() { window.showToast('Clinical assessment recorded', 'success'); });
    } else if (txt.includes('Back') || txt.includes('Snapshot')) {
      btn.addEventListener('click', function() { window.navigateToScreen(28); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(28); });
});
</script>
'@
        }
        "screen32" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button, a').forEach(function(el) {
    var txt = el.textContent.trim();
    if (txt.includes('Care Plan')) {
      el.addEventListener('click', function() { window.navigateToScreen(33); });
    } else if (txt.includes('Back') || txt.includes('Snapshot')) {
      el.addEventListener('click', function() { window.navigateToScreen(28); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(28); });
});
</script>
'@
        }
        "screen33" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Stable') || txt.includes('Monitor') || txt.includes('Review')) {
      btn.addEventListener('click', function() {
        document.querySelectorAll('[data-status-btn]').forEach(function(b) { b.removeAttribute('data-selected'); });
        btn.dataset.selected = true;
        btn.style.outline = '2px solid #720227';
      });
    } else if (txt.includes('Save') || txt.includes('Confirm')) {
      btn.addEventListener('click', function() {
        window.AasritiStore.updateDoctorCarePlan({ status: 'Stable' });
        window.showToast('Care plan saved to clinical record', 'success');
        setTimeout(function() { window.navigateToScreen(28); }, 400);
      });
    } else if (txt.includes('Export') || txt.includes('PDF')) {
      btn.addEventListener('click', function() { window.navigateToScreen(34); });
    } else if (txt.includes('Back') || txt.includes('Snapshot')) {
      btn.addEventListener('click', function() { window.navigateToScreen(28); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(28); });
});
</script>
'@
        }
        "screen34" {
            $wiringScript = @'
<script>
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('button').forEach(function(btn) {
    var txt = btn.textContent.trim();
    if (txt.includes('Generate') || txt.includes('Export') || txt.includes('Download') || txt.includes('PDF')) {
      btn.addEventListener('click', function() { window.simulatePdfExport('Bhaben Barua'); });
    } else if (txt.includes('Share')) {
      btn.addEventListener('click', function() { window.showToast('Report shared with Caregiver & ASHA', 'success'); });
    } else if (txt.includes('My Patients') || txt.includes('All Patients')) {
      btn.addEventListener('click', function() { window.navigateToScreen(27); });
    } else if (txt.includes('Patient') || txt.includes('Done') || txt.includes('Back') || txt.includes('Snapshot')) {
      btn.addEventListener('click', function() { window.navigateToScreen(28); });
    }
  });
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.navigateToScreen(28); });
});
</script>
'@
        }
        default {
            # Generic back handler for any screen not listed
            $wiringScript = @"
<script>
document.addEventListener('DOMContentLoaded', function() {
  var hback = document.querySelector('header button, [aria-label="Go back"]');
  if (hback) hback.addEventListener('click', function() { window.history.back(); });
});
</script>
"@
        }
    }

    # Append wiring before </body>
    if ($wiringScript -ne "") {
        if ($content -match "</body>") {
            $content = $content -replace "</body>", "$wiringScript`n</body>"
        } else {
            $content = $content + "`n" + $wiringScript
        }
    }

    $destPath = Join-Path $outDir $fileName
    [System.IO.File]::WriteAllText($destPath, $content, [System.Text.Encoding]::UTF8)
    Write-Host "Done: $fileName"
}

Write-Host "`nAll $($files.Count) screens rebuilt in $outDir"
