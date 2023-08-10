package ru.student.test.domain.usecases

import ru.student.test.domain.entity.Question
import ru.student.test.domain.repository.QuestionRepository

class GetQuestionsUseCase(
    private val repository: QuestionRepository
) {
    operator fun invoke(): List<Question> {
        return repository.getQuestions()
    }
}