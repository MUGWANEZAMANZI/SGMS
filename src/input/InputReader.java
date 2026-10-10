package input;

public interface InputReader {
    int readInt(String prompt, int minimum, int max);
    double readDouble(String prompt, double minimum, double max);
    String readName(String prompt);
    String readEmail(String prompt);
    String readRequired(String prompt);
    void pause();
}
