package wood.app.gannoz.domain.usecases

import wood.app.gannoz.domain.entity.Level
import wood.app.gannoz.domain.repository.QuestionRepository

class GetGameSettingsUseCase(
    private val repository: QuestionRepository
) {
    operator fun invoke(level: Level) = repository.getGameSettings(level)
}
