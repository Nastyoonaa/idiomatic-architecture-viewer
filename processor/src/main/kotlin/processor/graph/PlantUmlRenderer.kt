package com.example.processor.graph

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.File
import java.io.FileOutputStream

object PlantUmlRenderer {

    fun renderPng(
        source: String,
        output: File
    ) {

        output.parentFile?.mkdirs()

        FileOutputStream(output).use { stream ->

            SourceStringReader(source)
                .outputImage(stream)
        }
    }

    fun renderSvg(
        source: String,
        output: File
    ) {

        output.parentFile?.mkdirs()

        FileOutputStream(output).use { stream ->

            SourceStringReader(source)
                .outputImage(
                    stream,
                    FileFormatOption(FileFormat.SVG)
                )
        }
    }
}