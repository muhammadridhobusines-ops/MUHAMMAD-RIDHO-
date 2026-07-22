package com.example.data.model

data class CarGalleryItem(
    val label: String,
    val imageUrl: String? = null,
    val imageRes: Int? = null
)

data class CarItem(
    val id: String,
    val name: String,
    val brand: String,
    val model: String,
    val year: Int,
    val priceRp: Long,
    val location: String = "Sampit, Kalteng",
    val dealerName: String = "MRB Sampit Central",
    val fuelType: String = "Bensin",
    val transmission: String = "Automatic",
    val kilometer: Int = 20000,
    val isHot: Boolean = false,
    val isVerified: Boolean = true,
    val isPromo: Boolean = false,
    val isReadyCredit: Boolean = true,
    val category: String = "SUV",
    val imageRes: Int? = null,
    val imageUrl: String? = null,
    val engine: String = "1.496 cc Dual VVT-i",
    val color: String = "Hitam",
    val stnkStatus: String = "Aktif (Lengkap)",
    val bpkbStatus: String = "Tersedia (Ready)",
    val taxStatus: String = "Pajak Hidup",
    val description: String = "Kondisi unit istimewa, mulus, terawat penuh, bebas banjir, dokumen 100% aman dan bergaransi resmi MRB.",
    val variant: String = "",
    val driveTrain: String = "FWD",
    val passengerCapacity: String = "7 Kursi",
    val condition: String = "Bekas",
    val status: String = "Ready",
    val photoResList: List<Int> = emptyList(),
    val galleryPhotos: List<CarGalleryItem> = emptyList()
)
