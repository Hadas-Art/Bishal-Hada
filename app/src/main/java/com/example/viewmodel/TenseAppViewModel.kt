package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TenseRepository
import com.example.data.TenseScore
import com.example.data.UserStatsRecord
import com.example.model.EnglishTense
import com.example.model.QuizQuestion
import com.example.model.VerbEngine
import com.example.model.PracticeMode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TenseAppViewModel(private val repository: TenseRepository) : ViewModel() {

    // Filter selectors
    private val _currentMode = MutableStateFlow<PracticeMode>(PracticeMode.SINGLE_TENSE)
    val currentMode: StateFlow<PracticeMode> = _currentMode.asStateFlow()

    private val _selectedTense = MutableStateFlow<EnglishTense>(EnglishTense.PRESENT_SIMPLE)
    val selectedTense: StateFlow<EnglishTense> = _selectedTense.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow("easy")
    val selectedDifficulty: StateFlow<String> = _selectedDifficulty.asStateFlow()

    // Current quiz item
    private val _currentQuestion = MutableStateFlow<QuizQuestion?>(null)
    val currentQuestion: StateFlow<QuizQuestion?> = _currentQuestion.asStateFlow()

    // User selection state on the active question
    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer.asStateFlow()

    private val _isAnswered = MutableStateFlow(false)
    val isAnswered: StateFlow<Boolean> = _isAnswered.asStateFlow()

    // Live indicators from Room database
    val tenseScores: StateFlow<List<TenseScore>> = repository.allTenseScores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userStats: StateFlow<UserStatsRecord> = repository.userStats
        .map { it ?: UserStatsRecord() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserStatsRecord())

    init {
        generateNewQuestion()
    }

    fun setPracticeMode(mode: PracticeMode) {
        _currentMode.value = mode
        generateNewQuestion()
    }

    fun setTenseFilter(tense: EnglishTense) {
        _selectedTense.value = tense
        _currentMode.value = PracticeMode.SINGLE_TENSE
        generateNewQuestion()
    }

    fun setDifficultyFilter(difficulty: String) {
        _selectedDifficulty.value = difficulty
        generateNewQuestion()
    }

    fun generateNewQuestion() {
        val difficulty = _selectedDifficulty.value
        val mode = _currentMode.value
        _currentQuestion.value = when (mode) {
            PracticeMode.SINGLE_TENSE -> VerbEngine.generate(_selectedTense.value, difficulty)
            PracticeMode.MIXED -> VerbEngine.generateMixed(difficulty)
            PracticeMode.TENSE_FINDER -> VerbEngine.generateTenseFinder(difficulty)
            PracticeMode.IRREGULAR -> VerbEngine.generateIrregular(difficulty)
        }
        _selectedAnswer.value = null
        _isAnswered.value = false
    }

    fun selectAnswer(option: String) {
        val q = _currentQuestion.value ?: return
        if (_isAnswered.value) return // already answered

        _selectedAnswer.value = option
        _isAnswered.value = true

        val isCorrect = (option == q.correctValue)

        viewModelScope.launch {
            repository.updateScore(
                tenseId = q.tense.id,
                tenseName = q.tense.displayName,
                isCorrect = isCorrect
            )
        }
    }

    fun resetStats() {
        viewModelScope.launch {
            repository.resetAllStats()
            generateNewQuestion()
        }
    }
}
