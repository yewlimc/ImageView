package com.example.weblogx2;

import android.content.Context;

import androidx.test.espresso.ViewInteraction;
import androidx.test.platform.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.weblogx2.Activities.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class Logout_EspressoTest {
    @Rule
    public ActivityTestRule<HomeActivity> mLoginActivityTestRule = new ActivityTestRule<HomeActivity>(HomeActivity.class);


    @Test
    public void postGalleryImageTest() throws Exception {
        // Clicks the post button

        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();

        if (current == null)
        {

        }
        else
        {
            Thread.sleep(2500);
            // get context
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

            // open action bar
            openActionBarOverflowOrOptionsMenu(appContext);
            // openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
//            onView(withId(R.id.menu_logout)).perform(click());
            onView(withText("Logout"))
                    .perform(click());
        }
    }

}