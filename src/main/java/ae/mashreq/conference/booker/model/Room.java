package ae.mashreq.conference.booker.model;

import lombok.Getter;

@Getter
public enum Room {
    AMAZE("Amaze",3),
    BEAUTY("Beauty", 7),
    INSPIRE("Inspire",12),
    STRIVE("Strive", 20);

    private final String name;
    private final Integer capacity;
    Room(final String name, final Integer capacity) {
        this.name = name;
        this.capacity = capacity;
    }
}
