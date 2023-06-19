package ConsoleColors;

public class ConsoleColors {

    // Reset // fim do texto colorido
    public static final String RESET = "\033[0m";

    // Regular Colors
    public static final String RED = "\033[0;31m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String CYAN = "\033[0;36m";

    public static String MessageWColor( String Color, String mensagemPColorir, String mensagemSCor){
        return Color + mensagemPColorir + RESET + mensagemSCor;
    }

}
