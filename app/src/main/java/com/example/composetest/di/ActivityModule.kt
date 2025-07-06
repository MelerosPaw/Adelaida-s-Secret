package com.example.composetest.di

import android.content.Context
import com.example.composetest.ui.viewmodel.ResourceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityModule {

  @ActivityRetainedScoped
  @Provides
  fun provideResourceManager(@ActivityContext context: Context): ResourceManager =
    ResourceManager(context)
}