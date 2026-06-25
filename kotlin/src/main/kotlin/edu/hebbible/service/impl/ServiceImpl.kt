package edu.hebbible.service.impl

import edu.hebbible.model.Pasuk
import edu.hebbible.repository.Repo
import edu.hebbible.service.Svc
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ServiceImpl : Svc {

    @Autowired
    protected lateinit var repo: Repo

    override fun psukim(name: String, containsName: Boolean, withDups: Boolean): List<Pasuk> {
        initRepo()
        val result = mutableListOf<Pasuk>()
        val psukimList = repo.store
        for (pasuk in psukimList) {
            val line = pasuk.text
            if ((line.first() == name.first() && line.last() == name.last()) || (containsName && line.contains(name))) {
                if (result.isEmpty() || (withDups || result.last().text != pasuk.text)) {
                    log.debug("${pasuk.book} ${pasuk.perek}-${pasuk.pasuk} -- $line")
                    result.add(Pasuk(pasuk.book, pasuk.bookNo, pasuk.perek, pasuk.pasuk, noName(pasuk.text), pasuk.cntLetter))
                }
            }
        }
        log.info("${result.size} verses total")
        return result
    }

    override fun repoSize(): Int {
        initRepo()
        return repo.totalVersesCount
    }

    private fun initRepo() {
        repo.init()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ServiceImpl::class.java)

        private fun noName(orig: String): String {
            return orig.replace("יהוה", "ה'")
        }

    }
}
