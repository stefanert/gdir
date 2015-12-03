import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Vede on 28.11.2015.
 */
public class Dictionary {

    private HashMap<String, Token> dictionary;
    private ArrayList<File> allDocuments;

    public Dictionary(HashMap<String, Token> dictionary){
        this.dictionary = dictionary;

    }

    public Dictionary(){
        this.dictionary = new HashMap<String, Token>();
        this.allDocuments = new ArrayList<File>();
    }

    public void addToDict(Token token){
        this.dictionary.put(token.get_term(), token);
    }

    public void addDocument(File document){
        this.allDocuments.add(document);
    }


    public Token getToken(String word){
        return this.dictionary.get(word);
    }

    public boolean contains(Token token){
        return this.dictionary.get(token.get_term()) != null;
    }



    //Hier findet die Unterscheidung ob die Suche ein query/topic ist und in einem bestimmten Folder gesucht werden soll
    public String search(String[] query, Options options){
        String res = "";

        //Wenn die Query "exit" ist, soll er gleich rausgehen
        if(query[0].equals("exit")) res = "exit";

        else if(!query[0].equals("-f")){
            if(query[0].equals("-q")){

                //Anwenden der ausgewaehlten Optionen auf die Query
                ArrayList<String> queryList = new ArrayList<String>(Arrays.asList(query));
                if (options.getInput().contains("-cf") || options.getInput().contains("-st"))	queryList = options.normalize(queryList);
                query = queryList.toArray(new String[queryList.size()]);

                res = search_query(query);
            } else {

                String path = System.getProperty("user.dir") + "/resources/topics/" + query[1];

                File topic = new File(path);
                Index methods = new Index();

                //Anwenden der ausgewaehlten Optionen auf den Topic Inhalt
                ArrayList<String> useful_lines = methods.get_usefull_lines(topic);
                ArrayList<String> all_words     = methods.get_all_words(useful_lines);
                if (options.getInput().contains("-cf") || options.getInput().contains("-st"))	all_words = options.normalize(all_words);
                query = all_words.toArray(new String[all_words.size()]);

                res = search_query(query);
            }
        } else if(query[0].equals("-f")){
            if(query[2].equals("-q")){
                //Anwenden der ausgewaehlten Optionen auf den Topic Inhalt
                ArrayList<String> queryList = new ArrayList<String>(Arrays.asList(query));
                if (options.getInput().contains("-cf") || options.getInput().contains("-st"))	queryList = options.normalize(queryList);
                query = queryList.toArray(new String[queryList.size()]);

                res = search_query_in_folder(query);
            } else {
                String path = System.getProperty("user.dir") + "/resources/topics/" + query[3];

                File topic = new File(path);
                Index methods = new Index();

                //Anwenden der ausgewaehlten Optionen auf den Topic Inhalt
                ArrayList<String> useful_lines = methods.get_usefull_lines(topic);
                ArrayList<String> all_words     = methods.get_all_words(useful_lines);
                if (options.getInput().contains("-cf") || options.getInput().contains("-st"))	all_words = options.normalize(all_words);
                String[] allWords = all_words.toArray(new String[all_words.size()]);

                String[] resultQuery = new String[allWords.length + 2];
                resultQuery[0] = query[0];
                resultQuery[1] = query[1];
                System.arraycopy(allWords, 0, resultQuery, 2, resultQuery.length - 2);

                res = search_query_in_folder(resultQuery);
            }
        }

        return res ;
    }


	private String search_query(String[] query)
    {
        String result = "";

        HashMap<String, Double> ranking = new HashMap<String, Double>();
        HashSet<String> relevantDocuments = new HashSet<String>();

        //fuer jeders Wort in der Query
        for(int i = 1; i < query.length; ++i){
            //Die Dokumente der Posting List jedes Wortes das im Dictionary existiert werden dem "relevant Documents" Hashset hinzugefuegt, damit nicht in jedem Dokument gesucht werden muss
            //Fuer jedes weitere Wort werden dessen Dokumente hinzugefuegt
            //Hashset eliminiert Duplikate von alleine
            for (Map.Entry<String, Token> entry : dictionary.entrySet()) {
                if(entry.getKey().equals(query[i])){

                    //for(Map.Entry<String, Integer> doc : entry.getValue().get_posting_list().entrySet()){
                    //    relevantDocuments.add(doc.getKey());
                    //}

                    //Diese Methode macht das gleiche wie oben beschrieben bzw. ist einfach nur eine moderne Vereinfachung der for-Schleife
                    //Wenn du sie eventuelle nicht ausfuehren kannst (Java 7 oder hoeher is glaub ich gefordert, dann kommentiers aus und verwende die for-schleife
                    //Machen das gleiche
                    relevantDocuments.addAll(entry.getValue().get_posting_list().entrySet().stream().map(Map.Entry<String, Integer>::getKey).collect(Collectors.toList()));

                    //Hier findet eben fuer jedes relevante Dokument jetzt die Berechnung statt
                    for(String document : relevantDocuments){
                        double score;
                        //ich frage ob das wort auch wirklich in diesem dokument vorkommt, wenn ja berechne ich den Score wie in den Folien beschrieben
                        if(entry.getValue().get_posting_list().get(document) != null && (Math.log10(relevantDocuments.size() / entry.getValue().get_posting_list().size()) != 0))
                            //CALCULATION FOR SCORING METHOD
                            { score = Math.log(1 + entry.getValue().get_posting_list().get(document))*Math.log10(relevantDocuments.size() / entry.getValue().get_posting_list().size());
                        } else if ( entry.getValue().get_posting_list().get(document) != null && (Math.log10(relevantDocuments.size() / entry.getValue().get_posting_list().size()) == 0)){
                            score = Math.log(1 + entry.getValue().get_posting_list().get(document));
                        }else {
                            score = 0;
                        }
                        //Der Score wird hochsummiert, wie in der Folie beschrieben
                        if(ranking.containsKey(document)){
                            ranking.put(document, ranking.get(document)+ score);
                        } else {
                            ranking.put(document, score);
                        }
                    }
                }
            }
        }


        // All dies hier dient zur Auswahl der TOP 20
        ValueComparator bvc =  new ValueComparator(ranking);
        TreeMap<String,Double> sortedRanking = new TreeMap<String,Double>(bvc);
        sortedRanking.putAll(ranking);

        int i = 0;
            for (Map.Entry<String, Double> rank : sortedRanking.entrySet()) {
                result += rank.getKey() + " " + (i+1) + " " + rank.getValue() + "\n";
                ++i;
                if(i == 20) break;

            }

        return result;
    }


    //Siehe Beschreibung oben, ziemlich das Gleiche, nur dass die relevanten Dokumente nur die jenigen sind die in query[1] spezifiziert sind und dies ist genau der foldername
    private String search_query_in_folder(String[] query)
    {
        String result = "";

        HashMap<String, Double> ranking = new HashMap<String, Double>();
        HashSet<String> relevantDocuments = new HashSet<String>();

        for(int i = 2; i < query.length; ++i){
            for (Map.Entry<String, Token> entry : dictionary.entrySet()) {
                if(entry.getKey().equals(query[i])){
                    for(Map.Entry<String, Integer> doc : entry.getValue().get_posting_list().entrySet()){
                       if(doc.getKey().contains(query[1]))  relevantDocuments.add(doc.getKey());
                    }
                    for(String document : relevantDocuments){
                        double score;
                        if(entry.getValue().get_posting_list().get(document) != null && (Math.log10(relevantDocuments.size() / entry.getValue().get_posting_list().size()) != 0)){
                        //CALCULATION FOR SCORING METHOD
                            score = Math.log(1 + entry.getValue().get_posting_list().get(document))*Math.log10(relevantDocuments.size() / entry.getValue().get_posting_list().size());
                        } else if ( entry.getValue().get_posting_list().get(document) != null && (Math.log10(relevantDocuments.size() / entry.getValue().get_posting_list().size()) == 0)){
                            score = Math.log(1 + entry.getValue().get_posting_list().get(document));
                        } else {
                            score = 0;
                        }
                        if(ranking.containsKey(document)){
                            ranking.put(document, ranking.get(document)+ score);
                        } else {
                            ranking.put(document, score);
                        }
                    }
                }
            }
        }

        ValueComparator bvc =  new ValueComparator(ranking);
        TreeMap<String,Double> sortedRanking = new TreeMap<String,Double>(bvc);
        sortedRanking.putAll(ranking);

        int i = 0;
        for (Map.Entry<String, Double> rank : sortedRanking.entrySet()) {
            double score = rank.getValue() >= 0 ? rank.getValue() : 0;
            result += rank.getKey() + " " + (i+1) + " " + score + "\n";
            ++i;
            if(i == 20) break;

        }


        return result;
    }


    //innere Klasse, die notwendig ist fuer die TOP 20 Berechnung
    class ValueComparator implements Comparator<String> {

        Map<String, Double> base;
        public ValueComparator(Map<String, Double> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }

}
