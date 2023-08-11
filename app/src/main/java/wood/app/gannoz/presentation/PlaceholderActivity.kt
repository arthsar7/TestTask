package wood.app.gannoz.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import wood.app.gannoz.R
import wood.app.gannoz.databinding.ActivityPlaceholderBinding

class PlaceholderActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPlaceholderBinding.inflate(layoutInflater)
    }
    private var viewModelFactory: ViewModelProvider.Factory? = null

    private val questionViewModel by lazy {
        ViewModelProvider(this, viewModelFactory!!)[QuestionViewModel::class.java]
    }

    private var isAnswered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        showLevelChooseDialog()

        binding.nextQuestionBtn.setOnClickListener {
            if (isAnswered) {
                setNextQuestion()
            }
            else {
                answerQuestion()
            }
        }
    }

    private fun setNextQuestion() {
        isAnswered = false
        questionViewModel.getNextQuestion()
        binding.radiogrp.children.forEach {
            val radioButton = it as RadioButton
            radioButton.background = ContextCompat.getDrawable(
                this,
                R.drawable.button_background
            )
            radioButton.isClickable = true
        }
        binding.nextQuestionBtn.text = getString(R.string.submit)
        binding.radioButton1.isChecked = true
    }

    private fun answerQuestion() {
        binding.radiogrp.isClickable = false
        binding.radiogrp.children.forEachIndexed { index, view ->
            val radioButton = view as RadioButton
            if (radioButton.isChecked) {
                if (questionViewModel.checkAnswer(index)) {
                    radioButton.setBackgroundColor(Color.GREEN)
                }
                else {
                    radioButton.setBackgroundColor(Color.RED)
                }
            }
            radioButton.isClickable = false
        }
        isAnswered = true
        binding.nextQuestionBtn.text = getString(R.string.next_question)
    }

    private fun showLevelChooseDialog() {
        val levelChooseDialog = LevelChooseDialog()
        levelChooseDialog.onItemClickListener = { level ->
            viewModelFactory = ViewModelFactory(level)
            levelChooseDialog.dismiss()
            observeViewModel()

            binding.nextQuestionBtn.visibility = View.VISIBLE
            binding.radiogrp.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }
        levelChooseDialog.show(supportFragmentManager, "LevelChooseDialog")
    }

    private fun observeViewModel() {
        questionViewModel.currentQuestion.observe(this) {
            binding.imageView.setImageDrawable(
                ContextCompat.getDrawable(this, it.imageId)
            )
            binding.tvQuestion.text = it.question
            binding.radiogrp.children.forEachIndexed { index, view ->
                (view as RadioButton).text = it.answers[index]
            }
        }

        questionViewModel.questionNumber.observe(this) { questionNumber ->
            binding.tvQuestionNumber.text = String.format(
                getString(R.string.question_number),
                questionNumber,
                questionViewModel.size
            )
        }

        questionViewModel.score.observe(this) { score ->
            binding.txtPlayScore.text = String.format(
                getString(R.string.question_score),
                score
            )
        }

        questionViewModel.formattedTime.observe(this) {
            binding.quizTimer.text = it
        }

        questionViewModel.percentProgress.observe(this) {
            binding.progressBar.progress = it
        }

        questionViewModel.gameResult.observe(this) {
            startActivity(QuizResultActivity.newIntent(this, it))
        }
    }
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PlaceholderActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}