package org.example;

public class OrderTrackModel {
    private Integer track;

    public Integer getTrack() {
        return track;
    }

    public static OrderTrackModel fromOrderTrack(Integer track) {
        OrderTrackModel model = new OrderTrackModel();
        model.track = track;
        return model;
    }
}
