package com.kotlin.net.exception

import java.lang.RuntimeException

/**
 * @author :Sea
 * Date：2021-3-26 17:10
 * PackageName：com.kotlin.net.exception
 * Desc：
 */
class ApiException : RuntimeException {
    private var code: Int? = null

    constructor(throwable: Throwable, code: Int) : super(throwable) {
        this.code = code
    }

    constructor(message: String) : super(Throwable(message))
}