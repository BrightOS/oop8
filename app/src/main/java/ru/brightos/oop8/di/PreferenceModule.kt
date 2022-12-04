package ru.brightos.oop8.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.brightos.oop8.data.PreferenceRepository

@Module
@InstallIn(SingletonComponent::class)
class PreferenceModule {
    @Provides
    @Reusable
    fun providePreferenceRepository(@ApplicationContext context: Context): PreferenceRepository =
        PreferenceRepository(
            context.getSharedPreferences(
                "oop_pref",
                Context.MODE_PRIVATE
            )
        )
}