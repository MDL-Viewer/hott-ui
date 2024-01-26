package de.treichels.hott.ui

import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.scene.control.Alert
import javafx.stage.Stage
import org.w3c.dom.Element
import org.w3c.dom.events.EventTarget
import tornadofx.*
import java.awt.Desktop
import java.net.URI

class MessageDialog(alertType: Alert.AlertType, header: String, html: Boolean, message: String, vararg args: Any) : Alert(alertType) {
    init {
        (dialogPane.scene.window as Stage).icons += ResourceLookup(MessageDialog).image("icon.png")
        headerText = header

        val text = String.format(message, *args)

        if (html) {
            dialogPane.content = webview {
                prefHeight = 400.0
                prefWidth = 400.0
                engine.loadContent(text)
                engine.loadWorker.stateProperty().addListener { _, _, newState ->
                    if (newState == Worker.State.SUCCEEDED) {
                        // loop though all <a> tags
                        val links = engine.document.getElementsByTagName("a")
                        for (i in 0 until links.length) {
                            val link = links.item(i) as Element
                            val url = link.getAttribute("href")

                            // disable normal onclick event handler as it will load the page into the dialog
                            link.setAttribute("onclick", "return false;")

                            // instead, load the url using the system default web browser
                            (link as EventTarget).addEventListener("click", { Desktop.getDesktop().browse(URI(url)) }, false)
                        }
                    }
                }
            }
        } else
            contentText = text
    }

    constructor(alertType: Alert.AlertType, header: String, message: String, vararg args: Any) : this(alertType, header, false, message, *args)

    companion object {
        fun show(alertType: Alert.AlertType, header: String, html: Boolean, message: String, vararg args: Any) {
            Platform.runLater { MessageDialog(alertType, header, html, message, *args).showAndWait() }
        }

        fun show(alertType: Alert.AlertType, header: String, message: String, vararg args: Any) {
            Platform.runLater { MessageDialog(alertType, header, false, message, *args).showAndWait() }
        }
    }
}
