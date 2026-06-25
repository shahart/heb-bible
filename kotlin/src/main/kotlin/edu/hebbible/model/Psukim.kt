package edu.hebbible.model

class Psukim {
    var name: String? = null
    var date: String? = null
    var extra: String? = null
    var feature: String? = null

    override fun toString(): String {
        return "$name-$date-$feature"
    }
}
