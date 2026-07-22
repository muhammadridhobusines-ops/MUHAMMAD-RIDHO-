package com.example.data.model

data class CreditRequest(
    val id: String,
    val carId: String,
    val carName: String,
    val carPriceRp: Long,
    val dpRp: Long,
    val tenorMonths: Int,
    val monthlyInstallmentRp: Long,
    val applicantName: String,
    val applicantPhone: String,
    val applicantJob: String = "Swasta / Wiraswasta",
    val status: String = "Dalam Proses Review"
)

data class BookingRequest(
    val id: String,
    val carId: String,
    val carName: String,
    val userName: String,
    val userPhone: String,
    val bookingDate: String,
    val bookingFeeRp: Long = 2000000,
    val paymentMethod: String = "Transfer Bank / QRIS",
    val status: String = "Terkonfirmasi"
)
