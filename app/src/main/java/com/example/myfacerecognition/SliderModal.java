package com.example.myfacerecognition;

public class SliderModal {

    private String title;
    private String heading;
    private String imgUrl;
    private int backgroundDrawable;

    public SliderModal() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public SliderModal(String title, String heading, String imgUrl, int backgroundDrawable) {
        this.title = title;
        this.heading = heading;
        this.imgUrl = imgUrl;
        this.backgroundDrawable = backgroundDrawable;
    }

    public int getBackgroundDrawable() {
        return backgroundDrawable;
    }

    public void setBackgroundDrawable(int backgroundDrawable) {
        this.backgroundDrawable = backgroundDrawable;
    }

}
