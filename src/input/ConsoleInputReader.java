package input;

public class ConsoleInputReader implements InputReader {

    @Override
    public  int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            try {
                int value = Integer.parseInt(readRequired(prompt));
                if (value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                IO.println("The enter value is not valid. Please enter a valid whole number.");
            }
            IO.println("Enter a whole number from %d to %d.".formatted(minimum, maximum));
        }
    }

    @Override
    public double readDouble(String prompt, double minimum, double maximum) {
        while (true) {
            try {
                double value = Double.parseDouble(readRequired(prompt));
                if (value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                IO.println("The enter value is not valid. Please enter a valid number.");
            }
            IO.println("Enter a number from %.0f to %.0f.".formatted(minimum, maximum));
        }
    }

    @Override
    public  String readRequired(String prompt) {
        while (true) {
            String value = IO.readln(prompt);
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
            IO.println("This value is required.");
        }
    }

    @Override
    public String readEmail(String prompt) {
        while (true) {
            String value = IO.readln(prompt);
            if (value != null && !value.isBlank() ) {
                if(!value.contains("@")){
                    IO.println("Invalid email format.");
                } else {
                    return value.trim();
                }
            } else {
                IO.println("This Email field is required.");
            }
        }
    }

    @Override
    public String readName(String prompt) {
        while (true) {
            String value = IO.readln(prompt);
            if (value != null && !value.isBlank()) {
                try{
                    if(value.contains(".") || !(Double.parseDouble(value) == Double.parseDouble(value)))
                    {
                        return value.trim();
                    }else{
                        IO.println("Invalid name format. Failed bug check.");
                        break;
                    }
                } catch (NumberFormatException e) {
                    IO.println("Invalid name format. Cannot be a number.");
                }
            }else{
                IO.println("This Name field is required. JJJ");
            }
        }
        return null;

    }

    @Override
    public void pause(){
        IO.readln("\nPress Enter to continue...");
    }
}
