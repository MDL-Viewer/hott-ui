package de.treichels.hott.ui

import de.treichels.hott.util.AbstractCallback
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import tornadofx.*

class CallbackAdapter<T>(val task: FXTask<T>) : AbstractCallback() {
    override fun updateMessage(message: String) = runLater { task.updateMessage(message) }
    override fun warning(title: String, message: String, buttonTexts: Array<String>?, defaultButton: Int): Boolean =alert(Alert.AlertType.WARNING, title,message,buttonTexts)
    override fun confirm(title: String, message: String, buttonTexts: Array<String>?, defaultButton: Int): Boolean = alert(Alert.AlertType.CONFIRMATION, title,message,buttonTexts)

    private fun alert(alertType: Alert.AlertType, title: String, message: String, buttonTexts: Array<String>?): Boolean {
        val buttons = buttonTexts?.map { ButtonType(it) }?.toTypedArray()
        val dialog =  if (buttons!=null) {
            Alert(alertType, message, *buttons)
        } else {
            Alert(alertType, message)
        }.apply { this.title = title }

        return dialog.showAndWait().filter {response -> response == ButtonType.OK || response == buttons?.get(0)}.isPresent
    }

    override fun isCancelled(): Boolean = task.isCancelled
    override fun cancel(): Boolean = task.cancel()

    override fun updateProgress(workDone: Long, totalWork: Long) {
        super.updateProgress(workDone, totalWork)
        runLater { task.updateProgress(workDone, totalWork) }
    }

    override fun updateSubProgress(subWorkDone: Long, subTotalWork: Long) {
        super.updateSubProgress(subWorkDone, subTotalWork)
        runLater { task.updateProgress(subWorkDone, subTotalWork) }
    }
}
