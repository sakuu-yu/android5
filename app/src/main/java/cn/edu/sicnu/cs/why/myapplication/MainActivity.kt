package cn.edu.sicnu.cs.why.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

        findViewById<View>(R.id.btnEditProfile).setOnClickListener {
            startActivity(Intent(this, PersonInfoActivity::class.java))
        }
        findViewById<View>(R.id.btnPlayGame).setOnClickListener {
            startActivity(Intent(this, CardGameActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                showAboutDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.about_title)
            .setMessage(R.string.about_message)
            .setPositiveButton(R.string.btn_ok, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        renderProfile()
    }

    private fun renderProfile() {
        val profile = ProfileStore.load(this)

        setValue(R.id.tvName, profile.name)
        setValue(R.id.tvGender, profile.gender)
        setValue(R.id.tvStudentId, profile.studentId)
        setValue(R.id.tvPhone, profile.phone)
        setValue(R.id.tvEmail, profile.email)
        findViewById<TextView>(R.id.tvHobbies).text =
            if (profile.hobbies.isEmpty()) {
                getString(R.string.value_not_selected)
            } else {
                profile.hobbies.joinToString(getString(R.string.hobby_separator))
            }
        findViewById<TextView>(R.id.tvBestScore).text =
            ProfileStore.bestScore(this).toString()

        val avatar = AvatarStore.load(this)
        val imageView = findViewById<ImageView>(R.id.ivAvatar)
        val placeholder = findViewById<View>(R.id.avatarPlaceholder)
        if (avatar != null) {
            imageView.setImageBitmap(avatar)
            imageView.visibility = View.VISIBLE
            placeholder.visibility = View.GONE
        } else {
            imageView.setImageDrawable(null)
            imageView.visibility = View.GONE
            placeholder.visibility = View.VISIBLE
        }
    }

    private fun setValue(viewId: Int, value: String) {
        findViewById<TextView>(viewId).text =
            value.ifBlank { getString(R.string.value_not_filled) }
    }
}
