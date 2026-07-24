package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R

// Material 3 Colors as requested (#1565C0, White, Light Gray)
private val MrbBluePrimary = Color(0xFF1565C0)
private val MrbBlueLight = Color(0xFFE3F2FD)
private val MrbBlueDark = Color(0xFF0D47A1)
private val MrbBgGray = Color(0xFFF8FAFC)
private val MrbCardWhite = Color(0xFFFFFFFF)
private val MrbBorderColor = Color(0xFFE2E8F0)
private val MrbTextPrimary = Color(0xFF0F172A)
private val MrbTextSecondary = Color(0xFF64748B)

enum class PermissionStatus(val label: String, val badgeBg: Color, val badgeText: Color) {
    GRANTED("Diizinkan", Color(0xFFE8F5E9), Color(0xFF1B5E20)),
    DENIED("Ditolak", Color(0xFFFFEBEE), Color(0xFFC62828)),
    NOT_GRANTED("Belum diizinkan", Color(0xFFFFF3E0), Color(0xFFE65100))
}

data class PermissionItemData(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isOptional: Boolean = false,
    var status: PermissionStatus = PermissionStatus.GRANTED
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPermissionsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Initialize 7 permissions requested by user
    val permissionsList = remember {
        mutableStateListOf(
            PermissionItemData(
                id = "photo_video",
                title = "Foto dan Video",
                description = "Digunakan untuk memilih dan mengunggah foto mobil dari galeri.",
                icon = Icons.Default.PhotoLibrary,
                status = PermissionStatus.GRANTED
            ),
            PermissionItemData(
                id = "camera",
                title = "Kamera",
                description = "Digunakan untuk mengambil foto mobil, dokumen, dan bukti transaksi.",
                icon = Icons.Default.CameraAlt,
                status = PermissionStatus.GRANTED
            ),
            PermissionItemData(
                id = "location",
                title = "Lokasi",
                description = "Digunakan untuk mengetahui lokasi penyerahan dan pengembalian kendaraan.",
                icon = Icons.Default.LocationOn,
                status = PermissionStatus.GRANTED
            ),
            PermissionItemData(
                id = "files_docs",
                title = "File dan Dokumen",
                description = "Digunakan untuk mengunggah KTP, SIM, STNK, dan dokumen pendukung lainnya.",
                icon = Icons.Default.FolderOpen,
                status = PermissionStatus.GRANTED
            ),
            PermissionItemData(
                id = "notifications",
                title = "Notifikasi",
                description = "Digunakan untuk mengirim pengingat jadwal sewa, pengembalian, servis kendaraan, dan informasi penting.",
                icon = Icons.Default.Notifications,
                status = PermissionStatus.GRANTED
            ),
            PermissionItemData(
                id = "contacts",
                title = "Kontak (Opsional)",
                description = "Digunakan untuk memilih data pelanggan dari kontak.",
                icon = Icons.Default.Contacts,
                isOptional = true,
                status = PermissionStatus.NOT_GRANTED
            ),
            PermissionItemData(
                id = "phone",
                title = "Telepon (Opsional)",
                description = "Digunakan untuk menghubungi pelanggan langsung dari aplikasi.",
                icon = Icons.Default.Phone,
                isOptional = true,
                status = PermissionStatus.NOT_GRANTED
            )
        )
    }

    var activeFilter by remember { mutableStateOf("Semua") } // "Semua", "Diizinkan", "Belum/Ditolak"
    var selectedDetailItem by remember { mutableStateOf<PermissionItemData?>(null) }
    var itemToChangeStatus by remember { mutableStateOf<PermissionItemData?>(null) }

    val grantedCount = permissionsList.count { it.status == PermissionStatus.GRANTED }
    val totalCount = permissionsList.size
    val progress = if (totalCount > 0) grantedCount.toFloat() / totalCount.toFloat() else 0f

    if (itemToChangeStatus != null) {
        ChangeStatusDialog(
            permissionItem = itemToChangeStatus!!,
            onDismiss = { itemToChangeStatus = null },
            onSelectStatus = { newStatus ->
                val index = permissionsList.indexOfFirst { it.id == itemToChangeStatus!!.id }
                if (index != -1) {
                    permissionsList[index] = permissionsList[index].copy(status = newStatus)
                    Toast.makeText(
                        context,
                        "Izin '${permissionsList[index].title}' diubah ke: ${newStatus.label}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                itemToChangeStatus = null
            }
        )
    }

    if (selectedDetailItem != null) {
        PermissionDetailModal(
            permissionItem = selectedDetailItem!!,
            onDismiss = { selectedDetailItem = null },
            onToggleStatus = { newStatus ->
                val index = permissionsList.indexOfFirst { it.id == selectedDetailItem!!.id }
                if (index != -1) {
                    permissionsList[index] = permissionsList[index].copy(status = newStatus)
                }
                selectedDetailItem = null
            },
            onOpenSettings = {
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Membuka Pengaturan Sistem Android...", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    Scaffold(
        containerColor = MrbBgGray,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Izin Aplikasi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Membuka Pengaturan Sistem Android", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan Sistem",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MrbBluePrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // HEADER HERO CARD WITH LOGO "MRB"
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MrbCardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbBorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // MRB Logo Container
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MrbBlueLight)
                            .border(2.dp, MrbBluePrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mrb_exact_user_logo_512),
                            contentDescription = "Logo MRB",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "MRB",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = MrbBluePrimary,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Mitra Roda Borneo • Layanan Otomotif",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MrbTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Overview Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MrbBlueLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = MrbBluePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Status Keamanan & Privasi",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MrbBlueDark
                                    )
                                }

                                Text(
                                    text = "$grantedCount dari $totalCount Aktif",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MrbBluePrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MrbBluePrimary,
                                trackColor = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FILTER TABS / CHIPS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Semua" to "Semua ($totalCount)",
                    "Diizinkan" to "Diizinkan ($grantedCount)",
                    "Belum/Ditolak" to "Lainnya (${totalCount - grantedCount})"
                ).forEach { (key, label) ->
                    val isSelected = activeFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { activeFilter = key },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MrbBluePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = MrbCardWhite,
                            labelColor = MrbTextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) MrbBluePrimary else MrbBorderColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CATEGORY 1: DIIZINKAN SECTION
            val allowedItems = permissionsList.filter { it.status == PermissionStatus.GRANTED }
            val nonAllowedItems = permissionsList.filter { it.status != PermissionStatus.GRANTED }

            if (activeFilter == "Semua" || activeFilter == "Diizinkan") {
                if (allowedItems.isNotEmpty()) {
                    Text(
                        text = "Diizinkan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MrbBluePrimary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MrbCardWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MrbBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            allowedItems.forEachIndexed { index, item ->
                                PermissionRowItem(
                                    item = item,
                                    onToggle = { isChecked ->
                                        val targetIndex = permissionsList.indexOfFirst { it.id == item.id }
                                        if (targetIndex != -1) {
                                            val newStatus = if (isChecked) PermissionStatus.GRANTED else PermissionStatus.DENIED
                                            permissionsList[targetIndex] = permissionsList[targetIndex].copy(status = newStatus)
                                            Toast.makeText(
                                                context,
                                                "Izin '${item.title}' ${if (isChecked) "Diizinkan" else "Ditolak"}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    onClickRow = { selectedDetailItem = item },
                                    onChangeStatusClick = { itemToChangeStatus = item }
                                )
                                if (index < allowedItems.size - 1) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp),
                                        color = MrbBgGray
                                    ) {}
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // CATEGORY 2: DITOLAK / BELUM DIIZINKAN SECTION
            if (activeFilter == "Semua" || activeFilter == "Belum/Ditolak") {
                if (nonAllowedItems.isNotEmpty()) {
                    Text(
                        text = "Belum Diizinkan / Ditolak",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MrbCardWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MrbBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            nonAllowedItems.forEachIndexed { index, item ->
                                PermissionRowItem(
                                    item = item,
                                    onToggle = { isChecked ->
                                        val targetIndex = permissionsList.indexOfFirst { it.id == item.id }
                                        if (targetIndex != -1) {
                                            val newStatus = if (isChecked) PermissionStatus.GRANTED else PermissionStatus.DENIED
                                            permissionsList[targetIndex] = permissionsList[targetIndex].copy(status = newStatus)
                                            Toast.makeText(
                                                context,
                                                "Izin '${item.title}' ${if (isChecked) "Diizinkan" else "Ditolak"}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    onClickRow = { selectedDetailItem = item },
                                    onChangeStatusClick = { itemToChangeStatus = item }
                                )
                                if (index < nonAllowedItems.size - 1) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp),
                                        color = MrbBgGray
                                    ) {}
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // QUICK ACTIONS BAR (IZINKAN SEMUA & RESET)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MrbBlueLight.copy(alpha = 0.6f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbBluePrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MrbBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kontrol Akses Cepat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MrbBlueDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Gunakan tombol di bawah untuk mengaktifkan seluruh akses fitur MRB secara praktis.",
                        fontSize = 11.sp,
                        color = MrbTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                permissionsList.indices.forEach { idx ->
                                    permissionsList[idx] = permissionsList[idx].copy(status = PermissionStatus.GRANTED)
                                }
                                Toast.makeText(context, "✅ Seluruh izin berhasil Diizinkan!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MrbBluePrimary, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Izinkan Semua", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                permissionsList[0] = permissionsList[0].copy(status = PermissionStatus.GRANTED)
                                permissionsList[1] = permissionsList[1].copy(status = PermissionStatus.GRANTED)
                                permissionsList[2] = permissionsList[2].copy(status = PermissionStatus.GRANTED)
                                permissionsList[3] = permissionsList[3].copy(status = PermissionStatus.GRANTED)
                                permissionsList[4] = permissionsList[4].copy(status = PermissionStatus.GRANTED)
                                permissionsList[5] = permissionsList[5].copy(status = PermissionStatus.NOT_GRANTED)
                                permissionsList[6] = permissionsList[6].copy(status = PermissionStatus.NOT_GRANTED)
                                Toast.makeText(context, "🔄 Izin dikembalikan ke default.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MrbBlueDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MrbBluePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Standard", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // FOOTER PRIVACY STATEMENT
            Text(
                text = "Keamanan dan data Anda terlindungi. MRB hanya menggunakan izin untuk operasional layanan kendaraan dan dokumen resmi.",
                fontSize = 11.sp,
                color = MrbTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// PERMISSION ROW ITEM COMPONENT
@Composable
private fun PermissionRowItem(
    item: PermissionItemData,
    onToggle: (Boolean) -> Unit,
    onClickRow: () -> Unit,
    onChangeStatusClick: () -> Unit
) {
    val isGranted = item.status == PermissionStatus.GRANTED

    val animatedIconBg by animateColorAsState(
        targetValue = if (isGranted) MrbBlueLight else Color(0xFFF1F5F9),
        label = "iconBg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickRow() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Material Icon Container
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(animatedIconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = if (isGranted) MrbBluePrimary else MrbTextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title and Description
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MrbTextPrimary
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Status Badge Chip (clickable to explicitly pick Diizinkan / Ditolak / Belum)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = item.status.badgeBg,
                    modifier = Modifier.clickable { onChangeStatusClick() }
                ) {
                    Text(
                        text = item.status.label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = item.status.badgeText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = item.description,
                fontSize = 11.sp,
                color = MrbTextSecondary,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Toggle Switch
        Switch(
            checked = isGranted,
            onCheckedChange = { onToggle(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MrbBluePrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}

// DIALOG TO MANUALLY CHANGE STATUS TO DIIZINKAN, DITOLAK, OR BELUM DIIZINKAN
@Composable
private fun ChangeStatusDialog(
    permissionItem: PermissionItemData,
    onDismiss: () -> Unit,
    onSelectStatus: (PermissionStatus) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MrbCardWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, MrbBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Ubah Status Izin",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MrbTextPrimary
                )

                Text(
                    text = "Pilih status izin untuk '${permissionItem.title}':",
                    fontSize = 12.sp,
                    color = MrbTextSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                PermissionStatus.values().forEach { st ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectStatus(st) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = permissionItem.status == st,
                            onClick = { onSelectStatus(st) },
                            colors = RadioButtonDefaults.colors(selectedColor = MrbBluePrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = st.badgeBg
                        ) {
                            Text(
                                text = st.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = st.badgeText,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MrbBgGray, contentColor = MrbTextPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Batal")
                }
            }
        }
    }
}

// DETAIL PERMISSION MODAL
@Composable
private fun PermissionDetailModal(
    permissionItem: PermissionItemData,
    onDismiss: () -> Unit,
    onToggleStatus: (PermissionStatus) -> Unit,
    onOpenSettings: () -> Unit
) {
    val isGranted = permissionItem.status == PermissionStatus.GRANTED

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MrbCardWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, MrbBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MrbBlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = permissionItem.icon,
                        contentDescription = null,
                        tint = MrbBluePrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = permissionItem.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MrbTextPrimary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = permissionItem.status.badgeBg,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = permissionItem.status.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = permissionItem.status.badgeText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = permissionItem.description,
                    fontSize = 13.sp,
                    color = MrbTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MrbBgGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MrbBluePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Akses ini hanya digunakan saat fitur terkait aktif. Anda dapat mengubahnya kapan saja.",
                            fontSize = 11.sp,
                            color = MrbTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val nextStatus = if (isGranted) PermissionStatus.DENIED else PermissionStatus.GRANTED
                            onToggleStatus(nextStatus)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isGranted) Color(0xFFC62828) else MrbBluePrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isGranted) "Matikan Izin" else "Diizinkan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onOpenSettings()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MrbBlueDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MrbBluePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pengaturan HP", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
