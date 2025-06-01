package com.darekbx.emailbot.domain

import com.darekbx.emailbot.repository.database.dao.SpamDao
import com.darekbx.emailbot.repository.database.entities.SpamDto

class FetchSpamFiltersUseCase(private val spamDao: SpamDao) {

    suspend operator fun invoke(): List<SpamDto> {
        return spamDao.getAll()
    }
}
