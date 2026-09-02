# SmritiSetu (SIH26003) — Clinical Game Design Document (GDD)

## 🧠 1. Clinical Evidence Foundation & Theoretical Framework

SmritiSetu’s cognitive training exercises are grounded in empirical clinical trials demonstrating neuroplasticity preservation in aging brains:
1. **ACTIVE Cognitive Training Trial (Advanced Cognitive Training for Independent and Vital Elderly)**:
   * *Citation*: Ball, K., et al. (2002). "Effects of cognitive training interventions with older adults: A randomized controlled trial." *JAMA*, 288(18), 2271–2281.
   * *Clinical Finding*: Speed of processing training produced statistically significant reductions in longitudinal functional decline and instrumental activities of daily living (IADLs) over a 10-year follow-up.
2. **US POINTER / FINGER Multidomain Intervention Model**:
   * *Citation*: Kivipelto, M., et al. (2018). "World-Wide FINGERS: Multidomain intervention to prevent dementia." *Alzheimer's & Dementia*, 14(10), 1361–1370.
   * *Clinical Finding*: Simultaneous cognitive stimulation targeting executive function, episodic memory, and linguistic retrieval delays progression from Mild Cognitive Impairment (MCI) to Alzheimer's disease.
3. **Boston Naming Test (BNT) & Category Fluency in Cross-Cultural Populations**:
   * *Citation*: Kaplan, E., Goodglass, H., & Weintraub, S. (2001). *The Boston Naming Test*. Philadelphia: Lippincott Williams & Wilkins.

---

## 🎮 2. Game Suite Detailed Specifications

```
                     +---------------------------------------+
                     |     SMRITISETU CLINICAL GAMES         |
                     +---------------------------------------+
                                         |
     +-----------------------------------+-----------------------------------+
     |                                   |                                   |
     v                                   v                                   v
+-----------------------+     +-----------------------+     +-----------------------+
|     GAME 1:           |     |     GAME 2:           |     |     GAME 3:           |
|   SPEED MATCH         |     |   STORY WEAVER        |     |   PICTURE NAMING      |
| (প্ৰক্ৰিয়া বেগ)      |     | (সাধুকথা স্মৃতি)       |     | (ছবি চিনাক্তকৰণ)     |
|                       |     |                       |     |                       |
| Clinical Domain:      |     | Clinical Domain:      |     | Clinical Domain:      |
| Visual Processing &   |     | Episodic Memory &     |     | Semantic Retrieval &  |
| Selective Attention   |     | Verbal Phonemic Recall|     | Visual Confrontation  |
+-----------------------+     +-----------------------+     +-----------------------+
```

---

### 🕹️ Game 1: Speed Match (প্ৰক্ৰিয়া বেগ / ꯌꯥꯡꯅꯥ ꯆꯥꯡꯗꯝꯅꯕ)

#### Clinical Target
Visual processing speed, divided attention, and motor reaction time. Modeled after the ACTIVE UFOV (Useful Field of View) subtest.

#### Mechanics & Interaction Loop
* The screen displays a central target cultural motif for 1200ms.
* The screen then displays two high-contrast options side-by-side (minimum 120x120 dp touch cards).
* The patient taps the card that matches the target.
* Adaptive laddering: If the patient achieves 3 consecutive correct answers with reaction time $< 1000\text{ms}$, target display duration shortens by $100\text{ms}$ (floor: $400\text{ms}$). If 2 consecutive errors occur, display duration extends by $200\text{ms}$ (ceiling: $2500\text{ms}$).

#### NER Cultural Adaptation & Stimuli Assets
* **Assam**: *Japi* (কঁহুৱা জাপি woven bamboo hat), *Kaziranga Rhino* (*Rhinoceros unicornis*), *Dhol* (Bihu drum), *Pepa* (buffalo horn flute).
* **Manipur**: *Sangai Deer* (*Rucervus eldii eldii*), *Kangla Sha* mythical dragon lion, *Pung* drum.
* **Mizoram / Nagaland**: *Great Indian Hornbill* feather, *Mithun* (*Bos frontalis*), Naga traditional spear.
* **Textile Patterns**: High-contrast geometric motifs from Assamese *Gamosa*, Bodo *Dokhona*, and Manipuri *Innaphi*.

#### Mathematical Scoring Algorithm
The clinical speed score $S_{\text{speed}}$ for round $i$ is calculated using an exponential decay reaction time penalty:

$$S_{i} = 
\begin{cases} 
0, & \text{if incorrect} \\
100 \times \exp\left(-\frac{\max(0, RT_{i} - RT_{\text{floor}})}{\tau}\right) \times D_{i}, & \text{if correct}
\end{cases}$$

Where:
* $RT_{i}$ = Reaction time in milliseconds for round $i$.
* $RT_{\text{floor}}$ = Motor baseline floor ($350\text{ms}$ for elderly population to prevent motor penalty).
* $\tau$ = Decay time constant ($1200\text{ms}$).
* $D_{i}$ = Difficulty multiplier ($1.0$ at Level 1 up to $1.8$ at Level 5).

Overall Session Score:
$$S_{\text{session}} = \frac{1}{N} \sum_{i=1}^{N} S_{i} - (P_{\text{error}} \times N_{\text{errors}})$$
Where $P_{\text{error}} = 15$ points deduction per false tap.

---

### 🕹️ Game 2: Story Weaver (সাধুকথা স্মৃতি / ꯋꯥꯔꯤ ꯅꯤꯡꯁꯤꯡꯕ)

#### Clinical Target
Auditory episodic memory, narrative retention, and spontaneous verbal recall. Modeled after the Wechsler Memory Scale (WMS) Logical Memory subtest.

#### Mechanics & Interaction Loop
* The patient listens to a 45-second native audio parable spoken in their mother tongue (Assamese, Manipuri, or Bodo) accompanied by static illustrative storyboards.
* A chime sounds, and the patient is prompted verbally: *"Tell me everything you remember from the story of Tejimola."*
* The patient taps a large microphone button (72x72 dp) and speaks freely for up to 60 seconds.
* The local **IndicConformer ONNX** model transcribes the speech stream entirely in volatile RAM.
* Natural Language Token Matcher extracts clinical keyword recall without sending data to any cloud server.

#### NER Cultural Stories & Narrative Curricula
* **Story A (Assam)**: *Tejimola* (তেজীমলা) — Narrative elements: stepmother, lotus flower, weaver bird, father merchant returning on Brahmaputra boat.
* **Story B (Manipur)**: *The Legend of Loktak Lake* — Narrative elements: floating phumdis, fisherman, goddess of the lake, Sangai dancing deer.
* **Story C (Bodo)**: *The Sijou Tree & the Orphan* — Narrative elements: elder brother, bath in river, sacred Sijou euphorbia, Kherai dance.

#### Mathematical Scoring Algorithm
Semantic Recall Index $R_{\text{story}}$ is computed across core narrative units:

$$R_{\text{story}} = \left( 0.6 \times \frac{\sum_{k \in K_{\text{core}}} w_{k} \cdot \mathbb{I}(k \in T_{\text{patient}})}{\sum_{k \in K_{\text{core}}} w_{k}} + 0.4 \times \min\left(1.0, \frac{W_{\text{patient}}}{W_{\text{expected}}}\right) \right) \times 100$$

Where:
* $K_{\text{core}}$ = Clinically calibrated set of 10 essential story fact tokens (characters, setting, conflict, resolution).
* $w_{k}$ = Importance weight for token $k$ ($w=2$ for protagonist/climax, $w=1$ for secondary details).
* $\mathbb{I}(k \in T_{\text{patient}})$ = Indicator function: $1$ if token or lemma is present in local transcription $T_{\text{patient}}$, else $0$.
* $W_{\text{patient}}$ = Spoken word count (evaluating verbal fluency).
* $W_{\text{expected}}$ = Expected baseline word count ($30$ words).

---

### 🕹️ Game 3: Picture Naming (ছবি চিনাক্তকৰণ / ꯃꯁꯛ ꯈꯪꯗꯣꯛꯄ)

#### Clinical Target
Semantic retrieval, confrontation naming, and expressive vocabulary. Modeled after the Boston Naming Test (BNT).

#### Mechanics & Interaction Loop
* The screen presents a high-resolution, culturally familiar hand-drawn vector illustration.
* Patient is invited to name the object either by:
  1. **Voice Input**: Speaking the object name (transcribed via IndicConformer ONNX).
  2. **Multi-Choice Tap**: Tapping one of 3 large pictorial/text choices (if speech recognition confidence is low or patient is non-verbal).
* If the patient hesitates for $> 5000\text{ms}$, an automated acoustic phonemic cue is provided (e.g., first syllable *"Ja..."* for *Japi*).

#### NER Cultural Object Curricula
* **Object Tier 1 (High Frequency)**:
  * *Kaziranga Rhino* (গঁড় / ꯂꯥꯏꯔꯣꯏ)
  * *Japi Hat* (জাপি / ꯈꯨꯂꯥꯎ)
  * *Tea Leaves / Garden* (চাহ পাত)
* **Object Tier 2 (Moderate Frequency)**:
  * *Bihu Dhol* (ঢোল)
  * *Muga Silk Cocoon* (মুগা পলু)
  * *Loktak Fisherman Boat* (নাও / ꯍꯤ)
* **Object Tier 3 (Low Frequency / High Difficulty)**:
  * *Pepa Flute* (পেঁপা)
  * *Tangkhul Longpi Black Pot*
  * *Sangai Antlers*

#### Mathematical Scoring Algorithm
Confrontation Accuracy Score $A_{\text{naming}}$:

$$A_{\text{naming}} = \sum_{j=1}^{M} \left( C_{j} \times (1.0 - 0.25 \cdot \text{CueLevel}_{j}) \times \max\left(0.2, 1.0 - \frac{t_{j}}{10000}\right) \right)$$

Where:
* $M$ = Number of stimulus cards presented (default: 10).
* $C_{j}$ = Binary correctness ($1$ for correct identification, $0$ for incorrect).
* $\text{CueLevel}_{j}$ = $0$ if spontaneous naming, $1$ if phonemic audio cue given, $2$ if multi-choice visual hint used.
* $t_{j}$ = Time to response in milliseconds (capped at $10,000\text{ms}$).

---

## 📶 3. OFFLINE BEHAVIOR SPECIFICATION (Clinical Game Engines)

### 1. What happens when this feature runs with zero internet connectivity?
* All 3 games execute with 100% full gameplay fidelity.
* All graphic vectors, audio clips, story scripts, and ASR models reside in the local app package.
* Zero network calls are generated during any game phase.

### 2. What data is stored locally vs. requires cloud?
* **Stored Locally**: All game rounds, per-second tap latencies, ASR transcript text, computed scores ($S_{\text{speed}}, R_{\text{story}}, A_{\text{naming}}$), and session duration.
* **Requires Cloud**: No game features require cloud storage.

### 3. What is the fallback/degradation strategy if cloud is unreachable?
* Complete local persistence. Scores are saved to SQLite and reflected immediately in the caregiver's on-device dashboard.

### 4. How does the user know they're in offline mode? (UI indicators)
* A serene offline status icon: 🟢 **"অফলাইন সক্ৰিয় (Offline Active)"**.
* No nag screens or reconnect warnings disrupt the patient's cognitive focus.

### 5. How does data integrity survive app crashes during offline operation?
* Game progress is saved per-round in volatile Redux state and committed to SQLite at the end of each round.
* If the app closes abruptly mid-session, the completed rounds are preserved, and the session status is recorded as `'ABORTED'` without data corruption.
