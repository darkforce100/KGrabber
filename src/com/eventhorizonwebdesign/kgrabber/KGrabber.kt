/*
 * Copyright (c) 2017. Event Horizon Web Design. All rights reserved.
 */

package com.eventhorizonwebdesign.kgrabber

import com.eventhorizonwebdesign.kgrabber.util.*
import com.eventhorizonwebdesign.kgrabber.util.ui.ItemAddFrame
import com.eventhorizonwebdesign.kgrabber.util.ui.ItemContextMenu
import com.eventhorizonwebdesign.kgrabber.util.ui.RootFrame
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.*


/**
 * Created by Trenton on 7/14/2017.
 */

fun main(args: Array<String>?){
    parseDB()
    mainFrame = RootFrame()
}