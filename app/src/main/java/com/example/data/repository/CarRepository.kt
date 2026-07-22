package com.example.data.repository

import com.example.R
import com.example.data.local.FavoriteCarDao
import com.example.data.local.FavoriteCarEntity
import com.example.data.model.BookingRequest
import com.example.data.model.CarItem
import com.example.data.model.CreditRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class CarRepository(
    private val favoriteCarDao: FavoriteCarDao
) {
    private val _cars = MutableStateFlow<List<CarItem>>(initialCarData)
    val cars: StateFlow<List<CarItem>> = _cars.asStateFlow()

    private val _creditRequests = MutableStateFlow<List<CreditRequest>>(emptyList())
    val creditRequests: StateFlow<List<CreditRequest>> = _creditRequests.asStateFlow()

    private val _bookingRequests = MutableStateFlow<List<BookingRequest>>(emptyList())
    val bookingRequests: StateFlow<List<BookingRequest>> = _bookingRequests.asStateFlow()

    val favoriteIds: Flow<List<String>> = favoriteCarDao.getAllFavoriteIds()

    val favoriteCars: Flow<List<CarItem>> = favoriteIds.map { ids ->
        val currentCars = _cars.value
        currentCars.filter { ids.contains(it.id) }
    }

    suspend fun toggleFavorite(carId: String, currentIsFavorite: Boolean) {
        if (currentIsFavorite) {
            favoriteCarDao.removeFavorite(carId)
        } else {
            favoriteCarDao.addFavorite(FavoriteCarEntity(carId))
        }
    }

    fun isFavorite(carId: String): Flow<Boolean> {
        return favoriteCarDao.isFavorite(carId)
    }

    fun getCarById(id: String): CarItem? {
        return _cars.value.find { it.id == id }
    }

    fun addCar(newCar: CarItem) {
        _cars.value = listOf(newCar) + _cars.value
    }

    fun updateCar(updatedCar: CarItem) {
        _cars.value = _cars.value.map { if (it.id == updatedCar.id) updatedCar else it }
    }

    fun deleteCar(carId: String) {
        _cars.value = _cars.value.filterNot { it.id == carId }
    }

    fun submitCreditRequest(request: CreditRequest) {
        _creditRequests.value = listOf(request) + _creditRequests.value
    }

    fun submitBookingRequest(request: BookingRequest) {
        _bookingRequests.value = listOf(request) + _bookingRequests.value
    }

    companion object {
        val initialCarData = listOf(
            CarItem(
                id = "mrb-001",
                name = "Toyota Innova Reborn 2.4 G",
                brand = "Toyota",
                model = "Innova Reborn",
                year = 2021,
                priceRp = 395000000L,
                location = "Sampit, Kalteng",
                dealerName = "MRB Central Sampit",
                fuelType = "Diesel",
                transmission = "Automatic",
                kilometer = 45000,
                isHot = true,
                isVerified = true,
                isPromo = false,
                isReadyCredit = true,
                category = "MPV",
                imageRes = R.drawable.img_car_innova,
                engine = "2.393 cc Turbo Diesel 2GD-FTV",
                color = "Hitam Metalik",
                stnkStatus = "Aktif (Lengkap)",
                bpkbStatus = "Ready On Hand",
                taxStatus = "Pajak Hidup Kalteng",
                description = "Kondisi unit super mulus seperti baru! Mesin sangat kering, interior wangi dan sangat terawat, AC dingin menggigil. Siap pakai luar kota Kalimantan."
            ),
            CarItem(
                id = "mrb-002",
                name = "Toyota GR Supra 1.5 Sport",
                brand = "Toyota",
                model = "GR Supra",
                year = 2022,
                priceRp = 272000000L,
                location = "Sampit, Kalteng",
                dealerName = "MRB Luxury Kalimantan",
                fuelType = "Bensin",
                transmission = "Automatic",
                kilometer = 12000,
                isHot = true,
                isVerified = true,
                isPromo = true,
                isReadyCredit = true,
                category = "Sport",
                imageRes = R.drawable.img_car_supra,
                engine = "1.998 cc Twin Scroll Turbo Sport",
                color = "Kuning Solar",
                stnkStatus = "Aktif s/d 2027",
                bpkbStatus = "Ready On Hand",
                taxStatus = "Pajak Hidup",
                description = "Sports car langka di Kalimantan! Desain aerodinamis mewah, akselerasi responsif, knalpot sport standard bawaan pabrik, pemakaian terawat penuh."
            ),
            CarItem(
                id = "mrb-003",
                name = "Honda HR-V 1.5 SE",
                brand = "Honda",
                model = "HR-V",
                year = 2021,
                priceRp = 248000000L,
                location = "Palangka Raya, Kalteng",
                dealerName = "MRB Cabang Palangka",
                fuelType = "Bensin",
                transmission = "Automatic",
                kilometer = 22000,
                isHot = false,
                isVerified = true,
                isPromo = true,
                isReadyCredit = true,
                category = "SUV",
                imageRes = R.drawable.img_car_hrv,
                engine = "1.498 cc i-VTEC DOHC",
                color = "Putih Mutiara",
                stnkStatus = "Aktif (Lengkap)",
                bpkbStatus = "Ready On Hand",
                taxStatus = "Pajak Hidup",
                description = "Panoramic sunroof mewah, lampu LED stylish, kabin lapang dan nyaman. Perawatan rutin selalu di bengkel resmi Honda."
            ),
            CarItem(
                id = "mrb-004",
                name = "Mitsubishi Pajero Sport Dakar 4x2",
                brand = "Mitsubishi",
                model = "Pajero Sport",
                year = 2020,
                priceRp = 389000000L,
                location = "Banjarmasin, Kalsel",
                dealerName = "MRB Borneo Utama",
                fuelType = "Diesel",
                transmission = "Automatic",
                kilometer = 38000,
                isHot = true,
                isVerified = true,
                isPromo = false,
                isReadyCredit = true,
                category = "SUV",
                imageRes = R.drawable.img_banner_suv,
                engine = "2.442 cc MIVEC Turbo Diesel",
                color = "Hitam Diamond",
                stnkStatus = "Aktif",
                bpkbStatus = "Ready",
                taxStatus = "Pajak Hidup",
                description = "SUV gagah tangguh khas Kalimantan! Sunroof, Electric Seat, fitur keselamatan canggih, velg racing original Dakar."
            ),
            CarItem(
                id = "mrb-005",
                name = "Toyota Avanza 1.5 G",
                brand = "Toyota",
                model = "Avanza",
                year = 2022,
                priceRp = 198000000L,
                location = "Sampit, Kalteng",
                dealerName = "MRB Sampit Central",
                fuelType = "Bensin",
                transmission = "Automatic",
                kilometer = 18000,
                isHot = false,
                isVerified = true,
                isPromo = true,
                isReadyCredit = true,
                category = "MPV",
                engine = "1.496 cc Dual VVT-i",
                color = "Perak Metalik",
                stnkStatus = "Aktif",
                bpkbStatus = "Ready",
                taxStatus = "Pajak Hidup",
                description = "MPV favorit keluarga Indonesia. Irit bbm, suspensi empuk, kabin senyap dan ruang bagasi luas."
            ),
            CarItem(
                id = "mrb-006",
                name = "Mitsubishi Triton Double Cabin 4x4",
                brand = "Mitsubishi",
                model = "Triton",
                year = 2022,
                priceRp = 435000000L,
                location = "Sampit, Kalteng",
                dealerName = "MRB Heavy & Pickups",
                fuelType = "Diesel",
                transmission = "Manual",
                kilometer = 25000,
                isHot = true,
                isVerified = true,
                isPromo = false,
                isReadyCredit = true,
                category = "Double Cabin",
                engine = "2.442 cc MIVEC Turbo Clean Diesel",
                color = "Putih",
                stnkStatus = "Aktif (Lengkap)",
                bpkbStatus = "Ready",
                taxStatus = "Pajak Hidup",
                description = "Tangguh menembus segala medan area perkebunan & pertambangan Kalimantan. Sistem 4WD Super Select berjalan sempurna."
            ),
            CarItem(
                id = "mrb-007",
                name = "Lexus RX350 Luxury",
                brand = "Lexus",
                model = "RX350",
                year = 2021,
                priceRp = 890000000L,
                location = "Sampit, Kalteng",
                dealerName = "MRB Premium Select",
                fuelType = "Bensin",
                transmission = "Automatic",
                kilometer = 15000,
                isHot = true,
                isVerified = true,
                isPromo = false,
                isReadyCredit = true,
                category = "Luxury",
                engine = "3.456 cc V6 Dual VVT-i",
                color = "Hitam Glossy",
                stnkStatus = "Aktif",
                bpkbStatus = "Ready On Hand",
                taxStatus = "Pajak Hidup",
                description = "Kemewahan tingkat tertinggi. Interior kulit semi-aniline, Mark Levinson Surround Sound System, Head-Up Display."
            )
        )
    }
}
