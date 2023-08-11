package wood.app.gannoz.domain.entity

data class Question(
    val id: Int,
    val question: String,
    val answers: List<String>,
    val correctAnswerIndex: Int,
    val imageId: Int
)
