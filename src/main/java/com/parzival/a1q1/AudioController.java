package com.parzival.a1q1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class AudioController {
    @FXML
    private Text title;

    @FXML
    private Canvas canvas1;

    @FXML
    private Canvas canvas2;

    @FXML
    private Label numOfSamples;

    @FXML
    private Label samplingRate;
    
    Stage stage;

    //open a file dialog to open a .wav file
    public void openFile(ActionEvent event) throws UnsupportedAudioFileException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("WAV Files", "*.wav"));
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            System.out.printf("Open audio file\n");
            loadWAVFile(file);
        }
    }

    private void loadWAVFile(File file) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        AudioFormat audioFormat = audioStream.getFormat();
        int frameSize = audioFormat.getFrameSize();
        long numOfFrame = audioStream.getFrameLength();
        float samplingFrequency = audioFormat.getSampleRate();

        //show text
        numOfSamples.setText(String.format("%d", numOfFrame*2));
        samplingRate.setText(String.format("%.2f", samplingFrequency) + " Hz");

        System.out.printf(samplingRate.getText());

        //buffer to temporarily store the wav file
        byte[] audioBuffer = audioStream.readAllBytes();

        //amplitudes in left & right channel [1 , -1]
        double[] leftChannel = new double[(int) numOfFrame];
        double[] rightChannel = new double[(int) numOfFrame];

        for (int i = 0; i < audioBuffer.length; i += frameSize){
            //calculate amplitude
            //Little Endian:
            //amplitude = (low_byte | high_byte << 8)
            int leftSample = (audioBuffer[i+1] << 8) | (audioBuffer[i] & 0xFF);
            int rightSample = (audioBuffer[i+3] << 8) | (audioBuffer[i+2] & 0xFF);

            //store amplitude to left & right channel
            int index = i/frameSize;
            leftChannel[index] = leftSample/32768.0;
            rightChannel[index] = rightSample/32768.0;

        }

        drawWaveForm(canvas1.getGraphicsContext2D(), leftChannel, canvas1.getWidth(), canvas1.getHeight());
        drawWaveForm(canvas2.getGraphicsContext2D(), rightChannel, canvas2.getWidth(), canvas2.getHeight());
    }

    private void drawWaveForm(GraphicsContext gc, double[] channel, double width, double height) {
        gc.clearRect(0, 0, width, height);
        gc.setStroke(Color.GREEN);

        double midY = height/2;
        double XStep = width / channel.length;

        for (int i = 1; i < channel.length; i++) {
            double x1 = (i - 1) * XStep;
            double y1 = midY - channel[i - 1] * midY;
            double x2 = i * XStep;
            double y2 = midY - channel[i] * midY;
            gc.strokeLine(x1, y1, x2, y2);
        }

    }
}