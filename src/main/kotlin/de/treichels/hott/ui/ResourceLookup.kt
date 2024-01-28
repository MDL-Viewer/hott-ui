package de.treichels.hott.ui

import javafx.scene.image.Image
import kotlin.reflect.KClass

class ResourceLookup(clazz: KClass<*>) {
    private val baseName = clazz.java.packageName

    fun image(name: String): Image {
        val url = ClassLoader.getSystemResource("${baseName}/${name}")
        return Image(url.toString())
    }
}