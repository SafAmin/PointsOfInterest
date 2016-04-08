package com.example.safaa.pointsofinterest;


public class PlaceData {
    private String ID;
    private String placeImage;
    private String placeName;
    private String placeAddress;
    private int checkFavorite=0;

    public PlaceData(String pID, String pName, String pAddress, String pImage) {
        this.ID = pID;
        this.placeAddress = pAddress;
        this.placeName = pName;
        this.placeImage = pImage;

    }

    public void setID(String pID)
    {
         ID = pID;
    }
    public void setImage(String pImage)
    {
         placeImage = pImage;
    }
    public void setPlaceName(String pName){
         placeName = pName;
    }
    public void setPlaceAddress(String pAddress){
         placeAddress = pAddress;
    }
    public void setMark(int pMark){
         checkFavorite = pMark;
    }

    public String getPlaceID()
    {
        return ID;
    }
    public String getPlaceImage()
    {
        return placeImage;
    }
    public String getPlaceName(){
        return placeName;
    }
    public String getPlaceAddress(){
        return placeAddress;
    }
    public int getMark(){
        return checkFavorite;
    }
}
