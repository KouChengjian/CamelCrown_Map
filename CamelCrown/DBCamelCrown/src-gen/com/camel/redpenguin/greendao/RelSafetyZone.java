package com.camel.redpenguin.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table REL_SAFETY_ZONE.
 */
public class RelSafetyZone {

    private Long id;
    private String relZoneIdentify;
    private String relZoneName;
    private String relZoneAddr;
    private String relZoneLatitude;
    private String relZoneLongtitude;
    private String relZoneRadius;
    private String relZoneCreated;

    public RelSafetyZone() {
    }

    public RelSafetyZone(Long id) {
        this.id = id;
    }

    public RelSafetyZone(Long id, String relZoneIdentify, String relZoneName, String relZoneAddr, String relZoneLatitude, String relZoneLongtitude, String relZoneRadius, String relZoneCreated) {
        this.id = id;
        this.relZoneIdentify = relZoneIdentify;
        this.relZoneName = relZoneName;
        this.relZoneAddr = relZoneAddr;
        this.relZoneLatitude = relZoneLatitude;
        this.relZoneLongtitude = relZoneLongtitude;
        this.relZoneRadius = relZoneRadius;
        this.relZoneCreated = relZoneCreated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelZoneIdentify() {
        return relZoneIdentify;
    }

    public void setRelZoneIdentify(String relZoneIdentify) {
        this.relZoneIdentify = relZoneIdentify;
    }

    public String getRelZoneName() {
        return relZoneName;
    }

    public void setRelZoneName(String relZoneName) {
        this.relZoneName = relZoneName;
    }

    public String getRelZoneAddr() {
        return relZoneAddr;
    }

    public void setRelZoneAddr(String relZoneAddr) {
        this.relZoneAddr = relZoneAddr;
    }

    public String getRelZoneLatitude() {
        return relZoneLatitude;
    }

    public void setRelZoneLatitude(String relZoneLatitude) {
        this.relZoneLatitude = relZoneLatitude;
    }

    public String getRelZoneLongtitude() {
        return relZoneLongtitude;
    }

    public void setRelZoneLongtitude(String relZoneLongtitude) {
        this.relZoneLongtitude = relZoneLongtitude;
    }

    public String getRelZoneRadius() {
        return relZoneRadius;
    }

    public void setRelZoneRadius(String relZoneRadius) {
        this.relZoneRadius = relZoneRadius;
    }

    public String getRelZoneCreated() {
        return relZoneCreated;
    }

    public void setRelZoneCreated(String relZoneCreated) {
        this.relZoneCreated = relZoneCreated;
    }

}
