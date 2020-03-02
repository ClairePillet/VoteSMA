/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
 
 
public class DegreeLineChart extends Application {
 
    static String[] args;
    
    @Override public void start(Stage stage) {
        stage.setTitle("Line Chart Sample");
        Parameters parameters = this.getParameters();
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("degree");
        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
                
        lineChart.setTitle("Degree Distribution");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("Degree");
        //populating the series with data
        
        List<String> t = parameters.getRaw();
        for(int i=0;i<t.size()-1;i++){
            System.out.println(parameters.getRaw().get(i));
            series.getData().add(new XYChart.Data(Integer.parseInt(parameters.getRaw().get(i)),Integer.parseInt(parameters.getRaw().get(i+1))));
            i++;
        }
        
        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().add(series);
        saveAsPng(scene,"C:\\Users\\guifei\\Documents\\chqrt\\"+new java.util.Date().getTime()+".png");
       // stage.setScene(scene);
        stage.show();
        stage.close();
    }
     public void saveAsPng(Scene scene, String path) {
            WritableImage image = scene.snapshot(null);
            File file = new File(path);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    public static void main(String[] args) {
        DegreeLineChart.args=args;
        launch(args);
    }
}