package com.ata.rfiddemo.Util.Service;


import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class BcdUtilHelper {
    private static HashMap<String, String> bcdCode = new HashMap<String, String>();
    private static HashMap<String, String> binCode = new HashMap<String, String>();
    private static HashMap<String, String> divCode = new HashMap<String, String>();

    static {
        bcdCode.put("0" , "001010" );
        bcdCode.put("1" , "000001" );
        bcdCode.put("2" , "000010" );
        bcdCode.put("3" , "000011" );
        bcdCode.put("4" , "000100" );
        bcdCode.put("5" , "000101" );
        bcdCode.put("6" , "000110" );
        bcdCode.put("7" , "000111" );
        bcdCode.put("8" , "001000" );
        bcdCode.put("9" , "001001" );
        bcdCode.put("A" , "010001" );
        bcdCode.put("B" , "010010" );
        bcdCode.put("C" , "010011" );
        bcdCode.put("D" , "010100" );
        bcdCode.put("E" , "010101" );
        bcdCode.put("F" , "010110" );
        bcdCode.put("G" , "010111" );
        bcdCode.put("H" , "011000" );
        bcdCode.put("I" , "011001" );
        bcdCode.put("J" , "100001" );
        bcdCode.put("K" , "100010" );
        bcdCode.put("L" , "100011" );
        bcdCode.put("M" , "100100" );
        bcdCode.put("N" , "100101" );
        bcdCode.put("O" , "100110" );
        bcdCode.put("P" , "100111" );
        bcdCode.put("Q" , "101000" );
        bcdCode.put("R" , "101001" );
        bcdCode.put("S" , "110010" );
        bcdCode.put("T" , "110011" );
        bcdCode.put("U" , "110100" );
        bcdCode.put("V" , "110101" );
        bcdCode.put("W" , "110110" );
        bcdCode.put("X" , "110111" );
        bcdCode.put("Y" , "111000" );
        bcdCode.put("Z" , "111001" );
        bcdCode.put(" " , "111010" );
        bcdCode.put("." , "111011" );
        bcdCode.put("-" , "111100" );
        bcdCode.put("/" , "111101" );

        binCode.put("0" , "00" );
        binCode.put("1" , "01" );
        binCode.put("2" , "10" );
        binCode.put("3" , "11" );

        divCode.put("0" , "0000" );
        divCode.put("1" , "0001" );
        divCode.put("2" , "0010" );
        divCode.put("3" , "0011" );
        divCode.put("4" , "0100" );
        divCode.put("5" , "0101" );
        divCode.put("6" , "0110" );
        divCode.put("7" , "0111" );
        divCode.put("8" , "1000" );
        divCode.put("9" , "1001" );
        divCode.put("A" , "1010" );
        divCode.put("B" , "1011" );
        divCode.put("C" , "1100" );
        divCode.put("D" , "1101" );
        divCode.put("E" , "1110" );
        divCode.put("F" , "1111" );

    };

    // BCD 코드 테이블에서 key 값으로 value 값 찾기
    public static String finderDivide(String key){
        if(binCode.containsKey(key))
        {
            return binCode.get(key).toString();
        }else {
            return "";
        }
    }

    // BCD 코드 테이블에서 key 값으로 value 값 찾기
    public static String finder(String key)
    {
        if(bcdCode.containsKey(key))
        {
            return bcdCode.get(key).toString();
        }else {
            return "";
        }

    }

    // BCD 코드 테이블에서 value 값으로 key 값 찾기
    public static Object keyFinder(String value) {

        for (Object o: bcdCode.keySet()){
            if(bcdCode.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    // string 을 BCD 코드로 변환
    public static String str2bcd(String str)
    {
        str = str.toUpperCase(); //대문자로 변환
        String result = "";
        String[] temp = str.split(""); // string 을 한글자 단위로 배열에 저장

        for(String s : temp){
            result = result + finder(s); // 배열을 루프 돌리면서 BCD 코드 값으로 bcd string 생성 (2진수)
        }
        return result;
    }

    // BCD string (2진수) 를 HEX 값으로 변환
    public static String bcd2hex(String str)
    {
        //string result = string.Join("", Enumerable.Range(0, str.Length / 8).Select(i => Convert.ToByte(str.Substring(i * 8, 8), 2).ToString("X2")));

        String result = "";

        for(int i=0; i<(str.length()/4) ; i++){
            int beginIndex = i * 4;
            int lastIndex  = beginIndex + 4;

            String token = str.substring(beginIndex, lastIndex);

            int decimal = Integer.parseInt(token,2);
            String hexStr = Integer.toString(decimal,16).toUpperCase();
            result += hexStr;
        }
        return result;
    }

    // string 을 HEX 값으로 변환
    public static String str2hex(String str) {
        String result = "";
        String temp = str2bcd(str); // string 을 BCD 코드 (2진수) 로 변환
        result = bcd2hex(temp); // 변환한 BCD 코드를 HEX 로 변환
        return result;
    }

    public static String int2hex(String intString) // 숫자를 HEX 값으로 변환 ( 숫자를 000001 포멧으로 표현해야 해서 string 사용)
    {
        int decimal = Integer.parseInt(intString);
        String hexStr = Integer.toString(decimal,16).toUpperCase();
        String result = String.format("%5s", hexStr).replaceAll(" ", "0");
        return result;
    }

    // HEX 값을 숫자로 변환
    public static String hex2int(String hexString){
        int result = Integer.parseInt(hexString, 16);
        return String.format("%06d", result); // 숫자를 000001 의 스트링 포멧으로 변환
    }

    // HEX 값을 string 으로 변환
    public static String hex2str(String hexValue) {

        String result = "";
        String[] temp = hexValue.replaceAll(".{2}", "$0 ").split(" "); // HEX 값을 2자리 단위로 분리 (FF FF FF 형태로 분리하고 구분자 ' ' 로 배열로 저장)

        for (String s : temp){
            for (int i = 0; i < s.length(); i++) {
                String sub = s.charAt(i) + "";
                int first = Integer.parseInt(sub, 16);
                String firstBinary = Integer.toBinaryString(first);
                String firstBinary1 = String.format("%4s", firstBinary).replaceAll(" ", "0");
                result += firstBinary1;
            }
        }
        String temp2 = result.replaceAll(".{6}", "$0 "); // 변환된 2진수 값을 6개 단위로 분리
        String[] temp3 = temp2.split(" "); // 분리된 2진수 값을 구분자 ' ' 로 배열로 저장
        String result2 = "";
        for (String s : temp3){
            result2 = result2 + keyFinder(s); // 배열을 루프 돌려서 BCD 코드 테이블에서 value 값으로 key 를 찾아서 string 로 변환
        }
        return result2;
    }


    public static String Tag2Epc(String encode)
    {
        String strDivide = finderDivide(encode.substring(0, 1));
        String strDivide2 = finderDivide(encode.substring(1, 2));
        String strLastDivide = Integer.toHexString(Integer.parseInt(strDivide + strDivide2, 2));

        String strStyle = encode.substring(2, 12);
        String strColor = encode.substring(12, 15);
        String strSize = encode.substring(15, 18);
        String strReorder = encode.substring(18, 20);
        String strSerial = encode.substring(20, 26);

        String strBarcode = strStyle + strColor + strSize + strReorder + strSerial;

        String beforeBCD = strStyle + strColor + strSize; // BCD 코드로 변환할 값 (코드, 컬러, 사이즈)

        String str2hex = str2hex(beforeBCD);
        String int2hex = int2hex(strSerial);
        String lastResult = strLastDivide + str2hex + strReorder + int2hex;

        return lastResult;
    }

    public static String Epc2Tag(String epc)
    {

        String epc_result = epc.replaceAll(" ", "");
        String token1 = "";
        String token2 = "";
        String token3 = "";
        String token4 = "";

        try {

            token1 = epc_result.substring(0, 1);
            token2 = epc_result.substring(1, 25);
            token3 = epc_result.substring(25, 27);
            token4 = epc_result.substring(27, 32);

        }catch (Exception ex){
            return null;
        }

        String token11 = Integer.toBinaryString(Integer.parseInt(token1, 16));
        String token111 = String.format("%4s", token11).replaceAll(" ", "0");
        String token111Left = String.valueOf(Integer.parseInt(token111.substring(0, 2), 2));
        String token111Right = String.valueOf(Integer.parseInt(token111.substring(2, 4), 2));

        String hex2Str = hex2str(token2);

        return token111Left + token111Right + hex2Str + token3 + hex2int(token4);
    }

    public static String getStyleFromTag(String tag)
    {
        if (tag == null){
            return null;
        }

        return tag.substring(2, 12);

    }

    public static String getColorFromTag(String tag)
    {
        if(tag == null){
            return null;
        }

        return tag.substring(12, 15);
    }

    public static String getSizeFromTag(String tag)
    {
        if(tag == null){
            return null;
        }

        return tag.substring(15, 18);
    }

    public static String getOrderDegreeFromTag(String tag)
    {
        if(tag == null){
            return null;
        }

        return tag.substring(18, 20);
    }

    public static String getSerialFromTag(String tag)
    {
        if(tag == null){
            return null;
        }

        return tag.substring(20);
    }

    public static String hexToString(String hex)
    {
        byte[] bytes = hexStringToByteArray(hex) ;
        String st = new String(bytes, StandardCharsets.UTF_8);

        return st;
    }

    public static byte[] hexStringToByteArray(String hex) {
        int l = hex.length();
        byte[] data = new byte[l/2];
        for (int i = 0; i < l; i += 2) {
            data[i/2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    public static String stringToHex(String s) {
        String result = "";

        for (int i = 0; i < s.length(); i++) {
            result += String.format("%02X", (int) s.charAt(i));
        }

        return result;
    }

}