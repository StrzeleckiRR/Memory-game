import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;
import java.util.List;

public class Memory extends Application {

    private static final int NUMBER_OF_PAIRS = 5; // Liczba par słów
    private static final int MAX_HEALTH = 3;     // Maksymalna liczba prób

    private int result = 0;                      // Liczba poprawnych par
    private int healthTries = MAX_HEALTH;        // Pozostałe próby
    private Label infoHealth;                    // Etykieta wyświetlająca liczbę prób

    private final List<Label> checkMarks = new ArrayList<>();  // Lista znaczników poprawnych par
    private final List<Button> buttonsEng = new ArrayList<>(); // Przyciski z słowami angielskimi
    private final List<Button> buttonsPl = new ArrayList<>();  // Przyciski z tłumaczeniami

    private final Button[] selectedButtons = new Button[2];    // Aktualnie wybrane przyciski
    private Map<String, String> wordPairs;               // Mapowanie słów angielskich na polskie


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        initializeWordPairs(); // Inicjalizacja słów
        setupUI(stage);        // Konfiguracja interfejsu użytkownika
    }

    //Inicjalizuje mapowanie słów angielskich na polskie.
    private void initializeWordPairs() {
        wordPairs = new HashMap<>();
        wordPairs.put("Both", "Obaj, obie");
        wordPairs.put("Develop", "Rozwijać");
        wordPairs.put("Solution", "Rozwiązanie");
        wordPairs.put("Opportunity", "Okazja");
        wordPairs.put("Attempt", "Próba");
        wordPairs.put("Failure", "Porażka");
        wordPairs.put("Benefit", "Korzyść");
        wordPairs.put("Improve", "Poprawiać");
        wordPairs.put("Add", "Dodawać");
        wordPairs.put("Which", "Który");
        wordPairs.put("Listen", "Słuchać");
        wordPairs.put("Keep out", "Nie wchodzić");
        wordPairs.put("Write", "Pisać");
    }

    //Konfiguruje interfejs użytkownika.
    private void setupUI(Stage stage) {
        // Tworzenie siatki z przyciskami
        GridPane gridPaneButtons = createButtonGrid();
        // Tworzenie paska menu
        MenuBar menuBar = createMenuBar(stage, gridPaneButtons);

        // Tworzenie tytułu gry
        Label title = new Label("Memory");
        title.setFont(new Font("Arial", 30));

        // Kontener na górną część interfejsu
        VBox vboxTop = new VBox(menuBar, title);
        vboxTop.setAlignment(Pos.TOP_CENTER);

        // Tworzenie panelu informacyjnego
        GridPane infoGridPane = createInfoPanel();

        // Główny kontener
        BorderPane root = new BorderPane();
        root.setTop(vboxTop);
        root.setCenter(gridPaneButtons);
        root.setBottom(infoGridPane);

        // Scena i okno
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Memory");
        stage.show();
    }

    //Tworzy pasek menu.
    private MenuBar createMenuBar(Stage stage, GridPane gridPaneButtons) {
        Menu startMenu = new Menu("Start");
        MenuItem newGame = new MenuItem("Nowa gra");
        MenuItem exitGame = new MenuItem("Exit");

        Menu helpMenu = new Menu("Pomoc");
        MenuItem rulesItem = new MenuItem("Zasady gry");

        exitGame.setOnAction(event -> stage.close());
        rulesItem.setOnAction(event -> showHelpWindow());
        newGame.setOnAction(event -> resetGame(gridPaneButtons));

        startMenu.getItems().addAll(newGame, exitGame);
        helpMenu.getItems().add(rulesItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(startMenu, helpMenu);
        return menuBar;
    }

    //Tworzy siatkę z przyciskami.
    private GridPane createButtonGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(0, 0, 0, 200));
        gridPane.setVgap(10);
        gridPane.setHgap(200);

        // Losowanie słów
        List<String> keys = new ArrayList<>(wordPairs.keySet());
        Collections.shuffle(keys);
        List<String> selectedKeys = keys.subList(0, NUMBER_OF_PAIRS);

        // Tworzenie przycisków
        for (String word : selectedKeys) {
            buttonsEng.add(createButton(word, this::handleButtonClick));
            buttonsPl.add(createButton(wordPairs.get(word), this::handleButtonClick));
        }
        Collections.shuffle(buttonsPl);

        // Dodawanie przycisków do siatki
        for (int i = 0; i < NUMBER_OF_PAIRS; i++) {
            gridPane.add(buttonsEng.get(i), 0, i);
            gridPane.add(buttonsPl.get(i), 1, i);

            Label checkMark = new Label();
            checkMark.setStyle("-fx-font-size: 20px; -fx-text-fill: green;");
            checkMarks.add(checkMark);
            gridPane.add(checkMark, 2, i);
        }
        return gridPane;
    }

    //Tworzy panel informacyjny.
    private GridPane createInfoPanel() {
        GridPane infoGridPane = new GridPane();
        infoGridPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        infoGridPane.setMinHeight(30);
        infoGridPane.setPrefWidth(400);

        infoHealth = new Label("Ilość prób : " + healthTries);
        infoHealth.setStyle("-fx-font-weight: bold;");
        GridPane.setHalignment(infoHealth, HPos.RIGHT);
        GridPane.setHgrow(infoHealth, Priority.ALWAYS);
        infoHealth.setPadding(new Insets(0, 20, 0, 0));
        infoGridPane.add(infoHealth, 0, 0);

        return infoGridPane;
    }

    //Obsługuje kliknięcie przycisku.
    private void handleButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        if (selectedButtons[0] == null) {
            selectedButtons[0] = clickedButton;
            selectedButtons[0].setDisable(true);
        } else if (selectedButtons[1] == null && selectedButtons[0] != clickedButton) {
            selectedButtons[1] = clickedButton;
            selectedButtons[1].setDisable(true);
            checkPair();
        }
    }

    //Sprawdza, czy wybrana para jest poprawna.
    private void checkPair() {
        String text1 = selectedButtons[0].getText();
        String text2 = selectedButtons[1].getText();

        boolean isMatch = wordPairs.containsKey(text1) && wordPairs.get(text1).equals(text2)
                || wordPairs.containsKey(text2) && wordPairs.get(text2).equals(text1);


        if (isMatch) {
            soundsAnswers(true);
            result++;
            int index;
            if(buttonsEng.contains(selectedButtons[0])) {
                index = buttonsEng.indexOf(selectedButtons[0]);
            } else {
                index = buttonsPl.indexOf(selectedButtons[0]);
            }
            if(index != -1) {
                checkMarks.get(index).setText("✅");
            }else{
                System.out.println("Nie znaleziono indeksu dla " + selectedButtons[0].getText());
            }
        } else {
            soundsAnswers(false);
            selectedButtons[0].setDisable(false);
            selectedButtons[1].setDisable(false);
            healthTries--;
            updateInfoHealth();
        }

        selectedButtons[0] = null;
        selectedButtons[1] = null;

        if (result == NUMBER_OF_PAIRS) {
            showModalWindow("Gratulacje!", "Udało Ci się połączyć wszystkie pary!");
        }
        if (healthTries == 0) {
            showModalWindow("Koniec gry!", "Wykorzystałeś wszystkie próby, spróbuj ponownie!");
            buttonsEng.forEach(button -> button.setDisable(true));
            buttonsPl.forEach(button -> button.setDisable(true));
        }
    }

    //Resetuje stan gry.
    private void resetGame(GridPane gridPaneButtons) {
        selectedButtons[0] = null;
        selectedButtons[1] = null;

        buttonsEng.forEach(button -> button.setDisable(false));
        buttonsPl.forEach(button -> button.setDisable(false));

        //reset result & attempts
        result = 0;
        healthTries = MAX_HEALTH;
        updateInfoHealth();

        //clear Buttons & checkMarks
        buttonsEng.clear();
        buttonsPl.clear();
        checkMarks.clear();

        // Losowanie słów
        List<String> keys = new ArrayList<>(wordPairs.keySet());
        Collections.shuffle(keys);
        List<String> selectedKeys = keys.subList(0, NUMBER_OF_PAIRS);

        // Tworzenie przycisków
        for (String word : selectedKeys) {
            buttonsEng.add(createButton(word, this::handleButtonClick));
            buttonsPl.add(createButton(wordPairs.get(word), this::handleButtonClick));
        }
        Collections.shuffle(buttonsPl);
        gridPaneButtons.getChildren().clear();

        // Dodawanie przycisków do siatki
        for (int i = 0; i < NUMBER_OF_PAIRS; i++) {
            gridPaneButtons.add(buttonsEng.get(i), 0, i);
            gridPaneButtons.add(buttonsPl.get(i), 1, i);

            Label checkMark = new Label();
            checkMark.setStyle("-fx-font-size: 20px; -fx-text-fill: green;");
            checkMarks.add(checkMark);
            gridPaneButtons.add(checkMark, 2, i);
        }

    }

    //Aktualizuje etykietę z liczbą prób.
    private void updateInfoHealth() {
        infoHealth.setText("Ilość prób : " + healthTries);
    }

    //Wyświetla okno pomocy.
    private void showHelpWindow() {
        Stage helpStage = new Stage();
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.setTitle("Zasady gry");

        Label helpLabel = new Label("Zasady gry:\n1. Połącz w pary słowa ANG - PL/ PL - ANG.\n2. Jeśli pasują – znikają.\n3. Jeśli nie pasują – wracają.\n4. Znajdź wszystkie pary, aby wygrać!\n5. Pamiętaj! Masz tylko 3 szanse!");
        helpLabel.setWrapText(true);
        helpLabel.setMaxWidth(250);

        Button buttonAccept = new Button("Akceptuję");
        buttonAccept.setOnAction(event -> helpStage.close());

        VBox layout = new VBox(20, helpLabel, buttonAccept);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 300, 200);
        helpStage.setScene(scene);
        helpStage.show();
    }

    //Wyświetla modalne okno z komunikatem.
    private void showModalWindow(String title, String message) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);

        Label textLabel = new Label(message);
        textLabel.setWrapText(true);

        Button buttonAccept = new Button("Akceptuję");
        buttonAccept.setOnAction(event -> stage.close());

        VBox layout = new VBox(20, textLabel, buttonAccept);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.show();
    }

    //Tworzy przycisk z obsługą zdarzeń.
    private Button createButton(String text, EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.setOnAction(handler);
        button.setFocusTraversable(false);
        return button;
    }

    //Obsługa dzwięków.

    private MediaPlayer createMediaPlayer(String soundFileName) {
        String soundFile = Objects.requireNonNull(getClass().getResource(soundFileName)).toString();
        Media media = new Media(soundFile);

        return new MediaPlayer(media);
    }


      //Obsługa dzwięków Correct/Incorrect.


    private void soundsAnswers(boolean isCorrect) {
        if (isCorrect) {
            MediaPlayer correct = createMediaPlayer("correct.mp3");
            correct.play();
        } else {
            MediaPlayer inCorrect = createMediaPlayer("incorrect.mp3");
            inCorrect.play();
        }
    }


}






