package com.dmsoft.firefly.plugin.chart;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Created by cherry on 2018/2/28.
 */
public class AnnotationSampleApp extends Application {

    public class SampleChart extends AreaChart<Number, Number> {
        public SampleChart() {
            super(new NumberAxis(), new NumberAxis());

            getXAxis().setLabel("X");
            getYAxis().setLabel("Y");

            final Series<Number, Number> data = new Series<Number, Number>();
            data.setName("Dummy data");
            data.getData().addAll(
                    new Data<Number, Number>(0, 4),
                    new Data<Number, Number>(1, 5),
                    new Data<Number, Number>(2, 6),
                    new Data<Number, Number>(3, 5),
                    new Data<Number, Number>(4, 5),
                    new Data<Number, Number>(5, 7),
                    new Data<Number, Number>(6, 8),
                    new Data<Number, Number>(7, 9),
                    new Data<Number, Number>(8, 7)
            );

            getData().add(data);
        }
    }

    public class ChartAnnotationNode {
        private final Node _node;
        private double _x;
        private double _y;

        public ChartAnnotationNode(final Node node, final double x, final double y) {
            _node = node;
            _x = x;
            _y = y;
        }

        public Node getNode() {
            return _node;
        }

        public double getX() {
            return _x;
        }

        public double getY() {
            return _y;
        }

        public void setX(final double x) {
            _x = x;
        }

        public void setY(final double y) {
            _y = y;
        }
    }

    public class ChartAnnotationOverlay extends Pane {
        private ObservableList<ChartAnnotationNode> _annotationNodes;
        private XYChart<Number, Number> _chart;

        public ChartAnnotationOverlay(final XYChart<Number, Number> chart) {
            _chart = chart;

    /* Create a list to hold your annotations */
            _annotationNodes = FXCollections.observableArrayList();

    /* This will be our update listener, to be invoked whenever the chart changes or annotations are added */
            final InvalidationListener listener = new InvalidationListener() {
                @Override
                public void invalidated(final Observable observable) {
                    update();
                }
            };
            _chart.needsLayoutProperty().addListener(listener);
            _annotationNodes.addListener(listener);

    /* Add new annotations by shift-clicking */
            setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.isShiftDown())
                        addAnnotation(mouseEvent.getX(), mouseEvent.getY());
                }
            });
        }

        /**
         * Invoked whenever the chart changes or annotations are added. This basically does a relayout of the annotation nodes.
         */
        private void update() {
            getChildren().clear();

            final Axis<Number> xAxis = _chart.getXAxis();
            final Axis<Number> yAxis = _chart.getYAxis();

    /* For each annotation, add a circle indicating the position and the custom node right next to it */
            for (ChartAnnotationNode annotation : _annotationNodes) {
                final double x = xAxis.localToParent(xAxis.getDisplayPosition(annotation.getX()), 0).getX() + _chart.getPadding().getLeft();
                final double y = yAxis.localToParent(0, yAxis.getDisplayPosition(annotation.getY())).getY() + _chart.getPadding().getTop();

                final Circle indicator = new Circle(3);
                indicator.setStroke(Color.BLUEVIOLET);
                indicator.setCenterX(x);
                indicator.setCenterY(y);

                getChildren().add(indicator);

                final Node node = annotation.getNode();
                getChildren().add(node);
                node.relocate(x + 10, y - node.prefHeight(Integer.MAX_VALUE) / 2);
                node.autosize();
            }
        }

        /**
         * Add a new annotation for the given display coordinate.
         */
        private void addAnnotation(final double displayX, final double displayY) {
            final Axis<Number> xAxis = _chart.getXAxis();
            final Axis<Number> yAxis = _chart.getYAxis();

            final double x = (xAxis.getValueForDisplay(xAxis.parentToLocal(displayX, 0).getX() - _chart.getPadding().getLeft())).doubleValue();
            final double y = (yAxis.getValueForDisplay(yAxis.parentToLocal(0, displayY).getY() - _chart.getPadding().getTop())).doubleValue();

            if (xAxis.isValueOnAxis(x) && yAxis.isValueOnAxis(y))
                _annotationNodes.add(new ChartAnnotationNode(new Label("Annotation " + System.currentTimeMillis()), x, y));
        }
    }


    @Override
    public void start(final Stage stage) throws Exception {
        final SampleChart chart = new SampleChart();

        final ChartAnnotationOverlay overlay = new ChartAnnotationOverlay(chart);

        final StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(chart, overlay);

        final Scene scene = new Scene(stackPane);
        stage.setScene(scene);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
