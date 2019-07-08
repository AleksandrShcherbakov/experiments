package com.work.sqlServerProject.controller;

import com.work.sqlServerProject.NBFparser.Parser;
import com.work.sqlServerProject.NBFparser.ParserHalper;
import com.work.sqlServerProject.Position.Cell;
import com.work.sqlServerProject.Position.Cell2G;
import com.work.sqlServerProject.Position.Position;
import com.work.sqlServerProject.dao.CellNameDAO;
import com.work.sqlServerProject.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by a.shcherbakov on 24.06.2019.
 */
@Controller
public class CheckingController {

    @Autowired
    private CellNameDAO cellNameDAO;
    private Position pos=null;
    private List<Point>allPoints=null;
    List<Point> points=null;


    @RequestMapping(value = "/inputScan", method = RequestMethod.GET)
    public String showSelectScanFilePage(Model model){
        PathScanFile pathScanFile = new PathScanFile();
        model.addAttribute("pathScanFile", pathScanFile);
        return "checking/checkPathFileScan";
    }

    @RequestMapping(value = "/inputScan", method = RequestMethod.POST)
    public String loadScanAndSelectPos(Model model, @ModelAttribute ("pathScanFile") PathScanFile pathScanFile){
        List<String> parsered;
        try {
            parsered = ParserHalper.createinSrtings(pathScanFile.getUrl());
        }
        catch (IOException e){
            return "Путь к файлу сканера указан не верно.";
        }
        points = Parser.getPointsFromScan(parsered);
        return "redirect:/checkPos";
    }

    @RequestMapping(value = "/checkPos", method = RequestMethod.GET)
    public String showSelectPos(Model model){
        PosForCheck posForCheck = new PosForCheck();
        model.addAttribute("pos", posForCheck);
        return "checking/checkPos";
    }



    @RequestMapping(value = "/checkPos", method = RequestMethod.POST)
    @ResponseBody
    public String selectPN(Model model, @ModelAttribute ("pos") PosForCheck posForCheck) throws IOException {
        if (posForCheck.getPosnames().equals("")){
            return "не введена позиция";
        }
        Position position=null;
        String pos= posForCheck.getPosnames();
        Integer posname=Integer.parseInt(pos);
        if (posname!=null) {
            List<CellInfo> list = cellNameDAO.getInfoForBS(posname);
            if (list.size()==0){
                return "БС с таким номером нет.";
            }
            position = new Position(list);
        }
        position.setPointsInPosition(points);
        position.setAllPointsToCells();
        position.findBestScan();
        this.pos=position;
        this.allPoints=points;
        StringBuilder ssylka= new StringBuilder();
        List<String>abouts=position.getCells().stream().map(p->p.getAbout()).distinct().collect(Collectors.toList());
        for(String p:abouts) {
            ssylka.append("<a href='/map?about=" + p + "'>показать " + posname + "</a><br>");
        }
        ssylka.append("<a href='/map'>показать весь маршрут</a>");
        return posname+"<br>"+
                position.toString()+"<br>"+
                ssylka.toString();
    }




    @RequestMapping(value = "/map", method = RequestMethod.GET)
    public String getMap(Model model,
                         @RequestParam(required = false, name = "about") String about,
                         @RequestParam(required = false, name = "posName") Integer number){
        System.out.println(about);
        List<Cell>cellList=pos.getCells().stream().filter(p->p.getAbout().equals(about)).peek(System.out::println).collect(Collectors.toList());
        if (about.startsWith("GSM")) {
            Set<String> bsshBsics = cellList.stream().map(p-> (Cell2G)p).map(p -> p.getBcchBsic()).collect(Collectors.toSet());
        }
        Set<Point> pointsTomap=new HashSet<>();
        for (Cell c : cellList){
            if (about.equals("GSM 1800")) {
                Cell2G g = (Cell2G)c;
                Set<Point> set = pos.getAllPointsInPosition().stream().filter(p -> p.getRxLevel1800().get(g.getBcchBsic())!=null).collect(Collectors.toSet());
                pointsTomap.addAll(set);
            }
            else
            if (about.equals("GSM 900")) {
                Cell2G g = (Cell2G)c;
                Set<Point> set = pos.getAllPointsInPosition().stream().filter(p -> p.getRxLevel900().get(g.getBcchBsic())!=null).collect(Collectors.toSet());
                pointsTomap.addAll(set);
            }
        }
        model.addAttribute("cells", cellList);
        List<PointToMap> list = pos.getAllPointsInPosition().stream().map(p->new PointToMap(p.getLongitude(),p.getLatitude())).collect(Collectors.toList());
        model.addAttribute("pointss", list);
        model.addAttribute("lon", pos.getCells().get(0).getLongitude());
        model.addAttribute("lat", pos.getCells().get(0).getLalitude());
        return "map";
    }
}
