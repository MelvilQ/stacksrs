package de.melvil.stacksrs.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DownloadableDeckInfo {
    private String name;
    private String file;
    private String front;
    private String back;
    private String description;

    public DownloadableDeckInfo(JSONObject object) throws JSONException {
        this.name = object.getString("name");
        this.file = object.getString("file");
        this.front = object.getString("front");
        this.back = object.getString("back");
        this.description = object.getString("description");
    }

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }

    public String getFront() {
        return front;
    }

    public String getBack() {
        return back;
    }

    public String getDescription() {
        return description;
    }
}
