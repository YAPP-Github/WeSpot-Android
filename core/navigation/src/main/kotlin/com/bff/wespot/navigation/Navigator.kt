package com.bff.wespot.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri

interface Navigator {
    fun navigateToMain(
        context: Context,
        targetId: Pair<String, Int>,
        date: Pair<String, String>,
        type: Pair<String, String>,
    ) : Intent

    fun navigateToAuth(context: Context): Intent

    fun navigateToInstaStory(
        context: Context,
        file: Uri,
    ): Intent

    fun navigateToSharing(context: Context)
}