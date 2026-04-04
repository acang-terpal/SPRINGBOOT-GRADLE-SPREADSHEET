class ForecastPlugin extends HyperFormula.FunctionPlugin {
    forecast(ast, state) {
        return this.runFunction(ast.args, state, this.metadata('FORECAST'), (x, knownY, knownX) => {
            const yArr = Array.isArray(knownY) ? knownY.flat() : [knownY];
            const xArr = Array.isArray(knownX) ? knownX.flat() : [knownX];
            const yNums = yArr.map(v => parseFloat(v)).filter(v => !isNaN(v));
            const xNums = xArr.map(v => parseFloat(v)).filter(v => !isNaN(v));
            if (yNums.length === 0 || yNums.length !== xNums.length) return "#N/A";
            const n = yNums.length;
            let sX = 0, sY = 0, sXY = 0, sXX = 0;
            for (let i = 0; i < n; i++) {
                sX += xNums[i]; sY += yNums[i];
                sXY += (xNums[i] * yNums[i]); sXX += (xNums[i] * xNums[i]);
            }
            const den = (n * sXX - Math.pow(sX, 2));
            if (den === 0) return "#DIV/0!";
            const b = (n * sXY - sX * sY) / den;
            const a = (sY / n) - (b * (sX / n));
            return a + (b * x);
        });
    }
}

// INI KUNCINYA BOR! (Hapus error "expected string for code")
ForecastPlugin.implementedFunctions = {
    FORECAST: {
        method: 'forecast',
        id: 'FORECAST', // Properti 'id' di sini dianggap 'code' oleh engine v3
        parameters: [
            { argumentType: HyperFormula.FunctionArgumentType.NUMBER },
            { argumentType: HyperFormula.FunctionArgumentType.RANGE },
            { argumentType: HyperFormula.FunctionArgumentType.RANGE }
        ],
    },
};

// Tambahin ID statis juga buat jaga-jaga
ForecastPlugin.id = 'FORECAST';

// Registrasi pake Translation yang bener
HyperFormula.registerFunctionPlugin(ForecastPlugin, { enUS: { MYFORECAST: 'MYFORECAST' } });

class HyperFormulaClass {
    hfInstance;
    sheetName;
    sheetId;
    datasourceInput;

    constructor() {
        //console.log("test");
    }

    setDataSource(datasourceInput) {
        this.datasourceInput = datasourceInput;
    }

    initHyperFormula() {
        // Konfigurasi HyperFormula
        this.hfInstance = HyperFormula.buildEmpty({
            licenseKey: 'gpl-v3', // Lisensi open source
            useArrayArithmetic: true,
        });

        // Tambahkan sheet bernama 'Input' ke engine
        this.sheetName = this.hfInstance.addSheet('Input');
        this.sheetId = this.hfInstance.getSheetId(this.sheetName);
        // Seed HyperFormula with initial data
        // this.hfInstance.setCellContents({ sheet: this.sheetId, row: 0, col: 0 }, this.datasourceInput);
        // Masukkan semua data awal ke HyperFormula
        this.hfInstance.setSheetContent(this.sheetId, this.datasourceInput);
        // this.syncFormulaResults();

        console.log(this.hfInstance.getRegisteredFunctionNames());
        console.log(this.hfInstance.getRegisteredFunctionNames().includes('FORECAST'));
        console.log(this.hfInstance.getAllFunctionPlugins());
        // read the value of cell H3
        const result = this.hfInstance.getCellValue({ sheet: 0, col: 7, row: 2 });
        // cell B1 should evaluate to 'Anthony'
        console.log(result);
    }

    syncFormulaResults(instanceJspreadSheet, x, y, value) {
        this.hfInstance.setCellContents({
            col: parseInt(x),
            row: parseInt(y),
            sheet: this.sheetId
        }, [[value]]);
        // Ambil semua hasil kalkulasi (berupa array 2D) dari HyperFormula
        const calculatedValues = this.hfInstance.getSheetValues(this.sheetId);
        // console.log(calculatedValues);

        // Matikan event sementara agar tidak terjadi looping (infinite loop)
        instanceJspreadSheet.ignoreEvents = true;
        instanceJspreadSheet.ignoreHistory = true;

        // this line code have purpose to collect all raw formula
        calculatedValues.forEach((row, r) => {
            row.forEach((value, c) => {
                if (value != '#DIV/0!'
                    && value != '#VALUE!'
                    && value != '#REF!'
                    && value != '#NUM!'
                    && value != '#CYCLE!'
                    && value != '#ERROR!'
                    && value != '#NAME?') {
                    // Ambil formula asli dari sel (misal: "=SUM(A1:B1)")
                    const rawValue = instanceJspreadSheet.getValueFromCoords(c, r, false);
                    // Jika sel tersebut memang berisi formula, update dataFormula array
                    if (rawValue && rawValue.toString().startsWith('=')) {

                    }
                } else {

                }
            });
        });

        // const addressA1Notation = hfInstance.simpleCellAddressFromString('A1', sheetId);
        // console.log(addressA1Notation);
        let address = { sheet: this.sheetId, col: parseInt(x), row: parseInt(y) };
        // console.log(address);
        value = this.hfInstance.getCellValue(address);
        // instanceJspreadSheet.setValueFromCoords(x, y, value, true); // true = force update without triggering onchange again
        // Hidupkan kembali event
        instanceJspreadSheet.ignoreEvents = false;
        instanceJspreadSheet.ignoreHistory = false;
        return value;
    }
}
