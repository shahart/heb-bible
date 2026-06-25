package edu.hebbible.repository

import edu.hebbible.model.Pasuk
import jakarta.annotation.PostConstruct
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Properties
import java.util.zip.GZIPInputStream

@Repository
class Repo {

    private val verses: MutableList<Pasuk> = ArrayList()
    private val torTxt = StringBuilder()

    private var totalVerses = 0
    private var totalLetters = 1

    val totalVersesCount: Int
        get() = totalVerses

    val totalLettersCount: Int
        get() = totalLetters

    val torTxtContent: StringBuilder
        get() = torTxt

    val store: List<Pasuk>
        get() = verses.toList()

    @PostConstruct
    fun postConstruct() {
        init()
    }

    @Suppress("REC_CATCH_EXCEPTION")
    fun init() {
        if (verses.isNotEmpty()) return
        val ts = System.currentTimeMillis()
        try {
            URL("https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.gz").openStream().use { is2 ->
                val `is` = IOUtils.toBufferedInputStream(is2)
                BufferedReader(InputStreamReader(GZIPInputStream(`is`), StandardCharsets.UTF_8)).use { br ->
                    var content: String?
                    while (br.readLine().also { content = it } != null) {
                        val splits = content!!.split(",")
                        val addr = splits[0].split(":")
                        addVerse(
                            splits[1],
                            addr[0].toInt() - 1, addr[1].toInt(), addr[2].toInt()
                        )
                    }
                }
            }
            log.info("Used gzip")
        } catch (e: Exception) {
            log.warn("Unable to gUnzip >> $e")
        }
        // log.info("${torTxt.length} total Letters (no spaces)")
        log.info("${System.currentTimeMillis() - ts} mSec")
        log.info("$totalVerses psukim")
    }

    private fun addVerse(line: String, currBookIdx: Int, PPrk: Int, PPsk: Int) {
        val txt = line.trim().replace(" ", "")
        torTxt.append(suffix(txt))
        totalLetters += txt.length
        val pasuk = Pasuk(StringBuilder(bookHeb[currBookIdx]).reverse().toString(), currBookIdx + 1, PPrk, PPsk, line.trim(), totalLetters)
        verses.add(pasuk)
        ++totalVerses
    }

    companion object {
        private val log = LoggerFactory.getLogger(Repo::class.java)

        @JvmStatic
        val bookHeb = arrayOf(
            "תישארב", "תומש", "ארקיו", "רבדמב", "םירבד", "עשוהי", "םיטפוש",
            "א לאומש", "ב לאומש", "א םיכלמ", "ב םיכלמ", "היעשי", "הימרי",
            "לאקזחי", "עשוה", "לאוי", "סומע", "הידבוע", "הנוי", "הכימ",
            "םוחנ", "קוקבח", "הינפצ", "יגח", "הירכז", "יכאלמ", "םילהת",
            "ילשמ", "בויא", "םירישה ריש", "תור", "הכיא", "תלהק", "רתסא",
            "לאינד", "ארזע", "הימחנ", "א םימיה ירבד", "ב םימיה ירבד"
        )

        @JvmStatic
        fun getHebChar(s: StringBuilder, i: Int) {
            val res = if (i == 31) ' ' else ('א'.code + i).toChar()
            s.insert(0, res)
        }

        @JvmStatic
        fun suffix(txt: String): String {
            var result = txt
            result = result.replace("ך", "כ")
            result = result.replace("ם", "מ")
            result = result.replace("ן", "נ")
            result = result.replace("ף", "פ")
            result = result.replace("ץ", "צ")
            return result
        }
    }
}
