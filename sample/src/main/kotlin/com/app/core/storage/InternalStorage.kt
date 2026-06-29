package com.app.core.storage

object InternalStorage {

    fun read(
        key: String
    ): String {
        return "value:$key"
    }
}
