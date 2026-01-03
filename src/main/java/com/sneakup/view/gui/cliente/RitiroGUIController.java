package com.sneakup.view.gui.cliente;

import com.sneakup.model.Sessione;
import com.sneakup.model.domain.Scarpa;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class RitiroGUIController {

    @FXML private Button btnLogin, btnConferma, btnIndietro, btnPosizione;
    @FXML private Label lblUser, lblCaricamento;
    @FXML private Region barraAnimata;
    @FXML private ComboBox<String> comboBrand;
    @FXML private ComboBox<String> comboNegozio;
    @FXML private WebView mapView;
    private WebEngine webEngine;

    // Database: Brand -> Lista Negozi
    private final Map<String, List<Map<String, Object>>> databaseNegozi = new HashMap<>();

    private String prevFxml;
    private Scarpa scarpaDettaglio;
    private Scarpa prodottoAcquistoSingolo;
    private String prevBrand, prevGen, prevCat, prevRicerca;

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);
        if (Sessione.getInstance().isLoggato()) {
            if (btnLogin != null) { btnLogin.setVisible(false); btnLogin.setManaged(false); }
            if (lblUser != null) {
                lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
                lblUser.setVisible(true); lblUser.setManaged(true);
            }
        }

        // 1. CARICAMENTO MASSIVO NEGOZI REALI ITALIA
        inizializzaDatabaseNegozi();

        // 2. CONFIGURAZIONE MENU
        if (comboBrand != null) {
            comboBrand.getItems().addAll(databaseNegozi.keySet());
            comboBrand.setOnAction(e -> {
                aggiornaListaNegozi(comboBrand.getValue());
                aggiornaMappaPerBrand(comboBrand.getValue());
            });
        }

        if (comboNegozio != null) {
            comboNegozio.setOnAction(e -> {
                String brand = comboBrand.getValue();
                String negozioNome = comboNegozio.getValue();
                if (brand != null && negozioNome != null) {
                    Map<String, Object> info = trovaNegozio(brand, negozioNome);
                    if (info != null) zoomaSuNegozio((double)info.get("lat"), (double)info.get("lon"));
                }
            });
        }

        if (mapView != null) {
            webEngine = mapView.getEngine();
            webEngine.setJavaScriptEnabled(true);
            caricaMappaVuota();
        }
    }

    // --- DATABASE NEGOZI REALI (ESTESO) ---
    private void inizializzaDatabaseNegozi() {
        // === JD SPORTS (Copertura Nazionale) ===
        List<Map<String, Object>> jd = new ArrayList<>();
        // Nord
        jd.add(creaNegozio("Milano Duomo", 45.4641, 9.1897));
        jd.add(creaNegozio("Milano C.so Buenos Aires", 45.4802, 9.2115));
        jd.add(creaNegozio("Arese Il Centro", 45.5558, 9.0558));
        jd.add(creaNegozio("Torino Le Gru", 45.0345, 7.6083));
        jd.add(creaNegozio("Venezia Marghera Nave de Vero", 45.4388, 12.2155));
        jd.add(creaNegozio("Bologna Gran Reno", 44.4842, 11.2725));
        // Centro
        jd.add(creaNegozio("Roma Termini", 41.9014, 12.5011));
        jd.add(creaNegozio("Roma Est Lunghezza", 41.9158, 12.6633));
        jd.add(creaNegozio("Roma Euroma2", 41.8159, 12.4578));
        jd.add(creaNegozio("Roma Aura Valle Aurelia", 41.9015, 12.4402));
        // Sud & Isole
        jd.add(creaNegozio("Napoli Afragola Porte di Napoli", 40.9168, 14.3314));
        jd.add(creaNegozio("Napoli Marcianise Campania", 41.0062, 14.2982));
        jd.add(creaNegozio("Bari Casamassima", 40.9575, 16.9156));
        jd.add(creaNegozio("Catania Centro Sicilia", 37.4722, 15.0278));
        jd.add(creaNegozio("Palermo Forum", 38.0934, 13.3995));
        databaseNegozi.put("JD Sports", jd);

        // === FOOT LOCKER (Città Principali) ===
        List<Map<String, Object>> fl = new ArrayList<>();
        // Nord
        fl.add(creaNegozio("Milano C.so Vitt. Emanuele", 45.4654, 9.1912));
        fl.add(creaNegozio("Milano Via Torino", 45.4621, 9.1865));
        fl.add(creaNegozio("Torino Via Roma", 45.0680, 7.6821));
        fl.add(creaNegozio("Genova Via XX Settembre", 44.4068, 8.9355));
        fl.add(creaNegozio("Verona Via Mazzini", 45.4418, 10.9972));
        fl.add(creaNegozio("Bologna Via Indipendenza", 44.4965, 11.3433));
        // Centro
        fl.add(creaNegozio("Firenze Via Calzaiuoli", 43.7712, 11.2550));
        fl.add(creaNegozio("Roma Via del Corso", 41.9056, 12.4823));
        fl.add(creaNegozio("Roma Via Ottaviano", 41.9082, 12.4575));
        fl.add(creaNegozio("Roma Tuscolana", 41.8682, 12.5356));
        // Sud
        fl.add(creaNegozio("Napoli Via Toledo", 40.8441, 14.2488));
        fl.add(creaNegozio("Napoli C.so Umberto I", 40.8488, 14.2625));
        fl.add(creaNegozio("Bari Via Sparano", 41.1232, 16.8698));
        fl.add(creaNegozio("Palermo Via Maqueda", 38.1167, 13.3618));
        databaseNegozi.put("Foot Locker", fl);

        // === SNIPES ===
        List<Map<String, Object>> snipes = new ArrayList<>();
        snipes.add(creaNegozio("Milano C.so Buenos Aires", 45.4789, 9.2101));
        snipes.add(creaNegozio("Torino Via Garibaldi", 45.0725, 7.6811));
        snipes.add(creaNegozio("Roma Porta di Roma", 41.9791, 12.5369));
        snipes.add(creaNegozio("Brescia Elnos", 45.5381, 10.1695));
        snipes.add(creaNegozio("Rimini Le Befane", 44.0456, 12.5532));
        databaseNegozi.put("Snipes", snipes);

        // === AW LAB ===
        List<Map<String, Object>> aw = new ArrayList<>();
        aw.add(creaNegozio("Napoli Via Toledo", 40.8427, 14.2494));
        aw.add(creaNegozio("Roma Via del Corso", 41.9033, 12.4801));
        aw.add(creaNegozio("Milano Via Torino", 45.4619, 9.1862));
        aw.add(creaNegozio("Catania Via Etnea", 37.5091, 15.0845));
        databaseNegozi.put("AW LAB", aw);
    }

    private Map<String, Object> creaNegozio(String nome, double lat, double lon) {
        Map<String, Object> m = new HashMap<>();
        m.put("nome", nome); m.put("lat", lat); m.put("lon", lon);
        return m;
    }

    // --- GEOLOCALIZZAZIONE REALE ---
    @FXML
    private void handleUsaMiaPosizione(ActionEvent event) {
        if (lblCaricamento != null) lblCaricamento.setVisible(true);
        if (comboBrand.getValue() == null) {
            mostraErrore("Seleziona prima un Brand (es. JD Sports).");
            if (lblCaricamento != null) lblCaricamento.setVisible(false);
            return;
        }

        new Thread(() -> {
            try {
                double[] myCoords = ottieniPosizioneRealeDaIP();
                if (myCoords == null) {
                    Platform.runLater(() -> mostraErrore("Impossibile rilevare la posizione."));
                    return;
                }
                double myLat = myCoords[0];
                double myLon = myCoords[1];

                Platform.runLater(() -> {
                    trovaEdEvidenziaPiuVicino(myLat, myLon);
                    if (lblCaricamento != null) lblCaricamento.setVisible(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    mostraErrore("Errore geolocalizzazione.");
                    if (lblCaricamento != null) lblCaricamento.setVisible(false);
                });
            }
        }).start();
    }

    private double[] ottieniPosizioneRealeDaIP() {
        try {
            URL url = new URL("http://ip-api.com/json");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine; StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) content.append(inputLine);
            in.close(); con.disconnect();

            String json = content.toString();
            double lat = estraiValoreJson(json, "\"lat\":");
            double lon = estraiValoreJson(json, "\"lon\":");
            return new double[]{lat, lon};
        } catch (Exception e) { return null; }
    }

    private double estraiValoreJson(String json, String key) {
        int startIndex = json.indexOf(key);
        if (startIndex == -1) return 0.0;
        startIndex += key.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
        return Double.parseDouble(json.substring(startIndex, endIndex).replace(":", "").trim());
    }

    private void trovaEdEvidenziaPiuVicino(double myLat, double myLon) {
        String brand = comboBrand.getValue();
        List<Map<String, Object>> negozi = databaseNegozi.get(brand);
        Map<String, Object> best = null;
        double minDist = Double.MAX_VALUE;

        for (Map<String, Object> shop : negozi) {
            double dist = calcolaDistanza(myLat, myLon, (double)shop.get("lat"), (double)shop.get("lon"));
            if (dist < minDist) { minDist = dist; best = shop; }
        }

        if (best != null) {
            String nome = (String) best.get("nome");
            comboNegozio.setValue(nome);
            zoomaSuNegozio((double)best.get("lat"), (double)best.get("lon"));
            new Alert(Alert.AlertType.INFORMATION, "Trovato!\nIl negozio più vicino è:\n" + nome + "\n(" + String.format("%.1f", minDist) + " km da te)").show();
        }
    }

    // --- MAPPA & HELPER ---
    private void caricaMappaVuota() { webEngine.loadContent(generaHtmlLeaflet(42.50, 12.50, 6, new ArrayList<>())); }

    private void aggiornaMappaPerBrand(String brand) {
        List<Map<String, Object>> negozi = databaseNegozi.get(brand);
        if (negozi != null) webEngine.loadContent(generaHtmlLeaflet(42.50, 12.50, 6, negozi));
    }

    private void zoomaSuNegozio(double lat, double lon) {
        webEngine.executeScript("map.flyTo([" + lat + ", " + lon + "], 16);");
    }

    private String generaHtmlLeaflet(double lat, double lon, int z, List<Map<String, Object>> m) {
        StringBuilder js = new StringBuilder();
        for (Map<String, Object> x : m) {
            js.append(String.format("L.marker([%s, %s]).addTo(map).bindPopup('<b>%s</b>');", x.get("lat"), x.get("lon"), ((String)x.get("nome")).replace("'", "\\'")));
        }
        return "<!DOCTYPE html><html><head><link rel='stylesheet' href='https://unpkg.com/leaflet@1.7.1/dist/leaflet.css'/><script src='https://unpkg.com/leaflet@1.7.1/dist/leaflet.js'></script><style>body,html,#map{margin:0;padding:0;height:100%}</style></head><body><div id='map'></div><script>var map=L.map('map').setView(["+lat+","+lon+"],"+z+");L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{attribution:'&copy; OSM'}).addTo(map);"+js+"</script></body></html>";
    }

    private void aggiornaListaNegozi(String brand) {
        comboNegozio.getItems().clear(); comboNegozio.setDisable(false); comboNegozio.setPromptText("Scegli punto vendita " + brand);
        for(Map<String, Object> n : databaseNegozi.get(brand)) comboNegozio.getItems().add((String)n.get("nome"));
    }

    private Map<String, Object> trovaNegozio(String brand, String nome) {
        for(Map<String, Object> m : databaseNegozi.get(brand)) if(m.get("nome").equals(nome)) return m;
        return null;
    }

    private double calcolaDistanza(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; double dLat = Math.toRadians(lat2-lat1); double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLon/2)*Math.sin(dLon/2);
        return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));
    }

    private void mostraErrore(String t) { new Alert(Alert.AlertType.ERROR, t).showAndWait(); }

    // --- NAVIGAZIONE ---
    public void setDatiNavigazione(String prevFxml, Scarpa scarpaDettaglio, Scarpa prodottoAcquisto, String brand, String gen, String cat, String ricerca) {
        this.prevFxml = prevFxml; this.scarpaDettaglio = scarpaDettaglio; this.prodottoAcquistoSingolo = prodottoAcquisto; this.prevBrand = brand; this.prevGen = gen; this.prevCat = cat; this.prevRicerca = ricerca;
    }

    @FXML private void handleTornaIndietro(ActionEvent e) {
        try { FXMLLoader l = new FXMLLoader(getClass().getResource("/com/sneakup/view/TipoConsegna.fxml")); Parent r = l.load();
            ((TipoConsegnaGUIController)l.getController()).setDatiNavigazione(prevFxml, scarpaDettaglio, prodottoAcquistoSingolo, prevBrand, prevGen, prevCat, prevRicerca);
            ((Stage)((Node)e.getSource()).getScene().getWindow()).setScene(new Scene(r)); } catch (IOException x) { x.printStackTrace(); }
    }

    @FXML private void handleConferma(ActionEvent e) {
        if (comboBrand.getValue() == null || comboNegozio.getValue() == null) { mostraErrore("Seleziona Brand e Negozio!"); return; }
        double tot = (prodottoAcquistoSingolo!=null)?prodottoAcquistoSingolo.getPrezzo():Sessione.getInstance().getCarrello().stream().mapToDouble(Scarpa::getPrezzo).sum();
        new Alert(Alert.AlertType.INFORMATION, "ORDINE CONFERMATO!\nRitiro: "+comboBrand.getValue()+" - "+comboNegozio.getValue()+"\nTotale: €"+String.format("%.2f", tot)).showAndWait();
        if(prodottoAcquistoSingolo==null) Sessione.getInstance().svuotaCarrello();
        navigaVerso("/com/sneakup/view/Benvenuto.fxml", e);
    }

    @FXML private void handleReloadHome(ActionEvent e) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", e); }
    @FXML private void handleReloadHomeMouse(MouseEvent e) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", e); }
    @FXML private void handleLoginGenerico(ActionEvent e) { navigaVerso("/com/sneakup/view/Login.fxml", e); }
    @FXML private void handlePreferiti(ActionEvent e) { navigaVerso("/com/sneakup/view/Preferiti.fxml", e); }
    @FXML private void handleStatoOrdine(ActionEvent e) { new Alert(Alert.AlertType.INFORMATION, "Funzione non disponibile.").showAndWait(); }

    private void navigaVerso(String fxml, java.util.EventObject e) {
        try { FXMLLoader l = new FXMLLoader(getClass().getResource(fxml)); Parent r = l.load();
            ((Stage)((Node)(e.getSource() instanceof Node ? e.getSource() : btnConferma)).getScene().getWindow()).setScene(new Scene(r)); } catch (IOException x) { x.printStackTrace(); }
    }

    @FXML public void mostraEmuoviBarra(MouseEvent e) { Node s=(Node)e.getSource(); Bounds b=s.localToScene(s.getBoundsInLocal()); Point2D l=barraAnimata.getParent().sceneToLocal(b.getMinX(),0); barraAnimata.setLayoutX(l.getX()); barraAnimata.setPrefWidth(b.getWidth()); barraAnimata.setOpacity(1.0); }
    @FXML public void nascondiBarra(MouseEvent e) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void iconaEntra(MouseEvent e) { ScaleTransition st=new ScaleTransition(Duration.millis(200),(Node)e.getSource()); st.setToX(1.05); st.setToY(1.05); st.play(); }
    @FXML public void iconaEsce(MouseEvent e) { ScaleTransition st=new ScaleTransition(Duration.millis(200),(Node)e.getSource()); st.setToX(1.0); st.setToY(1.0); st.play(); }
}