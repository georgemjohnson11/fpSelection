/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;


import com.opencsv.CSVReader;
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author prash
 */
public class Utilities {
    
    
    

    public static ThreadLocal<Random> threadRandom(final long seed) {
        return new ThreadLocal<Random>(){
            @Override
            protected Random initialValue() {
                return new Random(seed);
            }
        };
    }
    
    public static int getRandom(int min, int max) {
        if (min == max) {
            return min;
        }
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        return randomNum;
    }

    public static int getRandom(int min, int max, ThreadLocal<Random> random){
        if (min == max) {
            return min;
        }
        int randomNum = random.get().nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static void printDebugStatement(String message){
        System.out.println("#########################################");
        System.out.println("######################" + message);
        System.out.println("#########################################");
    }
    
    //<editor-fold desc="OS Check">
    public static boolean isSolaris() {
        String os = System.getProperty("os.name");
        return isSolaris(os);
    }

    public static boolean isSolaris(String os) {
        if (os.toLowerCase().indexOf("sunos") >= 0) {
            return true;
        }
        return false;
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return isWindows(os);
    }

    public static boolean isWindows(String os) {
        if (os.toLowerCase().indexOf("win") >= 0) {
            return true;
        }
        return false;
    }

    public static boolean isLinux() {
        String os = System.getProperty("os.name");
        return isLinux(os);
    }

    public static boolean isLinux(String os) {
        if ((os.toLowerCase().indexOf("nix") >= 0) || (os.indexOf("nux") >= 0) || (os.indexOf("aix") > 0)) {
            return true;
        }
        return false;
    }
    
    public static boolean isMac() {
        String os = System.getProperty("os.name");
        return isMac(os);
    }

    public static boolean isMac(String os) {
        if (os.toLowerCase().indexOf("mac") >= 0) {
            return true;
        }
        return false;
    }
    //</editor-fold>  
    
    //<editor-fold desc="File and Directory checks">
    public static boolean makeDirectory(String filepath){
        File file = new File(filepath);
        return file.mkdir();
    }
    
    public static boolean validFilepath(String filepath){
        File file = new File(filepath);
        return file.exists();
    }
    
    public static boolean isDirectory(String filepath){
        File file = new File(filepath);
        return file.isDirectory();
    }
    
    
    public static String getFilepath() {
        String _filepath = Utilities.class.getClassLoader().getResource(".").getPath();
        if (_filepath.contains("/target/")) {
            _filepath = _filepath.substring(0, _filepath.lastIndexOf("/target/"));
        } else if (_filepath.contains("/src/")) {
            _filepath = _filepath.substring(0, _filepath.lastIndexOf("/src/"));
        } else if (_filepath.contains("/build/classes/")) {
            _filepath = _filepath.substring(0, _filepath.lastIndexOf("/build/classes/"));
        }
        if (Utilities.isWindows()) {
            try {
                _filepath = URLDecoder.decode(_filepath, "utf-8");
                _filepath = new File(_filepath).getPath();
                if (_filepath.contains("\\target\\")) {
                    _filepath = _filepath.substring(0, _filepath.lastIndexOf("\\target\\"));
                } else if (_filepath.contains("\\src\\")) {
                    _filepath = _filepath.substring(0, _filepath.lastIndexOf("\\src\\"));
                } else if (_filepath.contains("\\build\\classes\\")) {
                    _filepath = _filepath.substring(0, _filepath.lastIndexOf("\\build\\classes\\"));
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if (_filepath.contains("/target/")) {
                _filepath = _filepath.substring(0, _filepath.lastIndexOf("/target/"));
            } else if (_filepath.contains("/src/")) {
                _filepath = _filepath.substring(0, _filepath.lastIndexOf("/src/"));
            } else if (_filepath.contains("/build/classes/")) {
                _filepath = _filepath.substring(0, _filepath.lastIndexOf("/build/classes/"));
            }
        }
        return _filepath;
    }
    
    public static String getResourcesFilepath(){
        String filepath = getFilepath();
        filepath += getSeparater() + "resources" + getSeparater();
        return filepath;
    }
    
    public static String getCaseStudyFilepath(){
        String filepath = getFilepath();
        filepath += getSeparater() + "caseStudies" + getSeparater();
        return filepath;
    }
    
    
    //</editor-fold>
    
    //<editor-fold desc="File content">
    
    public static String getFileContentAsString(String filepath){
        String filecontent = "";
        
        File file = new File(filepath);
        try { 
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line= "";
            while((line=reader.readLine()) != null){
                filecontent += (line+"\n");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, "File at " + filepath + " not found.");
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filecontent;
    }
    
    public static List<String> getFileContentAsStringList(String filepath){
        List<String> filecontent = null;
        
        File file = new File(filepath);
        try { 
            BufferedReader reader = new BufferedReader(new FileReader(file));
            filecontent = new ArrayList<String>();
            String line= "";
            while((line=reader.readLine()) != null){
                filecontent.add(line);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, "File at " + filepath + " not found.");
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filecontent;
    }
    
    
    
    public static List<String[]> getCSVFileContentAsList(InputStream is){
        List<String[]> listPieces = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(is, "utf8")));
            String[] nextline;
            while( (nextline = reader.readNext()) != null ){
                listPieces.add(nextline);
            }
        }  catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
//        List<String> stringList = getFileContentAsStringList(filepath);
//        for(String line:stringList){
//            listPieces.add(line.split(","));
//        }
        return listPieces;
    }
    
    
    public static List<String[]> getCSVFileContentAsList(File f){
        List<String[]> listPieces = new ArrayList<String[]>();
        try {
            CSVReader reader = new CSVReader(new FileReader(f));
            String[] nextline;
            while( (nextline = reader.readNext()) != null ){
                listPieces.add(nextline);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
//        List<String> stringList = getFileContentAsStringList(filepath);
//        for(String line:stringList){
//            listPieces.add(line.split(","));
//        }
        return listPieces;
    }
    
    
    public static List<String[]> getCSVFileContentAsList(String filepath){
        List<String[]> listPieces = new ArrayList<String[]>();
        try {
            CSVReader reader = new CSVReader(new FileReader(filepath));
            String[] nextline;
            while( (nextline = reader.readNext()) != null ){
                listPieces.add(nextline);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
//        List<String> stringList = getFileContentAsStringList(filepath);
//        for(String line:stringList){
//            listPieces.add(line.split(","));
//        }
        return listPieces;
    }
    
    public static void writeToFile(String filepath, String content){
        File file = new File(filepath);
        try {
            FileWriter fr = new FileWriter(file);
            BufferedWriter br = new BufferedWriter(fr);
            br.write(content);
            br.flush();
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void plotToFile(JavaPlot plot, String filepath){
        
        ImageTerminal png = new ImageTerminal();
        File file = new File(filepath);
        
        try {
            file.createNewFile();
            png.processOutput(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            System.err.print("File " + filepath + " not found.\n");
            System.err.print(ex);
        } catch (IOException ex) {
            System.err.print(ex);
        }
        
        plot.setPersist(false);
        plot.setTerminal(png);
        plot.plot();
        
        try {
            ImageIO.write(png.getImage(), "png", file);
        } catch (IOException ex) {
            System.err.print(ex);
        }
    } 
    
    public static void writeToFile(String filepath, List<String> lines){
        File file = new File(filepath);
        try {
            FileWriter fr = new FileWriter(file);
            BufferedWriter br = new BufferedWriter(fr);
            for(String line:lines){
                br.write(line);
                br.newLine();
            }
            
            br.flush();
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static char getSeparater(){
        if(Utilities.isWindows()){
            return '\\';
        }
        return '/';
    }
    
    //</editor-fold>
    
    
    public static void runPythonScript(String filepath) throws InterruptedException, IOException {
        StringBuilder commandBuilder = null;
        if (Utilities.isLinux()) {
            commandBuilder = new StringBuilder("/home/prash/anaconda2/bin/python " + filepath);
        } else {
            commandBuilder = new StringBuilder("python \"" + filepath + "\"");
        }
        String command = commandBuilder.toString();
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        proc.waitFor();
        InputStream is = proc.getInputStream();
        InputStream es = proc.getErrorStream();
        OutputStream os = proc.getOutputStream();
        is.close();
        es.close();
        os.close();
    }
    
    
}
