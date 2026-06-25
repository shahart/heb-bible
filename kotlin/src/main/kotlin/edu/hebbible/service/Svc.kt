package edu.hebbible.service

import edu.hebbible.model.Pasuk

interface Svc {
    fun psukim(name: String, containsName: Boolean, withDups: Boolean): List<Pasuk>
    fun repoSize(): Int
}
