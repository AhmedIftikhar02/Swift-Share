package com.example.swiftshare.common.utils

import android.util.Patterns

/**
 * Common input validators, used by any form screen (login, signup, edit profile, etc.)
 * Kept as plain functions (no Android Context/View dependency) so they're trivially unit
 * testable and reusable from both Fragments and ViewModels.
 */
object Validators {

    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean =
        password.length >= 8

    fun isValidPhoneNumber(phone: String): Boolean =
        phone.isNotBlank() && Patterns.PHONE.matcher(phone).matches() && phone.length in 7..15

    fun isNotEmpty(value: String): Boolean = value.trim().isNotEmpty()
}
