package com.eventhorizonwebdesign.kgrabber.util

import com.eventhorizonwebdesign.kgrabber.util.web.PageParserThread
import java.io.*
import java.util.*
import javax.swing.DefaultListModel
import javax.swing.JFrame
import javax.swing.JList

var accessedPages = Vector<String>()
var mainFrame: JFrame? = null
var rootsPanel = JList<String>(DefaultListModel<String>())
val dataHome = System.getProperty("user.home") + System.getProperty("file.separator") + ".KGrabber"
var saveHome = System.getProperty("user.home") + System.getProperty("file.separator") + "Downloads"
var users = Vector<String>()
var subreddits = Vector<String>()

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