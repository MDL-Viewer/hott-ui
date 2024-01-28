package de.treichels.hott.ui

import de.treichels.hott.util.Callback
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType

class CallbackAdapter<T>(val function: () -> T) : Callback, Task<T>() {
    private var subTotalWork: Long = 0
    private var subWorkDone: Long = 0

    override fun updateMessage(message: String) = Platform.runLater { super.updateMessage(message) }

    override fun warning(title: String, message: String, buttonTexts: Array<String>?, defaultButton: Int): Boolean =
        alert(Alert.AlertType.WARNING, title, message, buttonTexts)

    override fun confirm(title: String, message: String, buttonTexts: Array<String>?, defaultButton: Int): Boolean =
        alert(Alert.AlertType.CONFIRMATION, title, message, buttonTexts)

    override fun call(): T {
        return function()
    }

    override fun updateProgress(workDone: Long, totalWork: Long) {
        Platform.runLater {
            super<Task>.updateProgress(workDone, totalWork)
        }
    }

    override fun updateSubProgress(subWorkDone: Long, subTotalWork: Long) {
        Platform.runLater {
            super<Task>.updateProgress(subWorkDone, subTotalWork)
            this.subTotalWork = subTotalWork
            this.subWorkDone = subWorkDone
        }
    }

    companion object {
        private fun alert(
            alertType: Alert.AlertType, title: String, message: String, buttonTexts: Array<String>?
        ): Boolean {
            val buttons = buttonTexts?.map { ButtonType(it) }?.toTypedArray()
            val dialog = if (buttons != null) {
                Alert(alertType, message, *buttons)
            } else {
                Alert(alertType, message)
            }.apply { this.title = title }

            return dialog.showAndWait()
                .filter { response -> response == ButtonType.OK || response == buttons?.get(0) }.isPresent
        }
    }
}
