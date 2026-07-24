package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.AddCarScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AppPermissionsScreen
import com.example.ui.screens.CarDetailScreen
import com.example.ui.screens.ChatRoomScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainContainerScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.RoleSelectionScreen
import com.example.ui.screens.SellerDashboardScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MrbBackground
import com.example.ui.theme.MrbTheme
import com.example.ui.viewmodel.MainViewModel

sealed class AppScreen {
    object Splash : AppScreen()
    object Onboarding : AppScreen()
    object Login : AppScreen()
    object RoleSelection : AppScreen()
    object Main : AppScreen()
    object CarDetail : AppScreen()
    data class ChatRoom(val threadId: String) : AppScreen()
    object AdminDashboard : AppScreen()
    object SellerDashboard : AppScreen()
    object AddCar : AppScreen()
    object Permissions : AppScreen()
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MrbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MrbBackground
                ) {
                    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }

                    when (val screen = currentScreen) {
                        is AppScreen.Splash -> {
                            SplashScreen(
                                onSplashFinished = {
                                    currentScreen = AppScreen.Onboarding
                                }
                            )
                        }
                        is AppScreen.Onboarding -> {
                            OnboardingScreen(
                                onNavigateToLogin = {
                                    currentScreen = AppScreen.Login
                                }
                            )
                        }
                        is AppScreen.Login -> {
                            LoginScreen(
                                onLoginSuccess = {
                                    currentScreen = AppScreen.RoleSelection
                                }
                            )
                        }
                        is AppScreen.RoleSelection -> {
                            RoleSelectionScreen(
                                viewModel = viewModel,
                                onRoleSelected = { role ->
                                    viewModel.setUserRole(role)
                                    if (role.contains("Admin", ignoreCase = true)) {
                                        currentScreen = AppScreen.AdminDashboard
                                    } else if (role.contains("Penjual", ignoreCase = true) || role.contains("Sales", ignoreCase = true) || role.contains("Makelar", ignoreCase = true)) {
                                        currentScreen = AppScreen.SellerDashboard
                                    } else {
                                        currentScreen = AppScreen.Main
                                    }
                                },
                                onNavigateBack = {
                                    currentScreen = AppScreen.Login
                                }
                            )
                        }
                        is AppScreen.Main -> {
                            MainContainerScreen(
                                viewModel = viewModel,
                                onNavigateToDetail = { car ->
                                    viewModel.selectCar(car)
                                    currentScreen = AppScreen.CarDetail
                                },
                                onNavigateToChatRoom = { threadId ->
                                    currentScreen = AppScreen.ChatRoom(threadId)
                                },
                                onNavigateToAdmin = {
                                    currentScreen = AppScreen.AdminDashboard
                                },
                                onNavigateToPermissions = {
                                    currentScreen = AppScreen.Permissions
                                },
                                onLogout = {
                                    currentScreen = AppScreen.Login
                                }
                            )
                        }
                        is AppScreen.CarDetail -> {
                            CarDetailScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    currentScreen = AppScreen.Main
                                },
                                onNavigateToChat = {
                                    currentScreen = AppScreen.ChatRoom("thread-1")
                                }
                            )
                        }
                        is AppScreen.ChatRoom -> {
                            ChatRoomScreen(
                                threadId = screen.threadId,
                                viewModel = viewModel,
                                onNavigateBack = {
                                    currentScreen = AppScreen.Main
                                }
                            )
                        }
                        is AppScreen.AdminDashboard -> {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    currentScreen = AppScreen.Main
                                },
                                onNavigateToAddCar = {
                                    currentScreen = AppScreen.AddCar
                                }
                            )
                        }
                        is AppScreen.SellerDashboard -> {
                            SellerDashboardScreen(
                                viewModel = viewModel,
                                onNavigateToChatRoom = { threadId ->
                                    currentScreen = AppScreen.ChatRoom(threadId)
                                },
                                onLogout = {
                                    currentScreen = AppScreen.RoleSelection
                                }
                            )
                        }
                        is AppScreen.AddCar -> {
                            AddCarScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    currentScreen = AppScreen.AdminDashboard
                                }
                            )
                        }
                        is AppScreen.Permissions -> {
                            AppPermissionsScreen(
                                onNavigateBack = {
                                    currentScreen = AppScreen.Main
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

