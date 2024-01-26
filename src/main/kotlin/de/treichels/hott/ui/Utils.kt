package de.treichels.hott.ui

import tornadofx.*
import java.io.File

fun File.copyTo(task: FXTask<*>, target: File, overwrite: Boolean = false, bufferSize: Int = DEFAULT_BUFFER_SIZE): File {
    if (!this.exists()) {
        throw NoSuchFileException(file = this, reason = "The source file doesn't exist.")
    }

    if (target.exists()) {
        val stillExists = if (!overwrite) true else !target.delete()

        if (stillExists) {
            throw FileAlreadyExistsException(file = this,
                    other = target,
                    reason = "The destination file already exists.")
        }
    }

    if (this.isDirectory) {
        if (!target.mkdirs())
            throw FileSystemException(file = this, other = target, reason = "Failed to create target directory.")
    } else {
        target.parentFile?.mkdirs()

        val totalBytes = this.length()
        this.inputStream().use { input ->
            target.outputStream().use { output ->
                var bytesCopied: Long = 0
                val buffer = ByteArray(bufferSize)
                var bytes = input.read(buffer)
                while (bytes >= 0) {
                    output.write(buffer, 0, bytes)
                    bytesCopied += bytes
                    task.updateProgress(bytesCopied, totalBytes)
                    bytes = input.read(buffer)
                }
            }
        }
    }

    return target
}
