package ght.app.datalogger

import ght.app.datalogger.UnitAdapter.ViewHolder

/**
 * This interface is for click events happening in an adapter and trigger events in a
 * Fragment to determine from which item the click event happened.
 */

interface EventInterface {

    /**
     * Function for updating the [ViewHolder] at the given position
     * @param pos Position of item that triggered the event
     * @param source Source of [Click] to handle in a fragment
     */
    fun setClick(pos: Int, source: Click)

    enum class Click {
        CONNECT,
        DISCONNECT,
        BUTTON1,
        BUTTON2,
        BUTTON3,
        REMOVE,
        TREND,
        REFRESHTREND,
    }
}