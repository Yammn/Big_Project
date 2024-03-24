package com.example.dictionaryapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import com.voicerss.tts.*;
import java.io.*;
import java.util.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;


public class Controller {
    @FXML
    private Label myLabel;
    @FXML
    private CheckBox choice;
    @FXML
    private ListView<String> listView;
    @FXML
    private WebView definitionView;
    @FXML
    private TextField searchWord;
    @FXML
    private HTMLEditor htmlEditor;

    public void exitProgram() {
        System.exit(0);
    }

    public static final Map<String, String> data_eng2vie = Main.getE2V();
    public static Set<String> set_eng2vie = data_eng2vie.keySet();
    public static final Map<String, String> data_vie2eng = Main.getV2E();
    public static Set<String> set_vie2eng = data_vie2eng.keySet();
    public static Map<String, String> data;
    public static Set<String> set;

    public void search() throws Exception {
        String text = searchWord.getText();
        //String lowerCasetext = text.toLowerCase();
        if (set.contains(text)) {
            definitionView.getEngine().loadContent(data.get(text), "text/html");
            spelling();
        }
    }

    public void selectedWord() throws Exception {
        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            definitionView.getEngine().loadContent(data.get(selected), "text/html");
            searchWord.setText(selected);
            spelling();
        }
    }

    public void keyPressed(KeyEvent e) throws Exception {
        if (e.getCode().equals(KeyCode.ENTER)) {
            search();
        }
    }

    public static List<String> curr;
    public static Map<String, List<String>> prefixMap;

    public void showAllWords() {
        if (choice.isSelected()) {
            prefixMap = Main.getPrefixMap_V2E();
            set = set_vie2eng;
            data = data_vie2eng;
        } else {
            prefixMap = Main.getPrefixMap_E2V();
            set = set_eng2vie;
            data = data_eng2vie;
        }
        String text = searchWord.getText();
        if (text.length() == 1) {
            listView.getItems().removeAll(set);
            curr = prefixMap.getOrDefault(text, new ArrayList<>());
            listView.getItems().addAll(curr);

        } else if (text.length() > 1) {
            listView.getItems().removeAll(curr);
            curr = prefixMap.getOrDefault(text, new ArrayList<>());
            listView.getItems().addAll(curr);
        } else {
            listView.getItems().addAll(set);
        }
    }

    private static final String myKey = "40ccd1f320c549f3afc53b26046c49a4";
    public static String ACCENT = Languages.English_GreatBritain;
    public static String path = "data/tts_rss_word.mp3";

    public void requestDownload(String text) throws Exception {
        VoiceProvider tts = new VoiceProvider(myKey);
        VoiceParameters params = new VoiceParameters(text, ACCENT);
        params.setCodec(AudioCodec.MP3);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);
        byte[] voice = tts.speech(params);
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(voice, 0, voice.length);
        fos.flush();
        fos.close();
    }

    public void spelling() throws Exception {
        String text = searchWord.getText();
        requestDownload(text);
        Media sound = new Media(new File(path).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void change() throws IOException {
        if (choice.isSelected()) {
            myLabel.setText("Vie-Eng");
            path = "data/tts_rss_text.mp3";
            ACCENT = Languages.Vietnamese;
            listWords = listWordsVE;
        } else {
            myLabel.setText("Eng-Vie");
            path = "data/tts_rss_word.mp3";
            ACCENT = Languages.English_GreatBritain;
            listWords = listWordsEV;
        }
    }
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToAdd_Delete_Word_scene(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Test.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 870, 600);
        stage.setScene(scene);
        stage.show();
    }

    public void modifyWord() {
        htmlEditor.setHtmlText("<html>" + searchWord.getText() +
                "<br/><ul><li><b><i> Loại từ: " +
                "</i></b><ul><li><font color='#cc0000'><b> Nghĩa của từ: " +
                "</b></font></li></ul></li></ul></html>");
    }
    public void show() {
        modifyWord();
        showAllWords();
    }
    public static List<String> listWordsEV = Main.getList_EV();
    public static List<String> listWordsVE = Main.getList_VE();
    public static List<String> listWords;
    public void add() throws IOException {
        String word = searchWord.getText();
        String definition = htmlEditor.getHtmlText().replace(" dir=\"ltr\"", "");
        String text = word + definition;
        listWords.addLast(text);
        FileWriter fw = new FileWriter(path);
        BufferedWriter bw = new BufferedWriter(fw);
        for (String content : listWords) {
            bw.write(content);
            bw.write("\n");
        }
        bw.close();
        fw.close();
    }
    public void remove() throws IOException {
        String selected = listView.getSelectionModel().getSelectedItem();
        String definition = data.get(selected);
        FileWriter fw = new FileWriter(path);
        BufferedWriter bw = new BufferedWriter(fw);
        for (String content : listWords) {
            if(!content.equals(selected+definition)) {
                bw.write(content);
                bw.write("\n");
            }
        }
        bw.close();
        fw.close();
    }
}
