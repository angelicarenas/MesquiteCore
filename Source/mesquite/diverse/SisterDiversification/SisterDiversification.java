/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.. Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.diverse.SisterDiversification;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.categ.lib.*;import mesquite.diverse.lib.*;/* ======================================================================== *//**Calculates a number for a character and a tree.*/public class SisterDiversification extends NumForCharAndTreeDivers  {	public boolean startJob(String arguments, Object condition, boolean hiredByName) { 		return true;  	} 		/*.................................................................................................................*/	public void initialize(Tree tree, CharacterDistribution states) {  	 }		public boolean requestPrimaryChoice(){		return true;	}	/*.................................................................................................................*/	public long statesAbove(int node, Tree tree, CategoricalDistribution states) {		if (tree.nodeIsTerminal(node))			return states.getState(tree.taxonNumberOfNode(node));		long left = statesAbove(tree.firstDaughterOfNode(node), tree, states);		long right = statesAbove(tree.lastDaughterOfNode(node), tree, states);		if (CategoricalState.cardinality(left) == 1 && CategoricalState.cardinality(right) ==1 && CategoricalState.cardinality(left | right) > 1){			int leftState = CategoricalState.minimum(left);			int rightState = CategoricalState.minimum(right);			if (leftState == 0 && rightState == 1){				if (tree.numberOfTerminalsInClade(tree.firstDaughterOfNode(node)) > tree.numberOfTerminalsInClade(tree.lastDaughterOfNode(node))) //first, left, 0 is bigger					win0++;				else if (tree.numberOfTerminalsInClade(tree.firstDaughterOfNode(node)) < tree.numberOfTerminalsInClade(tree.lastDaughterOfNode(node))) //first, left, 1 is bigger					win1++;			}			else if (leftState == 1 && rightState == 0){				if (tree.numberOfTerminalsInClade(tree.firstDaughterOfNode(node)) > tree.numberOfTerminalsInClade(tree.lastDaughterOfNode(node))) //first, left, 0 is bigger					win1++;				else if (tree.numberOfTerminalsInClade(tree.firstDaughterOfNode(node)) < tree.numberOfTerminalsInClade(tree.lastDaughterOfNode(node))) //first, left, 1 is bigger					win0++;			}		}			return left | right;	}	int win1 = 0;	int win0 =0;	/*.................................................................................................................*/	public  void calculateNumber(Tree tree, CharacterDistribution observedStates, MesquiteNumber result, MesquiteString resultString) {      	 	if (result==null)    	 		return;    	clearResultAndLastResult(result);		if (tree==null || observedStates==null) {			if (resultString!=null)				resultString.setValue("Sister diversification unassigned because no tree or no character supplied");			return;		}		win0 = 0;		win1 = 0;		statesAbove(tree.getRoot(), tree, (CategoricalDistribution)observedStates);		//assumes state 1 is 		int n = win0 + win1;		if (n>=0){			double sum = 0;			for (int i=win1; i<=n; i++)				sum += Binomial.probability(n, i, 0.5);			result.setValue(sum);			if (resultString != null)				resultString.setValue("Sister Diversification Binomial P= " + sum);		}		else			if (resultString!=null)				resultString.setValue("Sister diversification unassigned because no appropriate comparisons found");			saveLastResult(result);		saveLastResultString(resultString);	}	/*.................................................................................................................*/    	 public String getName() {		return "Sister Diversification";   	 }	/*.................................................................................................................*/    	 public boolean showCitation() {		return true;   	 }	/*.................................................................................................................*/   	 public boolean isPrerelease(){  	 	return true;  	 } 	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Compares sister clades and returns the P value of the one-tailed null hypothesis that uniform clades of state 1 are smaller than uniform clades of state 0 among those with different values (P value calculated by Binomial probability)." ;   	 }  	public CompatibilityTest getCompatibilityTest(){		return new RequiresExactlyCategoricalData();	}	 }