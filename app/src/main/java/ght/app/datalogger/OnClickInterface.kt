package ght.app.datalogger

interface OnClickInterface {
    fun setClick(pos: Int, source: Click)

    enum class Click {
        CONNECT,
        DISCONNECT,
        BUTTON1,
        BUTTON2,
        BUTTON3,
        REMOVE,
        TREND,
    }
}