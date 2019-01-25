package com.eventhorizonwebdesign.kgrabber.util.web

import org.jsoup.Jsoup

class PageParserThread constructor(private val s: String, private val f: String) : Thread() {
    var nextFound = false
    override fun run() {
        try {
            print("\nLINK CRAWLER $s")
            val doc = Jsoup.connect(s).ignoreContentType(true).get()
            val links = doc.select("a[href]")
            for (link in links) {
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
                    nextFound = true
                    PageParserThread(link.attr("abs:href"), f).start()
                }
            }
            print("\nPAGE PARSE COMPLETE")
            if (!nextFound){
                print("\nNO NEXT FOUND")
            }
        } catch (e: Exception) {
            print("\n" + e.message)
            print("\n$s 404ed :(")
        }
    }
}