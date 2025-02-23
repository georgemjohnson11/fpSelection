/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cidarlab.fpSelection.algorithms.HillClimbingSelection;
import org.cidarlab.fpSelection.GUI.PlotAdaptor;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.json.JSONObject;

/**
 *
 * @author Alex
 */
@MultipartConfig
public class HillClimbingServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /////////////////////////////////////////
        // Parse CSVs and turn them into Files //
        /////////////////////////////////////////
        String errMsg = "";
        InputStream fpInput;
        InputStream cytoInput;
        InputStream brightnessInput;
        
        try {
            fpInput = request.getPart("FPMasterList").getInputStream();
            cytoInput = request.getPart("cytometer").getInputStream();
            brightnessInput = request.getPart("FPBrightness").getInputStream();
            
        } catch (Exception e) {
            errMsg += "Error downloading CSV's: Error " + e.toString() + " \n ";
            PrintWriter writer = response.getWriter();
            JSONObject result = new JSONObject();
            result.put("SNR", errMsg);
            writer.println(result);
            writer.flush();
            writer.close();
            return;
        }
        int n = Integer.parseInt(new BufferedReader(new InputStreamReader(request.getPart("n").getInputStream())).readLine());
        
        /////////////////////
        // Parse the files //
        /////////////////////
        Map<String, Fluorophore> spectralMaps = null;
        Cytometer cytoSettings = null;
        try {
            spectralMaps = fpSpectraParse.parse(fpInput);
            fpSpectraParse.addBrightness(brightnessInput, spectralMaps);
            
            cytoSettings = fpFortessaParse.parse(cytoInput, false);
            
        } catch (Exception x) {
            errMsg += "CSVs formatted incorrectly or unreadable: Error " + x.toString() + " \n ";
            x.printStackTrace();          
            PrintWriter writer = response.getWriter();
            JSONObject result = new JSONObject();
            result.put("SNR", errMsg);
            writer.println(result);
            writer.flush();
            writer.close();
            return;
        }
        
        fpInput.close();
        cytoInput.close();

        ////////////////////////////////////////////
        // Parse the rest of the request variables//
        ////////////////////////////////////////////
        
        List<SelectionInfo> solution = HillClimbingSelection.run(n, spectralMaps, cytoSettings);

        LinkedList<String> info = PlotAdaptor.webPlot(solution);

        PrintWriter writer = response.getWriter();
        JSONObject result = new JSONObject();

        result.put("img", info.get(0));
        result.put("SNR", info.get(1));
        writer.println(result);
        writer.flush();
        writer.close();
    }
}
