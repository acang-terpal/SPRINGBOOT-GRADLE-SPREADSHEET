class FormulaSuggestClass {
    observer;
    instance;
    cell;
    x;
    y;
    value;
    editor;
    helper;
    formulaList = ['SUM(', 'AVERAGE(', 'COUNT(', 'MAX(', 'MIN(', 'IF(', 'CONCATENATE('];

    constructor(instance, cell, x, y, value, editor) {
        this.instance = instance;
        this.cell = cell;
        this.x = x;
        this.y = y;
        this.value = value;
        this.editor = editor;

        this.helper = document.createElement('div');
        this.helper.style = "position:absolute; background:white; border:1px solid #ccc; z-index:1000; display:none; box-shadow: 2px 2px 5px rgba(0,0,0,0.2); font-family: sans-serif; font-size: 12px;";
        document.body.appendChild(this.helper);
        // handler user to be able to pick the top suggestion by hitting the Tab key (like Excel)
        this.editor.addEventListener('keydown', (e) => {
            if (e.key === 'Tab' && this.helper.style.display === 'block') {
                // 1. Stop Jspreadsheet from moving to the next cell
                e.preventDefault();
                e.stopImmediatePropagation();
                // 2. Pick the first suggestion
                let firstHint = this.helper.querySelector('.hint-item');
                if (firstHint) {
                    this.editor.value = '=' + firstHint.innerText;
                    this.helper.style.display = 'none';
                    setTimeout(() => this.helper.remove(), 200);
                    // 3. Keep the cursor at the end so they can type the range
                    let len = this.editor.value.length;
                    this.editor.setSelectionRange(len, len);
                }
            }
        });
        // Cleanup: Remove helper when user stops editing
        this.editor.addEventListener('blur', () => {
            setTimeout(() => this.helper.remove(), 200);
        });
    }

    check(val) {
        let self = this;
        let cellName = jspreadsheet.helpers.getCellNameFromCoords(this.x, this.y);
        self.editor.style.color = 'blue';
        self.editor.style.fontWeight = 'bold';
        let search = val.substring(1); // Remove the '='
        let matches = self.formulaList.filter(f => f.startsWith(search.toUpperCase()));
        if (matches.length > 0 && search.length > 0) {
            // Position the helper right under the cell
            let rect = self.editor.getBoundingClientRect();
            self.helper.style.left = rect.left + 'px';
            self.helper.style.top = rect.bottom + 'px';
            self.helper.style.display = 'block';

            // Build the list of suggestions
            self.helper.innerHTML = matches.map(m => `<div class="hint-item" style="padding:5px; cursor:pointer;">${m}</div>`).join('');

            // Add click event to suggestions
            self.helper.querySelectorAll('.hint-item').forEach(item => {
                item.onmousedown = (event) => {
                    // 1. Prevent the spreadsheet from "blurring" the editor
                    event.preventDefault();
                    event.stopPropagation();

                    let selectedFormula = item.innerText;
                    self.editor.value = '=' + selectedFormula;

                    // 2. Hide helper
                    self.helper.style.display = 'none';

                    // 3. Force focus back using a timeout to override Jspreadsheet's focus manager
                    setTimeout(() => {
                        self.editor.focus();
                        // Move cursor to the end of the text
                        let len = self.editor.value.length;
                        self.editor.setSelectionRange(len, len);
                    }, 0);
                    setTimeout(() => this.helper.remove(), 200);
                };
            });
        } else {
            self.helper.style.display = 'none';
        }
    }
}
