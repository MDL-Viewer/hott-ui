package de.treichels.hott.ui

import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Stage
import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.Logger

class ExceptionDialog(throwable: Throwable) : Alert(AlertType.ERROR) {
    init {
        Logger.getLogger(javaClass.name).throwing(javaClass.name, "<init>", throwable)

        title = "Error"
        (dialogPane.scene.window as Stage).icons += ResourceLookup(ExceptionDialog::class).image("icon.png")
        headerText = null

        var message: String? = throwable.localizedMessage
        if (message == null) message = throwable.message
        if (message == null) message = throwable.javaClass.simpleName
        contentText = message

        // Create expandable Exception.
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))

        // Set expandable Exception into the dialog pane.
        dialogPane.expandableContent = GridPane().apply {
            maxWidth = Double.MAX_VALUE
            Label("The exception stacktrace was:").apply {
                add(this, 0, 0)
            }
            TextArea(sw.toString()).apply {
                isEditable = false
                isWrapText = true
                maxWidth = Double.MAX_VALUE
                maxHeight = Double.MAX_VALUE
                add(this, 0, 1)
                GridPane.setVgrow(this, Priority.ALWAYS)
            }
        }
    }

    companion object {
        private var SHOWING = false

        @Synchronized
        fun show(throwable: Throwable) {
            throwable.printStackTrace()

            // show only one instance of the dialog at a time
            if (!SHOWING) {
                SHOWING = true
                ExceptionDialog(throwable).showAndWait()
                SHOWING = false
            }
        }
    }
}
