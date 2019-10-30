package org.umbrellahq.baseapp

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.umbrellahq.baseapp.activities.MainActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun findFabAddTask_shouldExist() {
        Espresso.onView(ViewMatchers.withId(R.id.fabAddTask)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickFabAddTask_threeTimes_shouldDisplayThreeTasks() {
        Espresso.onView(ViewMatchers.withId(R.id.fabAddTask)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.fabAddTask)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.fabAddTask)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.rvTasks)).check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText("New Task 2"))))
    }
}
