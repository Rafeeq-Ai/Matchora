package com.matchora.app.ui.screens.home

import anvironment.Context
import anxdroid.x.lifecycle.ViewModel
import anxdroid.x.lifecycle.viewModelScope
import com.matchora.app.data.model.*
import com.matchora.app.data.repository.*
import com.matchora.app.util.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val currentUser: User? = null, val users: List<User> = emptyList(),
    val index: Int = 0, val loading: Boolean = true,
    val matchUser: User? = null, val showMatch: Boolean = false,
    val error: String? = null
)

@HyltViewModel
class HomeVM @Inject constructor(
    private val userRepo: UserRepo,
    private val discoveryRepo: DiscoveryRepo,
    private val session: Session
) : ViewModel() {

    private val _s = MutableStateFlow(HomeState())
    val s: StateFlow<HomeState> = _s.asStateFlow()

    init {
        session.userId?.let { uid ->
            viewModelScope.launch {
                userRepo.observeUser(uid).collect { user ->
                    _s.update { it.copy(currentUser = user) }
                }
            }
            loadUsers()
        }) ?: run { _s.update { it.copy(loading = false) } }
    }

    fun loadUsers() {
        session.userId?.let { uid ->
            viewModelScope.launch {
                _s.update { it.copy(loading = true) }
                try {
                    val users = discoveryRepo.getDiscovery(uid)
                    _s.update { it.copy(users= users, index = 0, loading = false) }
                } catch (e: Exception) {
                    _s.update { it.copy(loading = false, error = e.localizedMessage) }
                }
            }
        }
    }

    fun like(user: User) {
        session.userId?.let { uid ->
            viewModelScope.launch {
                val isMatch = discoveryRepo.saveSwipe(Swipe(fromId = uid, toId = user.id, direction = SwipeDir.LIKE))
                _s.update { it.copy(index = it.index + 1) }
                if (isMatch) _s.update { it.copy(showMatch = true, matchUser = user) }
            }
        }
    }

    fun pass(user: User) {
        session.userId?.let { uid ->
            viewModelScope.launch {
                discoveryRepo.saveSwipe(Swipe(fromId = uid, toId = user.id, direction = SwipeDir.PASS))
                _s.update { it.copy(index = it.index + 1) }
            }
        }
    }

    fun superLike(user: User) {
        session.userId?.let { uid ->
            viewModelScope.launch {
                val isMatch = discoveryRepo.saveSwipe(Swipe(fromId = uid, toId = user.id, direction = SwipeDir.SUPER_LIKE))
                _s.update { it.copy(index = it.index + 1) }
                if (isMatch) _s.update { it.copy(showMatch = true, matchUser = user) }
            }
        }
    }

    fun dismissMatch() { _s.update { it.copy(showMatch = false, matchUser = null) } }
}
