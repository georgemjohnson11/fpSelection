/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import static org.cidarlab.fpSelection.adaptors.ScrapedCSVParse.generateFPs;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import static org.cidarlab.fpSelection.adaptors.fpSelectionAdaptor.uploadFluorescenceSpectrums;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.selectors.ProteinSelector;

/**
 *
 * @author david
 */
public class SelectionTest {

    public static void main(String[] args) throws IOException {
//        File input = new File("src/main/resources/fp_spectra.csv");
//        HashMap<String, Fluorophore> spectralMaps = uploadFluorescenceSpectrums(input);

        File input = new File("src/main/resources/ScrapedCSVs/");
        HashMap<String,Fluorophore> spectralMaps = generateFPs(input);

        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer testCyto = fpFortessaParse.parseFortessa(cyto);
        Laser testLaser = testCyto.getLasers().getFirst();

        System.out.println(spectralMaps.isEmpty());
        System.out.println(testCyto.getSheathPressure());

        ProteinSelector.laserFiltersToFPs(spectralMaps, testLaser);

    }

}
