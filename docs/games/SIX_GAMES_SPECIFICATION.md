# SmritiSetu (SIH26003) — The Six First-Class Games Specification

Every game inherits from `BaseGameEngine` and implements the strict 5-level adaptive difficulty curve.

---

### Game 1: Family Trivia (👨‍👩‍👧 পৰিয়ালৰ স্মৃতি)
* **Domain**: Memory & Identity Preservation
* **Content Source**: Local patient relationships (`RelationshipEntity`) stored in Room.
* **Levels**:
  * Level 1: Photo + Name + 2 choices.
  * Level 2: Relationship + 3 choices.
  * Level 3: 4 choices.
  * Level 4: Voice prompt clue + photo.
  * Level 5: Direct recall without choice elimination.
* **Non-Punitive Rule**: Incorrect tap plays soothing encouragement (*"একো কথা নাই, আকৌ চেষ্টা কৰক"*), never alarms or docks points.

---

### Game 2: Voice Cue Card (🔊 কণ্ঠ আৰু ছবি)
* **Domain**: Memory + Attention
* **Interaction**: Tap is ALWAYS available. Voice recognition is optional for supported languages.
* **Levels**:
  * Level 1: Identify image from simple voice cue.
  * Level 2: Short spoken instruction.
  * Level 3: Two-step instruction.
  * Level 4: Mixed instruction.
  * Level 5: Hear sequence and tap in order.

---

### Game 3: Daily Activity Sequencing (🫖 দৈনন্দিন ক্ৰম)
* **Domain**: Executive Function & Procedural Memory
* **Culturally Replaceable Scenarios**: Making traditional Assam milk tea, morning routine, market trip.
* **Levels**:
  * Level 1: 2 obvious chronological steps.
  * Level 2: 3 chronological steps.
  * Level 3: 4 steps.
  * Level 4: 5 steps with distractor.
  * Level 5: 5-6 steps with subtle ordering differences.

---

### Game 4: Categorisation (🧺 শ্ৰেণীবিভাজন)
* **Domain**: Attention & Categorical Categorization
* **Interaction**: Large tactile target categories, uncluttered card layout.
* **Levels**:
  * Level 1: 2 distinct groups (Fruits vs Vegetables).
  * Level 2: 3 groups (+ Animals).
  * Level 3: 4 groups (+ Handloom Clothing).
  * Level 4: Subtle distinctions and distractors.
  * Level 5: Timed category switching.

---

### Game 5: Village Market (🛍️ গাঁওৰ বজাৰ)
* **Domain**: Working Memory & Visual Search
* **Locally Authentic Items**: Joha Rice, Assam Kaji Nemu, Bhut Jolokia, Betel leaves, Assam tea, local river fish.
* **Levels**:
  * Level 1: Memorize 2 shopping items.
  * Level 2: Memorize 3 shopping items.
  * Level 3: 4 items + market distractors.
  * Level 4: Larger stall layout.
  * Level 5: 5+ items with delayed recall.

---

### Game 6: Pattern Recognition (🔷 আৰ্হি চিনাক্তকৰণ)
* **Domain**: Visuospatial Processing & Cognitive Speed
* **Visuals**: High contrast geometric shapes and cultural motifs.
* **Levels**:
  * Level 1: 2-item alternating pattern (A-B-A-B-[?]).
  * Level 2: 3-item repeating pattern (A-B-C-A-B-[?]).
  * Level 3: Longer sequence with variable step size.
  * Level 4: Complex pattern with color/shape distractors.
  * Level 5: Abstract sequence requiring rapid evaluation.
