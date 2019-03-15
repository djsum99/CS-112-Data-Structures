package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	String sub = "";
    	for(int i=0;i<expr.length();i++) {
    		String character = expr.substring(i,i+1);
    		if(character.equals(" ")==false&&character.equals("\t")==false&&character.equals("*")==false&&character.equals("+")==false&&character.equals("-")==false&&character.equals("/")==false&&character.equals("(")==false&&character.equals(")")==false&&character.equals("[")==false&&character.equals("]")==false&&character.equals("0")==false&&character.equals("1")==false&&character.equals("2")==false&&character.equals("3")==false&&character.equals("4")==false&&character.equals("5")==false&&character.equals("6")==false&&character.equals("7")==false&&character.equals("8")==false&&character.equals("9")==false) {
    			sub = sub+character;
    		}
    		else if(sub.length()!=0) {
    			if(character.equals("[")) {
    				if(arrays.size()==0) {
    					arrays.add(new Array(sub));
    				}
    				else {
    					boolean copies = false;
    					for(int j=0;j<arrays.size();j++) {
        					if(arrays.get(j).name.equals(sub)!=false) {
        						copies=true;
        					}
    					}
    					if(copies==false) {
    						arrays.add(new Array(sub));
    					}
    				}
    			}
    			else {
    				if(vars.size()==0) {
    					vars.add(new Variable(sub));
    				}
    				else {
    					boolean copies = false;
    					for(int j=0;j<vars.size();j++) {
        					if(vars.get(j).name.equals(sub)!=false) {
        						copies=true;
        					}
    					}
    					if(copies==false) {
    						vars.add(new Variable(sub));
    					}
    				}
    			}
    			sub = "";
    		}
    	}
    	if(sub.equals("")==false) {
    		boolean copies = false;
    		for(int i=0;i<vars.size();i++) {
    			if(vars.get(i).name.equals(sub)) {
    				copies=true;
    			}
    		}
    		if(copies==false) {
        		vars.add(new Variable(sub));
    		}
    	}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	return Float.parseFloat(eval(expr,vars,arrays));
    }
    public static String insertValues(String expr, ArrayList<Variable> vars) {
    	expr.replaceAll(" ", "");
    	expr.replaceAll("\t","");
    	String sub = "";
    	String exprWithNums = "";
    	for(int i=0;i<expr.length();i++) {
    		String character = expr.substring(i,i+1);
    		if(character.equals(" ")==false&&character.equals("	")==false&&character.equals("*")==false&&character.equals("+")==false&&character.equals("-")==false&&character.equals("/")==false&&character.equals("(")==false&&character.equals(")")==false&&character.equals("[")==false&&character.equals("]")==false&&character.equals("0")==false&&character.equals("1")==false&&character.equals("2")==false&&character.equals("3")==false&&character.equals("4")==false&&character.equals("5")==false&&character.equals("6")==false&&character.equals("7")==false&&character.equals("8")==false&&character.equals("9")==false) {
    			sub = sub+character;
    		}
    		else {
    			if(character.equals("[")) {
    				exprWithNums=exprWithNums+sub+character;
    			}
    			else {
    				for(int j=0;j<vars.size();j++) {
        				if(vars.get(j).name.equals(sub)) {
        					exprWithNums=exprWithNums+vars.get(j).value;
        				}
        			}
        			exprWithNums=exprWithNums+character;
    			}
    			sub="";
    		}
    	}
    	if(sub.equals("")==false) {
    		for(int j=0;j<vars.size();j++) {
				if(vars.get(j).name.equals(sub)) {
					exprWithNums=exprWithNums+vars.get(j).value;
				}
    		}
    	}
    	String exprWithNumsNoSpaces = "";
    	for(int i=0;i<exprWithNums.length();i++) {
    		if(exprWithNums.substring(i,i+1).equals(" ")==false&&exprWithNums.substring(i,i+1).equals("\t")==false) {
    			exprWithNumsNoSpaces=exprWithNumsNoSpaces+exprWithNums.substring(i,i+1);
    		}
    	}
    	return exprWithNumsNoSpaces;
    }
    public static String eval(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	expr = insertValues(expr,vars);
    	ArrayList<String> ex = new ArrayList<String>();
    	String sub = "";
    	for(int i=0;i<expr.length();i++) {
    		String character = expr.substring(i,i+1);
    		if(character.equals("*")==false&&character.equals("+")==false&&character.equals("-")==false&&character.equals("/")==false&&character.equals("(")==false&&character.equals(")")==false&&character.equals("[")==false&&character.equals("]")==false) {
    			sub = sub+character;
    		}
    		else if(character.equals("(")) {
    			sub="(";
    			int counter = i+1;
    			int numOpeners = 1;
    			int numClosers = 0;
    			while(numClosers!=numOpeners) {
    				String pointer=expr.substring(counter,counter+1);
    				if(pointer.equals("(")) {
    					numOpeners++;
    				}
    				else if(pointer.equals(")")) {
    					numClosers++;
    				}
    				if(counter<expr.length()) {
        				sub=sub+pointer;
        				counter++;
    				}
    			}
    			ex.add(sub);
    			i=counter-1;
    			sub="";
    		}
    		else if(character.equals("[")) {
    			sub=sub+"[";
    			int counter = i+1;
    			int numOpeners = 1;
    			int numClosers = 0;
    			while(numClosers!=numOpeners) {
    				String pointer=expr.substring(counter,counter+1);
    				if(pointer.equals("[")) {
    					numOpeners++;
    				}
    				else if(pointer.equals("]")) {
    					numClosers++;
    				}
    				if(counter<expr.length()) {
        				sub=sub+pointer;
        				counter++;
    				}
    			}
    			ex.add(sub);
    			i=counter-1;
    			sub="";
    		}
    		else {
    			if(sub.equals("")) {
    				ex.add(character);
    			}
    			else {
    				ex.add(sub);
    				ex.add(character);
    			}
    			sub="";
    		}
    	}
    	if(sub.equals("")==false) {
    		ex.add(sub);
    	}
    	for(int i=0;i<ex.size();i++) {
    		if(ex.get(i).substring(0,1).equals("(")) {
    			ex.set(i,eval(ex.get(i).substring(1, ex.get(i).length()-1),vars,arrays));
    		}
    	}
    	for(int i=0;i<ex.size();i++) {
    		if(ex.get(i).substring(0,1).equals("0")==false&&ex.get(i).substring(0,1).equals("1")==false&&ex.get(i).substring(0,1).equals("2")==false&&ex.get(i).substring(0,1).equals("3")==false&&ex.get(i).substring(0,1).equals("4")==false&&ex.get(i).substring(0,1).equals("5")==false&&ex.get(i).substring(0,1).equals("6")==false&&ex.get(i).substring(0,1).equals("7")==false&&ex.get(i).substring(0,1).equals("8")==false&&ex.get(i).substring(0,1).equals("9")==false&&ex.get(i).substring(0,1).equals("+")==false&&ex.get(i).substring(0,1).equals("-")==false&&ex.get(i).substring(0,1).equals("*")==false&&ex.get(i).substring(0,1).equals("/")==false&&ex.get(i).substring(0,1).equals("(")==false) {
    			int whereBracketBegins = 0;
    			String temp = ex.get(i).substring(0,1);
    			while(temp.equals("[")==false) {
    				temp=ex.get(i).substring(whereBracketBegins, whereBracketBegins+1);
    				whereBracketBegins++;
    			}
    			String toBeEvaluated = ex.get(i).substring(whereBracketBegins, ex.get(i).length()-1);
    			String evaluated = eval(toBeEvaluated,vars,arrays);
    			float index1 = Float.parseFloat(evaluated);
    			int index2 = (int) index1;
    			String arrayName = ex.get(i).substring(0, whereBracketBegins-1);
    			for(int j=0;j<arrays.size();j++) {
    				if(arrays.get(j).name.equals(arrayName)) {
    					int done = arrays.get(j).values[index2];
    					ex.set(i,done+"");
    				}
    			}
    		}
    	}
    	for(int i=0;i<ex.size()-1;i++) {
    		if(ex.get(i+1).equals("*")) {
    			float product = Float.parseFloat(ex.get(i))*Float.parseFloat(ex.get(i+2));
    			ex.set(i,product+"");
    			ex.remove(i+2);
    			ex.remove(i+1);
    			i-=1;
    		}
    		else if(ex.get(i+1).equals("/")) {
    			float quotient = Float.parseFloat(ex.get(i))/Float.parseFloat(ex.get(i+2));
    			ex.set(i,quotient+"");
    			ex.remove(i+2);
    			ex.remove(i+1);
    			i-=1;
    		}
    	}
    	for(int i=0;i<ex.size()-1;i++) {
    		if(ex.get(i+1).equals("+")) {
    			float sum = Float.parseFloat(ex.get(i))+Float.parseFloat(ex.get(i+2));
    			ex.set(i,sum+"");
    			ex.remove(i+2);
    			ex.remove(i+1);
    			i-=1;
    		}
    		else if(ex.get(i+1).equals("-")) {
    			float difference = Float.parseFloat(ex.get(i))-Float.parseFloat(ex.get(i+2));
    			ex.set(i,difference+"");
    			ex.remove(i+2);
    			ex.remove(i+1);
    			i-=1;
    		}
    	}
    	String answer = ex.get(0);
    	return answer;
    }
}
