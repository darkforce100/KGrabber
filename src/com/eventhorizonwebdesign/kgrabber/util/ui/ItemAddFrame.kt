package com.eventhorizonwebdesign.kgrabber.util.ui

import com.eventhorizonwebdesign.kgrabber.main
import com.eventhorizonwebdesign.kgrabber.util.subreddits
import com.eventhorizonwebdesign.kgrabber.util.users
import com.eventhorizonwebdesign.kgrabber.util.writeDBFile
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

class ItemAddFrame: JFrame() {
    init{
        this.title = "Add"
        this.isVisible = true
        val addPanel = JPanel()
        this.contentPane = addPanel
        addPanel.layout = BorderLayout()
        val addInstruct = JLabel("Add a new /u/ or /r/ to crawl:")
        addInstruct.alignmentX = JLabel.CENTER_ALIGNMENT
        addInstruct.horizontalAlignment = SwingConstants.CENTER
        addInstruct.size = Dimension(500, 50)
        addPanel.add(addInstruct, BorderLayout.NORTH)
        this.size = Dimension(500, 200)
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        val radioContainer = Container()
        radioContainer.layout = GridLayout(1, 2)
        val uRadio = JRadioButton("User")
        val rRadio = JRadioButton("Subreddit")
        val group = ButtonGroup()
        group.add(uRadio)
        group.add(rRadio)
        radioContainer.add(uRadio)
        radioContainer.add(rRadio)
        addPanel.add(radioContainer, BorderLayout.CENTER)
        val dataContainer = Container()
        dataContainer.layout = BorderLayout()
        val inputBox = JTextField()
        val addButton = JButton("Add")
        addButton.addActionListener { _->
            if (inputBox.text == ""){
                //TODO error no text
            } else {
                when {
                    uRadio.isSelected -> users.addElement(inputBox.text)
                    rRadio.isSelected -> subreddits.addElement(inputBox.text)
                    else -> {
                        //TODO error no type selected
                    }
                }
            }
            writeDBFile()
            this.dispose()
            main(Array(0, {""}))
        }
        dataContainer.add(inputBox, BorderLayout.CENTER)
        dataContainer.add(addButton, BorderLayout.SOUTH)
        addPanel.add(dataContainer, BorderLayout.SOUTH)
    }
}