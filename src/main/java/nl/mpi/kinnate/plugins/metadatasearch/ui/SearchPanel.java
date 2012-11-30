package nl.mpi.kinnate.plugins.metadatasearch.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import nl.mpi.arbil.plugin.PluginArbilDataNode;
import nl.mpi.arbil.plugin.PluginArbilDataNodeLoader;
import nl.mpi.arbil.plugin.PluginArbilTable;
import nl.mpi.arbil.plugin.PluginArbilTableModel;
import nl.mpi.arbil.plugin.PluginBugCatcher;
import nl.mpi.arbil.plugin.PluginDialogHandler;
import nl.mpi.arbil.plugin.PluginDialogHandler.DialogueType;
import nl.mpi.arbil.plugin.PluginException;
import nl.mpi.arbil.plugin.PluginSessionStorage;
import nl.mpi.arbil.plugin.PluginWidgetFactory;
import nl.mpi.arbil.plugin.WrongNodeTypeException;
import nl.mpi.kinnate.entityindexer.QueryException;
import nl.mpi.kinnate.plugins.metadatasearch.data.DbTreeNode;
import nl.mpi.kinnate.plugins.metadatasearch.data.MetadataFileType;
import nl.mpi.kinnate.plugins.metadatasearch.data.MetadataTreeNode;
import nl.mpi.kinnate.plugins.metadatasearch.db.ArbilDatabase;
import nl.mpi.kinnate.plugins.metadatasearch.db.ArbilDatabase.CriterionJoinType;
import nl.mpi.kinnate.plugins.metadatasearch.db.SearchParameters;

/**
 * Created on : Jul 31, 2012, 6:34:07 PM
 *
 * @author Peter Withers
 */
public class SearchPanel extends JPanel implements ActionListener {

    final private ArbilDatabase arbilDatabase;
    final private JProgressBar jProgressBar;
//    final private JTextArea resultsTextArea;
    final private ArrayList<SearchCriterionPanel> criterionPanelArray;
    final private JPanel criterionArrayPanel;
    final private JComboBox criterionJoinComboBox;
    private MetadataFileType[] metadataPathTypes;
    private MetadataFileType[] metadataFieldTypes;
    final private JTree resultsTree;
    final private DefaultTreeModel defaultTreeModel;
    final private PluginArbilTable arbilTable;
    final private PluginArbilTableModel arbilTableModel;
    final private PluginArbilDataNodeLoader arbilDataNodeLoader;
    final private PluginDialogHandler arbilWindowManager;
    private int actionProgressCounter = 0;

    public SearchPanel(final PluginArbilDataNodeLoader arbilDataNodeLoader, final PluginDialogHandler dialogHandler, final PluginBugCatcher pluginBugCatcher, PluginSessionStorage pluginSessionStorage, PluginWidgetFactory pluginWidgetFactory) {
        this.arbilDataNodeLoader = arbilDataNodeLoader;
        this.arbilWindowManager = dialogHandler;
        arbilDatabase = new ArbilDatabase(pluginSessionStorage, arbilWindowManager, pluginBugCatcher);
        this.setLayout(new BorderLayout());
//        ArbilNodeSearchColumnComboBox.setSessionStorage(new ArbilSessionStorage());
//        this.add(new ArbilNodeSearchPanel(null, null, new ArbilNode[0]), BorderLayout.PAGE_END);
        criterionPanelArray = new ArrayList<SearchCriterionPanel>();

        criterionArrayPanel = new JPanel();
        criterionArrayPanel.setLayout(new BoxLayout(criterionArrayPanel, BoxLayout.PAGE_AXIS));

        this.add(criterionArrayPanel, BorderLayout.PAGE_START);
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel progressPanel = new JPanel(new BorderLayout());

        final JButton createButton = new JButton("create db");
        createButton.setActionCommand("create");
//        final JButton optionsButton = new JButton("update options");
//        optionsButton.setActionCommand("options");
//        optionsButton.addActionListener(this);
        createButton.addActionListener(this);

        JPanel dbButtonsPanel = new JPanel();

        JButton addExtraButton = new JButton("+");
        addExtraButton.setToolTipText("Add another criterion");
        addExtraButton.setActionCommand("add");
        addExtraButton.addActionListener(this);
        addExtraButton.setPreferredSize(new Dimension(addExtraButton.getPreferredSize().height, addExtraButton.getPreferredSize().height));
        dbButtonsPanel.add(addExtraButton);

        dbButtonsPanel.add(createButton);
//        dbButtonsPanel.add(optionsButton);

        progressPanel.add(dbButtonsPanel, BorderLayout.LINE_START);
        jProgressBar = new JProgressBar();

        progressPanel.add(jProgressBar, BorderLayout.CENTER);

        JPanel searchButtonsPanel = new JPanel();
        criterionJoinComboBox = new JComboBox(CriterionJoinType.values());
        criterionJoinComboBox.setSelectedItem(CriterionJoinType.intersect);
        searchButtonsPanel.add(criterionJoinComboBox);
        final JButton searchButton = new JButton("Search");
        searchButton.setActionCommand("search");
        searchButton.addActionListener(this);
        searchButtonsPanel.add(searchButton);
        progressPanel.add(searchButtonsPanel, BorderLayout.LINE_END);

        centerPanel.add(progressPanel, BorderLayout.PAGE_START);
//        resultsTextArea = new JTextArea();
//        centerPanel.add(new JScrollPane(resultsTextArea), BorderLayout.CENTER);

        defaultTreeModel = new DefaultTreeModel(new DbTreeNode("Please add or select a facet"));
        resultsTree = new JTree(defaultTreeModel);
        resultsTree.setRootVisible(false);
        resultsTree.setCellRenderer(new SearchTreeCellRenderer());
        resultsTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent tse) {
                ArrayList<PluginArbilDataNode> arbilDataNodeList = new ArrayList<PluginArbilDataNode>();
                final TreePath[] selectionPaths = resultsTree.getSelectionPaths();
                if (selectionPaths != null) {
                    for (TreePath treePath : selectionPaths) {
                        final Object lastPathComponent = treePath.getLastPathComponent();
                        if (lastPathComponent instanceof MetadataTreeNode) {
                            final PluginArbilDataNode arbilNode = ((MetadataTreeNode) lastPathComponent).getArbilNode();
                            if (arbilNode != null) {
                                arbilDataNodeList.add(arbilNode);
                            }
                        }
                    }
                }
                arbilTableModel.removeAllArbilDataNodeRows();
                arbilTableModel.addArbilDataNodes(arbilDataNodeList.toArray(new PluginArbilDataNode[0]));
            }
        });

        arbilTableModel = pluginWidgetFactory.createTableModel();
        arbilTable = pluginWidgetFactory.createTable(arbilTableModel, "FacetedTreeSelectionTable");
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(resultsTree), new JScrollPane((Component) arbilTable));

        centerPanel.add(jSplitPane, BorderLayout.CENTER);
        this.add(centerPanel, BorderLayout.CENTER);
    }

    static public void main(String[] args) {
        JFrame jFrame = new JFrame("Search Panel Test");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final PluginSessionStorage sessionStorage = new PluginSessionStorage() {
            public File getApplicationSettingsDirectory() {
                return new File("/Users/petwit2/.arbil/");
            }

            public File getProjectDirectory() {
                return new File("/Users/petwit2/.arbil/");
            }

            public File getProjectWorkingDirectory() {
                return new File("/Users/petwit2/.arbil/ArbilWorkingFiles/");
            }
        };
        final PluginArbilDataNodeLoader dataNodeLoader = new PluginArbilDataNodeLoader() {
            public PluginArbilDataNode getPluginArbilDataNode(Object registeringObject, URI localUri) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public URI getNodeURI(PluginArbilDataNode dataNode) throws WrongNodeTypeException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean isNodeLoading(PluginArbilDataNode dataNode) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        PluginDialogHandler dialogHandler = new PluginDialogHandler() {
            public void addMessageDialogToQueue(String messageString, String messageTitle) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean showConfirmDialogBox(String messageString, String messageTitle) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public int showDialogBox(String message, String title, int optionType, int messageType) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public int showDialogBox(String message, String title, int optionType, int messageType, Object[] options, Object initialValue) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public File[] showFileSelectBox(String titleText, boolean directorySelectOnly, boolean multipleSelect, Map<String, FileFilter> fileFilterMap, DialogueType dialogueType, JComponent customAccessory) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        PluginBugCatcher bugCatcher = new PluginBugCatcher() {
            public void logException(PluginException exception) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        PluginWidgetFactory widgetFactory = new PluginWidgetFactory() {
            public PluginArbilTable createTable(PluginArbilTableModel pluginArbilTableModel, String tableName) {
                class mockTable extends JTable implements PluginArbilTable {
                };
                return new mockTable();
            }

            public PluginArbilTableModel createTableModel() {
                return new PluginArbilTableModel() {
                    public void removeAllArbilDataNodeRows() {
//                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void addArbilDataNodes(PluginArbilDataNode[] pluginArbilDataNodes) {
//                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }
        };
        SearchPanel searchPanel = new SearchPanel(dataNodeLoader, dialogHandler, bugCatcher, sessionStorage, widgetFactory);
        jFrame.setContentPane(searchPanel);
        jFrame.pack();
        jFrame.setVisible(true);
        searchPanel.initOptions();
    }

    public void actionPerformed(ActionEvent e) {
        actionProgressCounter++;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jProgressBar.setIndeterminate(actionProgressCounter > 0);
            }
        });
        final String actionCommand = e.getActionCommand();
        System.out.println(actionCommand);
        SearchCriterionPanel eventCriterionPanel = null;
        Object sourceObject = e.getSource();
//        if (sourceObject instanceof SearchOptionBox) {
        while (sourceObject != null) {
            sourceObject = ((Component) sourceObject).getParent();
            if (sourceObject instanceof SearchCriterionPanel) {
                eventCriterionPanel = (SearchCriterionPanel) sourceObject;
                break;
            }
        }
//        }
        new Thread(getRunnable(actionCommand, eventCriterionPanel)).start();
    }

    public void initOptions() {
        actionProgressCounter++;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jProgressBar.setIndeterminate(actionProgressCounter > 0);
            }
        });
        new Thread(getRunnable("add", null)).start();
    }

    private Runnable getRunnable(final String actionCommand, final SearchCriterionPanel eventCriterionPanel) {
        return new Runnable() {
            public void run() {
//                System.out.println("actionProgressCounter: " + actionProgressCounter);
                if ("create".equals(actionCommand)) {
                    try {
                        System.out.println("create db");
                        arbilDatabase.createDatabase();
                        System.out.println("done");
                    } catch (QueryException exception) {
                        arbilWindowManager.addMessageDialogToQueue(exception.getMessage(), "Database Error");
                        exception.printStackTrace();
                    }
//                } else if ("options".equals(actionCommand)) {
                    // todo: when a database update occurs these queries should be run again and the UI updated
//                    metadataPathTypes = arbilDatabase.getMetadataTypes(null);
//                    metadataFieldTypes = arbilDatabase.getFieldMetadataTypes(null);
                } else if ("remove".equals(actionCommand)) {
                    criterionPanelArray.remove(eventCriterionPanel);
                    criterionArrayPanel.remove(eventCriterionPanel);
                    SearchPanel.this.revalidate();
                } else if ("add".equals(actionCommand)) {
                    // store the types so a query is not required each time
                    if (metadataFieldTypes == null || metadataPathTypes == null) {
                        System.out.println("run query");
                        metadataPathTypes = arbilDatabase.getMetadataTypes(null);
                        System.out.println("done");
                        System.out.println("run query");
                        metadataFieldTypes = arbilDatabase.getFieldMetadataTypes(null);
                        System.out.println("done");
                    }
                    final SearchCriterionPanel searchCriterionPanel = new SearchCriterionPanel(SearchPanel.this, metadataPathTypes, metadataFieldTypes, criterionPanelArray.size());
                    criterionPanelArray.add(searchCriterionPanel);
                    criterionArrayPanel.add(searchCriterionPanel);
                    SearchPanel.this.revalidate();
                } else if ("paths".equals(actionCommand)) {
                    System.out.println("run query");
                    MetadataFileType[] metadataFieldTypes = arbilDatabase.getFieldMetadataTypes(eventCriterionPanel.getMetadataFileType());
                    System.out.println("done");
                    eventCriterionPanel.setFieldOptions(metadataFieldTypes);
                } else if ("fields".equals(actionCommand)) {
                    // todo: get controlled vocabulary of field values for the search text area
                } else if ("search".equals(actionCommand)) {
                    System.out.println("run query");
                    final CriterionJoinType criterionJoinType = (CriterionJoinType) criterionJoinComboBox.getSelectedItem();
                    ArrayList<SearchParameters> searchParametersList = new ArrayList<SearchParameters>();
                    for (SearchCriterionPanel eventCriterionPanel : criterionPanelArray) {
                        searchParametersList.add(new SearchParameters(eventCriterionPanel.getMetadataFileType(), eventCriterionPanel.getMetadataFieldType(), eventCriterionPanel.getSearchNegator(), eventCriterionPanel.getSearchType(), eventCriterionPanel.getSearchText()));
                    }
                    final DbTreeNode rootTreeNode = arbilDatabase.getSearchResult(criterionJoinType, searchParametersList);

                    System.out.println("run query");
//        final DbTreeNode rootTreeNode = arbilDatabase.getTreeData(treeBranchTypeList);
                    rootTreeNode.setParentDbTreeNode(null, defaultTreeModel, arbilDataNodeLoader, arbilDatabase);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            defaultTreeModel.setRoot(rootTreeNode);
                        }
                    });
                    System.out.println("done");
//                    System.out.println("done");
//                    StringBuilder stringBuilder = new StringBuilder();
//                    if (resultTypes != null) {
//                        for (MetadataFileType resultType : resultTypes) {
//                            stringBuilder.append(resultType.getArbilPathString());
//                            stringBuilder.append("\n");
//                        }
//                    }
//                    resultsTextArea.setText(stringBuilder.toString());
                }
                actionProgressCounter--;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
//                        System.out.println("progress done: " + actionProgressCounter);
                        jProgressBar.setIndeterminate(actionProgressCounter > 0);
                    }
                });
            }
        };
    }
}
