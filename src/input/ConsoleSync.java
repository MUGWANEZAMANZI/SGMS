package input;

/**
 * Coordinates console writes between the main input thread and background threads.
 *
 * Background threads call printBackground() to surface a notification without
 * fragmenting the active input prompt. The main thread calls setActivePrompt()
 * just before blocking on input so the background side knows what to reprint.
 */
public final class ConsoleSync {

    private static volatile String activePrompt = "";

    private ConsoleSync() {}

    /**
     * Records the prompt currently being shown to the user.
     * Called by ConsoleInputReader immediately before blocking on input.
     */
    public static void setActivePrompt(String prompt) {
        activePrompt = prompt;
    }

    /**
     * Prints a background notification on its own line, then reprints the
     * active input prompt so the user can continue typing without confusion.
     *
     * @param message the notification text to display
     */
    public static void printBackground(String message) {
        System.out.print("\n[Auto-import] " + message + "\n");
        System.out.print(activePrompt);
        System.out.flush();
    }
}
