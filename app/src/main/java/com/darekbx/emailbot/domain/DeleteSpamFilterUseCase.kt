package com.darekbx.emailbot.domain

import com.darekbx.emailbot.repository.database.dao.SpamDao

class DeleteSpamFilterUseCase(private val spamDao: SpamDao) {

    suspend operator fun invoke(id: String) {
        spamDao.delete(id)
    }
}
