/*
 * Copyright (c) 2017. Event Horizon Web Design. All rights reserved.
 */

package com.eventhorizonwebdesign.kgrabber.webutils

import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import java.io.File
import java.net.URL
import java.util.Vector

/**
 * Created by Trenton on 7/15/2017.
 */

var accessedPages = Vector<String>()

class PageParserThread constructor(s: String, f: String) : Thread() {
    val s: String
    val f: String
    init {
        this.s = s
        this.f = f
    }
    override fun run() {
        try {
            print("\nLINK CRAWLER " + s)
            val doc = Jsoup.connect(s).ignoreContentType(true).get()
            val links = doc.select("a[href]")

            for (link in links) {
                // Site filter
                // @INDEV print("\n -- " + link.attr("abs:href"))
                if ((link.attr("abs:href").contains("://imgur.com/", true)
                        || link.attr("abs:href").contains("://i.imgur.com/", true)
                        || link.attr("abs:href").contains("://www.flickr.com/", true)
                        || link.attr("abs:href").contains("://gfycat.com/", true)
                        || link.attr("abs:href").contains("://i.redd.it/", true)
                        || link.attr("abs:href").contains("://www.instagram.com/", true))
                        && !link.attr("abs:href").contains("://www.facebook.com/tr", true)
                        && !link.attr("abs:href").contains("cloudfront.net", true)) {
                    ImageDownloaderThread(link.attr("abs:href"), f).start()
                } else if (link.text().contains("next", true)) {
                    print("\nNEXT FOUND")
                    PageParserThread(link.attr("abs:href"), f).start()
                }
            }
        } catch (e: Exception) {
            print("\n" + e.message)
            print("\n" + s + " 404ed :(")
        }
    }


}

class ImageDownloaderThread constructor(s: String, f: String) : Thread() {
    val s: String
    val f: String
    init {
        this.s = s
        this.f = f
    }
    override fun run() {
        print("\n --- " + s)
        var accessed: Boolean = false
        for (i: String in accessedPages){
            if (s == i){
                accessed = true
            }
        }
        if (!accessed) {
            accessedPages.addElement(s)
            try {
                if ((s.contains("jpg")
                        || s.contains("png")
                        || s.contains("gif")
                        || s.contains("mp4"))
                        && !s.contains("quantserve")
                        && !s.contains("facebook.com")
                        && !s.contains("cloudfront.net")) {
                    print("\n SELECTED")
                    FileUtils.copyURLToFile(URL(s), File(f + System.getProperty("file.separator") + s.substring(s.lastIndexOf('/') + 1, s.length)))
                } else {
                    print("\n PAGE SELECTED")
                    val doc = Jsoup.connect(s).get()

                    val media = doc.select("[src]")

                    for (src in media) {
                        print("\n ---P--- " + src.attr("abs:src"))
                        // Image Filter
                        if ((src.tagName() == "img" || src.tagName() == "source") && (src.attr("abs:src").contains("jpg")
                                || src.attr("abs:src").contains("png")
                                || src.attr("abs:src").contains("gif")
                                || src.attr("abs:src").contains("mp4")
                                || src.attr("abs:src").contains("webm"))
                                && !src.attr("abs:src").contains("quantserve")
                                && !src.attr("abs:src").contains("facebook.com")
                                && !src.attr("abs:src").contains("cloudfront.net")) {
                            print("\n SELECTED")
                            FileUtils.copyURLToFile(URL(src.attr("abs:src")), File(f + System.getProperty("file.separator") + src.attr("abs:src").substring(src.attr("abs:src").lastIndexOf('/') + 1, src.attr("abs:src").length)))
                        }
                    }
                }
            } catch (e: Exception) {
                print("\n" + e.message)
                print("\n" + s + " 404ed :(")
            }
        } else {
            print("\nALREADY PARSED - SKIPPING")
        }
    }
}