package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.CarItem
import com.example.ui.theme.MrbCardBackground
import com.example.ui.theme.MrbGold
import com.example.ui.theme.MrbGoldDark
import com.example.ui.theme.MrbGoldLight
import com.example.ui.theme.MrbGoldOutline
import com.example.ui.theme.MrbRedHot
import com.example.ui.theme.MrbSurfaceVariant
import com.example.ui.theme.MrbTextMuted
import com.example.ui.theme.MrbTextWhite
import java.text.NumberFormat
import java.util.Locale

// Gold Gradient Brush
val GoldBrush = Brush.horizontalGradient(
    colors = listOf(MrbGold, MrbGoldDark)
)

val DarkCardBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFF222222), Color(0xFF141414))
)

fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    formatter.maximumFractionDigits = 0
    return formatter.format(amount).replace("Rp", "Rp ")
}

@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(GoldBrush, shape = RoundedCornerShape(24.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF121212)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF121212)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF121212)
            )
        }
    }
}

@Composable
fun CarCard(
    car: CarItem,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
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

                // Badges top-left
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (car.isHot) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MrbRedHot
                        ) {
                            Text(
                                text = "♦ Hot",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (car.isVerified) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = MrbGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Verified",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Favorite button top-right
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MrbGold else Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = car.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MrbTextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${car.year} • ${car.fuelType}",
                        fontSize = 13.sp,
                        color = MrbTextMuted
                    )
                    Text(
                        text = "• ${car.transmission}",
                        fontSize = 13.sp,
                        color = MrbTextMuted
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = MrbGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${car.kilometer} KM",
                        fontSize = 12.sp,
                        color = MrbTextMuted
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MrbGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = car.location,
                        fontSize = 12.sp,
                        color = MrbTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (car.isReadyCredit) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF2B2618)
                            ) {
                                Text(
                                    text = "Siap Kredit",
                                    color = MrbGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                        Text(
                            text = formatRupiah(car.priceRp),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MrbGold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onChatClick,
                            modifier = Modifier
                                .border(1.dp, MrbGoldOutline, CircleShape)
                                .size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = "Chat",
                                tint = MrbGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Button(
                            onClick = onClick,
                            colors = ButtonDefaults.buttonColors(containerColor = MrbGold),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Detail",
                                color = Color(0xFF121212),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CarGridCard(
    car: CarItem,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
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

                // Badges top-left
                if (car.isHot) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF59E0B)
                    ) {
                        Text(
                            text = "🔥 Hot",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (car.isVerified || car.isPromo) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = MrbGold
                    ) {
                        Text(
                            text = "Baru ✨",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Favorite button top-right
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(30.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MrbGold else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = car.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MrbTextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${car.year} • ${car.transmission} • ${car.fuelType}",
                    fontSize = 11.sp,
                    color = MrbTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = formatRupiah(car.priceRp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MrbGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MrbTextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = car.location,
                        fontSize = 10.sp,
                        color = MrbTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CreditSimulationDialog(
    car: CarItem,
    onDismiss: () -> Unit,
    onSubmit: (dp: Long, tenor: Int, name: String, phone: String) -> Unit
) {
    var dpPercentage by remember { mutableStateOf(20f) }
    var selectedTenor by remember { mutableStateOf(36) }
    var applicantName by remember { mutableStateOf("") }
    var applicantPhone by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    val dpAmount = (car.priceRp * (dpPercentage / 100f)).toLong()
    val loanAmount = car.priceRp - dpAmount
    val totalInterest = loanAmount * 0.08 * (selectedTenor / 12.0)
    val monthlyPayment = ((loanAmount + totalInterest) / selectedTenor).toLong()

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
            ) {
                if (showSuccess) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(MrbGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pengajuan Kredit Terkirim!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MrbGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tim MRB Finance Sampit akan segera menghubungi $applicantPhone untuk verifikasi kelengkapan berkas.",
                            fontSize = 13.sp,
                            color = MrbTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        GoldButton(text = "Tutup", onClick = onDismiss)
                    }
                } else {
                    Text(
                        text = "Simulasi & Ajukan Kredit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MrbGold
                    )
                    Text(
                        text = car.name,
                        fontSize = 14.sp,
                        color = MrbTextWhite
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Uang Muka (DP): ${dpPercentage.toInt()}% (${formatRupiah(dpAmount)})",
                        fontSize = 13.sp,
                        color = MrbTextMuted
                    )
                    Slider(
                        value = dpPercentage,
                        onValueChange = { dpPercentage = it },
                        valueRange = 10f..50f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = MrbGold,
                            activeTrackColor = MrbGold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Pilih Tenor Bulan:", fontSize = 13.sp, color = MrbTextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(12, 24, 36, 48, 60).forEach { tenor ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selectedTenor == tenor) MrbGold else MrbSurfaceVariant)
                                    .clickable { selectedTenor = tenor }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${tenor}B",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTenor == tenor) Color.Black else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF252219)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Estimasi Angsuran / Bulan:", fontSize = 12.sp, color = MrbTextMuted)
                            Text(
                                text = "${formatRupiah(monthlyPayment)} / bln",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = MrbGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = applicantName,
                        onValueChange = { applicantName = it },
                        label = { Text("Nama Lengkap", color = MrbTextMuted) },
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
                    OutlinedTextField(
                        value = applicantPhone,
                        onValueChange = { applicantPhone = it },
                        label = { Text("Nomor HP / WhatsApp", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    GoldButton(
                        text = "Kirim Pengajuan Kredit",
                        onClick = {
                            if (applicantName.isNotEmpty() && applicantPhone.isNotEmpty()) {
                                onSubmit(dpAmount, selectedTenor, applicantName, applicantPhone)
                                showSuccess = true
                            }
                        },
                        enabled = applicantName.isNotEmpty() && applicantPhone.isNotEmpty()
                    )
                }
            }
        }
    }
}

@Composable
fun BookingUnitDialog(
    car: CarItem,
    onDismiss: () -> Unit,
    onSubmit: (name: String, phone: String, date: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Besok (10:00 WITA)") }
    var showSuccess by remember { mutableStateOf(false) }

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
            ) {
                if (showSuccess) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(MrbGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Unit Berhasil Di-Booking!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MrbGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Booking unit ${car.name} sebesar Rp 2.000.000 telah terkonfirmasi. Tim MRB Sampit akan menyiapkan unit untuk kedatangan Anda pada $date.",
                            fontSize = 13.sp,
                            color = MrbTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        GoldButton(text = "Tutup", onClick = onDismiss)
                    }
                } else {
                    Text(
                        text = "Booking Unit Sekarang",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MrbGold
                    )
                    Text(text = car.name, fontSize = 14.sp, color = MrbTextWhite)
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF252219)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Booking Fee / Tanda Jadi:", fontSize = 12.sp, color = MrbTextMuted)
                                Text(
                                    text = "Rp 2.000.000",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MrbGold
                                )
                            }
                            Text(text = "Refundable 100%", fontSize = 11.sp, color = MrbGoldLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Pemesan", color = MrbTextMuted) },
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
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Nomor HP / WhatsApp", color = MrbTextMuted) },
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
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Rencana Tanggal Kunjungan / Test Drive", color = MrbTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MrbGold,
                            unfocusedBorderColor = MrbSurfaceVariant,
                            focusedTextColor = MrbTextWhite,
                            unfocusedTextColor = MrbTextWhite
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    GoldButton(
                        text = "Konfirmasi Booking Unit",
                        onClick = {
                            if (name.isNotEmpty() && phone.isNotEmpty()) {
                                onSubmit(name, phone, date)
                                showSuccess = true
                            }
                        },
                        enabled = name.isNotEmpty() && phone.isNotEmpty()
                    )
                }
            }
        }
    }
}
