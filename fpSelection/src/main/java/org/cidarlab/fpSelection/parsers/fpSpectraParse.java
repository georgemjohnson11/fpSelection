/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cidarlab.fpSelection.dom.Fluorophore;
import java.util.regex.Pattern;
import java.util.TreeMap;
import org.cidarlab.fpSelection.Utilities;

/**
 *
 * @author Alex
 */
public class fpSpectraParse {

//    public static void main(String args[]) throws FileNotFoundException, Exception {
//        File input = new File("src/main/resources/fp_spectra.csv");
//        HashMap<String, Fluorophore> spectralMaps = parse(input);
//    }

    /*
     * This method is for uploading fluorescence spectrum data to be associated with Fluorphore objects
     */
    public static final Pattern p = Pattern.compile(" \\((EX|EM|AB)\\)"); //regex pattern to remove (EX),(EM), and (AB)
    
    public static Map<String, Fluorophore> parse(InputStream input) throws FileNotFoundException, IOException {

        //Import file, begin reading
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf8"));
        HashMap<String, Fluorophore> spectralMaps = new HashMap<>();

        //The first line describes the spectra
        String line = reader.readLine();
        String[] spectra = line.split(",");
        
        String[] spectraTrimmedStrings = new String[spectra.length];
        int numSpectra = spectra.length;
        for (int i = 1; i < numSpectra; i++) {
            Fluorophore f = new Fluorophore(); 

            spectraTrimmedStrings[i] = p.matcher(spectra[i]).replaceAll(""); //remove above regex pattern
            f.name = spectraTrimmedStrings[i];
            f.EMspectrum = new TreeMap<>();
            f.EXspectrum = new TreeMap<>();
            spectralMaps.put(spectraTrimmedStrings[i], f);
        }
        line = reader.readLine();

        //Read each line of the input file to parse parts
        double k, v;
        while (line != null) {
            String[] tokens = line.split(",");
            for (int j = 1; j < tokens.length; j++) {
                if (!tokens[j].isEmpty()) {
                    if (spectra[j].contains("EX") || spectra[j].contains("AB")) {

                        k = Double.parseDouble(tokens[0]);
                        v = Double.parseDouble(tokens[j]) * 100;

                        spectralMaps.get(spectraTrimmedStrings[j]).EXspectrum.put(k, v);

                    } else if (spectra[j].contains("EM")) {
                        k = Double.parseDouble(tokens[0]);
                        v = Double.parseDouble(tokens[j]) * 100;

                        spectralMaps.get(spectraTrimmedStrings[j]).EMspectrum.put(k, v);

                    }
                }
            }
            line = reader.readLine();
        }
        return spectralMaps;
    }
    
    public static Map<String, Fluorophore> parse(String filepath) throws IOException{
        File f = new File(filepath);
        return parse(f);
    }
    
    /*
    public static void adjustBrightness(File brightness, Map<String,Fluorophore> fps){
        List<String[]> lines = Utilities.getCSVFileContentAsList(brightness);
        adjustBrightness(lines,fps);

    }

    private static void adjustBrightness(List<String[]> lines, Map<String,Fluorophore> fps){
        double maxEC = Double.valueOf(lines.get(0)[2]);
        String normalized = lines.get(0)[0];
        for(String[] line:lines){
            double ec = Double.valueOf(line[2]);
            String normal = line[0];
            if(ec > maxEC){
                maxEC = ec; 
                normalized = normal;
            }
        }
        
        for(String[] line:lines){
            double value = (Double.valueOf(line[2]))/maxEC;
            String fp = line[0];
            fps.get(fp).setEc(value);
            fps.get(fp).setEcNormalizedTo(normalized);
            fps.get(fp).setQy(Double.valueOf(line[3]));
        }
    }*/

    public static void addBrightness(File brightness, Map<String, Fluorophore> fps){
        List<String[]> lines = Utilities.getCSVFileContentAsList(brightness);
        addBrightness(lines,fps);
    }
    
    public static void addBrightness(InputStream brightness, Map<String,Fluorophore> fps){
        List<String[]> lines = Utilities.getCSVFileContentAsList(brightness);
        addBrightness(lines,fps);
    }
    
    private static void addBrightness(List<String[]> lines, Map<String,Fluorophore> fps){
        double maxBrightness = Double.valueOf(lines.get(0)[1]);
        String normalized = lines.get(0)[0];
        for(String[] line:lines){
            double bright = Double.valueOf(line[1]);
            String normal = line[0];
            if(bright > maxBrightness){
                maxBrightness = bright; 
                normalized = normal;
            }
        }
        
        for(String[] line:lines){
            double value = (Double.valueOf(line[1]))/maxBrightness;
            String fp = line[0];
            fps.get(fp).setBrightness(value);
            fps.get(fp).setBrightnessNormalizedTo(normalized);
        }
    }
    
    
    public static Map<String, Fluorophore> parse(File input) throws FileNotFoundException, IOException {

        //Import file, begin reading
        BufferedReader reader = new BufferedReader(new FileReader(input.getAbsolutePath()));
        HashMap<String, Fluorophore> spectralMaps = new HashMap<>();

        //The first line describes the spectra
        String line = reader.readLine();
        
        String[] spectra = line.split(",");
        
        String[] spectraTrimmedStrings = new String[spectra.length];
        int numSpectra = spectra.length;
        for (int i = 1; i < numSpectra; i++) {
            Fluorophore f = new Fluorophore(); 

            spectraTrimmedStrings[i] = p.matcher(spectra[i]).replaceAll(""); //remove above regex pattern
            f.name = spectraTrimmedStrings[i];
            f.EMspectrum = new TreeMap<>();
            f.EXspectrum = new TreeMap<>();
            spectralMaps.put(spectraTrimmedStrings[i], f);
        }
        line = reader.readLine();

        //Read each line of the input file to parse parts
        double k, v;
        while (line != null) {
            String[] tokens = line.split(",");
            for (int j = 1; j < tokens.length; j++) {
                if (!tokens[j].isEmpty()) {
                    if (spectra[j].contains("EX") || spectra[j].contains("AB")) {

                        k = Double.parseDouble(tokens[0]);
                        v = Double.parseDouble(tokens[j]) * 100;

                        spectralMaps.get(spectraTrimmedStrings[j]).EXspectrum.put(k, v);

                    } else if (spectra[j].contains("EM")) {
                        k = Double.parseDouble(tokens[0]);
                        v = Double.parseDouble(tokens[j]) * 100;

                        spectralMaps.get(spectraTrimmedStrings[j]).EMspectrum.put(k, v);

                    }
                }
            }
            line = reader.readLine();
        }
        return spectralMaps;
    }
}
