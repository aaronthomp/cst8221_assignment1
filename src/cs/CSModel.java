package cs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/** CSModel class extends Application to allow use of JavaFX libraries
 *  class contains all methods and main/start to load java GUI for CA
 * @author Aaron
 *
 */
public class CSModel extends Application {
	// if changing cell size adjust row and column sizes to shorten window since window is set as unadjustable sizing.
	// another good looking demo would be 5 cell size, 150 row size, 300 column size
	/**
	 * determines amount of rows
	 */
	private static final int ROW_SIZE = 400; 
	/**
	 * determines amount of columns
	 */
	private static final int COLUMN_SIZE = 800; 
	/**
	 * determines cell size
	 */
	private static final int CELL_SIZE = 1;
	
	/**
	 * declaring grid size using ROW_SIZE and COLUMN_SIZE variables
	 * used to determine grid size.
	 */
	private int[][] grid = new int[ROW_SIZE][COLUMN_SIZE];
	
	// block below will be used for drop-down menu for language selection
	/**
	 * variable for drop down language selection
	 */
	private ComboBox<String> languageComboBox; 
	/**
	 * variable stores users binary input
	 */
	private TextField ruleInputField;
	/**
	 * used to listen for user clicks. button generates
	 */
	private Button generateButton;
	/**
	 * creating new Canvas object, the canvas...
	 */
	private Canvas canvas;
	
	/**
	 * label for languages
	 */
	private Label languageLabel; 
	/**
	 * label for model
	 */
	private Label ruleLabel;
	
	/**
	 * new map created to translate between the two languages 
	 */
	// using HashMap to map languages, probably not ideal for large scale however it works for this project.
	private Map<String, Map<String, String>> translations = new HashMap<>();

	/**
	 * main line runs launch which goes to start method for Java GUI to run
	 * @param args takes in arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();

		// load header "Cellular Automata" image
		Image image = new Image("file:ca.png"); 
		ImageView imageView = new ImageView(image);

		// create the header with the image, set image in the middle to beautify GUI... hopefully.
		BorderPane header = new BorderPane();
		Region leftSpacer = new Region();
		Region rightSpacer = new Region();
		HBox.setHgrow(leftSpacer, Priority.ALWAYS);
		HBox.setHgrow(rightSpacer, Priority.ALWAYS);
		header.setLeft(leftSpacer);
		header.setCenter(imageView);
		header.setRight(rightSpacer);

		// create the input pane at the bottom, center aligned too. Better design?
		HBox inputPane = new HBox();
		inputPane.setSpacing(10);
		inputPane.setStyle("-fx-alignment: CENTER;"); 

		// create the label for the language selection
		languageLabel = new Label("Language:");

		// create the ComboBox (drop-down menu) for language selection, add language options, action to swap to language when selected.
		languageComboBox = new ComboBox<>();
		languageComboBox.getItems().addAll("English", "French"); 
		languageComboBox.setValue("English");
		languageComboBox.setOnAction(e -> changeLanguage());

		// label creations, adding my "set"/"generate" button for generating binary image. generate sounds high IQ.
		ruleLabel = new Label("Model:");
		ruleInputField = new TextField();
		ruleInputField.setPromptText("Enter binary rule");
		generateButton = new Button("Generate");

		// add all input elements to input pane
		inputPane.getChildren().addAll(languageLabel, languageComboBox, ruleLabel, ruleInputField, generateButton);

		// makes the canvas
		canvas = new Canvas(COLUMN_SIZE * CELL_SIZE, ROW_SIZE * CELL_SIZE);

		// set header, image goes in this space
		root.setTop(header);

		// setting canvas to become the center or middle of GUI
		root.setCenter(canvas);

		// input pane set to bottom
		root.setBottom(inputPane);

		// load language translations into map
		populateTranslations();

		// set button click to generate the pattern, may be inefficient or bad UX design. change later? 
		// allow for user to click or press enter instead? could implement on next/ future assignments.
		generateButton.setOnAction(e -> generatePattern());

		// creates the scene and sets it in the primary stage
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Cellular Automata");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	/**
	 * This method populates the translations for when the user selects English or French from the drop-down menu
	 */
	private void populateTranslations() {
		Map<String, String> englishTranslations = new HashMap<>();
		englishTranslations.put("LanguageLabel", "Language:");
		englishTranslations.put("ModelLabel", "Model:");
		englishTranslations.put("EnterBinaryRulePrompt", "Enter binary rule");
		englishTranslations.put("GenerateButton", "Generate");
		translations.put("English", englishTranslations);

		Map<String, String> frenchTranslations = new HashMap<>();
		frenchTranslations.put("LanguageLabel", "Langue :");
		frenchTranslations.put("ModelLabel", "Modèle :");
		frenchTranslations.put("EnterBinaryRulePrompt", "Entrez une règle binaire");
		frenchTranslations.put("GenerateButton", "Générer");
		translations.put("French", frenchTranslations);
	}

	/**
	 * This method updates the UI with the correct language if its changed within the drop down menu
	 */
	private void changeLanguage() {
		String selectedLanguage = languageComboBox.getValue();
		Map<String, String> selectedTranslations = translations.get(selectedLanguage);

		if (selectedTranslations != null) {
			// Update UI elements with translations
			languageLabel.setText(selectedTranslations.get("LanguageLabel"));
			ruleLabel.setText(selectedTranslations.get("ModelLabel"));
			ruleInputField.setPromptText(selectedTranslations.get("EnterBinaryRulePrompt"));
			generateButton.setText(selectedTranslations.get("GenerateButton"));
		}
	}
	/**
	 * This method does the error checking for input into the text field for the CA rule,
	 * if there is no errors then it takes the user input and uses it in the generateAutomatonPattern method
	 */
	private void generatePattern() {
		String binaryRule = ruleInputField.getText();

		// regex to make sure input is 1s and 0s and 8 bits
		if (binaryRule.matches("[01]{8}")) {
			// parse the binary rule into an integer
			int rule = Integer.parseInt(binaryRule, 2);

			// create the cellular automaton pattern based on the rule
			generateAutomatonPattern(rule);

			// draw the pattern on the canvas
			drawPattern();
		} else {
			// show an error dialog
			showErrorDialog("Invalid Input", "Please enter a valid 8-bit binary rule.");
		}
	}

	/**
	 * method to show error dialog
	 * @param title takes in a title
	 * @param message displays the message
	 */
	private void showErrorDialog(String title, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}

	/**
	 * this method determines how the CA works and will define each cell as a 1 or 0 based on the Cellular Automata rules
	 * @param rule takes in binary rule from user input
	 */
	private void generateAutomatonPattern(int rule) {
		// initialize the center cell of the first row to 1
		grid[0][COLUMN_SIZE / 2] = 1;

		// Generate subsequent rows based on the rule
		// need to figure out logic for this, took a lot of the idea from stackoverflow/chatGPT. comment each line once it understood
		for (int i = 1; i < ROW_SIZE; i++) {
			for (int j = 0; j < COLUMN_SIZE; j++) {
				// use modulus to wrap around to check bordered cell
				int left = grid[i - 1][(j - 1 + COLUMN_SIZE) % COLUMN_SIZE];
				int center = grid[i - 1][j];
				// use modulus for the same reason, wrap around
				int right = grid[i - 1][(j + 1) % COLUMN_SIZE];
				
				int ruleIndex = (left << 2) | (center << 1) | right;
				grid[i][j] = (rule & (1 << ruleIndex)) != 0 ? 1 : 0;
			}
		}
	}
	
	/**
	 * this method checks to see if the grid contains 1's then if it does it paints square with fillRect black, this is how i get the image of my pattern
	 */
	private void drawPattern() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		// iterate through and paint all cells that are equal to 1, skip over 0's
		for (int i = 0; i < ROW_SIZE; i++) {
			for (int j = 0; j < COLUMN_SIZE; j++) {
				// if grid is 1 it means paint it or its an "on" cell
				if (grid[i][j] == 1) {
					// fillRect method gets the x coordinate, y coordinate, width, height. 
					gc.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
				}
			}
		}
	}
}