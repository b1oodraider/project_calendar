package ru.mai.javafx.javafxcalendarapplication.modules;

import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import ru.mai.javafx.javafxcalendarapplication.CalendarController;
import ru.mai.javafx.javafxcalendarapplication.GetPhotosController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MonthlyCalendar extends GridPane {
    private double sizeX;
    private double sizeY;
    private String month;
    private int year;
    String[] months;

    public MonthlyCalendar(String month, int year) {
        this.sizeX = 357;
        this.sizeY = 350;
        this.month = month;
        this.year = year;

        fillContent();
    }

    public MonthlyCalendar() {
        Calendar calendar = Calendar.getInstance();

        this.sizeX = 357;
        this.sizeY = 350;
        this.month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("en"));
        this.year = calendar.get(Calendar.YEAR);

        fillContent();
    }

    public void fillContent() {
        months = new String[] {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        String[] weekDays = new String[]{"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, Arrays.asList(months).indexOf(month), 1);
        ArrayList<Button> buttons = new ArrayList<>();

        for (int i = 0; i < weekDays.length; ++i) {
            Label label = new Label(weekDays[i]);
            label.setPrefSize(sizeX / 3, sizeY / 5);
            label.setAlignment(Pos.CENTER);
            label.setFont(Font.font(20));

            if (i > 4) {
                label.setTextFill(Color.RED);
            }

            this.add(label, i, 0);
        }

        for (int i = 1; i < 7; ++i) {
            for (int j = 0; j < weekDays.length; ++j) {
                Button button = new Button();
                button.setCursor(Cursor.HAND);
                button.setPrefSize(sizeX / 3, sizeY / 5);
                if (j > 4) {
                    button.setTextFill(Color.RED);
                    button.setStyle("-fx-background-color: #ffe2e2; -fx-border-color: #f48a8a");
                }

                this.add(button, j, i);
                buttons.add(button);
            }
        }

        int count = 0;
        int countOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int numberOfDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (numberOfDayOfWeek == 1) {
            numberOfDayOfWeek += 5;
        } else {
            numberOfDayOfWeek -= 2;
        }

        calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        String currentMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("en"));
        int currentYear = calendar.get(Calendar.YEAR);


        String[] dates;
        String[] holidays;

        try {
            List<String> listOfDates = new ArrayList<String>();
            List<String> listOfHolidays = new ArrayList<String>();
            BufferedReader bf = new BufferedReader(new FileReader("Holidays.txt"));
            String line = bf.readLine();
            while(line != null) {
                listOfDates.add(line.split("/")[0]);
                listOfHolidays.add(line.split("/")[1]);
                line = bf.readLine();
            }
            bf.close();

            dates = listOfDates.toArray(new String[0]);
            holidays = listOfHolidays.toArray(new String[0]);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int day;
        String dateOfDay = "";
        String dateOfBtn = "";
        boolean isContains = false;
        int whatIsDate = 0;
        for (Button btn : buttons) {
            if (count >= numberOfDayOfWeek && count < countOfDays + numberOfDayOfWeek) {
                day = count - numberOfDayOfWeek + 1;
                dateOfBtn = String.valueOf(year) + "-" + String.valueOf(Arrays.asList(months).indexOf(month) + 1) + "-" + String.valueOf(day) ;
                btn.setId(dateOfBtn);
                CalendarController calendarController = new CalendarController();
                btn.setOnMouseClicked((event) -> {
                    if (calendarController.checkLogin()) {
                        if (event.getButton() == MouseButton.SECONDARY) {
                            GetPhotosController gtf = new GetPhotosController();
                            String description = "";
                            boolean isPhotoHere;
                            try {
                                gtf.getPhotos(btn.getId().toString());
                                isPhotoHere = gtf.isPhotoHere;
                                description = gtf.getDescription();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            if (isPhotoHere) {
                                Label label = new Label();
                                label.setText(description);
                                label.setMaxWidth(600);
                                label.setMaxHeight(200);
                                label.setLayoutY(450);
                                label.setLayoutX(100);
                                label.setWrapText(true);
                                Image image;
                                try {
                                    image = new Image(MonthlyCalendar.class.getResourceAsStream("/pictures/pic" + btn.getId().toString() + ".jpg"));
                                } catch (RuntimeException e) {
                                    image = new Image(MonthlyCalendar.class.getResourceAsStream("/pictures/pic" + btn.getId().toString() + ".png"));
                                }
                                ImageView imageView = new ImageView(image);
                                imageView.setFitHeight(400);
                                imageView.setFitWidth(600);
                                imageView.setLayoutX(100);
                                imageView.setLayoutY(50);
                                Group root = new Group(label, imageView);
                                Scene scene = new Scene(root);
                                Stage stagePhotos = new Stage();
                                stagePhotos.setWidth(800);
                                stagePhotos.setHeight(700);
                                stagePhotos.setTitle("This day's photo");
                                stagePhotos.setScene(scene);
                                stagePhotos.show();
                            }
                        } else {
                            try {
                                addDateTOFile(Const.FILE_DATE_PATH, btn);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                            Button buttonLeaveEntry = new Button();
                            buttonLeaveEntry.setText("Leave an entry for the day");
                            buttonLeaveEntry.setLayoutY(75);
                            buttonLeaveEntry.setLayoutX(50);
                            buttonLeaveEntry.setPrefWidth(200);
                            buttonLeaveEntry.setTextAlignment(TextAlignment.CENTER);
                            buttonLeaveEntry.setStyle("-fx-background-color: lightgreen; -fx-border-color: black; -fx-cursor: HAND");
                            Button buttonShowEntry = new Button();
                            buttonShowEntry.setText("Show an entry for the day");
                            buttonShowEntry.setLayoutY(150);
                            buttonShowEntry.setLayoutX(50);
                            buttonShowEntry.setPrefWidth(200);
                            buttonShowEntry.setTextAlignment(TextAlignment.CENTER);
                            buttonShowEntry.setStyle("-fx-background-color: lightgreen; -fx-border-color: black; -fx-cursor: HAND");
                            Group rootChoice = new Group(buttonLeaveEntry, buttonShowEntry);
                            Scene sceneChoice = new Scene(rootChoice, Color.CYAN);
                            Stage stageChoice = new Stage();
                            stageChoice.setTitle("Choice");
                            stageChoice.setHeight(300);
                            stageChoice.setWidth(300);
                            stageChoice.setScene(sceneChoice);
                            stageChoice.show();
                            buttonLeaveEntry.setOnMouseClicked((eventLeave) -> {
                                stageChoice.hide();
                                RadioButton radioButtonNotes = new RadioButton();
                                radioButtonNotes.setText("Note");
                                radioButtonNotes.setFont(Font.font(20));
                                radioButtonNotes.setLayoutY(260);
                                RadioButton radioButtonTodo = new RadioButton();
                                radioButtonTodo.setText("To-do");
                                radioButtonTodo.setFont(Font.font(20));
                                radioButtonTodo.setLayoutY(310);
                                RadioButton radioButtonHoliday = new RadioButton();
                                radioButtonHoliday.setFont(Font.font(20));
                                radioButtonHoliday.setLayoutY(360);
                                radioButtonHoliday.setText("Holiday");
                                ToggleGroup radioGroup = new ToggleGroup();
                                radioButtonNotes.setToggleGroup(radioGroup);
                                radioButtonTodo.setToggleGroup(radioGroup);
                                radioButtonHoliday.setToggleGroup(radioGroup);
                                Button buttonSave = new Button();
                                buttonSave.setText("Save");
                                buttonSave.setFont(Font.font(20));
                                buttonSave.setLayoutY(410);
                                buttonSave.setStyle("-fx-background-color: lightgreen; -fx-border-color: black; -fx-cursor: HAND");
                                TextArea text = new TextArea();
                                text.setPromptText("Enter your text");
                                text.setWrapText(true);
                                text.setStyle("-fx-border-color: black");
                                text.setLayoutY(25);
                                text.setPrefHeight(200);
                                text.setPrefWidth(450);
                                if (radioButtonNotes.isSelected()) {
                                    text.setId("Notes");
                                } else if (radioButtonTodo.isSelected()) {
                                    text.setId("Plan");
                                } else {
                                    text.setId("Holiday");
                                }
                                Group root = new Group(buttonSave, text, radioButtonHoliday, radioButtonNotes, radioButtonTodo);
                                Scene scene = new Scene(root, Color.CYAN);
                                Stage stagePhotos = new Stage();
                                stagePhotos.setWidth(450);
                                stagePhotos.setHeight(500);
                                stagePhotos.setTitle("This day's notes");
                                stagePhotos.setScene(scene);
                                stagePhotos.show();
                                buttonSave.setOnMouseClicked((eventSave) -> {
                                    if (radioButtonNotes.isSelected()) {
                                        text.setId("Notes");

                                        DatabaseHandler dbHandler = new DatabaseHandler();
                                        String aue = text.getText();
                                        Plan plan = new Plan(readIdFromFile(Const.FILE_USER_ID_PATH), readDateFromFile(Const.FILE_DATE_PATH), aue);
                                        dbHandler.makeNote(plan);

                                        showNotificationAboutCreatingNote(readDateFromFile(Const.FILE_DATE_PATH));

                                    } else if (radioButtonTodo.isSelected()) {
                                        text.setId("Plan");

                                        DatabaseHandler dbHandler = new DatabaseHandler();
                                        String aue = text.getText();
                                        Plan plan = new Plan(readIdFromFile(Const.FILE_USER_ID_PATH), readDateFromFile(Const.FILE_DATE_PATH), aue);
                                        dbHandler.makePlan(plan);

                                        showNotificationAboutCreatingPlan(readDateFromFile(Const.FILE_DATE_PATH));

                                    } else {
                                        text.setId("Holiday");

                                        DatabaseHandler dbHandler = new DatabaseHandler();
                                        String aue = text.getText();
                                        Plan plan = new Plan(readIdFromFile(Const.FILE_USER_ID_PATH), readDateFromFile(Const.FILE_DATE_PATH), aue);
                                        dbHandler.makeHoliday(plan);

                                        showNotificationAboutCreatingHoliday(readDateFromFile(Const.FILE_DATE_PATH));
                                    }
                                });
                            });
                            buttonShowEntry.setOnMouseClicked((eventShow) -> {
                                Label labelHeadNotes = new Label("Notes:");
                                Label labelNotes = new Label();
                                labelNotes.setWrapText(true);
                                labelNotes.setLayoutY(30);
                                labelNotes.setText("NOTES IS HERE");
                                labelNotes.setStyle("-fx-border-color: black");
                                Button buttonChangeNotes = new Button();//СДЕЛАЙ КНОПКИ ПОД КАЖДЫМ ЛЕЙБЛОМ
                                Label labelHeadPlans = new Label("Plans");
                                labelHeadPlans.setLayoutY(70);
                                Label labelPlans = new Label();
                                labelPlans.setWrapText(true);
                                labelPlans.setLayoutY(100);
                                labelPlans.setText("Plans IS HERE");
                                labelPlans.setStyle("-fx-border-color: black");
                                Label labelHeadHolidays = new Label("Holidays");
                                labelHeadHolidays.setLayoutY(140);
                                Label labelHolidays = new Label();
                                labelHolidays.setWrapText(true);
                                labelHolidays.setLayoutY(170);
                                labelHolidays.setText("HOLIDAYS IS HERE");
                                labelHolidays.setStyle("-fx-border-color: black");
                                Group rootShow = new Group(labelHeadNotes, labelHeadPlans, labelHeadHolidays, labelNotes, labelPlans, labelHolidays);
                                Scene sceneShow = new Scene(rootShow, Color.CYAN);
                                Stage stageShow = new Stage();
                                stageShow.setTitle("ShowEntry");
                                stageShow.setScene(sceneShow);
                                stageShow.setHeight(300);
                                stageShow.setWidth(300);
                                stageShow.show();
                                stageChoice.hide();

                                DatabaseHandler gettingNote = new DatabaseHandler();
                                ResultSet resultNote = gettingNote.getNote();

                                try {
                                    if (resultNote.next()) {
                                        labelNotes.setText(resultNote.getString(1));
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                } catch (NullPointerException e) {

                                }

                                DatabaseHandler gettingPlan = new DatabaseHandler();
                                ResultSet resultPlan = gettingPlan.getPlan();

                                try {
                                    if (resultPlan.next()) {
                                        labelPlans.setText(resultPlan.getString(1));
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                } catch (NullPointerException e) {

                                }

                                DatabaseHandler gettingHoliday = new DatabaseHandler();
                                ResultSet resultHoliday = gettingHoliday.getHoliday();

                                try {
                                    if (resultHoliday.next()) {
                                        labelHolidays.setText(resultHoliday.getString(1));
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                } catch (NullPointerException e) {

                                }
                                
                            });
                        }
                    } else {
                        showNotificationAboutNotSignIn();
                    }
                });
                dateOfDay = String.valueOf(day) + "-" + String.valueOf(Arrays.asList(months).indexOf(month) + 1);
                for (int i = 0; i < dates.length; ++i) {
                    if (dateOfDay.equals(dates[i])) {
                        isContains = true;
                        whatIsDate = i;
                    }
                }
                if (isContains) {
                    btn.setText(day + "\n" + holidays[whatIsDate]);
                    btn.setFont(new Font(8));
                    btn.setStyle("-fx-background-color: #ffe2e2; -fx-border-color: #f48a8a; -fx-text-alignment: CENTER");
                } else {
                    btn.setText(String.valueOf(day));
                }
                isContains = false;
                if (currentDay == day && currentYear == year && currentMonth.equals(month)) {
                    btn.setStyle(btn.getStyle() + "; -fx-border-color: black");
                }
            } else {
                btn.setDisable(true);
            }
            ++count;
        }
    }

    public void addDateTOFile(String path, Button button) throws FileNotFoundException, SQLException {
        File file = new File(path);
        PrintWriter printWriter = new PrintWriter(file);

        try (BufferedWriter bf = Files.newBufferedWriter(Path.of(path),
                StandardOpenOption.TRUNCATE_EXISTING)) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        printWriter.println(button.getId());
        printWriter.close();
    }

    public String readDateFromFile(String path) {
        String date = "";
        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file);
            date = scanner.nextLine();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String finalDate = date;
        return finalDate;
    }

    public int readIdFromFile(String path) {
        int id = 0;
        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file);
            id = scanner.nextInt();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int finalId = id;
        return finalId;
    }

    public String getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String[] getMonths() {
        return months;
    }

    private void showNotificationAboutCreatingNote(String date) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creating note");
        alert.setHeaderText("You've created the note on " + date);

        alert.showAndWait();
    }

    private void showNotificationAboutCreatingPlan(String deadline) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creating plan");
        alert.setHeaderText("You've created the plan. DeadLine - " + deadline);

        alert.showAndWait();
    }

    private void showNotificationAboutCreatingHoliday(String date) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creating holiday");
        alert.setHeaderText("You've created the holiday on " + date);

        alert.showAndWait();
    }

    private void showNotificationAboutNotSignIn() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Not Sign In");
        alert.setHeaderText("Please Sign In to use full version");
        alert.showAndWait();
    }
}