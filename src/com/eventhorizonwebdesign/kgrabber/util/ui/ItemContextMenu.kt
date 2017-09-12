package com.eventhorizonwebdesign.kgrabber.util.ui

import javax.swing.JMenuItem
import javax.swing.JPopupMenu


internal class ItemContextMenu : JPopupMenu() {
    val deleteItem: JMenuItem = JMenuItem("Delete")
    init {
        add(deleteItem)
    }
}