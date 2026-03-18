package com.example.application

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import android.util.Log

class AcademicRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // --- Funciones de Administrador / General ---
    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val usersSnapshot = firestore.collection("users").get().await()
            Result.success(usersSnapshot.toObjects(User::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllSubjects(): Result<List<Subject>> {
        return try {
            val subjectsSnapshot = firestore.collection("subjects").get().await()
            Result.success(subjectsSnapshot.toObjects(Subject::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignRole(userId: String, newRole: UserRole): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update("role", newRole.name).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSubjectAndSchedule(subject: Subject, schedule: Schedule): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val subjectRef = firestore.collection("subjects").document()
                val newSubject = subject.copy(id = subjectRef.id)
                transaction.set(subjectRef, newSubject)

                val scheduleRef = firestore.collection("schedules").document()
                val newSchedule = schedule.copy(id = scheduleRef.id, subjectId = subjectRef.id)
                transaction.set(scheduleRef, newSchedule)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignSubjectToProfessor(subjectId: String, professorId: String): Result<Unit> {
        return try {
             firestore.collection("subjects").document(subjectId)
                .update("professorId", professorId).await()
            
             val userRef = firestore.collection("users").document(professorId)
             firestore.runTransaction { transaction ->
                 val userSnapshot = transaction.get(userRef)
                 if(userSnapshot.exists()) {
                     val currentSubjects = userSnapshot.get("enrolledSubjectIds") as? List<String> ?: emptyList()
                     if (!currentSubjects.contains(subjectId)) {
                         transaction.update(userRef, "enrolledSubjectIds", currentSubjects + subjectId)
                     }
                 }
             }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun enrollStudentToSubject(studentId: String, subjectId: String): Result<Unit> {
        return try {
            val subjectRef = firestore.collection("subjects").document(subjectId)
            val userRef = firestore.collection("users").document(studentId)

            // Utilizamos FieldValue.arrayUnion() que es más seguro y atómico para modificar arreglos en Firestore
            subjectRef.update("studentIds", FieldValue.arrayUnion(studentId)).await()
            userRef.update("enrolledSubjectIds", FieldValue.arrayUnion(subjectId)).await()
            
            Log.d("Enrollment", "Enrolled student $studentId to $subjectId successfully via arrayUnion.")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Enrollment", "Error enrolling student $studentId to $subjectId", e)
            Result.failure(e)
        }
    }

    // --- Funciones de Profesor ---

    suspend fun recordAttendance(studentId: String, subjectId: String): Result<Unit> {
        return try {
            val newAttendance = Attendance(
                id = firestore.collection("attendances").document().id,
                studentId = studentId,
                subjectId = subjectId
            )
            firestore.collection("attendances").document(newAttendance.id).set(newAttendance).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignGrade(grade: Grade): Result<Unit> {
        return try {
             val gradeRef = firestore.collection("grades").document(grade.id.ifEmpty { firestore.collection("grades").document().id })
             val newGrade = grade.copy(id = gradeRef.id)
            gradeRef.set(newGrade).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfessorSchedule(professorId: String): Result<List<Schedule>> {
        return try {
             val subjectsSnapshot = firestore.collection("subjects")
                 .whereEqualTo("professorId", professorId)
                 .get().await()
             
             val subjectIds = subjectsSnapshot.documents.map { it.id }
             if (subjectIds.isEmpty()) return Result.success(emptyList())

             val schedulesSnapshot = firestore.collection("schedules")
                 .whereIn("subjectId", subjectIds)
                 .get().await()

             val schedules = schedulesSnapshot.toObjects(Schedule::class.java)
             Result.success(schedules)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Funciones de Alumno ---
    
    suspend fun getStudentGrades(studentId: String): Result<List<Grade>> {
        return try {
             val gradesSnapshot = firestore.collection("grades")
                 .whereEqualTo("studentId", studentId)
                 .get().await()
             
             Result.success(gradesSnapshot.toObjects(Grade::class.java))
        } catch (e: Exception) {
             Result.failure(e)
        }
    }

    suspend fun getStudentSchedule(studentId: String): Result<List<Schedule>> {
        return try {
            val studentSnapshot = firestore.collection("users").document(studentId).get().await()
            val student = studentSnapshot.toObject(User::class.java)
            val enrolledSubjectIds = student?.enrolledSubjectIds ?: emptyList()
            
            if (enrolledSubjectIds.isEmpty()) return Result.success(emptyList())

            val chunkedSubjectIds = enrolledSubjectIds.chunked(10)
            val allSchedules = mutableListOf<Schedule>()

            for (chunk in chunkedSubjectIds) {
                val schedulesSnapshot = firestore.collection("schedules")
                    .whereIn("subjectId", chunk)
                    .get().await()
                allSchedules.addAll(schedulesSnapshot.toObjects(Schedule::class.java))
            }

            Result.success(allSchedules)

        } catch (e: Exception) {
             Result.failure(e)
        }
    }

    suspend fun getEnrolledSubjects(studentId: String): Result<List<Subject>> {
        return try {
            val studentSnapshot = firestore.collection("users").document(studentId).get().await()
            val student = studentSnapshot.toObject(User::class.java)
            val enrolledSubjectIds = student?.enrolledSubjectIds ?: emptyList()

            if (enrolledSubjectIds.isEmpty()) return Result.success(emptyList())

            // Firestore "in" queries only support up to 10 items. Workaround using chunking.
            val chunkedIds = enrolledSubjectIds.chunked(10)
            val allSubjects = mutableListOf<Subject>()

            for(chunk in chunkedIds) {
                val subjectsSnapshot = firestore.collection("subjects")
                    .whereIn(FieldPath.documentId(), chunk)
                    .get().await()
                allSubjects.addAll(subjectsSnapshot.toObjects(Subject::class.java))
            }

            Result.success(allSubjects)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
