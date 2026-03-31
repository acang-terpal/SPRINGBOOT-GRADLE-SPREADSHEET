$(document).ready(function () {
    //detect mobile browser
    if (/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/|/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(navigator.userAgent||navigator.vendor||window.opera)) {
        console.log('detect mobile browser')
        UIkit.notify({
            message : '<a class=\'notify-action\'>Remove</a> Please use desktop browser to enable task moving!',
            status  : 'info',
            timeout : 0,
            pos     : 'top-center'
        });
        ctrlScrumDragable.isMobile = true;
    } else {
        console.log('mobile browser not detected')
        ctrlScrumDragable.isMobile = false;
    }

    // (function(a,b){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4)))window.location=b})(navigator.userAgent||navigator.vendor||window.opera,'http://detectmobilebrowser.com/mobile');

    //disable scroll when task on drag
    var lastY;
    var listener = function (e) {
        if (!ctrlScrumDragable.scrollable) {
            e.preventDefault(); // does nothing since the listener is passive 
        }
    }
    document.addEventListener('touchmove', listener, { passive: false });
    // draggable tasks
    ctrlScrumDragable.init();
    ctrlScrum.init();
    ctrlScrum.reinitColumnWidth();
});

ctrlHelper = {
    randomString: function(len, charSet){
        charSet = charSet || 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        var randomString = '';
        for (var i = 0; i < len; i++) {
            var randomPoz = Math.floor(Math.random() * charSet.length);
            randomString += charSet.substring(randomPoz,randomPoz+1);
        }
        return randomString;
    }
}

ctrlScrumDragable = {
    isMobile: false,
    drake: null,
    scrollable: true,
    init: function () {
        //drake config
        //allows the user to drag elements from left into right, and from right into left.
        ctrlScrumDragable.drake = dragula({
            isContainer: function (el) {
                //console.log(el.classList.contains('dragula-container'));
                return el.classList.contains('dragula-container');
                //return false; // only elements in drake.containers will be taken into account
            },
            moves: function (el, source, handle, sibling) {
                if(ctrlScrumDragable.isMobile){
                    return false; //disable drag on mobile
                }
                return true; // elements are always draggable by default
            },
            accepts: function (el, target, source, sibling) {
                if(ctrlScrumDragable.isMobile){
                    return false; //disable drag on mobile
                }
                return true; // elements can be dropped in any of the `containers` by default
            },
            invalid: function (el, handle) {
                return false; // don't prevent any drags from initiating by default
            },
            direction: 'vertical',             // y axis is considered when determining where an element would be dropped
            copy: false,                       // elements are moved by default, not copied
            copySortSource: false,             // elements in copy-source containers can be reordered
            revertOnSpill: true,              // spilling will put the element back where it was dragged from, if this is true
            removeOnSpill: false,              // spilling will `.remove` the element, if this is true
            mirrorContainer: document.body,    // set the element that gets mirror elements appended
            ignoreInputTextSelection: true,     // allows users to select input text, see details below
            slideFactorX: 0,               // allows users to select the amount of movement on the X axis before it is considered a drag instead of a click
            slideFactorY: 0,               // allows users to select the amount of movement on the Y axis before it is considered a drag instead of a click
        });
        // ctrlScrumDragable.drake = dragula({});
        ctrlScrumDragable.draggableTasks();
    },
    reInit: function () {
        //reinit drake is not necessary
        // ctrlScrumDragable.removeOnClickListenerActionTask();
        // ctrlScrumDragable.drake.destroy();
        // ctrlScrumDragable.init();
        ctrlScrum.modifyArrayTaskColumnHtmlData();
    },
    draggableTasks: function () {
        // var drake = dragula([
        //     $('#scrum_column_todo')[0],
        //     $('#scrum_column_inAnalysis')[0],
        //     $('#scrum_column_inProgress')[0],
        //     $('#scrum_column_done')[0],
        //     $('#scrum_column_drop')[0]
        // ]);

        //--------------------init column--------------
        var rawData = $.trim($("#listColumn").html().replace(/\s/g, ""));
        //console.log(rawData);
        var jArrListColumnIdStr = rawData.substring(0, rawData.length - 2) + "]";
        // console.log(jArrListColumnIdStr);
        if(rawData != '[]'){
            var jArrListColumnId = JSON.parse(jArrListColumnIdStr);
            $.each(jArrListColumnId, function (index, val) {
                var elementColumn = $('#scrum_column_' + val)[0];
                //console.log(elementColumn);
                ctrlScrumDragable.drake.containers.push(elementColumn);
            });
        }
        //console.log(ctrlScrumDragable.drake.containers);
        //--------------------end init column-------------

        for (var i = 0; i < ctrlScrumDragable.drake.containers.length; i++) {
            $(ctrlScrumDragable.drake.containers[i]).addClass('dragula dragula-vertical'); //show scroll vertical on each column
            $(ctrlScrumDragable.drake.containers[i]).css('height', '99%'); //show scroll vertical on each column when its full
        }

        ctrlScrumDragable.drake.on('drag', function (el, source) {
            ctrlScrumDragable.scrollable = false;
            // console.log('drag');
            // console.log(el);
            // console.log(source);
            // console.log('------------------------------------------------------------')
        })
        ctrlScrumDragable.drake.on('dragend', function (el, source) {
            ctrlScrumDragable.scrollable = true;
            // console.log('dragend');
            // console.log(el);
            // //console.log(source);
            // console.log('------------------------------------------------------------')
        })
        ctrlScrumDragable.drake.on('drop', function (el, target, source, sibling) {
            ctrlScrumDragable.scrollable = true;
            //console.log('drop');
            ctrlScrumDragable.drakeOnDropElement(el, target, source);
            // console.log('------------------------------------------------------------')
            //code send to backend drag change
        })
    },
    drakeOnDropElement: function (el, target, source) {
        // console.log(el);
        // console.log(target);
        //console.log(source);
        columnIdSource = source.id.split('scrum_column_');
        // console.log('source column : ' + columnIdSource[1]);
        // console.log('source taskId : ' + taskId);
        taskIdMove = el.id.split('_task_');
        columnIdMove = target.id.split('scrum_column_');
        //change id of element target
        $(el).attr('id', columnIdMove[1] + '_task_' + taskIdMove[1]);
        $('#' + columnIdSource[1] + '_scrum_task_delete_' + taskIdMove[1]).attr('id', columnIdMove[1] + '_scrum_task_delete_' + taskIdMove[1]);
        // $('#')
        //find the order number
        i = 0;
        dataTaskArr = []
        $(target).children('div').each(function () {
            //console.log(this.id); // "this" is the current element in the loop
            taskId = this.id.split('_task_');
            dataTaskObj = {}
            dataTaskObj.task_id = taskId[1];
            dataTaskObj.order_num = i;
            dataTaskArr.push(dataTaskObj);
            i++;
        });
        //console.log(JSON.stringify(dataTaskArr));
        dataToSend = {
            'task_id': taskIdMove[1],
            'column_id': columnIdMove[1],
            'order_number': JSON.stringify(dataTaskArr)
        }
        $.ajax({
            url: $("#webRootUrl").html() + 'ControllerMamenScrum/moveTask',
            type: 'POST',
            async: true,
            cache: false,
            data: dataToSend,
            xhrFields: {
                withCredentials: true
            },
            //milisecond timeout
            timeout: 0,
            success: function (jObjRespStr) {
                jObjResp = JSON.parse(jObjRespStr);
                console.log(jObjResp);
                if (jObjResp.rc == '00') {
                    ctrlScrum.removeOnClickListenerActionTask();
                    ctrlScrum.addOnClickListenerActionTask();
                    ctrlScrum.removeOnClickListenerActionColumn();
                    ctrlScrum.addOnClickListenerActionColumn();
                    ctrlScrum.modifyArrayTaskColumnHtmlData();
                } else if (jObjResp.rc == '02') {
                    window.location.href = jObjResp.redirectUrl;
                } else {
                    UIkit.modal.alert("Failed update data, something wrong, please refresh page!");
                }
            },
            error: function (e) {
                console.debug(e);
                UIkit.modal.alert("Failed update data, network error, please refresh page!");
            },
            complete: function (e) {
                //reinit drake
                //ctrlScrumDragable.reInit();
            }
        });
    }
};

var ctrlScrum = {
    jArrColumnId: null,
    modalNewColumn: UIkit.modal("#new_column"),
    modalUpdateColumn: UIkit.modal("#update_column"),
    modalNewTask: UIkit.modal("#new_task"),
    modalUpdateTask: UIkit.modal("#update_task"),
    modalShowTask: UIkit.modal("#show_task"),
    selectizeAssigneeUpdate: '',
    init: function () {
        ctrlScrum.initData();
        ctrlScrum.initBehaviour();
    },
    initData: function () {
        //init collection data column
        var rawData = $.trim($("#listColumn").html().replace(/\s/g, ""));
        console.log("data column : " + rawData)
        if (rawData != '[]') {
            var jArrListColumnIdStr = rawData.substring(0, rawData.length - 2) + "]";
            ctrlScrum.jArrColumnId = JSON.parse(jArrListColumnIdStr);
        }
        //log task list
        //console.log("data task : " + $("#listTask").html().replace(/\s/g, ""));
    },
    initBehaviour: function(){
        //on focus to input update column form when modal update data show
        $('#update_column').on({
            'show.uk.modal': function () {
                $('#update_column_input').focus();
            },
            'hide.uk.modal': function () {

            }
        });
        $('#submit_new_task').click(function(){
            ctrlScrum.submitNewTask();
        });
        $('#submit_update_task').click(function(){
            ctrlScrum.submitUpdateTask();
        });
        $('#button_new_column').click(function () {
            ctrlScrum.modalNewColumn.show();
        });
        $('#submit_new_column').click(function(){
            ctrlScrum.submitNewColumn();
        });
        $('#submit_update_column').click(function(){
            ctrlScrum.submitUpdateColumn();
        });
        ctrlScrum.initNewTaskInputForm();
        ctrlScrum.initUpdateTaskInputForm();
        //-----------------listener click on column target
        $.each(ctrlScrum.jArrColumnId, function (index, val) {
            
        });
        ctrlScrum.addOnClickListenerActionColumn();
        ctrlScrum.addOnClickListenerActionTask();
    },
    initNewTaskInputForm: function(){
        //date range
        var $dp_start = $('#uk_dp_start'),
        $dp_end = $('#uk_dp_end');
        var start_date = UIkit.datepicker($dp_start, {
            format:'DD-MM-YYYY'
        });
        var end_date = UIkit.datepicker($dp_end, {
            format:'DD-MM-YYYY'
        });
        $dp_start.on('change',function() {
            end_date.options.minDate = $dp_start.val();
            setTimeout(function() {
                $dp_end.focus();
            },300);
        });
        $dp_end.on('change',function() {
            start_date.options.maxDate = $dp_end.val();
        });
        //selectize assignee
        jArrDataoption = []
        jArrMember = JSON.parse($('#jArrMemberStr').html());
        $.each(jArrMember, function (index, val) {
            item = {}
            item ["id"] = val.account_id;
            item ["title"] = val.account_name;
            item ["url"] = val.account_email;
            jArrDataoption.push(item);
        });
        $('#task_assignee').selectize({
            plugins: {
                'remove_button': {
                    label     : ''
                }
            },
            options: jArrDataoption,
            maxItems: null,
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            create: false,
            render: {
                option: function(data, escape) {
                    return  '<div class="option">' +
                            '<span class="title">' + escape(data.title) + '</span>' +
                            '</div>';
                },
                item: function(data, escape) {
                    return '<div class="item"><a href="' + escape(data.url) + '" target="_blank">' + escape(data.title) + '</a></div>';
                }
            },
            onDropdownOpen: function($dropdown) {
                $dropdown
                    .hide()
                    .velocity('slideDown', {
                        begin: function() {
                            $dropdown.css({'margin-top':'0'})
                        },
                        duration: 200,
                        easing: easing_swiftOut
                    })
            },
            onDropdownClose: function($dropdown) {
                $dropdown
                    .show()
                    .velocity('slideUp', {
                        complete: function() {
                            $dropdown.css({'margin-top':''})
                        },
                        duration: 200,
                        easing: easing_swiftOut
                    })
            }
        });
        //todo list
        $('#todo_list').find('input:checkbox')
            .on('ifChecked', function(){
                $(this).closest('li').addClass('md-list-item-disabled');
            })
            .on('ifUnchecked', function(){
                $(this).closest('li').removeClass('md-list-item-disabled');
            });
        $('#add_new_todo').click(function(){
            $('#container_input_todo').css('display', 'block');
            $('#input_todo').focus();
        })
        $('#input_todo').keypress(function(event){
            var keycode = (event.keyCode ? event.keyCode : event.which);
            //enter key press listener
            if(keycode == '13'){
                //randomNumber = Math.floor((Math.random() * 100) + 1); //Math.random()*100 returns a random number between 0 and 100:
                randomNumber = ctrlHelper.randomString(5);
                element = '<li id="todo_item_container_'+randomNumber+'" class="uk-nestable-item">\
                            <div class="md-list-addon-element">\
                                <input id="todo_check" type="checkbox" data-md-icheck />\
                            </div>\
                            <div class="md-list-content">\
                                <span id="todo_item" class="md-list-heading" style="width: 85%">'+ $('#input_todo').val() +'</span>\
                            </div>\
                            <a id="todo_delete_'+randomNumber+'" style="float: right;"><i class="md-icon material-icons">&#xE872;</i></a>\
                        </li>' 
                $('#new_task_to_do').append(element);
                //listener delete todo
                $('#new_task_to_do').children().each( (index, element) => {
                    deleteElement = $(element).find("#todo_delete_"+randomNumber);
                    $('#' + deleteElement.attr('id')).on('click', function (e) {
                        parent = $(this).closest('li');
                        id = parent.attr('id').split('todo_item_container_');
                        //$('#' + deleteElement.attr('id')).off('click');
                        $('#todo_item_container_'+id[1]).remove();
                    });
                });
                $('#container_input_todo').css('display', 'none');
                $('#input_todo').val("");
                // reinitialize checkboxes
                altair_md.checkbox_radio($('#todo_list').find('[data-md-icheck]'));
            }
        });
    },
    initUpdateTaskInputForm: function(){
        //date range
        var $dp_start = $('#update_uk_dp_start'),
        $dp_end = $('#update_uk_dp_end');
        var start_date = UIkit.datepicker($dp_start, {
            format:'DD-MM-YYYY'
        });
        var end_date = UIkit.datepicker($dp_end, {
            format:'DD-MM-YYYY'
        });
        $dp_start.on('change',function() {
            end_date.options.minDate = $dp_start.val();
            setTimeout(function() {
                $dp_end.focus();
            },300);
        });
        $dp_end.on('change',function() {
            start_date.options.maxDate = $dp_end.val();
        });
        //selectize assignee
        jArrDataoption = []
        jArrMember = JSON.parse($('#jArrMemberStr').html());
        $.each(jArrMember, function (index, val) {
            item = {}
            item ["id"] = val.account_id;
            item ["title"] = val.account_name;
            item ["url"] = val.account_email;
            jArrDataoption.push(item);
        });
        ctrlScrum.selectizeAssigneeUpdate = $('#update_task_assignee').selectize({
            plugins: {
                'remove_button': {
                    label     : ''
                }
            },
            options: jArrDataoption,
            maxItems: null,
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            create: false,
            render: {
                option: function(data, escape) {
                    return  '<div class="option">' +
                            '<span class="title">' + escape(data.title) + '</span>' +
                            '</div>';
                },
                item: function(data, escape) {
                    return '<div class="item"><a href="' + escape(data.url) + '" target="_blank">' + escape(data.title) + '</a></div>';
                }
            },
            onDropdownOpen: function($dropdown) {
                $dropdown
                    .hide()
                    .velocity('slideDown', {
                        begin: function() {
                            $dropdown.css({'margin-top':'0'})
                        },
                        duration: 200,
                        easing: easing_swiftOut
                    })
            },
            onDropdownClose: function($dropdown) {
                $dropdown
                    .show()
                    .velocity('slideUp', {
                        complete: function() {
                            $dropdown.css({'margin-top':''})
                        },
                        duration: 200,
                        easing: easing_swiftOut
                    })
            }
        });
        //todo list
        $('#update_todo_list').find('input:checkbox')
            .on('ifChecked', function(){
                $(this).closest('li').addClass('md-list-item-disabled');
            })
            .on('ifUnchecked', function(){
                $(this).closest('li').removeClass('md-list-item-disabled');
            });
        $('#update_todo').click(function(){
            $('#container_update_input_todo').css('display', 'block');
            $('#update_input_todo').focus();
        })
        $('#update_input_todo').keypress(function(event){
            var keycode = (event.keyCode ? event.keyCode : event.which);
            //enter key press listener
            if(keycode == '13'){
                randomNumber = ctrlHelper.randomString(5);
                element = '<li id="update_todo_item_container_'+randomNumber+'" class="uk-nestable-item">\
                            <div class="md-list-addon-element">\
                                <input id="update_todo_check" type="checkbox" data-md-icheck />\
                            </div>\
                            <div class="md-list-content">\
                                <span id="update_todo_item_'+randomNumber+'" class="md-list-heading" style="width: 85%">'+ $('#update_input_todo').val() +'</span>\
                            </div>\
                            <a id="update_todo_delete_'+randomNumber+'" style="float: right;"><i class="md-icon material-icons">&#xE872;</i></a>\
                        </li>' 
                $('#update_task_to_do').append(element);
                //listener delete todo
                $('#update_task_to_do').children().each( (index, element) => {
                    deleteElement = $(element).find("#update_todo_delete_"+randomNumber);
                    $('#' + deleteElement.attr('id')).on('click', function (e) {
                        parent = $(this).closest('li');
                        id = parent.attr('id').split('update_todo_item_container_');
                        $('#update_todo_item_container_'+id[1]).remove();
                    });
                });
                $('#update_container_input_todo').css('display', 'none');
                $('#update_input_todo').val("");
                // reinitialize checkboxes
                altair_md.checkbox_radio($('#update_todo_list').find('[data-md-icheck]'));
            }
        });
        //on focus when modal update data show
        $('#update_task').on({
            'show.uk.modal': function () {
                //set on focus
                $('#update_uk_dp_end').focus()
                $('#update_uk_dp_start').focus()
                $('#update_task_description').focus();
                $('#update_task_title').focus();
            },
            'hide.uk.modal': function () {

            }
        });
    },
    reinitColumnWidth: function(){
        // modify size of scrum_board page to fit all in page, and enable responsive layout
        var count = $("#scrum_board").children().length;
        var percentWidth = 100 / count;
        $('#scrum_board > div').css('width', percentWidth + '%');
        $('#scrum_board').css('min-width', (244 * count) + 'px');
    },
    modifyArrayTaskColumnHtmlData: function(){
        //------------------------------just prof of concept if element is delete from drake object-------------------------------------------------
        //create json data
        jArrData = [];
        jArrTaskStr = "[";
        jArrColumnStr = "[";
        for (var i = 0; i < ctrlScrumDragable.drake.containers.length; i++) {
            if (ctrlScrumDragable.drake.containers[i] != null) {
                columnId = $(ctrlScrumDragable.drake.containers[i]).attr('id').replace('scrum_column_','');
                jArrColumnStr = jArrColumnStr + columnId + ',';
                for (var j = 0; j < ctrlScrumDragable.drake.containers[i].childElementCount; j++) {
                    children = ctrlScrumDragable.drake.containers[i].children[j];
                    columnTask = children.id.split("_task_");
                    columnId = columnTask[0];
                    taskId = columnTask[1];
                    jObjData = {};
                    jObjData.columnId = columnId;
                    jObjData.taskId = taskId;
                    jArrData.push(jObjData);
                    jArrTaskStr = jArrTaskStr + taskId + ',';
                }
            }
        }
        //--------------------------task part----------------------------------
        jArrTaskStr = jArrTaskStr + ']';
        jArrTaskStrHtml = $("#listTask").html().replace(/\s/g, "");
        jArrTaskStr = jArrTaskStr.substring(0, jArrTaskStr.length - 2) + "]";
        jArrTaskStrHtml = jArrTaskStrHtml.substring(0, jArrTaskStrHtml.length - 2) + "]";
        jArrTaskStr = (jArrTaskStr == ']') ? '[]' : jArrTaskStr;
        jArrTaskStrHtml = (jArrTaskStrHtml == ']') ? '[]' : jArrTaskStrHtml;
        //console.log("task arr str from data modify : " + jArrTaskStr);
        //console.log("task arr str from html : " + jArrTaskStrHtml);
        jArrTask = JSON.parse(jArrTaskStr);
        jArrTaskHtml = JSON.parse(jArrTaskStrHtml);
        // console.log("task arr from data modify : " + jArrTask);
        // console.log("task arr from html : " + jArrTaskHtml);
        difference = [];
        jQuery.grep(jArrTaskHtml, function (el) {
            if (jQuery.inArray(el, jArrTask) == -1) difference.push(el);
        });
        //console.log(" the difference task is " + difference); //difference variable is data remove
        $("#listTask").html(jArrTaskStr.substring(0, jArrTaskStr.length - 1) + ",]");

        //---------------------------column part----------------------------------
        jArrColumnStr = jArrColumnStr + ']';
        jArrColumnStrHtml = $("#listColumn").html().replace(/\s/g, "");
        jArrColumnStr = jArrColumnStr.substring(0, jArrColumnStr.length - 2) + "]";
        jArrColumnStrHtml = jArrColumnStrHtml.substring(0, jArrColumnStrHtml.length - 2) + "]";
        jArrColumnStr = (jArrColumnStr == ']') ? '[]' : jArrColumnStr;
        jArrColumnStrHtml = (jArrColumnStrHtml == ']') ? '[]' : jArrColumnStrHtml;
        //console.log("column arr str from data modify : " + jArrColumnStr);
        //console.log("column arr str from html : " + jArrColumnStrHtml);
        jArrColumn = JSON.parse(jArrColumnStr);
        jArrColumnHtml = JSON.parse(jArrColumnStrHtml);
        // console.log("column arr from data modify : " + jArrTask);
        // console.log("column arr from html : " + jArrTaskHtml);
        difference = [];
        jQuery.grep(jArrColumnHtml, function (el) {
            if (jQuery.inArray(el, jArrColumn) == -1) difference.push(el);
        });
        //console.log(" the difference column is " + difference); //difference variable is data remove
        $("#listColumn").html(jArrColumnStr.substring(0, jArrColumnStr.length - 1) + ",]")
    },
    addOnClickListenerActionColumn: function(){
        for (var i = 0; i < ctrlScrumDragable.drake.containers.length; i++) {
            if (ctrlScrumDragable.drake.containers[i] != null) {
                columnId = $(ctrlScrumDragable.drake.containers[i]).attr('id').replace('scrum_column_', '');
                $('#button_new_task_' + columnId).click(function () {
                    columnId = $(this).attr('id').replace("button_new_task_", "");
                    dataEachColumn = JSON.parse($('#data_' + columnId).html());
                    //modify modal dialog form value
                    $('#new_task_column_id').html(columnId);
                    $('#header_new_task').html('NEW TASK FOR "' + dataEachColumn.name + '"');
                    //clear first all input new task form
                    $('#task_title').val('');
                    $('#task_description').val('');
                    $("#task_assignee")[0].selectize.clear();
                    $("#uk_dp_start").val('');
                    $("#uk_dp_end").val('');
                    $("#new_task_to_do").html('');
                    ctrlScrum.modalNewTask.show();
                });
                $('#button_edit_column_' + columnId).click(function () {
                    columnId = $(this).attr('id').replace("button_edit_column_", "");
                    dataEachColumn = JSON.parse($('#data_' + columnId).html());
                    //modify modal dialog form value
                    $('#update_column_id').html(columnId);
                    $('#update_column_input').val(dataEachColumn.name);
                    ctrlScrum.modalUpdateColumn.show();
                });
                $('#button_delete_column_' + columnId).click(function () {
                    columnId = $(this).attr('id').replace('button_delete_column_','');
                    ctrlScrum.submitDeleteColumn(columnId);
                });
            }
        }
    },
    removeOnClickListenerActionColumn: function(){
        for (var i = 0; i < ctrlScrumDragable.drake.containers.length; i++) {
            if (ctrlScrumDragable.drake.containers[i] != null) {
                columnId = $(ctrlScrumDragable.drake.containers[i]).attr('id').replace('scrum_column_', '');
                //console.log(columnId);
                $('#button_new_task_' + columnId).off("click");
                $('#button_edit_column_' + columnId).off("click");
                $('#button_delete_column_' + columnId).off("click");
            }
        }
    },
    addOnClickListenerActionTask: function () {
        //-----------------listener click on task target
        for (var i = 0; i < ctrlScrumDragable.drake.containers.length; i++) {
            if (ctrlScrumDragable.drake.containers[i] != null) {
                for (var j = 0; j < ctrlScrumDragable.drake.containers[i].childElementCount; j++) {
                    children = ctrlScrumDragable.drake.containers[i].children[j];
                    columnTask = children.id.split("_task_");
                    columnId = columnTask[0];
                    taskId = columnTask[1];
                    //delete listener click
                    $('#' + columnId + '_scrum_task_delete_' + taskId).click(function () {
                        //console.log(this.id);
                        columnTask = this.id.split("_scrum_task_delete_");
                        // columnId = parseInt(columnTask[0]) + 1;
                        columnId = columnTask[0];
                        taskId = columnTask[1];
                        console.log(columnId+ " remove task : " + taskId);
                        ctrlScrum.submitDeleteTask(columnId, taskId);
                    });
                    //edit listener click
                    $('#' + columnId + '_scrum_task_edit_' + taskId).click(function () {
                        //console.log(this.id);
                        columnTask = this.id.split("_scrum_task_edit_");
                        // columnId = parseInt(columnTask[0]) + 1;
                        columnId = columnTask[0];
                        taskId = columnTask[1];
                        console.log(columnId+ " update task : " + taskId);
                        ctrlScrum.prepareTaskUpdate(columnId, taskId);
                    });
                    //show task detil click listener
                    $('#scrum_task_anchor_' + taskId).click(function () {
                        columnTask = this.id.split("scrum_task_anchor_");
                        taskId = columnTask[1];
                        console.log(columnId+ " open task : " + taskId);
                        ctrlScrum.getDetailTask(taskId);
                    });
                }
            }
        }
    },
    removeOnClickListenerActionTask: function(){
        //unbind all delete onclick, show detail task onclick, update task on clik
        for (var i = 0; i < ctrlScrumDragable.drake.containers.length; i++) {
            if (ctrlScrumDragable.drake.containers[i] != null) {
                for (var j = 0; j < ctrlScrumDragable.drake.containers[i].childElementCount; j++) {
                    children = ctrlScrumDragable.drake.containers[i].children[j];
                    columnTask = children.id.split("_task_");
                    columnId = columnTask[0];
                    taskId = columnTask[1];
                    $('#' + columnId + '_scrum_task_delete_' + taskId).off("click");
                    $('#' + columnId + '_scrum_task_edit_' + taskId).off("click");
                    // $('#' + columnId + '_task_' + taskId).off("click");
                    $('#scrum_task_anchor_' + taskId).off("click");
                    console.log('listener click remove');
                }
            }
        }
    },
    getDetailTask: function(taskId){
        //clear html element first
        $('#show_task_name').html('');
        $('#show_task_description').html('');
        $('#show_task_assignee').html('');
        //execution date
        $('#show_start_date').html('');
        $('#show_end_date').html('');
        //unbind clik
        $('.iCheck-helper').off('click')
        $('#show_task_to_do').html(''); 
        //get data detail from backend
        dataToSend = {
            'task_id' : taskId
        }
        $.ajax({
            url: $("#webRootUrl").html() + 'ControllerMamenScrum/getTaskDetail',
            type: 'POST',
            async: true,
            cache: false,
            data: dataToSend,
            xhrFields: {
                withCredentials: true
            },
            //milisecond timeout
            timeout: 0,
            success: function (jObjRespStr) {
                jObjResp = JSON.parse(jObjRespStr);
                console.log(jObjResp);
                if (jObjResp.rc == '00') {
                    ctrlScrum.removeOnClickListenerActionTask();
                    ctrlScrum.addOnClickListenerActionTask();
                    ctrlScrum.modifyArrayTaskColumnHtmlData();
                    //modify detail modal show
                    $('#show_task_name').html(jObjResp.jObjTask.task_name);
                    $('#show_task_description').html(jObjResp.jObjTask.task_description);
                    //assignee
                    htmlAssignee = '';
                    $.each(jObjResp.jArrAccount, function(key,value) {
                        htmlAssignee = htmlAssignee + value.account_name + ', ';
                    }); 
                    htmlAssignee = htmlAssignee.substring(0, htmlAssignee.length - 2);
                    $('#show_task_assignee').html(htmlAssignee);
                    //execution date
                    $('#show_start_date').html('Start date : ' + jObjResp.jObjTask.task_start_date.replace(" 00:00:00", ""));
                    $('#show_end_date').html('End date : ' + jObjResp.jObjTask.task_end_date.replace(" 00:00:00", ""));
                    //todo
                    htmlTodo = '';
                    $.each(jObjResp.jArrTodo, function(key,value) {
                        htmlTodo = htmlTodo + '<li class="uk-nestable-item">\
                            <div id="checked_task_'+value.task_todo_id+'" class="md-list-addon-element">\
                                <input type="checkbox" data-md-icheck '+((value.task_todo_finish == 'f') ? '' : 'checked')+'/>\
                            </div>\
                            <div class="md-list-content">\
                                <span class="md-list-heading">'+value.task_todo_name+'</span>\
                            </div>\
                        </li>';
                    });
                    $('#show_task_to_do').html(htmlTodo); 
                    altair_md.checkbox_radio($('#show_task_to_do').find('[data-md-icheck]'));
                    //add listener checked_task
                    $('.iCheck-helper').click(function(){
                        parent = $(this).closest('.md-list-addon-element');
                        id = parent.attr('id').split('checked_task_');
                        //detect check or not
                        parentChecked = $(this).closest('.icheckbox_md');
                        if($(parentChecked).attr('class').indexOf('checked') !== -1) {
                            console.log('should hit backend to update status task finish for task_todo_id : ' + id[1]);
                            ctrlScrum.submitChangeTodo(id[1], true);
                        } else {
                            console.log('should hit backend to update status task unfinish for task_todo_id : ' + id[1]);
                            ctrlScrum.submitChangeTodo(id[1], false);
                        }
                    })
                    ctrlScrum.modalShowTask.show();
                } else if (jObjResp.rc == '02') {
                    window.location.href = jObjResp.redirectUrl;
                } else {
                    UIkit.modal.alert("Failed update data, something wrong, please refresh page!");
                }
            },
            error: function (e) {
                console.debug(e);
                UIkit.modal.alert("Failed update data, network error, please refresh page!");
            },
            complete: function (e) {
                //reinit drake
                //ctrlScrumDragable.reInit();
            }
        });
    },
    prepareTaskUpdate: function (columnId, taskId) {
        //clear first all input update task form
        $('#header_update_task').html('');
        $('#update_task_title').val('');
        $('#update_task_description').val('');
        $("#update_task_assignee")[0].selectize.clear();
        $("#update_uk_dp_start").val('');
        $("#update_uk_dp_end").val('');
        $("#update_task_to_do").html('');
        $("#update_task_id").html(taskId);
        //get data from server
        dataToSend = {
            'task_id' : taskId
        }
        var modalLoading = UIkit.modal.blockUI("Get data task"); // modal.hide() to unblock
        $.ajax({
            url: $("#webRootUrl").html() + 'ControllerMamenScrum/getTaskDetail',
            type: 'POST',
            async: true,
            cache: false,
            data: dataToSend,
            xhrFields: {
                withCredentials: true
            },
            //milisecond timeout
            timeout: 0,
            success: function (jObjRespStr) {
                jObjResp = JSON.parse(jObjRespStr);
                console.log(jObjResp);
                if (jObjResp.rc == '00') {
                    //modify detail modal show
                    $('#header_update_task').html("Update Task : " + jObjResp.jObjTask.task_name);
                    $('#update_task_title').val(jObjResp.jObjTask.task_name);
                    $('#update_task_description').val(jObjResp.jObjTask.task_description);
                    //task assignee
                    var selectize = ctrlScrum.selectizeAssigneeUpdate[0].selectize;
                    defaultIds = [];
                    $.each(jObjResp.jArrAccount, function(key,value) {
                        defaultIds.push(value.account_id);
                    }); 
                    selectize.setValue(defaultIds);
                    //execution date
                    dteSplit = jObjResp.jObjTask.task_start_date.replace(" 00:00:00", "").split("-");
                    //yr = dteSplit[0][2] + dteSplit[0][3]; //special yr format, take last 2 digits
                    yr = dteSplit[0];
                    month = dteSplit[1];
                    day = dteSplit[2];
                    finalDate = day +"-"+month+"-"+yr
                    $('#update_uk_dp_start').val(finalDate);
                    teSplit = jObjResp.jObjTask.task_end_date.replace(" 00:00:00", "").split("-");
                    yr = dteSplit[0];
                    day = dteSplit[2];
                    finalDate = day+"-"+month+"-"+yr
                    $('#update_uk_dp_end').val(finalDate);
                    //task todo
                    htmlTodo = '';
                    $.each(jObjResp.jArrTodo, function(key,value) {
                        htmlTodo = htmlTodo + '<li id="update_todo_item_container_'+value.task_todo_id+'" class="uk-nestable-item">\
                            <div id="update_checked_task_'+value.task_todo_id+'" class="md-list-addon-element">\
                                <input type="checkbox" data-md-icheck '+((value.task_todo_finish == 'f') ? '' : 'checked')+'/>\
                            </div>\
                            <div class="md-list-content">\
                                <span id="update_todo_item_'+value.task_todo_id+'" class="md-list-heading"  style="width: 85%">'+value.task_todo_name+'</span>\
                            </div>\
                            <a id="update_todo_delete_'+value.task_todo_id+'" style="float: right;"><i class="md-icon material-icons">&#xE872;</i></a>\
                        </li>';
                    });
                    $('#update_task_to_do').html(htmlTodo); 
                    //listener delete todo
                    $('#update_task_to_do').children().each( (index, element) => {
                        deleteElement = $(element).find("[id*='update_todo_delete_']");
                        console.log(deleteElement);
                        $('#' + deleteElement.attr('id')).on('click', function (e) {
                            parent = $(this).closest('li');
                            console.log(parent);
                            id = parent.attr('id').split('update_todo_item_container_');
                            //$('#' + deleteElement.attr('id')).off('click');
                            $('#update_todo_item_container_'+id[1]).remove();
                        });
                    });
                    $('#container_update_input_todo').css('display', 'none');
                    altair_md.checkbox_radio($('#update_task_to_do').find('[data-md-icheck]'));
                    ctrlScrum.modalUpdateTask.show();
                } else if (jObjResp.rc == '02') {
                    window.location.href = jObjResp.redirectUrl;
                } else {
                    UIkit.modal.alert("Failed update data, something wrong, please refresh page!");
                }
            },
            error: function (e) {
                console.debug(e);
                UIkit.modal.alert("Failed update data, network error, please refresh page!");
            },
            complete: function (e) {
                modalLoading.hide();
                //reinit drake
                //ctrlScrumDragable.reInit();
            }
        });
    },
    submitNewTask: function (){
        //collect data to do
        dataToDoArr = []
        $('#new_task_to_do').children().each( (index, element) => {
            //console.log(index);     // children's index
            //console.log(element);   // children's element
            console.log($(element).find(".icheckbox_md").attr('class')) //find if checked or not            
            console.log($(element).find("#todo_item").html()) //find name of todo      
            dataToDoObj = {
                'checked' : $(element).find(".icheckbox_md").attr('class').indexOf('checked') !== -1 ? true : false,
                'todo_name' : $(element).find("#todo_item").html()
            }    
            dataToDoArr.push(dataToDoObj);
        });
        dataToSend = {
            'column_id' : $('#new_task_column_id').html(),
            'task_data' : JSON.stringify({
                'task_name' : $('#task_title').val(),
                'task_description' : $('#task_description').val(),
                'task_assignee' : $('#task_assignee').val(),
                'task_start_date' : $('#uk_dp_start').val(),
                'task_end_date' : $('#uk_dp_end').val(),
                'task_to_do' : dataToDoArr
            })
        }
        $.ajax({
            url: $("#webRootUrl").html() + 'ControllerMamenScrum/addTask',
            type: 'POST',
            async: true,
            cache: false,
            data: dataToSend,
            xhrFields: {
                withCredentials: true
            },
            //milisecond timeout
            timeout: 0,
            success: function (jObjRespStr) {
                jObjResp = JSON.parse(jObjRespStr);
                console.log(jObjResp);
                if (jObjResp.rc == '00') {
                    ctrlScrum.modalNewTask.hide();
                    htmlAccount = "";
                    $.each(jObjResp.task_account, function(key,value) {
                        htmlAccount = htmlAccount + '<a>'+value.account_name+'</a>,';
                    }); 
                    htmlAccount = htmlAccount.substring(0, htmlAccount.length - 1);
                    //add element to dragable
                    htmlTask = '<div id="'+jObjResp.column_id+'_task_'+jObjResp.task_id+'">\
                        <div id="data_task_'+jObjResp.task_id+'" style="display: none;">\
                            {"id" : "'+jObjResp.task_id+'", "name" : "'+jObjResp.task_name+'"}\
                        </div>\
                        <div class="scrum_task minor">\
                            <h3 id="scrum_task_title_'+jObjResp.task_id+'" class="scrum_task_title">\
                                <a id="scrum_task_anchor_'+jObjResp.task_id+'">'+jObjResp.task_name+'</a>\
                                <div class="uk-button-dropdown" data-uk-dropdown="{pos:\'bottom-right\'}" style="float: right;">\
                                    <i class="md-icon material-icons">&#xE5D4;</i>\
                                    <div class="uk-dropdown">\
                                        <ul class="uk-nav">\
                                            <li class="md-btn-wave" id="'+jObjResp.column_id+'_scrum_task_edit_'+jObjResp.task_id+'"><a><i class="uk-icon-pencil uk-margin-small-right"></i> Edit</a></li>\
                                            <li class="md-btn-wave" id="'+jObjResp.column_id+'_scrum_task_delete_'+jObjResp.task_id+'"><a><i class="uk-icon-archive uk-margin-small-right"></i> Archive</a></li>\
                                        </ul>\
                                    </div>\
                                </div>\
                            </h3>\
                            <p id="scrum_task_description_'+jObjResp.task_id+'" class="scrum_task_description">'+jObjResp.task_description+'</p>\
                            <p id="scrum_task_info_'+jObjResp.task_id+'" class="scrum_task_info"><span class="uk-text-muted">Assignee:</span> '+htmlAccount+'</p>\
                        </div>\
                    </div>';
                    $('#scrum_column_' + jObjResp.column_id).prepend(htmlTask);
                    ctrlScrum.removeOnClickListenerActionTask();
                    ctrlScrum.addOnClickListenerActionTask();
                    ctrlScrum.modifyArrayTaskColumnHtmlData();
                    //this is should code send data on move to set order number
                } else if (jObjResp.rc == '02') {
                    window.location.href = jObjResp.redirectUrl;
                } else if (jObjResp.rc == '03') {
                    UIkit.modal.alert(jObjResp.message);
                } else {
                    UIkit.modal.alert("Failed update data, something wrong, please refresh page!");
                }
            },
            error: function (e) {
                console.debug(e);
                UIkit.modal.alert("Failed update data, network error, please refresh page!");
            },
            complete: function (e) {
                //reinit drake
                //ctrlScrumDragable.reInit();
            }
        });
    },
    submitUpdateTask: function(){
        UIkit.modal.confirm('Are you sure to update data task "'+$('#header_update_task').html().replace('Update Task : ','')+'"?', function () {
            // will be executed on confirm.
            //ajax
            var modalLoading = UIkit.modal.blockUI("On Progress Update"); // modal.hide() to unblock
            //collect data to do
            dataToDoArr = []
            $('#update_task_to_do').children().each( (index, element) => {
                //console.log(index);     // children's index
                //console.log(element);   // children's element
                console.log($(element).find(".icheckbox_md").attr('class')) //find if checked or not            
                console.log($(element).find("#update_todo_item").html()) //find name of todo   
                todoNameElement = $(element).find("[id*='update_todo_item_']");
                todoName = todoNameElement.html();
                todoId = $(todoNameElement).attr('id').replace('update_todo_item_', '');
                dataToDoObj = {
                    'checked' : $(element).find(".icheckbox_md").attr('class').indexOf('checked') !== -1 ? true : false,
                    'todo_name' : todoName,
                    'todo_id' : todoId
                }    
                dataToDoArr.push(dataToDoObj);
            });
            dataToSend = {
                'task_id' : $('#update_task_id').html(),
                'task_data' : JSON.stringify({
                    'task_name' : $('#update_task_title').val(),
                    'task_description' : $('#update_task_description').val(),
                    'task_assignee' : $('#update_task_assignee').val() != null ? $('#update_task_assignee').val() : [],
                    'task_start_date' : $('#update_uk_dp_start').val(),
                    'task_end_date' : $('#update_uk_dp_end').val(),
                    'task_to_do' : dataToDoArr
                })
            }
            console.log(dataToSend);
            //then send data to server to update
            $.ajax({
                url: $("#webRootUrl").html() + 'ControllerMamenScrum/updateTask',
                type: 'POST',
                async: true,
                cache: false,
                data: dataToSend,
                xhrFields: {
                    withCredentials: true
                },
                //milisecond timeout
                timeout: 0,
                success: function (jObjRespStr) {
                    jObjResp = JSON.parse(jObjRespStr);
                    console.log(jObjResp);
                    if (jObjResp.rc == '00') {
                        //modify task
                        dataHtml = '{"id" : "'+jObjResp.jObjTask.task_id+'", "name" : "'+jObjResp.jObjTask.task_name+'"}'
                        $('#data_task_'+jObjResp.jObjTask.task_id).html(dataHtml);
                        $('#scrum_task_anchor_'+jObjResp.jObjTask.task_id).html(jObjResp.jObjTask.task_name);
                        $('#scrum_task_description_'+jObjResp.jObjTask.task_id).html(jObjResp.jObjTask.task_description);
                        $('#scrum_task_info_'+jObjResp.jObjTask.task_id).html('');
                        htmlAssignee = '';
                        $.each(jObjResp.jArrAssignee, function(key,value) {
                            htmlAssignee = htmlAssignee + '<a>'+value.account_name+'</a>,';
                        }); 
                        htmlAssignee = htmlAssignee.substring(0, htmlAssignee.length - 1);
                        $('#scrum_task_info_'+jObjResp.jObjTask.task_id).html('<span class="uk-text-muted">Assignee:</span>' + htmlAssignee);
                        ctrlScrum.modalUpdateTask.hide();
                    } else if (jObjResp.rc == '02') {
                        window.location.href = jObjResp.redirectUrl;
                    } else if (jObjResp.rc == '03') {
                        UIkit.modal.alert(jObjResp.message);
                    } else {
                        UIkit.modal.alert("Failed update data, something wrong, please refresh page!");
                    }
                },
                error: function (e) {
                    console.debug(e);
                    UIkit.modal.alert("Failed update data, network error, please refresh page!");
                },
                complete: function (e) {
                    modalLoading.hide();
                }
            });
        }, function () {

        });
    },
    submitDeleteTask: function (columnId, taskId) {
        // console.log("data_task_" + taskId);
        dataTaskId = JSON.parse($('#data_task_' + taskId).html());
        UIkit.modal.confirm('Are you sure to delete data task "' + dataTaskId.name + '"?', function () {
            // will be executed on confirm.
            //ajax
            var modalLoading = UIkit.modal.blockUI("On Progress Archieve"); // modal.hide() to unblock
            $.ajax({
                url: $("#webRootUrl").html() + 'ControllerMamenScrum/deleteTask',
                type: 'POST',
                async: true,
                cache: false,
                data: {
                    'task_id': taskId
                },
                xhrFields: {
                    withCredentials: true
                },
                //milisecond timeout
                timeout: 0,
                success: function (jObjRespStr) {
                    jObjResp = JSON.parse(jObjRespStr);
                    console.log(jObjResp);
                    if (jObjResp.rc == '00') {
                        //jquery remove task and reinit click listener
                        ctrlScrum.removeOnClickListenerActionTask();
                        $('#' + columnId + "_task_" + taskId).remove();
                        ctrlScrum.addOnClickListenerActionTask();
                        ctrlScrum.modifyArrayTaskColumnHtmlData();
                    } else if (jObjResp.rc == '02') {
                        window.location.href = jObjResp.redirectUrl;
                    } else if (jObjResp.rc == '03') {
                        UIkit.modal.alert(jObjResp.message);
                    } else {
                        UIkit.modal.alert("Failed update data, something wrong!");
                    }
                },
                error: function (e) {
                    console.debug(e);
                    UIkit.modal.alert("Failed update data, network error!");
                },
                complete: function (e) {
                    modalLoading.hide();
                }
            });
        }, function () {

        });
    },
    submitNewColumn: function (){
        dataToSend = {
            'column_name' : $('#new_column_input').val(),
            'board_id' : $('#board_id').html()
        }
        $.ajax({
            url: $("#webRootUrl").html() + 'ControllerMamenScrum/addColumn',
            type: 'POST',
            async: true,
            cache: false,
            data: dataToSend,
            xhrFields: {
                withCredentials: true
            },
            //milisecond timeout
            timeout: 0,
            success: function (jObjRespStr) {
                jObjResp = JSON.parse(jObjRespStr);
                console.log(jObjResp);
                if (jObjResp.rc == '00') {
                    columnHtml = '<div>\
                        <div id="data_'+jObjResp.column_id+'" style="display: none;">\
                            {"id" : "'+jObjResp.column_id+'", "name" : "'+jObjResp.column_name+'"}\
                        </div>\
                        <div class="scrum_column_heading_wrapper">\
                            <div class="scrum_column_heading"> '+jObjResp.column_name+'</div>\
                            <div class="uk-button-dropdown" data-uk-dropdown="{mode:\'click\',pos:\'bottom-right\'}">\
                                <i class="md-icon material-icons">&#xE5D4;</i>\
                                <div class="uk-dropdown uk-dropdown-small">\
                                    <ul class="uk-nav uk-nav-dropdown">\
                                        <li><a id="button_new_task_'+jObjResp.column_id+'">Add task</a></li>\
                                        <li class="uk-nav-divider"></li>\
                                        <li><a id="button_edit_column_'+jObjResp.column_id+'">Edit Column</a></li>\
                                        <li><a id="button_delete_column_'+jObjResp.column_id+'" class="uk-text-danger">Delete Column</a></li>\
                                    </ul>\
                                </div>\
                            </div>\
                        </div>\
                        <div class="scrum_column">\
                            <div id="scrum_column_'+jObjResp.column_id+'">\
                            </div>\
                        </div>\
                    </div>';
                    $('#scrum_board').append(columnHtml);
                    //add to drake object
                    var elementColumn = $('#scrum_column_' + jObjResp.column_id)[0];
                    //add on click listener on new task action each column
                    //add dragable container
                    ctrlScrumDragable.drake.containers.push(elementColumn);
                    $(ctrlScrumDragable.drake.containers[ctrlScrumDragable.drake.containers.length-1]).addClass('dragula dragula-vertical'); //show scroll vertical on each column
                    $(ctrlScrumDragable.drake.containers[ctrlScrumDragable.drake.containers.length-1]).css('height', '99%'); //show scroll vertical on each column when its full
                    ctrlScrum.removeOnClickListenerActionTask();
                    ctrlScrum.addOnClickListenerActionTask();
                    ctrlScrum.removeOnClickListenerActionColumn();
                    ctrlScrum.addOnClickListenerActionColumn();
                    ctrlScrum.modifyArrayTaskColumnHtmlData();
                    ctrlScrum.reinitColumnWidth();
                } else if (jObjResp.rc == '02') {
                    window.location.href = jObjResp.redirectUrl;
                } else if (jObjResp.rc == '03') {
                    UIkit.modal.alert(jObjResp.message);
                } else {
                    UIkit.modal.alert("Failed update data, something wrong, please refresh page!");
                }
            },
            error: function (e) {
                console.debug(e);
                UIkit.modal.alert("Failed update data, network error, please refresh page!");
            },
            complete: function (e) {
                //reinit drake
                //ctrlScrumDragable.reInit();
            }
        });
    },
    submitUpdateColumn: function (){
        dataToSend = {
            'column_id' : $('#update_column_id').html(),
            'column_name' : $('#update_column_input').val()
        }
        $.ajax({
            url: $("#webRootUrl").html() + 'ControllerMamenScrum/updateColumn',
            type: 'POST',
            async: true,
            cache: false,
            data: dataToSend,
            xhrFields: {
                withCredentials: true
            },
            //milisecond timeout
            timeout: 0,
            success: function (jObjRespStr) {
                jObjResp = JSON.parse(jObjRespStr);
                console.log(jObjResp);
                if (jObjResp.rc == '00') {
                    //modify html element
                    htmlData = '{"id" : "'+jObjResp.column_id+'", "name" : "'+jObjResp.column_name+'"}';
                    $('#data_' + jObjResp.column_id).html(htmlData);
                    $('#column_name_' + jObjResp.column_id).html(jObjResp.column_name);
                    ctrlScrum.removeOnClickListenerActionTask();
                    ctrlScrum.addOnClickListenerActionTask();
                    ctrlScrum.removeOnClickListenerActionColumn();
                    ctrlScrum.addOnClickListenerActionColumn();
                    ctrlScrum.modifyArrayTaskColumnHtmlData();
                } else if (jObjResp.rc == '02') {
                    window.location.href = jObjResp.redirectUrl;
                } else if (jObjResp.rc == '03') {
                    UIkit.modal.alert(jObjResp.message);
                } else {
                    UIkit.modal.alert("Failed update data, something wrong, please refresh page!");
                }
            },
            error: function (e) {
                console.debug(e);
                UIkit.modal.alert("Failed update data, network error, please refresh page!");
            },
            complete: function (e) {
                //reinit drake
                //ctrlScrumDragable.reInit();
            }
        });
    },
    submitDeleteColumn: function(columnId){
        UIkit.modal.confirm('Are you sure to delete column "' + $('#column_name_' + columnId).html() + '"? all data task bind to this column will delete too!', function () {
            console.log('delete column ' + columnId);
            // will be executed on confirm.
            //ajax
            var modalLoading = UIkit.modal.blockUI("On Progress Archieve"); // modal.hide() to unblock
            $.ajax({
                url: $("#webRootUrl").html() + 'ControllerMamenScrum/deleteColumn',
                type: 'POST',
                async: true,
                cache: false,
                data: {
                    'column_id': columnId
                },
                xhrFields: {
                    withCredentials: true
                },
                //milisecond timeout
                timeout: 0,
                success: function (jObjRespStr) {
                    jObjResp = JSON.parse(jObjRespStr);
                    console.log(jObjResp);
                    if (jObjResp.rc == '00') {                       
                        //find dragable container
                        foundIndexDraggableContainer = 0;
                        for (var i = 0; i < ctrlScrumDragable.drake.containers.length; i++) {
                            if($(ctrlScrumDragable.drake.containers[i]).attr('id') == ('scrum_column_' + columnId)){
                                //remove container
                                console.log('get index : ' + i)
                                foundIndexDraggableContainer = i;
                            }
                        }
                        //remove container
                        removedDraggableContainer = ctrlScrumDragable.drake.containers.splice(foundIndexDraggableContainer, 1); //array.splice(start_index, no_of_elements)
                        console.log('dragula container remove ' , removedDraggableContainer);
                        $('#column_container_' + columnId).remove();

                        //just prof if container is remove from dragula element
                        // for (var i = 0; i < ctrlScrumDragable.drake.containers.length; i++) {
                        //     if (ctrlScrumDragable.drake.containers[i] != null) {
                        //         console.log(ctrlScrumDragable.drake.containers[i]);
                        //     }
                        // }

                        ctrlScrum.removeOnClickListenerActionTask();
                        ctrlScrum.addOnClickListenerActionTask();
                        ctrlScrum.modifyArrayTaskColumnHtmlData();
                        ctrlScrum.removeOnClickListenerActionColumn();
                        ctrlScrum.addOnClickListenerActionColumn();
                        ctrlScrum.reinitColumnWidth();
                    } else if (jObjResp.rc == '02') {
                        window.location.href = jObjResp.redirectUrl;
                    } else if (jObjResp.rc == '03') {
                        UIkit.modal.alert(jObjResp.message);
                    } else {
                        UIkit.modal.alert("Failed update data, something wrong!");
                    }
                },
                error: function (e) {
                    console.debug(e);
                    UIkit.modal.alert("Failed update data, network error!");
                },
                complete: function (e) {
                    modalLoading.hide();
                }
            });
        }, function () {

        });
    },
    submitChangeTodo: function(todoId, flagActive){
        $.ajax({
            url: $("#webRootUrl").html() + 'ControllerMamenScrum/updateToDo',
            type: 'POST',
            async: true,
            cache: false,
            data: {
                'todo_id': todoId,
                'checked': flagActive
            },
            xhrFields: {
                withCredentials: true
            },
            //milisecond timeout
            timeout: 0,
            success: function (jObjRespStr) {
                jObjResp = JSON.parse(jObjRespStr);
                console.log(jObjResp);
                if (jObjResp.rc == '00') {                       
                    
                } else if (jObjResp.rc == '02') {
                    window.location.href = jObjResp.redirectUrl;
                } else {
                    UIkit.modal.alert("Failed update data, something wrong!");
                }
            },
            error: function (e) {
                console.debug(e);
                UIkit.modal.alert("Failed update data, network error!");
            },
            complete: function (e) {
   
            }
        });
    }
}