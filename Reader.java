/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poe.kmltodb;

import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author dit82
 */
public class Reader {
    
    public static final String URL = "jdbc:postgresql://10.107.5.103:5432/gis?user=gis&password=gisadmin";
    static String filename = "C:\\Users\\Desperate\\Desktop\\finalKML\\try.kml";
    //static String filename = "D:\\Lines\\Карлівка\\КарлівкаКЛ.kml";
    
    static String filialNumber = "29";
    
    static int i = 0;
    public static void main(String... args) throws SQLException, UnsupportedEncodingException, FileNotFoundException {
        Connection connection = null;
            connection = DriverManager.getConnection(URL);
            if (!connection.isClosed())
                System.out.println("Связь установлена");
       
        List<KL> listKl = parseKL(filename);
            DBUpdater.PushKLToDB(connection, listKl, filialNumber);
        List<PL> listPl = parsePL (filename);
        DBUpdater.PushPLToDB(connection, listPl);
        List<TP> listTp = parseTP (filename);
         DBUpdater.PushTPToDB(connection, listTp);
        // List<TP> listPs = parsePS (filename);
        // DBUpdater.PushTpToDB(connection, listPs);
       // List<MBP> listMbp = parseMBP (filename);
        //DBUpdater.PushMbpToDB(connection, listMbp);
      // List<PsKvadrat> kvadrats= parseKvadrat (filename);
       // DBUpdater.PushKvToDB(connection, kvadrats);
            System.out.println("готово");
        //connection.close();
    }
    
   static List<KL> parseKL (String filename) {
        List<KL> listKl = new ArrayList<>();
        
            final Kml kml = Kml.unmarshal(new File(filename));
            
            final Document document = (Document) kml.getFeature();
            List<Feature> first = document.getFeature();
            for(Object start : first) {
                Folder filial = (Folder) start;  //Название филиала
                List<Feature> filialName = filial.getFeature();
            System.out.println(filial.getName());
                for(Object o : filialName){
                    
                    Folder klassU = (Folder)o; //папка класс напряжения
                    List<Feature> second = klassU.getFeature();
                    System.out.println(klassU.getName());
                    if(!klassU.getName().contains("ПС") && !klassU.getName().contains("МБП") ) {
                    for(Object ft : second) {
                        if (ft instanceof Folder) {
                        Folder categoryType = (Folder) ft; //папка категорий(ВЛ,ТП,КЛ)
                        List<Feature> third = categoryType.getFeature();
                        System.out.println(categoryType.getName());
                        if (categoryType.getName().contains("КЛ")) { //если нашло папку с кабельными линиями
                            for (Object ftg : third) {
                                Folder lines = (Folder) ftg; //список линий
                                System.out.println(lines.getName());
                           //РАЙОН1КЛ
                           /*
                            Placemark placemark = (Placemark)ftg;
                            Geometry geo = placemark.getGeometry();
                            if (geo instanceof LineString) {
                                LineString line = (LineString) placemark.getGeometry();
                                List<Coordinate> coordinates = line.getCoordinates();
                                JSONArray coords = new JSONArray();
                                coordinates.forEach((coordinate) -> {
                                    JSONObject coord = new JSONObject("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");
                                    coords.put(coord);
                                 //   kabel.setCoord(coords.toString());
                                    
                                   // System.out.println("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");
                                });
                            if (placemark.getName().contains("::")) {
                                    System.out.println(StringUtils.substringAfterLast(placemark.getName(), "::"));
                                   // StringUtils.substringAfterLast(lines.getName(), "::");
                                    KL kabel = new KL(StringUtils.substringAfterLast(placemark.getName(), "::"), coords.toString());
                                    i++;
                                    
                                    list.add(kabel);
                               // System.out.println(coords);
                                } else {
                                    System.out.println(placemark.getName().substring(0, placemark.getName().indexOf(' ')));
                                KL kabel = new KL(placemark.getName().substring(0, placemark.getName().indexOf(' ')), coords.toString());
                                i++;
                                    list.add(kabel);
                                }
                            }}}
                            */
                            //конец РАЙОН1КЛ
                            
                          //  KL kabel;
                          //ВЫБОР структуры 
                            
                           // System.out.println(lines.getName());
                          //  kabel.setNumber(lines.getName().substring(0, lines.getName().indexOf(' ')));
                            List<Feature> forth = lines.getFeature();
                         
                            for (Feature mark : forth) {
                                if (mark instanceof Placemark) {
                                final Placemark placemark = (Placemark) mark; //считываем точки координат
                                Geometry geometry = placemark.getGeometry();
                                if (geometry instanceof LineString) {
                                LineString line = (LineString) placemark.getGeometry();
                                List<Coordinate> coordinates = line.getCoordinates();
                                JSONArray coords = new JSONArray();
                                coordinates.forEach((coordinate) -> {
                                    JSONObject coord = new JSONObject("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");
                                    coords.put(coord);
                                 //   kabel.setCoord(coords.toString());
                                    
                                   // System.out.println("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");
                                });
                                if (lines.getName().contains("::")) {
                                    System.out.println(StringUtils.substringAfterLast(lines.getName(), "::"));
                                   // StringUtils.substringAfterLast(lines.getName(), "::");
                                    KL kabel = new KL( StringUtils.substringAfterLast(lines.getName(), "::"), coords.toString());
                                    i++;
                                    
                                    listKl.add(kabel);
                               // System.out.println(coords);
                                } else {
                                    System.out.println(lines.getName().substring(0, lines.getName().indexOf(' ')));
                                KL kabel = new KL(lines.getName().substring(0, lines.getName().indexOf(' ')), coords.toString());
                                i++;
                                    listKl.add(kabel);
                                }
                                }
                                }
                            }
                        }
                    }
                } 
            } 
        }
    }      
   }           
        //System.out.println(i);
        return listKl;
    }
   
   static List<PL> parsePL (String filename) throws UnsupportedEncodingException {
       List<PL> listPL = new ArrayList<>();
        
            final Kml kml = Kml.unmarshal(new File(filename));
            final Document document = (Document) kml.getFeature();
            List<Feature> first = document.getFeature();
            for(Object start : first) {
                Folder filial = (Folder) start;  //Название филиала
                List<Feature> filialName = filial.getFeature();
            
                for(Object o : filialName){
                    Folder klassU = (Folder)o; //папка класс напряжения
                    List<Feature> second = klassU.getFeature();
                    System.out.println(klassU.getName());
                    if(!klassU.getName().contains("ПС") && !klassU.getName().contains("МБП")) {
                    for(Object ft : second) {
                        if (ft instanceof Folder) {
                        Folder categoryType = (Folder) ft; //папка категорий(ВЛ,ТП,КЛ)
                        List<Feature> third = categoryType.getFeature();
                        System.out.println(categoryType.getName());
                        if (categoryType.getName().contains("ПЛ")) { //если нашло папку с высотными линиями
                        for (Object pl : third) {
                            
                            Folder lines = (Folder) pl; //название линии
                            List<Feature> plFeature = lines.getFeature();
                            JSONArray plCoords = new JSONArray();
                            for (Feature sec : plFeature) {
                                if (sec instanceof Placemark) {
                                    final Placemark plPlacemark = (Placemark) sec; //координаты главной линии
                                    Geometry plGeometry = plPlacemark.getGeometry();
                                    if (plGeometry instanceof LineString) {
                                        LineString plLine = (LineString) plPlacemark.getGeometry();
                                        List<Coordinate> plCoordinates = plLine.getCoordinates();
                                        for (Coordinate coordinate : plCoordinates) {
                                    //    plCoordinates.forEach((coordinate) -> {
                                            JSONObject coord = new JSONObject("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");
                                            plCoords.put(coord);
                                           // System.out.println("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");
                                        
                                        }
                                        //
                                    } else if (plGeometry instanceof MultiGeometry) {
                                        MultiGeometry mpg=(MultiGeometry) plPlacemark.getGeometry();
                                        List<Geometry> gmList= mpg.getGeometry();
                                        for(Geometry geoItr: gmList) {
                                            if (geoItr instanceof LineString) {
                                                //LineString plLine = (LineString) plPlacemark.getGeometry();
                                                List<Coordinate> plCoordinates = ((LineString) geoItr).getCoordinates();
                                                for (Coordinate coordinate : plCoordinates) {
                                            //    plCoordinates.forEach((coordinate) -> {
                                                    JSONObject coord = new JSONObject("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");
                                                    plCoords.put(coord);
                                                   // System.out.println("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");

                                                }
                                            }
                                        }
                                        break;    
                                    }
                                } else if (sec instanceof Folder) {
                                    Folder otp = (Folder) sec;
                                    List<Feature> otpFeature = otp.getFeature();
                                    JSONArray otpCoords = new JSONArray();
                                    for (Feature th : otpFeature) {
                                        if (th instanceof Placemark) {
                                            final Placemark otpPlacemark = (Placemark) th;
                                            Geometry otpGeometry = otpPlacemark.getGeometry();
                                            
                                            if(otpGeometry instanceof LineString) {
                                                LineString otpLine = (LineString) otpPlacemark.getGeometry();
                                                List<Coordinate> otpCoordinates = otpLine.getCoordinates();
                                                
                                                otpCoordinates.forEach((coordinate) -> {
                                                    JSONObject coord = new JSONObject("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");

                                                    otpCoords.put(coord);
                                                   // System.out.println("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");
                                                });
                                                //запись отпаек (не менять)
                                                PL oline = new PL(otp.getName(), otpCoords.toString());
                                                listPL.add(oline);
                                                System.out.println(oline.getName()+" "+ oline.getCoord());
                                            } 
                                        } else if (th instanceof Folder) {
                                            parseFolder(th, listPL);
                                        }
                                    }
                                    
                                }
                            }
                            PL pline = new PL(lines.getName(), plCoords.toString());
                            listPL.add(pline);
                            System.out.println(lines.getName()+" "+ plCoords.toString());
                        }
                        }
                    }        
                }                    
            }
        }
    }
            return listPL;
   }
   
   static void parseFolder(Feature feature, List<PL> listPL) {
       Folder folder = (Folder) feature;
       List<Feature> nextOtp = folder.getFeature();
       JSONArray otpCoords = new JSONArray();
       for (Feature fea : nextOtp) {
           
           if (fea instanceof Placemark) {
                final Placemark otpPlacemark = (Placemark) fea;
                Geometry otpGeometry = otpPlacemark.getGeometry();
                                            
                if(otpGeometry instanceof LineString) {
                LineString otpLine = (LineString) otpPlacemark.getGeometry();
                List<Coordinate> otpCoordinates = otpLine.getCoordinates();
                                                
                otpCoordinates.forEach((coordinate) -> {
                JSONObject coord = new JSONObject("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");

                otpCoords.put(coord);
               // System.out.println("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");
                });
                                         
                }               
           } else if (fea instanceof Folder) {
               parseFolder(fea, listPL);
               
           }
       }
       PL pline = new PL(folder.getName(), otpCoords.toString());
                listPL.add(pline);
                System.out.println(pline.getName()+" "+ otpCoords.toString()); 
   }
   
   static List<TP> parseTP (String filename) {
       List<TP> listTP = new ArrayList<>();
        
            final Kml kml = Kml.unmarshal(new File(filename));
            final Document document = (Document) kml.getFeature();
            List<Feature> first = document.getFeature();
            for(Object start : first) {
                Folder filial = (Folder) start;  //Название филиала
                List<Feature> filialName = filial.getFeature();
            
                for(Object o : filialName){
                    Folder klassU = (Folder)o; //папка класс напряжения
                    List<Feature> second = klassU.getFeature();
                    System.out.println(klassU.getName());
                    if(!klassU.getName().contains("ПС") && !klassU.getName().contains("МБП")) {
                    for(Object ft : second) {
                        if( ft instanceof Folder) {
                        Folder categoryType = (Folder) ft; //папка категорий(ВЛ,ТП,КЛ)
                        List<Feature> third = categoryType.getFeature();
                        System.out.println(categoryType.getName());
                    if (categoryType.getName().contains("ТП")) { //если нашло ТП-шку
                        for (Object tps : third) {
                            Placemark placemark = (Placemark) tps;
                            Geometry geo = placemark.getGeometry();
                            if(geo instanceof Point) {
                                Point tp = (Point) placemark.getGeometry();
                                List<Coordinate> tpCoords = tp.getCoordinates();
                                tpCoords.forEach((coordinate) -> {
                                    TP tpcoord = new TP(placemark.getDescription(), coordinate.getLatitude(), coordinate.getLongitude());
                                listTP.add(tpcoord);
                                System.out.println(placemark.getDescription() + " " +coordinate.getLatitude() +" " + coordinate.getLongitude());
                                });
                                
                            }  
                            }
                        }
                    }
   }
            }
                }
            }
            return listTP;
            }
            
   static List<TP> parsePS (String filename) {
       List<TP> listTp = new ArrayList<>();
        
            final Kml kml = Kml.unmarshal(new File(filename));
            final Document document = (Document) kml.getFeature();
            List<Feature> first = document.getFeature();
            for(Object start : first) {
                Folder filial = (Folder) start;  //Название филиала
                List<Feature> filialName = filial.getFeature();
            
                for(Object o : filialName){
                    Folder klassU = (Folder)o; //папка класс напряжения
                    System.out.println(klassU.getName());
                    if(klassU.getName().contains("ПС")) {
                        Folder psss = (Folder) o;
                            List<Feature> psFeature = psss.getFeature();
                            for (Feature ps : psFeature) {
                            Placemark placemark = (Placemark) ps;
                            Geometry geo = placemark.getGeometry();
                            if(geo instanceof Point) {
                                Point tp = (Point) placemark.getGeometry();
                                List<Coordinate> tpCoords = tp.getCoordinates();
                                //JSONArray coords = new JSONArray();
                                tpCoords.forEach((coordinate) -> {
                                TP pscoord = new TP(placemark.getDescription(),coordinate.getLatitude(),  coordinate.getLongitude());
                                listTp.add(pscoord);
                                System.out.println(coordinate.getLatitude() +" " + coordinate.getLongitude());
                                });
                                
                            }
                        }
                    }
                }
            }
        return listTp;
   }
   
   static List<MBP> parseMBP (String filename) {
       List<MBP> listMbp = new ArrayList<>();
        final Kml kml = Kml.unmarshal(new File(filename));
            final Document document = (Document) kml.getFeature();
            List<Feature> first = document.getFeature();
            for(Object start : first) {
                Folder filial = (Folder) start;  //Название филиала
                List<Feature> filialName = filial.getFeature();
            
                for(Object o : filialName){
                    Folder klassU = (Folder)o; //папка класс напряжения
                    System.out.println(klassU.getName());
                    if(klassU.getName().contains("МБП")) {
                        Folder mbp = (Folder) o;
                        List<Feature> mbpFeature = mbp.getFeature();
                        for (Feature ps : mbpFeature) {
                            Placemark placemark = (Placemark) ps;
                            Geometry geo = placemark.getGeometry();
                            if(geo instanceof Point) {
                                Point tp = (Point) placemark.getGeometry();
                                List<Coordinate> mbpCoords = tp.getCoordinates();
                                mbpCoords.forEach((coordinate) -> {
                                    MBP mbpcoord = new MBP(placemark.getName(), coordinate.getLatitude(), coordinate.getLongitude());
                                   listMbp.add(mbpcoord);
                                   System.out.println(coordinate.getLatitude() +" " + coordinate.getLongitude());
                                });
                            }
                        }
                    }
                }
            }  
            return listMbp;
        }
   
   static List<PsKvadrat> parseKvadrat (String filename) {
       List <PsKvadrat> kvadrats = new ArrayList<>();
       final Kml kml = Kml.unmarshal(new File(filename));
            final Document document = (Document) kml.getFeature();
            List<Feature> first = document.getFeature();
            for(Object start : first) {
                Folder filial = (Folder) start;  //Название филиала
                List<Feature> filialName = filial.getFeature();
            
                for(Object o : filialName){
                    Folder klassU = (Folder)o; //папка класс напряжения
                    List<Feature> second = klassU.getFeature();
                    System.out.println(klassU.getName());
                    for(Object ft : second) {
                        Folder categoryType = (Folder) ft; //папка категорий(ВЛ,ТП,КЛ)
                        List<Feature> third = categoryType.getFeature();
                        for (Object tps : third) {
                             
                            Placemark placemark = (Placemark) tps;
                            Geometry geo = placemark.getGeometry();
                            if(geo instanceof Polygon) {
                                System.out.println("fasdf");
                                Polygon polygon = (Polygon) geo;
                                Boundary outerBoundaryIs = polygon.getOuterBoundaryIs();
                                LinearRing kvadrat = outerBoundaryIs.getLinearRing();
                                List<Coordinate> kvCoords = kvadrat.getCoordinates();
                                JSONArray coords = new JSONArray();
                                kvCoords.forEach((coordinate) -> {
                                    JSONObject coord = new JSONObject("\t "+"{\"lat\":"+coordinate.getLatitude() +",\"lng\":"+ coordinate.getLongitude()+"}");
                                    coords.put(coord);
                                });
                                PsKvadrat kvcoord = new PsKvadrat(placemark.getDescription(), coords.toString());
                                kvadrats.add(kvcoord);
                                System.out.println(kvcoord.getTplnr() +" " + kvcoord.getCoord());
                            }
                        }
                    }
                }
            }
       return kvadrats;
   }
    }