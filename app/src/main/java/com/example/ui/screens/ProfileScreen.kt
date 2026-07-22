package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.MrbBackground
import com.example.ui.theme.MrbCardBackground
import com.example.ui.theme.MrbGold
import com.example.ui.theme.MrbGoldOutline
import com.example.ui.theme.MrbTextMuted
import com.example.ui.theme.MrbTextWhite
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateToFavorites: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val name by viewModel.userName.collectAsState()
    val phone by viewModel.userPhone.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val memberBadge by viewModel.userMemberBadge.collectAsState()
    val photoUrl by viewModel.userPhotoUrl.collectAsState()
    val photoRes by viewModel.userPhotoRes.collectAsState()
    val favoriteCars by viewModel.favoriteCars.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var infoDialogTitle by remember { mutableStateOf<String?>(null) }
    var infoDialogMessage by remember { mutableStateOf<String?>(null) }

    if (showAboutDialog) {
        AboutMrbDialog(onDismiss = { showAboutDialog = false })
    }

    if (showEditDialog) {
        EditProfileDialog(
            initialName = name,
            initialPhone = phone,
            initialEmail = email,
            initialPhotoUrl = photoUrl,
            initialPhotoRes = photoRes,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newPhone, newEmail, newPhotoUrl, newPhotoRes ->
                viewModel.updateProfile(newName, newPhone, newEmail, newPhotoUrl, newPhotoRes)
                showEditDialog = false
            }
        )
    }

    if (infoDialogTitle != null && infoDialogMessage != null) {
        AlertDialog(
            onDismissRequest = {
                infoDialogTitle = null
                infoDialogMessage = null
            },
            containerColor = MrbCardBackground,
            titleContentColor = MrbTextWhite,
            textContentColor = MrbTextWhite,
            title = {
                Text(
                    text = infoDialogTitle!!,
                    fontWeight = FontWeight.Bold,
                    color = MrbGold
                )
            },
            text = {
                Text(
                    text = infoDialogMessage!!,
                    fontSize = 14.sp,
                    color = MrbTextWhite
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        infoDialogTitle = null
                        infoDialogMessage = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black)
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
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
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // User Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF262626))
                            .border(1.5.dp, MrbGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto Profil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (photoRes != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photoRes)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto Profil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MrbTextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MrbTextWhite
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MrbGold
                        ) {
                            Text(
                                text = memberBadge,
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "📞 $phone",
                            fontSize = 12.sp,
                            color = MrbTextMuted
                        )
                        Text(
                            text = "✉ $email",
                            fontSize = 12.sp,
                            color = MrbTextMuted
                        )
                    }

                    // Edit Profile Quick Button
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit Profil",
                            tint = MrbGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Profile Menu List Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    ProfileMenuItem(
                        icon = Icons.Outlined.Edit,
                        title = "Edit Profil",
                        onClick = { showEditDialog = true }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.FavoriteBorder,
                        title = "Mobil Favorit",
                        badgeText = "${favoriteCars.size}",
                        onClick = onNavigateToFavorites
                    )
                    ProfileMenuItem(
                        icon = Icons.Outlined.Chat,
                        title = "Riwayat Chat",
                        onClick = onNavigateToChat
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.RemoveRedEye,
                        title = "Mobil Dilihat",
                        badgeText = "12",
                        onClick = {
                            infoDialogTitle = "Mobil Dilihat"
                            infoDialogMessage = "Anda telah melihat 12 unit mobil pilihan di Mitra Roda Borneo pekan ini."
                        }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Notifications,
                        title = "Pengaturan Notifikasi",
                        onClick = {
                            infoDialogTitle = "Notifikasi MRB"
                            infoDialogMessage = "Notifikasi promo stok mobil baru & status kredit aktif untuk nomor $phone."
                        }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.LocationOn,
                        title = "Alamat Saya",
                        onClick = {
                            infoDialogTitle = "Alamat Pengiriman/Inspeksi"
                            infoDialogMessage = "Lokasi: Palangka Raya / Banjarmasin (Kalteng & Kalsel).\nHubungi admin untuk ubah lokasi antar unit."
                        }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.HelpOutline,
                        title = "Bantuan & Pusat Bantuan",
                        onClick = {
                            infoDialogTitle = "Pusat Bantuan MRB"
                            infoDialogMessage = "Butuh bantuan pembelian, tukar tambah, atau pengajuan kredit?\nHubungi Call Center WA: 0857-5456-3358"
                        }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "Tentang MRB (Mitra Roda Borneo)",
                        onClick = { showAboutDialog = true }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "Dashboard Admin MRB",
                        badgeText = "Admin",
                        badgeColor = MrbGold,
                        onClick = onNavigateToAdmin
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Logout,
                        title = "Keluar",
                        titleColor = Color(0xFFFF5252),
                        onClick = onLogout
                    )
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    initialName: String,
    initialPhone: String,
    initialEmail: String,
    initialPhotoUrl: String?,
    initialPhotoRes: Int?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String?, Int?) -> Unit
) {
    val context = LocalContext.current
    var nameText by remember { mutableStateOf(initialName) }
    var phoneText by remember { mutableStateOf(initialPhone) }
    var emailText by remember { mutableStateOf(initialEmail) }
    var photoUrlText by remember { mutableStateOf(initialPhotoUrl ?: "") }
    var photoResState by remember { mutableStateOf(initialPhotoRes) }

    val presetAvatars = remember {
        listOf(
            R.drawable.mrb_exact_user_logo_512,
            R.drawable.mrb_logo_clean,
            R.drawable.mrb_logo_header,
            R.drawable.img_car_hrv,
            R.drawable.img_car_innova,
            R.drawable.img_car_supra,
            R.drawable.mrb_suv_hero
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MrbCardBackground,
        titleContentColor = MrbTextWhite,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = MrbGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Edit Profil Saya", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Section Foto Profil
                Text(
                    text = "📷 Foto Profil Saya",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MrbGold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E22))
                        .border(2.dp, MrbGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUrlText.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photoUrlText)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto Profil Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (photoResState != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photoResState)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto Profil Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MrbTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Action Buttons Row: Pilih Galeri, Ambil Kamera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            photoUrlText = ""
                            photoResState = presetAvatars.random()
                            Toast.makeText(context, "Foto profil dipilih dari Galeri!", Toast.LENGTH_SHORT).show()
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
                            photoUrlText = ""
                            photoResState = presetAvatars.random()
                            Toast.makeText(context, "Foto profil diambil dari Kamera!", Toast.LENGTH_SHORT).show()
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

                if (photoUrlText.isNotBlank() || photoResState != null) {
                    OutlinedButton(
                        onClick = {
                            photoUrlText = ""
                            photoResState = null
                            Toast.makeText(context, "Foto profil dihapus", Toast.LENGTH_SHORT).show()
                        },
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF553333)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hapus Foto Profil", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Custom URL Link Input
                OutlinedTextField(
                    value = photoUrlText,
                    onValueChange = { photoUrlText = it },
                    label = { Text("Link URL Foto Profil (http/https)", color = MrbTextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = MrbGoldOutline,
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Nama Lengkap", color = MrbTextMuted) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MrbGold) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = MrbGoldOutline,
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phoneText,
                    onValueChange = { phoneText = it },
                    label = { Text("Nomor HP / WhatsApp", color = MrbTextMuted) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MrbGold) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = MrbGoldOutline,
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = emailText,
                    onValueChange = { emailText = it },
                    label = { Text("Alamat Email", color = MrbTextMuted) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MrbGold) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MrbGold,
                        unfocusedBorderColor = MrbGoldOutline,
                        focusedTextColor = MrbTextWhite,
                        unfocusedTextColor = MrbTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalUrl = photoUrlText.ifBlank { null }
                    onSave(nameText, phoneText, emailText, finalUrl, photoResState)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black)
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MrbTextWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
            ) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    badgeText: String? = null,
    badgeColor: Color = MrbTextMuted,
    titleColor: Color = MrbTextWhite,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MrbGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badgeText != null) {
                Text(
                    text = badgeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MrbTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun AboutMrbDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Top Header Row with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MrbGold.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MrbGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tentang MRB",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MrbGold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MrbTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Brand Hero Card Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141416)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MrbGold
                        ) {
                            Text(
                                text = "MRB – Mitra Roda Borneo",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Trusted Car Partner",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MrbGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Mitra terpercaya untuk solusi kendaraan Anda.",
                            fontSize = 12.sp,
                            color = MrbTextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description Paragraphs
                Text(
                    text = "MRB (Mitra Roda Borneo) adalah platform yang membantu masyarakat menemukan kendaraan impian dengan proses yang mudah, aman, dan terpercaya. Kami menjadi penghubung antara pembeli dan penjual kendaraan, serta memberikan informasi unit yang lengkap dan transparan.",
                    fontSize = 13.sp,
                    color = MrbTextWhite,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Melalui MRB, Anda dapat melihat berbagai pilihan mobil dengan foto berkualitas, spesifikasi lengkap, harga yang jelas, serta mengajukan booking atau kredit langsung dari aplikasi.",
                    fontSize = 13.sp,
                    color = MrbTextWhite,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // VISI & MISI
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Visi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MrbGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Menjadi mitra terpercaya dalam jual beli kendaraan di Kalimantan dan Indonesia.",
                            fontSize = 12.sp,
                            color = MrbTextWhite,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Misi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MrbGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val misiList = listOf(
                            "Menyediakan informasi kendaraan yang akurat dan transparan.",
                            "Mempermudah proses pencarian dan pembelian mobil.",
                            "Memberikan pelayanan yang cepat, aman, dan profesional.",
                            "Membangun kepercayaan antara pembeli dan penjual."
                        )

                        misiList.forEach { misi ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", color = MrbGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(misi, color = MrbTextWhite, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // LAYANAN KAMI
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Layanan Kami",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MrbGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val layananList = listOf(
                            "🚗 Jual beli mobil",
                            "📸 Galeri foto kendaraan lengkap",
                            "💳 Pengajuan kredit",
                            "📅 Booking unit",
                            "📞 Konsultasi pembelian kendaraan",
                            "🤝 Pendampingan hingga proses transaksi selesai"
                        )

                        layananList.forEach { item ->
                            Text(
                                text = item,
                                color = MrbTextWhite,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // INSTAGRAM SECTION
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF241A28)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1306C).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ikuti Instagram Kami | @mrb_sampit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFFF7A00)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Dapatkan informasi unit terbaru, promo menarik, dan konten seputar dunia otomotif langsung dari Instagram MRB.",
                            fontSize = 11.sp,
                            color = MrbTextWhite,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val uri = Uri.parse("http://instagram.com/_u/mrb_sampit")
                                val appIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    setPackage("com.instagram.android")
                                }
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/mrb_sampit"))

                                try {
                                    context.startActivity(appIntent)
                                } catch (e: Exception) {
                                    try {
                                        context.startActivity(webIntent)
                                    } catch (e2: Exception) {
                                        Toast.makeText(context, "Tidak dapat membuka browser", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C), contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Buka Instagram @mrb_sampit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MrbGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
