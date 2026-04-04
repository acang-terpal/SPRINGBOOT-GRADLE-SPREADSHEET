class EditorClass {
    observer;
    instance;
    cell;
    x;
    y;
    value;
    isOnEditingFormula;

    constructor(instance, cell, x, y, value) {
        this.instance = instance;
        this.cell = cell;
        this.x = x;
        this.y = y;
        this.value = value;
        this.init();
    }

    init() {
        let self = this;
        let cellName = jspreadsheet.helpers.getCellNameFromCoords(this.x, this.y);
        // Access the editor's input element
        // 'cell' is the <td> element. Find the editor inside it.
        // Use a small timeout to ensure the editor is fully rendered in the DOM
        // setTimeout(() => {
        //     // Find the active input or textarea
        //     const editor = cell.querySelector('input, textarea');
        //     console.log(editor);
        //     if (editor) {
        //         editor.addEventListener('input', function(e) {
        //             console.log('Real-time typing:', e.target.value);
        //         });
        //     }
        // }, 500); // 0 is good, but greater number is ok

        // or this way
        // let checkCount = 0;
        // const interval = setInterval(() => {
        //     const editor = cell.querySelector('input, textarea');
        //     console.log(editor);
        //     checkCount++;
        //     if (editor) {
        //         console.log("Editor found!");
        //         editor.addEventListener('input', (e) => console.log('Real-time typing:', e.target.value));
        //         clearInterval(interval); // Stop the loop
        //     }
        //     // Safety: stop searching after 2 seconds (20 attempts)
        //     if (checkCount > 20) clearInterval(interval);
        // }, 100); // Check every 100ms

        // or this way (more elegant but not the best)
        // const findEditor = () => {
        //     const editor = cell.querySelector('input, textarea');
        //     console.log(editor);
        //     if (editor) {
        //         editor.addEventListener('input', (e) => console.log('Real-time typing:', e.target.value));
        //     } else {
        //         // If not found, wait for the next animation frame and try again
        //         requestAnimationFrame(findEditor);
        //     }
        // };
        // findEditor();

        // or with this way (the best)
        self.observer = new MutationObserver((mutations, obs) => {
            let editor = self.cell.querySelector('input, textarea');
            // console.log("the editor object :" + editor); console.log(editor);
            if (editor) {
                obs.disconnect(); // Stop observing once found
                // reset cell with last meta formula
                editor.value = self.instance.getMeta(cellName).meta.formula;
                let formulaSuggest = new FormulaSuggestClass(self.instance, self.cell, self.x, self.y, self.value, editor);
                // Attach the typing listener
                editor.addEventListener('input', (e) => {
                    let val = e.target.value;
                    console.log('Real-time typing:', val)
                    $('#formula_editor').val(val);
                    // Check if it starts with =
                    if (val.startsWith('=')) {
                        console.log("Formula detected:", val);
                        self.isOnEditingFormula = true;
                        // --------------------Optional: tab Style the text to show suggest it's a formula
                        formulaSuggest.check(val);
                        // --------------------end Optional: Style the text to show suggest it's a formula
                    } else {
                        editor.style.color = 'black';
                        editor.style.fontWeight = 'normal';
                    }
                });

            }
        });
        self.observer.observe(self.cell, { childList: true });
    }

    destroy() {
        if (this.observer) {
            this.observer.disconnect(); // Berhenti memantau DOM
            this.observer = null;
            console.log('Observer dihentikan.');
        }
    }
}
