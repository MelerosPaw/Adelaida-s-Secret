package com.example.composetest.thirdparty

import com.example.composetest.thirdparty.di.DaggerThirdPartyComponent
import com.example.composetest.thirdparty.di.ThirdPartyComponent
import com.example.composetest.thirdparty.di.ThirdPartyModule
import javax.inject.Inject

class ThirdPartyProvider {

  companion object {

    var component: ThirdPartyComponent?= null

    fun initialize(thirdPartyModule: ThirdPartyModule = ThirdPartyModule()) {
      component = DaggerThirdPartyComponent.factory().create(thirdPartyModule)
    }
  }

  @Inject
  lateinit var tpClass: ThirdPartyClass

  init {
    if (component == null) {
      initialize()
    }

    component?.injectStarter(this)
  }
}