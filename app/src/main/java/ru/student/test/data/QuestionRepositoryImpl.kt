package ru.student.test.data

import ru.student.test.domain.entity.GameSettings
import ru.student.test.domain.entity.Level
import ru.student.test.domain.entity.Question
import ru.student.test.domain.repository.QuestionRepository

object QuestionRepositoryImpl: QuestionRepository {

    private val dataSource = QuestionDataSource

    override fun getQuestions(): List<Question> {
        return dataSource.getQuestions()
    }

    override fun getGameSettings(level: Level): GameSettings {
        return dataSource.getGameSettings(level)
    }
}