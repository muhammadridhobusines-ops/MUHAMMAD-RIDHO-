package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalOffer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import android.widget.Toast
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
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

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddCar: () -> Unit = {}
) {
    val cars by viewModel.allCars.collectAsState()
    val creditRequests by viewModel.creditRequests.collectAsState()
    val bookingRequests by viewModel.bookingRequests.collectAsState()

    var showAddCarDialog by remember { mutableStateOf(false) }
    var carToEdit by remember { mutableStateOf<CarItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MrbBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MrbTextWhite
                        )
                    }
                    Text(
                        text = "Admin Panel MRB",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MrbTextWhite
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MrbGold
                ) {
                    Text(
                        text = "Kalimantan",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Metrics Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    icon = Icons.Default.DirectionsCar,
                    value = "${cars.size}",
                    label = "Jumlah Mobil",
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    icon = Icons.Default.Group,
                    value = "2,850",
                    label = "Jumlah User",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    icon = Icons.Default.Chat,
                    value = "64",
                    label = "Chat Aktif",
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    icon = Icons.Default.BookOnline,
                    value = "${bookingRequests.size + 19}",
                    label = "Booking Unit",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Add Car Button
            GoldButton(
                text = "Tambah Mobil Baru (Form Lengkap)",
                onClick = onNavigateToAddCar,
                icon = Icons.Default.Add
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Manage Inventory Section
            Text(
                text = "Kelola Stok Mobil (${cars.size} Unit)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MrbTextWhite
            )

            Spacer(modifier = Modifier.height(12.dp))

            cars.forEach { car ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = car.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MrbTextWhite
                            )
                            Text(
                                text = "${car.year} • ${car.fuelType} • ${formatRupiah(car.priceRp)}",
                                fontSize = 12.sp,
                                color = MrbGold
                            )
                            Text(
                                text = "Lokasi: ${car.location}",
                                fontSize = 11.sp,
                                color = MrbTextMuted
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { carToEdit = car }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MrbGold
                                )
                            }
                            IconButton(onClick = { viewModel.deleteCarAdmin(car.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFFF5252)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCarDialog) {
        AddCarDialog(
            onDismiss = { showAddCarDialog = false },
            onAdd = { newCar ->
                viewModel.addNewCarAdmin(newCar)
                showAddCarDialog = false
            }
        )
    }

    carToEdit?.let { car ->
        EditCarDialog(
            car = car,
            onDismiss = { carToEdit = null },
            onSave = { updatedCar ->
                viewModel.updateCarAdmin(updatedCar)
                carToEdit = null
            }
        )
    }
}

@Composable
fun AdminStatCard(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MrbGold, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontWeight = FontWeight.Black, fontSize = 20.sp, color = MrbTextWhite)
            Text(text = label, fontSize = 11.sp, color = MrbTextMuted)
        }
    }
}

@Composable
fun AddCarDialog(
    onDismiss: () -> Unit,
    onAdd: (CarItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("Toyota") }
    var year by remember { mutableStateOf("2022") }
    var price by remember { mutableStateOf("320000000") }
    var category by remember { mutableStateOf("SUV") }
    var fuel by remember { mutableStateOf("Bensin") }
    var transmission by remember { mutableStateOf("Automatic") }
    var km by remember { mutableStateOf("15000") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MrbCardBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, MrbGold)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Tambah Unit Mobil",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MrbGold
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Mobil", color = MrbTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = MrbSurfaceVariant,
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Merek", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Tahun", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Harga (Rp)", color = MrbTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = MrbSurfaceVariant,
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Kategori (MPV/SUV)", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fuel,
                        onValueChange = { fuel = it },
                        label = { Text("BBM", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                GoldButton(
                    text = "Simpan Mobil",
                    onClick = {
                        if (name.isNotEmpty()) {
                            val newCar = CarItem(
                                id = "mrb-${System.currentTimeMillis()}",
                                name = name,
                                brand = brand,
                                model = name,
                                year = year.toIntOrNull() ?: 2022,
                                priceRp = price.toLongOrNull() ?: 300000000L,
                                location = "Sampit, Kalteng",
                                category = category,
                                fuelType = fuel,
                                transmission = transmission,
                                kilometer = km.toIntOrNull() ?: 10000
                            )
                            onAdd(newCar)
                        }
                    }
                )
            }
        }
    }
}

private fun defaultGalleryForCar(car: CarItem): List<CarGalleryItem> {
    val labels = listOf(
        "Depan", "Belakang", "Samping Kanan", "Samping Kiri",
        "Interior Depan", "Interior Belakang", "Dashboard", "Jok",
        "Mesin", "Bagasi", "Velg", "Ban"
    )
    val sampleDrawables = listOf(
        car.imageRes ?: R.drawable.img_banner_suv,
        R.drawable.mrb_suv_bg,
        R.drawable.img_car_hrv,
        R.drawable.mrb_suv_hero,
        R.drawable.img_banner_suv,
        R.drawable.img_car_innova,
        R.drawable.img_car_supra,
        R.drawable.mrb_suv_bg,
        R.drawable.img_car_hrv,
        R.drawable.mrb_suv_hero,
        R.drawable.img_car_innova,
        R.drawable.img_banner_suv
    )
    return labels.mapIndexed { idx, label ->
        CarGalleryItem(
            label = label,
            imageRes = sampleDrawables.getOrNull(idx) ?: car.imageRes ?: R.drawable.img_banner_suv,
            imageUrl = if (idx == 0) car.imageUrl else null
        )
    }
}

@Composable
fun EditCarDialog(
    car: CarItem,
    onDismiss: () -> Unit,
    onSave: (CarItem) -> Unit
) {
    var name by remember { mutableStateOf(car.name) }
    var brand by remember { mutableStateOf(car.brand) }
    var year by remember { mutableStateOf(car.year.toString()) }
    var price by remember { mutableStateOf(car.priceRp.toString()) }
    var location by remember { mutableStateOf(car.location) }
    var category by remember { mutableStateOf(car.category) }
    var fuel by remember { mutableStateOf(car.fuelType) }
    var transmission by remember { mutableStateOf(car.transmission) }
    var km by remember { mutableStateOf(car.kilometer.toString()) }
    var isHot by remember { mutableStateOf(car.isHot) }
    var isVerified by remember { mutableStateOf(car.isVerified) }
    var isPromo by remember { mutableStateOf(car.isPromo) }
    var description by remember { mutableStateOf(car.description) }

    val context = LocalContext.current

    // Image Editing States
    var mainImageUrl by remember { mutableStateOf(car.imageUrl ?: "") }
    var mainImageRes by remember { mutableStateOf<Int?>(car.imageRes ?: R.drawable.img_banner_suv) }

    // Gallery Photos State
    var galleryList by remember {
        mutableStateOf(
            if (car.galleryPhotos.isNotEmpty()) car.galleryPhotos else defaultGalleryForCar(car)
        )
    }

    // New Photo Form State
    var newPhotoLabel by remember { mutableStateOf("Depan") }
    var newPhotoUrl by remember { mutableStateOf("") }
    var newPhotoRes by remember { mutableStateOf<Int?>(R.drawable.img_banner_suv) }

    val presetImages = listOf(
        "Banner SUV" to R.drawable.img_banner_suv,
        "Innova" to R.drawable.img_car_innova,
        "HRV" to R.drawable.img_car_hrv,
        "Supra" to R.drawable.img_car_supra,
        "Hero SUV" to R.drawable.mrb_suv_hero,
        "BG SUV" to R.drawable.mrb_suv_bg
    )

    val labelPresets = listOf(
        "Depan", "Belakang", "Samping Kanan", "Samping Kiri",
        "Interior Depan", "Interior Belakang", "Dashboard", "Jok",
        "Mesin", "Bagasi", "Velg", "Surat / STNK"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MrbCardBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, MrbGold)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Stok Mobil",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MrbGold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MrbTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ==================== SECTION 1: GAMBAR UTAMA MOBIL ====================
                Text(
                    text = "📷 Foto Utama Unit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MrbGold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Main Image Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black)
                        .border(1.dp, MrbGoldOutline, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (mainImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(mainImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Gambar Utama Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (mainImageRes != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(mainImageRes)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Gambar Utama Preview",
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
                                text = "Foto Utama Kosong",
                                color = MrbTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (mainImageUrl.isNotBlank()) "URL Kustom" else if (mainImageRes != null) "Asset Bawaan" else "Kosong",
                            fontSize = 10.sp,
                            color = MrbGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons Row: Pilih dari Galeri, Ambil dari Kamera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            mainImageUrl = ""
                            mainImageRes = presetImages.map { it.second }.random()
                            Toast.makeText(context, "Foto Utama dipilih dari Galeri!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pilih Galeri", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            mainImageUrl = ""
                            mainImageRes = presetImages.map { it.second }.random()
                            Toast.makeText(context, "Foto Utama diambil dari Kamera!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222228), contentColor = MrbGold),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ambil Kamera", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Button Hapus Foto Utama
                if (mainImageUrl.isNotBlank() || mainImageRes != null) {
                    OutlinedButton(
                        onClick = {
                            mainImageUrl = ""
                            mainImageRes = null
                            Toast.makeText(context, "Foto Utama dihapus", Toast.LENGTH_SHORT).show()
                        },
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF553333)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hapus Foto Utama", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dukungan Link / URL Gambar Langsung
                OutlinedTextField(
                    value = mainImageUrl,
                    onValueChange = { mainImageUrl = it },
                    label = { Text("Dukungan Link/URL Gambar Langsung (http/https)", color = MrbTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = MrbSurfaceVariant,
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Atau pilih preset gambar bawaan:",
                    fontSize = 11.sp,
                    color = MrbTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(presetImages) { _, (pLabel, pRes) ->
                        val isSelected = mainImageUrl.isBlank() && mainImageRes == pRes
                        Surface(
                            modifier = Modifier.clickable {
                                mainImageUrl = ""
                                mainImageRes = pRes
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MrbGold else MrbSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) MrbGold else MrbGoldOutline
                            )
                        ) {
                            Text(
                                text = pLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.Black else MrbTextWhite,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==================== SECTION 2: GALERI FOTO MOBIL ====================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📸 Galeri Foto Unit (${galleryList.size} Foto)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MrbGold
                    )

                    if (galleryList.isNotEmpty()) {
                        Text(
                            text = "Hapus All",
                            fontSize = 11.sp,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { galleryList = emptyList() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (galleryList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(MrbSurfaceVariant, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada foto galeri. Tambahkan foto di bawah.",
                            color = MrbTextMuted,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(galleryList) { index, item ->
                            Card(
                                modifier = Modifier.size(110.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Black),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    if (!item.imageUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(item.imageUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = item.label,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.matchParentSize()
                                        )
                                    } else if (item.imageRes != null) {
                                        Image(
                                            painter = painterResource(id = item.imageRes),
                                            contentDescription = item.label,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.matchParentSize()
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_banner_suv),
                                            contentDescription = item.label,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.matchParentSize()
                                        )
                                    }

                                    // Label Footer
                                    Surface(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth(),
                                        color = Color.Black.copy(alpha = 0.8f)
                                    ) {
                                        Text(
                                            text = item.label,
                                            fontSize = 10.sp,
                                            color = MrbTextWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .padding(vertical = 3.dp)
                                                .fillMaxWidth(),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }

                                    // Delete Photo Button
                                    IconButton(
                                        onClick = {
                                            galleryList = galleryList.toMutableList().apply { removeAt(index) }
                                        },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(24.dp)
                                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus Foto",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sub-card: Tambah Foto Baru ke Galeri
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MrbSurfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = MrbGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tambah Foto Baru ke Galeri",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MrbTextWhite
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Label Input & Presets
                        OutlinedTextField(
                            value = newPhotoLabel,
                            onValueChange = { newPhotoLabel = it },
                            label = { Text("Label Foto (contoh: Interior, Mesin)", color = MrbTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MrbGold,
                                unfocusedBorderColor = MrbGoldOutline,
                                focusedTextColor = MrbTextWhite,
                                unfocusedTextColor = MrbTextWhite
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(labelPresets) { _, label ->
                                val isSelected = newPhotoLabel == label
                                Surface(
                                    modifier = Modifier.clickable { newPhotoLabel = label },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) MrbGold else Color.Black.copy(alpha = 0.4f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.Black else MrbTextWhite,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Image Source Input & Presets
                        OutlinedTextField(
                            value = newPhotoUrl,
                            onValueChange = { newPhotoUrl = it },
                            label = { Text("URL Link Foto (opsional)", color = MrbTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MrbGold,
                                unfocusedBorderColor = MrbGoldOutline,
                                focusedTextColor = MrbTextWhite,
                                unfocusedTextColor = MrbTextWhite
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Atau pilih sampel gambar preset:",
                            fontSize = 11.sp,
                            color = MrbTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(presetImages) { _, (pLabel, pRes) ->
                                val isSelected = newPhotoUrl.isBlank() && newPhotoRes == pRes
                                Surface(
                                    modifier = Modifier.clickable {
                                        newPhotoUrl = ""
                                        newPhotoRes = pRes
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) MrbGold else Color.Black.copy(alpha = 0.4f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                                ) {
                                    Text(
                                        text = pLabel,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.Black else MrbTextWhite,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (newPhotoLabel.isNotBlank()) {
                                        val newItem = CarGalleryItem(
                                            label = newPhotoLabel,
                                            imageUrl = newPhotoUrl.ifBlank { null },
                                            imageRes = if (newPhotoUrl.isBlank()) newPhotoRes else null
                                        )
                                        galleryList = galleryList + newItem
                                        newPhotoUrl = ""
                                    }
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = MrbGold
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tambah Foto ke Galeri",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==================== SECTION 3: INFORMASI DETAIL MOBIL ====================
                Text(
                    text = "📝 Spesifikasi & Detail Unit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MrbGold
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Unit / Mobil", color = MrbTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = MrbSurfaceVariant,
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Merek", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Tahun", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Harga (Rp)", color = MrbTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = MrbSurfaceVariant,
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Lokasi Showroom", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = km,
                        onValueChange = { km = it },
                        label = { Text("Kilometer (KM)", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Kategori (SUV/MPV)", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fuel,
                        onValueChange = { fuel = it },
                        label = { Text("BBM", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi Unit", color = MrbTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = MrbSurfaceVariant,
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Label & Status Unit:",
                    fontSize = 12.sp,
                    color = MrbTextMuted,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isHot = !isHot },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isHot) Color(0xFFF59E0B) else MrbSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isHot) Color(0xFFF59E0B) else Color.Transparent)
                    ) {
                        Text(
                            text = "🔥 Hot",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHot) Color.Black else MrbTextMuted,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isVerified = !isVerified },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isVerified) MrbGold else MrbSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isVerified) MrbGold else Color.Transparent)
                    ) {
                        Text(
                            text = "✨ Verified",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isVerified) Color.Black else MrbTextMuted,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isPromo = !isPromo },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isPromo) MrbGold else MrbSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isPromo) MrbGold else Color.Transparent)
                    ) {
                        Text(
                            text = "🏷️ Promo",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPromo) Color.Black else MrbTextMuted,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                GoldButton(
                    text = "Simpan Perubahan",
                    onClick = {
                        if (name.isNotEmpty()) {
                            val updated = car.copy(
                                name = name,
                                brand = brand,
                                model = name,
                                year = year.toIntOrNull() ?: car.year,
                                priceRp = price.toLongOrNull() ?: car.priceRp,
                                location = location,
                                category = category,
                                fuelType = fuel,
                                transmission = transmission,
                                kilometer = km.toIntOrNull() ?: car.kilometer,
                                isHot = isHot,
                                isVerified = isVerified,
                                isPromo = isPromo,
                                description = description,
                                imageUrl = mainImageUrl.ifBlank { null },
                                imageRes = mainImageRes,
                                galleryPhotos = galleryList
                            )
                            onSave(updated)
                        }
                    }
                )
            }
        }
    }
}
