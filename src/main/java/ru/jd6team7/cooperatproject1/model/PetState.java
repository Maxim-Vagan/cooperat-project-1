package ru.jd6team7.cooperatproject1.model;

public enum PetState {
    AT_SHELTER("в приюте"),
    WITH_GUARDIAN("у опекуна"),
    WITH_VISITOR("у посетителя"),
    ADOPTED("усыновлён"),
    STAY_OUT("отсутствует");

    private final String code;

    PetState(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
