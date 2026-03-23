package com.example.todofriends

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
class TodoFriendsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "254aa0867f667a543324a53fddaa965d")

        val keyHash = Utility.getKeyHash(this)
        Log.d("KeyHash", "키 해시: $keyHash")
    }
}