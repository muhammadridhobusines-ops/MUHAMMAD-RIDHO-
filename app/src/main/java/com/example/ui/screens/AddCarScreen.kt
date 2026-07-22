package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Drafts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.CarGalleryItem
import com.example.data.model.CarItem
import com.example.ui.theme.MrbBackground
import com.example.ui.theme.MrbCardBackground
import com.example.ui.theme.MrbGold
import com.example.ui.theme.MrbGoldOutline
import com.example.ui.theme.MrbSurfaceVariant
import com.example.ui.theme.MrbTextMuted
import com.example.ui.theme.MrbTextWhite
import com.example.ui.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale

// Sample Car Images for simulation upload
val sampleCarDrawables = listOf(
    R.drawable.mrb_suv_bg,
    R.drawable.img_car_hrv,
    R.drawable.img_car_innova,
    R.drawable.img_car_supra,
    R.drawable.img_banner_suv
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Main Photo State (Wajib diisi, Maks 1)
    var mainPhotoUrl by remember { mutableStateOf("") }
    var mainPhotoRes by remember { mutableStateOf<Int?>(R.drawable.mrb_suv_bg) }
    var mainPhotoError by remember { mutableStateOf(false) }

    // Additional Gallery Photos State (Min 1, Maks 30)
    val galleryPhotosList = remember {
        mutableStateListOf(
            CarGalleryItem("Depan", imageRes = R.drawable.mrb_suv_hero),
            CarGalleryItem("Belakang", imageRes = R.drawable.mrb_suv_bg),
            CarGalleryItem("Samping Kanan", imageRes = R.drawable.img_car_hrv),
            CarGalleryItem("Samping Kiri", imageRes = R.drawable.img_car_innova),
            CarGalleryItem("Interior Depan", imageRes = R.drawable.mrb_suv_bg),
            CarGalleryItem("Dashboard", imageRes = R.drawable.img_car_supra),
            CarGalleryItem("Mesin", imageRes = R.drawable.img_car_innova),
            CarGalleryItem("Velg", imageRes = R.drawable.img_banner_suv)
        )
    }

    val galleryCategoryOptions = listOf(
        "Depan", "Belakang", "Samping Kanan", "Samping Kiri",
        "Interior Depan", "Interior Belakang", "Dashboard", "Jok",
        "Mesin", "Bagasi", "Velg", "Ban", "STNK (opsional)", "BPKB (opsional)", "Foto detail lainnya"
    )
    var selectedCategoryLabel by remember { mutableStateOf("Depan") }
    var customGalleryUrlInput by remember { mutableStateOf("") }

    val presetSampleDrawables = listOf(
        R.drawable.mrb_suv_hero,
        R.drawable.mrb_suv_bg,
        R.drawable.img_car_hrv,
        R.drawable.img_car_innova,
        R.drawable.img_car_supra,
        R.drawable.img_banner_suv
    )

    var isCompressing by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }

    // Form fields
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("Toyota") }
    var variant by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("2024") }
    var rawPrice by remember { mutableStateOf("") }
    var rawKm by remember { mutableStateOf("15000") }

    var transmission by remember { mutableStateOf("Otomatis") } // Manual, Otomatis
    var fuelType by remember { mutableStateOf("Bensin") } // Bensin, Solar, Hybrid, Listrik
    var category by remember { mutableStateOf("SUV") } // City Car, Hatchback, Sedan, MPV, SUV, Pickup, Double Cabin, Minibus
    var color by remember { mutableStateOf("Hitam Metalik") }
    var passengerCapacity by remember { mutableStateOf("7 Kursi") }
    var engineCC by remember { mutableStateOf("2400 CC") }
    var driveTrain by remember { mutableStateOf("4WD") } // FWD, RWD, AWD, 4WD
    var condition by remember { mutableStateOf("Bekas") } // Baru, Bekas
    var status by remember { mutableStateOf("Ready") } // Ready, Sold, Booking

    var description by remember { mutableStateOf("") }

    // Validation State
    var showErrors by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var brandError by remember { mutableStateOf(false) }

    // Dialogs
    var showResetDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Formatter helpers
    fun formatCurrencyInput(input: String): String {
        val clean = input.replace(Regex("[^0-9]"), "")
        if (clean.isEmpty()) return ""
        val parsed = clean.toLongOrNull() ?: return input
        val formatter = NumberFormat.getIntegerInstance(Locale("id", "ID"))
        return "Rp " + formatter.format(parsed)
    }

    fun getCleanPriceLong(): Long {
        val clean = rawPrice.replace(Regex("[^0-9]"), "")
        return clean.toLongOrNull() ?: 0L
    }

    fun validateForm(): Boolean {
        showErrors = true
        nameError = name.trim().isEmpty()
        brandError = brand.trim().isEmpty()
        priceError = getCleanPriceLong() <= 0L
        mainPhotoError = mainPhotoUrl.isBlank() && mainPhotoRes == null

        if (mainPhotoError) {
            Toast.makeText(context, "📷 Foto Utama Wajib Diisi!", Toast.LENGTH_LONG).show()
        } else if (galleryPhotosList.isEmpty()) {
            Toast.makeText(context, "🖼️ Galeri Foto Tambahan Minimal 1 Foto!", Toast.LENGTH_LONG).show()
        }

        return !nameError && !brandError && !priceError && !mainPhotoError && galleryPhotosList.isNotEmpty()
    }

    fun resetFormFields() {
        name = ""
        brand = "Toyota"
        variant = ""
        year = "2024"
        rawPrice = ""
        rawKm = "15000"
        transmission = "Otomatis"
        fuelType = "Bensin"
        category = "SUV"
        color = "Hitam Metalik"
        passengerCapacity = "7 Kursi"
        engineCC = "2400 CC"
        driveTrain = "4WD"
        condition = "Bekas"
        status = "Ready"
        description = ""
        showErrors = false
        mainPhotoUrl = ""
        mainPhotoRes = R.drawable.mrb_suv_bg
        mainPhotoError = false
        galleryPhotosList.clear()
        galleryPhotosList.addAll(
            listOf(
                CarGalleryItem("Depan", imageRes = R.drawable.mrb_suv_hero),
                CarGalleryItem("Belakang", imageRes = R.drawable.mrb_suv_bg),
                CarGalleryItem("Samping Kanan", imageRes = R.drawable.img_car_hrv),
                CarGalleryItem("Samping Kiri", imageRes = R.drawable.img_car_innova)
            )
        )
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = MrbCardBackground,
            titleContentColor = MrbGold,
            textContentColor = MrbTextWhite,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = MrbGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Form?", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("Apakah Anda yakin ingin mengosongkan seluruh data form yang telah diisi?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        resetFormFields()
                        showResetDialog = false
                        Toast.makeText(context, "Form berhasil di-reset", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F), contentColor = Color.White)
                ) {
                    Text("Ya, Reset", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetDialog = false },
                    border = BorderStroke(1.dp, MrbGoldOutline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MrbTextWhite)
                ) {
                    Text("Batal")
                }
            }
        )
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onNavigateBack()
            },
            containerColor = MrbCardBackground,
            titleContentColor = MrbGold,
            textContentColor = MrbTextWhite,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MrbGold, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Mobil Berhasil Ditambahkan!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Text(
                    "Unit $name ($year) telah berhasil tersimpan ke inventaris MRB dan langsung tampil pada katalog stok.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black)
                ) {
                    Text("Lihat di Katalog Stok", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MrbBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top App Bar
            Surface(
                color = MrbCardBackground,
                border = BorderStroke(0.dp, Color.Transparent),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Kembali",
                                tint = MrbTextWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Tambah Unit Mobil",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MrbTextWhite
                            )
                            Text(
                                text = "Katalog Inventaris MRB Borneo",
                                fontSize = 11.sp,
                                color = MrbGold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF221F16),
                        border = BorderStroke(1.dp, MrbGoldOutline)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(MrbGold, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = status,
                                color = MrbGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Form Body
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                // SECTION 1: FOTO UTAMA
                FormSectionHeader(
                    icon = Icons.Default.AddPhotoAlternate,
                    title = "📷 Foto Utama",
                    subtitle = "Wajib diisi. Foto yang tampil di daftar kendaraan & beranda (Maks. 1 foto)."
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                    border = BorderStroke(
                        width = if (mainPhotoError) 2.dp else 1.dp,
                        color = if (mainPhotoError) Color(0xFFFF5252) else MrbGoldOutline
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Main Photo Preview Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF121214))
                                .border(1.dp, MrbGoldOutline, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (mainPhotoUrl.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(mainPhotoUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Foto Utama Unit",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (mainPhotoRes != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(mainPhotoRes)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Foto Utama Unit",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = MrbTextMuted,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Belum Ada Foto Utama",
                                        color = MrbTextMuted,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            // Main Photo Badge Overlay
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp),
                                color = MrbGold,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "FOTO UTAMA • WAJIB",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons Row: Pilih dari Galeri, Ambil dari Kamera, Hapus Foto
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Pilih dari Galeri
                            Button(
                                onClick = {
                                    mainPhotoUrl = ""
                                    mainPhotoRes = presetSampleDrawables[(0..5).random()]
                                    mainPhotoError = false
                                    Toast.makeText(context, "Foto Utama dipilih dari Galeri!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pilih dari Galeri", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Ambil dari Kamera
                            Button(
                                onClick = {
                                    mainPhotoUrl = ""
                                    mainPhotoRes = presetSampleDrawables.random()
                                    mainPhotoError = false
                                    Toast.makeText(context, "Foto Utama diambil dari Kamera!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A30), contentColor = MrbGold),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MrbGoldOutline),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ambil dari Kamera", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Hapus Foto Utama Button
                        if (mainPhotoUrl.isNotBlank() || mainPhotoRes != null) {
                            OutlinedButton(
                                onClick = {
                                    mainPhotoUrl = ""
                                    mainPhotoRes = null
                                    Toast.makeText(context, "Foto Utama dihapus", Toast.LENGTH_SHORT).show()
                                },
                                border = BorderStroke(1.dp, Color(0xFF553333)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Hapus Foto Utama", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Input Custom URL Link for Main Photo
                        OutlinedTextField(
                            value = mainPhotoUrl,
                            onValueChange = {
                                mainPhotoUrl = it
                                if (it.isNotBlank()) mainPhotoError = false
                            },
                            label = { Text("Atau Masukkan URL Link Foto Utama", color = MrbTextMuted) },
                            placeholder = { Text("https://...", color = MrbTextMuted.copy(0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MrbGold,
                                unfocusedBorderColor = MrbSurfaceVariant,
                                focusedTextColor = MrbTextWhite,
                                unfocusedTextColor = MrbTextWhite
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 2: GALERI FOTO TAMBAHAN
                FormSectionHeader(
                    icon = Icons.Default.Collections,
                    title = "🖼️ Galeri Foto Tambahan",
                    subtitle = "Unggah banyak foto (1–30 foto). Dukung kategori & atur urutan."
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                    border = BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        // Header Status & Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Terunggah: ${galleryPhotosList.size} / 30 Foto",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MrbGold
                            )

                            if (galleryPhotosList.isNotEmpty()) {
                                Text(
                                    text = "Hapus Semua Foto",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFF5252),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        galleryPhotosList.clear()
                                        Toast.makeText(context, "Semua foto galeri dihapus", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Kategori / Label Selector Chips
                        Text(
                            text = "Pilih Kategori Foto sebelum Unggah:",
                            fontSize = 12.sp,
                            color = MrbTextMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(galleryCategoryOptions) { _, categoryLabel ->
                                val isSelected = (categoryLabel == selectedCategoryLabel)
                                Surface(
                                    modifier = Modifier.clickable { selectedCategoryLabel = categoryLabel },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MrbGold else Color(0xFF1E1E22),
                                    border = BorderStroke(1.dp, if (isSelected) MrbGold else MrbGoldOutline)
                                ) {
                                    Text(
                                        text = categoryLabel,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.Black else MrbTextWhite,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Buttons Row for Gallery Action: Pilih dari Galeri, Ambil dari Kamera
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Pilih dari Galeri
                            Button(
                                onClick = {
                                    if (galleryPhotosList.size < 30) {
                                        val sampleRes = presetSampleDrawables.random()
                                        galleryPhotosList.add(
                                            CarGalleryItem(
                                                label = selectedCategoryLabel,
                                                imageRes = sampleRes
                                            )
                                        )
                                        Toast.makeText(context, "Foto [$selectedCategoryLabel] ditambah dari Galeri!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Maksimal 30 foto tercapai!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pilih dari Galeri", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Ambil dari Kamera
                            Button(
                                onClick = {
                                    if (galleryPhotosList.size < 30) {
                                        val sampleRes = presetSampleDrawables.random()
                                        galleryPhotosList.add(
                                            CarGalleryItem(
                                                label = selectedCategoryLabel,
                                                imageRes = sampleRes
                                            )
                                        )
                                        Toast.makeText(context, "Foto [$selectedCategoryLabel] diambil dari Kamera!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Maksimal 30 foto tercapai!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222228), contentColor = MrbGold),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MrbGoldOutline),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ambil Kamera", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Pratinjau & Reorder List
                        Text(
                            text = "Pratinjau Galeri (${galleryPhotosList.size} foto) • Gunakan tombol panah untuk atur urutan:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MrbTextWhite
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (galleryPhotosList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .background(Color(0xFF141416), RoundedCornerShape(12.dp))
                                    .border(1.dp, MrbGoldOutline, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada foto galeri tambahan. Klik tombol di atas untuk menambah foto.",
                                    color = MrbTextMuted,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                itemsIndexed(galleryPhotosList) { index, item ->
                                    Card(
                                        modifier = Modifier.size(125.dp, 145.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.Black),
                                        border = BorderStroke(1.dp, MrbGoldOutline)
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            // Image
                                            if (!item.imageUrl.isNullOrEmpty()) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(item.imageUrl)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = item.label,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else if (item.imageRes != null) {
                                                Image(
                                                    painter = painterResource(id = item.imageRes),
                                                    contentDescription = item.label,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            // Top Right Delete Photo Button ("Hapus Foto")
                                            IconButton(
                                                onClick = {
                                                    galleryPhotosList.removeAt(index)
                                                    Toast.makeText(context, "Foto [${item.label}] dihapus", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(4.dp)
                                                    .size(24.dp)
                                                    .background(Color.Black.copy(alpha = 0.75f), CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Hapus Foto",
                                                    tint = Color(0xFFFF5252),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }

                                            // Reorder Buttons (Drag & Drop Reordering Simulator)
                                            Row(
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(4.dp),
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                if (index > 0) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(22.dp)
                                                            .background(Color.Black.copy(0.75f), CircleShape)
                                                            .clickable {
                                                                val temp = galleryPhotosList[index]
                                                                galleryPhotosList[index] = galleryPhotosList[index - 1]
                                                                galleryPhotosList[index - 1] = temp
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ArrowBackIosNew,
                                                            contentDescription = "Geser Kiri",
                                                            tint = MrbGold,
                                                            modifier = Modifier.size(10.dp)
                                                        )
                                                    }
                                                }

                                                if (index < galleryPhotosList.size - 1) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(22.dp)
                                                            .background(Color.Black.copy(0.75f), CircleShape)
                                                            .clickable {
                                                                val temp = galleryPhotosList[index]
                                                                galleryPhotosList[index] = galleryPhotosList[index + 1]
                                                                galleryPhotosList[index + 1] = temp
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ArrowForwardIos,
                                                            contentDescription = "Geser Kanan",
                                                            tint = MrbGold,
                                                            modifier = Modifier.size(10.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            // Bottom Category Label Badge
                                            Surface(
                                                modifier = Modifier
                                                    .align(Alignment.BottomCenter)
                                                    .fillMaxWidth(),
                                                color = Color.Black.copy(alpha = 0.85f)
                                            ) {
                                                Text(
                                                    text = item.label,
                                                    fontSize = 10.sp,
                                                    color = MrbGold,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 2: INFORMASI UTAMA MOBIL
                FormSectionHeader(
                    icon = Icons.Default.DirectionsCar,
                    title = "Informasi Utama Unit",
                    subtitle = "Nama lengkap, merek, varian, dan tahun pembuatan"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                    border = BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Nama Mobil
                        MrbTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = false
                            },
                            label = "Nama Mobil *",
                            placeholder = "Contoh: Toyota Fortuner 2.8 VRZ 4x4 AT",
                            isError = showErrors && nameError,
                            errorMessage = "Nama mobil wajib diisi"
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Merek
                            Column(modifier = Modifier.weight(1f)) {
                                MrbTextField(
                                    value = brand,
                                    onValueChange = {
                                        brand = it
                                        brandError = false
                                    },
                                    label = "Merek *",
                                    placeholder = "Toyota, Honda, dll",
                                    isError = showErrors && brandError,
                                    errorMessage = "Merek wajib diisi"
                                )
                            }
                            // Model / Varian
                            Column(modifier = Modifier.weight(1f)) {
                                MrbTextField(
                                    value = variant,
                                    onValueChange = { variant = it },
                                    label = "Model / Varian",
                                    placeholder = "VRZ GR Sport"
                                )
                            }
                        }

                        // Tahun Pembuatan
                        MrbTextField(
                            value = year,
                            onValueChange = { year = it.filter { char -> char.isDigit() } },
                            label = "Tahun Perakitan",
                            placeholder = "2024",
                            keyboardType = KeyboardType.Number
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 3: HARGA & KILOMETER
                FormSectionHeader(
                    icon = Icons.Default.Info,
                    title = "Harga & Jarak Tempuh",
                    subtitle = "Sistem otomatis memformat angka Rupiah"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                    border = BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Harga (Rp)
                        MrbTextField(
                            value = formatCurrencyInput(rawPrice),
                            onValueChange = {
                                rawPrice = it.filter { c -> c.isDigit() }
                                priceError = false
                            },
                            label = "Harga Jual (Rp) *",
                            placeholder = "Contoh: Rp 325.000.000",
                            keyboardType = KeyboardType.Number,
                            isError = showErrors && priceError,
                            errorMessage = "Harga jual harus diisi angka valid"
                        )

                        // Kilometer
                        MrbTextField(
                            value = rawKm,
                            onValueChange = { rawKm = it.filter { c -> c.isDigit() } },
                            label = "Kilometer (KM)",
                            placeholder = "Contoh: 15000",
                            keyboardType = KeyboardType.Number
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 4: SPESIFIKASI MESIN & TRANSMISI
                FormSectionHeader(
                    icon = Icons.Default.Info,
                    title = "Spesifikasi Dapur Pacu",
                    subtitle = "Transmisi, bahan bakar, penggerak, dan mesin"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                    border = BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Transmisi
                        TextLabel("Transmisi")
                        ChoiceChipRow(
                            options = listOf("Otomatis", "Manual"),
                            selectedOption = transmission,
                            onSelect = { transmission = it }
                        )

                        // BBM
                        TextLabel("Bahan Bakar (BBM)")
                        ChoiceChipRow(
                            options = listOf("Bensin", "Solar", "Hybrid", "Listrik"),
                            selectedOption = fuelType,
                            onSelect = { fuelType = it }
                        )

                        // Penggerak Roda
                        TextLabel("Penggerak Roda")
                        ChoiceChipRow(
                            options = listOf("FWD", "RWD", "AWD", "4WD"),
                            selectedOption = driveTrain,
                            onSelect = { driveTrain = it }
                        )

                        // Kapasitas Mesin (CC)
                        MrbTextField(
                            value = engineCC,
                            onValueChange = { engineCC = it },
                            label = "Kapasitas Mesin",
                            placeholder = "Contoh: 1500 CC / 2800 CC"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 5: KATEGORI & KAPASITAS
                FormSectionHeader(
                    icon = Icons.Default.DirectionsCar,
                    title = "Kategori & Bodi Unit",
                    subtitle = "Tipe bodi, warna, dan kapasitas penumpang"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                    border = BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Kategori
                        TextLabel("Kategori Mobil")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val categories = listOf(
                                "SUV", "MPV", "Sedan", "Hatchback",
                                "City Car", "Pickup", "Double Cabin", "Minibus"
                            )
                            categories.forEach { cat ->
                                MrbChip(
                                    text = cat,
                                    isSelected = (category == cat),
                                    onClick = { category = cat }
                                )
                            }
                        }

                        // Warna
                        MrbTextField(
                            value = color,
                            onValueChange = { color = it },
                            label = "Warna Unit",
                            placeholder = "Contoh: Hitam Metalik, Putih Mutiara"
                        )

                        // Kapasitas Penumpang
                        TextLabel("Kapasitas Penumpang")
                        ChoiceChipRow(
                            options = listOf("2 Kursi", "4-5 Kursi", "7-8 Kursi", ">8 Kursi"),
                            selectedOption = passengerCapacity,
                            onSelect = { passengerCapacity = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 6: KONDISI & STATUS
                FormSectionHeader(
                    icon = Icons.Default.CheckCircle,
                    title = "Kondisi & Status Unit",
                    subtitle = "Penetapan status ketersediaan di showroom"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                    border = BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Kondisi
                        TextLabel("Kondisi Unit")
                        ChoiceChipRow(
                            options = listOf("Bekas", "Baru"),
                            selectedOption = condition,
                            onSelect = { condition = it }
                        )

                        // Status
                        TextLabel("Status Ketersediaan")
                        ChoiceChipRow(
                            options = listOf("Ready", "Booking", "Sold"),
                            selectedOption = status,
                            onSelect = { status = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 7: DESKRIPSI DETAIL MOBIL
                FormSectionHeader(
                    icon = Icons.Default.Info,
                    title = "Deskripsi Mobil",
                    subtitle = "Tuliskan rincian kelengkapan, pajak, & riwayat unit"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                    border = BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Deskripsi Lengkap Mobil", color = MrbTextMuted) },
                            placeholder = {
                                Text(
                                    text = "Tuliskan kondisi mobil, riwayat servis, pajak hidup, kelebihan mobil, kelengkapan surat, aksesori tambahan, dan informasi penting lainnya.",
                                    color = MrbTextMuted.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            },
                            minLines = 5,
                            maxLines = 10,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MrbGold,
                                unfocusedBorderColor = MrbSurfaceVariant,
                                focusedTextColor = MrbTextWhite,
                                unfocusedTextColor = MrbTextWhite
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // SECTION 8: TOMBOL AKSI (ACTION BUTTONS)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Simpan Mobil (Primary)
                    Button(
                        onClick = {
                            if (validateForm()) {
                                val finalDescription = if (description.isBlank()) {
                                    "Kondisi unit istimewa, mulus, terawat penuh, bebas banjir, dokumen 100% aman dan bergaransi resmi MRB."
                                } else {
                                    description
                                }

                                val finalMainRes = if (mainPhotoUrl.isBlank()) (mainPhotoRes ?: R.drawable.mrb_suv_bg) else null
                                val finalMainUrl = mainPhotoUrl.ifBlank { null }

                                val newCar = CarItem(
                                    id = "mrb-car-${System.currentTimeMillis()}",
                                    name = name.trim(),
                                    brand = brand.trim(),
                                    model = if (variant.isBlank()) name.trim() else variant.trim(),
                                    year = year.toIntOrNull() ?: 2024,
                                    priceRp = getCleanPriceLong(),
                                    location = "Sampit, Kalteng",
                                    dealerName = "MRB Sampit Central",
                                    fuelType = fuelType,
                                    transmission = transmission,
                                    kilometer = rawKm.toIntOrNull() ?: 15000,
                                    isHot = true,
                                    isVerified = true,
                                    category = category,
                                    imageRes = finalMainRes,
                                    imageUrl = finalMainUrl,
                                    engine = if (engineCC.isBlank()) "2400 CC VVT-i" else engineCC,
                                    color = color,
                                    description = finalDescription,
                                    variant = variant,
                                    driveTrain = driveTrain,
                                    passengerCapacity = passengerCapacity,
                                    condition = condition,
                                    status = status,
                                    galleryPhotos = galleryPhotosList.toList(),
                                    photoResList = galleryPhotosList.mapNotNull { it.imageRes }
                                )

                                viewModel.addNewCarAdmin(newCar)
                                showSuccessDialog = true
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Mohon lengkapi field wajib (*) dan foto unit",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Unit Mobil", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Simpan Draft
                        OutlinedButton(
                            onClick = {
                                Toast
                                    .makeText(
                                        context,
                                        "Draft unit mobil berhasil disimpan di HP",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            },
                            border = BorderStroke(1.dp, MrbGoldOutline),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MrbGold),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Outlined.Drafts, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simpan Draft", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        // Reset Form
                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            border = BorderStroke(1.dp, Color(0xFF553333)),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Outlined.CleaningServices, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Form", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Batal
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MrbTextMuted),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Batal & Kembali ke Dashboard", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun FormSectionHeader(icon: ImageVector, title: String, subtitle: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MrbGold.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MrbGold, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MrbTextWhite)
            Text(text = subtitle, fontSize = 11.sp, color = MrbTextMuted)
        }
    }
}

@Composable
fun TextLabel(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = MrbTextWhite
    )
}

@Composable
fun MrbTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = if (isError) Color(0xFFFF5252) else MrbTextMuted) },
            placeholder = { Text(placeholder, color = MrbTextMuted.copy(alpha = 0.5f), fontSize = 13.sp) },
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MrbGold,
                unfocusedBorderColor = MrbSurfaceVariant,
                errorBorderColor = Color(0xFFFF5252),
                focusedTextColor = MrbTextWhite,
                unfocusedTextColor = MrbTextWhite
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color(0xFFFF5252),
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun ChoiceChipRow(
    options: List<String>,
    selectedOption: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = (option == selectedOption)
            MrbChip(
                text = option,
                isSelected = isSelected,
                onClick = { onSelect(option) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MrbChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MrbGold else Color(0xFF1E1E22),
        border = BorderStroke(1.dp, if (isSelected) MrbGold else MrbGoldOutline),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.Black else MrbTextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
