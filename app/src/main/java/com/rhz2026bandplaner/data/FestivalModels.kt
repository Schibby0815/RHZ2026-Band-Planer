package com.rhz2026bandplaner.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class EventType { BAND, SIGNING }

data class FestivalBand(
    val id: String,
    val name: String,
    val stage: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isFavorite: Boolean = false,
    val type: EventType = EventType.BAND,
    val countryEmoji: String = "",
    val countryName: String = "",
    val genre: String = "",
    val foundedYear: String = "",
) {
    val formattedTime: String
        get() = "${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
}

sealed class FavoriteTimelineItem {
    data class BandItem(val band: FestivalBand) : FavoriteTimelineItem()
    data class FreeTimeItem(val durationInMinutes: Long, val from: String, val to: String) : FavoriteTimelineItem()
    data class ConflictItem(val bands: List<FestivalBand>, val signings: List<FestivalBand>) : FavoriteTimelineItem()
}

val rockharz2026Bands = listOf(
    // WEDNESDAY
    FestivalBand("1", "Heavysaurus", "Dark Stage", LocalDateTime.of(2026, 7, 1, 15, 30), LocalDateTime.of(2026, 7, 1, 16, 15), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Heavy Metal (Dino-Rock)", foundedYear = "2017"),
    FestivalBand("2", "Soulbound", "Rock Stage", LocalDateTime.of(2026, 7, 1, 16, 20), LocalDateTime.of(2026, 7, 1, 17, 5), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Industrial/Gothic Metal", foundedYear = "2009"),
    FestivalBand("3", "Harakiri For The Sky", "Dark Stage", LocalDateTime.of(2026, 7, 1, 17, 10), LocalDateTime.of(2026, 7, 1, 17, 55), countryEmoji = "🇦🇹", countryName = "Österreich", genre = "Post-Black Metal", foundedYear = "2011"),
    FestivalBand("4", "The Haunted", "Rock Stage", LocalDateTime.of(2026, 7, 1, 18, 0), LocalDateTime.of(2026, 7, 1, 18, 45), countryEmoji = "🇸🇪", countryName = "Schweden", genre = "Thrash/Melodic Death Metal", foundedYear = "1996"),
    FestivalBand("5", "Ensiferum", "Dark Stage", LocalDateTime.of(2026, 7, 1, 18, 50), LocalDateTime.of(2026, 7, 1, 19, 40), countryEmoji = "🇫🇮", countryName = "Finnland", genre = "Folk/Melodic Death Metal", foundedYear = "1995"),
    FestivalBand("6", "Paradise Lost", "Rock Stage", LocalDateTime.of(2026, 7, 1, 19, 45), LocalDateTime.of(2026, 7, 1, 20, 45), countryEmoji = "🇬🇧", countryName = "Großbritannien", genre = "Gothic/Doom Metal", foundedYear = "1988"),
    FestivalBand("7", "Black Label Society", "Dark Stage", LocalDateTime.of(2026, 7, 1, 20, 50), LocalDateTime.of(2026, 7, 1, 21, 55), countryEmoji = "🇺🇸", countryName = "USA", genre = "Heavy/Groove Metal", foundedYear = "1998"),
    FestivalBand("8", "Helloween", "Rock Stage", LocalDateTime.of(2026, 7, 1, 22, 0), LocalDateTime.of(2026, 7, 2, 0, 0), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Power Metal", foundedYear = "1984"),
    FestivalBand("9", "Steve 'N' Seagulls", "Dark Stage", LocalDateTime.of(2026, 7, 2, 0, 10), LocalDateTime.of(2026, 7, 2, 1, 10), countryEmoji = "🇫🇮", countryName = "Finnland", genre = "Country/Bluegrass Metal", foundedYear = "2011"),

    // THURSDAY
    FestivalBand("10", "Final Cry", "Dark Stage", LocalDateTime.of(2026, 7, 2, 11, 50), LocalDateTime.of(2026, 7, 2, 12, 20), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Melodic Death/Thrash", foundedYear = "1989"),
    FestivalBand("11", "Die Habenichtse", "Rock Stage", LocalDateTime.of(2026, 7, 2, 12, 25), LocalDateTime.of(2026, 7, 2, 13, 0), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Mittelalter-Folk Rock", foundedYear = "2013"),
    FestivalBand("12", "Mittel Alta", "Dark Stage", LocalDateTime.of(2026, 7, 2, 13, 5), LocalDateTime.of(2026, 7, 2, 13, 45), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Mittelalter-Rap / Rap-Rock", foundedYear = "2023"),
    FestivalBand("13", "Hagane", "Rock Stage", LocalDateTime.of(2026, 7, 2, 13, 50), LocalDateTime.of(2026, 7, 2, 14, 25), countryEmoji = "🇯🇵", countryName = "Japan", genre = "Power Metal", foundedYear = "2018"),
    FestivalBand("14", "Stahlmann", "Dark Stage", LocalDateTime.of(2026, 7, 2, 14, 30), LocalDateTime.of(2026, 7, 2, 15, 10), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Neue Deutsche Härte", foundedYear = "2008"),
    FestivalBand("15", "Sagenbringer", "Rock Stage", LocalDateTime.of(2026, 7, 2, 15, 15), LocalDateTime.of(2026, 7, 2, 16, 0), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Folk/Pagan Metal", foundedYear = "2021"),
    FestivalBand("16", "Dogma", "Dark Stage", LocalDateTime.of(2026, 7, 2, 16, 5), LocalDateTime.of(2026, 7, 2, 16, 50), countryEmoji = "🇮🇹", countryName = "Italien", genre = "Heavy Metal / Hard Rock", foundedYear = "2021"),
    FestivalBand("17", "Warmen", "Rock Stage", LocalDateTime.of(2026, 7, 2, 16, 55), LocalDateTime.of(2026, 7, 2, 17, 45), countryEmoji = "🇫🇮", countryName = "Finnland", genre = "Power/Melodic Death Metal", foundedYear = "2000"),
    FestivalBand("18", "Decapitated", "Dark Stage", LocalDateTime.of(2026, 7, 2, 17, 50), LocalDateTime.of(2026, 7, 2, 18, 40), countryEmoji = "🇵🇱", countryName = "Polen", genre = "Technical Death Metal", foundedYear = "1996"),
    FestivalBand("19", "Betontod", "Rock Stage", LocalDateTime.of(2026, 7, 2, 18, 45), LocalDateTime.of(2026, 7, 2, 19, 35), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Punk Rock", foundedYear = "1990"),
    FestivalBand("20", "Agnostic Front", "Dark Stage", LocalDateTime.of(2026, 7, 2, 19, 40), LocalDateTime.of(2026, 7, 2, 20, 30), countryEmoji = "🇺🇸", countryName = "USA", genre = "Hardcore Punk", foundedYear = "1980"),
    FestivalBand("21", "Hämatom", "Rock Stage", LocalDateTime.of(2026, 7, 2, 20, 35), LocalDateTime.of(2026, 7, 2, 21, 35), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Groove Metal / NDH", foundedYear = "2004"),
    FestivalBand("22", "Avatar", "Dark Stage", LocalDateTime.of(2026, 7, 2, 21, 40), LocalDateTime.of(2026, 7, 2, 22, 40), countryEmoji = "🇸🇪", countryName = "Schweden", genre = "Melodic Death/Alternative", foundedYear = "2001"),
    FestivalBand("23", "Alice Cooper", "Rock Stage", LocalDateTime.of(2026, 7, 2, 22, 45), LocalDateTime.of(2026, 7, 3, 0, 0), countryEmoji = "🇺🇸", countryName = "USA", genre = "Hard Rock / Shock Rock", foundedYear = "1964"),
    FestivalBand("24", "Dominum", "Dark Stage", LocalDateTime.of(2026, 7, 3, 0, 5), LocalDateTime.of(2026, 7, 3, 1, 0), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Power Metal", foundedYear = "2022"),

    // FRIDAY
    FestivalBand("25", "Rodeo 5000", "Rock Stage", LocalDateTime.of(2026, 7, 3, 11, 20), LocalDateTime.of(2026, 7, 3, 11, 50), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Country/Folk Rock", foundedYear = "2021"),
    FestivalBand("26", "Haggefugg", "Dark Stage", LocalDateTime.of(2026, 7, 3, 11, 55), LocalDateTime.of(2026, 7, 3, 12, 25), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Mittelalter-Rock", foundedYear = "2015"),
    FestivalBand("27", "Motorjesus", "Rock Stage", LocalDateTime.of(2026, 7, 3, 12, 30), LocalDateTime.of(2026, 7, 3, 13, 0), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Heavy Rock / Hard Rock", foundedYear = "1992"),
    FestivalBand("28", "Cypecore", "Dark Stage", LocalDateTime.of(2026, 7, 3, 13, 5), LocalDateTime.of(2026, 7, 3, 13, 40), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Cyber/Industrial Metal", foundedYear = "2007"),
    FestivalBand("29", "Hiraes", "Rock Stage", LocalDateTime.of(2026, 7, 3, 13, 45), LocalDateTime.of(2026, 7, 3, 14, 30), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Melodic Death Metal", foundedYear = "2020"),
    FestivalBand("30", "Gothminister", "Dark Stage", LocalDateTime.of(2026, 7, 3, 14, 35), LocalDateTime.of(2026, 7, 3, 15, 20), countryEmoji = "🇳🇴", countryName = "Norwegen", genre = "Gothic/Industrial Metal", foundedYear = "1999"),
    FestivalBand("31", "Rauhbein", "Rock Stage", LocalDateTime.of(2026, 7, 3, 15, 25), LocalDateTime.of(2026, 7, 3, 16, 10), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Deutschrock / Folk", foundedYear = "2021"),
    FestivalBand("32", "Walls Of Jericho", "Dark Stage", LocalDateTime.of(2026, 7, 3, 16, 15), LocalDateTime.of(2026, 7, 3, 17, 0), countryEmoji = "🇺🇸", countryName = "USA", genre = "Metalcore / Hardcore", foundedYear = "1998"),
    FestivalBand("33", "Fiddler's Green", "Rock Stage", LocalDateTime.of(2026, 7, 3, 17, 5), LocalDateTime.of(2026, 7, 3, 17, 50), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Irish Speed Folk", foundedYear = "1990"),
    FestivalBand("34", "Die Apokalyptischen Reiter", "Dark Stage", LocalDateTime.of(2026, 7, 3, 17, 55), LocalDateTime.of(2026, 7, 3, 18, 40), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Heavy/Folk/Thrash Metal", foundedYear = "1995"),
    FestivalBand("35", "Biohazard", "Rock Stage", LocalDateTime.of(2026, 7, 3, 18, 45), LocalDateTime.of(2026, 7, 3, 19, 30), countryEmoji = "🇺🇸", countryName = "USA", genre = "Hardcore / Alternative Metal", foundedYear = "1987"),
    FestivalBand("36", "P.O.D.", "Dark Stage", LocalDateTime.of(2026, 7, 3, 19, 35), LocalDateTime.of(2026, 7, 3, 20, 35), countryEmoji = "🇺🇸", countryName = "USA", genre = "Nu Metal / Alt Rock", foundedYear = "1992"),
    FestivalBand("37", "Subway To Sally", "Rock Stage", LocalDateTime.of(2026, 7, 3, 20, 40), LocalDateTime.of(2026, 7, 3, 21, 40), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Mittelalter-Rock / Folk", foundedYear = "1992"),
    FestivalBand("38", "Airbourne", "Dark Stage", LocalDateTime.of(2026, 7, 3, 21, 45), LocalDateTime.of(2026, 7, 3, 22, 45), countryEmoji = "🇦🇺", countryName = "Australien", genre = "Hard Rock", foundedYear = "2003"),
    FestivalBand("39", "Kreator", "Rock Stage", LocalDateTime.of(2026, 7, 3, 22, 50), LocalDateTime.of(2026, 7, 4, 0, 20), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Thrash Metal", foundedYear = "1982"),
    FestivalBand("56", "Saint City Orchestra", "Dark Stage", LocalDateTime.of(2026, 7, 4, 0, 30), LocalDateTime.of(2026, 7, 4, 1, 30), countryEmoji = "🇨🇭", countryName = "Schweiz", genre = "Irish Folk Punk", foundedYear = "2013"),

    // SATURDAY
    FestivalBand("40", "Pinhead", "Rock Stage", LocalDateTime.of(2026, 7, 4, 11, 20), LocalDateTime.of(2026, 7, 4, 11, 50), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Progressive / Modern Metal", foundedYear = "2023"),
    FestivalBand("41", "Drone", "Dark Stage", LocalDateTime.of(2026, 7, 4, 11, 55), LocalDateTime.of(2026, 7, 4, 12, 25), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Thrash/Groove Metal", foundedYear = "2004"),
    FestivalBand("42", "Tailgunner", "Rock Stage", LocalDateTime.of(2026, 7, 4, 12, 30), LocalDateTime.of(2026, 7, 4, 13, 10), countryEmoji = "🇬🇧", countryName = "Großbritannien", genre = "Heavy Metal (NWOTHM)", foundedYear = "2022"),
    FestivalBand("43", "Necrotted", "Dark Stage", LocalDateTime.of(2026, 7, 4, 13, 15), LocalDateTime.of(2026, 7, 4, 13, 55), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Death Metal / Deathcore", foundedYear = "2008"),
    FestivalBand("44", "Tungsten", "Rock Stage", LocalDateTime.of(2026, 7, 4, 14, 0), LocalDateTime.of(2026, 7, 4, 14, 40), countryEmoji = "🇸🇪", countryName = "Schweden", genre = "Symphonic Power Metal", foundedYear = "2016"),
    FestivalBand("45", "Crypta", "Dark Stage", LocalDateTime.of(2026, 7, 4, 14, 45), LocalDateTime.of(2026, 7, 4, 15, 25), countryEmoji = "🇧🇷", countryName = "Brasilien", genre = "Death Metal", foundedYear = "2019"),
    FestivalBand("46", "Artillery", "Rock Stage", LocalDateTime.of(2026, 7, 4, 15, 30), LocalDateTime.of(2026, 7, 4, 16, 10), countryEmoji = "🇩🇰", countryName = "Dänemark", genre = "Thrash Metal", foundedYear = "1982"),
    FestivalBand("47", "Majestica", "Dark Stage", LocalDateTime.of(2026, 7, 4, 16, 15), LocalDateTime.of(2026, 7, 4, 17, 0), countryEmoji = "🇸🇪", countryName = "Schweden", genre = "Symphonic Power Metal", foundedYear = "2019"),
    FestivalBand("48", "Annisokay", "Rock Stage", LocalDateTime.of(2026, 7, 4, 17, 5), LocalDateTime.of(2026, 7, 4, 17, 50), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Post-Hardcore / Metalcore", foundedYear = "2007"),
    FestivalBand("49", "Finntroll", "Dark Stage", LocalDateTime.of(2026, 7, 4, 17, 55), LocalDateTime.of(2026, 7, 4, 18, 40), countryEmoji = "🇫🇮", countryName = "Finnland", genre = "Folk/Black Metal", foundedYear = "1997"),
    FestivalBand("50", "Danko Jones", "Rock Stage", LocalDateTime.of(2026, 7, 4, 18, 45), LocalDateTime.of(2026, 7, 4, 19, 30), countryEmoji = "🇨🇦", countryName = "Kanada", genre = "Hard Rock", foundedYear = "1996"),
    FestivalBand("51", "Doro", "Dark Stage", LocalDateTime.of(2026, 7, 4, 19, 35), LocalDateTime.of(2026, 7, 4, 20, 35), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Heavy Metal", foundedYear = "1989"),
    FestivalBand("52", "Knorkator", "Rock Stage", LocalDateTime.of(2026, 7, 4, 20, 40), LocalDateTime.of(2026, 7, 4, 21, 40), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Comedy Metal / Crossover", foundedYear = "1994"),
    FestivalBand("53", "Emperor", "Dark Stage", LocalDateTime.of(2026, 7, 4, 21, 45), LocalDateTime.of(2026, 7, 4, 22, 45), countryEmoji = "🇳🇴", countryName = "Norwegen", genre = "Symphonic Black Metal", foundedYear = "1991"),
    FestivalBand("54", "Feuerschwanz", "Rock Stage", LocalDateTime.of(2026, 7, 4, 22, 45), LocalDateTime.of(2026, 7, 5, 0, 15), countryEmoji = "🇩🇪", countryName = "Deutschland", genre = "Folk Metal / Medi Rock", foundedYear = "2004"),
    FestivalBand("55", "Soen", "Dark Stage", LocalDateTime.of(2026, 7, 5, 0, 30), LocalDateTime.of(2026, 7, 5, 1, 30), countryEmoji = "🇸🇪", countryName = "Schweden", genre = "Progressive Metal", foundedYear = "2004"),

    // Autogrammstunden (Signing Sessions) - Wednesday
    FestivalBand("sig_1", "Grabenschlampen (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 1, 15, 30), LocalDateTime.of(2026, 7, 1, 16, 0), type = EventType.SIGNING),
    FestivalBand("sig_2", "Heavysaurus (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 1, 17, 0), LocalDateTime.of(2026, 7, 1, 17, 30), type = EventType.SIGNING),
    FestivalBand("sig_3", "Soulbound (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 1, 18, 0), LocalDateTime.of(2026, 7, 1, 18, 30), type = EventType.SIGNING),
    FestivalBand("sig_4", "The Haunted (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 1, 21, 20), LocalDateTime.of(2026, 7, 1, 21, 50), type = EventType.SIGNING),
    FestivalBand("sig_5", "Steve 'N' Seagulls (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 1, 22, 0), LocalDateTime.of(2026, 7, 1, 22, 30), type = EventType.SIGNING),

    // Thursday
    FestivalBand("sig_6", "Final Cry (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 13, 0), LocalDateTime.of(2026, 7, 2, 13, 30), type = EventType.SIGNING),
    FestivalBand("sig_7", "Ernie Fleetenkieker (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 13, 30), LocalDateTime.of(2026, 7, 2, 14, 0), type = EventType.SIGNING),
    FestivalBand("sig_8", "Die Habenichtse (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 14, 0), LocalDateTime.of(2026, 7, 2, 14, 30), type = EventType.SIGNING),
    FestivalBand("sig_9", "Betontod (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 14, 30), LocalDateTime.of(2026, 7, 2, 15, 0), type = EventType.SIGNING),
    FestivalBand("sig_10", "Warmen (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 15, 0), LocalDateTime.of(2026, 7, 2, 15, 30), type = EventType.SIGNING),
    FestivalBand("sig_11", "Agnostic Front (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 15, 30), LocalDateTime.of(2026, 7, 2, 16, 0), type = EventType.SIGNING),
    FestivalBand("sig_12", "Hagane (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 16, 0), LocalDateTime.of(2026, 7, 2, 16, 30), type = EventType.SIGNING),
    FestivalBand("sig_13", "Sagenbringer (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 16, 30), LocalDateTime.of(2026, 7, 2, 17, 0), type = EventType.SIGNING),
    FestivalBand("sig_14", "Mittel Alta (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 17, 0), LocalDateTime.of(2026, 7, 2, 17, 30), type = EventType.SIGNING),
    FestivalBand("sig_15", "Stahlmann (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 17, 30), LocalDateTime.of(2026, 7, 2, 18, 0), type = EventType.SIGNING),
    FestivalBand("sig_16", "Hämatom (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 18, 0), LocalDateTime.of(2026, 7, 2, 18, 30), type = EventType.SIGNING),
    FestivalBand("sig_17", "Dominum (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 2, 20, 0), LocalDateTime.of(2026, 7, 2, 20, 30), type = EventType.SIGNING),

    // Friday
    FestivalBand("sig_18", "Cypecore (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 11, 30), LocalDateTime.of(2026, 7, 3, 12, 0), type = EventType.SIGNING),
    FestivalBand("sig_19", "Gothminister (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 12, 15), LocalDateTime.of(2026, 7, 3, 12, 45), type = EventType.SIGNING),
    FestivalBand("sig_20", "Rodeo 5000 (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 13, 0), LocalDateTime.of(2026, 7, 3, 13, 30), type = EventType.SIGNING),
    FestivalBand("sig_21", "Motorjesus (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 13, 40), LocalDateTime.of(2026, 7, 3, 14, 10), type = EventType.SIGNING),
    FestivalBand("sig_22", "Fiddler's Green (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 14, 20), LocalDateTime.of(2026, 7, 3, 14, 50), type = EventType.SIGNING),
    FestivalBand("sig_23", "Hiraes (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 15, 0), LocalDateTime.of(2026, 7, 3, 15, 30), type = EventType.SIGNING),
    FestivalBand("sig_24", "HaggeFugg (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 15, 35), LocalDateTime.of(2026, 7, 3, 16, 5), type = EventType.SIGNING),
    FestivalBand("sig_25", "P.O.D. (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 16, 10), LocalDateTime.of(2026, 7, 3, 16, 40), type = EventType.SIGNING),
    FestivalBand("sig_26", "Rauhbein (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 17, 0), LocalDateTime.of(2026, 7, 3, 17, 30), type = EventType.SIGNING),
    FestivalBand("sig_27", "Subway To Sally (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 17, 40), LocalDateTime.of(2026, 7, 3, 18, 10), type = EventType.SIGNING),
    FestivalBand("sig_28", "Walls Of Jericho (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 19, 20), LocalDateTime.of(2026, 7, 3, 19, 50), type = EventType.SIGNING),
    FestivalBand("sig_29", "Die Apokalyptischen Reiter (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 3, 20, 15), LocalDateTime.of(2026, 7, 3, 20, 45), type = EventType.SIGNING),

    // Saturday
    FestivalBand("sig_30", "Tungsten (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 4, 12, 0), LocalDateTime.of(2026, 7, 4, 12, 30), type = EventType.SIGNING),
    FestivalBand("sig_31", "Pinhead (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 4, 12, 30), LocalDateTime.of(2026, 7, 4, 13, 0), type = EventType.SIGNING),
    FestivalBand("sig_32", "Annisokay (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 4, 13, 10), LocalDateTime.of(2026, 7, 4, 13, 40), type = EventType.SIGNING),
    FestivalBand("sig_33", "Artillery (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 4, 13, 45), LocalDateTime.of(2026, 7, 4, 14, 15), type = EventType.SIGNING),
    FestivalBand("sig_34", "Necrotted (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 4, 15, 0), LocalDateTime.of(2026, 7, 4, 15, 30), type = EventType.SIGNING),
    FestivalBand("sig_35", "Crypta (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 4, 16, 25), LocalDateTime.of(2026, 7, 4, 16, 55), type = EventType.SIGNING),
    FestivalBand("sig_36", "Knorkator (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 4, 18, 0), LocalDateTime.of(2026, 7, 4, 18, 30), type = EventType.SIGNING),
    FestivalBand("sig_37", "Feuerschwanz (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 4, 18, 50), LocalDateTime.of(2026, 7, 4, 19, 20), type = EventType.SIGNING),
    FestivalBand("sig_38", "Drone (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 4, 20, 0), LocalDateTime.of(2026, 7, 4, 20, 30), type = EventType.SIGNING),
    FestivalBand("sig_39", "Finntroll (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 4, 20, 30), LocalDateTime.of(2026, 7, 4, 21, 0), type = EventType.SIGNING),
    FestivalBand("sig_40", "Danko Jones (Signing)", "Signing Area", LocalDateTime.of(2026, 7, 4, 21, 0), LocalDateTime.of(2026, 7, 4, 21, 30), type = EventType.SIGNING)
)
