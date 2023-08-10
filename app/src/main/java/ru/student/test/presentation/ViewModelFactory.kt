package ru.student.test.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.student.test.domain.entity.Level

class ViewModelFactory(
    private val level: Level
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionViewModel::class.java)) {
            return QuestionViewModel(level) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}