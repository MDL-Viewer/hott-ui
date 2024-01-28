package de.treichels.hott.ui

import javafx.concurrent.Task
import java.io.File

/*
 * A JavaFX task that copies a file in background.
 */
class FileCopyTask(
    private val source: File,
    private val target: File,
    private val overwrite: Boolean = false,
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE
) : Task<File>() {
    override fun call(): File {
        if (!source.exists()) {
            throw NoSuchFileException(file = source, reason = "The source file doesn't exist.")
        }

        if (target.exists()) {
            val stillExists = if (!overwrite) true else !target.delete()

            if (stillExists) {
                throw FileAlreadyExistsException(
                    file = source,
                    other = target,
                    reason = "The destination file already exists."
                )
            }
        }

        if (source.isDirectory) {
            if (!target.mkdirs())
                throw FileSystemException(file = source, other = target, reason = "Failed to create target directory.")
        } else {
            target.parentFile?.mkdirs()

            val totalBytes = source.length()
            source.inputStream().use { input ->
                target.outputStream().use { output ->
                    var bytesCopied: Long = 0
                    val buffer = ByteArray(bufferSize)
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        output.write(buffer, 0, bytes)
                        bytesCopied += bytes
                        updateProgress(bytesCopied, totalBytes)
                        bytes = input.read(buffer)
                    }
                }
            }
        }

        return target
    }
}
