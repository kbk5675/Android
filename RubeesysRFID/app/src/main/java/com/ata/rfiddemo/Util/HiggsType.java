package com.ata.rfiddemo.Util;

public class HiggsType {

    public String[] arrayPc   = {"2800", "3000", "3800", "4000","4800","5000","5800" , "6000","6800", "7000",  "7800", "8000",  "8800",   "9000" };
    public String[] bits      = {"80bit", "96bit", "112bit" , "128bit", "144bit", "160bit", "176bit", "192bit", "208bit", "224bit", "240bit", "256bit", "272bit", "288bit"  };

    public  String[] chipId  =  {"3412",     "3414",     "3811",     "3821",    "3813"} ;
    public  String[] chipType = {  "H3",       "H4",      "HEC",       "H9",     "H10"} ;
    public  String[] chip     = {   "A",        "B",        "C",        "D",       "E"} ;
    public  String[] epcBits  = {  "96",      "128",      "128",       "96",      "96"} ;
    public  String[] utidBits = {  "64",      "128",       "48",       "48",      "48"} ;
    public  String[] userBits = { "512",      "128",      "128",      "688",      "32"} ;

    public  String[] anotherChipId =  {"1100",    "110C",     "1105",     "1130",     "1170",      "680A",    "6806",   "6906",   "6B06",    "6810"} ;
    public  String[] anotherChip =    {"M4"  ,      "M4",       "M4",       "M5",       "M6",        "XM",      "XL",     "XL",     "XL",      "X7"} ;

    // higgs-4 higgs-ec
    public int[] epcOffset =  {1,  1,  1,  1,  1} ;
    public int[] epcLength =  {7,  9,  9,  7,  7};

    public int[] tidOffset =  {0, 0, 0, 0, 0} ;
    public int[] tidLength =  {6, 6, 6, 6, 6};

    public int[] uTidOffset =  {8, 8, 12, 12, 12};
    public int[] uTidLength =  {24, 24, 24, 24, 24};

    public int[] userOffset =  {0,    0, 0,  0, 0} ;
    public int[] userLength =  {32,   8, 8, 40, 2};


    public String[] getChipId() {
        return chipId;
    }

    public void setChipId(String[] chipId) {
        this.chipId = chipId;
    }

    public String[] getChipType() {
        return chipType;
    }

    public void setChipType(String[] chipType) {
        this.chipType = chipType;
    }

    public int[] getEpcOffset() {
        return epcOffset;
    }

    public void setEpcOffset(int[] epcOffset) {
        this.epcOffset = epcOffset;
    }

    public int[] getEpcLength() {
        return epcLength;
    }

    public void setEpcLength(int[] epcLength) {
        this.epcLength = epcLength;
    }

    public int[] getTidOffset() {
        return tidOffset;
    }

    public void setTidOffset(int[] tidOffset) {
        this.tidOffset = tidOffset;
    }

    public int[] getTidLength() {
        return tidLength;
    }

    public void setTidLength(int[] tidLength) {
        this.tidLength = tidLength;
    }

    public int[] getuTidOffset() {
        return uTidOffset;
    }

    public void setuTidOffset(int[] uTidOffset) {
        this.uTidOffset = uTidOffset;
    }

    public int[] getuTidLength() {
        return uTidLength;
    }

    public void setuTidLength(int[] uTidLength) {
        this.uTidLength = uTidLength;
    }

    public int[] getUserOffset() {
        return userOffset;
    }

    public void setUserOffset(int[] userOffset) {
        this.userOffset = userOffset;
    }

    public int[] getUserLength() {
        return userLength;
    }

    public void setUserLength(int[] userLength) {
        this.userLength = userLength;
    }



}


