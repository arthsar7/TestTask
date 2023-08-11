package wood.app.gannoz.presentation

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wood.app.gannoz.data.QuestionRepositoryImpl
import wood.app.gannoz.domain.entity.GameResult
import wood.app.gannoz.domain.entity.GameSettings
import wood.app.gannoz.domain.entity.Level
import wood.app.gannoz.domain.entity.Question
import wood.app.gannoz.domain.usecases.GetGameSettingsUseCase
import wood.app.gannoz.domain.usecases.GetQuestionsUseCase

class QuestionViewModel(
    private val level: Level
) : ViewModel() {
    private val repository = QuestionRepositoryImpl

    private val getQuestionsUseCase = GetQuestionsUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var timer: CountDownTimer? = null

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val questions = getQuestionsUseCase()

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question>
        get() = _currentQuestion

    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _questionNumber = MutableLiveData(1)
    val questionNumber: LiveData<Int>
        get() = _questionNumber

    private val _percentProgress = MutableLiveData(0)
    val percentProgress: LiveData<Int>
        get() = _percentProgress

    private var countOfQuestions = 0

    private lateinit var gameSettings: GameSettings

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    val size = questions.size

    init {
        startGame()
    }
    fun getNextQuestion() {
        if (countOfQuestions == questions.size) {
            finishGame()
        }
        countOfQuestions++
        _percentProgress.value = calculatePercent(countOfQuestions, size)
        _currentQuestion.value = questions[countOfQuestions - 1]
        _questionNumber.value = countOfQuestions
    }

    private fun calculatePercent(count: Int, size: Int) = ((count * 100.0) / size).toInt()


    private fun startGame() {
        getGameSettings()
        startTimer()
        getNextQuestion()
    }

    private fun getGameSettings() {
        gameSettings = getGameSettingsUseCase(level)
    }

    fun checkAnswer(answerIndex: Int): Boolean {
        val question = _currentQuestion.value
        if (question != null) {
            val score = _score.value ?: 0
            val isCorrect = answerIndex == question.correctAnswerIndex
            if (isCorrect) {
                _score.value = score + 1
            }
            return isCorrect
        }
        return false
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    private fun finishGame() {
        val countOfRightAnswers = score.value ?: 0
        val isWinner = gameSettings.minCountOfRightAnswers <= countOfRightAnswers
        _gameResult.value = GameResult(
            isWinner,
            countOfRightAnswers,
            countOfQuestions,
            gameSettings,
            calculatePercent(countOfRightAnswers, countOfQuestions)
        )
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        val minutes = seconds / MINUTE_IN_SECONDS
        val leftSeconds = seconds - minutes * MINUTE_IN_SECONDS
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    override fun onCleared() {
        timer?.cancel()
        super.onCleared()
    }

    private companion object {
        private const val MILLIS_IN_SECONDS = 1000L
        private const val MINUTE_IN_SECONDS = 60
    }
}