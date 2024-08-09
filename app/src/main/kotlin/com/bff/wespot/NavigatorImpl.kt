package com.bff.wespot

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bff.wespot.auth.AuthActivity
import com.bff.wespot.navigation.Navigator
import com.bff.wespot.navigation.util.buildIntent
import timber.log.Timber
import javax.inject.Inject


class NavigatorImpl @Inject constructor() : Navigator {
    private val sharingName = listOf(
        "instagram",
        "kakao"
    )

    override fun navigateToMain(
        context: Context,
    ): Intent {
        val intent = context.buildIntent<MainActivity>()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return intent
    }

    override fun navigateToAuth(context: Context): Intent {
        val intent = context.buildIntent<AuthActivity>()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return intent
    }

    override fun navigateToSharing(context: Context) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.setType("text/plain")

        val chooser: MutableList<Intent> = mutableListOf()
        val resInfo = context.packageManager.queryIntentActivities(sendIntent, 0)

        if (resInfo.isEmpty()) {
            return
        }

        val kakaoIntent = Intent(Intent.ACTION_SEND).apply {
            setComponent(
                ComponentName(
                    "com.kakao.talk",
                    "com.kakao.talk.activity.IntentFilterActivity",
                )
            )
            putExtra(Intent.EXTRA_SUBJECT, "메시지 제목")
            putExtra(Intent.EXTRA_TEXT, "메시지 내용")
            setType("text/plain")
        }

        chooser.add(kakaoIntent)

        val instaIntent = Intent(Intent.ACTION_SEND).apply {
            setComponent(
                ComponentName(
                    "com.instagram.android",
                    "com.instagram.direct.share.handler.DirectShareHandlerActivity",
                ),
            )
            setType("text/plain")
            putExtra(Intent.EXTRA_SUBJECT, "메시지 제목")
            putExtra(Intent.EXTRA_TEXT, "메시지 내용")
        }

        chooser.add(instaIntent)

        val choser = Intent.createChooser(chooser.removeAt(0), "타이틀")
        choser.putExtra(Intent.EXTRA_INITIAL_INTENTS, chooser.toTypedArray())
        context.startActivity(choser)
    }

    override fun navigateToInstaStory(context: Context, file: Uri): Intent {
        val intent = Intent("com.instagram.share.ADD_TO_STORY")
            .apply {
                setDataAndType(null, "image/*")
            }
        if (intent.resolveActivity(context.packageManager) == null) {
            redirectToPlayStoreForInstagram(context)
        }

        intent.apply {
            putExtra("source_application", BuildConfig.FACEBOOK_APP_ID)
            setDataAndType(file, "image/jpeg")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return intent
    }

    private fun redirectToPlayStoreForInstagram(context: Context) {
        val appStoreIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=com.instagram.android"),
        )
        appStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(appStoreIntent)
    }
}
