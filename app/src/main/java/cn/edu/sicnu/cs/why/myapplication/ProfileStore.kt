package cn.edu.sicnu.cs.why.myapplication

import android.content.Context

data class Profile(
    val name: String = "",
    val studentId: String = "",
    val gender: String = "男",
    val phone: String = "",
    val email: String = "",
    val hobbies: List<String> = emptyList()
)

object ProfileStore {
    private const val PREFS = "profile_store"
    private const val KEY_NAME = "name"
    private const val KEY_STUDENT_ID = "student_id"
    private const val KEY_GENDER = "gender"
    private const val KEY_PHONE = "phone"
    private const val KEY_EMAIL = "email"
    private const val KEY_HOBBIES = "hobbies"
    private const val KEY_BEST_SCORE = "best_score"

    val ALL_HOBBIES = listOf("旅游", "阅读", "游戏", "购物")

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(context: Context): Profile {
        val p = prefs(context)
        val hobbies = p.getString(KEY_HOBBIES, "")
            .orEmpty()
            .split(HOBBY_SEPARATOR)
            .filter { it.isNotBlank() }
        return Profile(
            name = p.getString(KEY_NAME, "").orEmpty(),
            studentId = p.getString(KEY_STUDENT_ID, "").orEmpty(),
            gender = p.getString(KEY_GENDER, "男").orEmpty().ifBlank { "男" },
            phone = p.getString(KEY_PHONE, "").orEmpty(),
            email = p.getString(KEY_EMAIL, "").orEmpty(),
            hobbies = hobbies
        )
    }

    fun save(context: Context, profile: Profile) {
        prefs(context).edit()
            .putString(KEY_NAME, profile.name)
            .putString(KEY_STUDENT_ID, profile.studentId)
            .putString(KEY_GENDER, profile.gender)
            .putString(KEY_PHONE, profile.phone)
            .putString(KEY_EMAIL, profile.email)
            .putString(KEY_HOBBIES, profile.hobbies.joinToString(HOBBY_SEPARATOR))
            .apply()
    }

    fun bestScore(context: Context): Int = prefs(context).getInt(KEY_BEST_SCORE, 0)

    fun saveBestScore(context: Context, score: Int) {
        prefs(context).edit().putInt(KEY_BEST_SCORE, score).apply()
    }

    private const val HOBBY_SEPARATOR = "|"
}
