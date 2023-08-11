package wood.app.gannoz.domain.repository

import wood.app.gannoz.domain.entity.GameSettings
import wood.app.gannoz.domain.entity.Level
import wood.app.gannoz.domain.entity.Question

interface QuestionRepository {

    fun getQuestions(): List<Question>

    fun getGameSettings(level: Level): GameSettings

}