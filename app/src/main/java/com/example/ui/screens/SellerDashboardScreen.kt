package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.MrbBackground
import com.example.ui.theme.MrbCardBackground
import com.example.ui.theme.MrbGold
import com.example.ui.theme.MrbGoldOutline
import com.example.ui.theme.MrbTextMuted
import com.example.ui.theme.MrbTextWhite
import com.example.ui.viewmodel.MainViewModel

// Enum for bottom navigation tabs
enum class SellerTab {
    BERANDA,
    JUAL_UNIT,
    BOOKING,
    CHAT,
    PROFIL
}

// Quick Menu Item data class
data class QuickMenuItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color = MrbGold
)

@Composable
fun SellerDashboardScreen(
    viewModel: MainViewModel,
    onNavigateToChatRoom: (String) -> Unit,
    onLogout: () -> Unit
) {
    var activeTab by remember { mutableStateOf(SellerTab.BERANDA) }
    var activeModalDetail by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val carsList by viewModel.allCars.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MrbBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main Content Body based on selected bottom tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    SellerTab.BERANDA -> {
                        SellerHomeView(
                            viewModel = viewModel,
                            onOpenQuickAction = { actionId ->
                                if (actionId == "tambah_unit") {
                                    activeTab = SellerTab.JUAL_UNIT
                                } else {
                                    activeModalDetail = actionId
                                }
                            }
                        )
                    }

                    SellerTab.JUAL_UNIT -> {
                        SellerJualUnitView(
                            viewModel = viewModel,
                            onSuccessAdded = {
                                Toast.makeText(context, "Unit berhasil dipublikasikan!", Toast.LENGTH_SHORT).show()
                                activeTab = SellerTab.BERANDA
                            }
                        )
                    }

                    SellerTab.BOOKING -> {
                        SellerBookingView()
                    }

                    SellerTab.CHAT -> {
                        SellerChatView(onOpenThread = onNavigateToChatRoom)
                    }

                    SellerTab.PROFIL -> {
                        SellerProfileView(onLogout = onLogout)
                    }
                }
            }

            // Bottom Navigation Bar
            SellerBottomNavBar(
                activeTab = activeTab,
                onTabSelected = { activeTab = it }
            )
        }

        // Action Modal Overlay Dialogs for Quick Menus
        if (activeModalDetail != null) {
            SellerQuickActionModal(
                actionId = activeModalDetail!!,
                viewModel = viewModel,
                onClose = { activeModalDetail = null },
                onNavigateToChatRoom = onNavigateToChatRoom
            )
        }
    }
}

@Composable
private fun SellerHomeView(
    viewModel: MainViewModel,
    onOpenQuickAction: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // --- 1. HEADER SECTION ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // MRB Logo Badge Top Left
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MrbGold,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline),
                    modifier = Modifier.size(48.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_mrb_logo_badge_1784874215022),
                        contentDescription = "MRB Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Selamat Pagi,",
                        fontSize = 12.sp,
                        color = MrbTextMuted
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hai, Sales MRB! ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MrbTextWhite
                        )
                        Text(text = "👋", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Sales ID Badge Chip
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFF1E1E24),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFF33333D))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "💬", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sales ID : MRB-0021",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MrbTextWhite
                            )
                        }
                    }
                }
            }

            // Notification Bell Icon with Dot Indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E1E24))
                    .clickable { onOpenQuickAction("promo") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                // Yellow Dot Indicator
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(MrbGold)
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 2.dp)
                )
            }
        }

        // --- 2. HERO YELLOW BANNER CARD ("Jual Unit Baru") ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MrbGold)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background SUV Image aligned right
                Image(
                    painter = painterResource(id = R.drawable.img_bg_veloz_1784874232737),
                    contentDescription = "Veloz SUV",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.65f)
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(topEnd = 22.dp, bottomEnd = 22.dp))
                )

                // Left Gradient Overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MrbGold,
                                    MrbGold.copy(alpha = 0.95f),
                                    MrbGold.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Text Content & Button
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.62f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.Black,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MrbGold,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "Jual Unit Baru",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Tambah unit mobil untuk dijual",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { onOpenQuickAction("tambah_unit") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = "+ Tambah Unit",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 3. 8 QUICK ACTION CARDS GRID ---
        val quickMenuItems = listOf(
            QuickMenuItem("stok_unit", "Stok Unit", "Kelola semua unit", Icons.Default.DirectionsCar),
            QuickMenuItem("booking_masuk", "Booking Masuk", "Lihat booking", Icons.Default.EventAvailable),
            QuickMenuItem("data_pelanggan", "Data Pelanggan", "Leads & Follow Up", Icons.Default.Group),
            QuickMenuItem("chat_pelanggan", "Chat Pelanggan", "Chat langsung", Icons.Default.Forum),
            QuickMenuItem("simulasi_kredit", "Simulasi Kredit", "Hitung cicilan", Icons.Default.Calculate),
            QuickMenuItem("pengajuan_kredit", "Pengajuan Kredit", "Lihat status", Icons.Default.Assignment),
            QuickMenuItem("laporan_penjualan", "Laporan Penjualan", "Penjualan & Komisi", Icons.Default.TrendingUp),
            QuickMenuItem("promo", "Promo", "Promo & Info", Icons.Default.LocalOffer)
        )

        // 2 Rows x 4 Columns Layout
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1 (Items 0 to 3)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickMenuItems.take(4).forEach { item ->
                    QuickActionCardItem(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenQuickAction(item.id) }
                    )
                }
            }

            // Row 2 (Items 4 to 7)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickMenuItems.drop(4).forEach { item ->
                    QuickActionCardItem(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenQuickAction(item.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 4. RINGKASAN PENJUALAN SECTION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282830))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Ringkasan Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ringkasan Penjualan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MrbTextWhite
                    )

                    // Dropdown Filter Pill "Bulan Ini v"
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFF24242C),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383844))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Bulan Ini",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MrbTextWhite
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "▾", fontSize = 12.sp, color = MrbTextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4 Horizontal Stats Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Stat 1: Unit Aktif
                    StatSummaryCard(
                        icon = Icons.Default.DirectionsCar,
                        iconBg = MrbGold.copy(alpha = 0.15f),
                        iconTint = MrbGold,
                        value = "12",
                        label = "Unit Aktif",
                        sublabel = "Unit terpasang",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenQuickAction("stok_unit") }
                    )

                    // Stat 2: Unit Terjual
                    StatSummaryCard(
                        icon = Icons.Default.CheckCircle,
                        iconBg = Color(0xFF28A745).copy(alpha = 0.15f),
                        iconTint = Color(0xFF28A745),
                        value = "5",
                        label = "Unit Terjual",
                        sublabel = "Bulan ini",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenQuickAction("laporan_penjualan") }
                    )

                    // Stat 3: Komisi Didapat
                    StatSummaryCard(
                        icon = Icons.Default.MonetizationOn,
                        iconBg = Color(0xFF8E24AA).copy(alpha = 0.15f),
                        iconTint = Color(0xFFAB47BC),
                        value = "12.5M",
                        label = "Komisi",
                        sublabel = "Rp 12.500.000",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenQuickAction("laporan_penjualan") }
                    )

                    // Stat 4: Target Tercapai
                    StatSummaryCard(
                        icon = Icons.Default.TrackChanges,
                        iconBg = Color(0xFF0288D1).copy(alpha = 0.15f),
                        iconTint = Color(0xFF29B6F6),
                        value = "75%",
                        label = "Target",
                        sublabel = "12 / 16 Unit",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenQuickAction("laporan_penjualan") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Yellow Progress Bar (75% filled)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF2B2B36))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(MrbGold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun QuickActionCardItem(
    item: QuickMenuItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1F)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2C35))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = item.iconColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = MrbTextWhite,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.subtitle,
                fontSize = 9.5.sp,
                color = MrbTextMuted,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StatSummaryCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    value: String,
    label: String,
    sublabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22222A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333340))
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MrbTextWhite,
                maxLines = 1
            )

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MrbTextWhite,
                maxLines = 1
            )

            Text(
                text = sublabel,
                fontSize = 8.5.sp,
                color = MrbTextMuted,
                maxLines = 1
            )
        }
    }
}

// --- 5. BOTTOM NAVIGATION BAR ---
@Composable
private fun SellerBottomNavBar(
    activeTab: SellerTab,
    onTabSelected: (SellerTab) -> Unit
) {
    Surface(
        color = Color(0xFF141417),
        border = androidx.compose.foundation.BorderStroke(1.dp, color = Color(0xFF282832)),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(
                title = "Beranda",
                icon = Icons.Default.Home,
                isSelected = activeTab == SellerTab.BERANDA,
                onClick = { onTabSelected(SellerTab.BERANDA) }
            )
            NavBarItem(
                title = "Jual Unit",
                icon = Icons.Default.DirectionsCar,
                isSelected = activeTab == SellerTab.JUAL_UNIT,
                onClick = { onTabSelected(SellerTab.JUAL_UNIT) }
            )
            NavBarItem(
                title = "Booking",
                icon = Icons.Default.CalendarToday,
                isSelected = activeTab == SellerTab.BOOKING,
                onClick = { onTabSelected(SellerTab.BOOKING) }
            )
            NavBarItem(
                title = "Chat",
                icon = Icons.Default.ChatBubbleOutline,
                isSelected = activeTab == SellerTab.CHAT,
                onClick = { onTabSelected(SellerTab.CHAT) }
            )
            NavBarItem(
                title = "Profil",
                icon = Icons.Default.Person,
                isSelected = activeTab == SellerTab.PROFIL,
                onClick = { onTabSelected(SellerTab.PROFIL) }
            )
        }
    }
}

@Composable
private fun NavBarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) MrbGold else MrbTextMuted,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MrbGold else MrbTextMuted
        )
    }
}

// --- SUB-VIEWS FOR BOTTOM TABS ---

@Composable
private fun SellerJualUnitView(
    viewModel: MainViewModel,
    onSuccessAdded: () -> Unit
) {
    var brandModel by remember { mutableStateOf("Toyota Veloz 1.5 Q CVT") }
    var year by remember { mutableStateOf("2023") }
    var price by remember { mutableStateOf("Rp 245.000.000") }
    var location by remember { mutableStateOf("Palangka Raya, Kalteng") }
    var description by remember { mutableStateOf("Unit mulus istimewa, kilometer rendah, service rutin showroom resmi, pajak hidup, siap pakai antar kota.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = MrbGold,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.padding(6.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Jual Unit Baru", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
                Text(text = "Formulir Penambahan Stok Sales MRB", fontSize = 12.sp, color = MrbTextMuted)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Image Preview Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_bg_veloz_1784874232737),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "📷 5 Foto Terlampir",
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SellerInputField(label = "Merk & Tipe Mobil", value = brandModel, onValueChange = { brandModel = it })
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                SellerInputField(label = "Tahun", value = year, onValueChange = { year = it })
            }
            Box(modifier = Modifier.weight(1f)) {
                SellerInputField(label = "Harga Jual", value = price, onValueChange = { price = it })
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        SellerInputField(label = "Lokasi Unit", value = location, onValueChange = { location = it })
        Spacer(modifier = Modifier.height(12.dp))
        SellerInputField(label = "Deskripsi / Garansi", value = description, onValueChange = { description = it }, maxLines = 3)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val cleanPrice = price.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 245000000L
                val cleanYear = year.toIntOrNull() ?: 2023
                val newCar = com.example.data.model.CarItem(
                    id = "mrb-sales-${System.currentTimeMillis()}",
                    name = brandModel.ifBlank { "Toyota Veloz 1.5 Q CVT" },
                    brand = brandModel.split(" ").firstOrNull() ?: "Toyota",
                    model = brandModel.split(" ").getOrNull(1) ?: "Veloz",
                    year = cleanYear,
                    priceRp = cleanPrice,
                    location = location.ifBlank { "Palangka Raya, Kalteng" },
                    dealerName = "Sales MRB Official",
                    fuelType = "Bensin",
                    transmission = "Automatic",
                    category = "SUV",
                    imageRes = com.example.R.drawable.img_bg_veloz_1784874232737,
                    description = description,
                    status = "Menunggu Persetujuan Admin",
                    isVerified = false
                )
                viewModel.submitCarFromSales(newCar)
                onSuccessAdded()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Kirim Pengajuan Unit ke Admin", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SellerInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    maxLines: Int = 1
) {
    Column {
        Text(text = label, fontSize = 12.sp, color = MrbTextMuted, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = maxLines == 1,
            maxLines = maxLines,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MrbGold,
                unfocusedBorderColor = Color(0xFF3B3B48),
                focusedContainerColor = Color(0xFF1B1B20),
                unfocusedContainerColor = Color(0xFF1B1B20),
                cursorColor = MrbGold
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SellerBookingView() {
    val bookingList = remember {
        listOf(
            Triple("Ahmad Rizky", "Toyota Veloz 1.5 Q", "Rp 5.000.000 (DP Paid)"),
            Triple("Budi Santoso", "Mitsubishi Pajero Sport", "Rp 10.000.000 (DP Paid)"),
            Triple("Siti Rahmah", "Honda HR-V SE", "Rp 3.000.000 (Verifikasi)"),
            Triple("Hendra Wijaya", "Toyota Innova Reborn", "Rp 5.000.000 (DP Paid)")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(text = "Booking Masuk Pelanggan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
        Text(text = "Daftar pemesanan unit dari calon pembeli", fontSize = 12.sp, color = MrbTextMuted)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(bookingList) { (customer, car, dp) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2C38)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = customer, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MrbTextWhite)
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MrbGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Booking Baru",
                                    color = MrbGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Unit : $car", fontSize = 13.sp, color = MrbGold)
                        Text(text = "Deposit : $dp", fontSize = 12.sp, color = MrbTextMuted)

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                            ) {
                                Text("Konfirmasi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2B36), contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                            ) {
                                Text("Hubungi", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerChatView(onOpenThread: (String) -> Unit) {
    val chats = remember {
        listOf(
            Triple("Pak Ahmad Rizky", "Halo mas, unit Veloz ready untuk test drive besok?", "10:15 AM"),
            Triple("Ibu Dewi Lestari", "Apakah Pajero Sport bisa kredit DP 20%?", "Kemarin"),
            Triple("Mas Faisal", "Terima kasih unitnya sudah sampai di Sampit!", "2 Hari lalu")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(text = "Chat Pelanggan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
        Text(text = "Pesan masuk dari pembeli & leads", fontSize = 12.sp, color = MrbTextMuted)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(chats) { (name, lastMsg, time) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenThread("thread-1") }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MrbGold,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MrbTextWhite)
                                Text(text = time, fontSize = 11.sp, color = MrbTextMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = lastMsg, fontSize = 12.sp, color = MrbTextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerProfileView(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Avatar Logo
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MrbGold,
            border = androidx.compose.foundation.BorderStroke(2.dp, MrbGoldOutline),
            modifier = Modifier.size(88.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_mrb_logo_badge_1784874215022),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "Sales Official MRB", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
        Text(text = "ID: MRB-0021 • Area Kalimantan Tengah", fontSize = 12.sp, color = MrbTextMuted)

        Spacer(modifier = Modifier.height(24.dp))

        // Performance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "12", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MrbGold)
                    Text(text = "Unit Aktif", fontSize = 11.sp, color = MrbTextMuted)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "5", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF28A745))
                    Text(text = "Terjual", fontSize = 11.sp, color = MrbTextMuted)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "4.9 ★", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MrbGold)
                    Text(text = "Rating Sales", fontSize = 11.sp, color = MrbTextMuted)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F), contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "Keluar dari Sesi Sales", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// --- QUICK ACTION MODAL OVERLAY ---
@Composable
private fun SellerQuickActionModal(
    actionId: String,
    viewModel: MainViewModel,
    onClose: () -> Unit,
    onNavigateToChatRoom: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MrbBackground),
            color = MrbBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (actionId) {
                            "stok_unit" -> "Stok Unit Kendaraan"
                            "booking_masuk" -> "Booking Masuk"
                            "data_pelanggan" -> "Data Pelanggan (Leads)"
                            "chat_pelanggan" -> "Chat Pelanggan"
                            "simulasi_kredit" -> "Kalkulator Simulasi Kredit"
                            "pengajuan_kredit" -> "Pengajuan Kredit"
                            "laporan_penjualan" -> "Laporan Penjualan & Komisi"
                            "promo" -> "Promo & Program Penjualan"
                            else -> "Menu Sales"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MrbTextWhite
                    )

                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Modal Content Body
                Box(modifier = Modifier.weight(1f)) {
                    when (actionId) {
                        "stok_unit" -> ModalStokUnitView(viewModel)
                        "booking_masuk" -> SellerBookingView()
                        "data_pelanggan" -> ModalDataPelangganView()
                        "chat_pelanggan" -> SellerChatView(onOpenThread = {
                            onClose()
                            onNavigateToChatRoom(it)
                        })
                        "simulasi_kredit" -> ModalSimulasiKreditView()
                        "pengajuan_kredit" -> ModalPengajuanKreditView()
                        "laporan_penjualan" -> ModalLaporanPenjualanView()
                        "promo" -> ModalPromoView()
                        else -> Text("Detail menu", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModalStokUnitView(viewModel: MainViewModel) {
    val cars by viewModel.allCars.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Daftar stok unit yang terdaftar untuk dijual di area Kalimantan:", fontSize = 12.sp, color = MrbTextMuted)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(cars) { car ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = car.imageRes ?: R.drawable.img_bg_veloz_1784874232737),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = car.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MrbTextWhite)
                            Text(text = "Rp %,d".format(car.priceRp), fontSize = 13.sp, color = MrbGold, fontWeight = FontWeight.Bold)
                            Text(text = "${car.year} • ${car.transmission}", fontSize = 11.sp, color = MrbTextMuted)
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF28A745).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Ready",
                                color = Color(0xFF28A745),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModalDataPelangganView() {
    val leads = remember {
        listOf(
            Triple("Dedi Kurniawan", "Minat Innova Reborn - Tanya DP", "Hot Lead 🔥"),
            Triple("Rina Marlina", "Minat Veloz 1.5 - Rencana Kredit 3 Thn", "Warm Lead ☀️"),
            Triple("Haji Samsul", "Tanya Pajero Sport 4x4", "Follow Up 📞")
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Data Leads & Prospek Pelanggan Sales:", fontSize = 12.sp, color = MrbTextMuted)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(leads) { (name, note, status) ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = name, fontWeight = FontWeight.Bold, color = MrbTextWhite, fontSize = 14.sp)
                            Text(text = status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MrbGold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = note, fontSize = 12.sp, color = MrbTextMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModalSimulasiKreditView() {
    var priceInput by remember { mutableStateOf("245000000") }
    var dpPercent by remember { mutableStateOf("20") }
    var tenorYears by remember { mutableStateOf("3") }

    val priceVal = priceInput.toLongOrNull() ?: 245000000L
    val dpVal = (priceVal * (dpPercent.toIntOrNull() ?: 20)) / 100
    val loanVal = priceVal - dpVal
    val tenorMonths = (tenorYears.toIntOrNull() ?: 3) * 12
    val monthlyEst = (loanVal / tenorMonths) + (loanVal * 0.08 / 12).toLong()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Hitung Estimasi Angsuran", fontWeight = FontWeight.Bold, color = MrbGold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(12.dp))

                SellerInputField("Harga OTR Mobil (Rp)", priceInput, { priceInput = it })
                Spacer(modifier = Modifier.height(10.dp))
                SellerInputField("Uang Muka / DP (%)", dpPercent, { dpPercent = it })
                Spacer(modifier = Modifier.height(10.dp))
                SellerInputField("Tenor (Tahun)", tenorYears, { tenorYears = it })

                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MrbGold,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Estimasi Cicilan per Bulan", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Rp %,d".format(monthlyEst),
                            color = Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text("DP: Rp %,d • Tenor: $tenorMonths Bulan".format(dpVal), color = Color(0xFF333333), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModalPengajuanKreditView() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Daftar Pengajuan Kredit Leasing Pelanggan:", fontSize = 12.sp, color = MrbTextMuted)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Budi Santoso - Mandiri Utama Finance", fontWeight = FontWeight.Bold, color = MrbTextWhite, fontSize = 13.sp)
                    Text("Disetujui ✓", color = Color(0xFF28A745), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Unit: Pajero Sport • Tenor: 48 Bulan", fontSize = 12.sp, color = MrbTextMuted)
            }
        }
    }
}

@Composable
private fun ModalLaporanPenjualanView() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Laporan Penjualan & Perhitungan Komisi Sales:", fontSize = 12.sp, color = MrbTextMuted)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pencapaian Bulan Ini", fontWeight = FontWeight.Bold, color = MrbGold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Total Unit Terjual: 5 Unit", color = Color.White, fontSize = 13.sp)
                Text("• Omset Penjualan: Rp 1.250.000.000", color = Color.White, fontSize = 13.sp)
                Text("• Komisi Diterima: Rp 12.500.000", color = MrbGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun ModalPromoView() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Promo & Program Penjualan Spesial MRB:", fontSize = 12.sp, color = MrbTextMuted)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🔥 Promo Merdeka DPHasil Murah", fontWeight = FontWeight.Bold, color = MrbGold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Potongan cashback s/d Rp 10 Juta + Free Garansi Mesin 1 Tahun untuk unit SUV & MPV.", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}
