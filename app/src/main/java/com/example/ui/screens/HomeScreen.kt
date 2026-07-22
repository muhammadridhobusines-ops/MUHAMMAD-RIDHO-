package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CarItem
import com.example.ui.theme.MrbBackground
import com.example.ui.theme.MrbCardBackground
import com.example.ui.theme.MrbGold
import com.example.ui.theme.MrbGoldDark
import com.example.ui.theme.MrbGoldOutline
import com.example.ui.theme.MrbSurfaceVariant
import com.example.ui.theme.MrbTextMuted
import com.example.ui.theme.MrbTextWhite
import com.example.ui.viewmodel.MainViewModel

data class CategoryItem(val name: String, val iconRes: Int? = null)

val carCategories = listOf(
    CategoryItem("MPV"),
    CategoryItem("SUV"),
    CategoryItem("Hatchback"),
    CategoryItem("Sedan"),
    CategoryItem("Pickup"),
    CategoryItem("Double Cabin"),
    CategoryItem("Luxury"),
    CategoryItem("Sport")
)

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToStock: () -> Unit,
    onNavigateToDetail: (CarItem) -> Unit,
    onNavigateToChat: () -> Unit
) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cars by viewModel.filteredCars.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

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
            // Top App Bar / Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Selamat Pagi,",
                        fontSize = 12.sp,
                        color = MrbTextMuted
                    )
                    Text(
                        text = "Hai, Teman MRB! 👋",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MrbTextWhite
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .border(1.dp, MrbGoldOutline, CircleShape)
                        .size(42.dp)
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MrbGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }

            // Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Cari mobil impianmu...", color = MrbTextMuted, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MrbGold
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MrbCardBackground,
                        unfocusedContainerColor = MrbCardBackground,
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = MrbSurfaceVariant,
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = onNavigateToStock,
                    modifier = Modifier
                        .size(52.dp)
                        .background(MrbCardBackground, RoundedCornerShape(16.dp))
                        .border(1.dp, MrbGoldOutline, RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = MrbGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Banner Slider Carousel Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_banner_suv),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.9f),
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "KOLEKSI SUV",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MrbTextWhite,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "PREMIUM KALIMANTAN",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MrbGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Temukan SUV terbaik untuk setiap perjalananmu",
                            fontSize = 11.sp,
                            color = MrbTextMuted
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onNavigateToStock,
                            colors = ButtonDefaults.buttonColors(containerColor = MrbGold),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Lihat Koleksi >",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Category Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pilih Kategori",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MrbTextWhite
                )
                Text(
                    text = "Lihat Semua >",
                    fontSize = 12.sp,
                    color = MrbGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToStock() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(carCategories) { cat ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MrbCardBackground,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline),
                        modifier = Modifier.clickable {
                            viewModel.selectCategory(cat.name)
                            onNavigateToStock()
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = cat.name,
                                tint = MrbGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = cat.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MrbTextWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section "Stok Terbaru"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stok Terbaru",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MrbTextWhite
                )
                Text(
                    text = "Lihat Semua >",
                    fontSize = 12.sp,
                    color = MrbGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToStock() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                cars.take(6).chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { car ->
                            CarGridCard(
                                car = car,
                                isFavorite = favoriteIds.contains(car.id),
                                onFavoriteToggle = { viewModel.toggleFavorite(car.id) },
                                onClick = {
                                    viewModel.selectCar(car)
                                    onNavigateToDetail(car)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Floating WhatsApp Chat button
        FloatingActionButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/6285754563358?text=Halo%20Admin%20MRB,%20saya%20tertarik%20dengan%20stok%20mobil%20Kalimantan")
                }
                context.startActivity(intent)
            },
            containerColor = MrbGold,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 20.dp)
                .size(60.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.Chat,
                    contentDescription = "WhatsApp Chat",
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "Chat Kami", fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
