package environment;

import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class MapFileReader {
    private String[][] map;

    MapFileReader(String fileName){
        csv2Map(fileName);
    }

    String[][] getMap(){
        return map;
    }

    private String[][] transpose(String[][] mat) {
        String[][] transposed = new String[mat[0].length][mat.length];
        for(int i = 0; i< mat.length;i++){
            for(int j = 0; j<mat[i].length;j++){
                transposed[j][i] = mat[i][j];
            }
        }
        return transposed;
    }

    private void csv2Map(String fileName){
        try {
            CSVReader reader = new CSVReaderBuilder(new FileReader((fileName)))
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .build();
            List<String[]>lines = reader.readAll();
            for(int i = 0; i< lines.size();i++){
                String[] strs = new String[lines.get(i).length];
                for(int j = 0; j< lines.get(i).length;j++){
                    String s = lines.get(i)[j];
                    strs[j] = (s == null) ? "" : s;
                }
                lines.set(i,strs);
            }
            map = transpose(lines.toArray(new String[][]{}));
        } catch (IOException e){
            System.err.println(e);
        }
    }
}