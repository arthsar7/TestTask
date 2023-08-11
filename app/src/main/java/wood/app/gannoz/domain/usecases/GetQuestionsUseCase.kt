package wood.app.gannoz.domain.usecases

import wood.app.gannoz.domain.entity.Question
import wood.app.gannoz.domain.repository.QuestionRepository

class GetQuestionsUseCase(
    private val repository: QuestionRepository
) {
    operator fun invoke(): List<Question> {
        return repository.getQuestions()
    }
}