package com.example.swiftshare.common.utils

import android.util.Patterns

object Validators {

    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean =
        password.length >= 8

    fun isValidPhoneNumber(phone: String): Boolean =
        phone.isNotBlank() && Patterns.PHONE.matcher(phone).matches() && phone.length in 7..15

    fun isNotEmpty(value: String): Boolean = value.trim().isNotEmpty()
}
