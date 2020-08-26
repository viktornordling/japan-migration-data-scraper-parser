package com.github.viktornordling.japan.migration

import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.File
import java.net.URL


object Downloader {

    private val monthCodes = mapOf(
            1 to "11010301",
            2 to "11010302",
            3 to "11010303",
            4 to "12040604",
            5 to "12040605",
            6 to "12040606",
            7 to "23070907",
            8 to "23070908",
            9 to "23070909",
            10 to "24101210",
            11 to "24101211",
            12 to "24101212"
    )

    fun run() {
        println("Starting download.")
        for (year in 2015..2020) {
            for (month in 1..12) {
                downloadFiles(year, month)
            }
        }
    }

    fun downloadFiles(year: Int, month: Int) {
        // Get the page with files for this month:
        println("Downloading file for year: $year, month: $month")
        val monthCode = monthCodes[month]
        val url = "https://www.e-stat.go.jp/stat-search/files?page=1&layout=datalist&toukei=00250011&tstat=000001012480&cycle=1&year=${year}0&month=${monthCode}&tclass1=000001012481"
        val doc: Document = Jsoup.connect(url).get()
        val files = mapOf(
                "immigrationSummary" to "港別　入国外国人の国籍・地域",
                "emigrationSummary" to "港別　出国外国人の国籍・地域",
                "immigrationDetails" to "国籍・地域別　入国外国人の在留資格",
                "migrationDetails" to "国籍・地域別　出国外国人の在留資格")
        for (file in files) {
            val descriptor = file.value
            val fileName = file.key
            val elements: Elements = doc.select("a:contains($descriptor)")
            if (elements.size == 1) {
                val element = elements[0]
                val excelLink = element.parent().parent().children().last().children().first().attr("href")
                val excelDownloadUrl = "https://www.e-stat.go.jp${excelLink}"
                FileUtils.copyURLToFile(URL(excelDownloadUrl), File("${fileName}_${year}_${month}.xls"), 15000, 15000);
                println(excelLink)
            } else {
                println("Didn't find exactly one match for $year, $month, $descriptor")
            }
        }
    }
}

fun main() {
    Downloader.run()
//    Downloader.downloadFiles(2019, 7)
}
