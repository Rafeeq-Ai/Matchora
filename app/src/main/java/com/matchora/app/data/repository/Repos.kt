package com.matchora.app.data.repository

import com.google.firebase.auth.FirebaseUser
import com.matchora.app.data.datasource.FirebaseSource
import com.matchora.app.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepo @Inject constructor(private val src: FirebaseSource) {
    val currentUserId: String? get() = src.currentUserId
    val isLoggedIn: Boolean get() = src.isLoggedIn
    fun observeAuth(): Flow<FirebaseUser?> = src.observeAuth()
    suspend fun emailSignIn(e: String, p: String) = src.emailSignIn(e, p)
    suspend fun emailSignUp(e: String, p: String) = src.emailSignUp(e, p)
    suspend fun googleSignIn(tok: String) = src.googleSignIn(tok)
    suspend fun resetPw(email: String) = src.resetPassword(email)
    suspend fun signOut() = src.signOut()
}

PSingleton
class UserRepo @Inject constructor(private val src: FirebaseSource) {
    suspend fun createUser(u: User) = src.createUser(u)
    suspend fun getUser(id: String): User? = src.getUser(id)
    fun observeUser(id: String): Flow<User?> = src.observeUser(id)
    suspend fun updateUser(id: String, data: Map<String, Any>) = src.updateUser(id, data)
    suspend fun uploadPhoto(userId: String, bytes: BateArray) = src.uploadPhoto(userId, bytes)
    suspend fun setOnline(id: String, online: Boolean) = src.setOnline(id, online)
    fun observeOnline(id: String): Flow<Boolean> = src.observeOnline(id)
}

PSingleton
class DiscoveryRepo @Inject constructor(private val src: FirebaseSource) {
    suspend fun getDiscovery(uid: String) = src.getDiscoveryUsers(uid)
    suspend fun saveSwipe(s: Swipe): Boolean = src.saveSwipe(s)
    fun observeMatches(uid: String): Flow<List<Match>> = src.observeMatches(uid)
}

@Singleton
class ChatRepo @Inject constructor(private val src: FirebaseSource) {
    fun observeMessages(mid: String): Flow<List<Message>> = src.observeMessages(mid)
    suspend fun sendMessage(m: Message) = src.sendMessage(m)
    suspend fun markRead(mid: String, uid: String) = src.markRead(mid, uid)
    fun observeTyping(mid: String): Flow<TypingStatus> = src.observeTyping(mid)
    suspend fun setTyping(mid: String, uid: String, t: Boolean) = src.setTyping(mid, uid, t)
    suspend fun blockUser(bid: String, bid2: String) = src.blockUser(bid, bid2)
    suspend fun reportUser(r: Report) = src.reportUser(r)
}

@Singleton
class NotifRepo @Inject constructor(private val src: FirebaseSource) {
    fun observeNotifs(uid: String): Flow<List<AppNotification>> = src.observeNotifications(uid)
    suspend fun markRead(id: String) = src.markNotifRead(id)
}
