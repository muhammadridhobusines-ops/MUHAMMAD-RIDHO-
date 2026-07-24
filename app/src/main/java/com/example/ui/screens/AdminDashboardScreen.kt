package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.CarItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

enum class AdminTab(val label: String, val category: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", "MANAJEMEN", Icons.Default.Dashboard),
    UNIT_MOBIL("Unit Mobil", "MANAJEMEN", Icons.Default.DirectionsCar),
    PESANAN_BOOKING("Pesanan & Booking", "MANAJEMEN", Icons.Default.CalendarToday),
    SIMULASI_KREDIT("Simulasi Kredit", "MANAJEMEN", Icons.Default.Calculate),
    TRANSAKSI("Transaksi", "MANAJEMEN", Icons.Default.Receipt),
    PELANGGAN("Pelanggan", "MANAJEMEN", Icons.Default.Group),
    TAMBAH_MOBIL("Tambah Mobil Baru", "MANAJEMEN", Icons.Default.AddCircleOutline),
    KELOLA_PIN("Kelola PIN Penjual", "MANAJEMEN", Icons.Default.Key),

    PROMO_BANNER("Promo & Banner", "KONTEN", Icons.Default.Campaign),
    BERITA_ARTIKEL("Berita & Artikel", "KONTEN", Icons.Default.Article),
    TESTIMONI("Testimoni", "KONTEN", Icons.Default.RateReview),

    PENJUALAN("Penjualan", "LAPORAN", Icons.Default.BarChart),
    KEUANGAN("Keuangan", "LAPORAN", Icons.Default.AccountBalanceWallet),
    PERFORMA_SALES("Performa Sales", "LAPORAN", Icons.Default.TrendingUp),

    PENGGUNA("Pengguna", "PENGATURAN", Icons.Default.PersonOutline),
    PENGATURAN_SISTEM("Pengaturan Sistem", "PENGATURAN", Icons.Default.Settings),
    BACKUP_DATA("Backup Data", "PENGATURAN", Icons.Default.Backup)
}

data class PinItemData(
    val pin: String,
    val createdBy: String,
    val createdAt: String,
    val usedBy: String,
    val status: String // "Aktif", "Tidak Aktif", "Dihapus"
)

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddCar: () -> Unit = {}
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(AdminTab.DASHBOARD) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0E))
    ) {
        val screenWidth = maxWidth
        val isAutoMobile = screenWidth < 768.dp

        // Mode choice: null = auto, true = Force Mobile (HP Vertikal), false = Force Desktop
        var userModeChoice by remember { mutableStateOf<Boolean?>(null) }
        val isMobileMode = userModeChoice ?: isAutoMobile

        // Sidebar state: on mobile mode default closed (overlay), on desktop default open
        var isSidebarOpen by remember { mutableStateOf(!isMobileMode) }

        // Sync sidebar state when user switches modes
        LaunchedEffect(isMobileMode) {
            isSidebarOpen = !isMobileMode
        }

        val cars by viewModel.allCars.collectAsState()
        val pendingCars by viewModel.pendingCars.collectAsState()
        val creditRequests by viewModel.creditRequests.collectAsState()
        val bookingRequests by viewModel.bookingRequests.collectAsState()

        // PIN table state
        var pinList by remember {
            mutableStateOf(
                listOf(
                    PinItemData("12345678", "Admin MRB", "01 Juni 2025 10:30 WIB", "Budi Santoso (Sales Senior)", "Aktif"),
                    PinItemData("00000000", "Admin MRB", "28 Mei 2025 14:20 WIB", "Siti Aisyah (Sales)", "Aktif"),
                    PinItemData("22222222", "Admin MRB", "25 Mei 2025 09:15 WIB", "Andi Wijaya (Makelar)", "Aktif"),
                    PinItemData("11111111", "Admin MRB", "20 Mei 2025 16:45 WIB", "Dewi Lestari (Sales)", "Aktif"),
                    PinItemData("33333333", "Admin MRB", "15 Mei 2025 11:11 WIB", "Rizky Pratama (Makelar)", "Tidak Aktif"),
                    PinItemData("44444444", "Admin MRB", "10 Mei 2025 13:50 WIB", "-", "Dihapus"),
                    PinItemData("55555555", "Admin MRB", "05 Mei 2025 08:30 WIB", "-", "Dihapus")
                )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // --- TOP HEADER BAR ---
            TopAdminHeader(
                activeTab = activeTab,
                isMobileMode = isMobileMode,
                onToggleMode = {
                    userModeChoice = !isMobileMode
                },
                onToggleSidebar = { isSidebarOpen = !isSidebarOpen },
                onNavigateBack = onNavigateBack,
                notifCount = pendingCars.size + creditRequests.size + bookingRequests.size
            )

            if (isMobileMode) {
                // ==========================================
                // MOBILE MODE: VERTICAL STACK WITH OVERLAY DRAWER
                // ==========================================
                Box(modifier = Modifier.fillMaxSize()) {
                    // MAIN CONTENT VIEWPORT
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0D0D12))
                    ) {
                        RenderAdminTabContent(
                            activeTab = activeTab,
                            isMobileMode = true,
                            cars = cars,
                            pendingCars = pendingCars,
                            creditRequests = creditRequests,
                            bookingRequests = bookingRequests,
                            pinList = pinList,
                            viewModel = viewModel,
                            onSelectTab = { activeTab = it },
                            onUpdatePinList = { pinList = it },
                            onNavigateBack = onNavigateBack
                        )
                    }

                    // OVERLAY DRAWER SIDEBAR
                    if (isSidebarOpen) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.75f))
                                .clickable { isSidebarOpen = false }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(280.dp)
                                    .align(Alignment.CenterStart)
                                    .clickable(enabled = false) {}
                            ) {
                                AdminSidebar(
                                    activeTab = activeTab,
                                    totalCarsCount = cars.size,
                                    onTabSelect = { selectedTab ->
                                        activeTab = selectedTab
                                        isSidebarOpen = false // close sidebar drawer on select
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                // ==========================================
                // DESKTOP MODE: SIDEBAR + CONTENT SIDE-BY-SIDE
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (isAutoMobile) Modifier.horizontalScroll(rememberScrollState()) else Modifier
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .then(
                                if (isAutoMobile) Modifier.width(1200.dp) else Modifier.fillMaxWidth()
                            )
                    ) {
                        // SIDEBAR
                        if (isSidebarOpen) {
                            AdminSidebar(
                                activeTab = activeTab,
                                totalCarsCount = cars.size,
                                onTabSelect = { activeTab = it }
                            )
                        }

                        // MAIN CONTENT
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFF0D0D12))
                        ) {
                            RenderAdminTabContent(
                                activeTab = activeTab,
                                isMobileMode = false,
                                cars = cars,
                                pendingCars = pendingCars,
                                creditRequests = creditRequests,
                                bookingRequests = bookingRequests,
                                pinList = pinList,
                                viewModel = viewModel,
                                onSelectTab = { activeTab = it },
                                onUpdatePinList = { pinList = it },
                                onNavigateBack = onNavigateBack
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderAdminTabContent(
    activeTab: AdminTab,
    isMobileMode: Boolean,
    cars: List<CarItem>,
    pendingCars: List<CarItem>,
    creditRequests: List<com.example.data.model.CreditRequest>,
    bookingRequests: List<com.example.data.model.BookingRequest>,
    pinList: List<PinItemData>,
    viewModel: MainViewModel,
    onSelectTab: (AdminTab) -> Unit,
    onUpdatePinList: (List<PinItemData>) -> Unit,
    onNavigateBack: () -> Unit
) {
    when (activeTab) {
        AdminTab.DASHBOARD -> AdminOverviewDashboardView(
            cars = cars,
            pendingCars = pendingCars,
            creditRequests = creditRequests,
            bookingRequests = bookingRequests,
            viewModel = viewModel,
            isMobileMode = isMobileMode,
            onGoToAddCar = { onSelectTab(AdminTab.TAMBAH_MOBIL) },
            onGoToManagePin = { onSelectTab(AdminTab.KELOLA_PIN) }
        )

        AdminTab.TAMBAH_MOBIL -> AdminTambahMobilBaruView(
            viewModel = viewModel,
            isMobileMode = isMobileMode,
            onBackToDashboard = { onSelectTab(AdminTab.DASHBOARD) }
        )

        AdminTab.KELOLA_PIN -> AdminKelolaPinView(
            viewModel = viewModel,
            pinList = pinList,
            isMobileMode = isMobileMode,
            onUpdatePinList = onUpdatePinList
        )

        AdminTab.UNIT_MOBIL -> AdminUnitMobilView(
            cars = cars,
            pendingCars = pendingCars,
            viewModel = viewModel,
            isMobileMode = isMobileMode,
            onGoToAddCar = { onSelectTab(AdminTab.TAMBAH_MOBIL) }
        )

        AdminTab.PESANAN_BOOKING -> AdminBookingAndApprovalView(
            pendingCars = pendingCars,
            bookingRequests = bookingRequests,
            viewModel = viewModel,
            isMobileMode = isMobileMode
        )

        else -> GenericPlaceholderAdminTab(
            title = activeTab.label,
            category = activeTab.category
        )
    }
}

// ==========================================
// 1. TOP HEADER COMPONENT
// ==========================================
@Composable
private fun TopAdminHeader(
    activeTab: AdminTab,
    isMobileMode: Boolean,
    onToggleMode: () -> Unit,
    onToggleSidebar: () -> Unit,
    onNavigateBack: () -> Unit,
    notifCount: Int
) {
    Surface(
        color = Color(0xFF13131A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22222E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isMobileMode) 8.dp else 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Logo & Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleSidebar, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Toggle Sidebar",
                        tint = MrbGold
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "MRB",
                    fontSize = if (isMobileMode) 18.sp else 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MrbGold,
                    letterSpacing = 1.sp
                )

                if (!isMobileMode) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1E1E28)
                    ) {
                        Text(
                            text = "ADMIN PANEL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MrbGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Mode Switcher Button (📱 Mode HP vs 💻 Mode Desktop)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1F1F2E),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onToggleMode() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isMobileMode) Icons.Default.Smartphone else Icons.Default.Computer,
                        contentDescription = null,
                        tint = MrbGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isMobileMode) "📱 Mode HP (Tegak)" else "💻 Mode Desktop",
                        color = MrbGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Right Notification & Profile / Logout
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Bell
                Box(modifier = Modifier.padding(2.dp)) {
                    IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notif",
                            tint = MrbTextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (notifCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE53935),
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Text(
                                text = "$notifCount",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                if (!isMobileMode) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MrbGold,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Admin MRB",
                                color = MrbTextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Administrator",
                                color = MrbTextMuted,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = onNavigateBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Exit Admin",
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// 2. SIDEBAR COMPONENT
// ==========================================
@Composable
private fun AdminSidebar(
    activeTab: AdminTab,
    totalCarsCount: Int,
    onTabSelect: (AdminTab) -> Unit
) {
    Surface(
        color = Color(0xFF101016),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E1E2A)),
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp)
        ) {
            // Header Logo Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161622)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MrbGold,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "MRB", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "MITRA RODA BORNEO", color = MrbGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "TRUSTED CAR PARTNER", color = MrbTextMuted, fontSize = 9.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sidebar Menu Sections
            val categories = listOf("MANAJEMEN", "KONTEN", "LAPORAN", "PENGATURAN")

            categories.forEach { category ->
                Text(
                    text = category,
                    color = MrbTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )

                AdminTab.values().filter { it.category == category }.forEach { tab ->
                    val isSelected = activeTab == tab
                    val isNewTag = tab == AdminTab.TAMBAH_MOBIL || tab == AdminTab.KELOLA_PIN

                    Surface(
                        color = if (isSelected) MrbGold else Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 2.dp)
                            .clickable { onTabSelect(tab) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (isSelected) Color.Black else MrbTextWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = tab.label,
                                color = if (isSelected) Color.Black else MrbTextWhite,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )

                            if (isNewTag && !isSelected) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MrbGold
                                ) {
                                    Text(
                                        text = "Baru",
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sidebar Footer Cards
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181824)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A3A))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Total Unit Aktif", color = MrbGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = MrbTextWhite, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "$totalCarsCount", color = MrbTextWhite, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    }
                    Text(text = "Unit tersedia", color = MrbTextMuted, fontSize = 10.sp)
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181824)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A3A))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = MrbGold, modifier = Modifier.size(20.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Pusat Bantuan", color = MrbTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Butuh bantuan? Hubungi tim support kami.", color = MrbTextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "Hubungi Kami", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "MRB Mitra Roda Borneo\n© 2026 All rights reserved.",
                color = MrbTextMuted,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )
        }
    }
}

// ==========================================
// 3. SCREENSHOT 1: TAMBAH MOBIL BARU VIEW
// ==========================================
@Composable
private fun AdminTambahMobilBaruView(
    viewModel: MainViewModel,
    isMobileMode: Boolean = false,
    onBackToDashboard: () -> Unit
) {
    val context = LocalContext.current

    // Form fields
    var namaMobil by remember { mutableStateOf("") }
    var selectedMerek by remember { mutableStateOf("Toyota") }
    var modelVarian by remember { mutableStateOf("") }
    var selectedTahun by remember { mutableStateOf("2023") }
    var nopol by remember { mutableStateOf("") }
    var norangka by remember { mutableStateOf("") }
    var nomesin by remember { mutableStateOf("") }
    var selectedWarna by remember { mutableStateOf("Hitam Metalik") }

    var hargaJual by remember { mutableStateOf("") }
    var hargaCash by remember { mutableStateOf("") }
    var hargaKredit by remember { mutableStateOf("") }
    var kilometer by remember { mutableStateOf("") }

    var transmisi by remember { mutableStateOf("Otomatis") }
    var bensinType by remember { mutableStateOf("Bensin") }
    var driveTrain by remember { mutableStateOf("FWD") }
    var engineCc by remember { mutableStateOf("1500") }

    var selectedKategori by remember { mutableStateOf("SUV") }
    var selectedKapasitas by remember { mutableStateOf("7-8 Kursi") }
    var kondisiUnit by remember { mutableStateOf("Bekas") }
    var statusUnit by remember { mutableStateOf("Ready") }
    var deskripsi by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(if (isMobileMode) 12.dp else 24.dp)
    ) {
        // Title & Breadcrumb
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tambah Mobil Baru",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MrbTextWhite
                )
                Text(
                    text = "Home  /  Management  /  Tambah Mobil Baru",
                    fontSize = 12.sp,
                    color = MrbTextMuted
                )
            }

            OutlinedButton(
                onClick = onBackToDashboard,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MrbGold),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Kembali ke Dashboard", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Grid Row 1: Section 1 (Foto Utama) & Section 2 (Galeri Foto)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Foto Utama Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    FormSectionHeader(number = "1", title = "Foto Utama")

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dropzone
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F0F16))
                            .border(1.dp, Color(0xFF2E2E42), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUrl.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(photoUrl).crossfade(true).build(),
                                contentDescription = "Foto Utama",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    tint = MrbGold,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Belum ada foto utama", fontSize = 13.sp, color = MrbTextWhite, fontWeight = FontWeight.SemiBold)
                                Text(text = "Upload foto utama unit mobil", fontSize = 11.sp, color = MrbTextMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { photoUrl = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=800" },
                            colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(38.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Pilih Galeri", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { photoUrl = "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800" },
                            colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(38.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Ambil Kamera", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (photoUrl.isNotBlank()) {
                        OutlinedButton(
                            onClick = { photoUrl = "" },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Hapus Foto Utama", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AdminTextField(
                        label = "Atau masukkan URL link foto utama (opsional)",
                        value = photoUrl,
                        onValueChange = { photoUrl = it }
                    )
                }
            }

            // 2. Galeri Foto Tambahan Card
            Card(
                modifier = Modifier.weight(1.2f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FormSectionHeader(number = "2", title = "Galeri Foto Tambahan")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Total: 8 / 30 Foto", fontSize = 11.sp, color = MrbTextMuted)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Hapus Semua",
                                fontSize = 11.sp,
                                color = Color(0xFFEF5350),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { }
                            )
                        }
                    }

                    Text(
                        text = "Unggah banyak foto (maks. 30 foto). Drag & drop untuk mengatur urutan.",
                        fontSize = 11.sp,
                        color = MrbTextMuted,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // Thumbnails Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val samplePhotos = listOf("Depan", "Belakang", "Samping Kanan", "Samping Kiri", "Interior", "Dashboard", "Mesin", "Bagasi")
                        items(samplePhotos) { photoLabel ->
                            Card(
                                modifier = Modifier.width(110.dp).height(120.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A26))
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_car_innova),
                                        contentDescription = photoLabel,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 8.dp),
                                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                                    ) {
                                        Text(
                                            text = photoLabel,
                                            color = MrbGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "Kategori Foto Sebelum Unggah:", fontSize = 11.sp, color = MrbTextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    val categoryPills = listOf("Depan", "Belakang", "Samping Kanan", "Samping Kiri", "Interior", "Dashboard", "Mesin", "Bagasi", "Velg", "Lainnya")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categoryPills) { pill ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (pill == "Depan") MrbGold else Color(0xFF1E1E2C),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E42)),
                                modifier = Modifier.clickable { }
                            ) {
                                Text(
                                    text = pill,
                                    color = if (pill == "Depan") Color.Black else MrbTextWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Grid Row 2: Section 3 (Informasi Utama), Section 4 (Harga), Section 5 (Spesifikasi)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 3. Informasi Utama Card
            Card(
                modifier = Modifier.weight(1.2f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    FormSectionHeader(number = "3", title = "Informasi Utama")

                    Spacer(modifier = Modifier.height(14.dp))

                    AdminTextField(label = "Nama Mobil *", value = namaMobil, onValueChange = { namaMobil = it }, placeholder = "Contoh: Toyota Innova Reborn")

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            AdminTextField(label = "Merek *", value = selectedMerek, onValueChange = { selectedMerek = it })
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            AdminTextField(label = "Model / Varian *", value = modelVarian, onValueChange = { modelVarian = it }, placeholder = "Contoh: 2.4 G AT")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            AdminTextField(label = "Tahun *", value = selectedTahun, onValueChange = { selectedTahun = it })
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            AdminTextField(label = "Warna *", value = selectedWarna, onValueChange = { selectedWarna = it })
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            AdminTextField(label = "Nomor Polisi", value = nopol, onValueChange = { nopol = it }, placeholder = "Contoh: KH 1234 AB")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            AdminTextField(label = "Nomor Rangka", value = norangka, onValueChange = { norangka = it }, placeholder = "Contoh: MHFGW65...")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AdminTextField(label = "Nomor Mesin", value = nomesin, onValueChange = { nomesin = it }, placeholder = "Contoh: 2GD-1234567")
                }
            }

            // 4. Harga Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    FormSectionHeader(number = "4", title = "Harga")

                    Spacer(modifier = Modifier.height(14.dp))

                    AdminTextField(label = "Harga Jual (Rp) *", value = hargaJual, onValueChange = { hargaJual = it }, placeholder = "Rp 0")
                    Spacer(modifier = Modifier.height(10.dp))
                    AdminTextField(label = "Harga Cash (Rp)", value = hargaCash, onValueChange = { hargaCash = it }, placeholder = "Rp 0")
                    Spacer(modifier = Modifier.height(10.dp))
                    AdminTextField(label = "Harga Kredit (Rp)", value = hargaKredit, onValueChange = { hargaKredit = it }, placeholder = "Rp 0")
                    Spacer(modifier = Modifier.height(10.dp))
                    AdminTextField(label = "Kilometer (KM) *", value = kilometer, onValueChange = { kilometer = it }, placeholder = "0 KM")
                }
            }

            // 5. Spesifikasi Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    FormSectionHeader(number = "5", title = "Spesifikasi")

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Transmisi", fontSize = 11.sp, color = MrbTextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Otomatis", "Manual").forEach { option ->
                            val isSel = transmisi == option
                            Button(
                                onClick = { transmisi = option },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSel) MrbGold else Color(0xFF1A1A26),
                                    contentColor = if (isSel) Color.Black else MrbTextWhite
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text(text = option, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Bahan Bakar (BBM)", fontSize = 11.sp, color = MrbTextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Bensin", "Solar", "Hybrid", "Listrik").forEach { option ->
                            val isSel = bensinType == option
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) MrbGold else Color(0xFF1A1A26),
                                modifier = Modifier.weight(1f).clickable { bensinType = option }
                            ) {
                                Text(
                                    text = option,
                                    color = if (isSel) Color.Black else MrbTextWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Penggerak Roda", fontSize = 11.sp, color = MrbTextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("FWD", "RWD", "AWD", "4WD").forEach { option ->
                            val isSel = driveTrain == option
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) MrbGold else Color(0xFF1A1A26),
                                modifier = Modifier.weight(1f).clickable { driveTrain = option }
                            ) {
                                Text(
                                    text = option,
                                    color = if (isSel) Color.Black else MrbTextWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AdminTextField(label = "Kapasitas Mesin (CC)", value = engineCc, onValueChange = { engineCc = it }, placeholder = "Contoh: 2400")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Grid Row 3: Section 6 (Kategori), Section 7 (Kapasitas), Section 8 (Kondisi), Section 9 (Status)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 6. Kategori Mobil Card
            Card(
                modifier = Modifier.weight(1.2f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    FormSectionHeader(number = "6", title = "Kategori Mobil")
                    Spacer(modifier = Modifier.height(14.dp))

                    val categories = listOf("SUV", "MPV", "Sedan", "Hatchback", "City Car", "Pickup", "Double Cabin", "Minibus")
                    val rows = categories.chunked(4)
                    rows.forEach { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            rowItems.forEach { cat ->
                                val isSel = selectedKategori == cat
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) MrbGold else Color(0xFF1A1A26),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) MrbGold else Color(0xFF2E2E40)),
                                    modifier = Modifier.weight(1f).clickable { selectedKategori = cat }
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSel) Color.Black else MrbTextWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 7. Kapasitas Penumpang Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    FormSectionHeader(number = "7", title = "Kapasitas Penumpang")
                    Spacer(modifier = Modifier.height(14.dp))

                    val capacities = listOf("2 Kursi", "4-5 Kursi", "7-8 Kursi", ">8 Kursi")
                    val capRows = capacities.chunked(2)
                    capRows.forEach { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            rowItems.forEach { cap ->
                                val isSel = selectedKapasitas == cap
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) MrbGold else Color(0xFF1A1A26),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) MrbGold else Color(0xFF2E2E40)),
                                    modifier = Modifier.weight(1f).clickable { selectedKapasitas = cap }
                                ) {
                                    Text(
                                        text = cap,
                                        color = if (isSel) Color.Black else MrbTextWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 8. Kondisi Unit Card
            Card(
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    FormSectionHeader(number = "8", title = "Kondisi Unit")
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Baru", "Bekas").forEach { cond ->
                            val isSel = kondisiUnit == cond
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MrbGold else Color(0xFF1A1A26),
                                modifier = Modifier.weight(1f).clickable { kondisiUnit = cond }
                            ) {
                                Text(
                                    text = cond,
                                    color = if (isSel) Color.Black else MrbTextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 9. Status Unit Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    FormSectionHeader(number = "9", title = "Status Unit")
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val statuses = listOf(
                            "Ready" to Color(0xFF2E7D32),
                            "Booking" to Color(0xFFF57F17),
                            "Sold" to Color(0xFFC62828)
                        )
                        statuses.forEach { (stat, col) ->
                            val isSel = statusUnit == stat
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) col else Color(0xFF1A1A26),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) col else Color(0xFF2E2E40)),
                                modifier = Modifier.weight(1f).clickable { statusUnit = stat }
                            ) {
                                Text(
                                    text = stat,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 10: Deskripsi Mobil
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                FormSectionHeader(number = "10", title = "Deskripsi Mobil")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tuliskan rincian kelengkapan, pajak, riwayat servis, kondisi unit, dan catatan lainnya.",
                    fontSize = 11.sp,
                    color = MrbTextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    placeholder = { Text("Tulis deskripsi lengkap mobil di sini...", color = MrbTextMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F0F16),
                        unfocusedContainerColor = Color(0xFF0F0F16),
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = Color(0xFF2E2E42),
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons Row matching Screenshot 1 Footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    val cleanPrice = hargaJual.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 350000000L
                    val cleanYear = selectedTahun.toIntOrNull() ?: 2023
                    val newCar = CarItem(
                        id = "mrb-${System.currentTimeMillis()}",
                        name = namaMobil.ifBlank { "Toyota Innova Zenix" },
                        brand = selectedMerek,
                        model = modelVarian.ifBlank { "Zenix 2.0 V CVT" },
                        year = cleanYear,
                        priceRp = cleanPrice,
                        location = "Sampit, Kalteng",
                        dealerName = "MRB Central",
                        fuelType = bensinType,
                        transmission = transmisi,
                        kilometer = kilometer.toIntOrNull() ?: 15000,
                        category = selectedKategori,
                        passengerCapacity = selectedKapasitas,
                        condition = kondisiUnit,
                        status = statusUnit,
                        imageUrl = photoUrl.ifBlank { null },
                        imageRes = if (photoUrl.isBlank()) R.drawable.img_car_innova else null,
                        description = deskripsi.ifBlank { "Unit istimewa bergaransi resmi MRB." }
                    )
                    viewModel.addNewCarAdmin(newCar)
                    Toast.makeText(context, "✅ Unit Mobil Berhasil Disimpan ke Real-time Database!", Toast.LENGTH_LONG).show()
                    onBackToDashboard()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1.5f).height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Simpan Unit Mobil", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    Toast.makeText(context, "📁 Draft disimpan secara lokal.", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E2C), contentColor = MrbTextWhite),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Folder, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Simpan Draft", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    namaMobil = ""
                    modelVarian = ""
                    nopol = ""
                    norangka = ""
                    nomesin = ""
                    hargaJual = ""
                    hargaCash = ""
                    hargaKredit = ""
                    kilometer = ""
                    deskripsi = ""
                    photoUrl = ""
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Reset Form", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Helper for Numbered Section Headers in Form
@Composable
private fun FormSectionHeader(number: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = MrbGold,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = number, color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = title, color = MrbTextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

// Helper TextField for Admin Panel
@Composable
private fun AdminTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
) {
    Column {
        Text(text = label, fontSize = 11.sp, color = MrbTextMuted)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = MrbTextMuted, fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F0F16),
                unfocusedContainerColor = Color(0xFF0F0F16),
                focusedBorderColor = MrbGold,
                unfocusedBorderColor = Color(0xFF2E2E42),
                focusedTextColor = MrbTextWhite,
                unfocusedTextColor = MrbTextWhite
            )
        )
    }
}

// ==========================================
// 4. SCREENSHOT 2: KELOLA PIN PENJUAL VIEW
// ==========================================
@Composable
private fun AdminKelolaPinView(
    viewModel: MainViewModel,
    pinList: List<PinItemData>,
    isMobileMode: Boolean = false,
    onUpdatePinList: (List<PinItemData>) -> Unit
) {
    val context = LocalContext.current
    var inputPinBaru by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("Semua Status") }

    val filteredList = pinList.filter {
        val matchesQuery = searchQuery.isEmpty() || it.pin.contains(searchQuery) || it.usedBy.contains(searchQuery, ignoreCase = true)
        val matchesStatus = selectedStatusFilter == "Semua Status" || it.status.equals(selectedStatusFilter, ignoreCase = true)
        matchesQuery && matchesStatus
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(if (isMobileMode) 12.dp else 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Kelola PIN Penjual / Sales / Makelar",
                    fontSize = if (isMobileMode) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MrbTextWhite
                )
                Text(
                    text = "Home  /  Management  /  Kelola PIN Penjual / Sales / Makelar",
                    fontSize = 11.sp,
                    color = MrbTextMuted
                )
                Text(
                    text = "Tambah, hapus, atau kelola PIN akses 4/8-digit yang diberikan kepada tim sales/makelar.",
                    fontSize = 11.sp,
                    color = MrbTextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (!isMobileMode) {
                OutlinedButton(
                    onClick = { Toast.makeText(context, "📜 Log riwayat aktivitas PIN ditampilkan.", Toast.LENGTH_SHORT).show() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MrbGold),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Riwayat Aktivitas", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Metrics Cards Grid
        if (isMobileMode) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PinMetricCard(
                        icon = Icons.Default.Key,
                        iconBg = MrbGold,
                        title = "Total PIN Aktif",
                        value = "${pinList.count { it.status == "Aktif" }}",
                        subtitle = "PIN digunakan",
                        modifier = Modifier.weight(1f)
                    )
                    PinMetricCard(
                        icon = Icons.Default.Person,
                        iconBg = MrbGold,
                        title = "Total Sales",
                        value = "8",
                        subtitle = "Tim terdaftar",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PinMetricCard(
                        icon = Icons.Default.Shield,
                        iconBg = MrbGold,
                        title = "PIN Baru",
                        value = "4",
                        subtitle = "↑ 33.3% m-o-m",
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                    PinMetricCard(
                        icon = Icons.Default.DeleteOutline,
                        iconBg = Color(0xFFEF5350),
                        title = "PIN Dihapus",
                        value = "2",
                        subtitle = "↑ 100% m-o-m",
                        isPositive = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PinMetricCard(
                    icon = Icons.Default.Key,
                    iconBg = MrbGold,
                    title = "Total PIN Aktif",
                    value = "${pinList.count { it.status == "Aktif" }}",
                    subtitle = "PIN sedang digunakan",
                    modifier = Modifier.weight(1f)
                )
                PinMetricCard(
                    icon = Icons.Default.Person,
                    iconBg = MrbGold,
                    title = "Total Sales / Makelar",
                    value = "8",
                    subtitle = "Tim terdaftar",
                    modifier = Modifier.weight(1f)
                )
                PinMetricCard(
                    icon = Icons.Default.Shield,
                    iconBg = MrbGold,
                    title = "PIN Baru Bulan Ini",
                    value = "4",
                    subtitle = "↑ 33.3% dari bulan lalu",
                    isPositive = true,
                    modifier = Modifier.weight(1f)
                )
                PinMetricCard(
                    icon = Icons.Default.DeleteOutline,
                    iconBg = Color(0xFFEF5350),
                    title = "PIN Dihapus Bulan Ini",
                    value = "2",
                    subtitle = "↑ 100% dari bulan lalu",
                    isPositive = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main 2 Columns Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // LEFT COLUMN (Tambah PIN Baru + Ringkasan Donut Chart)
            Column(modifier = Modifier.weight(1f)) {
                // Card 1: Tambah PIN Baru
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = "Tambah PIN Baru", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
                        Text(text = "Buat PIN 4/8-digit baru untuk diberikan kepada sales / makelar.", fontSize = 11.sp, color = MrbTextMuted)

                        Spacer(modifier = Modifier.height(14.dp))

                        AdminTextField(
                            label = "PIN 4/8 Digit Baru",
                            value = inputPinBaru,
                            onValueChange = { if (it.length <= 8) inputPinBaru = it },
                            placeholder = "contoh: 12345678"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (inputPinBaru.length == 4 || inputPinBaru.length == 8) {
                                    viewModel.addSellerPin(inputPinBaru)
                                    val newItem = PinItemData(
                                        pin = inputPinBaru,
                                        createdBy = "Admin MRB",
                                        createdAt = "24 Juli 2026 12:00 WIB",
                                        usedBy = "Sales Baru",
                                        status = "Aktif"
                                    )
                                    onUpdatePinList(listOf(newItem) + pinList)
                                    inputPinBaru = ""
                                    Toast.makeText(context, "✅ PIN $inputPinBaru Berhasil Ditambahkan!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "⚠️ PIN harus berisi 4 atau 8 digit angka!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Tambah PIN", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Info Card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1B1B26),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A3E))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = MrbGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Informasi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MrbGold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "• PIN harus 4 atau 8 digit angka (0000 - 99999999)", fontSize = 10.sp, color = MrbTextMuted)
                                Text(text = "• PIN tidak boleh sama dengan PIN yang sudah ada", fontSize = 10.sp, color = MrbTextMuted)
                                Text(text = "• Berikan PIN hanya kepada tim yang terpercaya", fontSize = 10.sp, color = MrbTextMuted)
                                Text(text = "• PIN dapat digunakan untuk login aplikasi / panel sales", fontSize = 10.sp, color = MrbTextMuted)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card 2: Ringkasan Penggunaan PIN (Donut Chart)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = "Ringkasan Penggunaan PIN", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Donut Chart Canvas
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(120.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeWidth = 18.dp.toPx()
                                    // Aktif (75% -> 270 deg)
                                    drawArc(color = Color(0xFFFFC107), startAngle = -90f, sweepAngle = 270f, useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                                    // Tidak Aktif (18.75% -> 67.5 deg)
                                    drawArc(color = Color(0xFF757575), startAngle = 180f, sweepAngle = 67.5f, useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                                    // Dihapus (6.25% -> 22.5 deg)
                                    drawArc(color = Color(0xFFE53935), startAngle = 247.5f, sweepAngle = 22.5f, useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "${pinList.size}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MrbTextWhite)
                                    Text(text = "Total PIN", fontSize = 9.sp, color = MrbTextMuted)
                                }
                            }

                            // Legend
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ChartLegendItem(color = MrbGold, label = "Aktif", value = "${pinList.count { it.status == "Aktif" }} (75%)")
                                ChartLegendItem(color = Color(0xFF757575), label = "Tidak Aktif", value = "${pinList.count { it.status == "Tidak Aktif" }} (18.75%)")
                                ChartLegendItem(color = Color(0xFFE53935), label = "Dihapus", value = "${pinList.count { it.status == "Dihapus" }} (6.25%)")
                            }
                        }
                    }
                }
            }

            // RIGHT COLUMN (Daftar PIN Penjual Data Table)
            Card(
                modifier = Modifier.weight(1.8f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Daftar PIN Penjual / Sales / Makelar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Search
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Cari PIN...", color = MrbTextMuted, fontSize = 11.sp) },
                                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MrbTextMuted, modifier = Modifier.size(14.dp)) },
                                singleLine = true,
                                modifier = Modifier.width(180.dp).height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF0F0F16),
                                    unfocusedContainerColor = Color(0xFF0F0F16),
                                    focusedBorderColor = MrbGold,
                                    unfocusedBorderColor = Color(0xFF2A2A3E),
                                    focusedTextColor = MrbTextWhite,
                                    unfocusedTextColor = MrbTextWhite
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // TABLE HEADER
                    Surface(
                        color = Color(0xFF1B1B28),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "PIN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MrbGold, modifier = Modifier.width(90.dp))
                            Text(text = "Dibuat Oleh", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite, modifier = Modifier.width(90.dp))
                            Text(text = "Dibuat Pada", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite, modifier = Modifier.width(120.dp))
                            Text(text = "Digunakan Oleh", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite, modifier = Modifier.weight(1f))
                            Text(text = "Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite, modifier = Modifier.width(80.dp))
                            Text(text = "Aksi", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // TABLE ROWS
                    filteredList.forEachIndexed { index, item ->
                        Surface(
                            color = if (index % 2 == 0) Color(0xFF12121A) else Color(0xFF161622),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF242434),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                                ) {
                                    Text(
                                        text = item.pin,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MrbGold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(text = item.createdBy, fontSize = 11.sp, color = MrbTextWhite, modifier = Modifier.width(80.dp))
                                Text(text = item.createdAt, fontSize = 10.sp, color = MrbTextMuted, modifier = Modifier.width(110.dp))
                                Text(text = item.usedBy, fontSize = 11.sp, color = MrbTextWhite, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))

                                // Status Badge
                                val (statusBg, statusTextCol) = when (item.status) {
                                    "Aktif" -> Color(0xFF1B5E20) to Color(0xFF81C784)
                                    "Tidak Aktif" -> Color(0xFFE65100) to Color(0xFFFFB74D)
                                    else -> Color(0xFFB71C1C) to Color(0xFFE57373)
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = statusBg,
                                    modifier = Modifier.width(75.dp)
                                ) {
                                    Text(
                                        text = item.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusTextCol,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier.width(60.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    IconButton(
                                        onClick = { Toast.makeText(context, "✏️ Edit PIN ${item.pin}", Toast.LENGTH_SHORT).show() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MrbGold, modifier = Modifier.size(14.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.removeSellerPin(item.pin)
                                            val updated = pinList.map { if (it.pin == item.pin) it.copy(status = "Dihapus") else it }
                                            onUpdatePinList(updated)
                                            Toast.makeText(context, "🗑️ PIN ${item.pin} dinonaktifkan.", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF5350), modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pagination Footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Menampilkan 1 - ${filteredList.size} dari ${pinList.size} data", fontSize = 11.sp, color = MrbTextMuted)

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(shape = RoundedCornerShape(6.dp), color = MrbGold, modifier = Modifier.size(28.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "1", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E1E2C), modifier = Modifier.size(28.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "2", color = MrbTextWhite, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartLegendItem(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = color, modifier = Modifier.size(10.dp)) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label: ", fontSize = 11.sp, color = MrbTextMuted)
        Text(text = value, fontSize = 11.sp, color = MrbTextWhite, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PinMetricCard(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    value: String,
    subtitle: String,
    isPositive: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262636))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg.copy(alpha = 0.2f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconBg, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = title, fontSize = 11.sp, color = MrbTextMuted)
                Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = MrbTextWhite)
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = if (subtitle.contains("↑")) (if (isPositive) Color(0xFF81C784) else Color(0xFFE57373)) else MrbTextMuted
                )
            }
        }
    }
}

// ==========================================
// 5. OVERVIEW DASHBOARD VIEW
// ==========================================
@Composable
private fun AdminOverviewDashboardView(
    cars: List<CarItem>,
    pendingCars: List<CarItem>,
    creditRequests: List<com.example.data.model.CreditRequest>,
    bookingRequests: List<com.example.data.model.BookingRequest>,
    viewModel: MainViewModel,
    isMobileMode: Boolean = false,
    onGoToAddCar: () -> Unit,
    onGoToManagePin: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(if (isMobileMode) 12.dp else 24.dp)
    ) {
        Text(text = "Dashboard Overview MRB", fontSize = if (isMobileMode) 20.sp else 24.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
        Text(text = "Selamat datang di Panel Kontrol Real-Time Admin Mitra Roda Borneo.", fontSize = 11.sp, color = MrbTextMuted)

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics Grid
        if (isMobileMode) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminStatCard(icon = Icons.Default.DirectionsCar, value = "${cars.size}", label = "Total Mobil", modifier = Modifier.weight(1f))
                    AdminStatCard(icon = Icons.Default.PendingActions, value = "${pendingCars.size}", label = "Pending Sales", modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminStatCard(icon = Icons.Default.Calculate, value = "${creditRequests.size}", label = "Pengajuan Kredit", modifier = Modifier.weight(1f))
                    AdminStatCard(icon = Icons.Default.BookOnline, value = "${bookingRequests.size}", label = "Booking Unit", modifier = Modifier.weight(1f))
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                AdminStatCard(icon = Icons.Default.DirectionsCar, value = "${cars.size}", label = "Total Unit Mobil", modifier = Modifier.weight(1f))
                AdminStatCard(icon = Icons.Default.PendingActions, value = "${pendingCars.size}", label = "Pengajuan Sales", modifier = Modifier.weight(1f))
                AdminStatCard(icon = Icons.Default.Calculate, value = "${creditRequests.size}", label = "Pengajuan Kredit", modifier = Modifier.weight(1f))
                AdminStatCard(icon = Icons.Default.BookOnline, value = "${bookingRequests.size}", label = "Booking Unit", modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Actions Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF191926)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
        ) {
            if (isMobileMode) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Akses Cepat Pengelolaan Real-time", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
                    Text(text = "Tambah unit mobil baru atau kelola PIN sales/makelar.", fontSize = 11.sp, color = MrbTextMuted)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = onGoToAddCar,
                            colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "+ Mobil Baru", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onGoToManagePin,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MrbGold),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Kelola PIN", fontSize = 11.sp)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Akses Cepat Pengelolaan Real-time", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
                        Text(text = "Tambah unit mobil baru dengan form 10-seksi lengkap atau kelola PIN sales/makelar.", fontSize = 11.sp, color = MrbTextMuted)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = onGoToAddCar,
                            colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Tambah Mobil Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onGoToManagePin,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MrbGold),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Kelola PIN Sales", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION: Pending Approval Units from Sales / Makelar
        if (pendingCars.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1812)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = Color(0xFFFFB300), modifier = Modifier.size(10.dp)) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pengajuan Unit Baru dari Sales / Makelar (${pendingCars.size} Menunggu Persetujuan)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MrbGold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    pendingCars.forEach { pendingUnit ->
                        Surface(
                            color = Color(0xFF13131A),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF262636), modifier = Modifier.size(50.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = MrbGold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = pendingUnit.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
                                        Text(text = "${pendingUnit.year} • Rp ${String.format("%,d", pendingUnit.priceRp).replace(',', '.')} • ${pendingUnit.location}", fontSize = 11.sp, color = MrbTextMuted)
                                        Text(text = "Diajukan oleh: ${pendingUnit.dealerName}", fontSize = 10.sp, color = MrbGold)
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            viewModel.approveCar(pendingUnit.id)
                                            Toast.makeText(context, "✅ Unit '${pendingUnit.name}' disetujui dan langsung tampil di Dashboard Pembeli & Sales!", Toast.LENGTH_LONG).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32), contentColor = Color.White),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(text = "✅ Setujui", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.rejectCar(pendingUnit.id, "Spesifikasi perlu dilengkapi")
                                            Toast.makeText(context, "❌ Pengajuan unit ditolak.", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(text = "❌ Tolak", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Recent Cars List
        Text(text = "Stok Unit Terdaftar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
        Spacer(modifier = Modifier.height(10.dp))

        cars.take(5).forEach { car ->
            AdminCarRowCard(car = car, viewModel = viewModel)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AdminCarRowCard(car: CarItem, viewModel: MainViewModel) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showEditDialog = true },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222232))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1A1A28),
                        modifier = Modifier.size(56.dp)
                    ) {
                        if (car.imageRes != null) {
                            Image(painter = painterResource(id = car.imageRes), contentDescription = null, contentScale = ContentScale.Crop)
                        } else if (!car.imageUrl.isNullOrBlank()) {
                            AsyncImage(model = car.imageUrl, contentDescription = null, contentScale = ContentScale.Crop)
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = MrbGold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = car.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
                            if (car.isPromo) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE53935)) {
                                    Text(text = "PROMO 🔥", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${car.year} • Rp ${String.format("%,d", car.priceRp).replace(',', '.')} • ${car.category}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MrbGold
                        )
                        Text(
                            text = "Mesin: ${car.engine.ifBlank { "Standard" }} | Transmisi: ${car.transmission}",
                            fontSize = 10.sp,
                            color = MrbTextMuted
                        )
                    }
                }

                // Status & Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Quick Status Cycle Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (car.status) {
                            "Ready" -> Color(0xFF1B5E20)
                            "Booking" -> Color(0xFFF57F17)
                            "Terjual" -> Color(0xFF424242)
                            else -> Color(0xFFB71C1C)
                        },
                        modifier = Modifier.clickable {
                            val nextStatus = when (car.status) {
                                "Ready" -> "Booking"
                                "Booking" -> "Terjual"
                                else -> "Ready"
                            }
                            viewModel.updateCarStatus(car.id, nextStatus)
                            Toast.makeText(context, "⚡ Status unit ${car.name} diubah ke: $nextStatus (Tersinkron Real-time)", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(
                            text = car.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Edit Button
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Unit", tint = MrbGold, modifier = Modifier.size(18.dp))
                    }

                    // Delete Button
                    IconButton(
                        onClick = {
                            viewModel.deleteCarAdmin(car.id)
                            Toast.makeText(context, "🗑️ Unit ${car.name} berhasil dihapus dari database pusat.", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus Unit", tint = Color(0xFFEF5350), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EditCarModalDialog(
            car = car,
            viewModel = viewModel,
            onDismiss = { showEditDialog = false }
        )
    }
}

// ==========================================
// EDIT CAR MODAL DIALOG (SINGLE SOURCE OF TRUTH)
// ==========================================
@Composable
private fun EditCarModalDialog(
    car: CarItem,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(car.name) }
    var brand by remember { mutableStateOf(car.brand) }
    var model by remember { mutableStateOf(car.model) }
    var yearStr by remember { mutableStateOf(car.year.toString()) }
    var priceStr by remember { mutableStateOf(car.priceRp.toString()) }
    var category by remember { mutableStateOf(car.category) }
    var status by remember { mutableStateOf(car.status) }
    var isPromo by remember { mutableStateOf(car.isPromo) }
    var isReadyCredit by remember { mutableStateOf(car.isReadyCredit) }
    var isHot by remember { mutableStateOf(car.isHot) }
    var isVerified by remember { mutableStateOf(car.isVerified) }

    var engine by remember { mutableStateOf(car.engine) }
    var transmission by remember { mutableStateOf(car.transmission) }
    var fuelType by remember { mutableStateOf(car.fuelType) }
    var color by remember { mutableStateOf(car.color) }
    var kilometerStr by remember { mutableStateOf(car.kilometer.toString()) }
    var stnkStatus by remember { mutableStateOf(car.stnkStatus) }
    var bpkbStatus by remember { mutableStateOf(car.bpkbStatus) }
    var taxStatus by remember { mutableStateOf(car.taxStatus) }
    var imageUrl by remember { mutableStateOf(car.imageUrl ?: "") }
    var description by remember { mutableStateOf(car.description) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF14141E),
            border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.EditNote, contentDescription = null, tint = MrbGold, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Edit Data Unit Mobil", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
                            Text(text = "Pengelolaan Pusat • Single Source of Truth", fontSize = 10.sp, color = MrbGold)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = MrbTextMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF222232))

                // Scrollable Form Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // SECTION 1: DOKUMEN & INFORMASI UTAMA
                    Text(text = "1. Informasi Utama Unit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MrbGold)
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Lengkap Mobil") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = Color(0xFF2E2E3E),
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Merek") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Model") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = yearStr,
                            onValueChange = { yearStr = it },
                            label = { Text("Tahun") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Kategori (MPV/SUV/dll)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                    }

                    // SECTION 2: HARGA & SIMULASI KREDIT & PROMO
                    Text(text = "2. Harga, Promo & Status Unit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MrbGold)

                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Harga Cash (Rp)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                        singleLine = true
                    )

                    // Status Dropdown selector
                    Text(text = "Status Ketersediaan Unit:", fontSize = 11.sp, color = MrbTextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Ready", "Booking", "Terjual", "Menunggu Persetujuan").forEach { st ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (status == st) MrbGold else Color(0xFF1E1E2E),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (status == st) MrbGold else Color(0xFF2E2E3E)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { status = st }
                            ) {
                                Text(
                                    text = st,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (status == st) Color.Black else MrbTextWhite,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    // Toggles for Promo, Credit, Hot Unit, Verification
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isPromo, onCheckedChange = { isPromo = it })
                            Text(text = "Aktifkan Label/Badge Promo Diskon 🔥", fontSize = 12.sp, color = MrbTextWhite)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isReadyCredit, onCheckedChange = { isReadyCredit = it })
                            Text(text = "Bisa Pengajuan Simulasi Kredit Real-time 💳", fontSize = 12.sp, color = MrbTextWhite)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isHot, onCheckedChange = { isHot = it })
                            Text(text = "Tampilkan sebagai Unit HOT / Best Seller 🔥", fontSize = 12.sp, color = MrbTextWhite)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isVerified, onCheckedChange = { isVerified = it })
                            Text(text = "Tanda Terverifikasi (Inspeksi MRB 100% Layak) ✅", fontSize = 12.sp, color = MrbTextWhite)
                        }
                    }

                    // SECTION 3: SPESIFIKASI MESIN & SURAT-SURAT
                    Text(text = "3. Spesifikasi Mesin & Legalitas Surat", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MrbGold)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = engine,
                            onValueChange = { engine = it },
                            label = { Text("Kapasitas Mesin & Kode") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = transmission,
                            onValueChange = { transmission = it },
                            label = { Text("Transmisi") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fuelType,
                            onValueChange = { fuelType = it },
                            label = { Text("Bahan Bakar") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = color,
                            onValueChange = { color = it },
                            label = { Text("Warna Body") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = kilometerStr,
                            onValueChange = { kilometerStr = it },
                            label = { Text("Kilometer (KM)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = taxStatus,
                            onValueChange = { taxStatus = it },
                            label = { Text("Status Pajak") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = stnkStatus,
                            onValueChange = { stnkStatus = it },
                            label = { Text("Status STNK") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = bpkbStatus,
                            onValueChange = { bpkbStatus = it },
                            label = { Text("Status BPKB") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                            singleLine = true
                        )
                    }

                    // SECTION 4: FOTO & DESKRIPSI
                    Text(text = "4. Foto URL & Deskripsi Lengkap", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MrbGold)

                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("URL Foto Utama (Opsional HTTPS / Asset)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi Kondisi & Catatan Inspeksi Unit") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MrbGold, unfocusedBorderColor = Color(0xFF2E2E3E), focusedTextColor = MrbTextWhite, unfocusedTextColor = MrbTextWhite)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF222232))

                // Footer Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Batal", color = MrbTextMuted)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val parsedYear = yearStr.toIntOrNull() ?: car.year
                            val parsedPrice = priceStr.toLongOrNull() ?: car.priceRp
                            val parsedKm = kilometerStr.toIntOrNull() ?: car.kilometer

                            val updatedCar = car.copy(
                                name = name.ifBlank { car.name },
                                brand = brand.ifBlank { car.brand },
                                model = model.ifBlank { car.model },
                                year = parsedYear,
                                priceRp = parsedPrice,
                                category = category.ifBlank { car.category },
                                status = status,
                                isPromo = isPromo,
                                isReadyCredit = isReadyCredit,
                                isHot = isHot,
                                isVerified = isVerified,
                                engine = engine,
                                transmission = transmission,
                                fuelType = fuelType,
                                color = color,
                                kilometer = parsedKm,
                                stnkStatus = stnkStatus,
                                bpkbStatus = bpkbStatus,
                                taxStatus = taxStatus,
                                imageUrl = if (imageUrl.isNotBlank()) imageUrl else car.imageUrl,
                                description = description
                            )

                            viewModel.updateCarAdmin(updatedCar)
                            Toast.makeText(
                                context,
                                "⚡ Perubahan $name disimpan! Otomatis tersinkron ke Marketplace Pembeli & Penjual.",
                                Toast.LENGTH_LONG
                            ).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Simpan & Sinkron Real-time", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. UNIT MOBIL VIEW
// ==========================================
@Composable
private fun AdminUnitMobilView(
    cars: List<CarItem>,
    pendingCars: List<CarItem>,
    viewModel: MainViewModel,
    isMobileMode: Boolean = false,
    onGoToAddCar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(if (isMobileMode) 12.dp else 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Kelola Unit Mobil", fontSize = if (isMobileMode) 20.sp else 24.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
                Text(text = "Total ${cars.size} unit mobil terdaftar.", fontSize = 11.sp, color = MrbTextMuted)
            }

            Button(
                onClick = onGoToAddCar,
                colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isMobileMode) "+ Baru" else "Tambah Mobil Baru", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        cars.forEach { car ->
            AdminCarRowCard(car = car, viewModel = viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// ==========================================
// 7. BOOKING & APPROVAL VIEW
// ==========================================
@Composable
private fun AdminBookingAndApprovalView(
    pendingCars: List<CarItem>,
    bookingRequests: List<com.example.data.model.BookingRequest>,
    viewModel: MainViewModel,
    isMobileMode: Boolean = false
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(if (isMobileMode) 12.dp else 24.dp)
    ) {
        Text(text = "Pesanan, Booking & Persetujuan", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
        Text(text = "Kelola pengajuan unit dari Sales/Makelar dan pesanan booking pelanggan.", fontSize = 12.sp, color = MrbTextMuted)

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Pengajuan Unit Sales (Approval Queue)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MrbGold)
        Spacer(modifier = Modifier.height(10.dp))

        if (pendingCars.isEmpty()) {
            Surface(
                color = Color(0xFF14141E),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Tidak ada pengajuan unit yang pending.", color = MrbTextMuted, fontSize = 12.sp, modifier = Modifier.padding(16.dp))
            }
        } else {
            pendingCars.forEach { pendingUnit ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A28)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = pendingUnit.name, color = MrbTextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Diajukan oleh Sales: ${pendingUnit.dealerName}", color = MrbGold, fontSize = 11.sp)
                            Text(text = "Harga: Rp ${String.format("%,d", pendingUnit.priceRp).replace(',', '.')}", color = MrbTextMuted, fontSize = 11.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.approveCar(pendingUnit.id)
                                    Toast.makeText(context, "✅ Disetujui! Langsung terpublikasi ke Seluruh Panel.", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32), contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "✅ Setujui", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.rejectCar(pendingUnit.id, "Ditolak oleh Admin")
                                    Toast.makeText(context, "❌ Ditolak.", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "❌ Tolak", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Pesanan Booking Unit Pelanggan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
        Spacer(modifier = Modifier.height(10.dp))

        bookingRequests.forEach { req ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = req.carName, color = MrbTextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Pelanggan: ${req.userName} (${req.userPhone})", color = MrbTextMuted, fontSize = 11.sp)
                        Text(text = "Tanggal Booking: ${req.bookingDate}", color = MrbGold, fontSize = 11.sp)
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1B5E20)
                    ) {
                        Text(text = req.status, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}

// Helper for Stat Cards
@Composable
private fun AdminStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF242436))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MrbGold.copy(alpha = 0.2f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = MrbGold, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = MrbTextWhite)
                Text(text = label, fontSize = 11.sp, color = MrbTextMuted)
            }
        }
    }
}

// Generic Placeholder for Other Tabs
@Composable
private fun GenericPlaceholderAdminTab(title: String, category: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = MrbGold, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
        Text(text = "Modul $category - Terhubung ke Real-time Single Source of Truth MRB.", fontSize = 12.sp, color = MrbTextMuted)
    }
}
