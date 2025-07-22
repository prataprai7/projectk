package com.taskmaster.app.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Task(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: Category = Category.PERSONAL,
    val dueDate: Date? = null,
    @ServerTimestamp
    val createdAt: Date = Date(),
    @ServerTimestamp
    val updatedAt: Date = Date(),
    val userId: String = ""
)

enum class Priority(val displayName: String, val color: Long) {
    LOW("Low", 0xFF4CAF50),
    MEDIUM("Medium", 0xFFFF9800),
    HIGH("High", 0xFFF44336),
    URGENT("Urgent", 0xFF9C27B0)
}

enum class Category(val displayName: String, val icon: String) {
    PERSONAL("Personal", "person"),
    WORK("Work", "work"),
    HEALTH("Health", "favorite"),
    EDUCATION("Education", "school"),
    FINANCE("Finance", "attach_money"),
    SHOPPING("Shopping", "shopping_cart"),
    TRAVEL("Travel", "flight"),
    OTHER("Other", "category")
}