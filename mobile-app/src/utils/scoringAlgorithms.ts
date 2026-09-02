/**
 * SmritiSetu Clinical Scoring Algorithms
 * Implementations of ACTIVE UFOV and WMS Logical Memory scoring models.
 */

export interface SpeedMatchRound {
  reactionTimeMs: number;
  isCorrect: boolean;
  difficultyLevel: number;
}

/**
 * Calculates visual processing speed score based on the Ball et al. (2002) ACTIVE trial model.
 * Incorporates an exponential reaction time decay penalty with a motor floor baseline.
 */
export function calculateSpeedMatchScore(
  rounds: SpeedMatchRound[],
  rtFloorMs: number = 350,
  decayTauMs: number = 1200,
  errorPenalty: number = 15
): { sessionScore: number; meanReactionTimeMs: number; accuracy: number } {
  if (rounds.length === 0) {
    return { sessionScore: 0, meanReactionTimeMs: 0, accuracy: 0 };
  }

  let totalScore = 0;
  let totalRt = 0;
  let correctCount = 0;
  let errorCount = 0;

  for (const round of rounds) {
    totalRt += round.reactionTimeMs;
    if (round.isCorrect) {
      correctCount++;
      const effectiveRt = Math.max(0, round.reactionTimeMs - rtFloorMs);
      const difficultyMultiplier = 1.0 + (round.difficultyLevel - 1) * 0.2;
      const roundScore = 100 * Math.exp(-effectiveRt / decayTauMs) * difficultyMultiplier;
      totalScore += roundScore;
    } else {
      errorCount++;
    }
  }

  const meanScore = totalScore / rounds.length;
  const penalizedScore = Math.max(0, meanScore - errorCount * errorPenalty);

  return {
    sessionScore: Math.round(penalizedScore * 10) / 10,
    meanReactionTimeMs: Math.round(totalRt / rounds.length),
    accuracy: Math.round((correctCount / rounds.length) * 100),
  };
}

/**
 * Calculates Story Weaver episodic recall index based on keyword matching and verbal fluency.
 */
export function calculateStoryRecallScore(
  transcribedText: string,
  coreKeywords: Array<{ word: string; weight: number }>,
  expectedWordCount: number = 30
): { recallIndex: number; matchedKeywords: string[]; wordCount: number } {
  const normalizedText = transcribedText.toLowerCase();
  const words = normalizedText.trim().split(/\s+/).filter(Boolean);
  const wordCount = words.length;

  let earnedWeight = 0;
  let totalWeight = 0;
  const matchedKeywords: string[] = [];

  for (const item of coreKeywords) {
    totalWeight += item.weight;
    if (normalizedText.includes(item.word.toLowerCase())) {
      earnedWeight += item.weight;
      matchedKeywords.push(item.word);
    }
  }

  const keywordRatio = totalWeight > 0 ? earnedWeight / totalWeight : 0;
  const fluencyRatio = Math.min(1.0, wordCount / expectedWordCount);

  // 60% semantic content, 40% verbal fluency
  const recallIndex = Math.round((0.6 * keywordRatio + 0.4 * fluencyRatio) * 100);

  return {
    recallIndex,
    matchedKeywords,
    wordCount,
  };
}

/**
 * Calculates Picture Naming confrontation accuracy score based on Boston Naming Test methodology.
 */
export function calculatePictureNamingScore(
  cards: Array<{ isCorrect: boolean; cueLevel: number; latencyMs: number }>
): number {
  if (cards.length === 0) return 0;

  let totalScore = 0;
  for (const card of cards) {
    if (card.isCorrect) {
      const cueDeduction = 1.0 - 0.25 * card.cueLevel;
      const latencyFactor = Math.max(0.2, 1.0 - card.latencyMs / 10000);
      totalScore += cueDeduction * latencyFactor * 10;
    }
  }

  return Math.round((totalScore / (cards.length * 10)) * 100);
}
