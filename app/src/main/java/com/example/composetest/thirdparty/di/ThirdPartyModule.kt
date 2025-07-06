package com.example.composetest.thirdparty.di

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck

@DisableInstallInCheck
@Module
open class ThirdPartyModule {

  @Provides
  open fun provideStoreName(): String = "Third-party store name"
}