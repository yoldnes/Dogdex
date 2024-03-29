package com.example.dogdex

import android.util.Patterns


fun isValidEmail(email: String?): Boolean {
    return !email.isNullOrEmpty() &&
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
}