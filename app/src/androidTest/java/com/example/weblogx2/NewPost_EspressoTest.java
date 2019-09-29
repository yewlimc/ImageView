package com.example.weblogx2;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.weblogx2.Activities.LoginActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class NewPost_EspressoTest {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    @Rule
    public GrantPermissionRule writeEXT_PermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Rule
    public GrantPermissionRule readEXT_PermissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Rule
    public GrantPermissionRule cameraPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);


    @Test
    public void postGalleryImageTest() throws Exception {
        // Clicks the post button
        onView(withId(R.id.home_postButton)).perform(click());

        onView(withId(R.id.newPost_galleryButton)).perform(click());

        onView(withId(R.id.newPostDesc)).perform(typeText("UI Test: Post Gallery Image"), closeSoftKeyboard());

        onView(withId(R.id.newPost_postButton)).perform(click());
    }

    @Test
    public void postCameraImageTest() throws Exception {
        // Clicks the post button
        onView(withId(R.id.home_postButton)).perform(click());

        onView(withId(R.id.newPost_cameraButton)).perform(click());
        onView(withId(R.id.newPostDesc)).perform(typeText("UI Test: Post Camera Image"), closeSoftKeyboard());

        // Check is imageview is empty
        onView(withId(R.id.newPost_postButton)).perform(click());
    }
}

