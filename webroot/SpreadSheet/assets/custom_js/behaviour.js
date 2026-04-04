$(document).ready(function () {

});

// diluar class artinya singleton
let isInitialized = false;
let callBePromiseOnLoad = [];
class CtrlBehaviour extends CtrlCommon {
    observerPageContentInerResize;
    worksheet;
    worksheetName;
    modalCreateColumn;
    dropDownPic;
    dropDownLevel;
    dropDownSatuan;
    lastClickCell = 'A1';
    isFormulaMode = false;
    kindOfInstance;
    isEditable;
    childInstance;
    cellHighlights = [];

    // The constructor method is automatically called when a new object is created
    constructor() {
        super();
    }
    init(childInstance = undefined, kindOfInstance = 'singleton', isEditable = false) {
        this.childInstance = childInstance;
        this.kindOfInstance = kindOfInstance;
        this.isEditable = isEditable;
        console.log(isInitialized, this.kindOfInstance);
        if (isInitialized && this.kindOfInstance == 'singleton') {
            console.log("Instance sudah aktif, inisialisasi dibatalkan.");
            return;
        }
        this.initKendo();
        this.initModal();
        this.initSelectize();
        this.initKendoWindow();
        this.initContentBehaviour();
        isInitialized = true;
    }
    setWorkSheet(worksheet, worksheetName, readOnlyColumn = []) {
        this.worksheet = worksheet;
        this.worksheetName = worksheetName;
        this.initHtmlStyling();
        if(this.isEditable){
            // change colour of column isEditable
        }
        super.setWorksheet(this, this.worksheet, this.worksheetName, readOnlyColumn);
        super.initDataInstance();
    }
    getFormulaMode() {
        return this.isFormulaMode;
    }
    setFormulaMode(mode) {
        this.isFormulaMode = mode;
    }
    getLastClickCell() {
        return this.lastClickCell;
    }
    setLastClickCell(lastClickCell) {
        this.lastClickCell = lastClickCell;
    }
    setCallBePromiseOnLoad(keyName, value) {
        // Cari apakah key sudah pernah ada di array
        let existingObj = callBePromiseOnLoad.find(obj => obj.hasOwnProperty(keyName));
        if (existingObj) {
            // Kalau ketemu, update isinya
            existingObj[keyName] = value;
        } else {
            // Kalau gak ada, baru push data baru
            callBePromiseOnLoad.push({ [keyName]: value });
        }
    }
    getCallBePromiseOnLoad() {
        // console.log(JSON.stringify(callBePromiseOnLoad));
        return callBePromiseOnLoad;
    }
    initKendo() {
        $("#tahun").kendoDatePicker({
            // Sets the format of the input field
            format: "yyyy",
            // The view that is initially shown when the calendar opens
            start: "decade",
            // The most detailed view the user can select (prevents navigating to month/day views)
            depth: "decade",
            value: new Date()
        });
        // Change the default icon to a different one (e.g., k-i-calendar-date)
        $(".k-icon").removeClass("k-i-calendar").addClass('k-i-calendar-date');

        let $search = $('#search');
        if ($search.length) {
            let data = [
                { text: "Deviasi Ekspor Impor", value: "1" },
                { text: "NKO", value: "2" },
                { text: "Capaian", value: "3" }
            ];
            $search.kendoDropDownList({
                dataTextField: "text",
                dataValueField: "value",
                dataSource: data,
                index: 0
            });
        }
    }
    initModal() {
        this.modalCreateColumn = UIkit.modal($('#modal_overflow'));
    }
    initSelectize() {
        let self = this;
        // init dropdown pic
        this.dropDownPic = $('#drop_pic').selectize({
            plugins: {
                'remove_button': {
                    label: ''
                }
            },
            options: [
                { id: 1, text: 'DMEP' },
                { id: 2, text: 'DMOT' },
                { id: 3, text: 'DMEP' },
                { id: 4, text: 'DMBS' },
            ],
            maxItems: null,
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            create: false,
            render: {
                option: function (data, escape) {
                    return '<div class="option">' +
                        '<span class="title">' + escape(data.text) + '</span>' +
                        '</div>';
                },
                item: function (data, escape) {
                    return '<div class="item">' + escape(data.text) + '</div>';
                }
            },
            create: function (input) {
                return { id: 123, text: input };
            },
            onDropdownOpen: function ($dropdown) {
                $dropdown
                    .hide()
                    .velocity('slideDown', {
                        begin: function () {
                            $dropdown.css({ 'margin-top': '0' })
                        },
                        duration: 200,
                        easing: easing_swiftOut
                    })
            },
            onDropdownClose: function ($dropdown) {
                $dropdown
                    .show()
                    .velocity('slideUp', {
                        complete: function () {
                            $dropdown.css({ 'margin-top': '' })
                        },
                        duration: 200,
                        easing: easing_swiftOut
                    })
            }
        });

        // init dropdown level
        this.dropDownLevel = $('#drop_level').selectize({
            plugins: {
                'remove_button': {
                    label: ''
                }
            },
            options: [
                { id: 1, text: 'IKSP' },
                { id: 2, text: 'IKSK-2' },
                { id: 3, text: 'IKSK-3' },
                { id: 4, text: 'IKSK-4' },
            ],
            maxItems: null,
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            create: false,
            render: {
                option: function (data, escape) {
                    return '<div class="option">' +
                        '<span class="title">' + escape(data.text) + '</span>' +
                        '</div>';
                },
                item: function (data, escape) {
                    return '<div class="item">' + escape(data.text) + '</div>';
                }
            },
            create: function (input) {
                return { id: 123, text: input };
            },
            onDropdownOpen: function ($dropdown) {
                $dropdown
                    .hide()
                    .velocity('slideDown', {
                        begin: function () {
                            $dropdown.css({ 'margin-top': '0' })
                        },
                        duration: 200,
                        easing: easing_swiftOut
                    })
            },
            onDropdownClose: function ($dropdown) {
                $dropdown
                    .show()
                    .velocity('slideUp', {
                        complete: function () {
                            $dropdown.css({ 'margin-top': '' })
                        },
                        duration: 200,
                        easing: easing_swiftOut
                    })
            }
        });

        // init dropdown satuan
        this.dropDownSatuan = $('#drop_satuan').selectize({
            plugins: {
                'remove_button': {
                    label: ''
                }
            },
            options: [
                { id: 1, text: 'MMSTB' },
                { id: 2, text: 'TCF' },
                { id: 3, text: 'BBOE' },
                { id: 4, text: 'km2' },
                { id: 5, text: 'sumur' },
                { id: 6, text: 'Jumlah Rekom POD I' },
                { id: 7, text: 'Jumlah Evaluasi' },
                { id: 8, text: 'Jumlah Rekomendasi' },
                { id: 9, text: 'Dokumen/SR' },
                { id: 10, text: 'Ruas' },
                { id: 11, text: 'ton/d' },
                { id: 12, text: 'mton' },
                { id: 13, text: 'RIBU BCPD' },
                { id: 14, text: 'RIBU BOPD' },
                { id: 15, text: 'JUTA TON' },
                { id: 16, text: 'Tingkat Kepuasan' },
                { id: 17, text: 'Jumlah Penandasahan' },
                { id: 18, text: 'Level' },
                { id: 19, text: 'Nilai SAKIP' },
                { id: 20, text: 'Jumlah Komponen' },
                { id: 21, text: '%' }
            ],
            maxItems: null,
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            create: false,
            render: {
                option: function (data, escape) {
                    return '<div class="option">' +
                        '<span class="title">' + escape(data.text) + '</span>' +
                        '</div>';
                },
                item: function (data, escape) {
                    return '<div class="item">' + escape(data.text) + '</div>';
                }
            },
            create: function (input) {
                return { id: 123, text: input };
            },
            onDropdownOpen: function ($dropdown) {
                $dropdown
                    .hide()
                    .velocity('slideDown', {
                        begin: function () {
                            $dropdown.css({ 'margin-top': '0' })
                        },
                        duration: 200,
                        easing: easing_swiftOut
                    })
            },
            onDropdownClose: function ($dropdown) {
                $dropdown
                    .show()
                    .velocity('slideUp', {
                        complete: function () {
                            $dropdown.css({ 'margin-top': '' })
                        },
                        duration: 200,
                        easing: easing_swiftOut
                    })
            }
        });

        $('#jenis_input').on('change', function () {
            // This function will execute every time the selected option changes
            let selectedValue = $(this).val();
            let selectedText = $(this).find('option:selected').text();
            console.log('Selected Value: ' + selectedValue);
            console.log('Selected Text: ' + selectedText);
            if (selectedText == 'Input') {
                self.hideSelectize('all');
            } else if (selectedText == 'Dropown PIC') {
                self.showSelectize('pic');
            } else if (selectedText == 'Dropown Level PIC') {
                self.showSelectize('level');
            } else {
                self.showSelectize('satuan');
            }
        });
        // default hide all
        this.hideSelectize('all');
    }
    initKendoWindow() {
        var $kUI_window = $('#kUI_window_custom');
        var $kUI_undo = $('#kUI_undo');
        if ($kUI_window.length) {
            var k_window = $kUI_window,
                k_undo = $kUI_undo
                    .bind("click", function () {
                        k_window.data("kendoWindow").open();
                        k_undo.hide();
                    });

            var onClose = function () {
                k_undo.show();
            };

            if (!k_window.data("kendoWindow")) {
                k_window.kendoWindow({
                    width: "800px",
                    title: "Formula Editor",
                    actions: [
                        "Pin",
                        "Minimize",
                        // "Maximize",
                        // "Close"
                    ],
                    close: onClose
                });
            }
        }
    }
    initContentBehaviour() {
        let self = this;
        // --------------------------------responsive layout jspreadsheet----------------------
        // 1. Siapkan satu objek kosong sebagai penampung (di luar loop/fungsi)
        let observerPageContentInerResize = {};
        // 2. Masukkan instance-nya ke dalam properti objek menggunakan key dinamis
        observerPageContentInerResize[this.worksheetName] = new ResizeObserver(entries => {
            for (let entry of entries) {
                if (entry.contentRect.width > 0) {
                    const newWidth = entry.contentRect.width + 'px';
                    // Update CSS Variable
                    document.documentElement.style.setProperty('--jss-global-width', newWidth);
                }
            }
        });
        // 3. Jalankan observasi
        const allWrappers = document.querySelectorAll('.md-card-content');
        allWrappers.forEach(wrapper => {
            observerPageContentInerResize[this.worksheetName].observe(wrapper);
        });

        // --------------------------------binding event----------------------
        // Bind change event to #formula_editor
        $('#formula_editor').on('keydown.' + this.worksheetName, (e) => {
            console.log('akan mengedit : ', self.lastClickCell);
            let targetCell = self.lastClickCell;
            // Ini kuncinya! Supaya Jspreadsheet nggak tau kalau kamu lagi ngetik
            // e.stopPropagation();
            // Check if the pressed key is Enter (key code 13)
            let coords = jspreadsheet.helpers.getCoordsFromCellName(targetCell);
            let x = coords[0];
            let y = coords[1];
            let cell = this.worksheet.getCellFromCoords(x, y);
            let formula = $('#formula_editor').val();
            self.isFormulaMode = formula.startsWith('=');
            console.log("isFormulaMode : " + self.isFormulaMode);
            console.log("isEditable : " + self.isEditable);
            const keysShifter = [37, 38, 39, 40]; // kanan, kiri, atas, bawah
            if (keysShifter.includes(e.which)) {
                if (self.isFormulaMode) return; // disable pindah kanan, kiri, atas, bawah ke cell lain
                e.preventDefault(); // Stop scroll browser
                this.shiftRow(e.which);
            } else if (e.which === 13) { // Tombol enter
                // e.preventDefault(); // Optional: prevent form submission or default action
                if (self.isEditable) {
                    self.submitEditor(formula, targetCell, e.which);
                }
                // self.shiftRow(13);
            } else if (e.which === 9) { // Tombol Tab
                // e.preventDefault(); // Stop browser pindah fokus ke elemen HTML lain
                if (self.isEditable) {
                    self.submitEditor(formula, targetCell, e.which);
                }
                // self.shiftRow(9);
            } else if (e.which === 46) { // Tombol Delete (46) atau Backspace (8)
                e.preventDefault()
                self.deleteSelectedRange();
            } else if (e.which === 27) {
                e.preventDefault()
                $('#formula_editor').val("");
                $('#formula_editor').blur();
                this.isFormulaMode = false;
            }
        });

        // 1. Tangkap semua link di dalam navigasi tab
        $('#tabs_nav').on('click.' + this.worksheetName, 'li', function (e) {
            e.preventDefault();
            e.stopPropagation();

            // 1. Logic Show/Hide tab
            let target = $(this).data('target');
            console.log(target);
            $('.wrapper_content').hide();

            //animate content
            altair_md.card_show_hide($('#' + target), undefined, undefined, $('#' + target).show())
            //  $('#' + target).show();

            // change title and set active spreadsheet
            if (target == 'spreadSheetInput') {
                $('#headerPanel').html("Report INPUT");
            } else if (target == 'spreadSheetCapaian') {
                $('#headerPanel').html("Report CAPAIAN");
            } else if (target == 'spreadSheetScale') {
                $('#headerPanel').html("Report SCALE");
            } else if (target == 'spreadSheetKonversiTahapan') {
                $('#headerPanel').html("Report KONVERSI TAHAPAN");
            } else if (target == 'spreadSheetSheet1') {
                $('#headerPanel').html("Report SHEET1");
            } else if (target == 'spreadSheetNko') {
                $('#headerPanel').html("Report NKO");
            } else if (target == 'spreadSheetDeviasiEksporImpor') {
                $('#headerPanel').html("Report DEVIASI EKSPOR IMPOR");
            } else if (target == 'spreadSheetIndexKeselamatan') {
                $('#headerPanel').html("Report INDEX KESELAMATAN");
            } else if (target == 'spreadSheetKetersediaan') {
                $('#headerPanel').html("Report KETERSEDIAAN");
            } else if (target == 'spreadSheetHarga') {
                $('#headerPanel').html("Report HARGA");
            } else if (target == 'spreadSheetIndexAksesibilitas') {
                $('#headerPanel').html("Report INDEX AKSESIBILITAS");
            } else if (target == 'spreadSheetTurunanIpAsn') {
                $('#headerPanel').html("Report TURUNAN IP ASN");
            }

            // PENTING: JSpreadsheet butuh refresh koordinat pas show
            // (Karena pas hide ukurannya 0, pas show harus dihitung ulang)
            if (window.currentSheet) {
                window.dispatchEvent(new Event('resize'));
            }

            // // hide dropdown sheet
            var dropdown = UIkit.dropdown($('#dropdown-sheet'));
            if (dropdown) {
                dropdown.hide();
                $('#sheetButton').blur();
            }
        });
    }
    initHtmlStyling() {
        self = this;
        //------------------------------date picker---------------------------------
        $('.k-widget.k-datepicker.k-header').attr("style", "top:0px !important; padding-left: 0px !important");
        // --------------------------------reinit kendo window----------------------
        // let posisiKiri = $('#kUI_window_custom').offset().left;
        // // console.log("posisi kiri : " + posisiKiri);
        // setTimeout(function () {
        //     let lebarPageContent = $('#page_content').width();
        //     let lebarSidebar = 0;

        //     let lebarHeading_b = $('.heading_b ').width();
        //     let lebarYearSelection = $('#yearSelection ').width();
        //     let tinggiHeader = $('#header_main').height();

        //     $('.k-widget.k-window').attr("style", "padding-top: 38px; \
        //         min-width: 90px; \
        //         min-height: 50px; \
        //         // width: 1000px; \
        //         // width: " + (screen.width - lebarSidebar - lebarHeading_b - lebarYearSelection - lebarYearSelection - (24 * 6) - 6) + "px; \
        //         width: " + (lebarPageContent - lebarHeading_b - lebarYearSelection - (24 * 3)) + "px; \
        //         top: " + (tinggiHeader + 15) + "px; \
        //         // top: 0px; \
        //         // left: " + (lebarSidebar + lebarHeading_b + lebarYearSelection + 24 + 24 + 24 + 6) + "px; \
        //         left: " + (posisiKiri + lebarHeading_b + 6) + "px; \
        //         z-index: 10003; \
        //         touch-action: pan-y; \
        //         position: fixed !important;");
        //     // auto minimize
        //     // $('.k-icon.k-i-minimize').trigger('click');
        // }, 10);

        // --------------------------------reinit style paging and search jspreadsheet with altair----------------------
        // input
        $('.jss_search').each(function () {
            let $input = $(this);
            // 1. Tambah class 'md-input'
            $input.addClass('md-input');
            // 2. Hapus teks "Search:" di parent masing-masing
            $input.parent()
                .contents()
                .filter(function () { return this.nodeType === 3; })
                .remove();
            // 3. Tambahkan <label> sebelum input ini
            $input.before('<label>Search</label>');
            // 4. Tambahkan class ke parent masing-masing
            $input.parent().addClass('uk-width-medium-2-5');
        });

        // select
        $('.jss_pagination_dropdown').each(function () {
            // 1. Pilih elemen select-nya
            let $select = $(this);
            // 2. Tambah class 'md-input' dan hapus teks "Search:" di parent-nya
            $select.addClass('md-input');
            // 3. hapus semua text di parent
            $select.parent().contents().filter(function () {
                return this.nodeType === 3; // Pilih semua teks ("Show" & "entries")
            }).remove();
            // 4. add class uk-width-medium-1-5 di parentnya
            $select.parent().addClass('uk-width-medium-1-5');
        });

        // table
        $('.jss_worksheet').each(function () {
            let $table = $(this);
            $table.addClass('uk-table').addClass('uk-table-hover');
            $('<style>')
                .prop('type', 'text/css')
                .html(`
                    /* Baris Genap (Warna Dasar: Ungu Muda) */
                    .jss_worksheet tbody tr:nth-child(even) {
                        background-color: #EEE9F1;
                    }

                    /* Baris Ganjil (Putih) */
                    .jss_worksheet tbody tr:nth-child(odd) {
                        background-color: #FFFFFF;
                    }

                    /* Hover pada Baris (Dibuat Lebih Gelap dari #EEE9F1) */
                    .jss_worksheet tbody tr:hover,
                    .jss_worksheet tbody tr:nth-child(even):hover {
                        background-color: #D6CFE2; /* Ungu lebih gelap */
                    }

                    /* Hover pada Sel Individual (Jika ingin lebih gelap lagi saat kursor tepat di atas sel) */
                    .jss_worksheet tbody td:hover {
                        background-color: #C1B5D4; /* Fokus sel lebih gelap */
                    }

                    .jss_worksheet tbody td.cell-highlight {
                        background-color: #ce0000 !important;
                        color: white !important;
                    }
                `)
                .appendTo('head');
        });

        // // reinit md
        altair_md.inputs();
    }
    applyHighlight(x, y, isTargetRemove = false) {
        if(!isTargetRemove){
            let coordinate = x + "-" + y;
            if (!this.cellHighlights.includes(coordinate)) {
                this.cellHighlights.push(coordinate);
            }
        }
        this.cellHighlights.forEach(coord => {
            let [x, y] = coord.split('-');
            // Cari elemennya dan hajar pake !important biar menang lawan ungu/putih
            $(`.jss_worksheet td[data-x="${x}"][data-y="${y}"]`).addClass('cell-highlight');
        });
    }
    removeHighlight(x, y){
        let coordinate = x + "-" + y;
        this.cellHighlights = this.cellHighlights.filter(item => item !== coordinate);
        this.applyHighlight(x, y, true);
    }

    hideSelectize(elName) {
        if (elName == 'all') {
            // hide selectize
            $('#wrapper_pic').hide();
            // hide selectize
            $('#wrapper_level').hide();
            // hide selectize
            $('#wrapper_satuan').hide();
        }
    }
    showSelectize(elName) {
        if (elName == 'pic') {
            $('#wrapper_pic').show();
            $('#wrapper_level').hide();
            $('#wrapper_satuan').hide();
        } else if (elName == 'level') {
            $('#wrapper_pic').hide();
            $('#wrapper_level').show();
            $('#wrapper_satuan').hide();
        } else if (elName == 'satuan') {
            $('#wrapper_pic').hide();
            $('#wrapper_level').hide();
            $('#wrapper_satuan').show();
        }
    }
}
