/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implement;

import configuration.ControllerLog;
import configuration.DependencyConfig;
import entity.EntityConfig;
import helper.Helper;
import helper.Utilities;
import interfc.InterfcSpreadSheetHtb;
import org.apache.commons.lang.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import service.sqlite.SpreadSheetHtbService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Hasan
 */
@Service
@Scope("prototype")
public class ImpleSpreadSheetHtb implements InterfcSpreadSheetHtb {

    private final SpreadSheetHtbService SpreadSheetHtbService;
    EntityConfig entityConfig;
    Helper hlp;
    private final Utilities utilities;
    private final ControllerLog controllerLog;

    @Autowired
    public ImpleSpreadSheetHtb(DependencyConfig dependencyConfig, Utilities utilities, Helper helper, SpreadSheetHtbService SpreadSheetHtbService) {
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.controllerLog = dependencyConfig.getControllerLog();
        this.utilities = utilities;
        this.hlp = helper;
        this.SpreadSheetHtbService = SpreadSheetHtbService;
    }

    public List sheelExec(String cmd, String identifier) {
        HashMap<String, Object> jObjData = new HashMap<>();
        List listData = new ArrayList();
        try {
            if (!SystemUtils.IS_OS_LINUX) {
                if (identifier.equals("rate")) {
                    int min = 0; // Minimum value of range
                    int max = 51200; // Maximum value of range
                    int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
                    listData.add(random_int + "Kbit");
                } else {
                    int min = 0; // Minimum value of range
                    int max = 512000000; // Maximum value of range
                    int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
                    listData.add(random_int + "GB");
                }
            } else {
                //get download rate
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("bash", "-c", cmd);
                Process process = processBuilder.start();
                process.waitFor();
                StringBuilder output = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    //output.append(line + "\n");
                    listData.add(line);
                }
                int exitVal = process.waitFor();
                //simulation if something wrong in tc
                if(listData.size() == 0){
                    if (identifier.equals("rate")) {
                        int min = 0; // Minimum value of range
                        int max = 51200; // Maximum value of range
                        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
                        listData.add(random_int + "Kbit");
                    } else {
                        int min = 0; // Minimum value of range
                        int max = 512000000; // Maximum value of range
                        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
                        listData.add(random_int + "GB");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }

    public String parsingRate(String rate) {
        double totalUpDbl = Double.parseDouble("0");
        String rateOri = rate;
        if (rate.contains("Mbit")) {
            rate = rate.replace("Mbit", "");
            totalUpDbl = Double.parseDouble(rate);
        } else if (rate.contains("Kbit")) {
            rate = rate.replace("Kbit", "");
            totalUpDbl = Double.parseDouble(rate) / Double.parseDouble("1000");
        } else if (rate.contains("bit")) {
            rate = rate.replace("bit", "");
            totalUpDbl = Double.parseDouble(rate) / Double.parseDouble("1000") / Double.parseDouble("1000");
        } else {
            //total download or upload bytes
            rate = rate.replace("GB", "");
            totalUpDbl = Double.parseDouble(rate) / Double.parseDouble("1000") / Double.parseDouble("1000") / Double.parseDouble("1000");
        }
        String totalUpDblStr = "0";

        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(2);
        totalUpDblStr = df.format(totalUpDbl);
        if (String.valueOf(totalUpDblStr.charAt(0)).equals(".")) {
            totalUpDblStr = "0" + totalUpDblStr;
        }

        if (rateOri.contains("bit")) {
            totalUpDblStr = totalUpDblStr + "Mbit";
        } else {
            totalUpDblStr = totalUpDblStr + "GB";
        }

        return totalUpDblStr;
    }

    @Override
    public HashMap getRootRateDownload(HashMap objRoot) {
        //get download rate
        String cmd = "tc -s class show dev ifb1 | grep -A4 \"htb " + objRoot.get("class_id") + " root\" | tail -n +3 | sed '2,3d' | cut -d' ' -f3";
        List listData = this.sheelExec(cmd, "rate");
        HashMap<String, Object> objData = new HashMap<>();
        if (listData.size() > 0) {
            String totalUpDblStr = this.parsingRate(listData.get(0).toString());
            objData.put("rate_download", totalUpDblStr);
        } else {
            objData.put("rate_download", "0Kbit");
        }
        return objData;
    }

    @Override
    public HashMap getChildrenRateDownload(HashMap objRoot) {
        String cmd = "tc -s class show dev ifb1 | grep -A4 \"" + objRoot.get("class_id") + " parent " + objRoot.get("parent_id") + "\" | tail -n +3 | sed '2,3d' | cut -d' ' -f3";
        List listData = this.sheelExec(cmd, "rate");
        HashMap<String, Object> objData = new HashMap<>();
        if (listData.size() > 0) {
            String totalUpDblStr = this.parsingRate(listData.get(0).toString());
            objData.put("rate_download", totalUpDblStr);
        } else {
            objData.put("rate_download", "0Kbit");
        }
        return objData;
    }

    @Override
    public HashMap getRootRateUpload(HashMap objRoot) {
        String cmd = "tc -s class show dev ifb0 | grep -A4 \"htb " + objRoot.get("class_id") + " root\" | tail -n +3 | sed '2,3d' | cut -d' ' -f3";
        List listData = this.sheelExec(cmd, "rate");
        HashMap<String, Object> objData = new HashMap<>();
        if (listData.size() > 0) {
            String totalUpDblStr = this.parsingRate(listData.get(0).toString());
            objData.put("rate_upload", totalUpDblStr);
        } else {
            objData.put("rate_upload", "0Kbit");
        }
        return objData;
    }

    @Override
    public HashMap getChildrenRateUpload(HashMap objDataTier1) {
        String cmd = "tc -s class show dev ifb0 | grep -A4 \"" + objDataTier1.get("class_id") + " parent " + objDataTier1.get("parent_id") + "\" | tail -n +3 | sed '2,3d' | cut -d' ' -f3";
        List listData = this.sheelExec(cmd, "rate");
        HashMap<String, Object> objData = new HashMap<>();
        if (listData.size() > 0) {
            String totalUpDblStr = this.parsingRate(listData.get(0).toString());
            objData.put("rate_upload", totalUpDblStr);
        } else {
            objData.put("rate_upload", "0Kbit");
        }
        return objData;
    }

    @Override
    public HashMap getRootTotalDownload(HashMap objRoot) {
        //get download rate
        String cmd = "tc -s class show dev ifb1 | grep -A4 \"htb " + objRoot.get("class_id") + " root\" | tail -n +2 | sed '2,4d' | cut -d' ' -f3";
        List listData = this.sheelExec(cmd, "total");
        HashMap<String, Object> objData = new HashMap<>();
        if (listData.size() > 0) {
            String totalUpDblStr = this.parsingRate(listData.get(0).toString());
            objData.put("total_download", totalUpDblStr);
        } else {
            objData.put("total_download", "0GB");
        }
        return objData;
    }

    @Override
    public HashMap getChildrenTotalDownload(HashMap objRoot) {
        String cmd = "tc -s class show dev ifb1 | grep -A4 \"" + objRoot.get("class_id") + " parent " + objRoot.get("parent_id") + "\" | tail -n +2 | sed '2,4d' | cut -d' ' -f3";
        List listData = this.sheelExec(cmd, "total");
        HashMap<String, Object> objData = new HashMap();
        if (listData.size() > 0) {
            String totalUpDblStr = this.parsingRate(listData.get(0).toString());
            objData.put("total_download", totalUpDblStr);
        } else {
            objData.put("total_download", "0GB");
        }
        return objData;
    }

    @Override
    public HashMap getRootTotalUpload(HashMap objRoot) {
        String cmd = "tc -s class show dev ifb0 | grep -A4 \"htb " + objRoot.get("class_id") + " root\" | tail -n +2 | sed '2,4d' | cut -d' ' -f3";
        List listData = this.sheelExec(cmd, "total");
        HashMap<String, Object> objData = new HashMap();
        if (listData.size() > 0) {
            String totalUpDblStr = this.parsingRate(listData.get(0).toString());
            objData.put("total_upload", totalUpDblStr);
        } else {
            objData.put("total_upload", "0GB");
        }
        return objData;
    }

    @Override
    public HashMap getChildrenTotalUpload(HashMap objDataTier1) {
        String cmd = "tc -s class show dev ifb0 | grep -A4 \"" + objDataTier1.get("class_id") + " parent " + objDataTier1.get("parent_id") + "\" | tail -n +2 | sed '2,4d' | cut -d' ' -f3";
        List listData = this.sheelExec(cmd, "total");
        HashMap<String, Object> objData = new HashMap<>();
        if (listData.size() > 0) {
            String totalUpDblStr = this.parsingRate(listData.get(0).toString());
            objData.put("total_upload", totalUpDblStr);
        } else {
            objData.put("total_upload", "0GB");
        }
        return objData;
    }
}
