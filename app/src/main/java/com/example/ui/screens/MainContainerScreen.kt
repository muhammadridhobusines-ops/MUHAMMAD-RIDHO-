package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CarItem
import com.example.ui.theme.MrbBackground
import com.example.ui.theme.MrbCardBackground
import com.example.ui.theme.MrbGold
import com.example.ui.theme.MrbGoldOutline
import com.example.ui.theme.MrbTextMuted
import com.example.ui.theme.MrbTextWhite
import com.example.ui.viewmodel.MainViewModel

enum class MainTab(val title: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
    HOME("Beranda", Icons.Default.Home, Icons.Outlined.Home),
    STOCK("Stok Mobil", Icons.Default.DirectionsCar, Icons.Outlined.DirectionsCar),
    FAVORITES("Favorit", Icons.Default.Favorite, Icons.Default.FavoriteBorder),
    CHAT("Chat", Icons.Outlined.Chat, Icons.Outlined.Chat),
    PROFILE("Profil", Icons.Default.Person, Icons.Outlined.Person)
}

@Composable
fun MainContainerScreen(
    viewModel: MainViewModel,
    onNavigateToDetail: (CarItem) -> Unit,
    onNavigateToChatRoom: (String) -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    var currentTab by remember { mutableStateOf(MainTab.HOME) }
    val favoriteCars by viewModel.favoriteCars.collectAsState()

    Scaffold(
        containerColor = MrbBackground,
        bottomBar = {
            Surface(
                color = Color(0xFF121212),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent
                ) {
                    MainTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                if (tab == MainTab.FAVORITES && favoriteCars.isNotEmpty()) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = MrbGold,
                                                contentColor = Color.Black
                                            ) {
                                                Text(text = "${favoriteCars.size}")
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                            contentDescription = tab.title,
                                            tint = if (isSelected) MrbGold else MrbTextMuted
                                        )
                                    }
                                } else if (tab == MainTab.CHAT) {
                                    BadgedBox(
                                        badge = {
                                            Badge(containerColor = MrbGold) { Text("1") }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                            contentDescription = tab.title,
                                            tint = if (isSelected) MrbGold else MrbTextMuted
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                        contentDescription = tab.title,
                                        tint = if (isSelected) MrbGold else MrbTextMuted
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MrbGold else MrbTextMuted,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFF262214)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToStock = { currentTab = MainTab.STOCK },
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToChat = { currentTab = MainTab.CHAT }
                )
                MainTab.STOCK -> CarStockScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentTab = MainTab.HOME },
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToChat = { currentTab = MainTab.CHAT }
                )
                MainTab.FAVORITES -> FavoritesScreen(
                    viewModel = viewModel,
                    onNavigateToStock = { currentTab = MainTab.STOCK },
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToChat = { currentTab = MainTab.CHAT }
                )
                MainTab.CHAT -> ChatScreen(
                    viewModel = viewModel,
                    onOpenThread = { threadId -> onNavigateToChatRoom(threadId) }
                )
                MainTab.PROFILE -> ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToFavorites = { currentTab = MainTab.FAVORITES },
                    onNavigateToChat = { currentTab = MainTab.CHAT },
                    onNavigateToAdmin = onNavigateToAdmin,
                    onLogout = onLogout
                )
            }
        }
    }
}
