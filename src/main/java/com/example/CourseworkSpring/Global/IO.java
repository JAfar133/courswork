package com.example.CourseworkSpring.Global;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.isParsable;

public class IO {
    //Из файла в List
    public static List<String> inpLines(File file) throws IOException{
        List<String> lines = new ArrayList<>();
        String line;
        BufferedReader inp = null;
        try {
            inp = new BufferedReader(new FileReader(file));
            while((line=inp.readLine())!=null){
                line=line.trim();
                if(line.equals(""))continue;
                lines.add(line);
            }
        }catch (IOException e){
            return null;
        }
        finally {
            if(inp!=null)inp.close();
        }
        if(lines.isEmpty())return null;
        return lines;
    }
    //Из списка строк в файл
    public static boolean outpLines(File file,List<String> lines) throws IOException {
        PrintWriter out = null;
        if ((lines==null)||lines.isEmpty()) return false;
        try {
            out = new PrintWriter(new FileWriter(file));
            int n = lines.size();
            for (int i = 0; i < n; i++) {
                out.println(lines.get(i).trim());
            }
        }catch (IOException e){
            return false;
        }
        finally {
            if(out!=null)out.close();
        }
        return true;
    }
    //Запись в файл
    public static boolean textToFile(File file, String text) throws IOException {
            String[] arrayLines = text.split("\r\n");
            List<String> stringList = new ArrayList<>();
            for (int i = 0; i < arrayLines.length; i++) {
                String words[]=arrayLines[i].trim().split(",");
                if(words.length!=3||!isParsable(words[0])||!isParsable(words[2])) continue;
                stringList.add(arrayLines[i]);
            }
            outpLines(file,stringList);
        return true;
    }
}
