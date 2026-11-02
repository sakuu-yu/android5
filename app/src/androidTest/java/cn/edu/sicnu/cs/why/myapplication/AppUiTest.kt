package cn.edu.sicnu.cs.why.myapplication

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppUiTest {

    private val context: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun mainScreenShowsProfileFieldsAndActions() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.tvHeader)).check(matches(withText(R.string.app_name)))
            onView(withId(R.id.tvName)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGender)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStudentId)).check(matches(isDisplayed()))
            onView(withId(R.id.tvBestScore)).check(matches(isDisplayed()))
            onView(withId(R.id.btnEditProfile)).check(matches(withText(R.string.btn_edit_profile)))
            onView(withId(R.id.btnPlayGame)).check(matches(withText(R.string.btn_play_game)))
        }
    }

    @Test
    fun editButtonOpensPersonInfoScreen() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnEditProfile)).perform(click())

            onView(withId(R.id.etName)).check(matches(isDisplayed()))
            onView(withId(R.id.etStudentId)).check(matches(isDisplayed()))
            onView(withId(R.id.personInfoRoot)).perform(swipeUp())
            onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun savingProfileUpdatesMainScreen() {
        val name = "UI-Test-User"
        val studentId = "2023000001"

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnEditProfile)).perform(click())

            onView(withId(R.id.etName))
                .perform(replaceText(name), closeSoftKeyboard())
            onView(withId(R.id.etStudentId))
                .perform(replaceText(studentId), closeSoftKeyboard())
            onView(withId(R.id.personInfoRoot)).perform(swipeUp())
            onView(withId(R.id.btnSave)).perform(click())

            onView(withId(R.id.tvName)).check(matches(withText(name)))
            onView(withId(R.id.tvStudentId)).check(matches(withText(studentId)))
        }
    }

    @Test
    fun gameScreenHasSixteenCardsAndStartsAtZero() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnPlayGame)).perform(click())

            onView(withId(R.id.gridCards)).check(matches(withChildCount(16)))
            onView(withId(R.id.tvScore))
                .check(matches(withText(context.getString(R.string.score_format, 0))))
        }
    }

    @Test
    fun tappingCardChangesScoreAndResetRestoresZero() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnPlayGame)).perform(click())

            val firstCard = context.getString(R.string.card_description, 1)
            onView(withContentDescription(firstCard)).perform(click())
            onView(withId(R.id.tvScore))
                .check(matches(withText(context.getString(R.string.score_format, -1))))

            onView(withId(R.id.btnReset)).perform(click())
            onView(withId(R.id.tvScore))
                .check(matches(withText(context.getString(R.string.score_format, 0))))
        }
    }

    @Test
    fun exitButtonReturnsToMainScreen() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnPlayGame)).perform(click())
            onView(withId(R.id.btnExit)).perform(click())

            onView(withId(R.id.btnPlayGame)).check(matches(isDisplayed()))
        }
    }

    private fun withChildCount(expectedCount: Int): Matcher<View> =
        object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("with $expectedCount children")
            }

            override fun matchesSafely(view: View): Boolean =
                view is ViewGroup && view.childCount == expectedCount
        }
}
