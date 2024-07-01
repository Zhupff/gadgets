package zhupff.gadgets.qrcode

import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("QRCode")
class GadgetQRCode : GadgetDelegate() {

    fun qrcode(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetQRCodePublication.dependency("qrcode"))
    }
}