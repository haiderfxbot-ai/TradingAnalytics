package com.tradinganalytics.patterns.library

object PatternLibrary {

    private val trendNames = listOf(
        "BullishFlag", "BearishFlag", "AscendingTriangle", "DescendingTriangle",
        "SymmetricalTriangle", "BullishPennant", "BearishPennant", "FallingWedge",
        "RisingWedge", "BullishChannel", "BearishChannel", "HorizontalChannel",
        "GoldenCross", "DeathCross", "MACDBullishCross", "MACDBearishCross",
        "VWAPBreakout", "VWAPRejection", "MovingAverageRibbon", "BollingerSqueeze",
        "TrendContinuation", "TrendExhaustion", "AccelerationBreakout", "ParabolicRise",
        "TrendPullback", "TrendExtension", "BreakawayGap", "ExhaustionGap",
        "RunawayGap", "CommonGap", "BreakoutRetest", "SupportBounce",
        "ResistanceRejection", "HigherHigh", "HigherLow", "LowerHigh",
        "LowerLow", "SwingHigh", "SwingLow", "ImpulseWave",
        "CorrectiveWave", "TrendAcceleration", "TrendDeceleration", "TrendMomentum",
        "TrendDivergence", "TrendConvergence", "TrendConfirmation", "TrendReversal",
        "TimeWarp", "DriftDetection", "PullbackEntry", "ContinuationEntry",
        "TrendFiltered", "TrendSmoothed", "TrendBreak", "TrendChannel",
        "TrendLineBreak", "TrendLineTouch", "TrendLineBounce", "SwingPoint",
        "MarketStructureHigh", "MarketStructureLow", "OrderBlockBreak", "LiquiditySweep",
        "SweepHigh", "SweepLow", "BreakOfStructure", "ChangeOfCharacter",
        "ImpliedFairValueGap", "BalancedPriceRange", "OptimalTradeEntry", "MitigationBlock",
        "InversionFairValueGap", "VolumeImbalance", "OrderFlowSurge", "DeltaDivergence",
        "CumulativeDeltaSwing", "MarketProfileValueArea", "HighVolumeNode", "LowVolumeNode"
    )

    private val reversalNames = listOf(
        "BullishMorningStar", "BearishEveningStar", "BullishEngulfing", "BearishEngulfing",
        "BullishHarami", "BearishHarami", "PiercingLine", "DarkCloudCover",
        "Hammer", "ShootingStar", "InvertedHammer", "HangingMan",
        "Doji", "LongLeggedDoji", "DragonflyDoji", "GravestoneDoji",
        "ThreeWhiteSoldiers", "ThreeBlackCrows", "TwoCrows", "ThreeInsideUp",
        "ThreeInsideDown", "ThreeOutsideUp", "ThreeOutsideDown", "AdvanceBlock",
        "Deliberation", "StalledPattern", "BeltHoldBullish", "BeltHoldBearish",
        "AbandonedBabyBullish", "AbandonedBabyBearish", "BreakawayBullish", "BreakawayBearish",
        "LadderBottom", "LadderTop", "MatHold", "MatchingLow",
        "MeetingLinesBullish", "MeetingLinesBearish", "MistakeLine", "NecklinePiercing",
        "OnNeckline", "InNeckline", "ThrustingLine", "SeparatingLinesBullish",
        "SeparatingLinesBearish", "UpsideGapTwoCrows", "UpsideTasukiGap", "DownsideTasukiGap",
        "ThreeLineStrikeBullish", "ThreeLineStrikeBearish", "ThreeStarsSouth", "StickSandwich",
        "UniqueThreeRiver", "TriStarBullish", "TriStarBearish", "ConcealedBullish",
        "KickingBullish", "KickingBearish", "BearishAbandonedBaby", "BullishAbandonedBaby",
        "CounterattackBullish", "CounterattackBearish", "DumplingTop", "FrypanBottom",
        "HookReversalBullish", "HookReversalBearish", "KeyReversalDayUp", "KeyReversalDayDown",
        "OutsideReversal", "InsideReversal", "TwoBarReversalUp", "TwoBarReversalDown",
        "OneBarReversalUp", "OneBarReversalDown", "ReversalSpike", "ClimacticVolumeReversal",
        "DivergenceReversal", "HiddenDivergenceReversal", "RegularDivergenceReversal", "ExhaustionReversal"
    )

    private val repeatingSequenceNames = listOf(
        "DoubleBottom", "DoubleTop", "TripleBottom", "TripleTop",
        "HeadAndShoulders", "InverseHeadAndShoulders", "RoundingBottom", "RoundingTop",
        "CupAndHandle", "InverseCupAndHandle", "SaucerBottom", "SaucerTop",
        "WPattern", "MPattern", "FlagRepeating", "PennantRepeating",
        "TriangleRepeating", "WedgeRepeating", "ChannelRepeating", "RectangleRepeating",
        "MeasuredMoveUp", "MeasuredMoveDown", "ThreePushUp", "ThreePushDown",
        "ThreeDriveUp", "ThreeDriveDown", "ABCDPattern", "ABEqualsCDPattern",
        "BullishGartley", "BearishGartley", "BullishButterfly", "BearishButterfly",
        "BullishBat", "BearishBat", "BullishCrab", "BearishCrab",
        "BullishShark", "BearishShark", "BullishCypher", "BearishCypher",
        "ThreeMethodFlagBullish", "ThreeMethodFlagBearish", "RisingThreeMethods", "FallingThreeMethods",
        "UpsideGapThreeMethods", "DownsideGapThreeMethods", "UpsideTasukiThree", "DownsideTasukiThree",
        "SideBySideWhiteLines", "SideBySideBlackLines", "IdenticalThreeCrows", "ConsecutiveDoji",
        "AlternatingPattern", "OscillatingPattern", "BouncingPattern", "ZigzagPattern",
        "TwoLegPullback", "ThreeLegPullback", "ExpandingPattern", "ContractingPattern",
        "MeasuredSwing", "FibonacciRetracement", "FibonacciExtension", "HarmonicPattern",
        "ElliottWaveImpulse", "ElliottWaveCorrective", "WaveADown", "WaveBUp",
        "WaveCDown", "TriangleWaveElliott", "FlatWave", "ZigzagWave",
        "DiagonalWave", "LeadingDiagonal", "EndingDiagonal", "RunningFlat",
        "ExpandingFlat", "ContractingFlat", "DoubleThreeWave", "TripleThreeWave"
    )

    private val frequencyNames = listOf(
        "HighFrequencyBreakout", "LowFrequencyConsolidation", "VolumeSpikeBuy", "VolumeSpikeSell",
        "VolumeClimax", "VolumeDryUp", "TickVolumeSurge", "DeltaVolumeSurge",
        "CumulativeDeltaBreak", "OrderFlowImbalance", "HighFrequencyReversal", "LowFrequencyReversal",
        "FrequentTouchSupport", "FrequentTouchResistance", "HighVolumeNodeTest", "LowVolumeNodeTest",
        "PointOfControlBreak", "ValueAreaHighBreak", "ValueAreaLowBreak", "VolumeProfileGap",
        "BidAskImbalance", "SpreadCompression", "SpreadExpansion", "LiquidityCrunch",
        "DarkPoolVolume", "BlockTradeDetection", "IcebergOrderDetection", "AliasOrderDetection",
        "HighFrequencyOscillation", "LowFrequencyDrift", "FrequencyDivergence", "CycleHigh",
        "CycleLow", "CycleMidpoint", "DominantCyclePhase", "CycleTurningPoint",
        "ShortCycleBreakout", "MediumCycleBreakout", "LongCycleBreakout", "CycleCompression",
        "CycleExpansion", "FrequencyHarmonic", "SubHarmonicPattern", "OverHarmonicPattern",
        "FundamentalFrequency", "FrequencyModulation", "AmplitudeModulation", "PhaseShiftDetection",
        "PeriodicPattern", "AperiodicPattern", "QuasiPeriodicPattern", "FrequencyCluster",
        "BandwidthBreakout", "CenterFrequencyCross", "NyquistBreakout", "SamplingPattern",
        "HighPassFiltered", "LowPassFiltered", "BandPassFiltered", "FrequencyDomainCross",
        "SpectralDensityPeak", "SpectralDensityValley", "SignalToNoiseBreak", "NoiseFloorBreak",
        "HarmonicOscillator", "CycleSynchronization", "CycleDesynchronization", "EntropyDropDetection",
        "EntropyRiseDetection", "InformationFlowSurge"
    )

    private val timingNames = listOf(
        "OpeningRangeBreakout", "OpeningRangeReversal", "MorningSessionSurge", "AfternoonSessionDrift",
        "ClosingRangeBreakout", "ClosingRangeReversal", "PreMarketGap", "PostMarketDrift",
        "HourlyPivotHigh", "HourlyPivotLow", "SessionHighTest", "SessionLowTest",
        "MidnightReversal", "EarlyMorningDip", "LateMorningRally", "LunchTimeLull",
        "EarlyAfternoonContinuation", "LateAfternoonReversal", "PowerHourSurge", "PowerHourReversal",
        "FirstHourRange", "LastHourRange", "WeeklyOpenTest", "WeeklyCloseTest",
        "MonthlyOpenTest", "MonthlyCloseTest", "QuarterlyRotation", "ExpirationWeekEffect",
        "DayOfWeekPattern", "MonthEndPattern", "SeasonalPatternDetection", "HolidayEffectPattern",
        "TimeBasedSupport", "TimeBasedResistance", "SessionBasedTrend", "IntradayCycle",
        "TimeDecayPattern", "TimeCompression", "TimeExpansion", "TemporalCluster",
        "BarCountReversal", "TimeStopRun", "OpeningDrive", "ClosingDrive",
        "MidSessionPivot", "SessionVolumeProfile", "TimeWeightedAverage", "PeriodBasedBreakout",
        "TemporalDivergence", "ChronologicalDisplacement", "SessionContinuation", "SessionReversal",
        "TimeFilteredTrend", "SessionBiasDetection", "FirstHourBias", "LastHourBias",
        "WeekdaySeasonality", "MonthlySeasonality", "QuarterlySeasonality", "AnnualSeasonality"
    )

    private val probabilityNames = listOf(
        "HighProbabilitySetup", "LowProbabilitySetup", "MeanReversionEntry", "MomentumContinuationEntry",
        "StatisticalArbitrage", "PairsTradeSignal", "RegressionToMean", "BollingerMeanReversion",
        "StandardDeviationBreakout", "ZScoreExtreme", "ProbabilityZoneHigh", "ProbabilityZoneLow",
        "ConfidenceIntervalBreak", "NormalDistributionTest", "ChiSquaredSignal", "BayesianUpdatePattern",
        "MonteCarloSimCross", "ExpectedValuePositive", "ExpectedValueNegative", "RiskRewardOptimal",
        "ProbabilityDensityPeak", "ProbabilityDensityValley", "CumulativeProbabilityBreak", "PercentileExtreme",
        "QuantileShift", "DecileBreakout", "NormalizedMomentum", "ProbabilityWeightedTrend",
        "StochasticPivot", "RandomWalkTest", "MarkovChainState", "HiddenMarkovDetection",
        "MaximumLikelihoodTrend", "MaximumLikelihoodReversal", "AkaikeCriterionBest", "BayesianCriterionBest",
        "PosteriorProbabilityUp", "PosteriorProbabilityDown", "PriorProbabilityShift", "LikelihoodRatioTest",
        "OddsRatioTrend", "LogOddsReversal", "EntropyBasedSignal", "MutualInformationPeak",
        "ConditionalProbabilityHigh", "ConditionalProbabilityLow", "JointProbabilityBreak", "MarginalProbabilityShift",
        "ExpectedReturnPositive", "ExpectedReturnNegative"
    )

    private val momentumNames = listOf(
        "RSIDivergenceBullish", "RSIDivergenceBearish", "HiddenBullishDivergence", "HiddenBearishDivergence",
        "RSIOverbought", "RSIOversold", "RSISwingRejection", "RSITrendlineBreak",
        "StochasticCrossUp", "StochasticCrossDown", "StochasticOverbought", "StochasticOversold",
        "StochasticDivergence", "WilliamsRExtreme", "WilliamsRDivergence", "WilliamsRTurn",
        "MomentumAcceleration", "MomentumDivergence", "MomentumExhaustion", "MomentumContinuation",
        "RateOfChangeBreakout", "RateOfChangeDivergence", "RateOfChangeAcceleration", "RateOfChangeDeceleration",
        "CCIBreakout", "CCIDivergence", "CCITrendChange", "CCIPullback",
        "UltimateOscillatorSignal", "UltimateOscillatorDivergence", "MoneyFlowPositive", "MoneyFlowNegative",
        "ChaikinMoneyFlowSurge", "ChaikinMoneyFlowDivergence", "ADXStrengthening", "ADXWeakening",
        "ADXCrossOver", "ADXCrossUnder", "DirectionalMovementPlus", "DirectionalMovementMinus"
    )

    private val statisticalNames = listOf(
        "StandardDeviationChannelBreak", "StandardDeviationChannelBounce", "RegressionChannelBreak", "RegressionChannelBounce",
        "CorrelationShiftBullish", "CorrelationShiftBearish", "CovarianceSurge", "AutocorrelationPeak",
        "PartialAutocorrelationSignal", "CrossCorrelationLead", "CrossCorrelationLag", "CorrelationDivergence",
        "BetaSurgeBullish", "BetaSurgeBearish", "AlphaGenerationSignal", "AlphaDecayPattern",
        "AlphaDivergence", "BetaDivergence", "VolatilitySmileBreak", "VolatilitySkewShift",
        "VolatilityTermStructure", "ForwardVolatilityBreak", "ImpliedVolatilitySurge", "HistoricalVolatilityBreak",
        "VarianceRatioTest", "DickeyFullerStationary", "DickeyFullerNonStationary", "KruskalWallisSignal",
        "MannWhitneyBreak", "WilcoxonSignal", "KolmogorovSmirnovDivergence", "GrangerCausalityDetection",
        "CointegrationBreak", "CointegrationRecovery", "KalmanFilterBreak", "KalmanFilterReversal",
        "ParticleFilterSignal", "HurstExponentTrending", "HurstExponentMeanReverting", "FractalDimensionBreak"
    )

    private val categoryRanges = listOf(
        "Trend Patterns" to (1..80),
        "Reversal Patterns" to (81..160),
        "Repeating Sequences" to (161..240),
        "Frequency Patterns" to (241..310),
        "Timing Patterns" to (311..370),
        "Probability Patterns" to (371..420),
        "Momentum Patterns" to (421..460),
        "Statistical Patterns" to (461..500)
    )

    private val categoryNameMap = mapOf(
        "Trend Patterns" to trendNames,
        "Reversal Patterns" to reversalNames,
        "Repeating Sequences" to repeatingSequenceNames,
        "Frequency Patterns" to frequencyNames,
        "Timing Patterns" to timingNames,
        "Probability Patterns" to probabilityNames,
        "Momentum Patterns" to momentumNames,
        "Statistical Patterns" to statisticalNames
    )

    private val trendDescriptions = listOf(
        "Detects momentum continuation after a brief consolidation period with above-average volume confirmation",
        "Identifies a temporary pause in a downtrend before further downward movement, confirmed by decreasing volume",
        "Captures a consolidation phase within an uptrend characterized by converging higher lows and flat highs",
        "Detects a consolidation within a downtrend with converging lower highs and flat lows",
        "Identifies converging trendlines with diminishing volatility preceding a directional breakout",
        "Captures a brief consolidation after a sharp upward move, forming a small symmetrical triangle",
        "Detects a brief pause after a sharp decline, forming a small pennant before continuation",
        "Identifies a declining channel with decreasing downward momentum, signaling potential trend reversal",
        "Captures an ascending channel with increasing upward momentum, indicating bullish continuation",
        "Detects parallel upward channel movement with consistent higher highs and higher lows",
        "Identifies parallel downward channel movement with consistent lower highs and lower lows",
        "Captures sideways price action between parallel support and resistance levels",
        "Detects the bullish crossover of a short-term moving average above a long-term moving average",
        "Identifies the bearish crossover of a short-term moving average below a long-term moving average",
        "Captures the MACD line crossing above the signal line with increasing histogram momentum",
        "Detects the MACD line crossing below the signal line with decreasing histogram momentum",
        "Identifies price breaking above the Volume Weighted Average Price with above-average volume",
        "Detects price rejecting the VWAP level and reversing lower on increased selling pressure",
        "Captures multiple moving averages aligning in bullish order with increasing separation",
        "Identifies Bollinger Band contraction indicating low volatility and potential expansion breakout",
        "Detects continuation of existing trend after a brief pause with confirming momentum",
        "Identifies waning momentum in the current trend direction suggesting potential reversal",
        "Captures a sharp acceleration in price movement with increasing velocity and volume",
        "Detects a parabolic price rise with exponential acceleration and climactic volume characteristics",
        "Identifies a temporary counter-trend move that respects the original trend structure",
        "Captures extension of the primary trend beyond recent swing highs or lows with momentum",
        "Detects a price gap in the direction of the trend with above-average volume support",
        "Identifies a gap near the end of a trend signaling potential trend exhaustion",
        "Captures a gap during a strong trend that confirms the prevailing direction",
        "Detects a gap that does not represent any significant pattern or trend initiation",
        "Identifies price returning to test a breakout level before continuing in the breakout direction",
        "Captures price bouncing off a known support level with rejection wicks and volume confirmation",
        "Detects price rejecting a known resistance level with selling pressure and bearish candlestick patterns",
        "Identifies a swing high that exceeds the previous swing high, confirming uptrend structure",
        "Captures a swing low that forms above the previous swing low, maintaining uptrend structure",
        "Detects a swing high that forms below the previous swing high, confirming downtrend structure",
        "Identifies a swing low that breaks below the previous swing low, maintaining downtrend structure",
        "Captures a significant local maximum with surrounding lower highs on both sides",
        "Detects a significant local minimum with surrounding higher lows on both sides",
        "Identifies a strong directional move with five distinct sub-waves in the direction of the larger trend",
        "Captures a three-wave counter-trend move that retraces part of the preceding impulse wave",
        "Detects increasing velocity in trend movement measured by rate of change acceleration",
        "Identifies decreasing velocity in trend movement measured by rate of change deceleration",
        "Captures sustained directional momentum confirmed by multiple momentum oscillators",
        "Detects price moving opposite to oscillator direction indicating potential trend weakness",
        "Identifies price and oscillator moving in the same direction confirming trend strength",
        "Captures multiple technical indicators aligning to confirm the current trend direction",
        "Detects technical conditions suggesting the current trend is losing strength and may reverse",
        "Identifies price deviations from expected temporal patterns suggesting anomalous behavior",
        "Captures gradual directional drift confirmed by moving average slope and regression analysis",
        "Detects optimal entry points during counter-trend pullbacks within a larger trend structure",
        "Identifies confirmation signals that the prevailing trend is resuming after a pause",
        "Captures trend direction filtered through multiple timeframe analysis for higher accuracy",
        "Detects smoothed trend direction using advanced filtering techniques to reduce noise",
        "Identifies price breaking through a significant trendline with volume confirmation",
        "Captures price movement within established channel boundaries with clear trend context",
        "Detects a decisive break of a significant trendline with follow-through confirmation",
        "Identifies price touching a trendline with a reaction that confirms the trendline validity",
        "Captures price bouncing off a trendline with momentum in the direction of the trend",
        "Detects significant turning points in price action at the swing level",
        "Identifies breaks of previous swing highs indicating bullish market structure continuation",
        "Captures breaks of previous swing lows indicating bearish market structure continuation",
        "Detects price breaking through an order block with momentum and volume confirmation",
        "Identifies sweeps of liquidity above recent highs with subsequent reversal or continuation",
        "Captures liquidity sweeps of recent lows with rejection and potential reversal",
        "Detects breaks in market structure where price moves beyond previous swing points",
        "Identifies a change in market character from bullish to bearish or vice versa",
        "Detects imbalance in price delivery suggesting unfilled orders remain in the market",
        "Identifies a balanced price range where equilibrium between buyers and sellers exists",
        "Captures the most efficient entry point for a trade based on market structure analysis",
        "Detects price returning to mitigate a previously established order block or fair value gap",
        "Identifies inversion of a previously bullish fair value gap now acting as resistance",
        "Captures significant volume imbalance between buy and sell orders at a price level",
        "Detects a surge in order flow volume suggesting institutional participation",
        "Identifies divergence between cumulative delta and price suggesting hidden buying or selling",
        "Captures swings in cumulative delta that indicate shifts in aggressive order flow",
        "Detects high volume nodes in market profile indicating significant price acceptance levels",
        "Captures low volume nodes indicating potential price gaps or rapid movement zones"
    )

    private val reversalDescriptions = listOf(
        "A three-candle bullish reversal pattern with a long bearish, small doji, and long bullish candle",
        "A three-candle bearish reversal pattern with a long bullish, small doji, and long bearish candle",
        "A two-candle bullish reversal where a bearish candle is engulfed by a larger bullish candle",
        "A two-candle bearish reversal where a bullish candle is engulfed by a larger bearish candle",
        "A two-candle bullish reversal with a long bearish followed by a smaller bullish inside the previous range",
        "A two-candle bearish reversal with a long bullish followed by a smaller bearish inside the previous range",
        "A two-candle bullish reversal where a bearish candle is followed by a bullish candle closing above its midpoint",
        "A two-candle bearish reversal where a bullish candle is followed by a bearish candle closing below its midpoint",
        "A single-candle bullish reversal with a small body and long lower wick at the bottom of a downtrend",
        "A single-candle bearish reversal with a small body and long upper wick at the top of an uptrend",
        "A single-candle bullish reversal similar to a hammer but occurring after a significant downtrend",
        "A single-candle bearish reversal similar to a shooting star but with a lower confirmation requirement",
        "A single-candle indecision pattern with a very small body indicating potential trend reversal",
        "A doji with exceptionally long upper and lower wicks indicating extreme indecision",
        "A doji with a long lower wick and no upper wick suggesting bullish reversal potential",
        "A doji with a long upper wick and no lower wick suggesting bearish reversal potential",
        "Three consecutive long bullish candles closing at or near their highs signaling strong buying pressure",
        "Three consecutive long bearish candles closing at or near their lows signaling strong selling pressure",
        "A bearish reversal pattern with two bullish candles followed by a bearish candle",
        "A bullish reversal pattern with a long bearish, a smaller bullish inside and a bullish closing above the first",
        "A bearish reversal pattern with a long bullish, a smaller bearish inside and a bearish closing below the first",
        "A bullish reversal pattern where the third candle closes above the second candle's high",
        "A bearish reversal pattern where the third candle closes below the second candle's low",
        "Three bullish candles with each closing at progressively higher levels but with decreasing bodies",
        "Three bullish candles with the last two showing gaps up but weakening momentum",
        "Three-candle pattern where the last candle stalls after a strong uptrend suggesting exhaustion",
        "A single bullish candle with a long body and no upper wick opening at the low and closing at the high",
        "A single bearish candle with a long body and no lower wick opening at the high and closing at the low",
        "A rare bullish reversal with a gap down, a doji, and a gap up forming an island bottom",
        "A rare bearish reversal with a gap up, a doji, and a gap down forming an island top",
        "A five-candle bullish reversal with gaps on both sides of the middle candles",
        "A five-candle bearish reversal with gaps on both sides of the middle candles",
        "A five-candle bullish reversal pattern at market bottoms with lower shadows lengthening",
        "A five-candle bearish reversal pattern at market tops with upper shadows lengthening",
        "A five-candle bullish continuation pattern with a long bullish followed by three small bearish and another bullish",
        "A bullish reversal where two bearish candles have matching lows suggesting support",
        "Two bullish candles that meet at the same closing price suggesting support",
        "Two bearish candles that meet at the same closing price suggesting resistance",
        "A single candle mistaken as a continuation but actually signaling reversal",
        "A bullish reversal where price pierces the previous candle's body but closes within it",
        "A bearish continuation where the close is on the previous candle's low",
        "A bearish continuation where price closes within the neck of the previous candle",
        "A bullish reversal where price thrusts into the previous bearish candle's body",
        "Two bullish candles separated by a gap suggesting continuation of upward momentum",
        "Two bearish candles separated by a gap suggesting continuation of downward momentum",
        "A bearish reversal where a gap up is followed by two bearish candles",
        "A bullish continuation with a gap up between two bullish candles",
        "A bearish continuation with a gap down between two bearish candles",
        "Three bullish candles with the middle offset to the upside in a gap pattern",
        "Three bearish candles with the middle offset to the downside in a gap pattern",
        "A bullish reversal where three consecutive candles are all white with specific gap characteristics",
        "A bearish reversal where three consecutive candles are all black with specific gap characteristics",
        "A rare three-line bearish reversal pattern at market bottoms",
        "A bullish reversal where a black candle is sandwiched between two white candles",
        "A rare bullish reversal with three specific candles forming a river-like pattern",
        "A rare doji-based reversal pattern with three dojis at a turning point",
        "A rare doji-based bearish reversal pattern with three dojis at a turning point",
        "A concealed bullish reversal where bearish activity hides underlying accumulation",
        "A two-candle bullish reversal where a bullish candle opens below the previous close and kicks higher",
        "A two-candle bearish reversal where a bearish candle opens above the previous close and kicks lower",
        "A bearish reversal where a doji gaps up after a long white candle before turning bearish",
        "A bullish reversal where a doji gaps down after a long black candle before turning bullish",
        "A two-candle bullish reversal where both close at the same level after a downtrend",
        "A two-candle bearish reversal where both close at the same level after an uptrend",
        "A gradual top formation with a rounded shape suggesting accumulation followed by distribution",
        "A gradual bottom formation with a rounded shape suggesting distribution followed by accumulation",
        "A single-candle pattern that hooks in the opposite direction of the prevailing trend",
        "A single-candle bearish hook pattern at the top of an uptrend",
        "A single-day reversal that establishes a new high then closes near the low with high volume",
        "A single-day reversal that establishes a new low then closes near the high with high volume",
        "A two-candle pattern where the second candle completely engulfs the first in the opposite direction",
        "A two-candle pattern where the second candle forms entirely within the first candle's range",
        "A two-candle bullish reversal where the second candle reverses the first candle's direction",
        "A two-candle bearish reversal where the second candle reverses the first candle's direction",
        "A single-candle bullish reversal where the candle reverses mid-session and closes strong",
        "A single-candle bearish reversal where the candle reverses mid-session and closes weak",
        "A sharp price spike that immediately reverses indicating exhaustion of buying or selling pressure",
        "A reversal accompanied by extremely high volume often marking a significant turning point",
        "A reversal confirmed by divergence between price and oscillator readings",
        "A reversal detected through hidden divergence not immediately visible on standard oscillators",
        "A reversal confirmed by regular divergence on multiple timeframe analysis",
        "A reversal pattern that occurs after an extended move with declining momentum",
        "A double-bottom-like reversal pattern forming a W shape at market lows",
        "A double-top-like reversal pattern forming an M shape at market highs"
    )

    private val repeatingSequenceDescriptions = listOf(
        "A bullish reversal pattern with two consecutive lows at approximately the same price level",
        "A bearish reversal pattern with two consecutive highs at approximately the same price level",
        "A bullish reversal pattern with three consecutive lows at approximately the same price level",
        "A bearish reversal pattern with three consecutive highs at approximately the same price level",
        "A bearish reversal pattern with three peaks where the middle peak is the highest",
        "A bullish reversal pattern with three troughs where the middle trough is the lowest",
        "A long-term bullish reversal pattern with a gradual rounded bottom formation",
        "A long-term bearish reversal pattern with a gradual rounded top formation",
        "A bullish continuation pattern with a rounded bottom followed by a handle consolidation",
        "A bearish continuation pattern with a rounded top followed by a handle consolidation",
        "A gradual bullish bottoming pattern resembling a saucer shape with shallow curvature",
        "A gradual bearish topping pattern resembling a saucer shape with shallow curvature",
        "A bullish pattern forming two consecutive pullbacks creating a W-shaped structure",
        "A bearish pattern forming two consecutive rallies creating an M-shaped structure",
        "A repeating flag pattern where each flag is followed by another leg in trend direction",
        "A repeating pennant pattern where each pennant precedes a continuation move",
        "A repeating triangle pattern forming within the context of a larger trend",
        "A repeating wedge pattern indicating gradual loss of momentum with each oscillation",
        "A repeating channel pattern with consistent boundaries and periodic touches",
        "A repeating rectangle pattern with clear horizontal support and resistance levels",
        "A pattern where price moves an approximately equal distance after a consolidation period",
        "A pattern where price declines an approximately equal distance after a consolidation period",
        "Three consecutive upward pushes with each push showing declining momentum",
        "Three consecutive downward pushes with each push showing declining momentum",
        "A harmonic pattern with three drives in the direction of the trend",
        "A harmonic pattern with three drives against the direction of the trend",
        "A classic ABCD harmonic pattern with equal AB and CD legs",
        "An ABCD pattern where the AB and CD legs are precisely equal in price distance",
        "A bullish Gartley pattern with specific Fibonacci retracement ratios",
        "A bearish Gartley pattern with specific Fibonacci retracement ratios",
        "A bullish Butterfly pattern with extreme Fibonacci extension ratios",
        "A bearish Butterfly pattern with extreme Fibonacci extension ratios",
        "A bullish Bat pattern with precise 0.886 retracement of the XA leg",
        "A bearish Bat pattern with precise 0.886 retracement of the XA leg",
        "A bullish Crab pattern with extreme 1.618 extension of the XA leg",
        "A bearish Crab pattern with extreme 1.618 extension of the XA leg",
        "A bullish Shark pattern with specific Fibonacci relationships between all legs",
        "A bearish Shark pattern with specific Fibonacci relationships between all legs",
        "A bullish Cypher pattern with precise 0.382 and 1.13 Fibonacci projections",
        "A bearish Cypher pattern with precise 0.382 and 1.13 Fibonacci projections",
        "A three-method bullish flag where three counter-trend candles form within a flag",
        "A three-method bearish flag where three counter-trend candles form within a flag",
        "A bullish continuation with three small bearish candles between two long bullish candles",
        "A bearish continuation with three small bullish candles between two long bearish candles",
        "A bullish continuation with a gap between two groups of three candles",
        "A bearish continuation with a gap between two groups of three candles",
        "A bullish continuation with a specific tasuki gap pattern between three candles",
        "A bearish continuation with a specific tasuki gap pattern between three candles",
        "Two side-by-side white candles at the same price level suggesting resistance absorption",
        "Two side-by-side black candles at the same price level suggesting support absorption",
        "Three consecutive black candles with progressively smaller bodies indicating selling exhaustion",
        "Multiple consecutive doji candles indicating prolonged indecision at a significant level",
        "An alternating pattern of up and down candles with decreasing range",
        "An oscillating pattern of higher highs and lower lows creating a broadening formation",
        "A bouncing pattern between support and resistance levels with diminishing amplitude",
        "A zigzag pattern of alternating directional moves with connecting retracements",
        "A two-wave pullback against the trend with a specific Fibonacci relationship",
        "A three-wave pullback against the trend with a complex corrective structure",
        "An expanding pattern with increasing range between alternating swings",
        "A contracting pattern with decreasing range between alternating swings",
        "A measured swing pattern with equal legs in both time and price",
        "A retracement to a specific Fibonacci level followed by resumption of the trend",
        "An extension beyond the initial move measured by Fibonacci ratios",
        "A price pattern that aligns with specific Fibonacci ratio relationships",
        "A five-wave impulse pattern in the direction of the larger trend",
        "A three-wave corrective pattern moving against the larger trend direction",
        "The first wave down of a corrective ABC pattern in an uptrend",
        "The second wave up of a corrective ABC pattern in an uptrend",
        "The final wave down of a corrective ABC pattern in an uptrend",
        "An Elliott wave triangle pattern with converging boundary lines",
        "A flat correction pattern with a specific ABC structure",
        "A sharp zigzag correction pattern with a 5-3-5 wave structure",
        "A diagonal wave pattern with overlapping sub-waves and converging boundaries",
        "A leading diagonal pattern typically occurring in wave 1 or A",
        "An ending diagonal pattern typically occurring in wave 5 or C",
        "An irregular flat correction where wave B exceeds the start of wave A",
        "An expanding flat where each wave extends beyond the previous wave's range",
        "A contracting flat where each wave is smaller than the previous wave's range",
        "A double three correction combining two three-wave corrective patterns",
        "A triple three correction combining three three-wave corrective patterns",
        "An irregular correction with non-standard wave relationships",
        "A running triangle continuation pattern within a strong trend"
    )

    private val frequencyDescriptions = listOf(
        "A breakout accompanied by exceptionally high tick frequency indicating aggressive buying",
        "A consolidation period with declining tick frequency suggesting diminishing interest",
        "A sudden spike in buying volume with high frequency trades confirming accumulation",
        "A sudden spike in selling volume with high frequency trades confirming distribution",
        "A volume climax where the highest volume bar signals potential trend exhaustion",
        "A volume dry-up where declining volume suggests a pending explosive move",
        "A surge in tick volume that precedes or confirms a directional price move",
        "A surge in delta volume indicating aggressive market order flow in one direction",
        "A break in cumulative delta trend that confirms a change in order flow dynamics",
        "An extreme imbalance between buy and sell orders suggesting institutional activity",
        "A rapid price reversal on high frequency trading activity with volume confirmation",
        "A slow price reversal characterized by low frequency but persistent directional movement",
        "Price repeatedly testing a support level with high frequency indicating strong defense",
        "Price repeatedly testing a resistance level with high frequency indicating strong selling interest",
        "Price testing a previously established high volume node with rejection signals",
        "Price testing a previously established low volume node with rapid movement through",
        "Price breaking through the point of control with volume confirmation",
        "Price breaking above the value area high with acceptance and follow-through",
        "Price breaking below the value area low with acceptance and follow-through",
        "A gap in the volume profile indicating a price level with minimal trading activity",
        "A significant imbalance between bid and ask volumes at a specific price level",
        "A compression of the bid-ask spread suggesting decreased liquidity or pending volatility",
        "A widening of the bid-ask spread suggesting increased uncertainty or volatility",
        "A sharp reduction in available liquidity suggesting market maker withdrawal",
        "Detection of dark pool trading activity through volume pattern analysis",
        "Detection of a large block trade executed at a significant price level",
        "Detection of iceberg order activity with repeated small fills at the same price",
        "Detection of alias order placement strategies through order flow pattern analysis",
        "Rapid price oscillation at high frequency indicating algorithmic trading activity",
        "Slow directional drift at low frequency suggesting institutional accumulation or distribution",
        "Divergence between price frequency and volume frequency indicating potential reversal",
        "The peak of a detected price cycle suggesting imminent cycle reversal",
        "The trough of a detected price cycle suggesting imminent cycle reversal",
        "The midpoint of a detected cycle with potential for continuation",
        "Identification of the dominant cycle phase and position within the cycle",
        "A turning point identified through cycle analysis with multiple confirming indicators",
        "A breakout aligned with the short-term cycle phase and direction",
        "A breakout aligned with the medium-term cycle phase and direction",
        "A breakout aligned with the long-term cycle phase and direction",
        "Compression of multiple cycle components suggesting an explosive expansion move",
        "Expansion of cycle amplitude suggesting increased volatility and directional conviction",
        "A harmonic relationship between multiple detected cycles in the same instrument",
        "A sub-harmonic cycle pattern that aligns with the dominant cycle",
        "An over-harmonic cycle pattern that aligns with the dominant cycle",
        "Detection of the fundamental frequency component in the price signal",
        "Frequency modulation in the price signal suggesting changing market dynamics",
        "Amplitude modulation detected in price oscillations indicating momentum shifts",
        "Detection of a phase shift in the dominant price cycle",
        "A periodically repeating pattern with consistent frequency characteristics",
        "An irregular non-periodic pattern requiring adaptive detection algorithms",
        "A quasi-periodic pattern with varying frequency but identifiable structure",
        "A cluster of multiple frequency components at a significant price level",
        "A breakout from a well-defined bandwidth channel with momentum confirmation",
        "Price crossing the center frequency of a detected oscillation band",
        "A breakout beyond the Nyquist frequency boundary suggesting trend acceleration",
        "A pattern detected through sampling theory analysis of price movements",
        "A signal passing through a high-pass filter indicating short-term momentum",
        "A signal passing through a low-pass filter indicating long-term trend",
        "A signal within a band-pass filter range indicating medium-term cycle activity",
        "Crossing of multiple components in the frequency domain analysis",
        "A peak in spectral density indicating a dominant frequency component",
        "A valley in spectral density indicating absence of activity at a frequency",
        "Improvement in the signal-to-noise ratio suggesting higher quality directional signal",
        "A break below the noise floor suggesting potential trend initiation",
        "An oscillator tuned to harmonic frequencies of the dominant cycle",
        "Synchronization of multiple cycle components confirming trend direction",
        "Desynchronization of cycle components suggesting potential trend change",
        "A drop in market entropy suggesting increasing structure and potential trend",
        "A rise in market entropy suggesting increasing randomness and potential reversal",
        "A surge in information flow as measured by price change frequency",
        "A shift in kurtosis indicating changing tail risk in the distribution",
        "A shift in skewness indicating changing directional bias in returns"
    )

    private val timingDescriptions = listOf(
        "A breakout beyond the opening range high with momentum and volume confirmation",
        "A reversal from the opening range boundaries back into the opening range",
        "A surge in buying activity during the morning session with above-average volume",
        "A gradual drift in price during the afternoon session with declining momentum",
        "A breakout beyond the previous session's closing range as new session begins",
        "A reversal from the closing range back into the previous range",
        "A gap in pre-market trading that persists into the regular session",
        "A price drift after the regular session close suggesting after-hours activity",
        "An hourly pivot high formed with specific candlestick rejection patterns",
        "An hourly pivot low formed with specific candlestick rejection patterns",
        "Price testing the current session's high with potential rejection or breakout",
        "Price testing the current session's low with potential rejection or breakout",
        "A reversal pattern occurring around midnight suggesting overnight positioning changes",
        "A dip during the early morning session that reverses with buying pressure",
        "A rally during the late morning session suggesting momentum accumulation",
        "A lull in activity during lunch hours with reduced volatility and volume",
        "A continuation of the morning trend during early afternoon hours",
        "A reversal during the late afternoon session as traders adjust positions",
        "A surge in activity during the final hour of trading with high volume",
        "A reversal during the final hour suggesting position squaring before close",
        "Price movement within the first hour's trading range setting the session tone",
        "Price movement within the last hour's trading range indicating closing bias",
        "Price testing the weekly open level with potential for weekly trend continuation",
        "Price testing the weekly close level with potential for weekly reversal",
        "Price testing the monthly open level with potential for monthly trend",
        "Price testing the monthly close level with potential for monthly reversal",
        "A quarterly rotation pattern as institutional portfolios are rebalanced",
        "A pattern observed during options expiration week with increased volatility",
        "A recurring pattern based on the specific day of the week",
        "A recurring pattern observed at month-end with institutional activity",
        "A seasonal pattern detected through multi-year historical analysis",
        "A pattern that emerges around market holidays with lower participation",
        "A support level that strengthens or weakens based on time of day or session",
        "A resistance level that strengthens or weakens based on time of day or session",
        "A trend that is more reliable during specific trading sessions",
        "An intraday cycle pattern with predictable timing of highs and lows",
        "A pattern where the probability of continuation decreases with time",
        "Compression of price action into shorter timeframes suggesting pending expansion",
        "Expansion of price action into longer timeframes suggesting trend maturity",
        "A temporal cluster of significant price levels within a specific time window",
        "A reversal based on the number of bars since the last swing point",
        "A stop run pattern where stops are triggered before the trend resumes",
        "Price driving directionally from the opening bell with sustained momentum",
        "Price driving directionally into the closing bell with sustained momentum",
        "A pivot point calculated from the mid-session range with predictive value",
        "Volume profile analysis of the current session indicating value area acceptance",
        "Calculation of time-weighted average price as a dynamic support or resistance",
        "A breakout based on a specific period or time cycle completion",
        "Divergence between price and expected temporal pattern suggesting reversal",
        "Displacement of price from its expected chronological position in the cycle",
        "Continuation of a trend within the same trading session",
        "Reversal of a trend within the same trading session",
        "A trend filtered by time of day to remove unreliable trading periods",
        "Detection of directional bias specific to the current trading session",
        "Directional bias observed during the first hour of trading",
        "Directional bias observed during the final hour of trading",
        "Seasonal pattern specific to the current day of the week",
        "Seasonal pattern specific to the current month of the year",
        "Seasonal pattern specific to the current quarter of the year",
        "Seasonal pattern based on multi-year annual analysis"
    )

    private val probabilityDescriptions = listOf(
        "A setup with high statistical probability based on multiple confirming factors",
        "A setup with low statistical probability that offers asymmetric reward potential",
        "A mean reversion entry when price deviates significantly from its moving average",
        "A momentum continuation entry with acceleration and volume confirmation",
        "A statistical arbitrage opportunity between correlated instruments",
        "A pairs trading signal when correlated assets diverge from their historical relationship",
        "A regression to the mean signal with statistical significance testing",
        "A Bollinger Band touch with mean reversion expectation at the extreme",
        "A breakout beyond a statistically significant standard deviation threshold",
        "A Z-score extreme value indicating statistically significant price deviation",
        "A high probability zone where historical win rates exceed predefined thresholds",
        "A low probability zone where caution is warranted based on historical data",
        "A break outside the statistically derived confidence interval",
        "A test for normal distribution assumption violation suggesting non-random movement",
        "A chi-squared statistical test signal indicating non-random price behavior",
        "A Bayesian probability update signal based on new market information",
        "A Monte Carlo simulation crossing a critical threshold level",
        "A positive expected value opportunity based on historical outcome analysis",
        "A negative expected value zone where avoidance is statistically optimal",
        "An optimal risk-reward ratio setup based on probability-weighted outcomes",
        "A peak in the probability density function suggesting a high-conviction level",
        "A valley in the probability density function suggesting price avoidance",
        "A break above a cumulative probability threshold indicating statistical significance",
        "An extreme percentile reading suggesting overextended price conditions",
        "A quantile shift indicating changing distribution characteristics",
        "A decile breakout with statistical confirmation from multiple periods",
        "A momentum measure normalized by volatility for statistical consistency",
        "A probability-weighted trend analysis accounting for outcome uncertainty",
        "A stochastic oscillator pivot with probability-based confirmation",
        "A random walk hypothesis test indicating non-random price movement",
        "A Markov chain state transition detected at a critical level",
        "A Hidden Markov Model state detection at a significant turning point",
        "A maximum likelihood estimation indicating directional trend bias",
        "A maximum likelihood reversal signal with high confidence",
        "An Akaike Information Criterion best model selection signal",
        "A Bayesian Information Criterion optimal pattern detection",
        "A posterior probability shift indicating increasing probability of upward movement",
        "A posterior probability shift indicating increasing probability of downward movement",
        "A prior probability distribution shift suggesting changing market regime",
        "A likelihood ratio test indicating significant model improvement",
        "An odds ratio calculation suggesting favorable trading conditions",
        "A log-odds reversal signal with statistical significance",
        "A signal based on entropy measurement indicating pattern formation",
        "A mutual information peak between price and volume suggesting informed trading",
        "A conditional probability assessment with high predictive value",
        "A low conditional probability scenario suggesting caution",
        "A joint probability break suggesting coordinated movement across multiple factors",
        "A marginal probability shift indicating improving directional odds",
        "A positive expected return calculation based on statistical analysis",
        "A negative expected return zone identified through probability weighting"
    )

    private val momentumDescriptions = listOf(
        "A bullish divergence between price making lower lows and RSI making higher lows",
        "A bearish divergence between price making higher highs and RSI making lower highs",
        "A hidden bullish divergence where price makes a higher low but RSI makes a lower low",
        "A hidden bearish divergence where price makes a lower high but RSI makes a higher high",
        "RSI entering the overbought zone above 70 suggesting potential exhaustion",
        "RSI entering the oversold zone below 30 suggesting potential bounce",
        "A rejection at a significant RSI level with a corresponding price reaction",
        "A break of a trendline on the RSI indicator confirming momentum shift",
        "A bullish crossover of the %K line above the %D line on the stochastic oscillator",
        "A bearish crossover of the %K line below the %D line on the stochastic oscillator",
        "Stochastic oscillator entering overbought territory above 80",
        "Stochastic oscillator entering oversold territory below 20",
        "A divergence between stochastic oscillator and price action",
        "Williams %R reaching extreme levels below -80 indicating oversold conditions",
        "A divergence between Williams %R and price suggesting potential reversal",
        "A turn in Williams %R from extreme levels with price confirmation",
        "Acceleration of momentum as measured by rate of change indicators",
        "Divergence between momentum and price suggesting weakening trend",
        "Exhaustion of momentum as momentum oscillator flattens or turns",
        "Continuation of momentum with sustained acceleration and volume",
        "A breakout confirmed by an accelerating rate of change indicator",
        "Rate of change diverging from price action suggesting potential reversal",
        "Rate of change accelerating indicating strengthening directional movement",
        "Rate of change decelerating indicating weakening directional movement",
        "Commodity Channel Index crossing above +100 suggesting strong momentum",
        "Divergence between CCI and price suggesting potential trend change",
        "CCI crossing back below +100 or above -100 suggesting trend change",
        "CCI pullback to the zero line within a strong trend suggesting continuation",
        "Ultimate Oscillator generating a buy or sell signal at extremes",
        "Divergence between Ultimate Oscillator and price action",
        "Positive money flow volume confirming accumulation and bullish momentum",
        "Negative money flow volume confirming distribution and bearish momentum",
        "Chaikin Money Flow surging above 0.25 indicating strong buying pressure",
        "Divergence between Chaikin Money Flow and price suggesting potential reversal",
        "ADX rising above 25 indicating strengthening trend regardless of direction",
        "ADX falling below 20 indicating weakening trend and potential range",
        "The +DI crossing above -DI with ADX above 25 for bullish trend confirmation",
        "The -DI crossing above +DI with ADX above 25 for bearish trend confirmation",
        "Positive directional movement indicator strengthening with price",
        "Negative directional movement indicator strengthening with price"
    )

    private val statisticalDescriptions = listOf(
        "A breakout beyond the Keltner Channel or standard deviation bands",
        "A bounce from a standard deviation channel boundary back toward the mean",
        "A breakout from a linear regression channel with trend confirmation",
        "A bounce from a regression channel boundary back toward the regression line",
        "A correlation shift suggesting a change in the relationship between assets",
        "A negative correlation shift indicating decoupling from a correlated peer",
        "A surge in covariance suggesting increasing co-movement between related assets",
        "A peak in autocorrelation indicating a repeating pattern in the time series",
        "A partial autocorrelation signal at a significant lag suggesting model relevance",
        "A cross-correlation lead where one asset consistently leads another",
        "A cross-correlation lag where an asset follows another with a time delay",
        "A divergence in correlation suggesting a breakdown in the historical relationship",
        "A surge in beta suggesting increasing relative volatility to the market",
        "A decline in beta suggesting decreasing relative volatility to the market",
        "An alpha generation signal suggesting excess return potential",
        "An alpha decay pattern suggesting diminishing excess return opportunity",
        "A divergence between alpha and expected returns suggesting regime change",
        "A divergence between beta and market relationship suggesting structural shift",
        "A break in the volatility smile pattern suggesting extreme event pricing",
        "A shift in volatility skew suggesting changing tail risk perception",
        "A change in the volatility term structure suggesting shifting forward expectations",
        "A break in the forward volatility curve suggesting repricing of risk",
        "A surge in implied volatility suggesting increased uncertainty and option premium",
        "A break in historical volatility from its average suggesting regime change",
        "A variance ratio test indicating deviation from random walk behavior",
        "An Augmented Dickey-Fuller test indicating stationarity and mean reversion",
        "An Augmented Dickey-Fuller test indicating non-stationarity and trending behavior",
        "A Kruskal-Wallis test signal suggesting significant differences between groups",
        "A Mann-Whitney U test break suggesting distributional shift",
        "A Wilcoxon signed-rank test signal suggesting median shift",
        "A Kolmogorov-Smirnov test divergence suggesting different underlying distributions",
        "A Granger causality detection suggesting predictive relationship between variables",
        "A cointegration break suggesting a change in long-term equilibrium relationship",
        "A cointegration recovery suggesting a return to long-term equilibrium",
        "A Kalman filter break suggesting a regime change in the underlying process",
        "A Kalman filter reversal signal based on state estimation changes",
        "A particle filter signal detecting non-linear state transitions",
        "A Hurst exponent above 0.5 indicating trending behavior with long memory",
        "A Hurst exponent below 0.5 indicating mean-reverting behavior",
        "A fractal dimension shift suggesting changing market complexity and efficiency"
    )

    private val categoryDescriptionsMap = mapOf(
        "Trend Patterns" to trendDescriptions,
        "Reversal Patterns" to reversalDescriptions,
        "Repeating Sequences" to repeatingSequenceDescriptions,
        "Frequency Patterns" to frequencyDescriptions,
        "Timing Patterns" to timingDescriptions,
        "Probability Patterns" to probabilityDescriptions,
        "Momentum Patterns" to momentumDescriptions,
        "Statistical Patterns" to statisticalDescriptions
    )

    private val riskRatings = listOf("Low", "Medium", "High", "Very High")

    val allPatterns: List<PatternDefinition> by lazy { generateAllPatterns() }
    private var _allPatterns: List<PatternDefinition>? = null

    private val confidenceFormulas = mapOf(
        "Trend Patterns" to listOf(
            "0.6 * trendStrength + 0.2 * volumeConfirm + 0.2 * momentumScore",
            "0.5 * trendClarity + 0.3 * volumeRatio + 0.2 * breakoutDistance",
            "0.55 * patternQuality + 0.25 * volumeSupport + 0.2 * timeframeAlignment"
        ),
        "Reversal Patterns" to listOf(
            "0.4 * candlestickQuality + 0.3 * volumeConfirm + 0.3 * supportTest",
            "0.45 * patternRecognition + 0.35 * volumeDivergence + 0.2 * trendExhaustion",
            "0.5 * reversalStrength + 0.25 * volumeSpike + 0.25 * resistanceBreak"
        ),
        "Repeating Sequences" to listOf(
            "0.5 * harmonicRatio + 0.3 * fibonacciAccuracy + 0.2 * volumeConfirm",
            "0.45 * patternSymmetry + 0.35 * legEquality + 0.2 * momentumDivergence",
            "0.55 * waveStructure + 0.25 * retracementDepth + 0.2 * extensionTarget"
        ),
        "Frequency Patterns" to listOf(
            "0.5 * frequencySurge + 0.3 * volumeSpike + 0.2 * priceVelocity",
            "0.4 * spectralDensity + 0.35 * cyclePhase + 0.25 * harmonicConvergence",
            "0.45 * orderFlowImbalance + 0.35 * deltaDivergence + 0.2 * volumeProfile"
        ),
        "Timing Patterns" to listOf(
            "0.5 * sessionBias + 0.3 * volumeProfile + 0.2 * seasonalStrength",
            "0.4 * temporalAccuracy + 0.35 * historicalReliability + 0.25 * volumeConfirm",
            "0.45 * timeCycleAlignment + 0.3 * sessionMomentum + 0.25 * rangeExpansion"
        ),
        "Probability Patterns" to listOf(
            "0.5 * statisticalSignificance + 0.3 * historicalWinRate + 0.2 * riskReward",
            "0.55 * probabilityScore + 0.25 * expectedValue + 0.2 * confidenceInterval",
            "0.4 * bayesianLikelihood + 0.35 * posteriorProbability + 0.25 * entropyShift"
        ),
        "Momentum Patterns" to listOf(
            "0.5 * oscillatorDivergence + 0.3 * momentumVelocity + 0.2 * volumeConfirm",
            "0.45 * momentumAcceleration + 0.35 * overboughtOversold + 0.2 * trendConfirm",
            "0.55 * rsiDivergence + 0.25 * stochasticCross + 0.2 * adxStrength"
        ),
        "Statistical Patterns" to listOf(
            "0.5 * statisticalSignificance + 0.3 * effectSize + 0.2 * sampleStability",
            "0.45 * regressionFit + 0.35 * correlationStrength + 0.2 * residualAnalysis",
            "0.55 * testStatistic + 0.25 * degreesFreedom + 0.2 * confidenceLevel"
        )
    )

    private fun generateAllPatterns(): List<PatternDefinition> {
        val patterns = mutableListOf<PatternDefinition>()
        for ((category, range) in categoryRanges) {
            val names = categoryNameMap[category] ?: continue
            val descriptions = categoryDescriptionsMap[category] ?: continue
            val formulas = confidenceFormulas[category] ?: continue
            val indexedNames = names.mapIndexed { idx, name ->
                val seqNumber = range.first + idx
                val formattedId = "%03d".format(seqNumber)
                "$name" to formattedId
            }
            for (indexedName in indexedNames.withIndex()) {
                val withIdx = indexedName.index
                val name = indexedName.value.first
                val formattedId = indexedName.value.second
                val patternId = "PAT_$formattedId"
                val seqNumber = range.first + withIdx
                val descIndex = index % descriptions.size
                val formulaIndex = index % formulas.size
                val riskIndex = index % riskRatings.size
                val conditionCount = 2 + (index % 4)
                val requiredConditions = (1..conditionCount).map { i ->
                    when (category) {
                        "Trend Patterns" -> listOf("TREND_DIRECTION_CONFIRMED", "VOLUME_ABOVE_THRESHOLD", "MOMENTUM_ALIGNED", "SUPPORT_RESISTANCE_TESTED", "BREAKOUT_CONFIRMED")[i % 5]
                        "Reversal Patterns" -> listOf("PATTERN_STRUCTURE_COMPLETE", "VOLUME_CONFIRMATION", "TREND_EXHAUSTION", "SUPPORT_OR_RESISTANCE", "CANDLE_PATTERN_VALID")[i % 5]
                        "Repeating Sequences" -> listOf("WAVE_STRUCTURE_VALID", "HARMONIC_RATIO_MET", "FIBONACCI_LEVEL_HIT", "PATTERN_SYMMETRY", "EXTENSION_TARGET")[i % 5]
                        "Frequency Patterns" -> listOf("FREQUENCY_THRESHOLD_EXCEEDED", "VOLUME_CONFIRMATION", "CYCLE_PHASE_ALIGNED", "SPECTRAL_PEAK_DETECTED", "ORDER_FLOW_VALID")[i % 5]
                        "Timing Patterns" -> listOf("SESSION_WINDOW_ACTIVE", "TIME_CYCLE_ALIGNED", "VOLUME_CONFIRMATION", "HISTORICAL_PATTERN_MATCH", "RANGE_CONDITION_MET")[i % 5]
                        "Probability Patterns" -> listOf("STATISTICAL_SIGNIFICANCE_MET", "PROBABILITY_THRESHOLD_EXCEEDED", "SAMPLE_SIZE_ADEQUATE", "CONFIDENCE_INTERVAL_VALID", "RISK_REWARD_OPTIMAL")[i % 5]
                        "Momentum Patterns" -> listOf("OSCILLATOR_DIVERGENCE", "MOMENTUM_CONFIRMATION", "VOLUME_VALIDATION", "TREND_ALIGNMENT", "EXTREME_READING")[i % 5]
                        "Statistical Patterns" -> listOf("TEST_STATISTIC_SIGNIFICANT", "P_VALUE_THRESHOLD_MET", "EFFECT_SIZE_ADEQUATE", "ASSUMPTIONS_VALIDATED", "SAMPLE_STABLE")[i % 5]
                        else -> listOf("CONDITION_${i}")[0]
                    }
                }
                val weightageMap = requiredConditions.associateWith { cond ->
                    when (cond) {
                        "TREND_DIRECTION_CONFIRMED", "PATTERN_STRUCTURE_COMPLETE", "WAVE_STRUCTURE_VALID",
                        "FREQUENCY_THRESHOLD_EXCEEDED", "SESSION_WINDOW_ACTIVE", "STATISTICAL_SIGNIFICANCE_MET",
                        "OSCILLATOR_DIVERGENCE", "TEST_STATISTIC_SIGNIFICANT" -> 0.35
                        "VOLUME_ABOVE_THRESHOLD", "VOLUME_CONFIRMATION", "VOLUME_VALIDATION" -> 0.25
                        else -> 1.0 * (1.0 - 0.35 - 0.25 * (conditionCount - 2).coerceAtLeast(0)) / (conditionCount).coerceAtLeast(1)
                    }
                }
                val fullName = "${name}_${formattedId}"
                val description = descriptions[descIndex]
                val pattern = PatternDefinition(
                    patternId = patternId,
                    name = fullName,
                    category = category,
                    description = description,
                    detectionRules = DetectionRules(
                        minSequenceLength = 2 + (seqNumber % 5),
                        requiredConditions = requiredConditions,
                        weightage = weightageMap
                    ),
                    confidenceFormula = formulas[formulaIndex],
                    riskRating = riskRatings[riskIndex]
                )
                patterns.add(pattern)
            }
        }
        return patterns
    }

    fun getPatternById(patternId: String): PatternDefinition? {
        return allPatterns.find { it.patternId == patternId }
    }

    fun getPatternsByCategory(category: String): List<PatternDefinition> {
        return allPatterns.filter { it.category == category }
    }

    fun searchPatterns(query: String): List<PatternDefinition> {
        val lowerQuery = query.lowercase()
        return allPatterns.filter {
            it.name.lowercase().contains(lowerQuery) ||
            it.description.lowercase().contains(lowerQuery) ||
            it.category.lowercase().contains(lowerQuery)
        }
    }

    fun getCategories(): List<String> {
        return categoryRanges.map { it.first }
    }

    fun getPatternCount(): Int = allPatterns.size
}
