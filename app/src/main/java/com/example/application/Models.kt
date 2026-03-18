package com.example.application

// 1. Usuarios (Users)
enum class UserRole {
    ADMIN, PROFESSOR, STUDENT
}

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = UserRole.STUDENT.name, // Por defecto alumno
    val enrolledSubjectIds: List<String> = emptyList() // Materias a las que está inscrito/asociado
)

// 2. Materias (Subjects)
data class Subject(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val section: String = "", // e.g. "A", "B", "101"
    val professorId: String = "",
    val studentIds: List<String> = emptyList()
)

// 3. Horarios (Schedules)
data class Schedule(
    val id: String = "",
    val subjectId: String = "",
    val dayOfWeek: Int = 1, // 1 = Lunes, 7 = Domingo
    val startTime: String = "", // e.g. "08:00"
    val endTime: String = "", // e.g. "10:00"
    val classroom: String = ""
)

// 4. Calificaciones (Grades)
data class Grade(
    val id: String = "",
    val studentId: String = "",
    val subjectId: String = "",
    val partial1: Double = 0.0,
    val partial2: Double = 0.0,
    val partial3: Double = 0.0,
    val partial4: Double = 0.0,
    val remarks: String = ""
) {
    // Computed property (ignored by Firestore if no getter annotations, but useful locally)
    val finalScore: Double
        get() = (partial1 + partial2 + partial3 + partial4) / 4.0
}

// 5. Asistencias (Attendances) - útil para el escaneo de QR
data class Attendance(
    val id: String = "",
    val studentId: String = "",
    val subjectId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PRESENT" // PRESENT, ABSENT, LATE
)
