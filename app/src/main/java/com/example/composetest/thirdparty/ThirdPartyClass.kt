package com.example.composetest.thirdparty

import javax.inject.Inject

class ThirdPartyClass @Inject constructor(
  val storeName: String
)