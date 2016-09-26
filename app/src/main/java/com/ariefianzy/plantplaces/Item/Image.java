package com.ariefianzy.plantplaces.Item;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Image")
public class Image extends ParseObject {

	public Image() {
		// A default constructor is required.
	}

	public String getLatitude() {
		return getString("latitude");
	}

	public void setLatitude(String latitude) {
		put("latitude", latitude);
	}
	public void setCategory(String category) {
		put("category", category);
	}

	public String getLongitude() {
		return getString("longitude");
	}

	public void setLongitude(String longitude) {
		put("longitude", longitude);
	}

	public ParseUser getAuthor() {
		return getParseUser("author");
	}

	public void setAuthor(ParseUser user) {
		put("author", user);
	}

	public ParseFile getPhotoFile() {
		return getParseFile("photo");
	}

	public void setPhotoFile(ParseFile file) {
		put("photo", file);
	}

}
