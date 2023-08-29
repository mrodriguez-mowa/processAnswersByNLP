package pe.com.nlp.classifier.tools;

import java.util.HashMap;

public class NlpLabeler {
    public int NlpLabelerHash(String stringLabel) {
        HashMap<String, Integer> nlpHash = new HashMap<>();

        nlpHash.put("ASESORAMIENTO", 1);
        nlpHash.put("COMPROMISO", 2);
        nlpHash.put("CONFIRMADOS", 3);
        nlpHash.put("NO APLICA", 4);
        nlpHash.put("NO DESEADO", 5);
        nlpHash.put("PREVENCIÓN", 6);
        nlpHash.put("RENUENTE", 7);
        nlpHash.put("VERIFICACIÓN DE NÚMERO", 8);

        return nlpHash.get(stringLabel);
    }
}
