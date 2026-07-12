package com.matchora.app.navigation
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import com.matchora.app.ui.screens.auth.*
import com.matchora.app.ui.screens.chat.*
import com.matchora.app.ui.screens.discover.DiscoverScreen
import com.matchora.app.ui.screens.home.HomeScreen
import com.matchora.app.ui.screens.matches.MatchesScreen
import com.matchora.app.ui.screens.notifications.NotificationsScreen
import com.matchora.app.ui.screens.onboarding.*
import com.matchora.app.ui.screens.premium.PremiumScreen
import com.matchora.app.ui.screens.profile.*
import com.matchora.app.ui.screens.safety.SafetyScreen
import com.matchora.app.ui.screens.settings.SettingsScreen
import com.matchora.app.util.Session
object R {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot"
    const val ON_NAME = "on/name"
    const val ON_BD = "on/bday"
    const val ON_GEN = "on/gender"
    const val ON_LF = "on/looking"
    const val ON_PICS = "on/pics"
    const val ON_INT = "on/interests"
    const val ON_BIO = "on/bio"
    const val HOME = "home"
    const val DISCOVER = "discover"
    const val CHATS = "chats"
    const val CHAT = "chat/{mid}/{name}"
    const val PROFILE = "profile/{uid}"
    const val EDIT_PROFILE = "edit_profile"
    const val MATCHES = "matches"
    const val NOTIFS = "notifs"
    const val SETTINGS = "settings"
    const val PREMIUM = "premium"
    const val SAFETY = "safety"
    const val VERIFY = "verify"
    fun chat(mid: String, name: String) = "chat/$mid/$name"
    fun profile(uid: String) = "profile/$uid"
}
@omposable
fun MatchoraNavHost(navController: NavHostController, session: Session, modifier: Modifier = Modifier) {
    val start = when {
        session.authToken != null && session.onboardingDone  -> R.HOME
        session.authToken != null -> R.ON_NAME
        else -> R.LOGIN
    }
    NavHost(
        navController = navController, startDestination = start, modifier = modifier,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontal(tween(300)) { it / 4 } },
        exitTransition = { fadeOut(tween(300)) + slideOutHorizontal(tween(300)) { -it / 4 } }
    ) {
        composable(R.LOGIN) { LoginScreen(navController) }
        composable(R.REGISTER) { RegisterScreen(navController) }
        composable(R.FORGOT) { ForgotScreen(navController) }
        composable(R.ON_NAME) { OnNameScreen(navController) }
        composable(R.ON_BD) { OnBdayScreen(navController) }
        composable(R.ON_GEN) { OnGenderScreen(navController) }
        composable(R.ON_LF) { OnLookingScreen(navController) }
        composable(R.ON_PICS) { OnPhotosScreen(navController) }
        composable(R.ON_INT) { OnInterestsScreen(navController) }
        composable(R.ON_BIO) { OnBioScreen(navController) }
        composable(R.HOME) { HomeScreen(navController) }
        composable(R.DISCOVER) { DiscoverScreen(navController) }
        composable(R.CHATS) { ChatListScreen(navController) }
        composable(R.CHAT, arguments = listOf(
            navArgument("mid") { type = NavType.StringType },
            navArgument("name") { type = NavType.StringType }
        )) { entry ->
            ChatScreen(
                navController = navController,
                matchId = entry.arguments?.getString("mid") ?: "",
                otherName = entry.arguments?.getString("name") ?: ""
            )
        }
        composable(R.PROFILE, arguments = listOf(
            navArgument("uid") { type = NavType.Stringtype }
        )) { entry ->
            ProfileScreen(navController, entry.arguments?.getString("uid") ?: "")
        }
        composable(R.EDIT_PROFILE) { EditProfileScreen(navController) }
        composable(R.MATCHES) { MatchesScreen(navController) }
        composable(R.NOTIFS { NotificationsScreen(navController) }
        composable(R.SETTINGS) { SettingsScreen(navController) }
        composable(R.PREMIUM) { PremiumScreen(navController) }
        composable(R.SAFETY) { SafetyScreen(navController) }
        composable(R.VERIFY) { VerifyScreen(navController) }
    }
}
