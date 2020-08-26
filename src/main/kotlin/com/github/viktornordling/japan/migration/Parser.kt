package com.github.viktornordling.japan.migration

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.io.File

import java.io.FileInputStream


object Parser {

    private val katakanaToEnglishCountry = mapOf(
            "中国" to "China",
            "アフリカ" to "Africa",
            "なんきょくたいりく" to "Antarctica",
            "アジア" to "Asia",
            "オセアニア" to "Oceania",
            "ヨーロッパ" to "Europe",
            "きたアメリカ" to "North America",
            "みなみアメリカ" to "South America",
            "なんきょく" to "Antarctic / South Pole",
            "ほっきょく" to "Arctic / North Pole",
            "アフガニスタン" to "Afghanistan",
            "アルバニア" to "Albania",
            "アルジェリア" to "Algeria",
            "アメリカ" to "USA",
            "べいこく" to "USA",
            "英国" to "England",
            "北アメリカ" to "North America",
            "米国" to "USA",
            "アンゴラ" to "Angola",
            "アルゼンチン" to "Argentina",
            "オーストラリア" to "Australia",
            "ごうしゅう" to "Australia",
            "オーストリア" to "Austria",
            "バハマ" to "Bahamas",
            "バーレーン" to "Bahrain",
            "バングラデシュ" to "Bangladesh",
            "バルバドス" to "Barbados",
            "ベルギー" to "Belgium",
            "ベリーズ" to "Belize",
            "ブータン" to "Bhutan",
            "ボリビア" to "Bolivia",
            "ボスニヤ・" to "Bosnia and",
            "ヘルツェゴビナ" to "Herzegovina",
            "ボツワナ" to "Botswana",
            "ブラジル" to "Brazil",
            "ブルネイ" to "Brunei",
            "ブルガリア" to "Bulgaria",
            "カンボジア" to "Cambodia",
            "カメルーン" to "Cameroon",
            "カナダ" to "Canada",
            "ちゅうおうアフリカ" to "Central Africa",
            "チャド" to "Chad",
            "チリ" to "Chile",
            "ちゅうごく" to "China",
            "コロンビア" to "Colombia",
            "コンゴ" to "Congo",
            "コスタリカ" to "Costa Rica",
            "クロアチア" to "Croatia",
            "キューバ" to "Cuba",
            "キプロス" to "Cyprus",
            "チェコ" to "Czech",
            "デンマーク" to "Denmark",
            "ドミニカきょうわこく" to "Dominican Republic",
            "エクアドル" to "Ecuador",
            "エジプト" to "Egypt",
            "エルサルバドル" to "El Salvador",
            "せきどうギニア" to "Equatorial Guinea",
            "エストニア" to "Estonia",
            "エチオピア" to "Ethiopia",
            "フィジー" to "Fiji",
            "フィンランド" to "Finland",
            "フランス" to "France",
            "ガンビア" to "Gambia",
            "ドイツ" to "Germany",
            "ガーナ" to "Ghana",
            "えいこく" to "Great Britain",
            "イギリス" to "Great Britain",
            "ギリシャ" to "Greece",
            "グリーンランド" to "Greenland",
            "グレナダ" to "Grenada",
            "グアテマラ" to "Guatemala",
            "ギニア" to "Guinea",
            "ガイヤナ" to "Guyana",
            "ハイチ" to "Haiti",
            "オランダ" to "Holland / The Netherlands",
            "ホンジェラス" to "Honduras",
            "ほんこん" to "Hong Kong",
            "ハンガリー" to "Hungary",
            "アイスランド" to "Iceland",
            "インド" to "India",
            "インドネシア" to "Indonesia",
            "イラン" to "Iran",
            "イラク" to "Iraq",
            "アイルランド" to "Ireland",
            "イスラエル" to "Israel",
            "イタリア" to "Italy",
            "ジャマイカ" to "Jamaica",
            "にほん / にっぽん" to "Japan",
            "ヨルダン" to "Jordan",
            "ケニア" to "Kenya",
            "コソボ" to "Kosovo",
            "クウェート" to "Kuwait",
            "ラオス" to "Laos",
            "ラトビア" to "Latvia",
            "レバノン" to "Lebanon",
            "リベリア" to "Liberia",
            "リビア" to "Libya",
            "リトアニア" to "Lithuania",
            "ルクセンブルク" to "Luxembourg",
            "マカオ" to "Macau",
            "マダガスカル" to "Madagascar",
            "マラウィ" to "Malawi",
            "マレーシア" to "Malaysia",
            "マルタ" to "Malta",
            "モルジブ" to "Maudives",
            "モーリシャス" to "Mauritius",
            "メキシコ" to "Mexico",
            "モルドバ" to "Moldova",
            "モナコ" to "Monaco",
            "もうこ" to "Mongolia",
            "モンゴル" to "Mongolia",
            "モロッコ" to "Morocco",
            "モザンビーク" to "Mozambique",
            "ミャンマー" to "Myanmar",
            "ナミビア" to "Namibia",
            "ネパール" to "Nepal",
            "ニューギニア" to "New Guinea",
            "ニュージーランド" to "New Zealand",
            "ニカラグア" to "Nicaragua",
            "ナイジェリア" to "Nigeria",
            "きたちょうせん" to "North Korea",
            "ノルウェー" to "Norway",
            "オーマン" to "Oman",
            "パキスタン" to "Pakistan",
            "パレスチナ" to "Palestine",
            "パナマ" to "Panama",
            "パプアニューギニア" to "Papua new Guinea",
            "パラグアイ" to "Paraguay",
            "ペルー" to "Peru",
            "フィリピン" to "Philippines",
            "ポーランド" to "Poland",
            "ポルトガル" to "Portugal",
            "カタール" to "Qatar",
            "ルーマニア" to "Romania",
            "ロシア" to "Russia",
            "ルワンダ" to "Rwanda",
            "サウジアラビア" to "Saudi Arabia",
            "スコットランド" to "Scotland",
            "セネガル" to "Senegal",
            "セイシェル" to "Seychelles",
            "シンガポール" to "Singapore",
            "スロバキア" to "Slovakia",
            "スロベニア" to "Slovenia",
            "ソロモンしょとう" to "Soloman Islands",
            "ソマリア" to "Somalia",
            "みなみアフリカ" to "South Africa",
            "かんこく" to "South Korea",
            "スペイン" to "Spain",
            "スリランカ" to "Sri Lanka",
            "スーダン" to "Sudan",
            "スウェーデン" to "Sweden",
            "スイス" to "Switzerland",
            "シリア" to "Syria",
            "タヒチ" to "Tahiti",
            "たいわん" to "Taiwan",
            "タンザニア" to "Tanzania",
            "タイ" to "Thailand",
            "トリニダード・トバゴ" to "Trinidad and Tobago",
            "チュニジア" to "Tunisia",
            "トルコ" to "Turkey",
            "ウガンダ" to "Uganda",
            "ウクライナ" to "Ukraine",
            "アラブしゅちょうこくれんぽう" to "United Arab Emirates",
            "ウルグアイ" to "Uruguay",
            "バチカン" to "Vatican",
            "ベネズエラ" to "Venezuela",
            "ベトナム" to "Vietnam",
            "ウェールズ" to "Wales",
            "イエメン" to "Yemen",
            "ザイール" to "Zaire",
            "ザンビア" to "Zambia",
            "ジンバブエ" to "Zimbabwe"
    )

    data class Key(val country: String, val year: Int, val month: Int)

    fun run() {
        val data: MutableMap<Key, MutableMap<String, Int>> = mutableMapOf()
        val unknownCountryNames = mutableSetOf<String>()
        for (year in 2015..2020) {
            for (month in 1..12) {
                parseValue("immigrationSummary", year, month, 3, "totalImmigration", data, unknownCountryNames)
                parseValue("emigrationSummary", year, month, 3, "totalEmigration", data, unknownCountryNames)
            }
        }
//        println("Unknown country names: $unknownCountryNames")
        println("country_name,year,month,totalImmigration,totalEmigration")
        for (entry in data.entries) {
            val key = entry.key
            val value = entry.value
            println("${key.country},${key.year},${key.month},${value.getOrDefault("totalImmigration", 0)},${value.getOrDefault("totalEmigration", 0)}")
        }
    }

    private fun parseValue(filePrefix: String, year: Int, month: Int, colNumber: Int, valueName: String,
                           data: MutableMap<Key, MutableMap<String, Int>>, unknownCountryNames: MutableSet<String>) {
        val file = File("${filePrefix}_${year}_${month}.xlsx")
        if (!file.exists()) {
            return
        }
        val fileInput = FileInputStream(file)
        val workbook: Workbook = XSSFWorkbook(fileInput)
        val sheet = workbook.getSheetAt(0)

        for (rowNumber in 5..sheet.lastRowNum) {
            val row = sheet.getRow(rowNumber)
            val countryNameCell = row.getCell(1)
            val cell = row.getCell(colNumber)
            if (countryNameCell != null) {
                val katakanaName = countryNameCell.stringCellValue
                val key = Key(katakanaToEnglishCountry.getOrDefault(katakanaName, "unknown"), year, month)
                if (key.country == "unknown") {
                    unknownCountryNames.add(katakanaName)
                }
                if (cell == null) {
                    // Value is null, ignore.
                } else {
                    val value = cell.numericCellValue.toInt()
                    val existingData = data.getOrDefault(key, mutableMapOf())
                    existingData[valueName] = value
                    data[key] = existingData
                }
            }
        }
    }
}

fun main() {
    Parser.run()
}
