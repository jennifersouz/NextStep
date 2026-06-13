package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.InstitutionArchiveUpdateDto
import com.example.nextstep.data.model.InstitutionStudentDto
import com.example.nextstep.data.model.InstitutionTeacherDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import java.time.Instant

class InstitutionRepository {

    private val supabase = SupabaseClientProvider.client

    enum class ArchiveFilter {
        ACTIVE, ARCHIVED, ALL
    }

    suspend fun getInstitutionTeachers(filter: ArchiveFilter = ArchiveFilter.ACTIVE): Result<List<InstitutionTeacherDto>> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val allTeachers = supabase
                .from("teachers")
                .select {
                    filter {
                        eq("institution_profile_id", currentUser.id)
                    }
                }
                .decodeList<InstitutionTeacherDto>()

            val filteredTeachers = when (filter) {
                ArchiveFilter.ACTIVE -> allTeachers.filter { it.institutionArchivedAt == null }
                ArchiveFilter.ARCHIVED -> allTeachers.filter { it.institutionArchivedAt != null }
                ArchiveFilter.ALL -> allTeachers
            }

            Log.d("InstitutionRepository", "teachersCount=${filteredTeachers.size} filter=$filter")

            Result.success(filteredTeachers)
        } catch (exception: Exception) {
            Log.e("InstitutionRepository", "Erro ao carregar docentes", exception)
            Result.failure(exception)
        }
    }

    suspend fun getInstitutionTeacherDetail(teacherProfileId: String): Result<InstitutionTeacherDto> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val teacher = supabase
                .from("teachers")
                .select {
                    filter {
                        eq("profile_id", teacherProfileId)
                        eq("institution_profile_id", currentUser.id)
                    }
                    single()
                }
                .decodeAs<InstitutionTeacherDto>()

            Result.success(teacher)
        } catch (exception: Exception) {
            Log.e("InstitutionRepository", "Erro ao carregar detalhe do docente", exception)
            Result.failure(exception)
        }
    }

    suspend fun getInstitutionStudents(filter: ArchiveFilter = ArchiveFilter.ACTIVE): Result<List<InstitutionStudentDto>> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val allStudents = supabase
                .from("students")
                .select {
                    filter {
                        eq("institution_profile_id", currentUser.id)
                    }
                }
                .decodeList<InstitutionStudentDto>()

            val filteredStudents = when (filter) {
                ArchiveFilter.ACTIVE -> allStudents.filter { it.institutionArchivedAt == null }
                ArchiveFilter.ARCHIVED -> allStudents.filter { it.institutionArchivedAt != null }
                ArchiveFilter.ALL -> allStudents
            }

            Log.d("InstitutionRepository", "studentsCount=${filteredStudents.size} filter=$filter")

            Result.success(filteredStudents)
        } catch (exception: Exception) {
            Log.e("InstitutionRepository", "Erro ao carregar alunos", exception)
            Result.failure(exception)
        }
    }

    suspend fun getInstitutionStudentDetail(studentProfileId: String): Result<InstitutionStudentDto> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val student = supabase
                .from("students")
                .select {
                    filter {
                        eq("profile_id", studentProfileId)
                        eq("institution_profile_id", currentUser.id)
                    }
                    single()
                }
                .decodeAs<InstitutionStudentDto>()

            Result.success(student)
        } catch (exception: Exception) {
            Log.e("InstitutionRepository", "Erro ao carregar detalhe do aluno", exception)
            Result.failure(exception)
        }
    }

    suspend fun archiveTeacher(teacherProfileId: String, reason: String?): Result<InstitutionTeacherDto> {
        Log.d("InstitutionRepo", "archiveTeacher teacherProfileId=$teacherProfileId")
        
        if (teacherProfileId.isBlank()) {
            return Result.failure(IllegalArgumentException("ID do docente inválido."))
        }
        
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador autenticado não encontrado."))
            
            val institutionId = currentUser.id
            if (institutionId.isBlank()) {
                return Result.failure(IllegalStateException("ID da instituição autenticada inválido."))
            }
            
            Log.d("InstitutionRepo", "archiveTeacher teacherProfileId=$teacherProfileId institutionId=$institutionId")
            
            // PASSO 1: Verificar se o docente existe e se pertence a esta instituição
            val teacherRow = try {
                supabase.from("teachers").select {
                    filter {
                        eq("profile_id", teacherProfileId)
                    }
                    single()
                }.decodeAs<InstitutionTeacherDto>()
            } catch (e: Exception) {
                Log.e("InstitutionRepo", "Docente não encontrado: teacherProfileId=$teacherProfileId", e)
                return Result.failure(Exception("Docente não encontrado."))
            }
            
            Log.d("InstitutionRepo", "teacher belongsToInstitution=${teacherRow.institutionProfileId == institutionId}, teacherInstitutionId=${teacherRow.institutionProfileId}")
            
            if (teacherRow.institutionProfileId == null || teacherRow.institutionProfileId != institutionId) {
                Log.e("InstitutionRepo", "Docente não pertence a esta instituição: teacherProfileId=$teacherProfileId, teacherInstId=${teacherRow.institutionProfileId}, authInstId=$institutionId")
                return Result.failure(Exception("Este docente não pertence a esta instituição."))
            }
            
            // PASSO 2: Fazer o update na tabela teachers
            val now = Instant.now().toString()
            
            val updateDto = InstitutionArchiveUpdateDto(
                institutionArchivedAt = now,
                institutionArchivedBy = institutionId.takeIf { it.isNotBlank() },
                institutionArchiveReason = reason?.takeIf { it.isNotBlank() }
            )

            supabase.from("teachers").update(updateDto) {
                filter {
                    eq("profile_id", teacherProfileId)
                    eq("institution_profile_id", institutionId)
                }
            }

            // PASSO 3: Confirmar com SELECT que institution_archived_at foi preenchido
            val confirmed = try {
                supabase.from("teachers").select {
                    filter {
                        eq("profile_id", teacherProfileId)
                        eq("institution_profile_id", institutionId)
                    }
                    single()
                }.decodeAs<InstitutionTeacherDto>()
            } catch (e: Exception) {
                Log.e("InstitutionRepository", "Docente não encontrado após update: teacherProfileId=$teacherProfileId", e)
                return Result.failure(Exception("Não foi possível confirmar o arquivamento."))
            }
            
            Log.d("InstitutionRepo", "After archive teacherProfileId=${confirmed.profileId}, archivedAt=${confirmed.institutionArchivedAt}")

            if (confirmed.institutionArchivedAt == null) {
                Log.e("InstitutionRepo", "archiveTeacher: institutionArchivedAt is null after update for teacherProfileId=$teacherProfileId, institutionId=$institutionId")
                return Result.failure(Exception("Não foi possível arquivar o docente."))
            }

            Result.success(confirmed)
        } catch (exception: Exception) {
            Log.e("InstitutionRepository", "Erro ao arquivar docente", exception)
            Result.failure(exception)
        }
    }

    suspend fun archiveStudent(studentProfileId: String, reason: String?): Result<Unit> {
        if (studentProfileId.isBlank()) return Result.failure(IllegalArgumentException("ID do aluno inválido."))

        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val updateDto = InstitutionArchiveUpdateDto(
                institutionArchivedAt = Instant.now().toString(),
                institutionArchivedBy = currentUser.id,
                institutionArchiveReason = if (reason.isNullOrBlank()) null else reason
            )

            supabase.from("students").update(updateDto) {
                filter {
                    eq("profile_id", studentProfileId)
                    eq("institution_profile_id", currentUser.id)
                }
            }

            // Confirm update
            val confirmed = supabase.from("students").select {
                filter {
                    eq("profile_id", studentProfileId)
                    eq("institution_profile_id", currentUser.id)
                }
                single()
            }.decodeAs<InstitutionStudentDto>()

            if (confirmed.institutionArchivedAt != null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Não foi possível confirmar o arquivamento do aluno."))
            }
        } catch (exception: Exception) {
            Log.e("InstitutionRepository", "Erro ao arquivar aluno", exception)
            Result.failure(exception)
        }
    }
}
