package com.dev.makautmate.domain.model

data class Notice(
    val id: String = "",
    val title: String,
    val description: String,
    val date: String
)

data class MarkRequest(
    val subject: String,
    val marks: Int,
    val semester: Int
)

data class ActivityRequest(
    val type: String,
    val time: String // ISO 8601 string or similar
)

data class GradeCardRequest(
    val studentId: String
)

data class GradeCardResponse(
    val studentId: String,
    val name: String,
    val semester: Int,
    val gpa: Double,
    val subjects: List<SubjectGrade>
)

data class SubjectGrade(
    val subject: String,
    val grade: String,
    val points: Int
)
