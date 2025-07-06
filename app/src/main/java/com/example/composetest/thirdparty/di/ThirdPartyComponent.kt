package com.example.composetest.thirdparty.di

import com.example.composetest.thirdparty.ThirdPartyProvider
import dagger.Component

@Component(modules = [ThirdPartyModule::class])
interface ThirdPartyComponent {

  fun injectStarter(started: ThirdPartyProvider)

  @Component.Factory
  interface Factory {
    fun create(module: ThirdPartyModule): ThirdPartyComponent
  }
}