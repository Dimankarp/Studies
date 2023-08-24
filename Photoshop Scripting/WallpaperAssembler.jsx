/**
    Great Fonts
Modern 20 - For modern paintings
Harmony
Bigilla
Bosch
Herming
Remboy
Durer
Glusp

Cyillic:
Noir Pro
Corruption 
Baskerville old Face

Bell MT

Book Antiqua

Bookman old style

Callisto MT

Castellar - Marble
Felix Tittling - Choice
Perpetua titling

Centruy
Centruy-Gothic

Copperplate gothic light

        widthToHeightRation = 16/9
        

        const prefWidth =  canvasH * 0.484375
        const prefHeight = canvasV * 0.861
portrait: 638 / 944/px is good = 0.49/ 


        const prefWidth =  canvasH * 0.84375
        const prefHeight = canvasV * 0.75
        const topOffset = 0.1 * canvasV
        
        if(width >= 1.1 height) - landscape
        
  
taskbar max 60px, so 150px from 2  sides,  90 from top, textbox centered, height by old calculations, width is defined itself

*/
function WallpaperAssemblerBox(){ }

WallpaperAssemblerBox.prototype.run = function()
    {
	var retval = true;
    var backgroundImageFiles = []
    var paintingImageFiles = []
    var finishedFilesCounter = 0
    var canvasH = UnitValue(1920, "px")
    var canvasV = UnitValue(1080, "px")
    var fontSize= 55
    var currFontName = "Modern-Regular"
    var borderRGB = [0, 0, 0]
    var fontRGB =   [0, 0, 0]

	function createBuilderDialog() {		
           
            /*
            Code for Import https://scriptui.joonas.me — (Triple click to select): 
            {"activeId":35,"items":{"item-0":{"id":0,"type":"Dialog","parentId":false,"style":{"enabled":true,"varName":null,"windowType":"Dialog","creationProps":{"su1PanelCoordinates":false,"maximizeButton":false,"minimizeButton":false,"independent":false,"closeButton":true,"borderless":false,"resizeable":false},"text":"WallPaper Assembler","preferredSize":[600,0],"margins":16,"orientation":"column","spacing":10,"alignChildren":["center","top"]}},"item-3":{"id":3,"type":"StaticText","parentId":4,"style":{"enabled":true,"varName":null,"creationProps":{"truncate":"none","multiline":false,"scrolling":false},"softWrap":false,"text":"Background Images","justify":"left","preferredSize":[0,0],"alignment":null,"helpTip":null}},"item-4":{"id":4,"type":"Panel","parentId":0,"style":{"enabled":true,"varName":"filePanel","creationProps":{"borderStyle":"etched","su1PanelCoordinates":false},"text":"Images Choosing","preferredSize":[500,0],"margins":10,"orientation":"column","spacing":0,"alignChildren":["left","top"],"alignment":null}},"item-5":{"id":5,"type":"StaticText","parentId":4,"style":{"enabled":true,"varName":null,"creationProps":{"truncate":"none","multiline":false,"scrolling":false},"softWrap":false,"text":"Paintings ","justify":"left","preferredSize":[0,0],"alignment":null,"helpTip":null}},"item-7":{"id":7,"type":"Panel","parentId":10,"style":{"enabled":true,"varName":"sizePanel","creationProps":{"borderStyle":"etched","su1PanelCoordinates":false},"text":"Size Settings","preferredSize":[0,0],"margins":10,"orientation":"column","spacing":5,"alignChildren":["left","top"],"alignment":null}},"item-8":{"id":8,"type":"StaticText","parentId":7,"style":{"enabled":true,"varName":null,"creationProps":{"truncate":"none","multiline":false,"scrolling":false},"softWrap":false,"text":"Result Canvas Width","justify":"left","preferredSize":[0,0],"alignment":null,"helpTip":null}},"item-9":{"id":9,"type":"EditText","parentId":7,"style":{"enabled":true,"varName":"widthET","creationProps":{"noecho":false,"readonly":false,"multiline":false,"scrollable":false,"borderless":false,"enterKeySignalsOnChange":false},"softWrap":false,"text":"1920","justify":"left","preferredSize":[100,0],"alignment":null,"helpTip":"The wdith of the wallpaper"}},"item-10":{"id":10,"type":"Group","parentId":0,"style":{"enabled":true,"varName":"settingsGroup","preferredSize":[0,0],"margins":0,"orientation":"row","spacing":10,"alignChildren":["left","center"],"alignment":null}},"item-11":{"id":11,"type":"StaticText","parentId":7,"style":{"enabled":true,"varName":null,"creationProps":{"truncate":"none","multiline":false,"scrolling":false},"softWrap":false,"text":"Result Canvas Height","justify":"left","preferredSize":[0,0],"alignment":null,"helpTip":null}},"item-12":{"id":12,"type":"EditText","parentId":7,"style":{"enabled":true,"varName":"heightET","creationProps":{"noecho":false,"readonly":false,"multiline":false,"scrollable":false,"borderless":false,"enterKeySignalsOnChange":false},"softWrap":false,"text":"1080","justify":"left","preferredSize":[100,0],"alignment":null,"helpTip":"The height of the wallpaper"}},"item-13":{"id":13,"type":"Panel","parentId":10,"style":{"enabled":true,"varName":"textPanel","creationProps":{"borderStyle":"etched","su1PanelCoordinates":false},"text":"Text Settings","preferredSize":[0,0],"margins":10,"orientation":"column","spacing":5,"alignChildren":["left","top"],"alignment":null}},"item-14":{"id":14,"type":"StaticText","parentId":13,"style":{"enabled":true,"varName":null,"creationProps":{"truncate":"none","multiline":false,"scrolling":false},"softWrap":false,"text":"Size","justify":"left","preferredSize":[0,0],"alignment":null,"helpTip":null}},"item-15":{"id":15,"type":"EditText","parentId":13,"style":{"enabled":true,"varName":"fontSizeET","creationProps":{"noecho":false,"readonly":false,"multiline":false,"scrollable":false,"borderless":false,"enterKeySignalsOnChange":false},"softWrap":false,"text":"17","justify":"left","preferredSize":[80,0],"alignment":null,"helpTip":null}},"item-16":{"id":16,"type":"StaticText","parentId":13,"style":{"enabled":true,"varName":null,"creationProps":{"truncate":"none","multiline":false,"scrolling":false},"softWrap":false,"text":"Font","justify":"left","preferredSize":[0,0],"alignment":null,"helpTip":null}},"item-17":{"id":17,"type":"DropDownList","parentId":13,"style":{"enabled":true,"varName":"fontList","text":"DropDownList","listItems":"Times New Roman","preferredSize":[0,0],"alignment":null,"selection":0,"helpTip":null}},"item-18":{"id":18,"type":"Panel","parentId":10,"style":{"enabled":true,"varName":"colorPanel","creationProps":{"borderStyle":"etched","su1PanelCoordinates":false},"text":"Color","preferredSize":[0,0],"margins":10,"orientation":"column","spacing":5,"alignChildren":["left","top"],"alignment":null}},"item-20":{"id":20,"type":"Panel","parentId":18,"style":{"enabled":true,"varName":"borderColorPanel","creationProps":{"borderStyle":"etched","su1PanelCoordinates":false},"text":"Border Color RGB","preferredSize":[0,0],"margins":10,"orientation":"row","spacing":3,"alignChildren":["left","top"],"alignment":null}},"item-21":{"id":21,"type":"EditText","parentId":20,"style":{"enabled":true,"varName":"borderR","creationProps":{"noecho":false,"readonly":false,"multiline":false,"scrollable":false,"borderless":false,"enterKeySignalsOnChange":false},"softWrap":false,"text":"0","justify":"left","preferredSize":[30,0],"alignment":null,"helpTip":null}},"item-22":{"id":22,"type":"EditText","parentId":20,"style":{"enabled":true,"varName":"borderG","creationProps":{"noecho":false,"readonly":false,"multiline":false,"scrollable":false,"borderless":false,"enterKeySignalsOnChange":false},"softWrap":false,"text":"0","justify":"left","preferredSize":[30,0],"alignment":null,"helpTip":null}},"item-23":{"id":23,"type":"EditText","parentId":20,"style":{"enabled":true,"varName":"borderB","creationProps":{"noecho":false,"readonly":false,"multiline":false,"scrollable":false,"borderless":false,"enterKeySignalsOnChange":false},"softWrap":false,"text":"0","justify":"left","preferredSize":[30,0],"alignment":null,"helpTip":null}},"item-24":{"id":24,"type":"Panel","parentId":18,"style":{"enabled":true,"varName":"fontColorPanel","creationProps":{"borderStyle":"etched","su1PanelCoordinates":false},"text":"Font Color RGB","preferredSize":[0,0],"margins":10,"orientation":"row","spacing":3,"alignChildren":["left","top"],"alignment":null}},"item-25":{"id":25,"type":"EditText","parentId":24,"style":{"enabled":true,"varName":"fontR","creationProps":{"noecho":false,"readonly":false,"multiline":false,"scrollable":false,"borderless":false,"enterKeySignalsOnChange":false},"softWrap":false,"text":"0","justify":"left","preferredSize":[30,0],"alignment":null,"helpTip":null}},"item-26":{"id":26,"type":"EditText","parentId":24,"style":{"enabled":true,"varName":"fontG","creationProps":{"noecho":false,"readonly":false,"multiline":false,"scrollable":false,"borderless":false,"enterKeySignalsOnChange":false},"softWrap":false,"text":"0","justify":"left","preferredSize":[30,0],"alignment":null,"helpTip":null}},"item-27":{"id":27,"type":"EditText","parentId":24,"style":{"enabled":true,"varName":"fontB","creationProps":{"noecho":false,"readonly":false,"multiline":false,"scrollable":false,"borderless":false,"enterKeySignalsOnChange":false},"softWrap":false,"text":"0","justify":"left","preferredSize":[30,0],"alignment":null,"helpTip":null}},"item-28":{"id":28,"type":"Divider","parentId":0,"style":{"enabled":true,"varName":null}},"item-29":{"id":29,"type":"Button","parentId":33,"style":{"enabled":true,"varName":"assembleBTN","text":"Assemble","justify":"center","preferredSize":[100,50],"alignment":null,"helpTip":null}},"item-30":{"id":30,"type":"Button","parentId":36,"style":{"enabled":true,"varName":"backgroundsBTN","text":"Browse","justify":"center","preferredSize":[0,0],"alignment":null,"helpTip":null}},"item-31":{"id":31,"type":"Button","parentId":38,"style":{"enabled":true,"varName":"paintingsBTN","text":"Browse","justify":"center","preferredSize":[0,0],"alignment":null,"helpTip":null}},"item-32":{"id":32,"type":"Button","parentId":33,"style":{"enabled":true,"varName":"cancelBTN","text":"Cancel","justify":"center","preferredSize":[100,50],"alignment":null,"helpTip":null}},"item-33":{"id":33,"type":"Group","parentId":0,"style":{"enabled":true,"varName":"endButtonsGroup","preferredSize":[0,0],"margins":0,"orientation":"row","spacing":60,"alignChildren":["left","center"],"alignment":null}},"item-34":{"id":34,"type":"StaticText","parentId":36,"style":{"enabled":true,"varName":"backgroundsST","creationProps":{"truncate":"end","multiline":false,"scrolling":false},"softWrap":false,"text":"No images are selected","justify":"left","preferredSize":[0,0],"alignment":null,"helpTip":null}},"item-35":{"id":35,"type":"StaticText","parentId":38,"style":{"enabled":true,"varName":"paintingsST","creationProps":{"truncate":"none","multiline":false,"scrolling":false},"softWrap":false,"text":"No images are selected","justify":"left","preferredSize":[0,0],"alignment":null,"helpTip":null}},"item-36":{"id":36,"type":"Group","parentId":4,"style":{"enabled":true,"varName":"backgroundGroup","preferredSize":[0,0],"margins":6,"orientation":"row","spacing":10,"alignChildren":["left","center"],"alignment":null}},"item-37":{"id":37,"type":"Divider","parentId":4,"style":{"enabled":true,"varName":null}},"item-38":{"id":38,"type":"Group","parentId":4,"style":{"enabled":true,"varName":"paintingGroup","preferredSize":[0,0],"margins":6,"orientation":"row","spacing":10,"alignChildren":["left","center"],"alignment":null}}},"order":[0,4,3,36,34,30,37,5,38,35,31,28,10,7,8,9,11,12,13,14,15,16,17,18,20,21,22,23,24,25,26,27,33,29,32],"settings":{"importJSON":true,"indentSize":false,"cepExport":false,"includeCSSJS":true,"showDialog":true,"functionWrapper":false,"afterEffectsDockable":false,"itemReferenceList":"None"}}
            */ 

            // DIALOG
            // ======
            var dialog = new Window("dialog"); 
                dialog.text = "WallPaper Assembler"; 
                dialog.preferredSize.width = 600; 
                dialog.orientation = "column"; 
                dialog.alignChildren = ["center","top"]; 
                dialog.spacing = 10; 
                dialog.margins = 16; 

            // FILEPANEL
            // =========
            var filePanel = dialog.add("panel", undefined, undefined, {name: "filePanel"}); 
                filePanel.text = "Images Choosing"; 
                filePanel.preferredSize.width = 500; 
                filePanel.orientation = "column"; 
                filePanel.alignChildren = ["left","top"]; 
                filePanel.spacing = 0; 
                filePanel.margins = 10; 

            var statictext1 = filePanel.add("statictext", undefined, undefined, {name: "statictext1"}); 
                statictext1.text = "Background Images"; 

            // BACKGROUNDGROUP
            // ===============
            var backgroundGroup = filePanel.add("group", undefined, {name: "backgroundGroup"}); 
                backgroundGroup.orientation = "row"; 
                backgroundGroup.alignChildren = ["left","center"]; 
                backgroundGroup.spacing = 10; 
                backgroundGroup.margins = 6; 

            var backgroundsST = backgroundGroup.add("statictext", undefined, undefined, {name: "backgroundsST", truncate: "end"}); 
                backgroundsST.text = "No images are selected"; 

            var backgroundsBTN = backgroundGroup.add("button", undefined, undefined, {name: "backgroundsBTN"}); 
                backgroundsBTN.text = "Browse"; 


            // FILEPANEL
            // =========
            var divider1 = filePanel.add("panel", undefined, undefined, {name: "divider1"}); 
                divider1.alignment = "fill"; 

            var statictext2 = filePanel.add("statictext", undefined, undefined, {name: "statictext2"}); 
                statictext2.text = "Paintings "; 

            // PAINTINGGROUP
            // =============
            var paintingGroup = filePanel.add("group", undefined, {name: "paintingGroup"}); 
                paintingGroup.orientation = "row"; 
                paintingGroup.alignChildren = ["left","center"]; 
                paintingGroup.spacing = 10; 
                paintingGroup.margins = 6; 

            var paintingsST = paintingGroup.add("statictext", undefined, undefined, {name: "paintingsST"}); 
                paintingsST.text = "No images are selected"; 

            var paintingsBTN = paintingGroup.add("button", undefined, undefined, {name: "paintingsBTN"}); 
                paintingsBTN.text = "Browse"; 

            // DIALOG
            // ======
            var divider2 = dialog.add("panel", undefined, undefined, {name: "divider2"}); 
                divider2.alignment = "fill"; 

            // SETTINGSGROUP
            // =============
            var settingsGroup = dialog.add("group", undefined, {name: "settingsGroup"}); 
                settingsGroup.orientation = "row"; 
                settingsGroup.alignChildren = ["left","center"]; 
                settingsGroup.spacing = 10; 
                settingsGroup.margins = 0; 

            // SIZEPANEL
            // =========
            var sizePanel = settingsGroup.add("panel", undefined, undefined, {name: "sizePanel"}); 
                sizePanel.text = "Size Settings"; 
                sizePanel.orientation = "column"; 
                sizePanel.alignChildren = ["left","top"]; 
                sizePanel.spacing = 5; 
                sizePanel.margins = 10; 

            var statictext3 = sizePanel.add("statictext", undefined, undefined, {name: "statictext3"}); 
                statictext3.text = "Result Canvas Width"; 

            var widthET = sizePanel.add('edittext {properties: {name: "widthET"}}'); 
                widthET.helpTip = "The wdith of the wallpaper"; 
                widthET.text = "1920"; 
                widthET.preferredSize.width = 100; 

            var statictext4 = sizePanel.add("statictext", undefined, undefined, {name: "statictext4"}); 
                statictext4.text = "Result Canvas Height"; 

            var heightET = sizePanel.add('edittext {properties: {name: "heightET"}}'); 
                heightET.helpTip = "The height of the wallpaper"; 
                heightET.text = "1080"; 
                heightET.preferredSize.width = 100; 

            // TEXTPANEL
            // =========
            var textPanel = settingsGroup.add("panel", undefined, undefined, {name: "textPanel"}); 
                textPanel.text = "Text Settings"; 
                textPanel.orientation = "column"; 
                textPanel.alignChildren = ["left","top"]; 
                textPanel.spacing = 5; 
                textPanel.margins = 10; 

            var statictext5 = textPanel.add("statictext", undefined, undefined, {name: "statictext5"}); 
                statictext5.text = "Size"; 

            var fontSizeET = textPanel.add('edittext {properties: {name: "fontSizeET"}}'); 
                fontSizeET.text = "55"; 
                fontSizeET.preferredSize.width = 80; 

            var statictext6 = textPanel.add("statictext", undefined, undefined, {name: "statictext6"}); 
                statictext6.text = "Font"; 
            const fontList_array = []; 
            var fontList = textPanel.add("dropdownlist", undefined, undefined, {name: "fontList", items: fontList_array}); 
 

            // COLORPANEL
            // ==========
            var colorPanel = settingsGroup.add("panel", undefined, undefined, {name: "colorPanel"}); 
                colorPanel.text = "Color"; 
                colorPanel.orientation = "column"; 
                colorPanel.alignChildren = ["left","top"]; 
                colorPanel.spacing = 5; 
                colorPanel.margins = 10; 

            // BORDERCOLORPANEL
            // ================
            var borderColorPanel = colorPanel.add("panel", undefined, undefined, {name: "borderColorPanel"}); 
                borderColorPanel.text = "Border Color RGB"; 
                borderColorPanel.orientation = "row"; 
                borderColorPanel.alignChildren = ["left","top"]; 
                borderColorPanel.spacing = 3; 
                borderColorPanel.margins = 10; 

            var borderR = borderColorPanel.add('edittext {properties: {name: "borderR"}}'); 
                borderR.text = "0"; 
                borderR.preferredSize.width = 30; 

            var borderG = borderColorPanel.add('edittext {properties: {name: "borderG"}}'); 
                borderG.text = "0"; 
                borderG.preferredSize.width = 30; 

            var borderB = borderColorPanel.add('edittext {properties: {name: "borderB"}}'); 
                borderB.text = "0"; 
                borderB.preferredSize.width = 30; 

            // FONTCOLORPANEL
            // ==============
            var fontColorPanel = colorPanel.add("panel", undefined, undefined, {name: "fontColorPanel"}); 
                fontColorPanel.text = "Font Color RGB"; 
                fontColorPanel.orientation = "row"; 
                fontColorPanel.alignChildren = ["left","top"]; 
                fontColorPanel.spacing = 3; 
                fontColorPanel.margins = 10; 

            var fontR = fontColorPanel.add('edittext {properties: {name: "fontR"}}'); 
                fontR.text = "0"; 
                fontR.preferredSize.width = 30; 

            var fontG = fontColorPanel.add('edittext {properties: {name: "fontG"}}'); 
                fontG.text = "0"; 
                fontG.preferredSize.width = 30; 

            var fontB = fontColorPanel.add('edittext {properties: {name: "fontB"}}'); 
                fontB.text = "0"; 
                fontB.preferredSize.width = 30; 

            // ENDBUTTONSGROUP
            // ===============
            var endButtonsGroup = dialog.add("group", undefined, {name: "endButtonsGroup"}); 
                endButtonsGroup.orientation = "row"; 
                endButtonsGroup.alignChildren = ["left","center"]; 
                endButtonsGroup.spacing = 60; 
                endButtonsGroup.margins = 0; 

            var assembleBTN = endButtonsGroup.add("button", undefined, undefined, {name: "assembleBTN"}); 
                assembleBTN.text = "Assemble"; 
                assembleBTN.preferredSize.width = 100; 
                assembleBTN.preferredSize.height = 50; 

            var cancelBTN = endButtonsGroup.add("button", undefined, undefined, {name: "cancelBTN"}); 
                cancelBTN.text = "Cancel"; 
                cancelBTN.preferredSize.width = 100; 
                cancelBTN.preferredSize.height = 50; 



                    return dialog;
            } // createBuilderDialog
        
	/**
	 This function initializes the values in the controls
	 of the builder dialog 
	*/
	function initializeBuilder(builder) {
		// FILE PANEL
		with (builder.filePanel) {
                
                with(backgroundGroup)
                {
                        backgroundsBTN.addEventListener("click", function(){
                                backgroundImageFiles = File.openDialog ("Choose the background images.", "Image Files: *.jpg, *.jpeg, *.png", true)
                                if(backgroundImageFiles !== null && backgroundImageFiles.length > 0){
                                        var temp = ''
                                        for(var i=0; i<backgroundImageFiles.length; i++)temp+=backgroundImageFiles[i].name+';'
                                        backgroundsST.text = decodeURI(temp)
                                    }
                                else{
                                        backgroundsST.text = "No images are selected."
                                    }
                        })

                  }
              
              with(paintingGroup)
              {
                  paintingsBTN.addEventListener("click", function()
                        {
                                paintingImageFiles = File.openDialog ("Choose the paintings.", "Image Files: *.jpg, *.jpeg, *.png", true)
                                if(paintingImageFiles !== null && paintingImageFiles.length > 0){
                                        var temp = ''
                                        for(var i=0; i<paintingImageFiles.length; i++)temp+=paintingImageFiles[i].name+';'
                                        paintingsST.text  = decodeURI(temp)
                                    }
                                else{
                                        paintingsST.text = "No images are selected."
                                    }
                        })
               }
                
             
		}//filepanel
    
        //SIZE PANEL
        with(builder.settingsGroup.sizePanel)
        {
            widthET.addEventListener("changing", function()
            {
                try{
                    var num = parseInt(widthET.text)
                    if( isNaN(num) || num <=0 )throw new Error("Invalid CanvasH")
                    canvasH = UnitValue(num, "px")
                    }
                catch(e)
                    {
                     alert("Invalid Canvas Width: only positive whole numbers are allowed")
                     widthET.text = 1920
                     canvasH = UnitValue(1920, "px")
                     }
               })

           heightET.addEventListener("changing", function()
            {
                try{
                    var num = parseInt(heightET.text)
                    if(isNaN(num)|| num <=0)throw new Error("Invalid CanvasV")
                    canvasV = UnitValue(num, "px")
                    }
                catch(e)
                    {
                     alert("Invalid Canvas Height: only positive whole numbers are allowed")
                     heightET.text = 1080
                     canvasV = UnitValue(1080, "px")
                     }
               })
           
         }
     
     //TEXT PANEL
     with(builder.settingsGroup.textPanel)
     {
         fontSizeET.addEventListener("changing", function()
         {
                try{
                    var num = parseInt(fontSizeET.text)
                    if(isNaN(num)|| num <=0)throw new Error("Invalid FontSize")
                    fontSize = num
                    }
                catch(e)
                    {
                     alert("Invalid Font Size: only positive whole numbers are allowed")
                     fontSizeET.text = 55
                     fontSize = 55
                     }
          })
        //Fonts Initialization   
            for(var i = 0; i < app.fonts.length; i++)
            {
                fontList.add("item", app.fonts[i].postScriptName)                     
            }
            fontList.addEventListener("change", function()
            {
                 currFontName =fontList.selection
             })
            fontList.selection = 0;
           
       }
   
   
   with(builder.settingsGroup.colorPanel)
   {
       
        borderColorPanel.borderR.addEventListener("changing", function()
            {
                try{
                    var num = parseInt(borderColorPanel.borderR.text)
                    if(isNaN(num)||num <0 || num > 255)throw new Error("Invalid borderR")
                    borderRGB[0] = num;
                    }
                catch(e)
                    {
                     alert("Invalid Red for Border: only  whole numbers in range of 0 to 255 are allowed")
                     borderRGB[0] = 0
                     borderColorPanel.borderR.text = 0
                     }
               })
         borderColorPanel.borderG.addEventListener("changing", function(){
                try{
                    var num =parseInt(borderColorPanel.borderG.text)
                    if(isNaN(num)||num <0 || num > 255)throw new Error("Invalid borderG")
                    borderRGB[1] = num;
                    }
                catch(e)
                    {
                     alert("Invalid Green for Border: only  whole numbers in range of 0 to 255 are allowed")
                     borderRGB[1] = 0
                     borderColorPanel.borderG.text = 0
                     }
               })
         borderColorPanel.borderB.addEventListener("changing", function()
            {
                try{
                    var num =parseInt(borderColorPanel.borderB.text)
                    if(isNaN(num)||num <0 || num > 255)throw new Error("Invalid borderB")
                    borderRGB[2] = num;
                    }
                catch(e)
                    {
                     alert("Invalid Blue for Border: only  whole numbers in range of 0 to 255 are allowed")
                     borderRGB[2] = 0
                     borderColorPanel.borderB.text  = 0
                     }
               })

          fontColorPanel.fontR.addEventListener("changing", function()
            {
                try{
                    
                    var num =  parseInt(fontColorPanel.fontR.text)
                    
                    if(isNaN(num)|| num <0 || num > 255)throw new Error("Invalid fontR")
                    fontRGB[0] = num;
                    }
                catch(e)
                    {
                     alert("Invalid Red for Font: only  whole numbers in range of 0 to 255 are allowed")
                     fontRGB[0] = 0
                     fontColorPanel.fontR.text = 0
                     }
               })
           fontColorPanel.fontG.addEventListener("changing", function()
            {
                try{
                    var num = parseInt(fontColorPanel.fontG.text)
                    if(isNaN(num)||num <0 || num > 255)throw new Error("Invalid fontG")
                    fontRGB[1] = num;
                    }
                catch(e)
                    {
                     alert("Invalid Green for Font: only  whole numbers in range of 0 to 255 are allowed")
                     fontRGB[1] = 0
                     fontColorPanel.fontG.text = 0
                     }
               })
            fontColorPanel.fontB.addEventListener("changing", function()
            {
                try{
                    var num = parseInt(fontColorPanel.fontB.text)
                    if(isNaN(num)||num <0 || num > 255)throw new Error("Invalid fontB")
                    fontRGB[2] = num;
                    }
                catch(e)
                    {
                     alert("Invalid Blue for Font: only  whole numbers in range of 0 to 255 are allowed")
                     fontRGB[2] = 0
                     fontColorPanel.fontB.text = 0
                     }
               })
      }//Color Panel
		with (builder.endButtonsGroup) {
			// The Assemble and Cancel buttons close this dialog
			assembleBTN.addEventListener('click', function () { this.parent.parent.close(1); })
			cancelBTN.addEventListener('click', function () { this.parent.parent.close(2); });
		}

	} // initializeBuilder


	/**
	 This function invokes the dialog an returns its result
	*/
	function runBuilder(builder) {
		return builder.show();
	}

    /**
        Main function that assembles all the wallpapers
      */
    function assemble() {

        while(app.documents.length) {
        app.activeDocument.close()
        }
        var scriptsFile = new File($.fileName);
        var saveToRootFolder = scriptsFile.parent.fsName + "\\resultWallpapers\\"
       const startRulerUnits =  app.preferences.rulerUnits
       const startTypeUnits =     app.preferences.typeUnits 
        const startDisplayDialogs =  app.displayDialogs 
        app.preferences.rulerUnits = Units.PIXELS
        app.preferences.typeUnits  = TypeUnits.PIXELS
        app.displayDialog = DialogModes.NO
        
        const prefPortraitWidth =  canvasH * 0.484375
        const prefPortraitHeight = canvasV * 0.861
        const portraitWidthToHeightRatio = prefPortraitWidth / prefPortraitHeight
        
        const prefLandWidth = canvasH * 0.84375
        const prefLandHeight = canvasV * 0.75
        const landWidthToHeightRatio = prefLandWidth / prefLandHeight
        
        const taskBarMaxHeight = UnitValue(60, 'px')
        
        const centerX = canvasH/2
        const centerY = canvasV/2
         const borderColor = new SolidColor
         borderColor.rgb.red = borderRGB[0]
         borderColor.rgb.green =  borderRGB[1]
         borderColor.rgb.blue =  borderRGB[2]
         
         const fontColor = new SolidColor
         fontColor.rgb.red = fontRGB[0]
         fontColor.rgb.green =  fontRGB[1]
         fontColor.rgb.blue =  fontRGB[2]
        
        var rootFolder = Folder(saveToRootFolder);
        if(!rootFolder.exists) rootFolder.create();

    for(var backgroundIndex = 0; backgroundIndex < backgroundImageFiles.length; backgroundIndex++)
    {
        var backFile = backgroundImageFiles[backgroundIndex]
        
        var wallFolder = Folder( saveToRootFolder + backFile.name.match(/.+(?=\..*?)/gm) [0]+ "\\");
        if(!wallFolder.exists) wallFolder.create();

        var backgroundDoc = OpenImage(backFile)
         backgroundDoc.changeMode(ChangeMode.RGB)
         backgroundDoc.resizeCanvas(canvasH, canvasV)
        
        for(var paintingIndex = 0; paintingIndex<paintingImageFiles.length; paintingIndex++)
        {
          
            var paintFile = paintingImageFiles[paintingIndex]
            
            var resultPath = backFile.name.match(/.+(?=\..*?)/gm)[0] + "\\"+paintFile.name.match(/.+(?=\..*?)/gm)[0] + '.png'
           
            var paintingDoc  = OpenImage(paintFile)
            paintingDoc.changeMode(ChangeMode.RGB)
            
            //Checking for landscape
            var isLandscape = paintingDoc.width >= 1.3 * paintingDoc.height

            var textPiece = decodeURI(paintFile.name).replace ( /\.[^\.]*?$/gm, "").replace(/_+/gm, (isLandscape) ? ' ' : '\r')//No new lines with landscape mode

            var widthToHeigthRation = paintingDoc.width / paintingDoc.height
            if(isLandscape)
            {
               
                if(widthToHeigthRation > landWidthToHeightRatio)  paintingDoc.resizeImage(prefLandWidth, null, null, ResampleMethod.BICUBIC);
                else paintingDoc.resizeImage(null, prefLandHeight, null, ResampleMethod.BICUBIC);
             }
            else
            {
                if(widthToHeigthRation > portraitWidthToHeightRatio) paintingDoc.resizeImage(prefPortraitWidth, null, null, ResampleMethod.BICUBIC);
                else paintingDoc.resizeImage(null, prefPortraitHeight, null, ResampleMethod.BICUBIC);
            }
                
            
                var halfHeight = paintingDoc.height/2
                var halfWidth  = paintingDoc.width/2
                var textHMargin = UnitValue(10, 'px')
                var textHeight =  canvasV/2
                var textWidth =  centerX - halfWidth - 2 * textHMargin
                var textXOffset = 0
                var textYOffset = 0
                var paintingVerticalOffset = 0
                if(isLandscape)
                {
                    textHeight =UnitValue(2 * fontSize * 1.25, 'px')
                    textWidth =  paintingDoc.width
                    textXOffset = centerX - halfWidth
                    textYOffset = canvasV - taskBarMaxHeight - textHeight
                    paintingVerticalOffset = centerY - halfHeight - taskBarMaxHeight - textHeight
                }
                app.doAction("Automate Curves", "Wallpaper Assembler")
                paintingDoc.flatten()
                paintingDoc.selection.selectAll()
                paintingDoc.selection.copy()

                paintingDoc.close(SaveOptions.DONOTSAVECHANGES)
           
            var centerRegion = Array(
            Array(centerX - halfWidth, centerY-halfHeight + paintingVerticalOffset),
            Array(centerX + halfWidth, centerY-halfHeight+ paintingVerticalOffset),
            Array(centerX + halfWidth, centerY+halfHeight+ paintingVerticalOffset),
            Array(centerX - halfWidth, centerY+halfHeight+ paintingVerticalOffset),
            Array(centerX - halfWidth, centerY-halfHeight+ paintingVerticalOffset))

            backgroundDoc.selection.select(centerRegion)
            var paintingLayer = backgroundDoc.paste()
            paintingLayer.name = 'painting'
            
            backgroundDoc.selection.stroke(borderColor, 3 , StrokeLocation.INSIDE)
            var textLayer =  backgroundDoc.artLayers.add()
            textLayer.kind = LayerKind.TEXT
            textLayer.name = 'title'
            var text = textLayer.textItem
            
             text.color = fontColor
            text.kind = TextType.PARAGRAPHTEXT
            text.justification = Justification.CENTER
            text.height = new UnitValue(textHeight * 72/backgroundDoc.resolution, 'pt');
            text.width = new UnitValue(textWidth * 72/backgroundDoc.resolution, 'pt');
            text.size = new UnitValue(fontSize * 72/backgroundDoc.resolution, 'pt');
            text.contents = textPiece
            text.font = currFontName
            text.useAutoLeading=true
            text.position = [0, 0]
           
            
             text.convertToShape()

            var theBounds = textLayer.bounds;
            var layerWidth = theBounds[2] - theBounds[0];
            var layerHeight = theBounds[3] - theBounds[1];

           if(isLandscape)
           {
               textYOffset += (textHeight - layerHeight)/2
            }
            else
            {
                textYOffset =  centerY - (layerHeight/2)
             }
         
            textLayer.translate(textXOffset,  textYOffset)
            backgroundDoc.saveAs(new File(saveToRootFolder + resultPath), new PNGSaveOptions() ,true)
            backgroundDoc.artLayers.getByName('title').remove()
            backgroundDoc.artLayers.getByName('painting').remove()
            finishedFilesCounter++
           
         }
      backgroundDoc.close(SaveOptions.DONOTSAVECHANGES)
    }

    app.preferences.rulerUnits = startRulerUnits
    app.preferences.typeUnits = startTypeUnits
    app.displatDialogs = startDisplayDialogs
    alert(finishedFilesCounter + " new wallpapers are saved in: " + saveToRootFolder)
}

//------------- "Main" -------------//
    var builder = createBuilderDialog(); 
	initializeBuilder(builder);
    if(runBuilder(builder) == 1 ) assemble()
  return retval;
  }

function OpenImage(file)
{
	this.imageFile = new File(file);

    this.requiredContext = "\tExecute against ESTK\nAdobe Photoshop must be running and\nthe image file exists in the Resources folder.";
    $.level = 1; // Debugging level
    if(!BridgeTalk.isRunning("photoshop") || !this.imageFile.exists) 
	{   
        $.writeln("ERROR:: Cannot run OpenImage");
        $.writeln(this.requiredContext);
		return null;		
	}
    $.writeln("Starting opening " + this.imageFile.name);
    
	// send image to Photoshop using the Cross-DOM open() function
  return open(new File(this.imageFile));
}

    new WallpaperAssemblerBox().run();

