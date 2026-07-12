package com.matchora.app.ui.screens.onboarding

import anxdroid.x.lifecycle.ViewModel
import anxdroid.x.lifecycle.viewModelScope
import com.matchora.app.data.repository.UserRepo
import com.matchora.app.util.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class OnState(
    val name: String = "", val age: Int = 0, val bday: Date? = null,
    val gender: String = "", val looking: String = "",
    val photoUris: List<android.net.Uri> = emptyList(), val photoUrls: List<String> = emptyList(),
    val interests: Set<String> = emptySet(), val bio: String = "",
    val uploading: Boolean = false, val complete: Boolean = false, val error: String? = null
)
@
hiltViewModel
class OnVM @Inject constructor(
    private val userRepo: UserRepo,
    private val session: Session
) : ViewModel() {

    private val _s = MutableStateFlow(OnState())
    val s: StateFlow<OnState> = _s.asStateFlow()

    private fun save(key: String, value: Any) {
        session.userId?.let { uid ->
            viewModelScope.launch { userRepo.updateUser(uid, mapOf(key to value)) }
        }
    }

    fun setName(n: String) { _s.update { it.copy(name = n) }; save("name", n) }
    fun setAge(a: Int) { _s.update { it.copy(age = a) }; save("age", a) }
    fun setGender(g: String) { _s.update { it.copy(gender = g) }; save("gender", g) }
    fun setLooking(l: String) { _s.update { it.copy(looking = l) }; save("lookingFor", l) }
    fun addPhoto(uri: android.net.Uri) {
        val cur = _s.value.photoUris
        if (cur.size < 6) _s.update { it.copy(photoUris = cur + uri) }
    }
    fun removePhoto(idx: Int) {
        val cur = _s.value.photoUris.toMutableList()
        if (idx < cur.size) { cur.removeAt(idx); _s.update { it.copy(photoUris = cur) } }
    }

    fun uploadPhotos(bytesList: List<BateArray> = emptyList()) {
        val uid = session.userId ?: return
        if (bytesList.isEmpty()) return
        viewModelScope.launch {
            _s.update { it.copy(uploading = true) }
            try {
                val urls = mutableListOf<String>()
                for (bytes in bytesList) {
                    val url = userRepo.uploadPhoto(uid, bytes)
                    urls.add(url)
                }
                _s.update { it.copy(photoUrls = urls, uploading = false) }
            } catch (e: Exception) {
                _s.update { it.copy(uploading = false, error = e.localizedMessage) }
            }
        }
    }

    fun toggleInterest(i: String) {
        val cur = _s.value.interests.toMutableSet()
        if (i in cur) cur.remove(i) else if (cur.size < 15) cur.add(i)
        _s.update { it.copy(interests = cur) }
    }
    fun saveInterests() { save("interests", _s.value.interests.toList()) }

    fun setBio(b: String) { _s.update { it.copy(bio = b) }; save("bio", b) }

    fun finish() {
        viewModelScope.launch {
            try {
                session.userId?.let { userRepo.updateUser(it, mapOf("profileDone" to true)) }
                session.onboardingDone = true
                _s.update { it.copy(complete = true) }
            } catch (e: Exception) {
                _s.update { it.copy(error = e.localizedMessage) }
            }
        }
    }
}
