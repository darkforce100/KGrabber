/*
 * Copyright (c) 2017. Event Horizon Web Design. All rights reserved.
 */

package com.eventhorizonwebdesign.kgrabber

import com.eventhorizonwebdesign.kgrabber.util.*
import com.eventhorizonwebdesign.kgrabber.util.ui.ItemAddFrame
import com.eventhorizonwebdesign.kgrabber.util.ui.ItemContextMenu
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
    val values = Vector<String>()
    for (e:String in users){
        values.addElement("/u/" + e)
    }
    for (e:String in subreddits){
        values.addElement("/r/" + e)
    }
    rootsPanel = JList(values)
    rootsPanel.addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            if (SwingUtilities.isRightMouseButton(e)) {
                rootsPanel.selectedIndex = rootsPanel.locationToIndex(e.point)
                doPop(e)
            }
        }

        private fun doPop(e: MouseEvent) {
            val menu = ItemContextMenu()
            menu.deleteItem.addActionListener { _->
                if (values[rootsPanel.selectedIndex].contains("/u/")){
                    users
                            .filter { it.contains(values[rootsPanel.selectedIndex].substring(4, values[rootsPanel.selectedIndex].length), true) }
                            .forEach { users.remove(it) }
                } else if (values[rootsPanel.selectedIndex].contains("/r/")){
                    subreddits
                            .filter { it.contains(values[rootsPanel.selectedIndex].substring(4, values[rootsPanel.selectedIndex].length), true) }
                            .forEach { subreddits.remove(it) }
                }
                writeDBFile()
                mainFrame.dispose()
                main(null)
            }
            menu.show(e.component, e.x, e.y)
        }
    })
    mainFrame.isVisible = true
    val mainPanel = JPanel()
    mainFrame.contentPane = mainPanel
    mainPanel.layout = BorderLayout()
    val toolbar = Container()
    toolbar.layout = GridLayout(1, 6)
    mainFrame.size = Dimension(600, 200)
    mainFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val fetch = JButton("Fetch All")
    fetch.addActionListener { _->
        fetchAll()
    }
    val settings = JButton("Settings")
    settings.addActionListener { _->
        val chooser = JFileChooser()
        chooser.currentDirectory = java.io.File(".")
        chooser.dialogTitle = "Choose Download Location"
        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        chooser.isAcceptAllFileFilterUsed = false
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            saveHome = chooser.selectedFile.path.toString()
        }
    }
    val add = JButton("Add")
    add.addActionListener { _->
        mainFrame.dispose()
        ItemAddFrame()
    }
    val clear = JButton("Clear List")
    clear.addActionListener { _->
        users = Vector()
        subreddits = Vector()
        writeDBFile()
        mainFrame.dispose()
        main(null)
    }
    toolbar.add(settings)
    toolbar.add(Container())
    toolbar.add(Container())
    toolbar.add(clear)
    toolbar.add(add)
    toolbar.add(fetch)
    mainFrame.add(toolbar, BorderLayout.NORTH)
    rootsPanel.dragEnabled = false
    rootsPanel.layoutOrientation = JList.VERTICAL
    mainFrame.add(JScrollPane(rootsPanel), BorderLayout.CENTER)
    mainPanel.validate()
    mainPanel.isVisible = true
}