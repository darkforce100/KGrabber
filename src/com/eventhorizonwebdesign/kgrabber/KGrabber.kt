/*
 * Copyright (c) 2017. Event Horizon Web Design. All rights reserved.
 */

package com.eventhorizonwebdesign.kgrabber

import com.eventhorizonwebdesign.kgrabber.webutils.PageParserThread
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.awt.GridLayout
import java.io.*
import java.util.*
import javax.swing.*


/**
 * Created by Trenton on 7/14/2017.
 */
val mainFrame = JFrame("KGrabber")
var rootsPanel = JList<String>(DefaultListModel<String>())
val dataHome = System.getProperty("user.home") + System.getProperty("file.separator") + ".KGrabber"
var saveHome = System.getProperty("user.home") + System.getProperty("file.separator") + "Downloads"
var users = Vector<String>()
var subreddits = Vector<String>()

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
        addItemGUI()
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

fun addItemGUI(){
    val addFrame = JFrame("Add")
    addFrame.isVisible = true
    val addPanel = JPanel()
    addFrame.contentPane = addPanel
    addPanel.layout = BorderLayout()
    val addInstruct = JLabel("Add a new /u/ or /r/ to crawl:")
    addInstruct.alignmentX = JLabel.CENTER_ALIGNMENT
    addPanel.add(addInstruct, BorderLayout.NORTH)
    addFrame.size = Dimension(500, 200)
    addFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
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
        addFrame.dispose()
        main(Array(0, {""}))
    }
    dataContainer.add(inputBox, BorderLayout.CENTER)
    dataContainer.add(addButton, BorderLayout.SOUTH)
    addPanel.add(dataContainer, BorderLayout.SOUTH)
}

fun writeDBFile(){
    /*
    * Type 0 = USER
    * Type 1 = SUBREDDIT
    * */
    val usersFinalized = Vector<String>()
    users
            .map { it }
            .forEach {
                when {
                    it.substring(0, 3) == "/u/" -> usersFinalized.addElement(it.substring(3, it.length))
                    it.substring(0, 2) == "u/" -> usersFinalized.addElement(it.substring(2, it.length))
                    it.substring(it.length - 1, it.length) == "/" -> usersFinalized.addElement(it.substring(0, it.length - 1))
                    else -> usersFinalized.addElement(it)
                }
            }
    writeFinalizedUsers(usersFinalized)
    val subsFinalized = Vector<String>()
    subreddits
            .map { it }
            .forEach {
                when {
                    it.substring(0, 3) == "/r/" -> subsFinalized.addElement(it.substring(3, it.length))
                    it.substring(0, 2) == "r/" -> subsFinalized.addElement(it.substring(2, it.length))
                    it.substring(it.length - 1, it.length) == "/" -> subsFinalized.addElement(it.substring(0, it.length - 1))
                    else -> subsFinalized.addElement(it)
                }
            }
    writeFinalizedSubreddits(subsFinalized)
}

fun parseDB(){
    users = Vector()
    subreddits = Vector()
    try {
        val folder = File(dataHome)
        if(folder.exists()){
            if(folder.isDirectory){
                val usersFile = File(dataHome + System.getProperty("file.separator") + "users.bin")
                if (usersFile.exists()){
                    if (usersFile.isFile){
                        val usersReader = BufferedReader(FileReader(usersFile))
                        var line: String?
                        line = usersReader.readLine()
                        while (line != null) {
                            users.addElement(line)
                            line = usersReader.readLine()
                        }
                        usersReader.close()
                    }
                }
                val subsFile = File(dataHome + System.getProperty("file.separator") + "subs.bin")
                if (subsFile.exists()){
                    if (subsFile.isFile){
                        val subsReader = BufferedReader(FileReader(subsFile))
                        var line: String?
                        line = subsReader.readLine()
                        while (line != null) {
                            subreddits.addElement(line)
                            line = subsReader.readLine()
                        }
                        subsReader.close()
                    }
                }
            }
        }
    } catch (e: Exception){
        println(e.stackTrace)
    }
}

fun writeFinalizedUsers(v: Vector<String>){
    try {
        val folder = File(dataHome)
        if(folder.exists()){
            if(folder.isDirectory){
                val file = File(dataHome + System.getProperty("file.separator") + "users.bin")
                if (file.exists()){
                    if (file.isFile){
                        val b = BufferedWriter(FileWriter(file))
                        for (s:String in v){
                            b.write(s)
                            b.newLine()
                        }
                        b.close()
                    } else {
                        file.delete()
                        file.createNewFile()
                        writeFinalizedUsers(v)
                    }
                } else {
                    file.createNewFile()
                    writeFinalizedUsers(v)
                }
            } else {
                folder.delete()
                folder.mkdirs()
                writeFinalizedUsers(v)
            }
        } else {
            folder.mkdirs()
            writeFinalizedUsers(v)
        }
    } catch (e: Exception){
        println(e.stackTrace)
    }
}

fun writeFinalizedSubreddits(v: Vector<String>){
    try {
        val folder = File(dataHome)
        if(folder.exists()){
            if(folder.isDirectory){
                val file = File(dataHome + System.getProperty("file.separator") + "subs.bin")
                if (file.exists()){
                    if (file.isFile){
                        val b = BufferedWriter(FileWriter(file))
                        for (s:String in v){
                            b.write(s)
                            b.newLine()
                        }
                        b.close()
                    } else {
                        file.delete()
                        file.createNewFile()
                        writeFinalizedSubreddits(v)
                    }
                } else {
                    file.createNewFile()
                    writeFinalizedSubreddits(v)
                }
            } else {
                folder.delete()
                folder.mkdirs()
                writeFinalizedSubreddits(v)
            }
        } else {
            folder.mkdirs()
            writeFinalizedSubreddits(v)
        }
    } catch (e: Exception){
        println(e.stackTrace)
    }
}

fun fetchAll(){
    for (u: String in users){
        fetchUser("/u/" + u)
    }
    for (r: String in subreddits){
        fetchSubreddit("/r/" + r)
    }
}

fun fetchUser(u: String){
    val url = "http://reddit.com$u/submitted/"
    print("\nFetching $url...")
    url.replace("/u/", "/user/", true)
    val userFolder = saveHome + System.getProperty("file.separator") + u
    val userFolderFile = File(userFolder)
    userFolderFile.mkdirs()
    PageParserThread(url, userFolder).start()
}

fun fetchSubreddit(r: String){
    //TODO fetch to sub folder
    val url = "http://reddit.com$r/new/"
    print("\nFetching$url...")
    val subredditFolder = saveHome + System.getProperty("file.separator") + r
    val subredditFolderFile = File(subredditFolder)
    subredditFolderFile.mkdirs()
    PageParserThread(url, subredditFolder).start()
}