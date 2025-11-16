package view;

import lombok.Getter;

@Getter
public enum Menu {
    CREATE("создать"),
    READ("найти всех пользователей"),
    READ_BY_ID("найти пользователя по ID"),
    UPDATE("обновить"),
    DELETE("удалить"),
    EXIT("выход");

    private final String command;

    Menu(String command) {
        this.command = command;
    }

}
