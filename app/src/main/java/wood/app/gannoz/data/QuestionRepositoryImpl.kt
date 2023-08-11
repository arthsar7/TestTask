package wood.app.gannoz.data

import wood.app.gannoz.domain.entity.GameSettings
import wood.app.gannoz.domain.entity.Level
import wood.app.gannoz.domain.entity.Question
import wood.app.gannoz.domain.repository.QuestionRepository

object QuestionRepositoryImpl: QuestionRepository {

    private val dataSource = QuestionDataSource

    override fun getQuestions(): List<Question> {
        return dataSource.getQuestions()
    }

    override fun getGameSettings(level: Level): GameSettings {
        return dataSource.getGameSettings(level)
    }
}