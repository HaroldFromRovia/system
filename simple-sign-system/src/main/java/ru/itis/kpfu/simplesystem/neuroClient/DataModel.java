package ru.itis.kpfu.simplesystem.neuroClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataModel {
    private String w11="0";
    private String w12="0";
    private String w21="0";
    private String w22="0";
    private String v11="0";
    private String v12="0";
    private String v13="0";
    private String v21="0";
    private String v22="0";
    private String v23="0";
    private String w1="0";
    private String w2="0";
    private String w3="0";
    private String e="0";
    private String publickey;

    // return as normalized JSON object
    public String toString() {
        return new StringBuilder().append("{")
                .append("\"w11\":\"").append(w11).append("\",")
                .append("\"w12\":\"").append(w12).append("\",")
                .append("\"w21\":\"").append(w21).append("\",")
                .append("\"w22\":\"").append(w22).append("\",")
                .append("\"v11\":\"").append(v11).append("\",")
                .append("\"v12\":\"").append(v12).append("\",")
                .append("\"v13\":\"").append(v13).append("\",")
                .append("\"v21\":\"").append(v21).append("\",")
                .append("\"v22\":\"").append(v22).append("\",")
                .append("\"v23\":\"").append(v23).append("\",")
                .append("\"w1\":\"").append(w1).append("\",")
                .append("\"w2\":\"").append(w2).append("\",")
                .append("\"w3\":\"").append(w3).append("\",")
                .append("\"e\":\"").append(e).append("\",")
                .append("\"publickey\":\"").append(publickey).append("\"}")
                .toString();
    }
}