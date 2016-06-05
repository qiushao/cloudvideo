package com.morning.demo.bean;





public class ResultInfo {

	private String title;

    private String icon;

    private String posprofile;

    private String videoSource;

    private String description;

    private String actors;

    private String categories;

    private float similarity;

    private int isRecommend = 0;//IS_NOT_RECOMMEND

    public ResultInfo() {
	}

    public ResultInfo(String title) {
        this.title = title;
    }

    public ResultInfo(String title, String icon, String posprofile, String videoSource) {
		this();
		this.title = title;
		this.icon = icon;
		this.posprofile = posprofile;
		this.videoSource = videoSource;
	}

    public ResultInfo(String title, String icon, String posprofile, String videoSource, String description, String actors, String categories) {
        this.title = title;
        this.icon = icon;
        this.posprofile = posprofile;
        this.videoSource = videoSource;
        this.description = description;
        this.actors = actors;
        this.categories = categories;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPosprofile() {
        return posprofile;
    }

    public void setPosprofile(String posprofile) {
        this.posprofile = posprofile;
    }

    public String getVideoSource() {
        return videoSource;
    }

    public void setVideoSource(String videoSource) {
        this.videoSource = videoSource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

    public int getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(int isRecommend) {
        this.isRecommend = isRecommend;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ResultInfo) {
            return this.title.equals(((ResultInfo)o).title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.title.hashCode();
    }

    @Override
    public String toString() {
        return title;
    }
}
