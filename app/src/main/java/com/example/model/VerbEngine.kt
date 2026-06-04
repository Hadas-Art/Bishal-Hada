package com.example.model

import kotlin.random.Random

data class Verb(
    val base: String,
    val past: String,
    val pp: String,     // Past Participle (V3)
    val ing: String,    // Present Participle (-ing)
    val type: String    // "regular" or "irregular"
)

enum class EnglishTense(
    val id: String,
    val displayName: String,
    val formula: String,
    val description: String,
    val timeMarkers: List<String>
) {
    PRESENT_SIMPLE(
        "present_simple",
        "Present Simple",
        "S + V(s/es)",
        "Daily routines, habits, general truths, and permanent states.",
        listOf("every day", "always", "usually", "often", "on Sundays", "rarely")
    ),
    PRESENT_CONTINUOUS(
        "present_continuous",
        "Present Continuous",
        "S + am/is/are + V-ing",
        "Actions happening right now, active scene description, or temporary situations.",
        listOf("right now", "at the moment", "Look!", "Listen!", "currently", "now")
    ),
    PAST_SIMPLE(
        "past_simple",
        "Past Simple",
        "S + V2",
        "Completed activities in the past that started and ended at a specific time.",
        listOf("yesterday", "last night", "two days ago", "in 2022", "last week", "an hour ago")
    ),
    PAST_CONTINUOUS(
        "past_continuous",
        "Past Continuous",
        "S + was/were + V-ing",
        "Ongoing actions in the past that were in progress at a specific moment or interrupted.",
        listOf("when you called", "while I was sleeping", "at 8 PM yesterday", "all night")
    ),
    FUTURE_SIMPLE(
        "future_simple",
        "Future Simple",
        "S + will + V1",
        "Spontaneous decisions, predictions without direct evidence, promises, and offers.",
        listOf("tomorrow", "next week", "soon", "in two days", "by tonight", "one day")
    ),
    PRESENT_PERFECT(
        "present_perfect",
        "Present Perfect",
        "S + have/has + V3",
        "Completed actions connecting the past to the present, focusing on results or life experience.",
        listOf("already", "just", "yet", "ever", "never", "recently", "so far", "since 2018")
    ),
    PAST_PERFECT(
        "past_perfect",
        "Past Perfect",
        "S + had + V3",
        "Actions that occurred and were completed prior to another action in the past.",
        listOf("before we arrived", "by the time they left", "already", "previously")
    );

    companion object {
        fun getById(id: String): EnglishTense = entries.firstOrNull { it.id == id } ?: PRESENT_SIMPLE
    }
}

enum class PracticeMode(val id: String, val displayName: String, val icon: String, val description: String) {
    SINGLE_TENSE("single", "Specific Tense", "🎯", "Focus on practicing conjugations for a single tense selection."),
    MIXED("mixed", "Mixed Quiz", "🔀", "Practice sentences randomly blended from all seven tenses."),
    TENSE_FINDER("finder", "Tense Finder", "🔍", "Analyze a raw sentence and correctly identify the verb tense used."),
    IRREGULAR("irregular", "Irregular Verbs", "⚡", "Master challenging and high-frequency irregular verb forms.")
}

data class QuizQuestion(
    val tense: EnglishTense,
    val difficulty: String, // "easy", "hard", "difficult"
    val prompt: String,     // e.g. "We ___ (go) to the library yesterday."
    val options: List<String>,
    val correctValue: String,
    val explanation: String,
    val baseVerb: String,
    val isIrregular: Boolean
)

object VerbEngine {

    val VERBS = listOf(
        Verb("play", "played", "played", "playing", "regular"),
        Verb("work", "worked", "worked", "working", "regular"),
        Verb("talk", "talked", "talked", "talking", "regular"),
        Verb("visit", "visited", "visited", "visiting", "regular"),
        Verb("study", "studied", "studied", "studying", "regular"),
        Verb("clean", "cleaned", "cleaned", "cleaning", "regular"),
        Verb("watch", "watched", "watched", "watching", "regular"),
        Verb("paint", "painted", "painted", "painting", "regular"),
        Verb("cook", "cooked", "cooked", "cooking", "regular"),
        
        Verb("go", "went", "gone", "going", "irregular"),
        Verb("eat", "ate", "eaten", "eating", "irregular"),
        Verb("see", "saw", "seen", "seeing", "irregular"),
        Verb("take", "took", "taken", "taking", "irregular"),
        Verb("write", "wrote", "written", "writing", "irregular"),
        Verb("drink", "drank", "drunk", "drinking", "irregular"),
        Verb("sing", "sing", "sung", "singing", "irregular"),
        Verb("begin", "began", "begun", "beginning", "irregular"),
        Verb("break", "broke", "broken", "breaking", "irregular"),
        Verb("choose", "chose", "chosen", "choosing", "irregular"),
        Verb("drive", "drove", "driven", "driving", "irregular"),
        Verb("fly", "flew", "flown", "flying", "irregular"),
        Verb("forget", "forgot", "forgotten", "forgetting", "irregular"),
        Verb("give", "gave", "given", "giving", "irregular"),
        Verb("know", "knew", "known", "knowing", "irregular"),
        Verb("speak", "spoke", "spoken", "speaking", "irregular"),
        Verb("teach", "taught", "taught", "teaching", "irregular"),
        Verb("buy", "bought", "bought", "buying", "irregular"),
        Verb("think", "thought", "thought", "thinking", "irregular"),
        Verb("read", "read", "read", "reading", "irregular"),
        Verb("run", "ran", "run", "running", "irregular"),
        Verb("make", "made", "made", "making", "irregular"),
        Verb("build", "built", "built", "building", "irregular"),
        Verb("sleep", "slept", "slept", "sleeping", "irregular"),
        Verb("wear", "wore", "worn", "wearing", "irregular"),
        Verb("hide", "hid", "hidden", "hiding", "irregular"),
        Verb("spend", "spent", "spent", "spending", "irregular")
    )

    private val SUBJECTS_SINGULAR_3RD = listOf("He", "She", "It", "My smart brother", "Our teacher", "The doctor", "Emma", "The cat")
    private val SUBJECTS_OTHER = listOf("I", "You", "We", "They", "The students", "My parents", "Emma and Tom", "The workers")

    private val COMPLEMENTS_OBJECTS = mapOf(
        "play" to listOf("football", "the piano", "chess", "video games"),
        "work" to listOf("until late hours", "on the school project", "at the hospital", "very hard"),
        "talk" to listOf("about the upcoming trip", "with the principal", "secretly", "very quickly"),
        "visit" to listOf("the city museum", "their supportive grandparents", "Rome", "a modern farm"),
        "study" to listOf("English tenses", "biological sciences", "ancient history", "at the university library"),
        "clean" to listOf("the dusty room", "the whole kitchen", "their workspace", "the car"),
        "watch" to listOf("a horror movie", "the birds in the garden", "a documentary", "football matches"),
        "paint" to listOf("a beautiful landscape", "the main gate red", "a portrait of the queen"),
        "cook" to listOf("some delicious pasta", "vegetable soup", "dinner for my guests", "fried rice"),
        "go" to listOf("to the library", "home", "to Paris", "to the shopping mall"),
        "eat" to listOf("a fresh salad", "delicious Italian pizza", "hot vegetable soup", "breakfast"),
        "see" to listOf("a shooting star", "our history professor", "a modern airplane", "a wild deer"),
        "take" to listOf("a long test", "some photos of the city", "a useful notebook", "warm medicine"),
        "write" to listOf("a long poem", "an informative email", "a story about animals", "a postcard"),
        "drink" to listOf("a cup of herbal tea", "a glass of cold water", "warm milk", "fresh orange juice"),
        "sing" to listOf("a pop song", "traditional hymns", "melodious tunes", "karaoke"),
        "begin" to listOf("the test", "their speech", "the daily presentation", "a coding journey"),
        "break" to listOf("a vintage coffee cup", "the school rules", "the world record", "the window"),
        "choose" to listOf("the dark blue shirt", "a matching career", "the difficult quiz option"),
        "drive" to listOf("a fast red sportscar", "carefully through the storm", "to the suburbs"),
        "fly" to listOf("a private airplane", "to Switzerland", "all the way to Tokyo"),
        "forget" to listOf("the password", "the keys in the car", "their anniversary date", "to finish the work"),
        "give" to listOf("a nice pocket watch", "a quick feedback report", "a sincere apology"),
        "know" to listOf("the correct answers", "each other since school", "the way back home"),
        "speak" to listOf("English very fluently", "three European languages", "loudly during the call"),
        "teach" to listOf("math to high school kids", "scientific methods", "how to swim"),
        "buy" to listOf("a modern laptop", "groceries for the family", "a leather jacket"),
        "think" to listOf("about the grammar rule", "deeply", "about their sweet memories"),
        "read" to listOf("an interesting fantasy book", "news reports", "a classical romance novel"),
        "run" to listOf("a full marathon", "very fast to catch the train", "in the green park"),
        "make" to listOf("a tasty cheese sandwich", "a silly spelling mistake", "a wooden table"),
        "build" to listOf("a strong treehouse", "a popular mobile application", "bridges over the river"),
        "sleep" to listOf("like a newborn baby", "on the comfortable sofa", "under the blue sky"),
        "wear" to listOf("a warm winter jacket", "heavy black boots", "a friendly smile"),
        "hide" to listOf("behind the large oak tree", "secrets from everyone", "the birthday present"),
        "spend" to listOf("all their pockets money", "the whole weekend together", "time in self-reflection")
    )

    fun generate(tense: EnglishTense, difficulty: String): QuizQuestion {
        val isSingular3rd = Random.nextBoolean()
        val subject = if (isSingular3rd) {
            SUBJECTS_SINGULAR_3RD.random()
        } else {
            SUBJECTS_OTHER.random()
        }

        // Difficulty tweaks:
        // Easy is always affirmative.
        // Hard introduces negatives (about 40% of the time).
        // Difficult introduces complex time markers, negatives (60%), or interrogative helper contexts.
        val typeOfSentence = when (difficulty) {
            "easy" -> "affirmative"
            "hard" -> if (Random.nextFloat() < 0.4f) "negative" else "affirmative"
            else -> {
                val r = Random.nextFloat()
                if (r < 0.5f) "negative" else "affirmative"
            }
        }

        // Pick a verb
        val verb = VERBS.random()
        val objects = COMPLEMENTS_OBJECTS[verb.base] ?: listOf("and learn")
        val verbObject = objects.random()
        val timeMarker = tense.timeMarkers.random()

        var prompt = ""
        var correctChoice = ""
        var explanation = ""

        val verbPrompt = if (typeOfSentence == "negative") "not ${verb.base}" else verb.base

        // Format prompt & correct values
        when (tense) {
            EnglishTense.PRESENT_SIMPLE -> {
                if (typeOfSentence == "negative") {
                    val aux = if (isSingular3rd) "does not" else "do not"
                    correctChoice = "$aux ${verb.base}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = if (isSingular3rd) {
                        "With singular third-person subjects (he/she/it/singular noun), we form negatives using 'does not' + base form of the verb."
                    } else {
                        "With plural subjects or I/you/we/they, we form negatives using 'do not' + base form."
                    }
                } else {
                    if (isSingular3rd) {
                        correctChoice = addSExtension(verb.base)
                        prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                        explanation = "In Present Simple, positive statements with singular third-person subjects (He/She/It) require adding '-s', '-es', or '-ies' to the verb."
                    } else {
                        correctChoice = verb.base
                        prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                        explanation = "In Present Simple, positive statements with I, You, We, They, or plural nouns use the base form of the verb."
                    }
                }
            }
            
            EnglishTense.PRESENT_CONTINUOUS -> {
                val aux = when {
                    subject == "I" -> "am"
                    isSingular3rd -> "is"
                    else -> "are"
                }
                if (typeOfSentence == "negative") {
                    correctChoice = "$aux not ${verb.ing}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = "Present Continuous expresses current actions. Negatives use am/is/are + not + V-ing. Here, '$subject' takes '$aux not'."
                } else {
                    correctChoice = "$aux ${verb.ing}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = "Present Continuous uses am/is/are + V-ing. The subject '$subject' fits with '$aux ${verb.ing}'."
                }
            }

            EnglishTense.PAST_SIMPLE -> {
                if (typeOfSentence == "negative") {
                    correctChoice = "did not ${verb.base}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = "For negatives in Past Simple, we use 'did not' + base form of the verb regardless of the subject."
                } else {
                    correctChoice = verb.past
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = if (verb.type == "irregular") {
                        "Past Simple for the irregular verb '${verb.base}' is '${verb.past}' (does not follow standard -ed rules)."
                    } else {
                        "Past Simple for the regular verb '${verb.base}' is built by adding '-ed': '${verb.past}'."
                    }
                }
            }

            EnglishTense.PAST_CONTINUOUS -> {
                val aux = if (subject == "I" || isSingular3rd) "was" else "were"
                if (typeOfSentence == "negative") {
                    correctChoice = "$aux not ${verb.ing}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = "Past Continuous negatives use was/were + not + V-ing. Here, '$subject' goes with '$aux not'."
                } else {
                    correctChoice = "$aux ${verb.ing}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = "Past Continuous shows an action in progress in the past. It uses was/were + V-ing. Subject '$subject' takes '$aux'."
                }
            }

            EnglishTense.FUTURE_SIMPLE -> {
                if (typeOfSentence == "negative") {
                    correctChoice = "will not ${verb.base}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = "Future Simple negative statements use 'will not' (or won't) + base form of the verb."
                } else {
                    correctChoice = "will ${verb.base}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = "Future Simple uses 'will' + base form of the verb for predictions or promises."
                }
            }

            EnglishTense.PRESENT_PERFECT -> {
                val aux = if (subject == "I" || subject == "You" || subject == "We" || subject == "They" || !isSingular3rd) {
                    "have"
                } else {
                    "has"
                }
                if (typeOfSentence == "negative") {
                    correctChoice = "$aux not ${verb.pp}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = "Present Perfect negative statements use have/has + not + Past Participle (V3). Subject '$subject' takes '$aux'."
                } else {
                    correctChoice = "$aux ${verb.pp}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = "Present Perfect connects past actions with present results. It uses 'have' or 'has' (for he/she/it) + Past Participle (V3) of the verb: '$correctChoice'."
                }
            }

            EnglishTense.PAST_PERFECT -> {
                if (typeOfSentence == "negative") {
                    correctChoice = "had not ${verb.pp}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = "Past Perfect negatives use 'had not' + Past Participle (V3) for all subjects, indicating it occurred before another past milestone."
                } else {
                    correctChoice = "had ${verb.pp}"
                    prompt = "$subject ___ ($verbPrompt) $verbObject $timeMarker."
                    explanation = "Past Perfect uses 'had' + Past Participle (V3) to express that an action finished before another action in the past."
                }
            }
        }

        // Generate smart distractors
        val distractors = generateDistractors(tense, verb, isSingular3rd, typeOfSentence, correctChoice)
        val options = (distractors + correctChoice).shuffled()

        return QuizQuestion(
            tense = tense,
            difficulty = difficulty,
            prompt = prompt,
            options = options,
            correctValue = correctChoice,
            explanation = explanation,
            baseVerb = verb.base,
            isIrregular = verb.type == "irregular"
        )
    }

    fun generateMixed(difficulty: String): QuizQuestion {
        val randomTense = EnglishTense.entries.random()
        return generate(randomTense, difficulty)
    }

    fun generateTenseFinder(difficulty: String): QuizQuestion {
        val targetTense = EnglishTense.entries.random()
        val question = generate(targetTense, "easy")
        
        // Form a complete sentence with the blank filled in
        val completeSentence = question.prompt.replace("___ (${question.baseVerb})", question.correctValue)
            .replace("___ (not ${question.baseVerb})", question.correctValue)
        
        val correctTenseName = targetTense.displayName
        val otherTenses = EnglishTense.entries.filter { it != targetTense }.shuffled().take(3)
        val options = (otherTenses.map { it.displayName } + correctTenseName).shuffled()
        
        val explanation = "The sentence uses the verb form '${question.correctValue}'. The formula '${targetTense.formula}' matches the ${targetTense.displayName}. Usage: ${targetTense.description}"
        
        return QuizQuestion(
            tense = targetTense,
            difficulty = difficulty,
            prompt = "Identify the tense/aspect used in the following sentence:\n\"$completeSentence\"",
            options = options,
            correctValue = correctTenseName,
            explanation = explanation,
            baseVerb = question.baseVerb,
            isIrregular = question.isIrregular
        )
    }

    fun generateIrregular(difficulty: String): QuizQuestion {
        val irregularVerbsOnly = VERBS.filter { it.type == "irregular" }
        val testedTenses = listOf(
            EnglishTense.PAST_SIMPLE,
            EnglishTense.PRESENT_PERFECT,
            EnglishTense.PAST_PERFECT
        )
        val tense = testedTenses.random()
        
        val isSingular3rd = Random.nextBoolean()
        val subject = if (isSingular3rd) {
            SUBJECTS_SINGULAR_3RD.random()
        } else {
            SUBJECTS_OTHER.random()
        }
        
        val verb = irregularVerbsOnly.random()
        val objects = COMPLEMENTS_OBJECTS[verb.base] ?: listOf("and learn")
        val verbObject = objects.random()
        val timeMarker = tense.timeMarkers.random()
        
        val typeOfSentence = "affirmative"
        var correctChoice = ""
        var explanation = ""
        
        when (tense) {
            EnglishTense.PAST_SIMPLE -> {
                correctChoice = verb.past
                explanation = "In Past Simple, the irregular verb '${verb.base}' has the past tense form '${verb.past}' (does not follow standard -ed rules)."
            }
            EnglishTense.PRESENT_PERFECT -> {
                val aux = if (subject == "I" || subject == "You" || subject == "We" || subject == "They" || !isSingular3rd) {
                    "have"
                } else {
                    "has"
                }
                correctChoice = "$aux ${verb.pp}"
                explanation = "Present Perfect requires 'have/has' + Past Participle (V3). For the irregular verb '${verb.base}', V3 is '${verb.pp}', resulting in '${correctChoice}'."
            }
            EnglishTense.PAST_PERFECT -> {
                correctChoice = "had ${verb.pp}"
                explanation = "Past Perfect requires 'had' + Past Participle (V3). For the irregular verb '${verb.base}', V3 is '${verb.pp}', giving '${correctChoice}'."
            }
            else -> {
                correctChoice = verb.past
                explanation = "The irregular past form of '${verb.base}' is '${verb.past}'."
            }
        }
        
        val prompt = "$subject ___ (${verb.base}) $verbObject $timeMarker."
        
        val distractors = mutableSetOf<String>()
        if (tense == EnglishTense.PAST_SIMPLE) {
            distractors.add("${verb.base}ed") // typical spelling mistake
            distractors.add(verb.pp)
            distractors.add(verb.base)
        } else {
            val aux = if (tense == EnglishTense.PRESENT_PERFECT) {
                if (subject == "I" || subject == "You" || subject == "We" || subject == "They" || !isSingular3rd) "have" else "has"
            } else "had"
            distractors.add("$aux ${verb.past}") // using past form instead of participle
            distractors.add("$aux ${verb.base}") // using base form instead of participle
            distractors.add("$aux ${verb.base}ed") // incorrect regularized form
        }
        distractors.remove(correctChoice)
        val options = (distractors.filter { it.isNotBlank() }.take(3).toSet() + correctChoice).shuffled()
        
        return QuizQuestion(
            tense = tense,
            difficulty = difficulty,
            prompt = prompt,
            options = options,
            correctValue = correctChoice,
            explanation = explanation,
            baseVerb = verb.base,
            isIrregular = true
        )
    }

    private fun addSExtension(base: String): String {
        return when {
            base.endsWith("y") && !isVowel(base[base.length - 2]) -> base.dropLast(1) + "ies"
            base.endsWith("s") || base.endsWith("sh") || base.endsWith("ch") || base.endsWith("x") || base.endsWith("z") || base.endsWith("o") -> base + "es"
            else -> base + "s"
        }
    }

    private fun isVowel(c: Char): Boolean = c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u'

    private fun generateDistractors(
        tense: EnglishTense,
        verb: Verb,
        isSingular3rd: Boolean,
        sentenceType: String,
        correct: String
    ): Set<String> {
        val fakes = mutableSetOf<String>()

        if (sentenceType == "negative") {
            // Negative distractors
            when (tense) {
                EnglishTense.PRESENT_SIMPLE -> {
                    fakes.add(if (isSingular3rd) "do not ${verb.base}" else "does not ${verb.base}")
                    fakes.add("not ${verb.base}")
                    fakes.add("did not ${verb.base}")
                    fakes.add("no ${verb.base}")
                }
                EnglishTense.PRESENT_CONTINUOUS -> {
                    val badAux = if (isSingular3rd) "are not" else "is not"
                    fakes.add("$badAux ${verb.ing}")
                    fakes.add("am not ${verb.base}")
                    fakes.add("not ${verb.ing}")
                    fakes.add("don't ${verb.ing}")
                }
                EnglishTense.PAST_SIMPLE -> {
                    fakes.add("did not ${verb.past}") // redundant past (double past is common mistake)
                    fakes.add("would not ${verb.base}")
                    fakes.add("don't ${verb.base}")
                    fakes.add("not ${verb.past}")
                }
                EnglishTense.PAST_CONTINUOUS -> {
                    val badAux = if (isSingular3rd) "were not" else "was not"
                    fakes.add("$badAux ${verb.ing}")
                    fakes.add("did not ${verb.ing}")
                    fakes.add("was not ${verb.base}")
                }
                EnglishTense.FUTURE_SIMPLE -> {
                    fakes.add("will not ${verb.past}")
                    fakes.add("not will ${verb.base}")
                    fakes.add("going not to ${verb.base}")
                }
                EnglishTense.PRESENT_PERFECT -> {
                    val badAux = if (isSingular3rd) "have not" else "has not"
                    fakes.add("$badAux ${verb.pp}")
                    fakes.add("did not ${verb.pp}")
                    fakes.add("has not ${verb.base}")
                    fakes.add("have not ${verb.base}")
                }
                EnglishTense.PAST_PERFECT -> {
                    fakes.add("has not ${verb.pp}")
                    fakes.add("have not ${verb.pp}")
                    fakes.add("did not had ${verb.base}")
                }
            }
        } else {
            // Affirmative distractors
            when (tense) {
                EnglishTense.PRESENT_SIMPLE -> {
                    fakes.add(verb.base)
                    fakes.add(addSExtension(verb.base))
                    fakes.add(verb.past)
                    fakes.add(verb.ing)
                }
                EnglishTense.PRESENT_CONTINUOUS -> {
                    fakes.add("is ${verb.ing}")
                    fakes.add("are ${verb.ing}")
                    fakes.add("am ${verb.ing}")
                    fakes.add(verb.ing)
                    fakes.add("is ${verb.base}")
                }
                EnglishTense.PAST_SIMPLE -> {
                    fakes.add(verb.base)
                    fakes.add(verb.pp)
                    fakes.add(verb.ing)
                    fakes.add(verb.base + "ed") // regularized form of irregulars
                }
                EnglishTense.PAST_CONTINUOUS -> {
                    fakes.add("was ${verb.ing}")
                    fakes.add("were ${verb.ing}")
                    fakes.add("was ${verb.base}")
                    fakes.add(verb.past)
                }
                EnglishTense.FUTURE_SIMPLE -> {
                    fakes.add("will ${verb.past}")
                    fakes.add("will ${verb.pp}")
                    fakes.add("going to ${verb.base}")
                    fakes.add("will ${verb.ing}")
                }
                EnglishTense.PRESENT_PERFECT -> {
                    fakes.add("have ${verb.pp}")
                    fakes.add("has ${verb.pp}")
                    fakes.add("have ${verb.base}")
                    fakes.add("has ${verb.base}")
                    fakes.add(verb.past)
                }
                EnglishTense.PAST_PERFECT -> {
                    fakes.add("had ${verb.base}")
                    fakes.add("has ${verb.pp}")
                    fakes.add("have ${verb.pp}")
                    fakes.add("had ${verb.past}")
                }
            }
        }

        fakes.remove(correct)
        return fakes.filter { it.isNotBlank() }.take(3).toSet()
    }
}
