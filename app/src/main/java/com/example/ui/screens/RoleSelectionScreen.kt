package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MrbBackground
import com.example.ui.theme.MrbGold
import com.example.ui.theme.MrbGoldOutline
import com.example.ui.theme.MrbTextMuted
import com.example.ui.theme.MrbTextWhite
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

enum class SelectedRoleType {
    ADMIN,
    SELLER,
    BUYER
}

private sealed class RoleFlowStep {
    object SelectRole : RoleFlowStep()
    data class PinVerification(val roleType: SelectedRoleType) : RoleFlowStep()
    data class Loading(val roleType: SelectedRoleType, val message: String) : RoleFlowStep()
}

@Composable
fun RoleSelectionScreen(
    viewModel: MainViewModel,
    onRoleSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var currentStep by remember { mutableStateOf<RoleFlowStep>(RoleFlowStep.SelectRole) }

    when (val step = currentStep) {
        is RoleFlowStep.SelectRole -> {
            RoleCardSelectionView(
                onRoleChosen = { roleType ->
                    when (roleType) {
                        SelectedRoleType.ADMIN -> {
                            currentStep = RoleFlowStep.PinVerification(SelectedRoleType.ADMIN)
                        }
                        SelectedRoleType.SELLER -> {
                            currentStep = RoleFlowStep.PinVerification(SelectedRoleType.SELLER)
                        }
                        SelectedRoleType.BUYER -> {
                            currentStep = RoleFlowStep.Loading(
                                SelectedRoleType.BUYER,
                                "Memuat Dashboard Pembeli..."
                            )
                        }
                    }
                },
                onNavigateBack = onNavigateBack
            )
        }

        is RoleFlowStep.PinVerification -> {
            PinVerificationView(
                roleType = step.roleType,
                viewModel = viewModel,
                onSuccessPin = { roleType ->
                    val (roleName, loadingMsg) = when (roleType) {
                        SelectedRoleType.ADMIN -> "Admin" to "Memuat Dashboard Admin..."
                        SelectedRoleType.SELLER -> "Penjual / Sales / Makelar" to "Memuat Dashboard..."
                        SelectedRoleType.BUYER -> "Pembeli (Anggota)" to "Memuat Dashboard Pembeli..."
                    }
                    currentStep = RoleFlowStep.Loading(roleType, loadingMsg)
                },
                onBack = {
                    currentStep = RoleFlowStep.SelectRole
                }
            )
        }

        is RoleFlowStep.Loading -> {
            LoadingRoleView(
                message = step.message,
                onFinish = {
                    val roleString = when (step.roleType) {
                        SelectedRoleType.ADMIN -> "Admin"
                        SelectedRoleType.SELLER -> "Penjual / Sales / Makelar"
                        SelectedRoleType.BUYER -> "Pembeli (Anggota)"
                    }
                    onRoleSelected(roleString)
                }
            )
        }
    }
}

@Composable
private fun RoleCardSelectionView(
    onRoleChosen: (SelectedRoleType) -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MrbBackground)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Top Bar
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Kembali",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Pilih Peran Akun",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MrbTextWhite,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Subtitle
            Text(
                text = "Pilih jenis akun Anda",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MrbTextWhite,
                textAlign = TextAlign.Center
            )
            Text(
                text = "untuk melanjutkan",
                fontSize = 14.sp,
                color = MrbTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Option 1: Admin
            RoleCard(
                title = "Admin",
                subtitle = "Kelola seluruh sistem",
                iconType = RoleIconType.ADMIN,
                onClick = { onRoleChosen(SelectedRoleType.ADMIN) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 2: Penjual / Sales / Makelar
            RoleCard(
                title = "Penjual / Sales / Makelar",
                subtitle = "Kelola unit & transaksi",
                iconType = RoleIconType.SELLER,
                onClick = { onRoleChosen(SelectedRoleType.SELLER) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 3: Pembeli (Anggota)
            RoleCard(
                title = "Pembeli (Anggota)",
                subtitle = "Cari & beli mobil impian",
                iconType = RoleIconType.BUYER,
                onClick = { onRoleChosen(SelectedRoleType.BUYER) }
            )
        }
    }
}

@Composable
private fun PinVerificationView(
    roleType: SelectedRoleType,
    viewModel: MainViewModel,
    onSuccessPin: (SelectedRoleType) -> Unit,
    onBack: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val (title, subtitle, pinHint) = when (roleType) {
        SelectedRoleType.ADMIN -> Triple(
            "Masukkan PIN Admin",
            "Masukkan PIN untuk melanjutkan ke Dashboard Admin.",
            "💡 Default PIN Admin: 20050307"
        )
        else -> Triple(
            "Masukkan PIN Penjual / Sales",
            "Masukkan PIN 8-digit yang terdaftar.",
            "💡 Contoh PIN Sales: 12345678 atau 84729156"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MrbBackground)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Top Bar
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Kembali",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = if (roleType == SelectedRoleType.ADMIN) "Verifikasi PIN Admin" else "Verifikasi PIN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MrbTextWhite,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logo MRB Top
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MrbGold,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MrbGoldOutline),
                shadowElevation = 8.dp,
                modifier = Modifier.size(80.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_mrb_logo_badge_1784874215022),
                    contentDescription = "MRB Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MrbTextWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MrbTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // OutlinedTextField PIN (8-digit)
            OutlinedTextField(
                value = pinInput,
                onValueChange = { newValue ->
                    if (newValue.length <= 8 && newValue.all { it.isDigit() }) {
                        pinInput = newValue
                        errorMessage = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                label = { Text("PIN (8 Digit)", color = MrbTextMuted) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPinVisible = !isPinVisible }) {
                        Icon(
                            imageVector = if (isPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (isPinVisible) "Sembunyikan PIN" else "Tampilkan PIN",
                            tint = MrbGold
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MrbGold,
                    unfocusedBorderColor = Color(0xFF444444),
                    cursorColor = MrbGold,
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedLabelColor = MrbGold,
                    unfocusedLabelColor = MrbTextMuted
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // PIN Hint Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E1E28),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline.copy(alpha = 0.5f)),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = pinHint,
                    fontSize = 12.sp,
                    color = MrbGold,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Error Message
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFFF5252),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button: Masuk
            Button(
                onClick = {
                    when (roleType) {
                        SelectedRoleType.ADMIN -> {
                            val isValid = viewModel.validateAdminPin(pinInput)
                            if (isValid) {
                                errorMessage = null
                                onSuccessPin(roleType)
                            } else {
                                errorMessage = "PIN Admin tidak valid."
                            }
                        }

                        SelectedRoleType.SELLER -> {
                            val isValid = viewModel.validateSellerPin(pinInput)
                            if (isValid) {
                                errorMessage = null
                                onSuccessPin(roleType)
                            } else {
                                errorMessage = "PIN tidak valid."
                            }
                        }

                        SelectedRoleType.BUYER -> {
                            onSuccessPin(roleType)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MrbGold,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Masuk",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LoadingRoleView(
    message: String,
    onFinish: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1500) // 1.5 seconds loading display
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MrbBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // MRB Logo Badge
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MrbGold,
                border = androidx.compose.foundation.BorderStroke(2.dp, MrbGoldOutline),
                shadowElevation = 12.dp,
                modifier = Modifier.size(96.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_mrb_logo_badge_1784874215022),
                    contentDescription = "MRB Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Circular Loading Bar
            CircularProgressIndicator(
                color = MrbGold,
                strokeWidth = 3.5.dp,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Loading Message
            Text(
                text = message,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MrbTextWhite,
                textAlign = TextAlign.Center
            )
        }
    }
}

private enum class RoleIconType { ADMIN, SELLER, BUYER }

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    iconType: RoleIconType,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                when (iconType) {
                    RoleIconType.ADMIN -> {
                        Text(
                            text = "👑",
                            fontSize = 32.sp
                        )
                    }

                    RoleIconType.SELLER -> {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color(0xFFE5A812),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    RoleIconType.BUYER -> {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF1E1E1E),
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF555555)
                )
            }
        }
    }
}
