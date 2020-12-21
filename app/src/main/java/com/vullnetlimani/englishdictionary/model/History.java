package com.vullnetlimani.englishdictionary.model;

public class History {

    private final String en_word;
    private final String en_def;

    public History(String en_word, String en_def) {
        this.en_word = en_word;
        this.en_def = en_def;
    }

    public String getEn_word() {
        return en_word;
    }

    public String getEn_def() {
        return en_def;
    }
}
