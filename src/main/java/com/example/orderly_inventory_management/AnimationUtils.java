package com.example.orderly_inventory_management;

import javafx.animation.*;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class AnimationUtils {

    public static void applyButtonHoverClickAnimations(Button button) {
        // Hover Animation
        ScaleTransition hoverTransition = new ScaleTransition(Duration.millis(500), button);
        hoverTransition.setFromX(1.0);
        hoverTransition.setToX(1.05);
        hoverTransition.setFromY(1.0);
        hoverTransition.setToY(1.05);

        hoverTransition.setOnFinished(event -> {
            // Keep the button scaled up even after the hover animation completes
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });

        button.setOnMouseEntered(event -> hoverTransition.play());

        button.setOnMouseExited(event -> {
            hoverTransition.setRate(-1);
            hoverTransition.play();
        });

        // Click Animation
        ScaleTransition clickTransition = new ScaleTransition(Duration.millis(200), button);
        clickTransition.setFromX(1.0);
        clickTransition.setToX(0.95);
        clickTransition.setFromY(1.0);
        clickTransition.setToY(0.95);
        clickTransition.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), button);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.7);

        button.setOnMousePressed(event -> {
            hoverTransition.pause(); // Pause the hover animation while clicked
            clickTransition.play();
            fadeTransition.play();
        });

        button.setOnMouseReleased(event -> {
            hoverTransition.setRate(1); // Resume the hover animation on release
            clickTransition.setRate(-1);
            fadeTransition.setRate(-1);
            clickTransition.play();
            fadeTransition.play();
        });
    }
}

