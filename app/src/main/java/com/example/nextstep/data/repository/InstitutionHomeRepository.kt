package com.example.nextstep.data.repository

import com.example.nextstep.data.model.InstitutionProfileDto
import com.example.nextstep.data.model.InstitutionUserDto

class InstitutionHomeRepository {

    private val profileRepository = InstitutionProfileRepository()
    private val usersRepository = InstitutionUsersRepository()

    suspend fun getInstitutionProfile(): Result<InstitutionProfileDto> {
        return profileRepository.getInstitutionProfile()
    }

    suspend fun getInstitutionUsers(): Result<List<InstitutionUserDto>> {
        return usersRepository.getInstitutionUsers()
    }
}