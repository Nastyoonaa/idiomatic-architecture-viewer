package com.app.core.network

import uml.UmlDiagram

@UmlDiagram
class NetworkClient {

    fun execute(
        path: String
    ): String {

        return "GET $path"
    }
}
