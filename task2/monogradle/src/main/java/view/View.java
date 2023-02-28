package view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import controller.Controller;
import model.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class View {
    private JPanel Panel;
    private JPanel mainPanel;
    private JPanel textPanel;
    private JPanel buttonPannel;
    private JPanel resultPannel;
    private JPanel favouritePanel;
    private JTextField Name;
    private JTextField Distance;
    private JButton safeAsFavouriteButton;
    private JButton searchButton;
    private JList resultList;
    private JList favList;
    private JComboBox<String> Category;
    private JComboBox<Poi> POI;
    private JPanel saveFavourite;
    private JTextField favName;
    private JButton save;
    private JScrollPane resultPane;
    private JScrollPane favouritePane;
    private JButton delFav;
    private JButton delShop;
    private JPanel shopButtons;
    private JButton editShop;
    private JButton addShop;
    private JPanel favButtons;
    private JButton editFav;
    private JPanel addShopPanel;
    private JLabel addShopLable;
    private JPanel editShopPanel;
    private JLabel editShopLabel;
    private JPanel editFavPanel;
    private JLabel editFavLabel;
    private JComboBox categoryEditFav;
    private JPanel editFavFields;
    private JTextField nameEditFav;
    private JComboBox POIEditFav;
    private JTextField distanceEditFav;
    private JButton saveFavouriteButton;
    private JTextField LongAddShop;
    private JTextField LatAddShop;
    private JComboBox categoryAddShop;
    private JTextField nameAddShop;
    private JTextField hourAddShop;
    private JTextField websiteAddShop;
    private JButton addShopButton;
    private JTextField LongEditShop;
    private JTextField LatEditShop;
    private JComboBox CategoryEditShop;
    private JTextField NameEditShop;
    private JTextField hoursEditShop;
    private JTextField websiteEditShop;
    private JButton editShopButton;
    private JPanel fieldsEditShop;
    private JPanel addValuesPanel;
    private JPanel testPanel;

    private Filter filter;
    private long lastUsedIdShop = -1;
    private String lastUsedIdFavourite = null;

    FavouriteResult allFavourites;
    private JFrame frame;
    private JFrame frame2;
    private JFrame frame_addShop;
    private JFrame frame_editShop;
    private JFrame frame_editFav;

    private View() {
        filter = Filter.getInstance();
        frame = new JFrame("Shopfinder Graz - group04");
        frame2 = new JFrame("Save As Favourite");
        frame_addShop = new JFrame("Add New Shop To Database");
        frame_editShop = new JFrame("Edit Shop");
        frame_editFav = new JFrame("Edit Favourite");

    }

    public static void main(String[] args) {
        View app = new View();
        app.init(args);
    }

    private void init(String[] args) {
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        centreWindow(frame);
        frame.pack();
        // alle inhalte ausgrauen
        Category.setEnabled(false);
        Category.setBackground(Color.gray);
        Name.setEnabled(false);
        Name.setBackground(Color.gray);
        POI.setEnabled(false);
        POI.setBackground(Color.gray);
        Distance.setEnabled(false);
        Distance.setBackground(Color.gray);
        // keine datenbankconnection / database connecting
        frame.setVisible(true);
        frame.setResizable(false);
        // run (blocking)
        run(args);
    }

    private void run(String[] args) {
        CategoryResult allCategories = Controller.categories();
        if (allCategories.isStatusOk()) {
            Vector<String> vectorCategories = new Vector<String>(allCategories.getResult());
            vectorCategories.remove(null);
            Category.setModel(new DefaultComboBoxModel(vectorCategories));
            Category.setEnabled(true);
            Category.setBackground(Color.white);
        } else {
            JOptionPane.showMessageDialog(frame, allCategories.getStatusMessage());
            System.exit(-1);
        }

        allFavourites = Controller.favourites();
        if (!allFavourites.isStatusOk()) {
            JOptionPane.showMessageDialog(frame, allFavourites.getStatusMessage());
            System.exit(-2);
        }
        Vector<Favourite> favs = new Vector<Favourite>(allFavourites.getResult());
        favList.setListData(favs);

        PoiResult allPois = Controller.pois();
        if (allPois.isStatusOk()) {
            Vector<Poi> vectorPois = new Vector<Poi>(allPois.getResult());
            vectorPois.add(0, null);
            POI.setModel(new DefaultComboBoxModel(vectorPois));
            POI.setEnabled(true);
            POI.setBackground(Color.white);
        } else {
            JOptionPane.showMessageDialog(frame, allPois.getStatusMessage());
            System.exit(-3);
        }

        Name.setEnabled(true);
        Name.setBackground(Color.white);

        initiatePOIListener();
        initiateSearchButton();
        initiateSaveAsFavouriteButton();
        initiateSaveButton();

        initiateDeleteShopButton();
        initiateEditShopButton(allCategories);
        initiateSaveEditShopButton();
        initiateAddShopButton(allCategories);
        initiateSaveAddShopButton();

        initiateFavListListener();
        initiateDeleteFavouriteButton();
        initiateEditFavouriteButton(allCategories, allPois);
        initiateSaveEditFavouriteButton();
    }

    private void initiateSearchButton() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFilter() != 0)
                    return;
                SearchRequest req = SearchRequest.fromFilter(Filter.getInstance());
                SearchResult result = Controller.search(req);
                resultList.clearSelection();
                if (result.isStatusOk()) {
                    Vector<Shop> vectorResult = new Vector<Shop>(result.getResult());
                    resultList.setListData(vectorResult);
                } else
                    JOptionPane.showMessageDialog(frame, result.getStatusMessage());
            }
        });
    }

    private void initiatePOIListener() {
        POI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if ((Poi) POI.getSelectedItem() != null) {
                    Distance.setEnabled(true);
                    Distance.setBackground(Color.white);
                } else {
                    Distance.setText(null);
                    Distance.setEnabled(false);
                    Distance.setBackground(Color.gray);
                }
            }
        });
    }

    private void initiateSaveAsFavouriteButton() {
        safeAsFavouriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((Category.getSelectedItem() == null || Category.getSelectedItem().equals("")) &&
                  (Name.getText().equals("") || Name.getText() == null) &&
                  (POI.getSelectedItem() == null || POI.getSelectedItem().equals(""))) {
                    JOptionPane.showMessageDialog(frame, "You Can Not Save A Favourite Without Any Parameters.");
                    return;
                }
                if (validateFilter() != 0)
                    return;
                frame2.setContentPane(saveFavourite);
                frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame2.pack();
                centreWindow(frame2);
                favName.setText(null);
                frame2.setVisible(true);
                frame2.setResizable(false);
                return;
            }
        });
    }

    private void initiateSaveButton() {
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean found = false;
                for (Favourite fav : allFavourites.getResult())
                    if (fav.getName().equals(favName.getText())) {
                        found = true;
                        break;
                    }
                if (found) {
                    if (JOptionPane.showConfirmDialog(frame2, "This Favourite Name Is Already In Use. Would You Like To Overwrite The Old One?") != 0)
                        return;
                }
                FavouriteResult favRes = null;
                SearchRequest req = SearchRequest.fromFilter(Filter.getInstance());
                favRes = Controller.changeFavourite(favName.getText(), req);
                if (favRes.isStatusOk()) {
                    allFavourites = favRes;
                    favList.clearSelection();
                    Vector<Favourite> vectorResult = new Vector<Favourite>(allFavourites.getResult());
                    favList.setListData(vectorResult);
                    frame2.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame2, favRes.getStatusMessage());
                }
            }
        });
    }

    private void initiateDeleteShopButton() {
        delShop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Shop shop = (Shop) resultList.getSelectedValue();
                if (shop == null) {
                    JOptionPane.showMessageDialog(frame, "Please Select A Shop You Would Like To Delete.");
                    return;
                }
                if (JOptionPane.showConfirmDialog(frame, "Are You Sure That You Want To Delete This Shop?") != 0)
                    return;

                SuccessResult successResult = Controller.deleteShop(shop.getId());
                if (!successResult.isStatusOk()) {
                    JOptionPane.showMessageDialog(frame, successResult.getStatusMessage());
                    return;
                }
                SearchRequest req = SearchRequest.fromFilter(Filter.getInstance());
                SearchResult shopsList = Controller.search(req);
                if (shopsList.isStatusOk()) {
                    resultList.clearSelection();
                    Vector<Shop> shops = new Vector<Shop>(shopsList.getResult());
                    resultList.setListData(shops);
                } else
                    JOptionPane.showMessageDialog(frame, shopsList.getStatusMessage());
            }
        });
    }

    private void initiateEditShopButton(CategoryResult allCategories) {
        editShop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
//                JOptionPane.showMessageDialog(null, "TODO: Edit Shop");
                Shop shop = (Shop) resultList.getSelectedValue();
                if (shop == null) {
                    JOptionPane.showMessageDialog(frame, "Please Select A Shop You Would Like To Edit.");
                    return;
                }
                lastUsedIdShop = shop.getId();
                LongEditShop.setText(Double.toString(shop.getLongitude()));
                LatEditShop.setText(Double.toString(shop.getLatitude()));
                Vector<String> categories = new Vector<String>(allCategories.getResult());
                CategoryEditShop.setModel(new DefaultComboBoxModel(categories));
                CategoryEditShop.getModel().setSelectedItem(shop.getCategory());
                NameEditShop.setText(shop.getShopname());
                hoursEditShop.setText(shop.getOpeningHours());
                websiteEditShop.setText(shop.getWebsite());
                frame_editShop.setContentPane(editShopPanel);
                frame_editShop.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame_editShop.pack();
                centreWindow(frame_editShop);
                frame_editShop.setVisible(true);
                frame_editShop.setResizable(false);
            }
        });
    }

    private void initiateSaveEditShopButton() {
        editShopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Shop shop = new Shop();

                double lat = 0;
                double lon = 0;
                if (LongEditShop.getText() != null && !LongEditShop.getText().equals("") &&
                    LatEditShop.getText() != null && !LatEditShop.getText().equals("")) {
                    try {
                        lon = Double.parseDouble(LongEditShop.getText());
                        lat = Double.parseDouble(LatEditShop.getText());
                    } catch (NumberFormatException nfex) {
                        JOptionPane.showMessageDialog(frame_editShop, "Longitude And Latitude Have To Be Numbers.");
                        return;
                    }
                }
                else {
                    JOptionPane.showMessageDialog(frame_editShop, "Longitude And Latitude Are Not Allowed To Be Empty.");
                    return;
                }

                if (NameEditShop.getText() == null || NameEditShop.getText().equals("")) {
                    JOptionPane.showMessageDialog(frame_editShop, "Please Enter A Name For The Shop.");
                    return;
                }
                shop.setShopname(NameEditShop.getText());

                shop.setId(lastUsedIdShop);
                if (lat < -90 || lat > 90) {
                    JOptionPane.showMessageDialog(frame_addShop, "Latitude Has To Be Between -90 And 90.");
                    return;
                }
                if (lon < -180 || lon > 180) {
                    JOptionPane.showMessageDialog(frame_addShop, "Longitude Has To Be Between -180 And 180.");
                    return;
                }
                shop.setLatitude(lat);
                shop.setLongitude(lon);
                String category = (CategoryEditShop.getModel().getSelectedItem() != null) ? CategoryEditShop.getSelectedItem().toString() : null;
                shop.setCategory(category);
                shop.setOpeningHours(hoursEditShop.getText());
                shop.setWebsite(websiteEditShop.getText());
                SuccessResult success = Controller.changeShop(shop);
                if (!success.isStatusOk())
                    JOptionPane.showMessageDialog(frame_editShop, success.getStatusMessage());
                else {
                    if (validateFilter() != 0)
                        return;
                    SearchRequest req = SearchRequest.fromFilter(Filter.getInstance());
                    SearchResult shopsResult = Controller.search(req);
                    if (!shopsResult.isStatusOk())
                        JOptionPane.showMessageDialog(frame_editShop, shopsResult.getStatusMessage());
                    resultList.clearSelection();
                    Vector<Shop> shops = new Vector<Shop>(shopsResult.getResult());
                    resultList.setListData(shops);
                }
                lastUsedIdShop = -1;
                frame_editShop.dispose();
            }
        });
    }

    private void initiateAddShopButton(CategoryResult allCategories) {
        addShop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Vector<String> categories = new Vector<String>(allCategories.getResult());
                categoryAddShop.setModel(new DefaultComboBoxModel(categories));
                frame_addShop.setContentPane(addShopPanel);
                frame_addShop.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame_addShop.pack();
                centreWindow(frame_addShop);
                frame_addShop.setVisible(true);
                frame_addShop.setResizable(false);
                return;
            }
        });
    }

    private void initiateSaveAddShopButton() {
        addShopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Shop shop = new Shop();

                double lat = 0;
                double lon = 0;
                if (LongAddShop.getText() != null && !LongAddShop.getText().equals("") &&
                  LatAddShop.getText() != null && !LatAddShop.getText().equals("")) {
                    try {
                        lat = Double.parseDouble(LatAddShop.getText());
                        lon = Double.parseDouble(LongAddShop.getText());
                    } catch (NumberFormatException nfex) {
                        JOptionPane.showMessageDialog(frame_addShop, "Longitude And Latitude Have To Be Numbers.");
                        return;
                    }
                }
                else {
                    JOptionPane.showMessageDialog(frame_addShop, "Longitude And Latitude Are Not Allowed To Be Empty.");
                    return;
                }
                if (nameAddShop.getText() == null || nameAddShop.getText().equals("")) {
                    JOptionPane.showMessageDialog(frame_addShop, "Please Select A Name For Your Shop.");
                    return;
                }
                shop.setShopname(nameAddShop.getText());
                shop.setId(0);
                if (lat < -90 || lat > 90) {
                    JOptionPane.showMessageDialog(frame_addShop, "Latitude Has To Be Between -90 And 90.");
                    return;
                }
                if (lon < -180 || lon > 180) {
                    JOptionPane.showMessageDialog(frame_addShop, "Longitude Has To Be Between -180 And 180.");
                    return;
                }
                shop.setLatitude(lat);
                shop.setLongitude(lon);
                String category = (categoryAddShop.getSelectedItem() != null) ? categoryAddShop.getSelectedItem().toString() : null;
                shop.setCategory(category);
                shop.setOpeningHours(hourAddShop.getText());
                shop.setWebsite(websiteAddShop.getText());
                SuccessResult success = Controller.changeShop(shop);
                if (!success.isStatusOk()) {
                    JOptionPane.showMessageDialog(frame_addShop, success.getStatusMessage());
                    return;
                }

                SearchRequest req = SearchRequest.fromFilter(Filter.getInstance());
                SearchResult shopsResult = Controller.search(req);
                if (!shopsResult.isStatusOk()) {
                    JOptionPane.showMessageDialog(frame_addShop, shopsResult.getStatusMessage());
                    return;
                }
                JOptionPane.showMessageDialog(frame_addShop, "Shop Successfully Added To Database.");
                frame_addShop.dispose();
            }
        });
    }

    private void initiateFavListListener() {
        favList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Favourite favourite = (Favourite) favList.getSelectedValue();
                if (favourite == null) {
                    return;
                }
                filter.setCategory(favourite.getsCategory());
                filter.setName(favourite.getsName());
                filter.setPoi(favourite.getsPoi());
                filter.setDistance(favourite.getsDistance());
                Category.getModel().setSelectedItem(filter.getCategory());
                Name.setText(filter.getName());
                POI.getModel().setSelectedItem(filter.getPoi());
                if (filter.getPoi() != null) {
                    Distance.setText(filter.getDistance().toString());
                }
                SearchRequest req = SearchRequest.fromFilter(Filter.getInstance());
                SearchResult shopsList = Controller.search(req);
                if (shopsList.isStatusOk()) {
                    resultList.clearSelection();
                    Vector<Shop> shops = new Vector<Shop>(shopsList.getResult());
                    resultList.setListData(shops);
                } else
                    JOptionPane.showMessageDialog(frame, shopsList.getStatusMessage());
            }
        });
    }

    private void initiateDeleteFavouriteButton() {
        delFav.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Favourite favourite = (Favourite) favList.getSelectedValue();
                if (favourite == null) {
                    JOptionPane.showMessageDialog(frame, "Please Select A Favourite You Would Like To Delete.");
                    return;
                }
                if (JOptionPane.showConfirmDialog(frame, "Are You Sure That You Want To Delete This Favourite?") != 0)
                    return;
                FavouriteResult favRes = Controller.changeFavourite(favourite.getName(), null);
                if (favRes.isStatusOk()) {
                    favList.clearSelection();
                    allFavourites = favRes;
                    Vector<Favourite> favs = new Vector<Favourite>(allFavourites.getResult());
                    favList.setListData(favs);
                } else
                    JOptionPane.showMessageDialog(frame, favRes.getStatusMessage());
            }
        });
    }

    private void initiateEditFavouriteButton(CategoryResult allCategories, PoiResult allPois) {
        editFav.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Favourite favourite = (Favourite) favList.getSelectedValue();
                if (favourite == null) {
                    JOptionPane.showMessageDialog(frame, "Please Select A Favourite You Would Like To Edit.");
                    return;
                }
                lastUsedIdFavourite = favourite.getName();
                Vector<String> categories = new Vector<String>(allCategories.getResult());
                categoryEditFav.setModel(new DefaultComboBoxModel(categories));
                categoryEditFav.getModel().setSelectedItem(favourite.getsCategory());
                nameEditFav.setText(favourite.getsName());
                Vector<Poi> pois = new Vector<Poi>(allPois.getResult());
                pois.add(0, null);
                POIEditFav.setModel(new DefaultComboBoxModel(pois));
                POIEditFav.getModel().setSelectedItem(favourite.getsPoi());

                initiatePOIEditFavActionListener();
                if (POIEditFav.getSelectedItem() == null) {
                    distanceEditFav.setText(null);
                    distanceEditFav.setEnabled(false);
                    distanceEditFav.setBackground(Color.gray);
                    System.out.println("NO POI Selected");
                }
                String distance = (POIEditFav.getSelectedItem() != null) ? favourite.getsDistance().toString() : null;
                distanceEditFav.setText(distance);
                frame_editFav.setContentPane(editFavPanel);
                frame_editFav.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame_editFav.pack();
                centreWindow(frame_editFav);
                frame_editFav.setVisible(true);
                frame_editFav.setResizable(false);
            }
        });
    }

    private void initiatePOIEditFavActionListener () {
        POIEditFav.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (POIEditFav.getSelectedItem() != null) {
                    distanceEditFav.setEnabled(true);
                    distanceEditFav.setBackground(Color.white);
                } else {
                    distanceEditFav.setText(null);
                    System.out.println(distanceEditFav.getText());
                    distanceEditFav.setEnabled(false);
                    distanceEditFav.setBackground(Color.gray);
                }
            }
        });
    }

    private void initiateSaveEditFavouriteButton() {
        saveFavouriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer dist = null;
                System.out.println(distanceEditFav.getText());
                if (distanceEditFav.getText() != null && !distanceEditFav.getText().equals("")) {
                    try {
                        dist = Integer.parseInt(distanceEditFav.getText());
                    } catch (NumberFormatException nfex) {
                        JOptionPane.showMessageDialog(frame_editFav, "Please Only Use Numbers For Distance.");
                        return;
                    }
                }
                if (dist != null && dist < 0) {
                    JOptionPane.showMessageDialog(frame_editFav, "The Distance Has To Be Larger Than 0.");
                    return;
                }
                Category.getModel().setSelectedItem(categoryEditFav.getModel().getSelectedItem());
                Name.setText(nameEditFav.getText());
                POI.getModel().setSelectedItem(POIEditFav.getModel().getSelectedItem());
                Distance.setText(dist == null ? null : distanceEditFav.getText());
                if (validateFilter() != 0)
                    return;
                SearchRequest req = SearchRequest.fromFilter(Filter.getInstance());
                FavouriteResult favouriteResult = Controller.changeFavourite(lastUsedIdFavourite, req);
                if (!favouriteResult.isStatusOk()) {
                    JOptionPane.showMessageDialog(frame_editFav, favouriteResult.getStatusMessage());
                    return;
                }
                favList.clearSelection();
                Vector<Favourite> favourites = new Vector<Favourite>(favouriteResult.getResult());
                favList.setListData(favourites);
                SearchResult shopsResult = Controller.search(req);
                if (!shopsResult.isStatusOk()) {
                    JOptionPane.showMessageDialog(frame_addShop, shopsResult.getStatusMessage());
                    return;
                }
                resultList.clearSelection();
                Vector<Shop> shops = new Vector<Shop>(shopsResult.getResult());
                resultList.setListData(shops);

                lastUsedIdFavourite = null;
                frame_editFav.dispose();
            }
        });
    }

    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 5);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 5);
        frame.setLocation(x, y);
    }

    private int validateFilter() {
        int success = filter.validateFilters(Category.getSelectedItem(), Name.getText(), POI.getSelectedItem(), Distance.getText());
        if (success == filter.BELOWZERO) {
            JOptionPane.showMessageDialog(frame, "The Distance Has To Be Larger Than 0.");
        } else if (success == filter.CONTAINSLETTER) {
            JOptionPane.showMessageDialog(frame, "Please Only Use Numbers For Distance.");
        } else if (success == filter.EMPTY) {
            JOptionPane.showMessageDialog(frame, "Please Choose The Distance You Would Like To Search In.");
        } else if (success == filter.NOPOIBUTDIST) {
            JOptionPane.showMessageDialog(frame, "distance: should not be set if no Poi is given.");
        }
        return success;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        Panel = new JPanel();
        Panel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        saveFavourite = new JPanel();
        saveFavourite.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        Panel.add(saveFavourite, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(100, 100), null, 0, false));
        favName = new JTextField();
        saveFavourite.add(favName, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(500, 25), new Dimension(500, 25), new Dimension(500, 25), 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("favourite's name");
        saveFavourite.add(label1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        save = new JButton();
        save.setText("save");
        saveFavourite.add(save, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        saveFavourite.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer2 = new Spacer();
        saveFavourite.add(spacer2, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer3 = new Spacer();
        saveFavourite.add(spacer3, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer4 = new Spacer();
        saveFavourite.add(spacer4, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        addShopPanel = new JPanel();
        addShopPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        Panel.add(addShopPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addShopLable = new JLabel();
        addShopLable.setText("Add New Shop To Database:");
        addShopPanel.add(addShopLable, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addValuesPanel = new JPanel();
        addValuesPanel.setLayout(new GridLayoutManager(5, 8, new Insets(0, 0, 0, 0), -1, -1));
        addShopPanel.add(addValuesPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        LongAddShop = new JTextField();
        addValuesPanel.add(LongAddShop, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        LatAddShop = new JTextField();
        addValuesPanel.add(LatAddShop, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        categoryAddShop = new JComboBox();
        addValuesPanel.add(categoryAddShop, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 0, false));
        nameAddShop = new JTextField();
        addValuesPanel.add(nameAddShop, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        hourAddShop = new JTextField();
        addValuesPanel.add(hourAddShop, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        websiteAddShop = new JTextField();
        websiteAddShop.setText("");
        addValuesPanel.add(websiteAddShop, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        addShopButton = new JButton();
        addShopButton.setText("Add Shop");
        addValuesPanel.add(addShopButton, new GridConstraints(3, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        addValuesPanel.add(spacer5, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer6 = new Spacer();
        addValuesPanel.add(spacer6, new GridConstraints(4, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Longitude");
        addValuesPanel.add(label2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Latitude");
        addValuesPanel.add(label3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Shop Category");
        addValuesPanel.add(label4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Name Of Shop");
        addValuesPanel.add(label5, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Opening Hours");
        addValuesPanel.add(label6, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Website");
        addValuesPanel.add(label7, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        addValuesPanel.add(spacer7, new GridConstraints(3, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer8 = new Spacer();
        addValuesPanel.add(spacer8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer9 = new Spacer();
        addShopPanel.add(spacer9, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        editShopPanel = new JPanel();
        editShopPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        Panel.add(editShopPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        editShopLabel = new JLabel();
        editShopLabel.setText("Edit Shop:");
        editShopPanel.add(editShopLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldsEditShop = new JPanel();
        fieldsEditShop.setLayout(new GridLayoutManager(5, 8, new Insets(0, 0, 0, 0), -1, -1));
        editShopPanel.add(fieldsEditShop, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        LongEditShop = new JTextField();
        LongEditShop.setText("");
        fieldsEditShop.add(LongEditShop, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        LatEditShop = new JTextField();
        fieldsEditShop.add(LatEditShop, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        CategoryEditShop = new JComboBox();
        fieldsEditShop.add(CategoryEditShop, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 0, false));
        NameEditShop = new JTextField();
        fieldsEditShop.add(NameEditShop, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        hoursEditShop = new JTextField();
        fieldsEditShop.add(hoursEditShop, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        websiteEditShop = new JTextField();
        fieldsEditShop.add(websiteEditShop, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Longitude");
        fieldsEditShop.add(label8, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Latitude");
        fieldsEditShop.add(label9, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Shop Category");
        fieldsEditShop.add(label10, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Name Of Shop");
        fieldsEditShop.add(label11, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Opening Hours");
        fieldsEditShop.add(label12, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Website");
        fieldsEditShop.add(label13, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editShopButton = new JButton();
        editShopButton.setText("Edit Shop");
        fieldsEditShop.add(editShopButton, new GridConstraints(3, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        fieldsEditShop.add(spacer10, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer11 = new Spacer();
        fieldsEditShop.add(spacer11, new GridConstraints(4, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer12 = new Spacer();
        fieldsEditShop.add(spacer12, new GridConstraints(3, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer13 = new Spacer();
        fieldsEditShop.add(spacer13, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer14 = new Spacer();
        editShopPanel.add(spacer14, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        editFavPanel = new JPanel();
        editFavPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        Panel.add(editFavPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        editFavLabel = new JLabel();
        editFavLabel.setText("Edit Favourite:");
        editFavPanel.add(editFavLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editFavFields = new JPanel();
        editFavFields.setLayout(new GridLayoutManager(5, 6, new Insets(0, 0, 0, 0), -1, -1));
        editFavPanel.add(editFavFields, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        categoryEditFav = new JComboBox();
        editFavFields.add(categoryEditFav, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 0, false));
        nameEditFav = new JTextField();
        editFavFields.add(nameEditFav, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        POIEditFav = new JComboBox();
        editFavFields.add(POIEditFav, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(400, -1), new Dimension(400, -1), new Dimension(400, -1), 0, false));
        distanceEditFav = new JTextField();
        editFavFields.add(distanceEditFav, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Shop Category");
        editFavFields.add(label14, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Name Of Shop");
        editFavFields.add(label15, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("Point Of Interest");
        editFavFields.add(label16, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("Distance From POI In Meter");
        editFavFields.add(label17, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveFavouriteButton = new JButton();
        saveFavouriteButton.setText("Save Favourite");
        editFavFields.add(saveFavouriteButton, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer15 = new Spacer();
        editFavFields.add(spacer15, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer16 = new Spacer();
        editFavFields.add(spacer16, new GridConstraints(4, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer17 = new Spacer();
        editFavFields.add(spacer17, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer18 = new Spacer();
        editFavFields.add(spacer18, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer19 = new Spacer();
        editFavPanel.add(spacer19, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        testPanel = new JPanel();
        testPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        Panel.add(testPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        testPanel.add(mainPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        textPanel = new JPanel();
        textPanel.setLayout(new GridLayoutManager(3, 6, new Insets(0, 0, 0, 0), -1, -1));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(textPanel, gbc);
        Name = new JTextField();
        Name.setToolTipText("Name");
        textPanel.add(Name, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        Distance = new JTextField();
        textPanel.add(Distance, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 20), new Dimension(200, 20), new Dimension(200, 20), 0, false));
        Category = new JComboBox();
        textPanel.add(Category, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 0, false));
        POI = new JComboBox();
        textPanel.add(POI, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(400, -1), new Dimension(400, -1), new Dimension(400, -1), 0, false));
        final JLabel label18 = new JLabel();
        label18.setText("Name Of Shop");
        textPanel.add(label18, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label19 = new JLabel();
        label19.setText("Point Of Interest");
        textPanel.add(label19, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label20 = new JLabel();
        label20.setText("Distance From POI In Meter");
        textPanel.add(label20, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label21 = new JLabel();
        label21.setText("Shop Category");
        textPanel.add(label21, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer20 = new Spacer();
        textPanel.add(spacer20, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer21 = new Spacer();
        textPanel.add(spacer21, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer22 = new Spacer();
        textPanel.add(spacer22, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        buttonPannel = new JPanel();
        buttonPannel.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), -1, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(buttonPannel, gbc);
        safeAsFavouriteButton = new JButton();
        safeAsFavouriteButton.setText("Save As Favourite");
        buttonPannel.add(safeAsFavouriteButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        searchButton = new JButton();
        searchButton.setText("         Search         ");
        buttonPannel.add(searchButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer23 = new Spacer();
        buttonPannel.add(spacer23, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(1, 1), new Dimension(1, 1), new Dimension(1, 1), 0, false));
        final Spacer spacer24 = new Spacer();
        buttonPannel.add(spacer24, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(1, 1), new Dimension(1, 1), new Dimension(1, 1), 0, false));
        final Spacer spacer25 = new Spacer();
        buttonPannel.add(spacer25, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(1, 1), new Dimension(1, 1), new Dimension(1, 1), 0, false));
        final Spacer spacer26 = new Spacer();
        buttonPannel.add(spacer26, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 20), new Dimension(-1, 20), new Dimension(-1, 20), 0, false));
        final Spacer spacer27 = new Spacer();
        buttonPannel.add(spacer27, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 50), new Dimension(-1, 50), new Dimension(-1, 50), 0, false));
        resultPannel = new JPanel();
        resultPannel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(resultPannel, gbc);
        final JLabel label22 = new JLabel();
        label22.setText("Results");
        resultPannel.add(label22, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        resultPane = new JScrollPane();
        resultPannel.add(resultPane, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(200, 100), new Dimension(200, 100), null, 0, false));
        resultList = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        resultList.setModel(defaultListModel1);
        resultPane.setViewportView(resultList);
        shopButtons = new JPanel();
        shopButtons.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        resultPannel.add(shopButtons, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        delShop = new JButton();
        delShop.setText("Delete Shop");
        shopButtons.add(delShop, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editShop = new JButton();
        editShop.setText("Edit Shop");
        shopButtons.add(editShop, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addShop = new JButton();
        addShop.setText("Add Shop");
        shopButtons.add(addShop, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer28 = new Spacer();
        resultPannel.add(spacer28, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(750, 10), new Dimension(750, 10), new Dimension(750, 10), 0, false));
        final Spacer spacer29 = new Spacer();
        resultPannel.add(spacer29, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(2, -1), new Dimension(2, -1), new Dimension(2, -1), 0, false));
        favouritePanel = new JPanel();
        favouritePanel.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(favouritePanel, gbc);
        final JLabel label23 = new JLabel();
        label23.setText("Favourites");
        favouritePanel.add(label23, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        favouritePane = new JScrollPane();
        favouritePanel.add(favouritePane, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(200, 100), new Dimension(200, 100), null, 0, false));
        favList = new JList();
        final DefaultListModel defaultListModel2 = new DefaultListModel();
        favList.setModel(defaultListModel2);
        favouritePane.setViewportView(favList);
        favButtons = new JPanel();
        favButtons.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        favouritePanel.add(favButtons, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        delFav = new JButton();
        delFav.setText("Delete Favourite");
        favButtons.add(delFav, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editFav = new JButton();
        editFav.setText("Edit Favourite");
        favButtons.add(editFav, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer30 = new Spacer();
        favouritePanel.add(spacer30, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer31 = new Spacer();
        favouritePanel.add(spacer31, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(3, -1), new Dimension(3, -1), new Dimension(3, -1), 0, false));
        final Spacer spacer32 = new Spacer();
        favouritePanel.add(spacer32, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(5, -1), new Dimension(5, -1), new Dimension(5, -1), 0, false));
        label18.setLabelFor(Name);
        label19.setLabelFor(POI);
        label20.setLabelFor(Distance);
        label22.setLabelFor(Category);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Panel;
    }
}