package tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class EncounterEngine {
	public static final int cActors = 6;
	public static final int cFactors = 12;
	public static final int cOptions = 5;
	public static final int cReactions = 4;
	public static final int maxEncounters = 500;
	public static final int maxPreDis = 5;	
	
	public static final int Bad_Good = 0;	
	public static final int False_Honest = 1;	
	public static final int Timid_Dominant = 2;	

	String[] factor = new String[cFactors];
	ArrayList<Encounter> encounters = new ArrayList<Encounter>();
	Actor[] actor = new Actor[cActors];
//	int iAntagonist, iProtagonist;
	Random rand;

	//**********************************************************************
	EncounterEngine(JFrame editorFrame) {				
		factor[0] = "Bad_Good";
		factor[1] = "False_Honest";
		factor[2] = "Timid_Dominant";
		factor[3] = "pBad_Good";
		factor[4] = "pFalse_Honest";
		factor[5] = "pTimid_Dominant";
		factor[6] = "-Bad_Good";
		factor[7] = "-False_Honest";
		factor[8] = "-Timid_Dominant";
		factor[9] = "-pBad_Good";
		factor[10] = "-pFalse_Honest";
		factor[11] = "-pTimid_Dominant";
		
//		iAntagonist = -1;
//		iProtagonist = 0;
		
		rand = new Random();
		try { loadEncounters(editorFrame); } 
		catch (IOException e1) { e1.printStackTrace(); }
   	catch (ParserConfigurationException e1) { e1.printStackTrace(); }
		catch (SAXException e1) { e1.printStackTrace(); }	
		
	}
	//**********************************************************************
	public String getActorLabel(int i) { return actor[i].getLabel(); }
	//**********************************************************************
	public String getFactor(int i) { return factor[i]; }
	//**********************************************************************
	public Actor getActor(int i) { return actor[i]; }
	//**********************************************************************
	public float getTrait(int iActor, int iTrait) { return actor[iActor].getTrait(iTrait); }
	//**********************************************************************
	public float getPTrait(int iActor, int jActor, int iTrait) { return actor[iActor].getPTrait(jActor, iTrait); }
	//**********************************************************************
//	public void setPTrait(int iActor, int jActor, int iTrait, float newValue) {  actor[iActor].setPTrait(jActor, iTrait, newValue); }
	//**********************************************************************
	public float getCTrait(int iActor, int jActor, int kActor, int iTrait) { return actor[iActor].getCTrait(jActor, kActor, iTrait); }
	//**********************************************************************
	public void setCTrait(int iActor, int jActor, int kActor, int iTrait, float newValue) { actor[iActor].setCTrait(jActor, kActor, iTrait, newValue); }
	//**********************************************************************
	public Encounter getEncounter(int i) { return encounters.get(i); }
	//**********************************************************************
	public int getEncounterSize() { return encounters.size(); }
	//**********************************************************************
	public void addEncounter(Encounter theEncounter) { encounters.add(theEncounter); }
	//**********************************************************************
	public void removeEncounter(int iEncounter) { encounters.remove(iEncounter); }
	//**********************************************************************
	public int run(int i, int j, Option theOption) {
		return 0;		
	}
// **********************************************************************	
	public int calculateReaction(int tAntagonist, int tProtagonist, Option tOption) {
		float bestInclination = -1.00f;
		int bestIndex = -1;
		for (int i = 0; (i<cReactions); ++i) {
			if (!tOption.getReaction(i).getText().equals("unused Reaction")) {
				float x1 = getTraitValue(tAntagonist, tProtagonist, tOption.getReaction(i).getFirstTrait());
				float x2 = getTraitValue(tAntagonist, tProtagonist, tOption.getReaction(i).getSecondTrait());					
				float bias = tOption.getReaction(i).getBias();
				float inclination = blend(x1, x2, bias);
				if (inclination > bestInclination) {
					bestInclination = inclination;
					bestIndex = i;
				}
			}
		}
		return bestIndex;
	}
// **********************************************************************
	public float getTraitValue(int tAntagonist, int tProtagonist, String sTrait) {
		float value;
		switch (sTrait) {
			case "Bad_Good": { value = getTrait(tAntagonist, 0); break; }
			case "False_Honest": { value = getTrait(tAntagonist, 1); break; }
			case "Timid_Dominant": { value = getTrait(tAntagonist, 2); break; }
			case "pBad_Good": { value = getPTrait(tAntagonist, tProtagonist, 0); break; }
			case "pFalse_Honest": { value = getPTrait(tAntagonist, tProtagonist, 1); break; }
			case "pTimid_Dominant": { value = getPTrait(tAntagonist, tProtagonist, 2); break; }
			case "-Bad_Good": { value = -getTrait(tAntagonist, 0); break; }
			case "-False_Honest": { value = -getTrait(tAntagonist, 1); break; }
			case "-Timid_Dominant": { value = -getTrait(tAntagonist, 0); break; }
			case "-pBad_Good": { value = -getPTrait(tAntagonist, tProtagonist, 0); break; }
			case "-pFalse_Honest": { value = -getPTrait(tAntagonist, tProtagonist, 1); break; }
			case "-pTimid_Dominant": { value = -getPTrait(tAntagonist, tProtagonist, 2); break; }
			default: { value = -0.9999f; break; }
		}
	return value; 	
	}
// **********************************************************************	
	public String decodeText(int tAntagonist, int tProtagonist, String inputString) {
		String output = inputString.replaceAll("=antagonistName=",actor[tAntagonist].getLabel());
		output = output.replaceAll("=AntagonistName=",actor[tAntagonist].getLabel());
		output = output.replaceAll("=ProtagonistName=", actor[tProtagonist].getLabel());
		output = output.replaceAll("=protagonistName=", actor[tProtagonist].getLabel());
		output = output.replaceAll("=he/she=", actor[tAntagonist].getIsMale() ? "he":"she");
		output = output.replaceAll("=He/She=", actor[tAntagonist].getIsMale() ? "He":"She");
		output = output.replaceAll("=Him/Her=",actor[tAntagonist].getIsMale() ? "Him":"Her");
		output = output.replaceAll("=him/her=",actor[tAntagonist].getIsMale() ? "him":"her");
		output = output.replaceAll("=his/her=", actor[tAntagonist].getIsMale() ? "his":"her");
		output = output.replaceAll("=His/Her=", actor[tAntagonist].getIsMale() ? "His":"Her");
		output = output.replaceAll("=his/hers=",actor[tAntagonist].getIsMale() ? "his":"hers");
		output = output.replaceAll("=His/Hers=",actor[tAntagonist].getIsMale() ? "His":"Hers");
		
		return output;
	}
// **********************************************************************
	public void loadEncounters(JFrame editorFrame) 
			throws IOException, ParserConfigurationException, SAXException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();		
		Document encounterDoc = null;
		InputStream is = null;		
		try {
			File aFile = new File(System.getProperty("user.dir")+"/EncounterList.xml");
			is = new FileInputStream(aFile);	
			try {
				encounterDoc = builder.parse(is);
			} catch (SAXException e) { System.out.println("SAXException"); }
			is.close();
		} catch (IOException e) { System.out.println("error in file loading"); }
		

/*		
		JFileChooser chooser = new JFileChooser();
		ProtectionDomain pd = EncounterEditor.class.getProtectionDomain();
		CodeSource cs = pd.getCodeSource();
		URL location = cs.getLocation();
		chooser.setCurrentDirectory(new File(location.getFile()));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
		File bFile = null;
		 chooser.setFileFilter(filter);
		 int returnVal = chooser.showOpenDialog(editorFrame);
		 if(returnVal == JFileChooser.APPROVE_OPTION) {
		    bFile = chooser.getSelectedFile();
//		    fileName = bFile.getName();
//		    theAbsolutePath = bFile.getAbsolutePath();
		 }
		 else  {
				JOptionPane.showMessageDialog(editorFrame,
					    "I have to quit because there's no Encounter file to work with",
					    "Fatal error",
					    JOptionPane.ERROR_MESSAGE);	
				System.exit(0);
		 }
*/		
		
		NodeList actorList = encounterDoc.getElementsByTagName("Actor");
		for (int i = 0; (i < actorList.getLength()); ++i) {
			Node aNode = actorList.item(i);
			String tempLabel = aNode.getAttributes().getNamedItem("label").getNodeValue();
			if (tempLabel.length()>0) {
				actor[i] = new Actor(tempLabel);
		 		NodeList childNodes = aNode.getChildNodes();
		 		for (int j = 0; (j < childNodes.getLength()); ++j) {
	 				if ((childNodes.item(j).getNodeName()).equals("isMale"))
	 					actor[i].setIsMale(Boolean.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("Bad_Good"))
	 					actor[i].setTrait(0, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("False_Honest"))
	 					actor[i].setTrait(1, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("Timid_Dominant"))
	 					actor[i].setTrait(2, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("AccordBad_Good"))
	 					actor[i].setAccord(0, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("AccordFalse_Honest"))
	 					actor[i].setAccord(1, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("AccordTimid_Dominant"))
	 					actor[i].setAccord(2, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("Vain"))
	 					actor[i].setVain(Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("Knows_Rosa"))
	 					actor[i].setKnows(0, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("Knows_Malthe"))
	 					actor[i].setKnows(1, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("Knows_Valdemar"))
	 					actor[i].setKnows(2, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("Knows_Augusta"))
	 					actor[i].setKnows(3, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("Knows_Johan"))
	 					actor[i].setKnows(4, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("Knows_Olivia"))
	 					actor[i].setKnows(5, Float.valueOf(childNodes.item(j).getTextContent()));
		 		}
			}
		}
		// Need to recognize the node PTraitList
		
		NodeList pTraitList = encounterDoc.getElementsByTagName("pTrait");
		for (int i = 0; (i < pTraitList.getLength()); ++i) {
			Node aNode = pTraitList.item(i);
			String kindLabel = aNode.getAttributes().getNamedItem("akind").getNodeValue();
			String fromLabel = aNode.getAttributes().getNamedItem("from").getNodeValue();
			String toLabel = aNode.getAttributes().getNamedItem("to").getNodeValue();
			float value = Float.valueOf(aNode.getAttributes().getNamedItem("value").getNodeValue());
			
			int iTrait = 0;
			while (!factor[iTrait].equals(kindLabel)) ++iTrait;
			int iActor = 0;
			while (!actor[iActor].getLabel().equals(fromLabel)) ++iActor;
			int jActor = 0;
			while (!actor[jActor].getLabel().equals(toLabel)) ++jActor;			
			actor[iActor].setPTrait(jActor, iTrait, value);

			
			// here we fake the P-values using Accord values and intrinsic values
//			float fakeValue = blend(actor[iActor].getAccord(iTrait), actor[jActor].getTrait(iTrait), actor[iActor].getKnows(jActor));
			
			// round off to one digit
//			int iFakeValue = (int)(10.0f * fakeValue);
//			fakeValue = (float)iFakeValue / 10.0f;
			
//			actor[iActor].setPTrait(jActor, iTrait, fakeValue);			
//		}
		
		// initialize C-values to mirror P-values, modulated by Knows

//		for (int i = 0; (i<cActors); ++i) {
//			for (int j = 0; (j<cActors); ++j) {
//				for (int k = 0; (k<cActors); ++k) {
//					for (int n=0; (n<3); ++n) {
//						float cFakeValue = blend(((2.0f * rand.nextFloat()) - 1.0f), actor[i].getPTrait(j, n), actor[i].getKnows(j));
//						// round off to one digit
//						int iFakeValue = (int)(10.0f * cFakeValue);
//						cFakeValue = (float)iFakeValue / 10.0f;
						
//						actor[i].setCTrait(j, k, n, cFakeValue);
//					}
					
//				}
//			}
		}
	
		NodeList cTraitList = encounterDoc.getElementsByTagName("cTrait");
		for (int i = 0; (i < cTraitList.getLength()); ++i) {
			Node aNode = cTraitList.item(i);
			String kindLabel = aNode.getAttributes().getNamedItem("akind").getNodeValue();
			String fromLabel = aNode.getAttributes().getNamedItem("from").getNodeValue();
			String aboutLabel = aNode.getAttributes().getNamedItem("about").getNodeValue();
			String toLabel = aNode.getAttributes().getNamedItem("to").getNodeValue();
			float value = Float.valueOf(aNode.getAttributes().getNamedItem("value").getNodeValue());
			
			int iTrait = 0;
			while (!factor[iTrait].equals(kindLabel)) ++iTrait;
			int iActor = 0;
			while (!actor[iActor].getLabel().equals(fromLabel)) ++iActor;
			int jActor = 0;
			while (!actor[jActor].getLabel().equals(toLabel)) ++jActor;			
			int kActor = 0;
			while (!actor[kActor].getLabel().equals(aboutLabel)) ++kActor;			
			actor[iActor].setCTrait(jActor, kActor, iTrait, value);
		}
			
		NodeList encounterList = encounterDoc.getElementsByTagName("Encounter");
		for (int i = 0; (i < encounterList.getLength()); ++i) {
			Node aNode = encounterList.item(i);
			String testTitle = aNode.getAttributes().getNamedItem("title").getNodeValue();
			if (testTitle.length()>0) {
				encounters.add(getNewEncounter());
				Encounter thisEncounter = getEncounter(i);
				int iOption = 0;
				thisEncounter.setTitle(testTitle);
		 		NodeList childNodes = aNode.getChildNodes();
		 		for (int j = 0; (j < childNodes.getLength()); ++j) {
	 				if ((childNodes.item(j).getNodeName()).equals("Author"))
	 					thisEncounter.setAuthor(childNodes.item(j).getTextContent());
	 				if ((childNodes.item(j).getNodeName()).equals("IntroText"))
	 					thisEncounter.setIntroText(childNodes.item(j).getTextContent());
	 				
	 				else if ((childNodes.item(j).getNodeName()).equals("Prerequisites")) {
	 					thisEncounter.getPrerequisites().clear();
	 					NodeList prereqChildNodes = childNodes.item(j).getChildNodes();
	 					for (int k=0; (k < prereqChildNodes.getLength()); ++k) {
	 						String temp = prereqChildNodes.item(k).getTextContent();
	 						if (temp.length()>2)
							thisEncounter.getPrerequisites().add(temp);
	 					}
	 				}
	 				else if ((childNodes.item(j).getNodeName()).equals("Disqualifiers")) {
	 					thisEncounter.getDisqualifiers().clear();
	 					NodeList disqualChildNodes = childNodes.item(j).getChildNodes();
	 					for (int k=0; (k < disqualChildNodes.getLength()); ++k) {
	 						String temp = disqualChildNodes.item(k).getTextContent();
	 						if (temp.length()>2)
	 							thisEncounter.getDisqualifiers().add(temp);
	 					}
	 				}
	 				else if ((childNodes.item(j).getNodeName()).equals("ExcludeAntagonist")) {
	 					NodeList excludeAntagonistChildNodes = childNodes.item(j).getChildNodes();
	 					for (int k=0; (k<excludeAntagonistChildNodes.getLength()); ++k) {
	 						Node thisNode = excludeAntagonistChildNodes.item(k);
	 						if (thisNode.getNodeName().equals(actor[0].getLabel()))
	 							thisEncounter.setIsAllowedToBeAntagonist(!Boolean.valueOf(thisNode.getTextContent()),0);
	 						else if (thisNode.getNodeName().equals(actor[1].getLabel()))
	 							thisEncounter.setIsAllowedToBeAntagonist(!Boolean.valueOf(thisNode.getTextContent()),1);
	 						else if (thisNode.getNodeName().equals(actor[2].getLabel()))
	 							thisEncounter.setIsAllowedToBeAntagonist(!Boolean.valueOf(thisNode.getTextContent()),2);
	 						else if (thisNode.getNodeName().equals(actor[3].getLabel()))
	 							thisEncounter.setIsAllowedToBeAntagonist(!Boolean.valueOf(thisNode.getTextContent()),3);
	 						else if (thisNode.getNodeName().equals(actor[4].getLabel()))
	 							thisEncounter.setIsAllowedToBeAntagonist(!Boolean.valueOf(thisNode.getTextContent()),4);
	 						else if (thisNode.getNodeName().equals(actor[5].getLabel()))
	 							thisEncounter.setIsAllowedToBeAntagonist(!Boolean.valueOf(thisNode.getTextContent()),5);
	 					}
	 				}
	 				else if ((childNodes.item(j).getNodeName()).equals("ExcludeProtagonist")) {
	 					NodeList excludeProtagonistChildNodes = childNodes.item(j).getChildNodes();
	 					for (int k=0; (k<excludeProtagonistChildNodes.getLength()); ++k) {
	 						Node thisNode = excludeProtagonistChildNodes.item(k);
	 						if (thisNode.getNodeName().equals(actor[0].getLabel()))
	 							thisEncounter.setIsAllowedToBeProtagonist(!Boolean.valueOf(thisNode.getTextContent()),0);
	 						else if (thisNode.getNodeName().equals(actor[1].getLabel()))
	 							thisEncounter.setIsAllowedToBeProtagonist(!Boolean.valueOf(thisNode.getTextContent()),1);
	 						else if (thisNode.getNodeName().equals(actor[2].getLabel()))
	 							thisEncounter.setIsAllowedToBeProtagonist(!Boolean.valueOf(thisNode.getTextContent()),2);
	 						else if (thisNode.getNodeName().equals(actor[3].getLabel()))
	 							thisEncounter.setIsAllowedToBeProtagonist(!Boolean.valueOf(thisNode.getTextContent()),3);
	 						else if (thisNode.getNodeName().equals(actor[4].getLabel()))
	 							thisEncounter.setIsAllowedToBeProtagonist(!Boolean.valueOf(thisNode.getTextContent()),4);
	 						else if (thisNode.getNodeName().equals(actor[5].getLabel()))
	 							thisEncounter.setIsAllowedToBeProtagonist(!Boolean.valueOf(thisNode.getTextContent()),5);
	 					}
	 				}
	 				else if ((childNodes.item(j).getNodeName()).equals("DayWindow")) {
	 					NodeList dayLimitNodes = childNodes.item(j).getChildNodes();
	 					for (int k=0; (k < dayLimitNodes.getLength()); ++k) {
	 						if (dayLimitNodes.item(k).getNodeName().equals("Minimum")) 
	 							thisEncounter.setFirstDay(Integer.parseInt(dayLimitNodes.item(k).getTextContent()));
	 						else if (dayLimitNodes.item(k).getNodeName().equals("Maximum")) 
	 							thisEncounter.setLastDay(Integer.parseInt(dayLimitNodes.item(k).getTextContent()));
	 					}
	 				}
	 				else if ((childNodes.item(j).getNodeName()).equals("Option")) {
	 					NodeList optionChildNodes = childNodes.item(j).getChildNodes();
	 					thisEncounter.setOption(thisEncounter.getNewOption(), iOption);
	// 					theOption = thisEncounter.options[iOption];
	 					int iReaction = 0;
	 					for (int k=0; (k < optionChildNodes.getLength()); ++k) {
	 						String temp = optionChildNodes.item(k).getTextContent();
	 						String nodeName = optionChildNodes.item(k).getNodeName();
	 						if (nodeName.equals("OptionText")) {
	 							thisEncounter.getOption(iOption).setText(temp);
	 						}
	 						else if (nodeName.equals("DeltaPBad_Good"))
	 							thisEncounter.getOption(iOption).setDeltaPBad_Good(Float.parseFloat(temp));
	 						else if (nodeName.equals("DeltaPFalse_Honest"))
	 							thisEncounter.getOption(iOption).setDeltaPFalse_Honest(Float.parseFloat(temp));
	 						else if (nodeName.equals("DeltaPTimid_Dominant"))
	 							thisEncounter.getOption(iOption).setDeltaPTimid_Dominant(Float.parseFloat(temp));
	
	 						else if (nodeName.equals("Reaction")) {
	 		 					NodeList reactionChildNodes = optionChildNodes.item(k).getChildNodes();
	 		 					thisEncounter.getOption(iOption).setReaction(iReaction, thisEncounter.getOption(iOption).getNewReaction());
	 							Reaction thisReaction = thisEncounter.getOption(iOption).getReaction(iReaction++);
	 							for (int m=0; (m < reactionChildNodes.getLength()); ++m) {
	 		 						if (reactionChildNodes.item(m).getNodeName().equals("DesirableFormula")) {
	 		 		 					NodeList desirableChildNodes = reactionChildNodes.item(m).getChildNodes();
	 		 							for (int n=0; (n < desirableChildNodes.getLength()); ++n) {
	 	 		 		 					String content = desirableChildNodes.item(n).getTextContent();
	 		 		 						if (desirableChildNodes.item(n).getNodeName().equals("FirstTrait"))
	 		 		 						thisReaction.setFirstTrait(content);
	 		 		 						if (desirableChildNodes.item(n).getNodeName().equals("SecondTrait"))
	 		 		 						thisReaction.setSecondTrait(content);
	 		 		 						if (desirableChildNodes.item(n).getNodeName().equals("Bias"))
	 		 		 						thisReaction.setBias(Float.parseFloat(content));
	 		 							}
	 		 						} 		 							
	 		 						else if (reactionChildNodes.item(m).getNodeName().equals("ReactionText")) {
	 		 							thisReaction.setText(reactionChildNodes.item(m).getTextContent()); 
	 		 						}
	 							}
	 						}
	 					}
	 					++iOption;
	 				}
		 		}
			}
		}
		// Now we run through the entire set looking for misspellings of titles
		// If so, method "findEncounter" contains the error message
		for (int i=0; (i<encounters.size()); ++i) {
			Encounter theEncounter = encounters.get(i);
			
			ArrayList<String> prereq = theEncounter.getPrerequisites();
			// Check that all prerequisites are spelled correctly
			for (int j=0; (j<prereq.size()); ++j) {
				prereq.get(j).trim();
				findEncounter(prereq.get(j), editorFrame);
			}
			
			ArrayList<String> disqual = theEncounter.getDisqualifiers();
			//Check that all disqualifiers are spelled correctly
			for (int j=0; (j<disqual.size()); ++j) {
				disqual.get(j).trim();
				findEncounter(disqual.get(j), editorFrame);
			}
			
			// Check that this Encounter title is not duplicated
			for (int j=i+1; (j<getEncounterSize()); ++j) {
				if (theEncounter.getTitle().equals(encounters.get(j).getTitle())) {
					JOptionPane.showMessageDialog(editorFrame,
						    "I found two encounters with this title: "+theEncounter.getTitle(),
						    "Fatal error",
						    JOptionPane.ERROR_MESSAGE);	
					System.exit(0);

				}
			}
		}
	}	
// **********************************************************************
	private Encounter findEncounter(String title, JFrame editorFrame) {
		int i=0;
		if ((title.length() == 0) | (title.equals(" "))) return null;
		while ((i<getEncounterSize()) && !getEncounter(i).getTitle().equals(title)) { ++i; }
		if (i>=getEncounterSize()) {
			JOptionPane.showMessageDialog(editorFrame,
				    "I could not find any Encounter with the title "+title,
				    "Fatal error",
				    JOptionPane.ERROR_MESSAGE);	
			System.exit(0);
		}
		return(getEncounter(i));
	}
	//**********************************************************************
	public Encounter getNewEncounter() { return new Encounter(); }
	//**********************************************************************
	
	public class Encounter {
		private String title;
		private String introText;
		private String author;
		private ArrayList<String> prerequisites;
		private ArrayList<String> disqualifiers;
		private Option[] options;
		private boolean[] isAllowedToBeAntagonist;
		private boolean[] isAllowedToBeProtagonist;
		private int firstDay = 1;
		private int lastDay = 20;
		private boolean hasBeenUsed;
	//-----------------------------------------------------------------------
		Encounter() {
			title = "new Encounter";
			introText = "Introductory Text";
			author = "nobody";
			hasBeenUsed = false;
			prerequisites = new ArrayList<String>();
			disqualifiers = new ArrayList<String>();
			isAllowedToBeAntagonist = new boolean[cActors];
			isAllowedToBeProtagonist = new boolean[cActors];
			options = new Option[cOptions];
			for (int i=0; (i<cOptions); ++i) {
				options[i] = new Option();
			}
			
			for (int i=0; (i<cActors); ++i) {
				isAllowedToBeAntagonist[i] = true; 
				isAllowedToBeProtagonist[i] = true;
			}
		}		
	//-----------------------------------------------------------------------
	public Option getNewOption() { return new Option(); }
	//-----------------------------------------------------------------------
	public String getTitle() { return title; }
	//-----------------------------------------------------------------------
	public void setTitle(String newTitle) { title = newTitle; }
	//-----------------------------------------------------------------------
	public String getIntroText() { return introText; }
	//-----------------------------------------------------------------------
	public void setIntroText(String newText) { introText = newText; }
	//-----------------------------------------------------------------------
	public String getAuthor() { return author; }
	//-----------------------------------------------------------------------
	public void setAuthor(String newAuthor) { author = newAuthor; }
	//-----------------------------------------------------------------------
	public ArrayList<String> getPrerequisites() { return prerequisites; }
	//-----------------------------------------------------------------------
	public void setPrerequisites(ArrayList<String> newPrerequisites) { prerequisites = newPrerequisites; }
	//-----------------------------------------------------------------------
	public ArrayList<String> getDisqualifiers() { return disqualifiers; }
	//-----------------------------------------------------------------------
	public void setDisqualifiers(ArrayList<String> newDisqualifiers) { disqualifiers = newDisqualifiers; }
	//-----------------------------------------------------------------------
	public Option getOption(int index) { return options[index]; }
	//-----------------------------------------------------------------------
	public void setOption(Option newOption, int index) { options[index] = newOption; }
	//-----------------------------------------------------------------------
	public boolean getIsAllowedToBeAntagonist(int index) { return isAllowedToBeAntagonist[index]; }
	//-----------------------------------------------------------------------
	public void setIsAllowedToBeAntagonist(boolean newValue, int index) { isAllowedToBeAntagonist[index] = newValue; }
	//-----------------------------------------------------------------------
	public boolean getIsAllowedToBeProtagonist(int index) { return isAllowedToBeProtagonist[index]; }
	//-----------------------------------------------------------------------
	public void setIsAllowedToBeProtagonist(boolean newValue, int index) { isAllowedToBeProtagonist[index] = newValue; }
	//-----------------------------------------------------------------------
	public int getFirstDay() { return firstDay; }
	//-----------------------------------------------------------------------
	public void setFirstDay(int newValue) { firstDay = newValue; }
	//-----------------------------------------------------------------------
	public int getLastDay() { return lastDay; }
	//-----------------------------------------------------------------------
	public void setLastDay(int newValue) { lastDay = newValue; }
	//-----------------------------------------------------------------------
	public boolean getHasBeenUsed() { return hasBeenUsed; }
	//-----------------------------------------------------------------------
	public void setHasBeenUsed(boolean newValue) { hasBeenUsed = newValue; }
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	}
	//**********************************************************************
	public class Option {
		private String text;
//			private String trait1PBad_Good, trait2PBad_Good;
//			private String trait1PFalse_Honest, trait2PFalse_Honest;
//			private String trait1PTimid_Dominant, trait2PTimid_Dominant;
		private float deltaPBad_Good, deltaPFalse_Honest, deltaPTimid_Dominant;
		private Reaction[] reactions;
	//-----------------------------------------------------------------------
		Option() {
			text = "Unused option";
			deltaPBad_Good = 0.0f;
			deltaPFalse_Honest = 0.0f;
			deltaPTimid_Dominant = 0.0f;
//				trait1PBad_Good = "Bad_Good";
//				trait2PBad_Good = "Bad_Good";
//				trait1PFalse_Honest = "Bad_Good";
//				trait2PFalse_Honest = "Bad_Good";
//				trait1PTimid_Dominant = "Bad_Good";
//				trait2PTimid_Dominant = "Bad_Good";
			reactions = new Reaction[cReactions];
			for (int i=0; (i<cReactions); ++i) {
				reactions[i] = new Reaction();
			}
		}
	//-----------------------------------------------------------------------
		public Reaction getNewReaction() { return new Reaction(); }
	//-----------------------------------------------------------------------
		public String getText() { return text; }
	//-----------------------------------------------------------------------
		public void setText(String newText) { text = newText; }
	//-----------------------------------------------------------------------
		public Reaction getReaction(int index) { return reactions[index]; }
	//-----------------------------------------------------------------------
		public void setReaction (int index, Reaction newReaction) { reactions[index] = newReaction; }
	//-----------------------------------------------------------------------
		public float getDeltaPBad_Good() { return deltaPBad_Good; }
	//-----------------------------------------------------------------------
		public void setDeltaPBad_Good(float newValue) { deltaPBad_Good = newValue; }
	//-----------------------------------------------------------------------
		public float getDeltaPFalse_Honest() { return deltaPFalse_Honest; }
	//-----------------------------------------------------------------------
		public void setDeltaPFalse_Honest(float newValue) { deltaPFalse_Honest = newValue; }
	//-----------------------------------------------------------------------
		public float getDeltaPTimid_Dominant() { return deltaPTimid_Dominant; }
	//-----------------------------------------------------------------------
		public void setDeltaPTimid_Dominant(float newValue) { deltaPTimid_Dominant = newValue; }
	//-----------------------------------------------------------------------
	}
//**********************************************************************
	public class Reaction {
		private String firstTrait;
		private String secondTrait;
		private float bias;
		private String text;
	//-----------------------------------------------------------------------
		Reaction() {
			firstTrait = "Bad_Good";
			secondTrait = "False_Honest";
			bias = 0.0f;
			text = "unused Reaction";
		}
	//-----------------------------------------------------------------------
			public String getFirstTrait() { return firstTrait; }
	//-----------------------------------------------------------------------
			public void setFirstTrait(String newValue) { firstTrait = newValue; }
	//-----------------------------------------------------------------------
			public String getSecondTrait() { return secondTrait; }
	//-----------------------------------------------------------------------
			public void setSecondTrait(String newValue) { secondTrait = newValue; }
	//-----------------------------------------------------------------------
			public float getBias() { return bias; }
	//-----------------------------------------------------------------------
			public void setBias(float newValue) { bias = newValue; }
	//-----------------------------------------------------------------------
			public String getText() { return text; }
	//-----------------------------------------------------------------------
			public void setText(String newValue) { text = newValue; }
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	}
// **********************************************************************
	public class Actor {
		String label;
		float accord[];
		float trait[];
		float pTrait[][];
		float cTrait[][][];
		float knows[];
		boolean isMale;	
		float vain;
	//-----------------------------------------------------------------------
		private Actor(String tLabel) {
			label = tLabel;
			accord = new float[3];
			trait = new float[3];
			pTrait = new float[cActors][3];
			cTrait = new float[cActors][cActors][3];
			isMale = true;
			vain = 0.0f;
			knows = new float[cActors];
			
			for (int i= 0; (i<cActors); ++i) { knows[i] = 0.0f; }
			for (int j=0; (j<3); ++j) {
				accord[j] = 0.0f;
				trait[j] = 0.0f;
				for (int i=0; (i<cActors); ++i) {
					pTrait[i][j] = 0.0f;
					for (int k=0; (k<cActors); ++k) {
						cTrait[i][k][j] = 0.0f;
					}
				}
			}
		}
	//-----------------------------------------------------------------------
		public String getLabel() { return label; }
		//-----------------------------------------------------------------------
		public float getAccord(int iAccord) { return accord[iAccord]; }
	//-----------------------------------------------------------------------
		public void setAccord(int iAccord, float newValue) { accord[iAccord] = newValue; }
		//-----------------------------------------------------------------------
		public float getTrait(int iTrait) { return trait[iTrait]; }
	//-----------------------------------------------------------------------
		private void setTrait(int iTrait, float newValue) { trait[iTrait] = newValue; }
		//-----------------------------------------------------------------------
		public float getPTrait(int iActor, int iTrait) { return pTrait[iActor][iTrait]; }
	//-----------------------------------------------------------------------
		private void setPTrait(int iActor, int iTrait, float newValue) { pTrait[iActor][iTrait] = newValue; }
		//-----------------------------------------------------------------------
		public float getCTrait(int iActor, int jActor, int iTrait) { return cTrait[iActor][jActor][iTrait]; }
	//-----------------------------------------------------------------------
		private void setCTrait(int iActor, int jActor, int iTrait, float newValue) { cTrait[iActor][jActor][iTrait] = newValue; }
	//-----------------------------------------------------------------------
		public boolean getIsMale() {return isMale; }
	//-----------------------------------------------------------------------
		public void setIsMale(boolean newValue) { isMale = newValue; }
	//-----------------------------------------------------------------------
		public float getVain() {return vain; }
	//-----------------------------------------------------------------------
		public void setVain(float newValue) { vain = newValue; }
	//-----------------------------------------------------------------------
		public float getKnows(int iActor) { return knows[iActor]; }
	//-----------------------------------------------------------------------
		public void setKnows(int iActor, float newValue) { knows[iActor] = newValue; }
	//-----------------------------------------------------------------------
		
	}
//**********************************************************************
	public float blend(float x1, float x2, float z) {
		float bWeightingFactor = z;
		if (bWeightingFactor <= -1.00f) {
			bWeightingFactor = -1.00f;
		}
		if (bWeightingFactor >= 1.00f) {
			bWeightingFactor = 1.00f;
		}
		// this is a conversion from BNumber to UNumber
		float uWeightingFactor = (1.0f+bWeightingFactor)/2.0f;
		
		return(x2*uWeightingFactor + x1*(1.0f-uWeightingFactor));
	}

}
