package cn.edu.sicnu.cs.why.myapplication

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class PersonInfoActivity : AppCompatActivity() {

    private lateinit var pendingAvatarFile: File
    private var pendingAvatarSelected = false
    private var saved = false

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            if (AvatarStore.copyFromUri(this, uri, pendingAvatarFile)) {
                pendingAvatarSelected = true
                showAvatar(AvatarStore.decodeFile(pendingAvatarFile))
            } else {
                Toast.makeText(this, R.string.toast_avatar_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_person_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.personInfoRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pendingAvatarFile = File(cacheDir, "pending_avatar.png")

        findViewById<ImageButton>(R.id.btnPickAvatar).setOnClickListener {
            pickImage.launch("image/*")
        }
        findViewById<View>(R.id.btnSave).setOnClickListener { saveProfile() }
        findViewById<View>(R.id.btnCancel).setOnClickListener { finish() }

        loadProfile()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!saved && pendingAvatarFile.exists()) {
            pendingAvatarFile.delete()
        }
    }

    private fun loadProfile() {
        val profile = ProfileStore.load(this)
        findViewById<EditText>(R.id.etName).setText(profile.name)
        findViewById<EditText>(R.id.etStudentId).setText(profile.studentId)
        findViewById<EditText>(R.id.etPhone).setText(profile.phone)
        findViewById<EditText>(R.id.etEmail).setText(profile.email)

        if (profile.gender == getString(R.string.gender_female)) {
            findViewById<RadioButton>(R.id.rbFemale).isChecked = true
        } else {
            findViewById<RadioButton>(R.id.rbMale).isChecked = true
        }

        findViewById<CheckBox>(R.id.cbTravel).isChecked =
            profile.hobbies.contains(getString(R.string.hobby_travel))
        findViewById<CheckBox>(R.id.cbReading).isChecked =
            profile.hobbies.contains(getString(R.string.hobby_reading))
        findViewById<CheckBox>(R.id.cbGame).isChecked =
            profile.hobbies.contains(getString(R.string.hobby_game))
        findViewById<CheckBox>(R.id.cbShopping).isChecked =
            profile.hobbies.contains(getString(R.string.hobby_shopping))

        showAvatar(AvatarStore.load(this))
    }

    private fun showAvatar(bitmap: android.graphics.Bitmap?) {
        val imageView = findViewById<ImageView>(R.id.ivAvatar)
        val placeholder = findViewById<View>(R.id.avatarPlaceholder)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
            imageView.visibility = View.VISIBLE
            placeholder.visibility = View.GONE
        } else {
            imageView.setImageDrawable(null)
            imageView.visibility = View.GONE
            placeholder.visibility = View.VISIBLE
        }
    }

    private fun saveProfile() {
        val hobbies = mutableListOf<String>()
        if (findViewById<CheckBox>(R.id.cbTravel).isChecked) {
            hobbies.add(getString(R.string.hobby_travel))
        }
        if (findViewById<CheckBox>(R.id.cbReading).isChecked) {
            hobbies.add(getString(R.string.hobby_reading))
        }
        if (findViewById<CheckBox>(R.id.cbGame).isChecked) {
            hobbies.add(getString(R.string.hobby_game))
        }
        if (findViewById<CheckBox>(R.id.cbShopping).isChecked) {
            hobbies.add(getString(R.string.hobby_shopping))
        }

        val gender = if (findViewById<RadioButton>(R.id.rbFemale).isChecked) {
            getString(R.string.gender_female)
        } else {
            getString(R.string.gender_male)
        }

        val profile = Profile(
            name = findViewById<EditText>(R.id.etName).text.toString().trim(),
            studentId = findViewById<EditText>(R.id.etStudentId).text.toString().trim(),
            gender = gender,
            phone = findViewById<EditText>(R.id.etPhone).text.toString().trim(),
            email = findViewById<EditText>(R.id.etEmail).text.toString().trim(),
            hobbies = hobbies
        )
        ProfileStore.save(this, profile)

        if (pendingAvatarSelected && pendingAvatarFile.exists()) {
            pendingAvatarFile.copyTo(AvatarStore.avatarFile(this), overwrite = true)
            pendingAvatarFile.delete()
        }

        saved = true
        Toast.makeText(this, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
        finish()
    }
}
