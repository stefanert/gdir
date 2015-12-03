import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Vede on 28.11.2015.
 */
public class Options {

    private String[] options;
    private String input;

    public Options(){

    }

    public Options(String input){
        this.input = input;
        this.createArray();
    }

    public void createArray(){
        if(this.input.length() > 0){
            options = input.split(" ");
        }
    }

    public void checkArguments() {

        this.input = new Scanner(System.in).nextLine();
        if (input.length() > 0) {
            this.createArray();
                if ( options != null  &&  (( options.length > 2 &&(   !options[2].equals("-bw") ||
                                                                     !options[1].equals("-st") ||
                                                                     !options[0].equals("-cf")
                                                                 )
                                          ) ||
                                          (options.length > 1  &&!(  (options[0].equals("-cf") && options[1].equals("-st"))  ||
                                                                     (options[0].equals("-cf") && options[1].equals("-bw"))  ||
                                                                     (options[0].equals("-st") && options[1].equals("-bw"))
                                                                  )
                                          ) ||
                                          (options.length > 0) &&!(  options[0].equals("-cf")    ||
                                                                     options[0].equals("-st")    ||
                                                                     options[0].equals("-bw")
                                                                  ))){
                    System.out.print("Please check your input!\n" +
                            "> ");
                    checkArguments();
                }
        }
    }


    public String[] checkMode() {

        this.input = new Scanner(System.in).nextLine();
        if (input.length() <= 0)    this.input = "No arguments!";

            this.createArray();
            if ((options != null  &&  ((options.length > 1 &&!(   options[0].equals("-t")    ||
                                                                  options[0].equals("-q")    ||
                                                                  options[0].equals("-f")
                                                                )
                                        )
                                      )
                )                           ||
                  options == null           ||
                  (options.length < 2 && !(options[0].equals("exit")))
                ){
                System.out.print("Please check your input!\n" +
                        "> ");
                checkMode();
            }

        return options;
    }

    public ArrayList<String> normalize(ArrayList<String> words) {
        ArrayList<String> normalized_words = new ArrayList<String>();
        Stemmer st = new Stemmer();

        for (int i = 0; i < words.size(); ++i){
            String normalized_word = words.get(i);
            // case folding
            if (this.input.contains("cf"))	normalized_word = normalized_word.toLowerCase();
            // stemming
            if (this.input.contains("st")) {
                st.add(normalized_word.toCharArray(), normalized_word.length());
                st.stem();
                normalized_word = st.toString();
            }
            normalized_words.add(normalized_word);
        }
        return normalized_words;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }


}
