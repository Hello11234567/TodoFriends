package com.example.todofriends

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class TodoFriendsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "254aa0867f667a543324a53fddaa965d")
    }
}