package com.example.weblogx2.Models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PostTest {

    Post postTest;
    private String postKey = "test001";
    private String image = "contents://something/here";
    private String description = "test_description";
    private String userID = "androidTest";
    private String userImage = "image_link";
    private String userName = "androidTest_name";
    private String dateTime = "9/19/2019";
    private String location = "Singapore";


    @Before
    public void setUp() throws Exception {
        postTest = new Post();
    }

    @After
    public void tearDown() throws Exception {
        postTest = null;
    }

    @Test
    public void testConstructors() {
        assertNotNull("Could not create basic post object. ", postTest);
        Post post2 = new Post(image, description, userID, userImage, userName, dateTime, location);
        assertNotNull("Could not create complex post object. ", post2);

        assertEquals("Name is not set as expected. ", userName, post2.getUserName());
    }

    @Test
    public void getSetUserName() {
        postTest.setUserName(userName);
        assertEquals("Name is not set as expected. ", userName, postTest.getUserName());
    }

    @Test
    public void getSetPostKey() {
        postTest.setPostKey(postKey);
        assertEquals("Post Key is not set as expected. ", postKey, postTest.getPostKey());
    }

    @Test
    public void getSetImage() {
        postTest.setImage(image);
        assertEquals("Image is not set as expected. ", image, postTest.getImage());
    }

    @Test
    public void getSetDescription() {
        postTest.setDescription(description);
        assertEquals("Description is not set as expected. ", description, postTest.getDescription());
    }

    @Test
    public void getSetUserID() {
        postTest.setUserID(userID);
        assertEquals("User ID is not set as expected. ", userID, postTest.getUserID());
    }

    @Test
    public void getSetUserImage() {
        postTest.setUserImage(userImage);
        assertEquals("User Image is not set as expected. ", userImage, postTest.getUserImage());
    }

    @Test
    public void getSetDateTime() {
        postTest.setDateTime(dateTime);
        assertEquals("Dateime is not set as expected. ", dateTime, postTest.getDateTime());
    }

    @Test
    public void getSetLocation() {
        postTest.setLocation(location);
        assertEquals("Location is not set as expected. ", location, postTest.getLocation());
    }
}