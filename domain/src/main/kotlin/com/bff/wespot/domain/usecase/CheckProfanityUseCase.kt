package com.bff.wespot.domain.usecase

import com.bff.wespot.domain.repository.CommonRepository
import com.bff.wespot.model.exception.NetworkException
import javax.inject.Inject

class CheckProfanityUseCase @Inject constructor(
    private val commonRepository: CommonRepository
) {
    suspend operator fun invoke(content: String): Boolean {
        val result = commonRepository.checkProfanity(content)

        return if (result.isSuccess) {
            false
        } else {
            val exception = result.exceptionOrNull()
            val networkException = exception as? NetworkException
            if (networkException != null && networkException.status == 400) {
                true
            } else {
                throw exception!!
            }
        }
    }
}