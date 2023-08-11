package wood.app.gannoz.presentation

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import wood.app.gannoz.R
import wood.app.gannoz.databinding.ActivityQuizResultBinding
import wood.app.gannoz.domain.entity.GameResult

class QuizResultActivity : AppCompatActivity() {
    private val gameResult: GameResult by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getParcelable(EXTRA_RESULT, GameResult::class.java) as GameResult
        } else {
            intent.extras?.getParcelable(EXTRA_RESULT)!!
        }
    }

    private val binding by lazy {
        ActivityQuizResultBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        handleBackPressed()

        binding.tvRequiredAnswers.text = String.format(
            getString(R.string.required_answers),
            gameResult.gameSettings.minCountOfRightAnswers
        )

        binding.buttonFinish.setOnClickListener {
            finish()
        }

        binding.tvScoreAnswers.text = String.format(
            getString(R.string.right_answers),
            gameResult.countOfRightAnswers
        )

        binding.scorePercentage.text = String.format(
            getString(R.string.score_percentage),
            gameResult.percent
        )
        if (gameResult.winner) {
            binding.result.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.trophy
                )
            )
            binding.winner.text = getString(R.string.win)
        }
        else {
            binding.result.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_sad
                )
            )
            binding.winner.text = getString(R.string.lose)
        }
    }

    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    companion object {
        private const val EXTRA_RESULT = "extra_result"
        fun newIntent(context: Context, gameResult: GameResult): Intent {
            return Intent(context, QuizResultActivity::class.java).apply {
                putExtra(
                    EXTRA_RESULT,
                    gameResult
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}