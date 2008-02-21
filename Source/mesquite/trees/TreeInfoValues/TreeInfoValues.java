/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison.Version 2.01, December 2007.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)Modified 27 July 01: name reverted to "Tree Legend"; added getNameForMenuItem "Tree Legend..." */package mesquite.trees.TreeInfoValues;import java.awt.*;import java.net.*;import java.util.*;import java.io.*;import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.trees.lib.TreeInfoExtraPanel;public class TreeInfoValues extends TreeInfoPanelAssistant  {	ValuesPanel panel;	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed		EmployeeNeed e = registerEmployeeNeed(NumberForTree.class, getName() + " needs methods to calculate values that pertain to the tree in the tree window, such as a parsimony score, an imbalance statistic, and so on.", 		"Values for the tree are shown automatically in the Tree Information Panel which is available in the Tree menu of the Tree Window");		e.setPriority(1);	}	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		return true;	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return false;	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return true;	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	/*.................................................................................................................*	public  Class getHireSubchoice(){		return NumberForTree.class;	}	/*.................................................................................................................*/	public TreeInfoExtraPanel getPanel(ClosablePanelContainer container){		panel =  new ValuesPanel(container, this);		return panel;	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) {		Snapshot temp = new Snapshot();		for (int i = 0; i<getNumberOfEmployees(); i++) {			Object e=getEmployeeVector().elementAt(i);			if (e instanceof NumberForTree) {				temp.addLine("newValue ", ((MesquiteModule)e));			}		}		temp.addLine("panelOpen " + panel.isOpen());		return temp;	}	public void employeeQuit(MesquiteModule m){		if (m == null)			return;		//zap values panel line	}	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(this.getClass(), "Turns off one of the modules employed to display a line in the tree legend", "[employee number]", commandName, "closeEmployee")) {			// argument is ID; fire it and zap values panel line		}		else if (checker.compare(this.getClass(), "Hires a module to display a new line in the tree legend", "[name of module]", commandName, "newValue")) {			incrementMenuResetSuppression();			NumberForTree ntt= (NumberForTree)hireNamedEmployee(NumberForTree.class, arguments);			decrementMenuResetSuppression();			if (ntt!=null) {				ntt.setUseMenubar(false);  				panel.addEmployee(ntt);				resetContainingMenuBar();			}			return ntt;		}		else if (checker.compare(this.getClass(), "Sets the panel open", null, commandName, "panelOpen")) {			if (panel != null)				panel.setOpen(arguments == null || arguments.equalsIgnoreCase("true"));		}		else 			return  super.doCommand(commandName, arguments, checker);		return null;	}	public void hireNew(){		NumberForTree ntt= (NumberForTree)hireEmployee(NumberForTree.class, "Value to show for tree");		if (ntt!=null) {			ntt.setUseMenubar(false);  			panel.addEmployee(ntt);			resetContainingMenuBar();		}	}	/*.................................................................................................................*/	public void employeeOutputInvalid(MesquiteModule employee, MesquiteModule source) {		if (employee !=null) {			// make values panel line blank		}	}	/*.................................................................................................................*/	public int whichString(MesquiteModule employee) {		if (employee !=null) {			return getEmployeeVector().indexOf(employee) +1;		}		return -1;	}	/*.................................................................................................................*/	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification) {		if (employee !=null) {			panel.recalculate(employee);		}	}	/*.................................................................................................................*/	public void endJob() {		super.endJob();		resetContainingMenuBar();	}	/*.................................................................................................................*/	public String getName() {		return "Values (Tree information panel)...";	}	/*.................................................................................................................*/	public String getExplanation() {		return "Makes the Values section of the Tree Information Panel to display values pertaining to the tree.";	}}/*===========================================*/class ValuesPanel extends TreeInfoExtraPanel {	StringInABox[] boxes;	String message = null;	int neededHeight = 20;	TreeInfoValues ownerModule;	int maxNumLines = 100;	int numLines = 0;	NumberForTree[] employees;	String[] results;	int[] verts;	Image add, query;	public ValuesPanel(ClosablePanelContainer container, TreeInfoValues ownerModule){		super(container, "Values");		add = MesquiteImage.getImage(MesquiteModule.getRootImageDirectoryPath() + "addGray.gif");		query = MesquiteImage.getImage(MesquiteModule.getRootImageDirectoryPath() + "queryGray.gif");		boxes =  new StringInABox[maxNumLines];		for (int i= 0; i<maxNumLines; i++)			boxes[i] = new StringInABox("", null, 50);		verts = new int[maxNumLines];		employees = new NumberForTree[maxNumLines];		results = new String[maxNumLines];		this.ownerModule = ownerModule;	}	public void setTree(Tree tree){		super.setTree(tree);		recalculateAll();		container.requestHeightChange(this);		repaint();	}	public void recalculateAll(){		MesquiteString resultString = new MesquiteString();		MesquiteNumber result = new MesquiteNumber();		for (int i=0; i<numLines; i++){			employees[i].calculateNumber(tree, result, resultString);			results[i] = resultString.getValue();		}	}	public void recalculate(MesquiteModule mb){		MesquiteString resultString = new MesquiteString();		MesquiteNumber result = new MesquiteNumber();		int i = findLine(mb);		if (i>=0){			employees[i].calculateNumber(tree, result, resultString);			results[i] = resultString.getValue();			container.requestHeightChange(this);			repaint();		}	}	/* to be used by subclasses to tell that panel touched */	public void mouseUp(int modifiers, int x, int y, MesquiteTool tool) {		if (y<MINHEIGHT){			if (x> getWidth()- 20)				ownerModule.alert("Values:  To change settings of one of the calculations, click on its text below until the popup menu appears.  To add a new calculations, click the + button beside Values");  //query button hit			else if (x> getWidth()- 40)				ownerModule.hireNew();  //add button hit			else				super.mouseUp(modifiers,  x,  y,  tool);		}		else 			super.mouseUp(modifiers,  x,  y,  tool);	}	/* to be used by subclasses to tell that panel touched */	public void mouseDown (int modifiers, int clickCount, long when, int x, int y, MesquiteTool tool) {		int i = findLine(y);		if (i>=0 && i<numLines)			employees[i].showPopUp(this, 0, verts[i]);	}	public void addEmployee(NumberForTree mb){		employees[numLines] = mb;		if (tree != null){			MesquiteString resultString = new MesquiteString();			if (numLines == 0 && !open)				open = true;			MesquiteNumber result = new MesquiteNumber();			mb.calculateNumber(tree, result, resultString);			results[numLines] = resultString.getValue();		}		else 			results[numLines] = "";		numLines++;		container.requestHeightChange(this);	}	public void removeEmployee(NumberForTree mb){		int which = findLine(mb);		if (which>=0){			for (int i=which; i<numLines; i++)				employees[i] = employees[i+1];			numLines--;		}	}	public int findLine(MesquiteModule id) {		for (int i=0; i<employees.length; i++){			if (id == employees[i])				return i;		}		return -1;	}	public int findLine(int y) {		if (y < MINHEIGHT)			return -1;		for (int i=0; i<numLines; i++){			if (y< verts[i])				return i;		}		return -1;	}	public int getRequestedHeight(int width){		if (!open)			return MINHEIGHT;		neededHeight= 0;		for (int i= 0; i<numLines; i++){			boxes[i].setFont(getFont());			boxes[i].setString(results[i]);			boxes[i].setWidth(width-4);			neededHeight += 4 + boxes[i].getHeight();			verts[i] = neededHeight + MINHEIGHT;		}		return neededHeight + MINHEIGHT;	}	public void paint(Graphics g){		super.paint(g);		g.drawImage(query, getWidth()-20, 4, this);		g.drawImage(add, getWidth()-40, 4, this);		int vertical = MINHEIGHT;		int width = getWidth();		for (int i= 0; i<numLines; i++){			g.setColor(Color.black);			boxes[i].setFont(getFont());			boxes[i].setString(results[i]);			boxes[i].setWidth(width-4);			boxes[i].draw(g,4, vertical);			vertical += 4 + boxes[i].getHeight();			g.setColor(Color.gray);			g.drawLine(0,vertical, width, vertical);		}	}}