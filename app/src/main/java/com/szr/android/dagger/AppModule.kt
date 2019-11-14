package com.szr.android.dagger

import android.content.Context
import androidx.annotation.Nullable
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideSharedPreferences() = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun provideDatabaseReference() = FirebaseDatabase.getInstance().reference

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()
}