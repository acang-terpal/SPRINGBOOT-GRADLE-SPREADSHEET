$(document).ready(function () {

});

class BackendService {
    constructor() {

    }

    callBePromise(instance, dataSource, worksheetName) {
        const self = this;
        return new Promise((resolve, reject) => {
            // console.log(dataSource.length);
            let randomNumber = Math.random();
            let idObj = worksheetName.replace(/ /g, "_") + "_" + randomNumber;
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            $.ajax({
                // url: "spreedsheet/formulaExecutor",
                url: $('#webRootUrl').html() + "ControllerSpreadSheet/formulaExecutor",
                type: 'POST',
                async: true,
                contentType: "application/json; charset=utf-8",
                dataType: 'json',
                headers: {
                    'X-CSRF-TOKEN': token
                },
                data: JSON.stringify({
                    "_csrf": token,
                    dataSource: dataSource,
                    sheetName: worksheetName
                }),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                    altair_helpers.content_preloader_show();
                    instance.setCallBePromiseOnLoad(idObj, true);
                },
                success: function (res) {
                    console.log(worksheetName + " response BE complex formula ");
                    // console.log(worksheetName + " response BE complex formula " + JSON.stringify(res));
                    resolve(res); // Mengirim hasil ke 'then'
                    instance.setCallBePromiseOnLoad(idObj, false);
                    let isAnythingTrue = instance.getCallBePromiseOnLoad().some(obj => Object.values(obj).includes(true));
                    if (!isAnythingTrue) {
                        altair_helpers.content_preloader_hide();
                    }
                },
                error: function (err) {
                    let message = err.responseJSON.meta.message
                    console.log(`${err.status} ${err.statusText}`)
                    console.log(err.responseJSON)
                    reject(err);
                    instance.setCallBePromiseOnLoad(idObj, false);
                    let isAnythingTrue = instance.getCallBePromiseOnLoad().some(obj => Object.values(obj).includes(true));
                    if (!isAnythingTrue) {
                        altair_helpers.content_preloader_hide();
                    }
                },
            })
        });
    }
    async callBeAsync(dataSource) {
        try {
            const res = await $.ajax({
                url: $('#webRootUrl').html() + "ControllerSpreadSheet/formulaExecutor",
                type: 'POST',
                dataType: 'json',
                headers: {
                    'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
                },
                data: { dataSource: dataSource }
            });

            return res; // Sekarang bisa mengembalikan nilai
        } catch (err) {
            console.error(err);
            return null;
        }
    }
}
class CtrlCommon {
    worksheet;
    worksheetName;
    modalCreateColumn;
    flagAddRow;
    datasourceSatuan;
    datasourceLevel;
    datasourceInputValue;
    datasourceInputWithFormula;
    isChangeByProgram;
    resultExcelBe;
    childInstance;
    readOnlyColumn = [];

    // The constructor method is automatically called when a new object is created
    constructor() {

    }
    init() {
        this.childInstance = childInstance;
    }

    setWorksheet(childInstance, instance, worksheetName, readOnlyColumn = []) {
        this.childInstance = childInstance;
        this.worksheet = instance;
        this.worksheetName = worksheetName;
        this.readOnlyColumn = readOnlyColumn;
    }

    initDataInstance() {
        const self = this;
        // let coords = jspreadsheet.helpers.getCoordsFromCellName('AU1');
        // let x = coords[0];
        // let y = coords[1];
        // console.log(x + " " +y);
        // set default all meta cell
        let allData = this.worksheet.getData(); // ✅ Correct way in v5
        // console.log(allData.length);
        for (let row = 0; row < allData.length; row++) {
            for (let col = 0; col < allData[row].length; col++) {
                let cellName = jspreadsheet.helpers.getCellNameFromCoords(col, row);
                let formula = this.worksheet.getValue(cellName, false);
                this.updateCellMeta(cellName, "meta", { "cellName": cellName, "formula": formula, "value": formula });
            }
        }

        let result = this.convertMetaToDataSource(this.worksheet);
        // console.log(result.length);
        let backendService = new BackendService();
        backendService.callBePromise(self, result, this.worksheetName).then(result => {
            self.resultExcelBe = result;
            Object.entries(self.resultExcelBe).forEach(([cellName, value]) => {
                // update data spreadsheet
                let coords = jspreadsheet.helpers.getCoordsFromCellName(cellName);
                let x = coords[0];
                let y = coords[1];
                // let columnConfig = self.worksheet.options.columns[x];
                // if (columnConfig.type === 'dropdown') { }
                if (cellName != null && self.worksheet.getMeta(cellName)?.meta !== undefined && value != null) {
                    // console.log(`Cell: ${cellName} | Rumus: ${self.worksheet.getMeta(cellName).meta.formula} | Hasil: ${value}`);
                    self.worksheet.options.data[y][x] = value;
                    // update meta
                    self.updateCellMeta(cellName, "meta", {
                        "cellName": cellName,
                        "formula": self.worksheet.getMeta(cellName).meta.formula,
                        "value": value
                    });
                }
            });
            self.datasourceInputValue = self.worksheet.options.data;
            self.worksheet.setData(self.datasourceInputValue);
            // this.setupStyleCell(this.worksheet);
        });
    }

    shiftRow(keyCode, lastClickCell = false) {
        let coords = jspreadsheet.helpers.getCoordsFromCellName(this.childInstance.getLastClickCell().toUpperCase());
        let x = coords[0];
        let y = coords[1];
        if (lastClickCell) {
            this.worksheet.updateSelectionFromCoords(x, y, x, y);
            return false;
        }
        const keysShifter = [37, 38, 39, 40]; // kiri, atas, kanan, bawah
        if (keysShifter.includes(keyCode)) {
            let nextX, nextY;
            if (keyCode === 37) {
                nextX = Math.max(0, x - 1); // Kiri
                nextY = y;
            } else if (keyCode === 38) {
                nextX = x;
                nextY = Math.max(0, y - 1); // Atas
            } else if (keyCode === 39) {
                nextX = Math.min(this.worksheet.options.columns.length - 1, x + 1); // Kanan
                nextY = y;
            } else if (keyCode === 40) {
                nextX = x;
                nextY = Math.min(this.worksheet.options.data.length - 1, y + 1); // Bawah
            }
            //if (nextY == 0) return; // disable shift to title column
            // this.worksheet.updateSelectionFromCoords(nextX, nextY, nextX, nextY); // kalau ini diilangin jadi loncat karena e.stopPropagation(); di behhaviour js dimatikan
        } else if (keyCode == 13) {
            //pindah ke bawah cell
            let nextY = y + 1; // Pindah ke baris bawahnya
            // Cek apakah masih ada baris di bawahnya agar tidak error
            if (nextY < this.worksheet.rows.length) {
                // 1. Pindahkan seleksi visual ke baris bawah (x tetap sama)
                this.worksheet.updateSelectionFromCoords(x, nextY, x, nextY);
                // 2. Update variabel lastClickCell agar sinkron dengan cell baru
                this.childInstance.setLastClickCell(jspreadsheet.helpers.getColumnName(nextY) + (y + 1));
                // 3. Ambil nilai cell baru tersebut dan tampilkan di editor
                let nextVal = this.worksheet.getValueFromCoords(x, nextY);
                $('#formula_editor').val(nextVal).focus();
                // 4. Scroll otomatis jika cell di bawah tidak terlihat (opsional)
                // this.worksheet.scrollControls(nextY);
            }
        } else if (keyCode == 9) {
            let nextX = x + 1; // Geser ke kolom kanan
            let totalCols = this.worksheet.options.columns.length;
            if (nextX < totalCols) {
                // 1. Pindahkan seleksi visual ke kanan
                this.worksheet.updateSelectionFromCoords(nextX, y, nextX, y);
                // 2. Update variabel referensi cell kamu (misal dari A1 jadi B1)
                this.childInstance.setLastClickCell(jspreadsheet.helpers.getColumnName(nextX) + (y + 1));
                // 3. Ambil nilai cell baru dan tampilkan di editor
                let nextVal = this.worksheet.getValueFromCoords(nextX, y);
                $('#formula_editor').val(nextVal).focus();
                // 4. Pastikan kursor di editor ada di posisi paling akhir
                setTimeout(() => {
                    let el = $('#formula_editor')[0];
                    el.setSelectionRange(el.value.length, el.value.length);
                }, 10);
            }
        }
    }
    submitEditor(formula, targetCell, keyCode) {
        this.childInstance.setFormulaMode(false);
        let coords = jspreadsheet.helpers.getCoordsFromCellName(targetCell.toUpperCase());
        let x = coords[0];
        let y = coords[1];
        let cell = this.worksheet.getCellFromCoords(x, y);
        let columnConfig = this.worksheet.options.columns[x];
        if (this.readOnlyColumn.includes(x) || columnConfig.type == 'dropdown') { // disable editing on column target on enter key/tab
            console.log("column is readonly")
            return false;
        } else {
            this.updateCellMeta(targetCell.toUpperCase().toUpperCase(),
                "meta",
                {
                    "cellName": targetCell.toUpperCase().toUpperCase(),
                    "formula": formula,
                    "value": formula
                });

            this.worksheet.options.data[y][x] = formula;
            this.worksheet.setData(this.worksheet.options.data);
            this.childInstance.applyHighlight(x, y);
            // console.log("1. submit : " + targetCell);
            this.evaluateFormula(formula, targetCell, keyCode);
        }
    }
    evaluateFormula(formula, targetCell, keyCode) {
        const self = this;
        let coords = jspreadsheet.helpers.getCoordsFromCellName(targetCell.toUpperCase());
        let x = coords[0];
        let y = coords[1];
        // ------------------------try with builtin jspreadsheet first, then formula.js if fail, if formula not exist or error then using hyperformula
        let result = structuredClone(formula);
        try {
            this.updateCellMeta(targetCell.toUpperCase(),
                "meta",
                {
                    "cellName": targetCell.toUpperCase(),
                    "formula": formula,
                    "value": formula
                });
            let backendService = new BackendService();
            backendService.callBePromise(self, this.convertMetaToDataSource(), self.worksheetName).then(result => {
                self.resultExcelBe = result;
                // console.log("2. evaluete : " + targetCell);
                this.handleBeResult(formula, targetCell, keyCode);
            });
        } catch (e) {
            console.log(e);
            result = this.resultExcelBe[targetCell]; // ini sama aja dengan misalnya this.resultExcelBe.A4
        }
    }
    handleBeResult(formula, targetCell, keyCode) {
        const self = this;
        let coords = jspreadsheet.helpers.getCoordsFromCellName(targetCell.toUpperCase());
        let x = coords[0];
        let y = coords[1];
        this.updateCellMeta(targetCell.toUpperCase().toUpperCase(),
            "meta",
            {
                "cellName": targetCell.toUpperCase().toUpperCase(),
                "formula": formula,
                "value": this.resultExcelBe[targetCell.toUpperCase()]
            });

        this.worksheet.options.data[y][x] = this.resultExcelBe[targetCell.toUpperCase()];
        this.worksheet.setData(this.worksheet.options.data);
        this.childInstance.removeHighlight(x, y);

        $('#formula_editor').val("");
        // console.log("3. handle : " + targetCell);
        self.shiftRow("", true)
        // console.log("tes");
        // inisialisasi ulang style nya
        // this.setupStyleCell();
    }
    deleteSelectedRange() {
        this.childInstance.setFormulaMode(false);
        const coords = jspreadsheet.helpers.getCoordsFromCellName(this.childInstance.getLastClickCell());
        // Ambil koordinat seleksi saat ini (x1, y1 sampai x2, y2)
        const selection = this.worksheet.getHighlighted();
        if (selection && selection.length > 0) {
            // Koordinat seleksi: [x1, y1, x2, y2]
            const [x1, y1, x2, y2] = selection[0];
            // Looping untuk menghapus setiap cell di dalam range
            for (let y = y1; y <= y2; y++) {
                for (let x = x1; x <= x2; x++) {
                    // Set nilai menjadi kosong ('')
                    // Parameter ke-4 'true' agar Jspreadsheet merender ulang cell tersebut
                    let colLetter = jspreadsheet.helpers.getColumnName(x);
                    let cellName = colLetter + (y + 1);
                    console.log("cell yg dihapus : " + cellName);
                    // this.worksheet.setValueFromCoords(x, (y), '', true);
                    // Hapus data langsung di sumbernya (array row)
                    this.worksheet.options.data[y][x] = '';
                    this.childInstance.setLastClickCell(cellName);
                    this.submitEditor('', this.childInstance.getLastClickCell(), 27);
                }
            }
            // 2. Refresh Tampilan sekaligus
            // Ini akan merender ulang tabel berdasarkan data yang sudah bersih
            this.worksheet.setData(this.worksheet.options.data);
            $('#formula_editor').val("");
            // inisialisasi ulang style nya
            // self.setupStyleCell(this.worksheet);
            this.worksheet.updateSelectionFromCoords(coords[0], coords[1], coords[0], coords[1]);
        }
    }

    formulaCalc(formula) {
        try {
            if (!formula.startsWith('=')) return formula; // Not a formula

            // Remove '='
            const expr = formula.substring(1).trim();

            // Handle NOW()
            if (/^NOW\(\)$/i.test(expr)) {
                return formulajs.NOW().toLocaleString();
            }

            // Handle SUM(A1:A3) or SUM(1,2,3)
            const sumMatch = expr.match(/^SUM\((.+)\)$/i);
            if (sumMatch) {
                const args = sumMatch[1].split(',').map(arg => {
                    arg = arg.trim();
                    // If it's a cell reference like A1
                    const cellRef = arg.match(/^([A-Z]+)(\d+)$/i);
                    if (cellRef) {
                        const colIndex = cellRef[1].charCodeAt(0) - 65; // 'A' -> 0
                        const rowIndex = parseInt(cellRef[2], 10) - 1; // 1-based to 0-based
                        return parseFloat(this.worksheet.getValueFromCoords(colIndex, rowIndex)) || 0;
                    }
                    return parseFloat(arg) || 0;
                });
                return formulajs.SUM.apply(null, args);
            }

            return '#UNSUPPORTED';
        } catch (e) {
            return '#ERROR';
        }
    }

    setupStyleCell() {
        //----------------------set behaviour cell
        // Example: Loop through 8 columns (A-H) and 3 rows (1-3)
        const numCols = this.worksheet.options.columns.length - 1;
        const numRows = this.worksheet.options.data.length;

        for (let i = 0; i <= numCols; i++) { // column
            for (let j = 1; j <= numRows; j++) { // row
                let cellName = jspreadsheet.helpers.getColumnName(i) + (j); // A1, A2... B1...
                let columnConfig = this.worksheet.options.columns[i];
                this.worksheet.setReadOnly(cellName, true);
                this.worksheet.setStyle(cellName, 'color', '#363434', true);
                if (i <= 3 && j >= 0) {
                    this.worksheet.setStyle(cellName, 'background-color', '#eeeeee', true);
                } else if (columnConfig.type === 'dropdown') {
                    this.worksheet.setReadOnly(cellName, true);
                    if (i == 2) {

                    } else if (i == 3) {

                    }
                } else if (i >= 4 && j != 1) {
                    // this.worksheet.setReadOnly(cellCoord, false); // tadinya gini, supaya cell isian text bisa diedit
                    this.worksheet.setReadOnly(cellName, true);
                    this.worksheet.setStyle(cellName, 'background-color', 'white', true);
                }
            }
        }
    }
    addColumn() {
        this.modalCreateColumn.show();
        //---------------add new column with type dropdown
        // case (1, 3, true) column and row always start on index 0, so this mean: 1 -> add 1 column, 3 -> target column D, false -> add column after target column (D), f true is mean add column before column target (D)
        this.worksheet.insertColumn(1, this.worksheet.options.columns.length, false, {
            type: 'dropdown',
            width: '200px',
            source: ['New Dropdown', 'Apple', 'Banana', 'Orange']
        });
        let cellName = jspreadsheet.helpers.getCellNameFromCoords(this.worksheet.options.columns.length - 1, 0);
        // this.worksheet.setValue(cellName, 'New Dropdown');
        this.worksheet.setReadOnly(cellName, true);
        this.worksheet.setStyle(cellName, 'background-color', 'yellow', true);
        this.worksheet.setStyle(cellName, 'color', '#363434', true);

        //---------------add new column with type input
        this.worksheet.insertColumn(1, this.worksheet.options.columns.length, false, {
            type: 'text',
            width: '200px',
        });
        cellName = jspreadsheet.helpers.getCellNameFromCoords(this.worksheet.options.columns.length - 1, 0);
        // this.worksheet.setValue(cellName, 'New Column Text Formula');
        this.worksheet.setReadOnly(cellName, true);
        this.worksheet.setStyle(cellName, 'background-color', 'yellow', true);
        this.worksheet.setStyle(cellName, 'color', '#363434', true);

        const allData = this.worksheet.getData(); // ✅ Correct way in v5
        for (let row = 0; row < allData.length; row++) {
            for (let col = 0; col < allData[row].length; col++) {
                let cellName = jspreadsheet.helpers.getCellNameFromCoords(col, row);
                let formula = '';
                let value = '';
                if (this.worksheet.getMeta(cellName) != null) {
                    formula = this.worksheet.getMeta(cellName).meta.formula;
                    value = this.worksheet.getMeta(cellName).meta.value;
                }
                this.updateCellMeta(cellName, "meta", { "cellName": cellName, "formula": formula, "value": value });
            }
        }
        console.log('✅ All cell metadata set on load.');
        let backendService = new BackendService();
        backendService.callBePromise(self, this.convertMetaToDataSource(), self.worksheetName).then(result => {
            self.resultExcelBe = result;
        });
    }
    addRow() {
        this.flagAddRow = true;
        //---------------add rows
        this.worksheet.insertRow(1);
        const numRows = this.worksheet.options.data.length;
        let cellNameA = 'A' + numRows;
        let cellNameB = 'B' + numRows;
        let cellNameC = 'C' + numRows;
        let cellNameD = 'D' + numRows;

        // this.worksheet.setValue(cellNameA, numRows - 1);
        this.worksheet.setReadOnly(cellNameA, true);
        this.worksheet.setStyle(cellNameA, 'color', '#363434', true);
        this.worksheet.setStyle(cellNameA, 'background-color', '#eeeeee', true);
        this.updateCellMeta(cellNameA, "meta", { "cellName": cellNameA, "formula": numRows - 1, "value": numRows - 1 });

        this.worksheet.setReadOnly(cellNameB, true);
        this.worksheet.setStyle(cellNameB, 'color', '#363434', true);
        this.worksheet.setStyle(cellNameB, 'background-color', '#eeeeee', true);
        this.updateCellMeta(cellNameB, "meta", { "cellName": cellNameB, "formula": "", "value": "" });

        // this.worksheet.setValue(cellNameC, '%');
        this.updateCellMeta(cellNameC, "meta", { "cellName": cellNameC, "formula": "%", "value": "%" });

        // this.worksheet.setValue(cellNameD, 'DMB');
        this.updateCellMeta(cellNameD, "meta", { "cellName": cellNameD, "formula": "DMB", "value": "DMB" });

        this.flagAddRow = false;

        const allData = this.worksheet.getData(); // ✅ Correct way in v5
        for (let row = 0; row < allData.length; row++) {
            for (let col = 0; col < allData[row].length; col++) {
                let cellName = jspreadsheet.helpers.getCellNameFromCoords(col, row);
                let formula = '';
                let value = '';
                if (this.worksheet.getMeta(cellName) != null) {
                    formula = this.worksheet.getMeta(cellName).meta.formula;
                    value = this.worksheet.getMeta(cellName).meta.value;
                }
                this.updateCellMeta(cellName, "meta", { "cellName": cellName, "formula": formula, "value": value });
            }
        }
        console.log('✅ All cell metadata set on load.');
        let backendService = new BackendService();
        backendService.callBePromise(self, this.convertMetaToDataSource(), self.worksheetName).then(result => {
            self.resultExcelBe = result;
        });
    }
    updateCellMeta(cellName, meta, v) {
        // console.log("log from update meta : " + cellName + " meta : " + String(meta) + " value : " + JSON.stringify(v));
        this.worksheet.setMeta(cellName, String(meta), v);
        // console.log("log from update meta : " + JSON.stringify(this.worksheet.getMeta(cellName).meta));
    }
    getData() {
        // console.log(this.datasourceInputValue);
        console.log(this.worksheet.getData());
        console.log(this.worksheet.getStyle());
        console.log(this.worksheet.getMerge());
    }
    convertMetaToDataSource() {
        const metaData = this.worksheet.getMeta();
        // 1. Cari tahu rentang baris dan kolom tertinggi
        let maxRow = 0;
        let maxCol = 0;

        Object.keys(metaData).forEach(cellName => {
            const coords = jspreadsheet.helpers.getCoordsFromCellName(cellName);
            if (coords !== undefined) {
                const x = coords[0]; // Kolom
                const y = coords[1]; // Baris
                if (y > maxRow) maxRow = y;
                if (x > maxCol) maxCol = x;
            }
        });

        // 2. Buat Array 2D kosong sesuai ukuran tadi
        let result = [];
        for (let i = 0; i <= maxRow; i++) {
            result[i] = new Array(maxCol + 1).fill("");
        }

        // 3. Isi Array dengan property 'formula' (supaya rumus tetap masuk)
        Object.keys(metaData).forEach(cellName => {
            const coords = jspreadsheet.helpers.getCoordsFromCellName(cellName);
            if (coords !== undefined) {
                const x = coords[0];
                const y = coords[1];

                // Ambil nilai 'formula', jika kosong/undefined isi string kosong
                let cellContent = metaData[cellName].meta.formula;

                // Simpan ke posisi array yang tepat
                result[y][x] = cellContent;
            }
        });

        // Hapus baris terakhir jika isinya kosong (seperti baris 5 di data kamu)
        if (result.length > 0 && result[result.length - 1].every(cell => cell === "")) {
            result.pop();
        }
        this.datasourceInputWithFormula = result;
        // console.log(result.length);
        return result;
    }
}
