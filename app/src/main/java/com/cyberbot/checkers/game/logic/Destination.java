package com.cyberbot.checkers.game.logic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Represents the destination of a move or capture along with specific information
 * about intermediate steps that have taken place during capture and captured pieces.
 *
 * @see GridEntry
 */
public class Destination {
    private final GridEntry destinationEntry;
    private final ArrayList<GridEntry> capturedPieces;
    private final ArrayList<GridEntry> intermediateSteps;

    /**
     * Parse given {@link CaptureChain} object and extracts destination entry, captured pieces
     * and intermediate steps from it. This constructor should be called when moving the piece
     * involves a capture.
     *
     * @param captureChain A {@link CaptureChain} object. Cannot be null
     *
     * @see CaptureChain
     */
    Destination(@NotNull CaptureChain captureChain) {
        destinationEntry = captureChain.getLocationAfterCapture();
        capturedPieces = captureChain.getCapturedPieces();
        intermediateSteps = captureChain.getIntermediateSteps();
    }

    /**
     * Sets given {@code destinationEntry} as the destination. Other members are set to null.
     * This constructor should be called when moveing the piece doesn't involve captures.
     *
     * @param destinationEntry Destination {@link GridEntry}. Cannot be null
     */
    Destination(@NotNull GridEntry destinationEntry) {
        this.destinationEntry = destinationEntry;
        capturedPieces = null;
        intermediateSteps = null;
    }

    /**
     * Tell if move to destination involves a capture.
     *
     * @return {@code true} if destination involves a capture, {@code false} otherwise
     */
    public boolean isCapture() {
        return capturedPieces != null && capturedPieces.size() > 0;
    }

    @Nullable
    public ArrayList<GridEntry> getCapturedPieces() {
        return capturedPieces;
    }

    @NotNull
    public GridEntry getDestinationEntry() {
        return destinationEntry;
    }

    @Nullable
    public ArrayList<GridEntry> getIntermediateSteps() {
        return intermediateSteps;
    }
}
