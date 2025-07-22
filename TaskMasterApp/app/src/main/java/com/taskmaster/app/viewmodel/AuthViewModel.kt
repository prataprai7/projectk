package com.taskmaster.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }
    
    fun signInAnonymously() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = auth.signInAnonymously().await()
                _currentUser.value = result.user
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _currentUser.value = result.user
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun createUserWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                _currentUser.value = result.user
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }
    
    fun clearError() {
        _error.value = null
    }
    
    val isUserSignedIn: Boolean
        get() = _currentUser.value != null
}