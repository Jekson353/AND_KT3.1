package com.samoylenko.kt12.uimodel

import com.samoylenko.kt12.error.ApiError
import java.io.IOException

class ApiException(val error: ApiError, throwable: Throwable? = null) : IOException (throwable)