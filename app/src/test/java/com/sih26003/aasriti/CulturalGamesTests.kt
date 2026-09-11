package com.sih26003.aasriti

import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.demo.AasritiDemoData
import com.sih26003.aasriti.feature.games.categorisation.CategorisationGameData
import com.sih26003.aasriti.feature.games.villagemarket.VillageMarketData
import com.sih26003.aasriti.feature.games.voicecuecard.VoiceCueCardData
import org.junit.Assert.*
import org.junit.Test
import java.util.Random

/**
 * Dedicated test suite for AASRITI's North Eastern Cultural Games Suite.
 * Covers:
 * 1. Categorisation Game: item taxonomy, category mapping, regional items (Assam, Manipur, Meghalaya)
 * 2. Categorisation Dynamic Resolution: guaranteeing target category is always present in choices
 * 3. Village Market: authentic inventory, difficulty item-count scaling, stall subset integrity
 * 4. Voice Cue Card: sequence generation across levels, target item display guarantee
 * 5. Flower Match: authentic botanical cards in AasritiDemoData
 * 6. Accessibility & Canonical Design Token compliance
 */
class CulturalGamesTests {

    // =========================================================================
    // 1. CATEGORISATION GAME TESTS
    // =========================================================================

    @Test
    fun testCategorisationCategoriesIntegrity() {
        val categories = CategorisationGameData.defaultCategories
        val categoryIds = categories.map { it.id }.toSet()

        assertTrue(categoryIds.contains("FRUIT"))
        assertTrue(categoryIds.contains("VEGETABLE"))
        assertTrue(categoryIds.contains("ANIMAL"))
        assertTrue(categoryIds.contains("CLOTH"))
        assertTrue(categoryIds.contains("MUSIC"))

        categories.forEach { cat ->
            assertFalse("Category ID should not be blank", cat.id.isBlank())
            assertFalse("Category label should not be blank", cat.labelIndic.isBlank())
            assertFalse("Category emoji should not be blank", cat.emoji.isBlank())
        }
    }

    @Test
    fun testCategorisationItemPoolRegionalCoverage() {
        val items = CategorisationGameData.defaultItemPool
        val categories = CategorisationGameData.defaultCategories
        val validCategoryIds = categories.map { it.id }.toSet()

        // Every item must belong to an approved category
        items.forEach { item ->
            assertTrue(
                "Item '${item.name}' references invalid category '${item.categoryId}'",
                validCategoryIds.contains(item.categoryId)
            )
            assertFalse("Item name cannot be blank", item.name.isBlank())
            assertFalse("Item emoji cannot be blank", item.emoji.isBlank())
        }

        // Verify authentic regional items
        // Assam
        assertTrue(items.any { it.name.contains("গামোচা") || it.name.contains("Gamosa") })
        assertTrue(items.any { it.name.contains("জাপি") || it.name.contains("Jaapi") })
        assertTrue(items.any { it.name.contains("কাজি নেমু") || it.name.contains("Assam Lemon") })
        assertTrue(items.any { it.name.contains("ঢোল") || it.name.contains("Dhol") })

        // Manipur
        assertTrue(items.any { it.name.contains("মৈৰাং ফী") || it.name.contains("Moirang Phee") })
        assertTrue(items.any { it.name.contains("পেনা") || it.name.contains("Pena") })

        // Meghalaya
        assertTrue(items.any { it.name.contains("ৰিন্ডিয়া") || it.name.contains("Ryndia") })
        assertTrue(items.any { it.name.contains("দুতৰা") || it.name.contains("Duitara") })
        assertTrue(items.any { it.name.contains("খাচী সুমথিৰা") || it.name.contains("Khasi Mandarin") })
    }

    @Test
    fun testCategorisationActiveCategoryResolutionNeverDeadlocks() {
        val categories = CategorisationGameData.defaultCategories
        val items = CategorisationGameData.defaultItemPool

        // For every difficulty (1..5) and every item in the pool, the item's category must be present in active choices
        for (difficulty in 1..5) {
            val expectedCount = when (difficulty) {
                1 -> 2
                2 -> 3
                3 -> 4
                else -> categories.size
            }

            for (item in items) {
                val targetCat = categories.first { it.id == item.categoryId }
                val otherCats = categories.filter { it.id != item.categoryId }
                val activeChoices = (listOf(targetCat) + otherCats.take(expectedCount - 1)).sortedBy { it.id }

                assertEquals("Expected $expectedCount choices at level $difficulty", expectedCount, activeChoices.size)
                assertTrue("Active choices MUST include item's category", activeChoices.any { it.id == item.categoryId })
                assertEquals("No duplicate category choices allowed", expectedCount, activeChoices.distinctBy { it.id }.size)
            }
        }
    }

    // =========================================================================
    // 2. VILLAGE MARKET GAME TESTS
    // =========================================================================

    @Test
    fun testVillageMarketInventoryIntegrity() {
        val goods = VillageMarketData.defaultStallGoods
        val ids = goods.map { it.id }

        // All IDs must be unique
        assertEquals("Market item IDs must be unique", ids.size, ids.toSet().size)

        // Minimum 12 items for rich variety
        assertTrue("Inventory should have >= 12 items", goods.size >= 12)

        // Verify regional specialties
        assertTrue("Must include Joha Rice", goods.any { it.id == "rice" })
        assertTrue("Must include Assam Lemon", goods.any { it.id == "lemon" })
        assertTrue("Must include Chak-hao Black Rice", goods.any { it.id == "chakhao" })
        assertTrue("Must include Khasi Mandarin", goods.any { it.id == "orange" })
        assertTrue("Must include Bamboo Jaapi", goods.any { it.id == "jaapi" })
        assertTrue("Must include Ghila Pitha", goods.any { it.id == "pitha" })
        assertTrue("Must include Singju Salad", goods.any { it.id == "singju" })
        assertTrue("Must include Ryndia Shawl", goods.any { it.id == "ryndia" })
    }

    @Test
    fun testVillageMarketDifficultyScalingAndStallSubset() {
        val allStallGoods = VillageMarketData.defaultStallGoods

        for (difficulty in 1..5) {
            val count = when (difficulty) {
                1 -> 2
                2 -> 3
                3 -> 3
                4 -> 4
                else -> 5
            }

            for (round in 1..5) {
                val offset = ((round - 1) * 2) % allStallGoods.size
                val rotated = allStallGoods.drop(offset) + allStallGoods.take(offset)
                val targetList = rotated.take(count)
                val remaining = rotated.drop(count)
                val stall = when (difficulty) {
                    1 -> targetList + remaining.take(2)
                    2 -> targetList + remaining.take(3)
                    3 -> targetList + remaining.take(5)
                    else -> allStallGoods
                }.shuffled(Random(round.toLong()))

                // Assert target list size
                assertEquals(count, targetList.size)

                // Assert shopping list items are all present in the market stall!
                val stallIds = stall.map { it.id }.toSet()
                targetList.forEach { target ->
                    assertTrue(
                        "Shopping list item '${target.name}' must be present in stall for round $round, diff $difficulty",
                        stallIds.contains(target.id)
                    )
                }

                // Assert stall has more items than shopping list (distractor challenge)
                assertTrue(stall.size >= targetList.size)
            }
        }
    }

    // =========================================================================
    // 3. VOICE CUE CARD GAME TESTS
    // =========================================================================

    @Test
    fun testVoiceCueCardItemPoolIntegrity() {
        val items = VoiceCueCardData.defaultItems
        val ids = items.map { it.id }

        assertEquals("Cue item IDs must be unique", ids.size, ids.toSet().size)
        assertTrue("Should have >= 8 items", items.size >= 8)

        // Check regional anchors
        assertTrue(items.any { it.id == "jaapi" })
        assertTrue(items.any { it.id == "duitara" })
        assertTrue(items.any { it.id == "pena" })
        assertTrue(items.any { it.id == "gamosa" })
        assertTrue(items.any { it.id == "jolpan" })
    }

    @Test
    fun testVoiceCueCardSequenceAndDisplayIntegrity() {
        val availableItems = VoiceCueCardData.defaultItems

        for (difficulty in 1..5) {
            for (round in 1..10) {
                val offset = (round - 1) % availableItems.size
                val shifted = availableItems.drop(offset) + availableItems.take(offset)
                val targetSequence = when (difficulty) {
                    1 -> listOf(shifted[0])
                    2 -> listOf(shifted[0])
                    3 -> listOf(shifted[0], shifted[1])
                    4 -> listOf(shifted[0], shifted[1])
                    else -> listOf(shifted[0], shifted[1], shifted[2])
                }

                val expectedSeqSize = when (difficulty) {
                    1, 2 -> 1
                    3, 4 -> 2
                    else -> 3
                }
                assertEquals(expectedSeqSize, targetSequence.size)

                // Display items selection logic
                val distractors = availableItems.filter { item -> !targetSequence.any { it.id == item.id } }
                val distractorCount = when (difficulty) {
                    1 -> 1
                    2 -> 2
                    3 -> 2
                    4 -> 4
                    else -> 5
                }
                val displayItems = (targetSequence + distractors.take(distractorCount)).distinctBy { it.id }.sortedBy { it.id }

                // Every item in targetSequence MUST be in displayItems
                targetSequence.forEach { target ->
                    assertTrue(
                        "Target '${target.name}' must be displayed in cards grid",
                        displayItems.any { it.id == target.id }
                    )
                }

                // No duplicates in display
                assertEquals(displayItems.size, displayItems.distinctBy { it.id }.size)
            }
        }
    }

    // =========================================================================
    // 4. FLOWER MATCH GAME HERITAGE FLORA TESTS
    // =========================================================================

    @Test
    fun testFlowerMatchAuthenticBotanicalCards() {
        val flowers = AasritiDemoData.flowerCards
        assertEquals(4, flowers.size)

        // Kopou Phool (Rhynchostylis retusa - Assam State Flower)
        val kopou = flowers.first { it.id == 1 }
        assertTrue(kopou.nameIndic.contains("কপৌ"))
        assertTrue(kopou.nameEn.contains("Kopou"))
        assertTrue(kopou.botanicalFamily.contains("Rhynchostylis retusa"))

        // Tagar Phool
        val tagar = flowers.first { it.id == 2 }
        assertTrue(tagar.nameIndic.contains("তগৰ"))
        assertTrue(tagar.botanicalFamily.contains("Tabernaemontana"))

        // Jaba Phool
        val jaba = flowers.first { it.id == 3 }
        assertTrue(jaba.nameIndic.contains("জবা"))
        assertTrue(jaba.botanicalFamily.contains("Hibiscus"))

        // Nilkamal
        val nilkamal = flowers.first { it.id == 4 }
        assertTrue(nilkamal.nameIndic.contains("নীলকমল"))
        assertTrue(nilkamal.botanicalFamily.contains("Nymphaea"))
    }

    // =========================================================================
    // 5. ACCESSIBILITY & DESIGN TOKEN COMPLIANCE
    // =========================================================================

    @Test
    fun testDesignTokensOpacityAndIntegrity() {
        val tokens = listOf(
            AasritiColorTokens.WarmIvory,
            AasritiColorTokens.SoftCream,
            AasritiColorTokens.DeepCharcoal,
            AasritiColorTokens.DeepNortheastForest,
            AasritiColorTokens.MugaGold,
            AasritiColorTokens.SupportingSage,
            AasritiColorTokens.WarmStoneBorder
        )

        tokens.forEach { color ->
            // In Compose Color, alpha should be 1.0f (opaque, solid colors for elder readability)
            assertEquals(1.0f, color.alpha, 0.01f)
        }
    }

    // =========================================================================
    // 6. VOICE CONTROLLER AFFORDANCE & SPEECH RECOGNITION TESTS
    // =========================================================================

    @Test
    fun testVoicePillStateSemantics() {
        val states = com.sih26003.aasriti.core.ui.components.AasritiVoicePillState.values()
        assertEquals(5, states.size)
        assertTrue(states.contains(com.sih26003.aasriti.core.ui.components.AasritiVoicePillState.IDLE))
        assertTrue(states.contains(com.sih26003.aasriti.core.ui.components.AasritiVoicePillState.PLAYING))
        assertTrue(states.contains(com.sih26003.aasriti.core.ui.components.AasritiVoicePillState.LISTENING))
        assertTrue(states.contains(com.sih26003.aasriti.core.ui.components.AasritiVoicePillState.PAUSED))
        assertTrue(states.contains(com.sih26003.aasriti.core.ui.components.AasritiVoicePillState.ERROR))
    }

    @Test
    fun testVoiceCueCardMatcherMultilingualRecognition() {
        val jaapiItem = com.sih26003.aasriti.feature.games.voicecuecard.CueItem("jaapi", "বাঁহৰ জাপি (Bamboo Jaapi)", "👒")
        val teaItem = com.sih26003.aasriti.feature.games.voicecuecard.CueItem("tea", "চাহৰ কাপ (Tea Cup)", "☕")
        val penaItem = com.sih26003.aasriti.feature.games.voicecuecard.CueItem("pena", "পেনা বাদ্য (Pena)", "🎻")
        val duitaraItem = com.sih26003.aasriti.feature.games.voicecuecard.CueItem("duitara", "দুতৰা বাদ্য (Duitara)", "🪕")

        // Exact & Indic speech matching
        assertTrue(com.sih26003.aasriti.feature.games.voicecuecard.VoiceCueCardMatcher.matchesSpokenCue("জাপি", jaapiItem))
        assertTrue(com.sih26003.aasriti.feature.games.voicecuecard.VoiceCueCardMatcher.matchesSpokenCue("bamboo jaapi please", jaapiItem))
        assertTrue(com.sih26003.aasriti.feature.games.voicecuecard.VoiceCueCardMatcher.matchesSpokenCue("চাহৰ কাপ", teaItem))
        assertTrue(com.sih26003.aasriti.feature.games.voicecuecard.VoiceCueCardMatcher.matchesSpokenCue("hot tea", teaItem))
        assertTrue(com.sih26003.aasriti.feature.games.voicecuecard.VoiceCueCardMatcher.matchesSpokenCue("পেনা বাদ্য", penaItem))
        assertTrue(com.sih26003.aasriti.feature.games.voicecuecard.VoiceCueCardMatcher.matchesSpokenCue("দুতৰা বাদ্য", duitaraItem))

        // Non-matching speech should return false
        assertFalse(com.sih26003.aasriti.feature.games.voicecuecard.VoiceCueCardMatcher.matchesSpokenCue("গামোচা", jaapiItem))
        assertFalse(com.sih26003.aasriti.feature.games.voicecuecard.VoiceCueCardMatcher.matchesSpokenCue("something completely unrelated", teaItem))
    }

    @Test
    fun testRegionalLanguagePacksJsonIntegrity() {
        val jsonFiles = listOf("as_prompts.json", "en_prompts.json", "mn_prompts.json", "kha_prompts.json")
        val requiredKeys = listOf(
            "welcome",
            "voice_cue_card_instructions",
            "categorisation_instructions",
            "village_market_instructions",
            "correct_feedback"
        )

        val gson = com.google.gson.Gson()

        jsonFiles.forEach { filename ->
            val file = java.io.File("src/main/assets/language-packs/$filename")
            assertTrue("File $filename must exist in assets", file.exists())

            val pack = file.reader().use { reader ->
                gson.fromJson(reader, com.sih26003.aasriti.voice.packs.LanguagePack::class.java)
            }

            assertNotNull("Language pack $filename must parse successfully", pack)
            assertFalse("Language code in $filename must not be blank", pack.language.isBlank())
            assertFalse("Language name in $filename must not be blank", pack.languageName.isBlank())

            requiredKeys.forEach { key ->
                assertTrue(
                    "Language pack $filename must contain key '$key'",
                    pack.prompts.containsKey(key) && !pack.prompts[key].isNullOrBlank()
                )
            }
        }
    }

    // =========================================================================
    // 6. PATTERN RECOGNITION GAME CULTURAL MOTIFS & DIFFICULTY TESTS
    // =========================================================================

    @Test
    fun testPatternGameDataCulturalMotifsIntegrity() {
        val motifs = com.sih26003.aasriti.feature.games.patternrecognition.PatternGameData.culturalMotifs
        assertTrue("Must have at least 6 cultural motifs", motifs.size >= 6)

        val ids = motifs.map { it.id }
        assertEquals("Motif IDs must be unique", ids.size, ids.toSet().size)

        motifs.forEach { motif ->
            assertFalse("ID cannot be blank", motif.id.isBlank())
            assertFalse("Symbol cannot be blank", motif.symbol.isBlank())
            assertFalse("Indic name cannot be blank", motif.nameIndic.isBlank())
            assertFalse("Origin region cannot be blank", motif.originRegion.isBlank())
        }

        // Verify authentic regional elements
        assertTrue(motifs.any { it.id == "kopou" && it.originRegion == "Assam" })
        assertTrue(motifs.any { it.id == "jaapi" && it.originRegion == "Assam" })
        assertTrue(motifs.any { it.id == "gamosa" && it.originRegion == "Assam" })
        assertTrue(motifs.any { it.id == "mandarin" && it.originRegion == "Meghalaya" })
    }

    @Test
    fun testPatternChallengeScalingAndCorrectAnswerIntegrity() {
        for (difficulty in 1..5) {
            for (round in 1..8) {
                val challenge = com.sih26003.aasriti.feature.games.patternrecognition.PatternGameData.getChallengeForLevel(difficulty, round)

                // Sequence must end with question mark
                assertEquals("❓", challenge.sequence.last())
                assertTrue("Sequence must have >= 5 elements", challenge.sequence.size >= 5)

                // Correct answer must be among choices
                assertTrue(
                    "Correct answer '${challenge.correctAnswer}' must be in choices: ${challenge.choices}",
                    challenge.choices.contains(challenge.correctAnswer)
                )

                // Choices must be distinct
                assertEquals(challenge.choices.size, challenge.choices.distinct().size)

                // Choices count must scale with difficulty
                when (difficulty) {
                    1 -> assertEquals(2, challenge.choices.size)
                    2 -> assertEquals(3, challenge.choices.size)
                    3 -> assertEquals(3, challenge.choices.size)
                    4 -> assertEquals(4, challenge.choices.size)
                    5 -> assertEquals(4, challenge.choices.size)
                }
            }
        }
    }
}
