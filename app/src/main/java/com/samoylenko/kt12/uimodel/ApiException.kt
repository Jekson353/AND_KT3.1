package com.samoylenko.kt12.uimodel

import java.io.IOException

class ApiException(val error: ApiError, throwable: Throwable? = null) : IOException (throwable)