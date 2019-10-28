package org.umbrellahq.baseapp.activities


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.umbrellahq.baseapp.R

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testFabAddTaskExists() {
        onView(withId(R.id.fabAddTask)).check(matches(isDisplayed()))
    }

    @Test
    fun testFabAddTaskWorks() {
        onView(withId(R.id.fabAddTask)).perform(ViewActions.click())
        onView(withId(R.id.fabAddTask)).perform(ViewActions.click())
        onView(withId(R.id.fabAddTask)).perform(ViewActions.click())

        onView(withId(R.id.rvTasks)).check(matches(hasDescendant(withText("New Task 0"))))
    }
}
