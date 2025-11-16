package view;

import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

@Getter
public class ConsoleView {
    private final Scanner scanner = new Scanner(System.in);
    private final String MENU_MESSAGE = "Меню: ";
    private final String CHOOSE_MESSAGE = "Выберите пункт: ";
    private final String INPUT_NAME_MESSAGE = "Введите имя: ";
    private final String INPUT_EMAIL_MESSAGE = "Введите email: ";
    private final String INPUT_AGE_MESSAGE = "Введите возраст: ";
    private final String INPUT_USER_ID_MESSAGE = "Введите ID пользователя: ";
    private final String INPUT_CREATE_AT_MESSAGE = "Введите дату создания в формате \"dd.mm.yyyy\": ";
    private final String FORMAT_DATE = "dd.MM.yyyy";


    public void printMenu() {
        System.out.println(MENU_MESSAGE);
        for (Menu item : Menu.values()) {
            System.out.println(item.ordinal() + 1 + ". " + item.getCommand());
        }
    }

    public String printInput() {
        System.out.println(CHOOSE_MESSAGE);

        System.out.print("> ");
        return scanner.nextLine();
    }

    public void printMessage(String message) {
        System.out.println(message);
    }


    public String askUserName() {
        System.out.print(INPUT_NAME_MESSAGE);
        return scanner.nextLine();
    }

    public String askUserEmail() {
        System.out.print(INPUT_EMAIL_MESSAGE);
        return scanner.nextLine();
    }

    public int askUserAge() {
        System.out.print(INPUT_AGE_MESSAGE);
        return Integer.parseInt(scanner.nextLine());
    }

    public Long askUserId() {
        System.out.print(INPUT_USER_ID_MESSAGE);
        return Long.parseLong(scanner.nextLine());
    }

    public LocalDate askUserCreateAt() {
        System.out.print(INPUT_CREATE_AT_MESSAGE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATE);
        String input = scanner.nextLine().trim();
        return LocalDate.parse(input, formatter);
    }

}
