package controller;

import configuration.ControllerEncDec;
import configuration.ControllerLog;
import configuration.DependencyConfig;
import configuration.FreeMarkerConfig;
import entity.EntityConfig;
import helper.CookieHelperDb;
import helper.Helper;
import helper.Utilities;
import implement.ImpleSpreadSheetLogin;
import interfc.InterfcSpreadSheetLogin;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.ehcache.Cache;
import org.apache.poi.ss.usermodel.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import service.SpreadSheetAccountService;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("ControllerSpreadSheet")
@Scope("prototype")
public class ControllerSpreadSheet {
    // Cache simpel untuk simpan workbook di RAM
    private static final Map<String, Workbook> workbookCache = new ConcurrentHashMap<>();
    private final EntityConfig entityConfig;
    private final InterfcSpreadSheetLogin service;
    private final SpreadSheetAccountService SpreadSheetAccountService;
    private final Helper hlp;
    private final Utilities utilities;
    private final ControllerLog controllerLog;
    private final ControllerEncDec encDec;
    private final CookieHelperDb cookieHelper;
    private final FreeMarkerConfig freeMarkerCfg;
    private final Cache cacheCookies;
    private final Cache cacheMaster;
    private ResponseEntity<StreamingResponseBody> response;
    private String fullUri, fullUrl, query, webroot, controller, methode, view, webRootUrl, baseUrl, hostPort;
    private HashMap objParams;
    private Cookie cookie;
    private HashMap objRequestProperties;

    @Autowired
    public ControllerSpreadSheet(DependencyConfig dependencyConfig, FreeMarkerConfig freeMarkerCfg, Utilities utilities, Helper hlp, ImpleSpreadSheetLogin impleSpreadSheetLogin, CookieHelperDb cookieHelperDb, ControllerEncDec controllerEncDec, SpreadSheetAccountService SpreadSheetAccountService) {
        this.controllerLog = dependencyConfig.getControllerLog();
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.freeMarkerCfg = freeMarkerCfg;
        this.cacheCookies = dependencyConfig.getCacheCookies();
        this.cacheMaster = dependencyConfig.getCacheMaster();
        this.utilities = utilities;
        this.hlp = hlp;
        this.cookieHelper = cookieHelperDb;
        this.encDec = controllerEncDec;
        this.service = impleSpreadSheetLogin;
        this.SpreadSheetAccountService = SpreadSheetAccountService;
    }

    public void handleRequestProperties(HttpServletRequest request) {
        this.objRequestProperties = this.hlp.handleRequestProperties(request);
        this.response = (ResponseEntity<StreamingResponseBody>) objRequestProperties.get("response");
        this.fullUri = objRequestProperties.get("fullUri").toString();
        this.fullUrl = objRequestProperties.get("fullUrl").toString();
        this.query = objRequestProperties.get("query").toString();
        this.objParams = (HashMap) objRequestProperties.get("objParams");
        this.webroot = objRequestProperties.get("webroot").toString();
        this.controller = objRequestProperties.get("controller").toString();
        this.methode = objRequestProperties.get("methode").toString();
        this.view = objRequestProperties.get("view").toString();
        this.webRootUrl = objRequestProperties.get("webRootUrl").toString();
        this.baseUrl = objRequestProperties.get("baseUrl").toString();
        this.hostPort = objRequestProperties.get("hostPort").toString();
    }

    public ResponseEntity<StreamingResponseBody> formulaExecutor(HttpServletRequest request) throws Exception {
        this.handleRequestProperties(request);
        JSONObject jObjResponse = new JSONObject();
        jObjResponse.put("rc", "01");
        jObjResponse.put("message", "Failed");
        String filePath = this.entityConfig.getWorkDir() + File.separator + "webroot" + File.separator + "20261101 TW 4 Perhitungan Matriks Capaian DJ MIGAS.xlsx";

        // 1. LOAD WORKBOOK (Logic Cache)
        Workbook workbook = workbookCache.computeIfAbsent("excel_base_object", k -> {
            try {
//                System.out.println("--- SEDANG LOAD FILE FISIK EXCEL (BUKAN CACHE) ---");
                return WorkbookFactory.create(new File(filePath));
            } catch (Exception e) {
                throw new RuntimeException("Gagal load excel bro!", e);
            }
        });

        // 2. AMBIL DATA DARI REQUEST
        List<List<Object>> dataSource = (List<List<Object>>) objParams.get("dataSource");
        String sheetName = ((String) objParams.get("sheetName")).trim();

        Sheet activeSheet = workbook.getSheet(sheetName);
        if (activeSheet == null) {
            System.err.println("Sheet '" + sheetName + "' gak ketemu, bro!");
            jObjResponse.put("rc", "06");
            jObjResponse.put("message", "Failed, sheet name not found!");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
            JSONObject finalJObjResponse1 = jObjResponse;
            StreamingResponseBody responseBody = outputStream -> {
                String data = finalJObjResponse1.toString();
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            };
            this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.BAD_REQUEST);
        }

        synchronized (workbook) {
            // 3. MODIFIKASI DATA (Update Cells)
            for (int r = 0; r < dataSource.size(); r++) {
                List<Object> columns = dataSource.get(r);
                Row row = activeSheet.getRow(r);
                if (row == null) row = activeSheet.createRow(r);

                for (int c = 0; c < columns.size(); c++) {
                    Cell cell = row.getCell(c);
                    if (cell == null) cell = row.createCell(c);

                    Object value = columns.get(c);
                    updateCellValue(cell, value);
                }
            }

            // 4. RE-EVALUATE FORMULA
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.clearAllCachedResultValues(); // Di POI, rumus gak otomatis update pas cell berubah, harus dipicu manual
            evaluator.setIgnoreMissingWorkbooks(true); // JANGAN BERHENTI PAS KETEMU ERROR (Mirip PHP)
            workbook.setForceFormulaRecalculation(true);  // Paksa Workbook buat ngitung ulang pas file dibuka nanti

            // 5. RETURN HASIL (getCalculatedValue)
            Map<String, Object> calculatedValue = new HashMap<>();
            for (Row row : activeSheet) {
                for (Cell cell : row) {
                    String coordinate = cell.getAddress().formatAsString();
                    Object value = "";

                    try {
                        // Gunakan ini untuk mengevaluasi tanpa return CellValue langsung
                        if (cell.getCellType() == CellType.FORMULA) {
                            // Biarkan POI ngitung di dalam cell itu sendiri
                            evaluator.evaluateFormulaCell(cell);
                        }

                        // Panggil helper yang tadi udah kita bikin kebal error
                        value = getCalculatedValue(cell, evaluator);
                    } catch (Exception e) {
                        // Kalau rumusnya beneran "pecah" di dalem, tangkap di sini
                        value = "#VALUE!";
                    }

                    calculatedValue.put(coordinate, value);
                    jObjResponse.put(coordinate, value);
                }
            }
            final JSONObject finalJObjResponse = jObjResponse;
            StreamingResponseBody responseBody = outputStream -> {
                String data = finalJObjResponse.toString();
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            };
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", String.valueOf(MediaType.APPLICATION_JSON));
            this.response = ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseBody);
            return this.response;
        }
    }

    // Helper buat nulis berbagai tipe data
    private void updateCellValue(Cell cell, Object value) {
        if (value == null || value.toString().isEmpty()) {
            cell.setBlank();
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            String strValue = value.toString();

            // --- INI KUNCINYA COK ---
            if (strValue.startsWith("=")) {
                // Buang tanda '=' nya karena POI otomatis nambahin di dalem
                // Contoh: "=1+1" jadi "1+1"
                cell.setCellFormula(strValue.substring(1));
            } else {
                cell.setCellValue(strValue);
            }
        }
    }

    // Helper buat dapet calculated value (kayak getCalculatedValue di PHP)
    private Object getCalculatedValueManual(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return "";
        // 1. Evaluasi dulu kalau dia Formula
        CellValue cv = (cell.getCellType() == CellType.FORMULA) ? evaluator.evaluate(cell) : null;
        // Tentukan tipe (apakah hasil formula atau cell biasa)
        CellType type = (cv != null) ? cv.getCellType() : cell.getCellType();
        if (cell.getCellType() == CellType.FORMULA) {
            try {
                return switch (type) {
                    case NUMERIC -> {
                        double value = (cv != null) ? cv.getNumberValue() : cell.getNumericCellValue();
                        // --- INI KUNCINYA CEK PERSEN COK ---
                        String formatString = cell.getCellStyle().getDataFormatString();
                        if (formatString != null && formatString.contains("%")) {
                            // Kalau mau dibalikin dalam bentuk string "10%"
                            yield (value * 100) + "%";
                            // Atau kalau mau tetep angka 0.1 tapi lu tau itu persen, pake 'value' aja
                        }
                        yield value;
                    }
                    case BOOLEAN -> (cv != null) ? cv.getBooleanValue() : cell.getBooleanCellValue();
                    case STRING -> (cv != null) ? cv.getStringValue() : cell.getStringCellValue();
                    case ERROR -> "#VALUE!";
                    default -> "";
                };
            } catch (Exception e) {
                // Kalau POI beneran angkat tangan, kasih fallback biar gak crash satu app
                return "#ERROR!";
            }
        }

        // Kalau bukan formula, ambil nilai biasa
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            default -> "";
        };
    }

    private Object getCalculatedValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return "";

        try {
            // 1. Pakai DataFormatter: Ini bakal balikin STRING sesuai tampilan di layar Excel
            // Dia otomatis nge-handle FormulaEvaluator juga di dalemnya
            DataFormatter formatter = new DataFormatter();
            String val = formatter.formatCellValue(cell, evaluator);

            // 2. Cek kalau hasilnya kosong (Blank Cell)
            if (val == null || val.trim().isEmpty()) {
                return "";
            }

            // 3. (Opsional) Kalau lu pengen hasil balisannya tetep angka biar bisa diitung di JS
            // Tapi kalau lu mau "Persis" kayak Excel (ada Rp, %, dll), langsung return 'val'
            return val;

        } catch (Exception e) {
            // Kalau rumusnya error parah di dalem
            return "#ERROR!";
        }
    }
}

