package ru.student.test.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameSettings(
    val minCountOfRightAnswers: Int,
    val gameTimeInSeconds: Int
) : Parcelable