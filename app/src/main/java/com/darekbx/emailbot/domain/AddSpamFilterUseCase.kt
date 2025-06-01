package com.darekbx.emailbot.domain

import com.darekbx.emailbot.repository.database.dao.SpamDao
import com.darekbx.emailbot.repository.database.entities.SpamDto

class AddSpamFilterUseCase(private val spamDao: SpamDao) {

    suspend operator fun invoke(from: String?, subject: String?) {
        if (from.isNullOrBlank() && subject.isNullOrBlank()) {
            return
        }
        spamDao.insert(SpamDto(from = from, subject = subject))
    }
}
