package ru.student.test.domain.usecases

import ru.student.test.domain.entity.Level
import ru.student.test.domain.repository.QuestionRepository

class GetGameSettingsUseCase(
    private val repository: QuestionRepository
) {
    operator fun invoke(level: Level) = repository.getGameSettings(level)
}
