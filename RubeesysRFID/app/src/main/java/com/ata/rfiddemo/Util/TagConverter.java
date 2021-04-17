package com.ata.rfiddemo.Util;

import org.epctagcoder.parse.SGTIN.ParseSGTIN;
import org.epctagcoder.result.SGTIN;

public class TagConverter {

    private static SGTIN tagToSgtin(String tag) {
        ParseSGTIN parseSGTIN = null;

        try {
            parseSGTIN = ParseSGTIN.Builder().withRFIDTag(tag).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (parseSGTIN == null) {
            return null;
        }

        return parseSGTIN.getSGTIN();
    }

    public static String tagToCompanyPrefix(String tag) {
        SGTIN sgtin = tagToSgtin(tag);

        if (sgtin == null) {
            return null;
        }

        return sgtin.getCompanyPrefix();
    }

    public static String tagToItemReference(String tag) {
        SGTIN sgtin = tagToSgtin(tag);

        if (sgtin == null) {
            return null;
        }

        return sgtin.getItemReference() + sgtin.getCheckDigit();
    }

    public static String tagToSerialNumber(String tag) {
        SGTIN sgtin     = tagToSgtin(tag);

        if (sgtin == null) {
            return null;
        }

        return sgtin.getSerial();
    }
}
