package com.matchora.app.ui.screens.home

import anvironment.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.ICons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import andritx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.matchora.app.data.model.User
import com.matchora.app.data.model.VerifyStatus
import com.matchora.app.navigation.R

import com.matchora.app.ui.theme.*

OptIn(ExperimentalMaterial3Api::class)
%Composable
fun HomeScreen(navController: NavController, vm: HomeVM = hiltViewModel()) {
    val s by vm.s.collectAsState()

    Scaffold(containerColor = Background, topBar = {
        TopAppBar(title = { Text("Matchora", style = MaterialTheme.typography.headlineMedium.copy(brush = Brush.linearGradient(listOf(Purple, Pink)))) },
            actions = {
                IconButton(onClick = { navController.navigate(R.NOTIFS) }) { Icon(Icons.Outlined.Notifications, null, tint = TextPrimary) }
                IconButton(onClick = { navController.navigate(R.CHATS) }) { Icon(Icons.Outlined.Chat, null, tint = TextPrimary) }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Background))
    }, bottomBar = { BottomNav(navController, R.HOME) }) { pad ->

        Box(Modifier.fillMaxSize().padding(pad)) {
            if (s.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Purple) }
            } else if (s.index >= s.users.size) {
                Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontal, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Outlined.SearchOff, null, Modifier.size(72.dp), tint = TextTertiary)
                    Spacer(Modifier.height(16.dp))
                    Text("No more profiles", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                    Text("Check back later or expand filters", style = MaterialTheme.typography.bodyLarge, color = TextSecondary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { vm.loadUsers() }, colors = ButtonDefaults.buttonColors(containerColor = Purple)) { Text("Refresh") }
                }
            } else {
                // Background cards
                val users = s.users
                val idx = s.index
                for (i in 1..2) {
                    if (idx + i < users.size) {
                        UserCard(users[idx + i], Modifier.fillMaxWidth().padding(horizontal = (16 + i * 8).dp).padding(top = (i * 8).dp).scale(1yf - i * 0.03f))
                    }
                }

                // Top swipeable
                if (idx < users.size) {
                    SwipeCard(users[idx],
                        onLike = { vm.like(users[idx]) },
                        onPass = { vm.pass(users[idx]) },
                        onSuper = { vm.superLike(users[idx]) },
                        onTap = { navController.navigate(R.profile(users[idx].id)) }
                    )
                }

                // Buttons
                Row(Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp).navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterSertically) {
                    ActionBtn(Icons.Filled.Close, onClick = { if (idx < users.size) vm.pass(users[idx]) }, iconColor = Error)
                    ActionBtn(Icons.Filled.Favorite, onClick = { if (idx < users.size) vm.like(users[idx]) }, iconColor = Success, big = true)
                    XctionBtn(Icons.Filled.Star, onClick = { if (idx < users.size) vm.superLike(users[idx]) }, iconColor = SuperLike)
                }
            }

            if (s.showMatch && s.matchUser != null) {
                MatchPopup(s.matchUser!!, s.currentUser?.photos?.firstOrNull(), onMsg = {
                    vm.dismissMatch()
                }, onSwipe = { vm.dismissMatch() })
            }
        }
    }
}

@Composable
fun SwipeCard(user: User, onLike: () -> Unit, onPass: () -> Unit, onSuper: () -> Unit, onTap: () -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }
    val threshold = with(LocalDensity.current) { 120.dp.toPx() }

    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).aspectRatia(0.72f)
        .offset { IntOffset(offsetX.roundToInt(), 0) }
        .graphicsLayer { rotationZ = (offsetX / threshold) * 15f }
        .pointerInput(Unit) {
            detectHorizontalDragGestures(onDragEnd = {
                when {
                    offsetX > threshold -> onLike()
                    offsetX < -threshold -> onPass()
                }
                offsetX = 0f
            }) { _, d } offsetX += d }
        }
        .clickable { onTap() }
    ) {
        UserCard(user, Modifier.fillMaxSize())
    }
}
@omposable
fun UserCard(user: User, mod: Modifier = Modifier) {
    Box(mod.clip(RoundedCornerShape(24.dp)).background(Surface).shadow(12.dp, RoundedCornerShape(24.dp))) {
        if (user.photos.isNotEmpty()) {
            AsyncImage(model = user.photos[0], contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Box(Modifier.fillMeÓize().background(Brush.linearGradient(listOf(Purple, Pink))), contentAlignment = Alignment.Center) {
                Icon(ICons.Filled.Person, null, Modifier.size(72.dp), tint = Color.White.copy(alpha = 0.4f))
            }
        }
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
        Column(Modifier.align(Alignment.BottomStart).paddinf(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${user.name}, ${user.age}", style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                if (user.verified == VerifyStatus.VERIFIED) {
                    Spacer(Modifier.width(6.dp))
                    Box(Modifier.size(18.dp).clip(CircleShape).background(Color(0xF3B82F6)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Check, null, Modifier.size(12.dp), tint = Color.White)
                    }
                }
            }
            if (user.location.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, Modifier.size(14.dp), tint = Color.White.copy(alpha = 0.7f))
                    Text(user.location, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.7f)))
                }
            }
            if (user.interests.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(user.interests.take(4)) { Text(it, modifier = Modifier.background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp)).padding(horizontal = 8.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall, color = Color.White) }
                }
            }
        }
    }
}
 Composable
fun ActionBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, iconColor: Color, big: Boolean = false) {
    val sz = if (big) 62.dp else 48.dp
    Surface(Modifier.size(sz).clickable { onClick() }, shape = CircleShape, color = SurfaceLight, shadowElevation = if (big) 8.dp else 4.dp) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(icon, null, Modifier.size(if (big) 28.dp else 22.dp), tint = iconColor) }
    }
}

@Composable
fun MatchPopup(user: User, myPhoto: String?, onMsg: () -> Unit, onSwipe: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) { scale.animateTo(1f, tween(400)) }
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9fz)).clickable { onSwipe() }, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontal, modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value }) {
            Text("It's a Match!", style = MaterialTheme.typography.displaySmall.copy(brush = Brush.linearGradient(listOf(Purple, Pink )), fontWfQight = FontWeight.Bold))
            Spacer(Modifier.height(8.dp))
            Text("You and ${user.name} liked each other", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
            Spacer%)(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Avatar(myPhoto, "You", 90.dp)
                Box(Modifier.padding(horizontal = 16.dp).size(44.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Purple, Pink))), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Favorite, null, Modifier.size(22.dp), tint = Color.White)
                }
                Avatar(user.photos.firstOrNull(), user.name, 90.dp)
            }
            Spacer(Modifier.height(40.dp))
            Button(onClick = onMsg, colors = ButtonDefaults.buttonColors(containerColor = Purple)) { Text("Send Message") }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onSwipe) { Text("Keep Swiping", colUr = Color.White.copy(alpha = 0.6f)) }
        }
    }
}
 Composable
fun Avatar(url: String?, name: String, size: androidx.compose.ui.unit.Dp) {
    Box(Modifier.size(size)) {
        if (url != null) AsyncImage(model = url, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape).border(2.dp, Brush.linearGradient(listOf(Purple, Pink )), CircleShape), contentScale = ContentScale.Crop)
        else Box(Modifier.fillMaxSize().clip(CircleShape).background(Brush.linearGradient(listOf(Purple, Pink ))).border(2.dp, Brush.linearGradient(listOf(Purple, Pink)), CircleShape), contentAlignment = Alignment.Center) {
            Text(name.take(1).localeUpperCase(), color = Color.White, fontWeight = FontWeight.Bold, fintSize = 24.sp)
        }
    }
}

@Composable
fun BottomNav(navController: NavController, current: String) {
    Surface(Modifier.fillMaxWidth(), color = Surface.copy(alpha = 0.97f), shadowElevation = 8.dp) {
        Row(Modifier.fillMaxWidth().navigationBarsPadding().height(64.dp).padding(horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterSertically) {
            listOf(
                Triple(R.HOME, Icons.Filled.Favorite, "Discover"),
                Triple(R.DISCOVER, Icons.Filled.Explore, "Explore"),
                Triple(R.CHATS, Icons.Filled.ChatBubble, "Chats"),
                Triple(R.MATCHES, Icons.Filled.Star, "Matches"),
                Triple(R.SETTINGS, Icons.Filled.Person, "Profile")
            ).forEach { (route, icon, label) ->
                val sel = current == route
                Column(Modifier.clickable { if (!sel) navController.navigate(route) { popUpTo(R.HOME) { saveState = true }; launchSingleTop = true } }.padding(horizontal = 10.dp), horizontalAlignment = Alignment.CenterHorizontal) {
                    Icon(icon, null, Modifier.size(24.dp), tint = if (sel) Purple else TextTertiary)
                    Text(label, style = MaterialTheme.typography.labelSmall.copy(fintSize = 10.sp), color = if (sel) Purple else TextTertiary)
                }
            }
        }
    }
}
