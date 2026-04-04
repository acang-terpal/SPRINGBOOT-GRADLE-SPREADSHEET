let NEGATIVE = function (v) {
    return -1 * v;
}

// own offset function
let OFFSET = function (cellName, rows, cols, height, width) {
    // Ambil instance spreadsheet (sesuaikan ID-nya)
    let instance = document.getElementById('spreadSheetInput').jspreadsheet;

    // Konversi nama sel (misal 'A1') menjadi koordinat [x, y]
    let coords = jspreadsheet.helpers.getCoordsFromColumnName(cellName);
    let startX = parseInt(coords[0]) + (cols || 0);
    let startY = parseInt(coords[1]) + (rows || 0);

    // Default height dan width adalah 1 jika tidak diisi
    height = height || 1;
    width = width || 1;

    if (height === 1 && width === 1) {
        return instance.getValueFromCoords(startX, startY);
    } else {
        // Jika range, ambil data dalam bentuk array (perlu penanganan tambahan di cell)
        return instance.getData(false); // Sederhananya return raw data
    }
};

let FORECAST = function (x, knownY, knownX) {
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
}
