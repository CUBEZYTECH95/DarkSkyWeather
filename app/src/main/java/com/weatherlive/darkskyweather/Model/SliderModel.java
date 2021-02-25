package com.weatherlive.darkskyweather.Model;

import com.google.gson.annotations.SerializedName;

public class SliderModel{

	@SerializedName("download")
	public String download;

	@SerializedName("rating")
	public String rating;

	@SerializedName("description")
	public String description;

	@SerializedName("id")
	public int id;

	@SerializedName("title")
	public String title;

	@SerializedName("url")
	public String url;

	@SerializedName("imageuri")
	public String imageuri;


	public String getDownload() {
		return download;
	}

	public void setDownload(String download) {
		this.download = download;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImageuri() {
		return imageuri;
	}

	public void setImageuri(String imageuri) {
		this.imageuri = imageuri;
	}
}