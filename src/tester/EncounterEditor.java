package tester;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EncounterEditor implements ComponentListener, ActionListener {
	public static final int WindowWidth = 1200;
	public static final int WindowHeight = 1000;
	
	JLabel p2Label = new JLabel();
	JPanel optionsPanel, rightPanel, subOptionsPanel;
	Font labelFont;
	JTextArea introductoryText=new JTextArea(20,10);
	JTextArea[] optionsTextArea = new JTextArea[EncounterEngine.cOptions];
	JTextArea[] reactionsTextArea = new JTextArea[EncounterEngine.cReactions];
	JTextArea nothingBurgerArea;
	JFrame myFrame;
	JCheckBox[] antagonistCheckBox;
	JRadioButton[] antagonistRadio, protagonistRadio;
	ButtonGroup antagonistGroup;
	JButton saveButton, authorButton, testButton;
	JDialog testDialog;
	JFrame encounterSelector;
	JScrollPane eSelectorScrollPane;
	JPanel eSelectorPanel;
	JList<String> eSelectorJList;

	JScrollPane textScrollPane;
	JSpinner firstDaySpinner, lastDaySpinner;
	
	EncounterEngine theEngine;
	static boolean quitFlag, userInput;
	int iEncounter, iOption, iReaction;
	int thisDay;
	EncounterEngine.Encounter theEncounter;
	EncounterEngine.Option theOption;
	EncounterEngine.Reaction theReaction;
	FormatStuff commonFormat = new FormatStuff();	
	Color encounterColor, optionsColor, reactionColor, hilightColor;
	ArrayList<String> encounterTitles = new ArrayList<String>();
	String[] eTitlesArray;
	SillyPanel[] reactionPanel = new SillyPanel[EncounterEngine.cReactions];
	DeltaPanel deltaPBad_Good;
	DeltaPanel deltaPFalse_Honest;
	DeltaPanel deltaPTimid_Dominant;
	String fileName, theAbsolutePath;
	int theAntagonist;
	String addButtonType;
	int iDot, iMark;
	JTextArea selectedTextArea;
	float versionNumber;
	
	Doodad encounterDoodad;
	OtherDoodad prerequisitesDoodad, disqualifiersDoodad;
	
// **********************************************************************
	public EncounterEditor() {
		quitFlag = false;
		}
// **********************************************************************
   ActionListener menuGuy = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      }
    };
	CaretListener caretGuy = new CaretListener()  {
		public void caretUpdate(CaretEvent e) {
			if (userInput) {
				iDot = e.getDot();
				iMark = e.getMark();
				if (iMark<iDot) {
					int saveInt = iMark;
					iMark = iDot;
					iDot = saveInt;
				}
				selectedTextArea = (JTextArea)e.getSource();
			}
		}                                                                       
	};
	KeyListener keyGuy = new KeyListener() {
		public void keyPressed(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {
		}
	};
// -----------------------------------------------------------------------
	public void initialize() {
		myFrame = new JFrame();
		versionNumber = 2.1f;
		myFrame.setTitle("Encounter Editor Version 3.11");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setSize(WindowWidth, WindowHeight);
		myFrame.setBackground(Color.white);
		myFrame.setVisible(true);
//		labelFont = new Font("Arial", Font.BOLD, 18);
		encounterColor = new Color(255,232,229);
		optionsColor = new Color(239,247,232);
		reactionColor = new Color(220,246,255);
		hilightColor = new Color(255, 180, 255);
		theEngine = new EncounterEngine(myFrame);
		
		iEncounter = 1;
		iOption = 0;
		iReaction = 0;
		thisDay = 1;
		userInput = true;
		fileName = "";
		selectedTextArea = null;
		iDot = -1;
		iMark = -1;
		
		antagonistCheckBox = new JCheckBox[EncounterEngine.cActors -1];
		
		antagonistRadio = new JRadioButton[EncounterEngine.cActors];
		protagonistRadio = new JRadioButton[EncounterEngine.cActors];
		
		testDialog = new JDialog(myFrame, "antagonist?");
		testDialog.getContentPane().setLayout(new FlowLayout());
		testDialog.setVisible(false);
		testDialog.setLocation(400,400);
		testDialog.setSize(200,200);
		testDialog.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {} 
			public void windowClosed(WindowEvent e) {} 
			public void windowClosing(WindowEvent e) {
				for (int i = 0; (i<EncounterEngine.cReactions); ++i) {
					reactionsTextArea[i].setBackground(Color.white);
				}
			} 
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {} 
			public void windowIconified(WindowEvent e) {} 
			public void windowOpened(WindowEvent e) {
				theAntagonist = -1;
			} 
		});
		JPanel antagonistPanel = new JPanel();
		antagonistPanel.setSize(150,200);
		antagonistPanel.setLayout(new BoxLayout(antagonistPanel, BoxLayout.Y_AXIS));
		antagonistPanel.add(new JLabel("Antagonist"));
		
		antagonistGroup = new ButtonGroup();

		for (int i=1; (i<EncounterEngine.cActors); ++i) {
			antagonistRadio[i] = new JRadioButton(theEngine.getActorLabel(i));	
			antagonistGroup.add(antagonistRadio[i]);
			antagonistRadio[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String temp = e.getActionCommand();
					int selectedIndex = -1;
					for (int i=0; (i<EncounterEngine.cActors); ++i) {
						if (temp.equals(theEngine.getActorLabel(i)))
							selectedIndex = i;
					}
					theAntagonist = selectedIndex;
					runTest();
				}
			});
			antagonistPanel.add(antagonistRadio[i]);			
		}
		testDialog.add(antagonistPanel);

		for (int i=1; (i<EncounterEngine.cActors); ++i) {
			antagonistCheckBox[i-1] = new JCheckBox(theEngine.getActorLabel(i));			
			antagonistCheckBox[i-1].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String temp = e.getActionCommand();
					int selectedIndex = -1;
					for (int i=1; (i<EncounterEngine.cActors); ++i) {
						if (temp.equals(theEngine.getActorLabel(i)))
							selectedIndex = i-1;
					}
					theEncounter.setIsAllowedToBeAntagonist(!antagonistCheckBox[selectedIndex].isSelected(), selectedIndex+1);
				}
			});			
		}

		JPanel outermostPanel = new JPanel();
		outermostPanel.setLayout(new BoxLayout(outermostPanel, BoxLayout.X_AXIS));
		outermostPanel.setVisible(true);
		outermostPanel.setBackground(Color.white);
		
		authorButton = new JButton("Author");
		authorButton.setActionCommand("author");
		authorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("author")) {
	      		  String newAuthor = (String)JOptionPane.showInputDialog(myFrame, (Object)"Author: "+theEncounter.getAuthor());
	      		  if (newAuthor!=null) {
	      			  if (newAuthor.length()>19) 
	      				  newAuthor = newAuthor.substring(0,18);
	      			  theEncounter.setAuthor(newAuthor);
	      			  authorButton.setText("by "+newAuthor);
	      		  }
				}
			}
		});
		
		saveButton = new JButton("Save");
		saveButton.setMnemonic(KeyEvent.VK_S);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("save")) {
					JFileChooser chooser = new JFileChooser(theAbsolutePath);
					File bFile = null;
					 int returnVal = chooser.showSaveDialog(myFrame);
					 if (returnVal == JFileChooser.APPROVE_OPTION) {
						 String temp2 = chooser.getSelectedFile().getPath();
						 if (!temp2.endsWith(".xml"))
							 temp2+=".xml";
						 takeDownOldEncounter();
					    bFile = new File(temp2);
					    saveEncounters(bFile.getAbsolutePath());
					 }
				}
			}
		});

		testButton = new JButton("Test");
		testButton.setActionCommand("test");
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("test")) {
					testDialog.setVisible(true);
					for (int i=1; (i<6); ++i) {
						antagonistRadio[i].setEnabled(theEncounter.getIsAllowedToBeAntagonist(i));
					}
				}
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setVisible(true);
		buttonPanel.setBackground(Color.white);
		buttonPanel.setMaximumSize(new Dimension(240, 25));

		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(saveButton);
		buttonPanel.add(Box.createHorizontalStrut(30));
		buttonPanel.add(testButton);
		buttonPanel.add(Box.createHorizontalGlue());
		
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setVisible(true);
		leftPanel.setBackground(Color.white);

		JPanel authorPanel = new JPanel();
		authorPanel.setBackground(Color.white);
		authorPanel.setMaximumSize(new Dimension(400,30));
		authorPanel.add(authorButton);
		leftPanel.add(authorPanel);		
		leftPanel.add(Box.createVerticalStrut(5));
		leftPanel.add(buttonPanel);		
		leftPanel.add(Box.createVerticalStrut(5));
				
		encounterDoodad = new Doodad("Encounters", encounterTitles, encounterColor, 10, myFrame);
		leftPanel.add(encounterDoodad.getMainPanel());
		outermostPanel.add(leftPanel);
		
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setVisible(true);
		optionsPanel.setBackground(optionsColor);
		optionsPanel.setMaximumSize(new Dimension(500,1000));

		introductoryText.setFont(new Font("Times", Font.PLAIN, 16));
		introductoryText.setMargin(new Insets(10,10,10,10));
		introductoryText.setEditable(true);
		introductoryText.setLineWrap(true);
		introductoryText.setWrapStyleWord(true);
		introductoryText.setTabSize(4);
				
		introductoryText.addCaretListener(caretGuy);
		introductoryText.addKeyListener(keyGuy);
		
		textScrollPane = new JScrollPane(introductoryText);
		textScrollPane.setWheelScrollingEnabled(true);
		
		optionsPanel.add(textScrollPane);
		ArrayList<String> tempList = new ArrayList<String>();
		prerequisitesDoodad = new OtherDoodad("Prerequisites", tempList, encounterColor, 10, myFrame);
		disqualifiersDoodad = new OtherDoodad("Disqualifiers", tempList, encounterColor, 10, myFrame);
		JPanel predisPanel = new JPanel();
		predisPanel.setLayout(new BoxLayout(predisPanel, BoxLayout.X_AXIS));
		predisPanel.add(prerequisitesDoodad.getMainPanel());
		predisPanel.add(disqualifiersDoodad.getMainPanel());
		optionsPanel.add(predisPanel);
		
		// next in the stack: the actor exclusion checkbox lists
		JPanel outerExclusionPanel = new JPanel();
		outerExclusionPanel.setLayout(new BoxLayout(outerExclusionPanel, BoxLayout.Y_AXIS));
		outerExclusionPanel.setBackground(encounterColor);
		
		JPanel exclusionPanel = new JPanel();
		JPanel rightExclusionPanel = new JPanel();

		rightExclusionPanel.setLayout(new BoxLayout(rightExclusionPanel, BoxLayout.Y_AXIS));

		exclusionPanel.setBackground(encounterColor);
		rightExclusionPanel.setBackground(encounterColor);
		
		for (int i=0; (i<EncounterEngine.cActors-1); ++i) {
			antagonistCheckBox[i].setBackground(encounterColor);
			rightExclusionPanel.add(antagonistCheckBox[i]);
		}
		exclusionPanel.add(rightExclusionPanel);
		JLabel exclusionsTitle = new JLabel("Exclude as antagonist");
		exclusionsTitle.setBackground(encounterColor);
		exclusionsTitle.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		exclusionsTitle.setFont(exclusionsTitle.getFont().deriveFont(14.0f));
		outerExclusionPanel.add(Box.createVerticalStrut(5));
		outerExclusionPanel.add(exclusionsTitle);
		outerExclusionPanel.add(exclusionPanel);
		optionsPanel.add(outerExclusionPanel);
		
		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.Y_AXIS));
		spinnerPanel.setBackground(encounterColor);
		SpinnerNumberModel firstDayModel = new SpinnerNumberModel();
		firstDayModel.setMaximum(20);
		firstDayModel.setMinimum(1);
		firstDayModel.setValue(1);
		firstDaySpinner = new JSpinner(firstDayModel);
		firstDaySpinner.setBackground(encounterColor);
		firstDaySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
					theEncounter.setFirstDay(((SpinnerNumberModel)(firstDaySpinner.getModel())).getNumber().intValue());
			}
		});
		
		SpinnerNumberModel lastDayModel = new SpinnerNumberModel();
		lastDayModel.setMaximum(20);
		lastDayModel.setMinimum(1);
		lastDayModel.setValue(1);
		lastDaySpinner = new JSpinner(lastDayModel);
		lastDaySpinner.setBackground(encounterColor);
		lastDaySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				theEncounter.setLastDay(((SpinnerNumberModel)(lastDaySpinner.getModel())).getNumber().intValue());
			}
		});
		JPanel spinnerLabelPanel = new JPanel();
		JPanel spinnerSpinnerPanel = new JPanel();

		spinnerLabelPanel.setBackground(encounterColor);
		spinnerSpinnerPanel.setBackground(encounterColor);

		spinnerLabelPanel.setLayout(new BoxLayout(spinnerLabelPanel, BoxLayout.X_AXIS));
		spinnerLabelPanel.add(Box.createHorizontalStrut(100));
		spinnerLabelPanel.add(new JLabel("Earliest Turn"));
		spinnerLabelPanel.add(Box.createHorizontalStrut(100));
		spinnerLabelPanel.add(new JLabel("Latest Turn"));
		spinnerLabelPanel.add(Box.createHorizontalStrut(100));

		spinnerSpinnerPanel.setLayout(new BoxLayout(spinnerSpinnerPanel, BoxLayout.X_AXIS));
		spinnerSpinnerPanel.add(Box.createHorizontalStrut(120));
		spinnerSpinnerPanel.add(firstDaySpinner);
		spinnerSpinnerPanel.add(Box.createHorizontalStrut(80));
		spinnerSpinnerPanel.add(lastDaySpinner);
		spinnerSpinnerPanel.add(Box.createHorizontalStrut(120));		
		
		spinnerPanel.add(spinnerLabelPanel);		
		spinnerPanel.add(spinnerSpinnerPanel);		
		optionsPanel.add(spinnerPanel);
		
		
		// now build the options list, a pile of five editable text areas.		
		JPanel middleOptionsPanel = new JPanel();
		middleOptionsPanel.setBackground(optionsColor);
		optionsPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		middleOptionsPanel.setLayout(new BoxLayout(middleOptionsPanel, BoxLayout.X_AXIS));
		optionsPanel.add(Box.createHorizontalStrut(40));
		subOptionsPanel = new JPanel();
		subOptionsPanel.setBackground(optionsColor);
		subOptionsPanel.setLayout(new BoxLayout(subOptionsPanel, BoxLayout.Y_AXIS));
		
		TitleBar optionsTitleBar = new TitleBar("Options", 140, optionsColor, myFrame);
		subOptionsPanel.add(optionsTitleBar.titlePanel);

		for (int i=0; (i<EncounterEngine.cOptions); ++i) {
			optionsTextArea[i] = new JTextArea("");
			optionsTextArea[i].setMargin(new Insets(8,8,8,8));
			optionsTextArea[i].setEditable(true);
			optionsTextArea[i].setLineWrap(true);
			optionsTextArea[i].setWrapStyleWord(true);
			optionsTextArea[i].setBackground(optionsColor);
			optionsTextArea[i].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), new EmptyBorder(new Insets(5,5,5,5))));
			optionsTextArea[i].addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
				}
				public void mousePressed(MouseEvent e) {
					int clickX = e.getXOnScreen();
					int clickY = e.getYOnScreen();
					for (int j=0; (j<EncounterEngine.cOptions); ++j) {
						int left = optionsTextArea[j].getLocationOnScreen().x;
						int top = optionsTextArea[j].getLocationOnScreen().y;
						int right = left + (int)optionsTextArea[j].getSize().getWidth();
						int bottom = top + (int)optionsTextArea[j].getSize().getHeight();
						optionsTextArea[j].setBackground(optionsColor);
						optionsTextArea[j].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), new EmptyBorder(new Insets(5,5,5,5))));
						// I'm doing this test by hand because the damned "Component.contains()" method doesn't work
						if ((clickX>left) & (clickX<right) & (clickY>top) & (clickY<bottom)) {
							optionsTextArea[j].setBackground(Color.white);
							optionsTextArea[j].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 2), new EmptyBorder(new Insets(3,3,3,3))));
							takeDownOldOption(iOption);
							iOption = j;
							theOption = theEncounter.getOption(iOption);
							setUpNewOption();
							selectedTextArea = (JTextArea)e.getSource();
						}							
					}
				}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
			optionsTextArea[i].addCaretListener(caretGuy);			
			optionsTextArea[i].addKeyListener(keyGuy);
			subOptionsPanel.add(optionsTextArea[i]);
			subOptionsPanel.add(Box.createVerticalStrut(5));
		}
		optionsTextArea[0].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 3), new EmptyBorder(new Insets(3,3,3,3))));
		
		middleOptionsPanel.add(subOptionsPanel);
		optionsPanel.add(middleOptionsPanel);

		// the empty space at the bottom of the Options panel
		nothingBurgerArea = new JTextArea();
		nothingBurgerArea.setLineWrap(true);
		nothingBurgerArea.setBackground(optionsColor);
		optionsPanel.add(nothingBurgerArea);	
		
		outermostPanel.add(optionsPanel);
		
		rightPanel = new JPanel();
		rightPanel.setBackground(reactionColor);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setVisible(true);
		rightPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		rightPanel.setMaximumSize(new Dimension(500,1000));
		
		TitleBar rightTitleBar = new TitleBar("Reactions", 140, reactionColor, myFrame);
		rightPanel.add(rightTitleBar.titlePanel);
		JPanel subReactionsPanel = new JPanel();
		subReactionsPanel.setBackground(Color.white);
		subReactionsPanel.setLayout(new BoxLayout(subReactionsPanel, BoxLayout.Y_AXIS));
		
		for (int i=0; (i<EncounterEngine.cReactions); ++i) {
			reactionPanel[i] = new SillyPanel(i, reactionColor);
			rightPanel.add(reactionPanel[i]);
			rightPanel.add(Box.createVerticalStrut(5));
		}
		
		// add the panel holding the changes in relationship
		deltaPBad_Good = new DeltaPanel("pBad_Good", 0);		
		deltaPFalse_Honest = new DeltaPanel("pFalse_Honest", 1);
		deltaPTimid_Dominant = new DeltaPanel("pTimid_Dominant", 2);

		rightPanel.add(deltaPBad_Good.mainPanel);
		rightPanel.add(deltaPFalse_Honest.mainPanel);
		rightPanel.add(deltaPTimid_Dominant.mainPanel);
		
		outermostPanel.add(rightPanel);				
		myFrame.setContentPane(outermostPanel);
		
		myFrame.addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				if (e.getWindow()!=myFrame) 
					prerequisitesDoodad.getTitleBar().closeEncounterSelector(); 
			}
			public void windowLostFocus(WindowEvent e) { }			
		});
		ActionListener getTheFile = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				encounterDoodad.theJList.setSelectedIndex(1);
				iOption = 0;
				iReaction = 0;
				setUpNewEncounter(1);

				for (int i=0; (i<theEngine.getEncounterSize()); ++i) {
					encounterTitles.add(theEngine.getEncounter(i).getTitle());
					encounterDoodad.entryList[i] = encounterTitles.get(i);
				}
      		encounterDoodad.getTheJList().setListData(encounterDoodad.entryList);
      		textScrollPane.getVerticalScrollBar().setValue(0);
      		encounterDoodad.getTheJList().setSelectedIndex(1);
			}
		};
		
		eSelectorJList = new JList<String>(eTitlesArray);			
		eSelectorJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		eSelectorJList.setLayoutOrientation(JList.VERTICAL);
		eSelectorJList.setVisibleRowCount(-1);
//		reqList.setMinimumSize(new Dimension(240, 200));
		eSelectorJList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int x = eSelectorJList.getSelectedIndex();
				if ((x>=0) & (x < theEngine.getEncounterSize()) & e.getValueIsAdjusting()) {
					String selectedEncounter = theEngine.getEncounter(x).getTitle();
					ArrayList<String> shortList;
					if (addButtonType.equals("Prerequisites"))
						shortList = theEncounter.getPrerequisites();
					else			   						
						shortList = theEncounter.getDisqualifiers();
					// make certain that this Encounter is not already on the list
					int m = 0;
					boolean gotcha = false;
					while (!gotcha & (m<shortList.size())) {
							gotcha = (selectedEncounter.equals(shortList.get(m)));
							++m;
						}
					if ((!gotcha & (e.getValueIsAdjusting()) & (x<theEngine.getEncounterSize()))) {
						shortList.add(theEngine.getEncounter(x).getTitle());
						
						// the goddamn toArray() method refuses to return a String[]
						// so I'll do it by hand. I hate Java!
						String[] temp = new String[EncounterEngine.maxPreDis];
						for (int n=0; (n<shortList.size()); ++n)
							temp[n] = shortList.get(n);
   					if (addButtonType.equals("Prerequisites"))
   						prerequisitesDoodad.getTheJList().setListData(temp);
   					else 
   						disqualifiersDoodad.getTheJList().setListData(temp);
   					
   					// shut down window if the Doodad is full
						if (shortList.size()==EncounterEngine.maxPreDis) {
	   					if (addButtonType.equals("Prerequisites"))
	   						prerequisitesDoodad.getTitleBar().getAddButton().setEnabled(false);
	   					else
	   						disqualifiersDoodad.getTitleBar().getAddButton().setEnabled(false);
						}
						if (shortList.size()>0) {
	   					if (addButtonType.equals("Prerequisites"))
	   						prerequisitesDoodad.getTitleBar().getDeleteButton().setEnabled(true);
	   					else
	   						disqualifiersDoodad.getTitleBar().getDeleteButton().setEnabled(true);
						}
					}
//					reqList.clearSelection();
//					localJFrame.removeAll();
					encounterSelector.setVisible(false);
				}
			}
		});

		encounterSelector = new JFrame();
		encounterSelector.setTitle("Choose an Encounter");
		encounterSelector.setVisible(false);
		encounterSelector.setMinimumSize(new Dimension(240, 400));
		encounterSelector.setLocation(700,50);
		eSelectorScrollPane = new JScrollPane(eSelectorJList);	   			
		eSelectorPanel = new JPanel();
		eSelectorPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		eSelectorPanel.setLayout(new BoxLayout(eSelectorPanel, BoxLayout.Y_AXIS));
		eSelectorPanel.add(eSelectorScrollPane);
		encounterSelector.getContentPane().add(eSelectorPanel);
		
		Timer openingTime = new Timer(500, getTheFile);
		openingTime.setRepeats(false);
		openingTime.start();
		myFrame.pack();
		
	}

// **********************************************************************
	public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("Author")) {
 		  String newAuthor = (String)JOptionPane.showInputDialog(myFrame, (Object)"Author: "+theEncounter.getAuthor());
 		  if (newAuthor!=null) {
 			  if (newAuthor.length()>19) 
 				  newAuthor = newAuthor.substring(0,18);
 			  theEncounter.setAuthor(newAuthor);
 			  authorButton.setText("by "+newAuthor);
 		  }
		}
		if (e.getActionCommand().equals("Test")) {
			testDialog.setVisible(true);
		}
		if (e.getActionCommand().equals("Save")) {
			JFileChooser chooser = new JFileChooser(theAbsolutePath);
			File bFile = null;
			 int returnVal = chooser.showSaveDialog(myFrame);
			 if (returnVal == JFileChooser.APPROVE_OPTION) {
				 String temp2 = chooser.getSelectedFile().getPath();
				 if (!temp2.endsWith(".xml"))
					 temp2+=".xml";
				 takeDownOldEncounter();
			    bFile = new File(temp2);
			    saveEncounters(bFile.getAbsolutePath());
			 }
		}		
	}
// **********************************************************************
	public void componentHidden(ComponentEvent e) { }
// **********************************************************************
	public void componentMoved(ComponentEvent e) { }
// **********************************************************************
	public void componentResized(ComponentEvent e) { myFrame.pack(); }
// **********************************************************************
	public void componentShown(ComponentEvent e) { }
// **********************************************************************
// **********************************************************************
	private class Doodad implements DocumentListener {
		private String[] entryList;
		private JList<String> theJList;
		JScrollPane theScrollPane;
		JPanel mainPanel;
		TitleBar baseTitleBar;		
// -----------------------------------------------------------------------
		Doodad(String pTitle, ArrayList<String> pEntryList, Color backgroundColor, int spacing, JFrame owner) {
			entryList = pEntryList.toArray(new String[EncounterEngine.maxEncounters]);
			theJList = new JList<String>(entryList);			
			theJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			theJList.setLayoutOrientation(JList.VERTICAL);
			theJList.setVisibleRowCount(-1);
			theJList.setSelectedIndex(1);
			theJList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (theJList.getSelectedIndex() < theEngine.getEncounterSize()) {
						if (e.getValueIsAdjusting() & userInput & ((e.getFirstIndex()>0) | (e.getLastIndex()>0))) {
							if (theEncounter != null) {
								takeDownOldEncounter();
							}
							iEncounter = theJList.getSelectedIndex();
							iOption = 0;
							iReaction = 0;
							setUpNewEncounter(iEncounter);
						}
					}
					else {
						theJList.setSelectedIndex(iEncounter);
					}
				}
			});

			theScrollPane = new JScrollPane(theJList);
			theScrollPane.setBackground(backgroundColor);
			
			mainPanel = new JPanel();
			mainPanel.setBackground(backgroundColor);
			mainPanel.setPreferredSize(new Dimension(240, 200));
			mainPanel.setMaximumSize(new Dimension(240, 1000));
			mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			
			baseTitleBar = new TitleBar(pTitle, spacing, backgroundColor, owner);
			mainPanel.add(baseTitleBar.titlePanel);

			mainPanel.add(theScrollPane);			
		}	
// -----------------------------------------------------------------------
		public JList<String> getTheJList() { return theJList; }
// -----------------------------------------------------------------------
		public JPanel getMainPanel() { return mainPanel; }
// -----------------------------------------------------------------------
		// now add the listener implementation
		public void changedUpdate(DocumentEvent e) {
		}

		public void insertUpdate(DocumentEvent e) {
		}

		public void removeUpdate(DocumentEvent e) {
		}
		
	}	
// **********************************************************************
	private class OtherDoodad {
		String[] entryList;
		JList<String> theJList;
		JScrollPane theScrollPane;
		JPanel mainPanel;
		TitleBar baseTitleBar;		
// -----------------------------------------------------------------------
		OtherDoodad(String pTitle, ArrayList<String> pEntryList, Color backgroundColor, int spacing, JFrame owner) {
			entryList = pEntryList.toArray(new String[EncounterEngine.maxPreDis]);
			theJList = new JList<String>(entryList);			
			theJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			theJList.setLayoutOrientation(JList.VERTICAL);
			theJList.setVisibleRowCount(-1);
			theJList.setSelectedIndex(0);
			 MouseListener mouseListener = new MouseAdapter() {
			     public void mouseClicked(MouseEvent e) {
			         if (e.isShiftDown()) {
			             System.out.println("shift is down");
			       }
			      if (e.isControlDown()) {
			          System.out.println("control is down");
			          }
			     }
			 };
			 theJList.addMouseListener(mouseListener);
			theJList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (userInput) {
						System.out.println("list item selected");
						baseTitleBar.getDeleteButton().setEnabled(true);
					}
				}
			});


			theScrollPane = new JScrollPane(theJList);
			theScrollPane.setBackground(backgroundColor);
			
			mainPanel = new JPanel();
			mainPanel.setBackground(backgroundColor);
//			mainPanel.setMinimumSize(new Dimension(240, 200));
			mainPanel.setPreferredSize(new Dimension(240, 200));
			mainPanel.setMaximumSize(new Dimension(240, 1000));
			mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			
			baseTitleBar = new TitleBar(pTitle, spacing, backgroundColor, owner);
			mainPanel.add(baseTitleBar.titlePanel);
			
			mainPanel.add(theScrollPane);			
		}	
// -----------------------------------------------------------------------
		public JList<String> getTheJList() { return theJList; }
// -----------------------------------------------------------------------
		public TitleBar getTitleBar() { return baseTitleBar; }
// -----------------------------------------------------------------------
		private JPanel getMainPanel() { return mainPanel; }
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
	}
// **********************************************************************
	private class BlenderPanel {
		JComboBox<String> leftComboBox, rightComboBox;
		JSlider centerSlider;
		JPanel mainPanel, upperPanel, middlePanel, bottomLine, sliderPanel, traitValuePanel;
		JLabel trait1, weighting, trait2;
		int myIndex;
		JLabel testResult, leftTraitValue, rightTraitValue;
	// -----------------------------------------------------------------------
		BlenderPanel(int index, Color bColor) {
			userInput=false;
			myIndex = index;
			mainPanel = new JPanel();
			upperPanel = new JPanel();
			sliderPanel = new JPanel();
			middlePanel = new JPanel();
			traitValuePanel = new JPanel();
			bottomLine = new JPanel();
			
			trait1 = new JLabel("Trait #1");
			weighting = new JLabel("0.00");
			weighting.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			trait2 = new JLabel("Trait #2");
			testResult = new JLabel();
			testResult.setFont(Font.getFont("Monaco"));
			testResult.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			leftTraitValue = new JLabel();
			leftTraitValue.setFont(Font.getFont("Monaco"));
			rightTraitValue = new JLabel();
			rightTraitValue.setFont(Font.getFont("Monaco"));

//			leftTraitValue.setBackground(Color.yellow);
//			rightTraitValue.setBackground(Color.green);
			
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			mainPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
			mainPanel.setBackground(bColor);

			upperPanel.setBackground(bColor);
			sliderPanel.setBackground(bColor);
			middlePanel.setBackground(bColor);
			traitValuePanel.setBackground(bColor);
			
			leftComboBox = new JComboBox<String>();
			leftComboBox.setBackground(Color.white);
			for (int i = 0; (i < EncounterEngine.cFactors); ++i) {
				leftComboBox.addItem(theEngine.getFactor(i));	
			}
			leftComboBox.setSelectedItem(0);
//			leftComboBox.setEditable(false);
			leftComboBox.setMaximumRowCount(12);
			leftComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (userInput & (e.getActionCommand().equals("comboBoxChanged"))) {
						theOption.getReaction(myIndex).setFirstTrait((String)leftComboBox.getSelectedItem());
						runTest();
					}
				}
			});

			centerSlider = new JSlider(-99, 99, 0);
			centerSlider.setMaximumSize(new Dimension(198,30));
			centerSlider.setBackground(bColor);
			centerSlider.setValue(0);
			centerSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {	
					if (userInput) {
						theOption.getReaction(myIndex).setBias(((float)centerSlider.getValue())/100.0f);
						String temp = String.valueOf((float)(centerSlider.getValue())/100.0f);
						if (temp.length() == 3)
							temp+="0";
						weighting.setText(temp);
						runTest();
					}
				}
			});	
						
			// The upperPanel holds the labels for the controls
			upperPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			upperPanel.add(trait1);
			upperPanel.add(Box.createHorizontalStrut(10));
			upperPanel.add(centerSlider);
			upperPanel.add(Box.createHorizontalStrut(10));
			upperPanel.add(trait2);
			upperPanel.setMaximumSize(new Dimension(500,30));
			upperPanel.setPreferredSize(new Dimension(500,20));
						
			rightComboBox = new JComboBox<String>();
			rightComboBox.setBackground(Color.white);
			for (int i = 0; (i < EncounterEngine.cFactors); ++i) {
				rightComboBox.addItem(theEngine.getFactor(i));	
			}
			rightComboBox.setSelectedItem(0);
			rightComboBox.setEditable(false);
//			rightComboBox.setPreferredSize(rightComboBox.getMinimumSize());
			rightComboBox.setMaximumRowCount(12);
			rightComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (userInput & (e.getActionCommand().equals("comboBoxChanged"))) {
						theOption.getReaction(myIndex).setSecondTrait((String)rightComboBox.getSelectedItem());
						runTest();
					}
				}
			});
			
			middlePanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			middlePanel.add(leftComboBox);
			middlePanel.add(Box.createRigidArea(new Dimension(100, 10)));
			middlePanel.add(rightComboBox);
//			middlePanel.setPreferredSize(new Dimension(500,20));
			
			traitValuePanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			traitValuePanel.add(leftTraitValue);
			traitValuePanel.add(Box.createRigidArea(new Dimension(100, 10)));
			traitValuePanel.add(rightTraitValue);

			bottomLine.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			bottomLine.setMaximumSize(new Dimension(500, 3));
			bottomLine.setPreferredSize(new Dimension(500, 3));
			bottomLine.setBackground(Color.black);
			
			mainPanel.add(testResult);
			mainPanel.add(weighting);
			mainPanel.add(upperPanel);
			mainPanel.add(middlePanel);
			mainPanel.add(traitValuePanel);
			mainPanel.add(bottomLine);
			userInput=true;
		}
// -----------------------------------------------------------------------
		public JPanel getMainPanel() { return mainPanel; }
// -----------------------------------------------------------------------
		public String getTrait1() { return (String)leftComboBox.getSelectedItem(); }
// -----------------------------------------------------------------------
		public String getTrait2() { return (String)rightComboBox.getSelectedItem(); }
// -----------------------------------------------------------------------
		public int getBias() { return centerSlider.getValue(); }
// -----------------------------------------------------------------------
		public void setTestResult(float newValue) { 
			testResult.setText("Inclination value: "+commonFormat.myFormat(newValue)); 
		}
// -----------------------------------------------------------------------
		public void setTraitValues(float leftValue, float rightValue) { 
			leftTraitValue.setText(commonFormat.myFormat(leftValue)); 
			rightTraitValue.setText(commonFormat.myFormat(rightValue)); 
		}
// -----------------------------------------------------------------------
//		public JComboBox getLeftComboBox() { return leftComboBox; }
// -----------------------------------------------------------------------
//		public JComboBox getRightComboBox() { return rightComboBox; }
// -----------------------------------------------------------------------
//		public JScrollBar getCenterScroller() {return centerScroller; }
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
		private void reviseReactions() {
//			userInput = false;
			EncounterEngine.Reaction localReaction = theOption.getReaction(myIndex);
			reactionsTextArea[myIndex].setText(localReaction.getText());
			int index = -1;
			for (int j=0; j<EncounterEngine.cFactors; ++j) {
				if (theEngine.getFactor(j).equals(localReaction.getFirstTrait())) 
					index = j;
			}
			leftComboBox.setSelectedIndex(index);
			
			index = -1;
			for (int j=0; j<EncounterEngine.cFactors; ++j) {
				if (theEngine.getFactor(j).equals(localReaction.getSecondTrait())) 
					index = j;
			}
			String temp = String.valueOf((float)(localReaction.getBias()));
			if (temp.length() == 3)
				temp+="0";
			weighting.setText(temp);
			rightComboBox.setSelectedIndex(index);
			centerSlider.setValue((int)(100.0f * localReaction.getBias()));
			
//			userInput = true;
		}
	}
// **********************************************************************
	private class SillyPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		BlenderPanel myBlenderPanel;
	// -----------------------------------------------------------------------
		SillyPanel(int index, Color bColor) {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setAlignmentY(JComponent.CENTER_ALIGNMENT);
			setBackground(bColor);
			reactionsTextArea[index] = new JTextArea("");
			reactionsTextArea[index].setMargin(new Insets(4,0,4,0));
			reactionsTextArea[index].setEditable(true);
			reactionsTextArea[index].setLineWrap(true);
			reactionsTextArea[index].setWrapStyleWord(true);
			reactionsTextArea[index].setPreferredSize(new Dimension(438, 70));
			reactionsTextArea[index].setMaximumSize(new Dimension(466, 70));
			reactionsTextArea[index].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black),new EmptyBorder(new Insets(5,5,5,5))));

			reactionsTextArea[index].addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
				}
				public void mousePressed(MouseEvent e) {
					int clickX = e.getXOnScreen();
					int clickY = e.getYOnScreen();
					if (theEncounter != null) {
						for (int j=0; (j<EncounterEngine.cReactions); ++j) {
							int left = reactionsTextArea[j].getLocationOnScreen().x;
							int top = reactionsTextArea[j].getLocationOnScreen().y;
							int right = left + (int)reactionsTextArea[j].getSize().getWidth();
							int bottom = top + (int)reactionsTextArea[j].getSize().getHeight();
							reactionsTextArea[j].setBackground(optionsColor);
							reactionsTextArea[j].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), new EmptyBorder(new Insets(5,5,5,5))));
							// I'm doing this test by hand because the damned "Component.contains()" method doesn't work
							if ((clickX>left) & (clickX<right) & (clickY>top) & (clickY<bottom)) {
								reactionsTextArea[j].setBackground(Color.white);
								reactionsTextArea[j].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 2), new EmptyBorder(new Insets(3,3,3,3))));
								iReaction = j;
							}							
						}
					}
				}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
			
			reactionsTextArea[index].addCaretListener(caretGuy);
			reactionsTextArea[index].addKeyListener(keyGuy);
			add(reactionsTextArea[index]);
			myBlenderPanel = new BlenderPanel(index, bColor);
			add(myBlenderPanel.getMainPanel());
		}
// -----------------------------------------------------------------------
		public BlenderPanel getMyBlenderPanel() {return myBlenderPanel; }
	}
// **********************************************************************
	private class TitleBar implements ActionListener {
		JButton addButton, deleteButton;
		JLabel title;
		JPanel titlePanel;
		String type;		
	// -----------------------------------------------------------------------
		TitleBar(String pTitle, int spacing, Color backgroundColor, JFrame pOwner) {
			title = new JLabel(pTitle);
			title.setBackground(backgroundColor);

			addButton = new JButton("+");
			deleteButton = new JButton("-");
			
			addButton.setBackground(Color.white);
			deleteButton.setBackground(Color.white);
			
			addButton.setActionCommand("add"+pTitle);
			deleteButton.setActionCommand("del"+pTitle);
			
			addButton.addActionListener(this);
			deleteButton.addActionListener(this);

			titlePanel = new JPanel();
			titlePanel.setPreferredSize(new Dimension(500, 40));
			titlePanel.setMaximumSize(new Dimension(500, 40));
			titlePanel.setBackground(backgroundColor);
			titlePanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
			titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
			titlePanel.add(Box.createHorizontalStrut(10));
			// more magical special case code!!!
			if (!((pTitle.equals("Options") | (pTitle.equals("Reactions"))))) {
				titlePanel.add(addButton);
			}
			titlePanel.add(Box.createHorizontalStrut(spacing));
			title.setFont(title.getFont().deriveFont(18.0f));
			titlePanel.add(title);
			titlePanel.add(Box.createHorizontalStrut(spacing));
			if (!((pTitle.equals("Options") | (pTitle.equals("Reactions"))))) {
				titlePanel.add(deleteButton);
			}
			titlePanel.add(Box.createHorizontalStrut(10));	
		}
	    public void actionPerformed(ActionEvent e) {
	   	 String prefix = e.getActionCommand().substring(0, 3);
	   	 type = e.getActionCommand().substring(3);
	   	 addButtonType = type;
	        if (prefix.equals("add")) {
	      	  if (type.equals("Encounters")) {
	      		  String newTitle = (String)JOptionPane.showInputDialog(myFrame, (Object)"Name of new Encounter:");
	      		  boolean isTitleAlreadyTaken = false;
	      		  for (int i=0; (i<theEngine.getEncounterSize()); ++i) {
	      			  if (theEngine.getEncounter(i).getTitle().equals(newTitle)) 
	      				  isTitleAlreadyTaken = true;
	      		  }
	      		  if (isTitleAlreadyTaken) {
	      			  JOptionPane.showMessageDialog(myFrame,
	      					    "You snivelling fool!",
	      					    "That title is already taken",
	      					    JOptionPane.ERROR_MESSAGE);
	      		  }
	      		  else if (newTitle!=null) {
		      		  theEncounter = theEngine.getNewEncounter();
		      		  theEncounter.setTitle(newTitle);
		      		  theEngine.addEncounter(theEncounter);
		      		  userInput = false; // this prevents the Doodad slider from activating
		      		  encounterDoodad.entryList[theEngine.getEncounterSize()-1] = newTitle;
		      		  encounterTitles.add(newTitle);
		      		  encounterDoodad.getTheJList().setListData(encounterDoodad.entryList);
	      			  encounterDoodad.getTheJList().setSelectedIndex(theEngine.getEncounterSize()-1);
	      			  setUpNewEncounter(theEngine.getEncounterSize()-1);
	      			  userInput = true;
	      		  }
	      	  }
	      	  if ((type.equals("Prerequisites")) | (type.equals("Disqualifiers"))) {
	      		  eTitlesArray = encounterTitles.toArray(new String[EncounterEngine.maxEncounters]);
	      		  eSelectorJList.setListData(eTitlesArray);
	      		  encounterSelector.setVisible(true);
	      	  }
	        }
	        if (prefix.equals("del")) {
	      	  if (type.equals("Encounters")) {
	      		  int n = theEngine.getEncounterSize();
	      		  int m = encounterDoodad.getTheJList().getSelectedIndex();
	      		  theEngine.removeEncounter(m);
	      		  for (int i=m; (i<n); ++i) {
	      			  encounterDoodad.entryList[i] = encounterDoodad.entryList[i+1];
	      		  }
	      		  encounterDoodad.theJList.setListData(encounterDoodad.entryList);
	      		  if (theEngine.getEncounterSize() == 0) {
	      			  deleteButton.setEnabled(false);
	      		  }
	      	  }
	      	  if (type.equals("Prerequisites")) {
	      		  int m = prerequisitesDoodad.getTheJList().getSelectedIndex();
	      		  if (m>=0) {
	      			  theEncounter.getPrerequisites().remove(m);
	      			  prerequisitesDoodad.getTheJList().setListData(theEncounter.getPrerequisites().toArray(new String[EncounterEngine.maxPreDis]));
	      			  prerequisitesDoodad.getTitleBar().getDeleteButton().setEnabled(false);
	      		  }
	      	  }
	      	  if (type.equals("Disqualifiers")) {
	      		  int m = disqualifiersDoodad.getTheJList().getSelectedIndex();
	      		  if (m>=0) {
		      		  theEncounter.getDisqualifiers().remove(m);
		      		  disqualifiersDoodad.getTheJList().setListData(theEncounter.getDisqualifiers().toArray(new String[EncounterEngine.maxPreDis]));
		      		  disqualifiersDoodad.getTitleBar().getDeleteButton().setEnabled(false);
	      		  }
	      	  }
	        }
	    }		
// -----------------------------------------------------------------------
	    public void closeEncounterSelector() {
				encounterSelector.dispose();
	    }
	 // -----------------------------------------------------------------------
	    public JButton getDeleteButton() { return deleteButton; }
	 // -----------------------------------------------------------------------
	    public JButton getAddButton() { return addButton; }
	}
// **********************************************************************
	private class DeltaPanel {
		JPanel mainPanel;
		JPanel titlePanel;
		JLabel valueLabel;
		private JSlider biasSlider;
		int myIndex;
//-----------------------------------------------------------------------
		DeltaPanel(String title, int pIndex) {
			myIndex = pIndex;
			mainPanel = new JPanel();
			mainPanel.setPreferredSize(new Dimension(500, 40));
			mainPanel.setBackground(optionsColor);
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

			titlePanel = new JPanel();
			titlePanel.setPreferredSize(new Dimension(500, 40));
			titlePanel.setBackground(optionsColor);
			titlePanel.setAlignmentY(JComponent.LEFT_ALIGNMENT);
			titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
			titlePanel.add(Box.createHorizontalStrut(10));
			titlePanel.add(new JLabel("Blending change in "+title));
			
			biasSlider = new JSlider(-99, 99, 0);
			biasSlider.setMaximumSize(new Dimension(198,20));
			biasSlider.setPreferredSize(new Dimension(198,20));
			biasSlider.setBackground(optionsColor);
			biasSlider.setValue(0); 
			biasSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {	
					switch (myIndex) {
						case 0: { theOption.setDeltaPBad_Good((float)biasSlider.getValue() / 100.0f); break; }
						case 1: { theOption.setDeltaPFalse_Honest((float)biasSlider.getValue() / 100.0f); break; }
						case 2: { theOption.setDeltaPTimid_Dominant((float)biasSlider.getValue() / 100.0f); break; }
					}
					valueLabel.setText(String.valueOf((float)(biasSlider.getValue())/100));
				}
			});			
			
			mainPanel.add(titlePanel);
			JPanel valuePanel = new JPanel();
			valuePanel.setPreferredSize(new Dimension(500, 50));
			valuePanel.setBackground(optionsColor);
			valuePanel.setAlignmentY(JComponent.LEFT_ALIGNMENT);
			valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.X_AXIS));
			valueLabel = new JLabel("0.0");
			valuePanel.add(valueLabel);
			valuePanel.add(Box.createHorizontalStrut(4));
			mainPanel.add(valuePanel);			
			
			JPanel biasPanel = new JPanel();
			biasPanel.setPreferredSize(new Dimension(500, 40));
			biasPanel.setBackground(optionsColor);
			biasPanel.setAlignmentY(JComponent.LEFT_ALIGNMENT);
			biasPanel.setLayout(new BoxLayout(biasPanel, BoxLayout.X_AXIS));
			biasPanel.add(Box.createHorizontalStrut(20));
			biasPanel.add(new JLabel("-1"));
			biasPanel.add(biasSlider);
			biasPanel.add(new JLabel("+1"));
			biasPanel.add(Box.createHorizontalStrut(20));
//			biasPanel.add(Box.createHorizontalGlue());
			
			mainPanel.add(biasPanel);
			mainPanel.add(Box.createVerticalStrut(20));
		}
//-----------------------------------------------------------------------
		public JSlider getBiasSlider() { return biasSlider; }
	}
// **********************************************************************
	public class FormatStuff {
	   public NumberFormat integerFormat=NumberFormat.getIntegerInstance();
	   public NumberFormat doubleFormat=NumberFormat.getInstance();
	   public NumberFormat floatFormat=NumberFormat.getInstance();
	   public NumberFormat currencyFormat=NumberFormat.getCurrencyInstance();
	   public NumberFormat percentFormat=NumberFormat.getPercentInstance();
	   public NumberFormat percentFormat2=NumberFormat.getPercentInstance();
	   public NumberFormat fractionFormat=NumberFormat.getInstance();
	   
		//-----------------------------------------------------------------------
	   public FormatStuff() {
			integerFormat.setMaximumFractionDigits(0);
			doubleFormat.setMaximumFractionDigits(3);
			floatFormat.setMaximumFractionDigits(2);
			currencyFormat.setMaximumFractionDigits(0);
			percentFormat.setMaximumFractionDigits(0);
			percentFormat2.setMaximumFractionDigits(2);
			fractionFormat.setMaximumFractionDigits(4);
	   }
		//-----------------------------------------------------------------------
		public String myFormat(String formatType, double inValue) {
			// formats a double value for the particular page
			if (formatType.equals("Integer"))
				return integerFormat.format(inValue);
			else if (formatType.equals("Double"))
				return doubleFormat.format(inValue);
			else if (formatType.equals("Currency"))
				return currencyFormat.format(inValue);
			else if (formatType.equals("Percent"))
				return percentFormat.format(inValue);
			else if (formatType.equals("Percent2"))
				return percentFormat2.format(inValue);
			else if (formatType.equals("Fraction"))
				return fractionFormat.format(inValue);
			else return "bad Format: "+formatType;
		}
		//-----------------------------------------------------------------------
		public String myFormat(float newValue) {
			String temp = commonFormat.floatFormat.format(newValue);
//			int maxLength = 4;
//			if (newValue<0) maxLength = 5;
//			while (temp.length()<maxLength) temp+="0"; 
			return temp+" ";
		}

	}
// **********************************************************************
	private void takeDownOldEncounter() {
		theEncounter.setIntroText(introductoryText.getText());
		for (int i=0; (i<EncounterEngine.cOptions); ++i) {
			EncounterEngine.Option xOption = theEncounter.getOption(i);
			xOption.setText(optionsTextArea[i].getText());
			
			if (i==iOption) {
				xOption.setDeltaPBad_Good((float)deltaPBad_Good.getBiasSlider().getValue() / 100.0f);
				xOption.setDeltaPFalse_Honest((float)deltaPFalse_Honest.getBiasSlider().getValue() / 100.0f);
				xOption.setDeltaPTimid_Dominant((float)deltaPTimid_Dominant.getBiasSlider().getValue() / 100f);
				
				for (int j=0; (j<EncounterEngine.cReactions); ++j) {
					xOption.getReaction(j).setText(reactionsTextArea[j].getText());
					xOption.getReaction(j).setFirstTrait(reactionPanel[j].getMyBlenderPanel().getTrait1());
					xOption.getReaction(j).setSecondTrait(reactionPanel[j].getMyBlenderPanel().getTrait2());
					xOption.getReaction(j).setBias(((float)reactionPanel[j].getMyBlenderPanel().getBias())/100.0f);
				}
			}
		}		
	}
// **********************************************************************
	private void setUpNewEncounter(int listIndex) {
		theEncounter = theEngine.getEncounter(listIndex);
		theOption = theEncounter.getOption(iOption);
		theReaction = theOption.getReaction(iReaction);
		
		authorButton.setText("by "+theEncounter.getAuthor());
		
		introductoryText.setText(theEncounter.getIntroText());
		introductoryText.setCaretPosition(0);
		
		userInput = false;
		prerequisitesDoodad.getTheJList().setListData(theEncounter.getPrerequisites().toArray(new String[EncounterEngine.maxPreDis]));
		disqualifiersDoodad.getTheJList().setListData(theEncounter.getDisqualifiers().toArray(new String[EncounterEngine.maxPreDis]));
		prerequisitesDoodad.getTitleBar().getDeleteButton().setEnabled(false);
		disqualifiersDoodad.getTitleBar().getDeleteButton().setEnabled(false);
		userInput = true; 

		// these buttons must not be enabled until an entry has been selected
//		prerequisitesDoodad.getTitleBar().getDeleteButton().setEnabled(theEncounter.getPrerequisites().size()>0);
//		disqualifiersDoodad.getTitleBar().getDeleteButton().setEnabled(theEncounter.getDisqualifiers().size()>0);
			
		for (int i=0; (i<EncounterEngine.cActors-1); ++i) {
			antagonistCheckBox[i].setSelected(!theEncounter.getIsAllowedToBeAntagonist(i+1));
		}

		((SpinnerNumberModel)firstDaySpinner.getModel()).setValue((Object)theEncounter.getFirstDay());
		((SpinnerNumberModel)lastDaySpinner.getModel()).setValue((Object)theEncounter.getLastDay());

		for (int i=0; (i<EncounterEngine.cOptions); ++i) {
			optionsTextArea[i].setText(theEncounter.getOption(i).getText());
			optionsTextArea[i].setBorder(BorderFactory.createLineBorder(Color.black, 1));
			optionsTextArea[i].setBackground(optionsColor);
		}			
		optionsTextArea[iOption].setBackground(Color.white);
		optionsTextArea[iOption].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black,2), new EmptyBorder(new Insets(5,5,5,5))));
		iOption = 0;
		setUpNewOption();		
	}
// **********************************************************************
	private void takeDownOldOption(int optionIndex) {
		for (int i=0; (i<EncounterEngine.cOptions); ++i) {  // overkill. Don't trust Java.
			theEncounter.getOption(i).setText(optionsTextArea[i].getText());
		}			
		
		theOption.setDeltaPBad_Good((float)(deltaPBad_Good.getBiasSlider().getValue())/100.0f);
		theOption.setDeltaPFalse_Honest((float)(deltaPFalse_Honest.getBiasSlider().getValue())/100.0f);
		theOption.setDeltaPTimid_Dominant((float)(deltaPTimid_Dominant.getBiasSlider().getValue())/100.0f);

		for (int i=0; (i<EncounterEngine.cReactions); ++i) {
			theOption.getReaction(i).setText(reactionsTextArea[i].getText());
		}				
	}
// **********************************************************************
	private void setUpNewOption() {
		deltaPBad_Good.getBiasSlider().setValue((int)(theOption.getDeltaPBad_Good() * 100.0f));
		deltaPFalse_Honest.getBiasSlider().setValue((int)(theOption.getDeltaPFalse_Honest() * 100.0f));
		deltaPTimid_Dominant.getBiasSlider().setValue((int)(theOption.getDeltaPTimid_Dominant() * 100.0f));
		iReaction = 0;
		theReaction = theOption.getReaction(0);
		userInput = false;
		for (int i=0; (i<EncounterEngine.cReactions); ++i) {
			reactionsTextArea[i].setText(theOption.getReaction(i).getText());
			reactionsTextArea[i].setBackground(reactionColor);
			reactionPanel[i].myBlenderPanel.reviseReactions();
		}		
		reactionsTextArea[0].setBackground(Color.white);
		reactionsTextArea[0].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black,2), new EmptyBorder(new Insets(5,5,5,5))));
		userInput = true;
		
	}
// **********************************************************************
	public void runTest() {
		float bestInclination;
		int bestIndex;
		float x1, x2, bias, inclination;
		
		if ((theAntagonist>=0) & testDialog.isVisible()) {
			bestInclination = -1.00f;
			bestIndex = -1;
			for (int i = 0; (i<EncounterEngine.cReactions); ++i) {
				if (!theOption.getReaction(i).getText().equals("unused Reaction")) {
					reactionsTextArea[i].setBackground(Color.white);
					x1 = theEngine.getTraitValue(theAntagonist, 0, theOption.getReaction(i).getFirstTrait());
					x2 = theEngine.getTraitValue(theAntagonist, 0, theOption.getReaction(i).getSecondTrait());					
					bias = theOption.getReaction(i).getBias();
					inclination = theEngine.blend(x1, x2, bias);
					if (inclination > bestInclination) {
						bestInclination = inclination;
						bestIndex = i;
					}
					reactionPanel[i].getMyBlenderPanel().setTraitValues(x1, x2);
					reactionPanel[i].getMyBlenderPanel().setTestResult(inclination);
				}
			}
			reactionsTextArea[bestIndex].setBackground(hilightColor);
		}						
	}
// **********************************************************************
	private void saveEncounters(String savedFileName) {
		
		takeDownOldEncounter(); // get the last changes made

		FileOutputStream outputStream = null;
		DocumentBuilder builder = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element encounterList;

		try {
			outputStream = new FileOutputStream(savedFileName);
		} catch (Exception e) { }

		try {
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = builder.newDocument();

		// write to doc here.
		encounterList = doc.createElement("ListWrapper");
		encounterList.setAttribute("version", String.valueOf(versionNumber));
		doc.appendChild(encounterList);
				
		Element actorList = doc.createElement("ActorList");
		encounterList.appendChild(actorList);
		for (int i=0; (i<EncounterEngine.cActors); ++i) {
			EncounterEngine.Actor act = theEngine.getActor(i);
			Element actorElement = doc.createElement("Actor");
			actorElement.setAttribute("label", act.getLabel());
			Element isMaleElement = doc.createElement("isMale");
			if (act.getIsMale())
				isMaleElement.setTextContent("true");
			else
				isMaleElement.setTextContent("false");
			actorElement.appendChild(isMaleElement);
			
			Element bad_GoodElement = doc.createElement("Bad_Good");
			bad_GoodElement.setTextContent(String.valueOf(act.getTrait(0)));
			actorElement.appendChild(bad_GoodElement);
			
			Element false_HonestElement = doc.createElement("False_Honest");
			false_HonestElement.setTextContent(String.valueOf(act.getTrait(1)));
			actorElement.appendChild(false_HonestElement);
			
			Element timid_DominantElement = doc.createElement("Timid_Dominant");
			timid_DominantElement.setTextContent(String.valueOf(act.getTrait(2)));
			actorElement.appendChild(timid_DominantElement);

			Element accordBad_GoodElement = doc.createElement("AccordBad_Good");
			accordBad_GoodElement.setTextContent(String.valueOf(act.getAccord(0)));
			actorElement.appendChild(accordBad_GoodElement);
			
			Element accordFalse_HonestElement = doc.createElement("AccordFalse_Honest");
			accordFalse_HonestElement.setTextContent(String.valueOf(act.getAccord(1)));
			actorElement.appendChild(accordFalse_HonestElement);
			
			Element acordTimid_DominantElement = doc.createElement("AccordTimid_Dominant");
			acordTimid_DominantElement.setTextContent(String.valueOf(act.getAccord(2)));
			actorElement.appendChild(acordTimid_DominantElement);
			
			Element vainElement = doc.createElement("Vain");
			vainElement.setTextContent(String.valueOf(act.getVain()));
			actorElement.appendChild(vainElement);

			for (int j=0; (j<EncounterEngine.cActors); ++j) {
				Element knowsElement = doc.createElement("Knows_"+theEngine.getActorLabel(j));
				knowsElement.setTextContent(String.valueOf(act.getKnows(j)));
				actorElement.appendChild(knowsElement);
				
			}

			actorList.appendChild(actorElement);
		}
		
		Element pTraitList = doc.createElement("PTraitList");
		encounterList.appendChild(pTraitList);
		for (int i=0; (i<EncounterEngine.cActors); ++i) {
			for (int j=0; (j<EncounterEngine.cActors); ++j) {
				if (i!=j) {
					for (int k=0; (k<3); ++k) {
						Element pTraitElement = doc.createElement("pTrait");
						pTraitElement.setAttribute("akind", theEngine.getFactor(k));
						pTraitElement.setAttribute("from", theEngine.getActorLabel(i));
						pTraitElement.setAttribute("to", theEngine.getActorLabel(j));
						pTraitElement.setAttribute("value", String.valueOf(theEngine.getPTrait(i, j, k)));
						pTraitList.appendChild(pTraitElement);
					}
				}
			}
		}
		
		Element cTraitList = doc.createElement("CTraitList");
		encounterList.appendChild(cTraitList);
		for (int i=0; (i<EncounterEngine.cActors); ++i) {
			for (int j=0; (j<EncounterEngine.cActors); ++j) {
				for (int k=0; (k<EncounterEngine.cActors); ++k) {
					for (int n=0; (n<3); ++n) {
						Element cTraitElement = doc.createElement("cTrait");
						cTraitElement.setAttribute("akind", theEngine.getFactor(n));
						cTraitElement.setAttribute("from", theEngine.getActorLabel(i));
						cTraitElement.setAttribute("to", theEngine.getActorLabel(j));
						cTraitElement.setAttribute("about", theEngine.getActorLabel(k));
						cTraitElement.setAttribute("value", String.valueOf(theEngine.getCTrait(i, j, k, n)));
						cTraitList.appendChild(cTraitElement);
					}
				}
			}
		}
		
		Element eList = doc.createElement("EncounterList");
		encounterList.appendChild(eList);
		for (EncounterEngine.Encounter enc:theEngine.encounters) {
			Element encounterElement = doc.createElement("Encounter");
			encounterElement.setAttribute("title", enc.getTitle());
			
			Element authorElement = doc.createElement("Author");
			authorElement.setTextContent(enc.getAuthor());
			encounterElement.appendChild(authorElement);
			
			Element prerequisitesElement = doc.createElement("Prerequisites");
			for (String st:enc.getPrerequisites()) {
				Element prereq = doc.createElement("Prereq");
				prereq.setTextContent(st);
				prerequisitesElement.appendChild(prereq);				
			}
			encounterElement.appendChild(prerequisitesElement);
			
			Element disqualifiersElement = doc.createElement("Disqualifiers");
			for (String st:enc.getDisqualifiers()) {
				Element disqual = doc.createElement("Disqual");
				disqual.setTextContent(st);
				disqualifiersElement.appendChild(disqual);				
			}
			encounterElement.appendChild(disqualifiersElement);
			
			Element excludeProtagonistElement = doc.createElement("ExcludeProtagonist");
			for (int i=0; (i<EncounterEngine.cActors); ++i) {
				Element actorElement = doc.createElement(theEngine.getActorLabel(i));
				if (enc.getIsAllowedToBeProtagonist(i)) 
					actorElement.setTextContent("false");
				else
					actorElement.setTextContent("true");
				excludeProtagonistElement.appendChild(actorElement);
			}
			encounterElement.appendChild(excludeProtagonistElement);
			
			Element excludeAntagonistElement = doc.createElement("ExcludeAntagonist");
			for (int i=0; (i<EncounterEngine.cActors); ++i) {
				Element actorElement = doc.createElement(theEngine.getActorLabel(i));
				if (enc.getIsAllowedToBeAntagonist(i)) 
					actorElement.setTextContent("false");
				else
					actorElement.setTextContent("true");
				excludeAntagonistElement.appendChild(actorElement);
			}
			encounterElement.appendChild(excludeAntagonistElement);
			
			Element dayWindow = doc.createElement("DayWindow");
			Element minimum = doc.createElement("Minimum");
			minimum.setTextContent(String.valueOf(enc.getFirstDay()));
			dayWindow.appendChild(minimum);
			Element maximum = doc.createElement("Maximum");
			maximum.setTextContent(String.valueOf(enc.getLastDay()));
			dayWindow.appendChild(maximum);
			encounterElement.appendChild(dayWindow);
			
			Element introText = doc.createElement("IntroText");
			introText.setTextContent(enc.getIntroText());
			encounterElement.appendChild(introText);
			
			for (int i=0; (i<EncounterEngine.cOptions); ++i) {
				Element eOption = doc.createElement("Option");
				Element optionText = doc.createElement("OptionText");
				optionText.setTextContent(enc.getOption(i).getText());
				eOption.appendChild(optionText);
				
				Element deltaPBad_Good = doc.createElement("DeltaPBad_Good");
				float x = (float)enc.getOption(i).getDeltaPBad_Good();
				if (Math.abs(x) < 0.01f) x = 0.0f;
				deltaPBad_Good.setTextContent(String.valueOf(x));
				eOption.appendChild(deltaPBad_Good);
				
				Element deltaPFalse_Honest = doc.createElement("DeltaPFalse_Honest");
				x = (float)enc.getOption(i).getDeltaPFalse_Honest();
				if (Math.abs(x) < 0.01f) x = 0.0f;
				deltaPFalse_Honest.setTextContent(String.valueOf(x));
				eOption.appendChild(deltaPFalse_Honest);
				
				Element deltaPTimid_Dominant = doc.createElement("DeltaPTimid_Dominant");
				x = (float)enc.getOption(i).getDeltaPTimid_Dominant();
				if (Math.abs(x) < 0.01f) x = 0.0f;
				deltaPTimid_Dominant.setTextContent(String.valueOf(x));
				eOption.appendChild(deltaPTimid_Dominant);
				
				for (int j=0; (j<EncounterEngine.cReactions); ++j) {
					Element eReaction = doc.createElement("Reaction");
					
					Element reactionText = doc.createElement("ReactionText");
					reactionText.setTextContent(enc.getOption(i).getReaction(j).getText());
					eReaction.appendChild(reactionText);
					
					Element desirableFormula = doc.createElement("DesirableFormula");

					Element firstTrait = doc.createElement("FirstTrait");
					firstTrait.setTextContent(enc.getOption(i).getReaction(j).getFirstTrait());
					desirableFormula.appendChild(firstTrait);

					Element secondTrait = doc.createElement("SecondTrait");
					secondTrait.setTextContent(enc.getOption(i).getReaction(j).getSecondTrait());
					desirableFormula.appendChild(secondTrait);

					Element bias = doc.createElement("Bias");
					bias.setTextContent(String.valueOf(enc.getOption(i).getReaction(j).getBias()));
					desirableFormula.appendChild(bias);
					
					eReaction.appendChild(desirableFormula);
					eOption.appendChild(eReaction);
				}
				encounterElement.appendChild(eOption);
			}
			eList.appendChild(encounterElement);
		}
			
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.setAttribute("indent-number", new Integer(4));  //			
			Transformer transformer = tf.newTransformer();			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			
			// initialize StreamResult with File object to save to file			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outputStream);
			transformer.transform(source,result);
		} 
		catch (TransformerConfigurationException e) {
			System.out.println("can't tranform the DOM model");
		}
		catch (TransformerException e) {
			System.out.println("can't transform the DOM model");
		}
	}
// ************************************************************
	public static void main(String args[]) {
		EncounterEditor theEditor=new EncounterEditor();

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch(Exception evt) {}
		while (!quitFlag) {
			theEditor.initialize();
			while (!quitFlag) { 
				try { Thread.sleep(10); } catch (Exception e) {} 
			}
		}
		System.exit(0);
	}
// ************************************************************
}
