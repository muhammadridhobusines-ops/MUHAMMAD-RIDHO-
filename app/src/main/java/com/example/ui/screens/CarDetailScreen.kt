package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
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
fun CarDetailScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val context = LocalContext.current
    val car = viewModel.selectedCar.collectAsState().value ?: return
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(car.id)

    var showCreditDialog by remember { mutableStateOf(false) }
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedPhotoIndexForLightbox by remember { mutableStateOf<Int?>(null) }

    val galleryPhotos = remember(car) {
        if (car.galleryPhotos.isNotEmpty()) {
            car.galleryPhotos.map { item ->
                CarGalleryPhoto(
                    label = item.label,
                    imageRes = item.imageRes,
                    imageUrl = item.imageUrl
                )
            }
        } else {
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
            labels.mapIndexed { idx, label ->
                CarGalleryPhoto(
                    label = label,
                    imageRes = sampleDrawables.getOrNull(idx) ?: car.imageRes,
                    imageUrl = if (idx == 0) car.imageUrl else null
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MrbBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            // Header Image Box with overlay icons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                if (car.imageRes != null) {
                    Image(
                        painter = painterResource(id = car.imageRes),
                        contentDescription = car.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                } else if (!car.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(car.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = car.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_banner_suv),
                        contentDescription = car.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }

                // Dark top gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                            )
                        )
                )

                // Navigation top actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Lihat unit ${car.name} di MRB (Mitra Roda Borneo): ${formatRupiah(car.priceRp)}")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Bagikan Mobil"))
                            },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleFavorite(car.id) },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) MrbGold else Color.White
                            )
                        }
                    }
                }
            }

            // Main Car Title & Price Header
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF262112)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = MrbGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Lolos Inspeksi MRB 160 Titik",
                                color = MrbGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = car.category,
                        color = MrbTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = car.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = MrbTextWhite
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatRupiah(car.priceRp),
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = MrbGold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MrbGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = car.location,
                            fontSize = 13.sp,
                            color = MrbTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Specifications Grid
                Text(
                    text = "Spesifikasi Kendaraan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MrbTextWhite
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpecTile(
                            icon = Icons.Default.DirectionsCar,
                            label = "Tahun",
                            value = "${car.year}",
                            modifier = Modifier.weight(1f)
                        )
                        SpecTile(
                            icon = Icons.Default.Speed,
                            label = "Kilometer",
                            value = "${car.kilometer} KM",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpecTile(
                            icon = Icons.Default.LocalGasStation,
                            label = "Bahan Bakar",
                            value = car.fuelType,
                            modifier = Modifier.weight(1f)
                        )
                        SpecTile(
                            icon = Icons.Default.DirectionsCar,
                            label = "Transmisi",
                            value = car.transmission,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpecTile(
                            icon = Icons.Default.Palette,
                            label = "Warna",
                            value = car.color,
                            modifier = Modifier.weight(1f)
                        )
                        SpecTile(
                            icon = Icons.Default.Assignment,
                            label = "Surat (BPKB/STNK)",
                            value = car.stnkStatus,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section "Galeri Foto"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🖼️",
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Galeri Foto",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MrbTextWhite
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MrbSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                            ) {
                                Text(
                                    text = "${galleryPhotos.size} Foto",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    color = MrbGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (galleryPhotos.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .padding(horizontal = 16.dp)
                                    .background(MrbSurfaceVariant, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada foto tambahan",
                                    color = MrbTextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                itemsIndexed(galleryPhotos) { index, photo ->
                                    Card(
                                        modifier = Modifier
                                            .size(110.dp)
                                            .clickable { selectedPhotoIndexForLightbox = index },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.Black),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            if (photo.imageRes != null) {
                                                Image(
                                                    painter = painterResource(id = photo.imageRes),
                                                    contentDescription = photo.label,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.matchParentSize()
                                                )
                                            } else if (!photo.imageUrl.isNullOrEmpty()) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(photo.imageUrl)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = photo.label,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.matchParentSize()
                                                )
                                            } else {
                                                Image(
                                                    painter = painterResource(id = R.drawable.img_banner_suv),
                                                    contentDescription = photo.label,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.matchParentSize()
                                                )
                                            }

                                            Surface(
                                                modifier = Modifier
                                                    .align(Alignment.BottomCenter)
                                                    .fillMaxWidth(),
                                                color = Color.Black.copy(alpha = 0.75f)
                                            ) {
                                                Text(
                                                    text = photo.label,
                                                    modifier = Modifier
                                                        .padding(vertical = 3.dp)
                                                        .fillMaxWidth(),
                                                    color = MrbTextWhite,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Description
                Text(
                    text = "Deskripsi Unit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MrbTextWhite
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = car.description,
                    fontSize = 13.sp,
                    color = MrbTextMuted,
                    lineHeight = 20.sp
                )


            }
        }

        // Bottom Fixed Bar: Chat, Booking, Ajukan Kredit
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color(0xFF141414),
            shadowElevation = 12.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chat Button
                IconButton(
                    onClick = onNavigateToChat,
                    modifier = Modifier
                        .border(1.dp, MrbGold, CircleShape)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Chat,
                        contentDescription = "Chat",
                        tint = MrbGold
                    )
                }

                // Booking Button
                Button(
                    onClick = { showBookingDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262214)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGold),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Booking",
                        color = MrbGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Ajukan Kredit Button
                Button(
                    onClick = { showCreditDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MrbGold),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Ajukan Kredit",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    if (showCreditDialog) {
        CreditSimulationDialog(
            car = car,
            onDismiss = { showCreditDialog = false },
            onSubmit = { dp, tenor, name, phone ->
                viewModel.submitCredit(car, dp, tenor, name, phone)
            }
        )
    }

    if (showBookingDialog) {
        BookingUnitDialog(
            car = car,
            onDismiss = { showBookingDialog = false },
            onSubmit = { name, phone, date ->
                viewModel.submitBooking(car, name, phone, date)
            }
        )
    }

    selectedPhotoIndexForLightbox?.let { initialIndex ->
        var currentIndex by remember { mutableStateOf(initialIndex) }
        val currentPhoto = galleryPhotos.getOrNull(currentIndex)

        Dialog(
            onDismissRequest = { selectedPhotoIndexForLightbox = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
            ) {
                // Top bar in lightbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currentPhoto?.label ?: "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MrbGold
                        )
                        Text(
                            text = "Foto ${currentIndex + 1} dari ${galleryPhotos.size}",
                            fontSize = 12.sp,
                            color = MrbTextMuted
                        )
                    }

                    IconButton(
                        onClick = { selectedPhotoIndexForLightbox = null },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color.White
                        )
                    }
                }

                if (currentPhoto != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.75f)
                            .align(Alignment.Center)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = currentIndex,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                        slideOutHorizontally { width -> -width } + fadeOut())
                                } else {
                                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                        slideOutHorizontally { width -> width } + fadeOut())
                                }
                            },
                            label = "PhotoLightboxTransition"
                        ) { pageIndex ->
                            val photo = galleryPhotos.getOrNull(pageIndex) ?: currentPhoto
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, MrbGold, RoundedCornerShape(16.dp))
                            ) {
                                if (photo.imageRes != null) {
                                    Image(
                                        painter = painterResource(id = photo.imageRes),
                                        contentDescription = photo.label,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else if (!photo.imageUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(photo.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = photo.label,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_banner_suv),
                                        contentDescription = photo.label,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }

                        if (currentIndex > 0) {
                            IconButton(
                                onClick = { currentIndex-- },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .border(1.dp, MrbGoldOutline, CircleShape)
                                    .size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Sebelumnya",
                                    tint = MrbGold
                                )
                            }
                        }

                        if (currentIndex < galleryPhotos.size - 1) {
                            IconButton(
                                onClick = { currentIndex++ },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .border(1.dp, MrbGoldOutline, CircleShape)
                                    .size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Selanjutnya",
                                    tint = MrbGold
                                )
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 36.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MrbCardBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Text(
                        text = "${currentPhoto?.label ?: ""} (${currentIndex + 1}/${galleryPhotos.size})",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MrbTextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

data class CarGalleryPhoto(
    val label: String,
    val imageRes: Int? = null,
    val imageUrl: String? = null
)

@Composable
fun SpecTile(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MrbCardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MrbGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = label, fontSize = 10.sp, color = MrbTextMuted)
                Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MrbTextWhite)
            }
        }
    }
}
