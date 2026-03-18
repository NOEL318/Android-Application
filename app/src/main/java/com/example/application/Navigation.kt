package com.example.application

// Ejemplo básico de ruteo
sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object AdminDashboard : Screen("admin_dashboard")
    object ProfessorDashboard : Screen("professor_dashboard")
    object StudentDashboard : Screen("student_dashboard")
}
