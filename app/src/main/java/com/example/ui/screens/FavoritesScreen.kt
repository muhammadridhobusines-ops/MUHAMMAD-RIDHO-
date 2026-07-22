package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun FavoritesScreen(
    viewModel: MainViewModel,
    onNavigateToStock: () -> Unit,
    onNavigateToDetail: (CarItem) -> Unit,
    onNavigateToChat: () -> Unit
) {
    val favoriteCars by viewModel.favoriteCars.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MrbBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Header Top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = "Mobil Favorit Saya",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MrbTextWhite
                )
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = MrbGold
                )
            }

            if (favoriteCars.isEmpty()) {
                // Empty State matching reference Image 5
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 40.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .border(1.5.dp, MrbGold, CircleShape)
                                    .background(Color(0xFF1E1C12), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = MrbGold,
                                    modifier = Modifier.size(44.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Belum ada mobil favorit",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MrbTextWhite,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Simpan mobil impianmu agar mudah ditemukan kembali.",
                                fontSize = 13.sp,
                                color = MrbTextMuted,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            GoldButton(
                                text = "Cari Mobil",
                                onClick = onNavigateToStock,
                                icon = Icons.Default.Search,
                                modifier = Modifier.fillMaxWidth(0.85f)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoriteCars) { car ->
                        CarCard(
                            car = car,
                            isFavorite = true,
                            onFavoriteToggle = { viewModel.toggleFavorite(car.id) },
                            onClick = {
                                viewModel.selectCar(car)
                                onNavigateToDetail(car)
                            },
                            onChatClick = onNavigateToChat
                        )
                    }
                }
            }
        }
    }
}
