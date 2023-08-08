package com.dspark.smsassistance;

public class RequestRecyclerItem {
    private int id;
    private String title;
    private String contents;

    public RequestRecyclerItem(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public RequestRecyclerItem(int id, String title, String contents) {
        this.id = id;
        this.title = title;
        this.contents = contents;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }
}
