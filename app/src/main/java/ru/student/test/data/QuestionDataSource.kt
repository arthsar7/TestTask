package ru.student.test.data

import ru.student.test.R
import ru.student.test.domain.entity.GameSettings
import ru.student.test.domain.entity.Level
import ru.student.test.domain.entity.Question

object QuestionDataSource {
    fun getQuestions(): List<Question> {

        return  listOf(
            Question(
                id = 1,
                question = "Which country won the most number of Olympic gold medals in ice hockey?",
                answers = listOf("Canada", "United States", "Soviet Union", "Sweden"),
                correctAnswerIndex = 0,
                imageId = R.drawable.hockey_image_1
            ),
            Question(
                id = 2,
                question = "What is the maximum number of players allowed on the ice for a team during a game?",
                answers = listOf("5", "6", "7", "8"),
                correctAnswerIndex = 1,
                imageId = R.drawable.hockey_image_2
            ),
            Question(
                id = 3,
                question = "Which NHL team has won the most Stanley Cup championships?",
                answers = listOf("Montreal Canadiens", "Toronto Maple Leafs", "Detroit Red Wings", "Boston Bruins"),
                correctAnswerIndex = 0,
                imageId = R.drawable.hockey_image_3
            ),
            Question(
                id = 4,
                question = "Which player holds the record for the most career goals in the NHL?",
                answers = listOf("Wayne Gretzky", "Gordie Howe", "Bobby Orr", "Mario Lemieux"),
                correctAnswerIndex = 0,
                imageId = R.drawable.hockey_image_4
            ),
            Question(
                id = 5,
                question = "Which team won the first ever Stanley Cup in 1893?",
                answers = listOf("Montreal Hockey Club", "Ottawa Hockey Club", "Toronto Hockey Club", "Quebec Hockey Club"),
                correctAnswerIndex = 0,
                imageId = R.drawable.hockey_image_5
            ),
            Question(
                id = 6,
                question = "What is the standard size of an NHL hockey rink?",
                answers = listOf("200 feet by 85 feet", "200 feet by 100 feet", "180 feet by 75 feet", "180 feet by 100 feet"),
                correctAnswerIndex = 0,
                imageId = R.drawable.hockey_image_6
            ),
            Question(
                id = 7,
                question = "Which player holds the record for the most points in a single NHL season?",
                answers = listOf("Wayne Gretzky", "Mario Lemieux", "Bobby Orr", "Jaromir Jagr"),
                correctAnswerIndex = 0,
                imageId = R.drawable.hockey_image_7
            ),
            Question(
                id = 8,
                question = "Which team has won the most number of Stanley Cups in a row?",
                answers = listOf("Montreal Canadiens", "Edmonton Oilers", "New York Islanders", "Detroit Red Wings"),
                correctAnswerIndex = 2,
                imageId = R.drawable.hockey_image_8
            ),
            Question(
                id = 9,
                question = "What is the penalty called when a player deliberately shoots the puck out of play from their defensive zone?",
                answers = listOf("Icing", "Offside", "Delay of game", "Tripping"),
                correctAnswerIndex = 2,
                imageId = R.drawable.hockey_image_9
            ),
            Question(
                id = 10,
                question = "Who is the all-time leading scorer for the Pittsburgh Penguins?",
                answers = listOf("Sidney Crosby", "Mario Lemieux", "Evgeni Malkin", "Jaromir Jagr"),
                correctAnswerIndex = 1,
                imageId = R.drawable.hockey_image_10
            ),
            Question(
                id = 11,
                question = "Which player won the most Conn Smythe Trophies as the NHL playoffs MVP?",
                answers = listOf("Wayne Gretzky", "Mario Lemieux", "Patrick Roy", "Bobby Orr"),
                correctAnswerIndex = 2,
                imageId = R.drawable.hockey_image_11
            ),
            Question(
                id = 12,
                question = "Which team holds the record for the longest unbeaten streak in NHL history?",
                answers = listOf("Chicago Blackhawks", "Montreal Canadiens", "Pittsburgh Penguins", "Boston Bruins"),
                correctAnswerIndex = 1,
                imageId = R.drawable.hockey_image_12
            ),
            Question(
                id = 13,
                question = "Who is the youngest player to score 50 goals in an NHL season?",
                answers = listOf("Wayne Gretzky", "Sidney Crosby", "Alex Ovechkin", "Connor McDavid"),
                correctAnswerIndex = 0,
                imageId = R.drawable.hockey_image_13
            ),
            Question(
                id = 14,
                question = "Which team won the first ever NHL game in 1917?",
                answers = listOf("Montreal Canadiens", "Toronto Arenas", "Ottawa Senators", "Quebec Bulldogs"),
                correctAnswerIndex = 1,
                imageId = R.drawable.hockey_image_14
            ),
            Question(
                id = 15,
                question = "What is the name of the trophy awarded to the NHL's regular season MVP?",
                answers = listOf("Hart Trophy", "Vezina Trophy", "Norris Trophy", "Calder Trophy"),
                correctAnswerIndex = 0,
                imageId = R.drawable.hockey_image_15
            )
        )
    }
    fun getGameSettings(level: Level): GameSettings {
        return when (level) {

            Level.EASY -> GameSettings(
                7,
                40,
            )
            Level.NORMAL -> GameSettings(
                10,
                30
            )
            Level.HARD -> GameSettings(
                15,
                20
            )
        }
    }
}