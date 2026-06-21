package com.rhz2026bandplaner.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class FestivalBand(
    val id: String,
    val name: String,
    val stage: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isFavorite: Boolean = false,
) {
    val formattedTime: String
        get() = "${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
}

sealed class FavoriteTimelineItem {
    data class BandItem(val band: FestivalBand) : FavoriteTimelineItem()
    data class FreeTimeItem(val durationInMinutes: Long, val from: String, val to: String) : FavoriteTimelineItem()
}

val rockharz2026Bands = listOf(
    FestivalBand("1", "Heavysaurus", "Dark Stage", LocalDateTime.of(2026, 7, 1, 15, 30), LocalDateTime.of(2026, 7, 1, 16, 15)),
    FestivalBand("2", "Soulbound", "Rock Stage", LocalDateTime.of(2026, 7, 1, 16, 20), LocalDateTime.of(2026, 7, 1, 17, 5)),
    FestivalBand("3", "Harakiri For The Sky", "Dark Stage", LocalDateTime.of(2026, 7, 1, 17, 10), LocalDateTime.of(2026, 7, 1, 17, 55)),
    FestivalBand("4", "The Haunted", "Rock Stage", LocalDateTime.of(2026, 7, 1, 18, 0), LocalDateTime.of(2026, 7, 1, 18, 45)),
    FestivalBand("5", "Ensiferum", "Dark Stage", LocalDateTime.of(2026, 7, 1, 18, 50), LocalDateTime.of(2026, 7, 1, 19, 40)),
    FestivalBand("6", "Paradise Lost", "Rock Stage", LocalDateTime.of(2026, 7, 1, 19, 45), LocalDateTime.of(2026, 7, 1, 20, 45)),
    FestivalBand("7", "Black Label Society", "Dark Stage", LocalDateTime.of(2026, 7, 1, 20, 50), LocalDateTime.of(2026, 7, 1, 21, 55)),
    FestivalBand("8", "Helloween", "Rock Stage", LocalDateTime.of(2026, 7, 1, 22, 0), LocalDateTime.of(2026, 7, 2, 0, 0)),
    FestivalBand("9", "Steve 'N' Seagulls", "Dark Stage", LocalDateTime.of(2026, 7, 2, 0, 10), LocalDateTime.of(2026, 7, 2, 1, 10)),

    FestivalBand("10", "Final Cry", "Dark Stage", LocalDateTime.of(2026, 7, 2, 11, 50), LocalDateTime.of(2026, 7, 2, 12, 20)),
    FestivalBand("11", "Die Habenichtse", "Rock Stage", LocalDateTime.of(2026, 7, 2, 12, 25), LocalDateTime.of(2026, 7, 2, 13, 0)),
    FestivalBand("12", "Mittel Alta", "Dark Stage", LocalDateTime.of(2026, 7, 2, 13, 5), LocalDateTime.of(2026, 7, 2, 13, 45)),
    FestivalBand("13", "Hagane", "Rock Stage", LocalDateTime.of(2026, 7, 2, 13, 50), LocalDateTime.of(2026, 7, 2, 14, 25)),
    FestivalBand("14", "Stahlmann", "Dark Stage", LocalDateTime.of(2026, 7, 2, 14, 30), LocalDateTime.of(2026, 7, 2, 15, 10)),
    FestivalBand("15", "Sagenbringer", "Rock Stage", LocalDateTime.of(2026, 7, 2, 15, 15), LocalDateTime.of(2026, 7, 2, 16, 0)),
    FestivalBand("16", "Dogma", "Dark Stage", LocalDateTime.of(2026, 7, 2, 16, 5), LocalDateTime.of(2026, 7, 2, 16, 50)),
    FestivalBand("17", "Warmen", "Rock Stage", LocalDateTime.of(2026, 7, 2, 16, 55), LocalDateTime.of(2026, 7, 2, 17, 45)),
    FestivalBand("18", "Decapitated", "Dark Stage", LocalDateTime.of(2026, 7, 2, 17, 50), LocalDateTime.of(2026, 7, 2, 18, 40)),
    FestivalBand("19", "Betontod", "Rock Stage", LocalDateTime.of(2026, 7, 2, 18, 45), LocalDateTime.of(2026, 7, 2, 19, 35)),
    FestivalBand("20", "Agnostic Front", "Dark Stage", LocalDateTime.of(2026, 7, 2, 19, 40), LocalDateTime.of(2026, 7, 2, 20, 30)),
    FestivalBand("21", "Hämatom", "Rock Stage", LocalDateTime.of(2026, 7, 2, 20, 35), LocalDateTime.of(2026, 7, 2, 21, 35)),
    FestivalBand("22", "Avatar", "Dark Stage", LocalDateTime.of(2026, 7, 2, 21, 40), LocalDateTime.of(2026, 7, 2, 22, 40)),
    FestivalBand("23", "Alice Cooper", "Rock Stage", LocalDateTime.of(2026, 7, 2, 22, 45), LocalDateTime.of(2026, 7, 3, 0, 0)),
    FestivalBand("24", "Dominum", "Dark Stage", LocalDateTime.of(2026, 7, 3, 0, 5), LocalDateTime.of(2026, 7, 3, 1, 0)),

    FestivalBand("25", "Rodeo 5000", "Rock Stage", LocalDateTime.of(2026, 7, 3, 11, 20), LocalDateTime.of(2026, 7, 3, 11, 50)),
    FestivalBand("26", "Haggefugg", "Dark Stage", LocalDateTime.of(2026, 7, 3, 11, 55), LocalDateTime.of(2026, 7, 3, 12, 25)),
    FestivalBand("27", "Motorjesus", "Rock Stage", LocalDateTime.of(2026, 7, 3, 12, 30), LocalDateTime.of(2026, 7, 3, 13, 0)),
    FestivalBand("28", "Cypecore", "Dark Stage", LocalDateTime.of(2026, 7, 3, 13, 5), LocalDateTime.of(2026, 7, 3, 13, 40)),
    FestivalBand("29", "Hiraes", "Rock Stage", LocalDateTime.of(2026, 7, 3, 13, 45), LocalDateTime.of(2026, 7, 3, 14, 30)),
    FestivalBand("30", "Gothminister", "Dark Stage", LocalDateTime.of(2026, 7, 3, 14, 35), LocalDateTime.of(2026, 7, 3, 15, 20)),
    FestivalBand("31", "Rauhbein", "Rock Stage", LocalDateTime.of(2026, 7, 3, 15, 25), LocalDateTime.of(2026, 7, 3, 16, 10)),
    FestivalBand("32", "Walls Of Jericho", "Dark Stage", LocalDateTime.of(2026, 7, 3, 16, 15), LocalDateTime.of(2026, 7, 3, 17, 0)),
    FestivalBand("33", "Fiddler's Green", "Rock Stage", LocalDateTime.of(2026, 7, 3, 17, 5), LocalDateTime.of(2026, 7, 3, 17, 50)),
    FestivalBand("34", "Die Apokalyptischen Reiter", "Dark Stage", LocalDateTime.of(2026, 7, 3, 17, 55), LocalDateTime.of(2026, 7, 3, 18, 40)),
    FestivalBand("35", "Biohazard", "Rock Stage", LocalDateTime.of(2026, 7, 3, 18, 45), LocalDateTime.of(2026, 7, 3, 19, 30)),
    FestivalBand("36", "P.O.D.", "Dark Stage", LocalDateTime.of(2026, 7, 3, 19, 35), LocalDateTime.of(2026, 7, 3, 20, 35)),
    FestivalBand("37", "Subway To Sally", "Rock Stage", LocalDateTime.of(2026, 7, 3, 20, 40), LocalDateTime.of(2026, 7, 3, 21, 40)),
    FestivalBand("38", "Airbourne", "Dark Stage", LocalDateTime.of(2026, 7, 3, 21, 45), LocalDateTime.of(2026, 7, 3, 22, 45)),
    FestivalBand("39", "Kreator", "Rock Stage", LocalDateTime.of(2026, 7, 3, 22, 50), LocalDateTime.of(2026, 7, 4, 0, 20)),
    FestivalBand("56", "Saint City Orchestra", "Dark Stage", LocalDateTime.of(2026, 7, 4, 0, 30), LocalDateTime.of(2026, 7, 4, 1, 30)),

    FestivalBand("40", "Pinhead", "Rock Stage", LocalDateTime.of(2026, 7, 4, 11, 20), LocalDateTime.of(2026, 7, 4, 11, 50)),
    FestivalBand("41", "Drone", "Dark Stage", LocalDateTime.of(2026, 7, 4, 11, 55), LocalDateTime.of(2026, 7, 4, 12, 25)),
    FestivalBand("42", "Tailgunner", "Rock Stage", LocalDateTime.of(2026, 7, 4, 12, 30), LocalDateTime.of(2026, 7, 4, 13, 10)),
    FestivalBand("43", "Necrotted", "Dark Stage", LocalDateTime.of(2026, 7, 4, 13, 15), LocalDateTime.of(2026, 7, 4, 13, 55)),
    FestivalBand("44", "Tungsten", "Rock Stage", LocalDateTime.of(2026, 7, 4, 14, 0), LocalDateTime.of(2026, 7, 4, 14, 40)),
    FestivalBand("45", "Crypta", "Dark Stage", LocalDateTime.of(2026, 7, 4, 14, 45), LocalDateTime.of(2026, 7, 4, 15, 25)),
    FestivalBand("46", "Artillery", "Rock Stage", LocalDateTime.of(2026, 7, 4, 15, 30), LocalDateTime.of(2026, 7, 4, 16, 10)),
    FestivalBand("47", "Majestica", "Dark Stage", LocalDateTime.of(2026, 7, 4, 16, 15), LocalDateTime.of(2026, 7, 4, 17, 0)),
    FestivalBand("48", "Annisokay", "Rock Stage", LocalDateTime.of(2026, 7, 4, 17, 5), LocalDateTime.of(2026, 7, 4, 17, 50)),
    FestivalBand("49", "Finntroll", "Dark Stage", LocalDateTime.of(2026, 7, 4, 17, 55), LocalDateTime.of(2026, 7, 4, 18, 40)),
    FestivalBand("50", "Danko Jones", "Rock Stage", LocalDateTime.of(2026, 7, 4, 18, 45), LocalDateTime.of(2026, 7, 4, 19, 30)),
    FestivalBand("51", "Doro", "Dark Stage", LocalDateTime.of(2026, 7, 4, 19, 35), LocalDateTime.of(2026, 7, 4, 20, 35)),
    FestivalBand("52", "Knorkator", "Rock Stage", LocalDateTime.of(2026, 7, 4, 20, 40), LocalDateTime.of(2026, 7, 4, 21, 40)),
    FestivalBand("53", "Emperor", "Dark Stage", LocalDateTime.of(2026, 7, 4, 21, 45), LocalDateTime.of(2026, 7, 4, 22, 45)),
    FestivalBand("54", "Feuerschwanz", "Rock Stage", LocalDateTime.of(2026, 7, 4, 22, 45), LocalDateTime.of(2026, 7, 5, 0, 15)),
    FestivalBand("55", "Soen", "Dark Stage", LocalDateTime.of(2026, 7, 5, 0, 30), LocalDateTime.of(2026, 7, 5, 1, 30))
)
