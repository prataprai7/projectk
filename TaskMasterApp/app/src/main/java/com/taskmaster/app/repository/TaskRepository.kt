package com.taskmaster.app.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.taskmaster.app.data.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    fun getTasks(): Flow<List<Task>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val listener = db.collection("tasks")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Task::class.java)
                } ?: emptyList()
                
                trySend(tasks)
            }
        
        awaitClose { listener.remove() }
    }
    
    suspend fun addTask(task: Task): Result<String> {
        return try {
            val userId = getCurrentUserId() 
                ?: return Result.failure(Exception("User not authenticated"))
            
            val taskWithUser = task.copy(userId = userId)
            val docRef = db.collection("tasks").add(taskWithUser).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            val userId = getCurrentUserId() 
                ?: return Result.failure(Exception("User not authenticated"))
            
            if (task.userId != userId) {
                return Result.failure(Exception("Unauthorized"))
            }
            
            db.collection("tasks").document(task.id).set(task).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() 
                ?: return Result.failure(Exception("User not authenticated"))
            
            // Verify ownership before deletion
            val taskDoc = db.collection("tasks").document(taskId).get().await()
            val task = taskDoc.toObject(Task::class.java)
            
            if (task?.userId != userId) {
                return Result.failure(Exception("Unauthorized"))
            }
            
            db.collection("tasks").document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun toggleTaskCompletion(taskId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() 
                ?: return Result.failure(Exception("User not authenticated"))
            
            val taskDoc = db.collection("tasks").document(taskId).get().await()
            val task = taskDoc.toObject(Task::class.java)
                ?: return Result.failure(Exception("Task not found"))
            
            if (task.userId != userId) {
                return Result.failure(Exception("Unauthorized"))
            }
            
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            db.collection("tasks").document(taskId).set(updatedTask).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}