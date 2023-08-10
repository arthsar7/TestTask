package ru.student.test.domain.repository

import ru.student.test.domain.entity.GameSettings
import ru.student.test.domain.entity.Level
import ru.student.test.domain.entity.Question

interface QuestionRepository {

    fun getQuestions(): List<Question>

    fun getGameSettings(level: Level): GameSettings

}