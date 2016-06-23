/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import static org.cidarlab.fpSelection.adaptors.ScrapedCSVParse.generateFPs;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import static org.cidarlab.fpSelection.adaptors.fpSelectionAdaptor.uploadFluorescenceSpectrums;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.WrappedFluorophore;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.cidarlab.fpSelection.selectors.SelectionInfo;

/**
 *
 * @author Alex
 */
public class SomewhatExhaustiveSelectionTest {

    public static LinkedList<int[]> filterCombinations;
    public static LinkedList<int[]> fluorophorePermutations;

    public static void main(String[] args) throws IOException {
        
        //Get fluorophore set
        //File input = new File("src/main/resources/fp_spectra.csv");
        //HashMap<String, Fluorophore> spectralMaps = uploadFluorescenceSpectrums(input);
        File input = new File("src/main/resources/Fluorophores.org/");
        HashMap<String, Fluorophore> spectralMaps = generateFPs(input);

        //Get cytometer settings
        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer cytometer = fpFortessaParse.parseFortessa(cyto);

        //User input number of FPs
        //String numString = JOptionPane.showInputDialog("Input an integer n for the number of FPs you'd like");
        //int n = Integer.parseInt(numString);
        int n = 6;
        
        double topPercent = .005;
                
        //count fluorophores
        int numFluorophores = spectralMaps.size();
        
        //count filters
        int numFilters = 0;
        for (Laser laser : cytometer.lasers) {
            numFilters += laser.detectors.size();
        }
        
        //preprocess data structures
        
        //filter index --> fluorophore index --> wrapped fluorophore
        WrappedFluorophore[][] ranked = new WrappedFluorophore[numFilters][numFluorophores];
        //filter index --> fluorophore index --> riemann sum
        double[][] riemannSum = new double[numFilters][numFluorophores];
        int filterIndex = 0;
        for (Laser laser : cytometer.lasers) {
            for (Detector detector : laser.detectors) {
                int fluorophoreIndex = 0;
                for (Map.Entry<String, Fluorophore> entry : spectralMaps.entrySet()) {
                    Fluorophore fluorophore = entry.getValue();
                    ranked[filterIndex][fluorophoreIndex] = new WrappedFluorophore();
                    ranked[filterIndex][fluorophoreIndex].index = fluorophoreIndex;
                    ranked[filterIndex][fluorophoreIndex].fluorophore = fluorophore;
                    ranked[filterIndex][fluorophoreIndex].detector = detector;
                    ranked[filterIndex][fluorophoreIndex].laser = laser;
                    double express = fluorophore.express(laser, detector);
                    riemannSum[filterIndex][fluorophoreIndex] = express;
                    ranked[filterIndex][fluorophoreIndex].riemannSum = express;
                    fluorophoreIndex++;
                }
                filterIndex++;
            }
        }
        
        //sort each list of fluorophores for each filter by highest riemann sum
        for(int i = 0; i < numFilters; i++)
        {
            Arrays.sort(ranked[i]);   
        }

        //get all combinations of filters (order not important)
        filterCombinations = new LinkedList<>();
        int tempData[] = new int[n];
        getCombinations(tempData, 0, numFilters - 1, 0, n); 
        
        //get all permutations of fluorophores to match to filters (order is important)
        fluorophorePermutations = new LinkedList<>();
        tempData = new int[n];
        getPermutations(tempData, (int)(numFluorophores * topPercent), n);
        
        //iterate through "topPercent" of all possible combinations of filters/fluorophores
        double bestSignal = 0;
        int[] bestFilters = new int[n];
        int[] bestFluorophores = new int[n];
        int totalComputations = filterCombinations.size() * fluorophorePermutations.size();
        int onePercent = (int)(totalComputations * .01);
        int computationIndex = 0;
        int percent = 0;
        for (int[] filterCombo : filterCombinations)
        {
            for (int[] fluorophorePerm : fluorophorePermutations)
            {
                if(++computationIndex % onePercent == 0) System.out.println(++percent + " percent");
                double signal = 0;
                for (int i = 0; i < n; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        //desired signal
                        if (i == j) signal += riemannSum[filterCombo[i]][ranked[filterCombo[i]][fluorophorePerm[j]].index];
                        //undesired noise
                        else signal -= riemannSum[filterCombo[i]][ranked[filterCombo[j]][fluorophorePerm[j]].index];
                    }
                }
                if (signal > bestSignal)
                {
                    bestSignal = signal;
                    bestFilters = filterCombo;
                    bestFluorophores = fluorophorePerm;
                }
            }
        }

        //prepare data for graphs
        ArrayList<SelectionInfo> selected = new ArrayList<>();
        for (int i = 0; i < n; i++)
        {
            SelectionInfo si = new SelectionInfo();
            si.rankedFluorophores = new ArrayList<>();
            si.rankedFluorophores.add(ranked[bestFilters[i]][bestFluorophores[i]].fluorophore);
            si.selectedIndex = 0;
            si.selectedDetector = ranked[bestFilters[i]][bestFluorophores[i]].detector;
            si.selectedLaser = ranked[bestFilters[i]][bestFluorophores[i]].laser;
            selected.add(si);
        }
        ProteinSelector.calcSumSNR(selected);
        ProteinSelector.generateNoise(selected);
        
        ProteinSelector.plotSelection(selected);
    }
    
    static void getCombinations(int data[], int start, int n, int index, int k) {
        if (index == k) {
            filterCombinations.add(data.clone());
            return;
        }
        for (int i = start; i <= n && n - i + 1 >= k - index; i++) {
            data[index] = i;
            getCombinations(data, i + 1, n, index + 1, k);
        }
    }
    static void getPermutations(int data[], int n, int k) {
        if (k == 0) {
            fluorophorePermutations.add(data.clone());
            return;
        }      
        for (int i = 0; i < n; ++i) {
            data[k - 1] = i;
            getPermutations(data, n, k - 1);
        }
    }
}
