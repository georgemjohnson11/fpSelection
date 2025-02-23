/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.cidarlab.fpSelection.algorithms.SemiExhaustiveSelection;
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
public class SemiExhaustiveServlet extends HttpServlet {

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
        try {
            fpInput = request.getPart("FPMasterList").getInputStream();
            cytoInput = request.getPart("cytometer").getInputStream();
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
        double topPercent = Double.parseDouble(new BufferedReader(new InputStreamReader(request.getPart("topPercent").getInputStream())).readLine());
        topPercent *= .01;

        /////////////////////
        // Parse the files //
        /////////////////////
        Map<String, Fluorophore> spectralMaps = null;
        Cytometer cytoSettings = null;
        try {
            spectralMaps = fpSpectraParse.parse(fpInput);
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

        ///////////////////////
        // Search and Return //
        ///////////////////////
        ArrayList<SelectionInfo> selected = SemiExhaustiveSelection.run(n, spectralMaps, cytoSettings, topPercent);

        LinkedList<String> info = PlotAdaptor.webPlot(selected);

        PrintWriter writer = response.getWriter();
        JSONObject result = new JSONObject();

        result.put("img", info.get(0));
        result.put("SNR", info.get(1));
        writer.println(result);
        writer.flush();
        writer.close();
    }
}
