package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MrbDatabase
import com.example.data.model.BookingRequest
import com.example.data.model.CarItem
import com.example.data.model.ChatMessage
import com.example.data.model.CreditRequest
import com.example.data.repository.CarRepository
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MrbDatabase.getDatabase(application)
    val carRepository = CarRepository(db.favoriteCarDao())
    val chatRepository = ChatRepository()

    // Filter & Search states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedSort = MutableStateFlow("Terbaru")
    val selectedSort: StateFlow<String> = _selectedSort.asStateFlow()

    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    // Favorite Car IDs flow from Room
    val favoriteIds: StateFlow<List<String>> = carRepository.favoriteIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All cars
    val allCars: StateFlow<List<CarItem>> = carRepository.cars

    // Filtered cars
    val filteredCars: StateFlow<List<CarItem>> = combine(
        carRepository.cars,
        _searchQuery,
        _selectedCategory,
        _selectedSort
    ) { carsList, query, category, sort ->
        carsList.filter { car ->
            val matchesQuery = query.isEmpty() || car.name.contains(query, ignoreCase = true) ||
                    car.brand.contains(query, ignoreCase = true) ||
                    car.model.contains(query, ignoreCase = true)
            val matchesCategory = category == "Semua" || car.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }.sortedWith { c1, c2 ->
            when (sort) {
                "Termurah" -> c1.priceRp.compareTo(c2.priceRp)
                "Termahal" -> c2.priceRp.compareTo(c1.priceRp)
                "Tahun" -> c2.year.compareTo(c1.year)
                else -> 0 // "Terbaru"
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorite Cars List
    val favoriteCars: StateFlow<List<CarItem>> = combine(carRepository.cars, favoriteIds) { cars, ids ->
        cars.filter { ids.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Car for Detail Screen
    private val _selectedCar = MutableStateFlow<CarItem?>(carRepository.getCarById("mrb-001"))
    val selectedCar: StateFlow<CarItem?> = _selectedCar.asStateFlow()

    // User Profile
    val userName = MutableStateFlow("Ridho Pratama")
    val userPhone = MutableStateFlow("0857 5456 3358")
    val userEmail = MutableStateFlow("ridho.pratama@gmail.com")
    val userMemberBadge = MutableStateFlow("Member Gold")
    val userRole = MutableStateFlow("Pembeli (Anggota)")
    val userPhotoUrl = MutableStateFlow<String?>("")
    val userPhotoRes = MutableStateFlow<Int?>(null)

    // Dynamic PIN Management (simulating database persistence)
    val adminPins = MutableStateFlow(setOf("20050307"))
    val sellerPins = MutableStateFlow(
        setOf(
            "12345678",
            "1234",
            "84729156",
            "00000000",
            "11111111",
            "36198524",
            "59271483",
            "17846395",
            "93485127",
            "62519748",
            "48173692",
            "75928416",
            "21694857",
            "89352741"
        )
    )

    fun validateAdminPin(inputPin: String): Boolean {
        return adminPins.value.contains(inputPin.trim())
    }

    fun validateSellerPin(inputPin: String): Boolean {
        return sellerPins.value.contains(inputPin.trim())
    }

    fun addSellerPin(newPin: String) {
        if (newPin.trim().length == 8) {
            sellerPins.value = sellerPins.value + newPin.trim()
        }
    }

    fun removeSellerPin(pin: String) {
        sellerPins.value = sellerPins.value - pin.trim()
    }

    fun setUserRole(role: String) {
        userRole.value = role
        when {
            role.contains("Admin", ignoreCase = true) -> userMemberBadge.value = "Admin"
            role.contains("Penjual", ignoreCase = true) -> userMemberBadge.value = "Mitra Penjual"
            else -> userMemberBadge.value = "Pembeli (Anggota)"
        }
    }

    fun updateProfile(
        newName: String,
        newPhone: String,
        newEmail: String,
        newPhotoUrl: String? = null,
        newPhotoRes: Int? = null
    ) {
        if (newName.isNotBlank()) userName.value = newName.trim()
        if (newPhone.isNotBlank()) userPhone.value = newPhone.trim()
        if (newEmail.isNotBlank()) userEmail.value = newEmail.trim()
        userPhotoUrl.value = newPhotoUrl
        userPhotoRes.value = newPhotoRes
    }

    // Admin Requests & Approvals
    val creditRequests: StateFlow<List<CreditRequest>> = carRepository.creditRequests
    val bookingRequests: StateFlow<List<BookingRequest>> = carRepository.bookingRequests

    val pendingCars: StateFlow<List<CarItem>> = combine(carRepository.cars) { carsList ->
        carsList[0].filter { it.status.contains("Menunggu", ignoreCase = true) || it.status.contains("Pending", ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun approveCar(carId: String) {
        val car = carRepository.getCarById(carId)
        if (car != null) {
            val updated = car.copy(status = "Ready", isVerified = true)
            carRepository.updateCar(updated)
        }
    }

    fun rejectCar(carId: String, reason: String = "Spesifikasi/foto belum memenuhi standar MRB") {
        carRepository.deleteCar(carId)
    }

    fun updateCarStatus(carId: String, newStatus: String) {
        val car = carRepository.getCarById(carId)
        if (car != null) {
            val updated = car.copy(status = newStatus)
            carRepository.updateCar(updated)
        }
    }

    fun submitCarFromSales(newCar: CarItem) {
        val pendingCar = newCar.copy(status = "Menunggu Persetujuan Admin", isVerified = false)
        carRepository.addCar(pendingCar)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectSort(sort: String) {
        _selectedSort.value = sort
    }

    fun toggleGridView() {
        _isGridView.value = !_isGridView.value
    }

    fun selectCar(car: CarItem) {
        _selectedCar.value = car
    }

    fun toggleFavorite(carId: String) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(carId)
            carRepository.toggleFavorite(carId, isFav)
        }
    }

    fun submitCredit(car: CarItem, dpAmount: Long, tenorMonths: Int, applicantName: String, applicantPhone: String) {
        val loanAmount = car.priceRp - dpAmount
        val interestRatePerYear = 0.08
        val totalInterest = loanAmount * interestRatePerYear * (tenorMonths / 12.0)
        val totalAmount = loanAmount + totalInterest
        val monthly = (totalAmount / tenorMonths).toLong()

        val req = CreditRequest(
            id = "cred-${System.currentTimeMillis()}",
            carId = car.id,
            carName = car.name,
            carPriceRp = car.priceRp,
            dpRp = dpAmount,
            tenorMonths = tenorMonths,
            monthlyInstallmentRp = monthly,
            applicantName = applicantName,
            applicantPhone = applicantPhone
        )
        carRepository.submitCreditRequest(req)
    }

    fun submitBooking(car: CarItem, userName: String, userPhone: String, bookingDate: String) {
        val req = BookingRequest(
            id = "book-${System.currentTimeMillis()}",
            carId = car.id,
            carName = car.name,
            userName = userName,
            userPhone = userPhone,
            bookingDate = bookingDate
        )
        carRepository.submitBookingRequest(req)
    }

    fun addNewCarAdmin(newCar: CarItem) {
        carRepository.addCar(newCar)
    }

    fun updateCarAdmin(updatedCar: CarItem) {
        carRepository.updateCar(updatedCar)
    }

    fun deleteCarAdmin(carId: String) {
        carRepository.deleteCar(carId)
    }

    fun sendChatMessage(threadId: String, text: String, mediaType: ChatMessage.MediaType = ChatMessage.MediaType.TEXT) {
        viewModelScope.launch {
            chatRepository.sendMessage(threadId, text, mediaType)
        }
    }
}
