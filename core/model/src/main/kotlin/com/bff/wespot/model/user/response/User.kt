package com.bff.wespot.model.user.response

data class User(
    val id: Int,
    val name: String,
    val grade: Int,
    val classNumber: Int,
    val schoolId: Int,
    val schoolName: String,
    val profileCharacter: ProfileCharacter,
) {
    fun toSchoolInfo() = "$schoolName ${grade}학년 ${classNumber}반"

    fun toDescription() = "${schoolName.removeSuffix("학교")} ${grade}학년 ${classNumber}반 $name"
}
