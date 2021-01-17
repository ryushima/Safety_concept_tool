import javax.swing.*;

// for cut 'n paste:
def showDialog(String text) {
    def dialog = new JDialog(ui.frame)
    dialog.setSize(350, 450)
    dialog.setLocationRelativeTo(ui.frame)
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    dialog.add(new JScrollPane(new JTextArea(text)))
    ui.addEscapeActionToDialog(dialog)
    dialog.setVisible(true)
}

// Read the folder that is monitored
def Dirname=ID_1158912197.children[0].link.file

// get all code files under monitoring as well as their path relative to the root folder
def files=[]
def rel_files=[]
ID_608165453.children.each{
    files+=it.link.file
    rel_files+=Dirname.toPath().relativize(it.link.file.toPath()).toFile()
}

//Read repo link
repolink= ID_197895921['Github link']
//Read revision for the github links
revision= ID_197895921['revision']

// todo: Query the local clone for the revision
// for some reason, this code does not work, no idea why. the variant with echo works, the command itself run in a shell also works
//commandlinestring="""git -C "$Dirname" rev-parse HEAD"""
//out = commandlinestring.execute()
//out="""echo boris""".execute()
//stream= new StringBuffer()
//out.waitForProcessOutput(stream,System.err)
//showDialog(commandlinestring + '/n'+stream.toString())

//iterate all files under monitoring
def collection=[:]
files.eachWithIndex{file,index->
	def lines = file.readLines()
	repofile=rel_files[index]	
	// iterate all nodes of the map
	c.find{true}.each{currentNode->
		reqID=currentNode.getId()
		//iterate lines in code file
		lines.eachWithIndex{it,Line->
			// check if Requirement tag is present
			if (it.contains("[$reqID]")) {
				Linenumber=(Line+1).toString()
				//generate and store html link to the github file of the correct revision and line number
				html_linkstring="""<p><a href="$repolink/blob/$revision/$repofile#L$Linenumber">$repofile:L$Linenumber</a></p>"""
				if (collection.containsKey(reqID)) {
					collection[reqID]+=html_linkstring	
				}else {
					collection[reqID]=[html_linkstring]
				}
			}
		}

	}
}
// clean out Details of all Requirement nodes
c.find{it.style.name=="Requirement"}.each{
	it.setDetails(null)
}
// write Links in details of Requirement nodes
for (hit in collection){
	n=c.find{it.id==hit.key}
	n[0].setDetails('<html><body>' + hit.value.join('\n')+ '</body></html>')
}




