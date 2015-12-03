import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Vede on 28.11.2015.
 */
public class Dictionary {

    private HashMap<String, Token> dictionary;
    private ArrayList<File> allDocuments;
    private int totalDocumentCount;

    public Dictionary(HashMap<String, Token> dictionary){
        this.dictionary = dictionary;
        this.totalDocumentCount = 0;
        for (Map.Entry<String, Token> entry : dictionary.entrySet()) {
            totalDocumentCount += 1;
        }
    }

    public Dictionary(){
        this.dictionary = new HashMap<String, Token>();
        this.allDocuments = new ArrayList<File>();
        this.totalDocumentCount = 0;
    }

    public void addToDict(Token token){
        this.dictionary.put(token.get_term(), token);
        this.totalDocumentCount += 1;
    }

    public void addDocument(File document){
        this.allDocuments.add(document);
    }

    public HashMap<String, Token> getDict(){
        return this.dictionary;
    }

    public Token getToken(String word){
        return this.dictionary.get(word);
    }

    public boolean contains(Token token){
        return this.dictionary.get(token.get_term()) != null;
    }



    public String search(String[] query, Options options){
        String res = "";

        if(query[0].equals("exit")) res = "exit";

        else if(!query[0].equals("-f")){
            if(query[0].equals("-q")){

                ArrayList<String> queryList = new ArrayList<String>(Arrays.asList(query));
                if (options.getInput().contains("-cf") || options.getInput().contains("-st"))	queryList = options.normalize(queryList);
                query = queryList.toArray(new String[queryList.size()]);

                res = search_query(query);
            } else {

                String path = System.getProperty("user.dir") + "/resources/topics/" + query[1];

                File topic = new File(path);
                Index methods = new Index();

                ArrayList<String> useful_lines = methods.get_usefull_lines(topic);
                ArrayList<String> all_words     = methods.get_all_words(useful_lines);
                if (options.getInput().contains("-cf") || options.getInput().contains("-st"))	all_words = options.normalize(all_words);
                query = all_words.toArray(new String[all_words.size()]);

                res = search_query(query);
            }
        } else if(query[0].equals("-f")){
            if(query[2].equals("-q")){
                ArrayList<String> queryList = new ArrayList<String>(Arrays.asList(query));
                if (options.getInput().contains("-cf") || options.getInput().contains("-st"))	queryList = options.normalize(queryList);
                query = queryList.toArray(new String[queryList.size()]);

                res = search_query_in_folder(query);
            } else {
                String path = System.getProperty("user.dir") + "/resources/topics/" + query[3];

                File topic = new File(path);
                Index methods = new Index();

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

        for(int i = 1; i < query.length; ++i){
            for (Map.Entry<String, Token> entry : dictionary.entrySet()) {
                if(entry.getKey().equals(query[i])){
                    //for(Map.Entry<String, Integer> doc : entry.getValue().get_posting_list().entrySet()){
                    //    relevantDocuments.add(doc.getKey());
                    //}
                    relevantDocuments.addAll(entry.getValue().get_posting_list().entrySet().stream().map(Map.Entry<String, Integer>::getKey).collect(Collectors.toList()));

                    for(String document : relevantDocuments){
                        double score;
                        if(entry.getValue().get_posting_list().get(document) != null){ score = Math.log(1 + entry.getValue().get_posting_list().get(document))*Math.log10(totalDocumentCount / entry.getValue().get_posting_list().size());
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
                result += rank.getKey() + " " + (i+1) + " " + rank.getValue() + "\n";
                ++i;
                if(i == 20) break;

            }

        return result;
    }


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
                    //relevantDocuments.addAll(entry.getValue().get_posting_list().entrySet().stream().map(Map.Entry<String, Integer>::getKey).collect(Collectors.toList()));
                    for(String document : relevantDocuments){
                        double score;
                        if(entry.getValue().get_posting_list().get(document) != null){ score = Math.log(1 + entry.getValue().get_posting_list().get(document))*Math.log10(totalDocumentCount / entry.getValue().get_posting_list().size());
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
            result += rank.getKey() + " " + (i+1) + " " + rank.getValue() + "\n";
            ++i;
            if(i == 20) break;

        }


        return result;
    }


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
