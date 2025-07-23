package com.taskmaster.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaster.app.data.Task
import com.taskmaster.app.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()
    
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadTasks()
    }
    
    private fun loadTasks() {
        viewModelScope.launch {
            repository.getTasks().collect { taskList ->
                _tasks.value = taskList
            }
        }
    }
    
    fun addTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addTask(task).fold(
                onSuccess = {
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
            _isLoading.value = false
        }
    }
    
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task).fold(
                onSuccess = {
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
        }
    }
    
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId).fold(
                onSuccess = {
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
        }
    }
    
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId).fold(
                onSuccess = {
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun getTasksByCategory(category: String): List<Task> {
        return _tasks.value.filter { it.category.displayName == category }
    }
    
    fun getCompletedTasks(): List<Task> {
        return _tasks.value.filter { it.isCompleted }
    }
    
    fun getPendingTasks(): List<Task> {
        return _tasks.value.filter { !it.isCompleted }
    }
}