$(document).ready(function () {
    // ini bisa pakai jspreadsheet "=FORECAST(4, [10,20,30], [1,2,3])"
    // datasourceInputValue = [
    //     ['No', 'Data Dibutuhkan', 'Satuan', 'Level', 'TW I', 'TW II', 'TW III', 'TW IV'],
    //     ['1', 'Persentase Pegawai Direktorat Pembinaan Progam Migas yang Bebas Hukuman Disiplin', '%', 'DMB', '1', '2', '3', '=sum(E2:G2)'],
    //     ['2', 'Persentase Pegawai Direktorat Pembinaan Progam Migas yang Mencapai/ Melebihi Target Kinerja', '%', 'DMB', "=SUM({1, 2, 3})", '=second(now())', "=if(1<2,\"benar\", \"salah\")", "=FORECAST(4, {10,20,30}, {1,2,3})"],
    //     ['3', 'Persentase Pegawai Direktorat Pembinaan Progam Migas yang Mencapai/ Melebihi Target Kinerja', '%', 'DMB', "=SUM({1, 2, 3, 5})", '=10+10', "=if(1<2, 30, 10)", "=FORECAST(4, E4:G4, E2:G2)"],
    //     ['3', 'Persentase Pegawai Direktorat Pembinaan Progam Migas yang Mencapai/ Melebihi Target Kinerja', '%', 'DMB', "=NOW()"],
    // ];
    const datasourceInputValue = window.jsonCapaian.map(row => row.slice(0, 6));;
    const datasourceSatuan = window.jsonSatuan;
    const datasourceLevel = window.jsonLevel;
    let ctrlFormInput = new CtrlFormInput("CAPAIAN");
    ctrlFormInput.setDataSource(datasourceInputValue, datasourceSatuan, datasourceLevel);
    ctrlFormInput.init();
});

class CtrlFormInput extends CtrlBehaviour {
    worksheet;
    worksheetName;
    datasourceSatuan;
    datasourceLevel;
    datasourceInputValue;
    datasourceInputWithFormula;
    tempBeforeChange;

    // The constructor method is automatically called when a new object is created
    constructor(worksheetName) {
        // call and setup parent class
        super()
        this.worksheetName = worksheetName;
    }
    init() {
        const self = this;
        this.initSpreedSheetInput();
        super.init(self, "singleton", true);
        let readOnlyColumn = [0,1,2,3,4];
        super.setWorkSheet(this.worksheet, this.worksheetName, readOnlyColumn);
    }

    setDataSource(datasourceInputValue, datasourceSatuan, datasourceLevel) {
        this.datasourceInputValue = datasourceInputValue;
        this.datasourceSatuan = datasourceSatuan;
        this.datasourceLevel = datasourceLevel;
    }

    initSpreedSheetInput() {
        let self = this;
        // const syncFormulaResults = () => super.syncFormulaResults(this.worksheet, 0, 0, 0);
        let parentWidth = $("#spreadSheetInput").parent().width();
        // Create own formula
        formula.setFormula({ NEGATIVE });
        formula.setFormula({ OFFSET });
        formula.setFormula({ FORECAST }); // nyobain replace fungsi yang sudah ada
        // jspreadsheet create worksheet
        this.worksheet = $('#spreadSheetInput').jspreadsheet({
            toolbar: false,
            tabs: false,
            formula: false,
            parseFormulas: false,
            worksheets: [{
                worksheetName: self.worksheetName,
                worksheetId: '0',
                data: this.datasourceInputValue,
                // lazyLoading:true,
                // loadingSpin:true,
                columns: [
                    { type: 'text', title: 'A. No', width: 50, wordWrap: true },
                    { type: 'text', title: 'B. IKSP/IKSK', width: 420, wordWrap: true, align: 'left' },
                    { type: 'text', title: 'C. Leveling', width: '120px', wordWrap: true },
                    { type: 'text', title: 'D. Satuan', width: '120px', wordWrap: true },
                    { type: 'text', title: 'E. PIC', width: 120, wordWrap: true },
                    { type: 'text', title: 'F. Target', width: 120, wordWrap: true },
                    { type: 'text', title: 'G. Realisasi TW I', width: 120, wordWrap: true },
                    { type: 'text', title: 'H. Realisasi TW II', width: 120, wordWrap: true },
                    { type: 'text', title: 'I. Realisasi TW III', width: 120, wordWrap: true },
                    { type: 'text', title: 'J. Realisasi TW IV', width: 120, wordWrap: true },
                    { type: 'text', title: 'K. Kinerja TW I', width: 120, wordWrap: true },
                    { type: 'text', title: 'L. Kinerja TW II', width: 120, wordWrap: true },
                    { type: 'text', title: 'M. Kinerja TW III', width: 120, wordWrap: true },
                    { type: 'text', title: 'N. Kinerja TW IV', width: 120, wordWrap: true },
                    { type: 'text', title: 'O. Kinerja Tahunan', width: 120, wordWrap: true },
                    { type: 'text', title: 'P. Kategori Kinerja', width: 120, wordWrap: true },
                    { type: 'text', title: 'Q. Status Kinerja', width: 120, wordWrap: true },
                ],
                columnResize: true,
                tableWidth: parentWidth - 30,
                tableOverflow: true,
                filters: false,
                columnDrag: false,
                allowInsertRow: true,
                allowDeleteRow: true,
                freezeColumns: 4,
                freezeRows: 1,
                // --- KONFIGURASI PAGINATION ---
                pagination: 10,           // Menampilkan 20 baris per halaman
                paginationOptions: [10, 20, 30, 100, 500, 1000], // Opsi pilihan jumlah baris
                search: true,             // Tambahkan fitur cari agar mudah navigasi
                columnSorting: true,      // Biar bisa di-sort per kolom
            }],
            oncreatecell: function (instance, cell, x, y, value) {
                // Jika baris pertama (Judul)
                // if (y === 0) {
                //     // 1. Hapus class dropdown agar panah hilang
                //     cell.classList.remove('jss_dropdown');
                //     // 2. Matikan event klik dropdown di cell itu
                //     cell.onclick = null;
                //     // 3. Tambahkan style judul biar keren
                //     cell.style.fontWeight = 'bold';
                //     cell.style.backgroundColor = '#f3f3f3';
                // }
                cell.style.color = '#363434';
                // cell.style.backgroundColor = '#eeeeee';
                cell.classList.add('readonly');
            },
            onload: function (instance) {
                // self.initDataInstance();
                // self.callBePromise(self.datasourceInputValue).then(result => {
                //     self.resultExcelBe = result;
                //     self.initDataInstance();
                //     self.setupStyleCell(instance);
                // });

                // // atau gini
                // this.callBeAsync(this.datasourceInputValue).then(result => {
                //     if (result) {
                //         self.resultExcelBe = result;
                //         self.initDataInstance();
                //         self.setupStyleCell(instance);
                //     } else {
                //         // console.log("Request gagal atau return null");
                //     }
                // }).catch(err => {
                //     // console.error("Fatal error:", err);
                // });
            },
            oninsertrow: function (instance, cell, x, y, value) {
                // should add meta data on new row
            },
            ondeleterow: function (instance, cell, x, y, value) {
                // should delete meta data on deleted row
            },
            oninsertcolumn: function (instance, cell, x, y, value) {
                // should add meta data on new column
            },
            ondeletecolumn: function (instance, cell, x, y, value) {
                // should delete meta data on deleted column
            },
            onselection: function (instance, x1, y1, x2, y2) {
                //----------------------disable full row or full column selection supaya self.lastClickCell tidak rusak---------------------------
                // Ambil total baris dan kolom dari instance
                const totalRows = instance.rows.length;
                const totalCols = instance.options.columns.length;
                // 1. Deteksi Klik Header Kolom (memilih satu kolom penuh)
                // 2. Deteksi Klik Header Baris (memilih satu baris penuh)
                // 3. Deteksi Klik Pojok Kiri Atas (memilih semua cell)
                const isFullColumn = (y1 === 0 && y2 >= totalRows - 1);
                const isFullRow = (x1 === 0 && x2 >= totalCols - 1);
                if (isFullColumn || isFullRow) {
                    // Jika header diklik, kita batalkan range selection-nya
                    // Kembalikan seleksi hanya ke cell yang terakhir diklik (active cell)
                    let coords = jspreadsheet.helpers.getCoordsFromCellName(self.lastClickCell);
                    // Update selection agar hanya menyorot 1 cell saja
                    //instance.updateSelectionFromCoords(coords[0], coords[1], coords[0], coords[1]);
                    // Keluar dari fungsi agar tidak menjalankan logic range selector ke #formula_editor
                    return false;
                }
                //----------------------------------fungsi range selector----------------------------------------
                // 1. Konversi koordinat awal (start)
                let cellStart = jspreadsheet.helpers.getColumnName(x1) + (y1 + 1);
                // 2. Konversi koordinat akhir (end)
                let cellEnd = jspreadsheet.helpers.getColumnName(x2) + (y2 + 1);
                // 3. Gabungkan jadi range (A2:B4) atau satu cell saja (A2)
                let rangeText = (x1 === x2 && y1 === y2) ? cellStart : cellStart + ":" + cellEnd;
                console.log("range : " + rangeText);
                if (self.getFormulaMode()) {
                    // code tambahan yang akan aktif saat formula mode
                    // 2. Sisipkan ke #formula_editor
                    let editor = $('#formula_editor');
                    let currentVal = editor.val();

                    // Cari posisi kurung buka terakhir atau operator terakhir (+, -, *, /)
                    // Regex ini mencari karakter terakhir yang menandakan awal input range
                    let lastOperatorIndex = Math.max(
                        currentVal.lastIndexOf('('),
                        currentVal.lastIndexOf(','),
                        currentVal.lastIndexOf('+'),
                        currentVal.lastIndexOf('-'),
                        currentVal.lastIndexOf('*'),
                        currentVal.lastIndexOf('/')
                    );

                    if (lastOperatorIndex !== -1) {
                        // Ambil teks sebelum operator (misal: "=SUM(")
                        let prefix = currentVal.substring(0, lastOperatorIndex + 1);
                        editor.val(prefix + "Input!" + rangeText);
                    } else {
                        // Jika cuma ada "=", ganti jadi "=A1"
                        editor.val("=" + rangeText);
                    }

                    // 3. Kembalikan fokus ke editor agar user bisa lanjut ngetik
                    // e.stopPropagation() di keydown kamu tadi akan menjaga fokus ini aman
                    setTimeout(() => editor.focus(), 10);
                } else {
                    //----------------------------------fungsi custom editor supaya tau cell mana yang mau diedit----------------------------------------
                    // console.log("x1 : " + x1 + " y1 : " + y1 + " x2 : " + x2 + " y2 : " + y2);
                    let cellName = jspreadsheet.helpers.getCellNameFromCoords(x1, y1);
                    self.setLastClickCell(cellName);
                    console.log("onclick : " + self.getLastClickCell());
                    console.log("log meta from onselection " + JSON.stringify(self.worksheet.getMeta(cellName).meta));
                    $('#formula_editor_label').html("Formula Editor " + self.lastClickCell);
                    setTimeout(() => {
                        const editor = $('#formula_editor');
                        // editor.val(self.worksheet.getValue(cellName));
                        editor.val(self.worksheet.getMeta(cellName).meta.formula ? self.worksheet.getMeta(cellName).meta.formula : '');
                        editor.focus();
                        // Bonus: Pindahkan kursor ke akhir teks agar tidak di depan
                        const el = editor[0];
                        const val = editor.val();
                        el.setSelectionRange(val.length, val.length);
                    }, 10);
                    //----------------------------------end fungsi custom editor supaya tau cell mana yang mau diedit----------------------------------------
                }
                // its mean header column E clicked
                if (x2 == 4 && y2 > 0) {
                    // instance.resetSelection();
                    return false;
                }
                // its mean column B clicked on row 0
                if (x1 == 1 && y1 == 0) {
                    self.modalCreateColumn.show();
                }
            },
            contextMenu: function (o, x, y, e, items, section) {
                return false;
            },
            onbeforechange: function (instance, cell, x, y, value) {
                // -----------------------handle dropdown--------------------------
                // 1. Ambil konfigurasi kolom berdasarkan indeks x
                let columnConfig = instance.options.columns[x];
                // 2. Cek apakah tipe kolom tersebut adalah 'dropdown'
                if (columnConfig.type === 'dropdown') {
                    self.tempBeforeChange = structuredClone(self.datasourceInputValue[y][x]);
                    // kalau ngeklik value yg sama, maka return false, supaya pilihannya ga bisa kosong (defaultnya kalau pilih select yg sama maka jadi unclicked)
                    if (value == '') {
                        return false; // Cancel the change
                    }
                } else {
                    // handle auto close bracket
                    if (typeof value === 'string' && value.startsWith('=')) {
                        // Count opening vs closing brackets
                        const openCount = (value.match(/\(/g) || []).length;
                        const closeCount = (value.match(/\)/g) || []).length;

                        // If we are missing closing brackets, add them
                        if (openCount > closeCount) {
                            const missing = openCount - closeCount;
                            return value + ")".repeat(missing);
                        }
                    }
                }
                return value;
            },
            onchange: function (instance, cell, x, y, value) {
                // x is column, y is row
                let cellName = jspreadsheet.helpers.getCellNameFromCoords(x, y, value);
                // -----------------------handle dropdown--------------------------
                let columnConfig = instance.options.columns[x];
                // 2. Cek apakah tipe kolom tersebut adalah 'dropdown', dan kembalikan nilai seperti semula bila value adalah empty/null/''
                if (columnConfig.type === 'dropdown') {
                    // ambil row paling atas di colum tersebut, untuk mendeteksi apakah select adalah satuan atau level
                    let valCustomHeader = self.worksheet.getValue(jspreadsheet.helpers.getCellNameFromCoords(x, 0));
                    $('#formula_editor').val(value);
                    if (value == false || value == 'Satuan' || value == 'Level') {
                        self.updateCellMeta(instance, cellName, "meta", { "cellName": cellName, "formula": self.tempBeforeChange, "value": self.tempBeforeChange });
                        $('#formula_editor').val(self.tempBeforeChange);
                        instance.options.columns[2].source = self.datasourceSatuan;
                        instance.options.columns[3].source = self.datasourceLevel;
                        instance.options.data[y][x] = self.tempBeforeChange;
                        instance.setData(instance.options.data);
                        // inisialisasi ulang style nya
                        self.setupStyleCell(instance);
                        // instance.setValueFromCoords(x, y, self.tempBeforeChange, true);
                        self.evaluateFormula(self.tempBeforeChange, cellName, null);
                    } else {
                        // console.log("dua " + self.tempBeforeChange + " " + value);
                        self.updateCellMeta(instance, cellName, "meta", { "cellName": cellName, "formula": value, "value": value });
                        self.evaluateFormula(value, cellName, null);
                    }
                    $('#formula_editor').focus();
                } else {

                }
            },
            onafterchanges: function () {

            },
            oncreateeditor: function () {
                // code mengganti atau memodifikasi elemen input yang sedang dibuat
            },
            oneditionstart: function (instance, cell, x, y, value) {

            },
            oneditionend: function () {

            },
            onevent: function (event, instance, cell, x, y, value) {
                // console.log(event);
            }
        })[0];
    }
}
