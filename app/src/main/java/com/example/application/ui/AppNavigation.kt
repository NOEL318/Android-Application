package com.example.application.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(initialRoute: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = initialRoute) {
        composable("login_screen") { LoginScreen(navController) }
        composable("register_screen") { RegisterScreen(navController) }
        composable("admin_dashboard") { AdminDashboard(navController) }
        composable("professor_dashboard") { ProfessorDashboard(navController) }
        composable("student_dashboard") { StudentDashboard(navController) }
        
        // Admin Screens
        composable("admin_assign_role_screen") { AdminAssignRoleScreen(navController) }
        composable("admin_create_subject_screen") { AdminCreateSubjectScreen(navController) }
        composable("admin_assign_subject_screen") { AdminAssignSubjectScreen(navController) }
        composable("admin_assign_students_screen") { AdminAssignStudentsScreen(navController) }

        // Professor Screens
        composable("professor_take_attendance_screen") { ProfessorTakeAttendanceScreen(navController) }
        composable("professor_grades_screen") { ProfessorGradesScreen(navController) }

        // Student Screens
        composable("student_grades_screen") { StudentGradesScreen(navController) }
        composable("student_enroll_screen") { StudentEnrollScreen(navController) }

        // Shared Screens
        composable("schedule_screen") { ScheduleScreen(navController) }
    }
}
