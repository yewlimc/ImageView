package com.example.weblogx2;

import android.Manifest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.weblogx2.Activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class PostCamera_EspressoTest {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    @Rule
    public GrantPermissionRule writeEXT_PermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Rule
    public GrantPermissionRule readEXT_PermissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Rule
    public GrantPermissionRule cameraPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

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
