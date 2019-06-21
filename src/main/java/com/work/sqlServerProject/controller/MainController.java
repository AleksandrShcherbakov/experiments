package com.work.sqlServerProject.controller;

import com.work.sqlServerProject.Helper.Helper;
import com.work.sqlServerProject.NBFparser.Parser;
import com.work.sqlServerProject.NBFparser.ParserHalper;
import com.work.sqlServerProject.Position.*;
import com.work.sqlServerProject.dao.CellNameDAO;
import com.work.sqlServerProject.form.BTSForm;
import com.work.sqlServerProject.form.CellNameForm;
import com.work.sqlServerProject.model.CellInfo;
import com.work.sqlServerProject.model.CellNameInfo;
import com.work.sqlServerProject.model.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by AlVlShcherbakov on 14.02.2019.
 */
@Controller
public class MainController {

    @Autowired
    private CellNameDAO cellNameDAO;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String openStartPage(){
        return "start";
    }

    @RequestMapping(value = "/createBts", method = RequestMethod.GET)
    public String find(Model model){
        BTSForm btsForm = new BTSForm();
        model.addAttribute("btsForm", btsForm);
        return "createBts";
    }


    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public String select(Model model, CellNameForm cellNameForm){
        CellNameInfo cellNameInfo=cellNameDAO.findCellNumber(cellNameForm.getFormCellName());
        model.addAttribute("cellInfos",cellNameInfo);
        Helper.writeToCSV("C://",cellNameInfo.getCellName()+";"+cellNameInfo.getCI());
        return "show result";
    }

    List<Point>pointss;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String selectPN(Model model,@RequestParam (required = false) Integer posname, @RequestParam (required = false) String filename) throws IOException {
        Position position=null;
        if (posname!=null) {
            List<CellInfo> list = cellNameDAO.getInfoForBS(posname);
            position = new Position(list);
        }
        List<String> parsered = ParserHalper.createinSrtings("C:\\Users\\AlVlShcherbakov\\Documents\\" +filename);
        List<Point> points = Parser.getPointsFromScan(parsered);
        position.setPointsInPosition(points);
        StringBuilder builder = new StringBuilder(posname+"<br><br>");
        for (Cell c : position.getCells()){
            if (c.getClass().getSimpleName().equals("Cell2G")){
                Cell2G p = (Cell2G)c;
                p.putAllrxLevinband();
                p.findBestCI();
                p.checkCell();
                builder.append("self CI: "+p.getCi()+" bestScanCI: "+p.getBestCellID()+" "+p.isCheck());
                builder.append("<br>");
                System.out.println(p.getClass().getSimpleName()+" "+p.getCi()+" "+p.getBcchBsic()+" "+p.getSystem()+" "+p.getBand()+" "+p.getAzimuth()+" "+p.getPointsInCell().size()+" "+p.findAverRxLevPerBCCHBSIC(p.getBcchBsic()));
                for (Integer s : p.getAllRxLev().keySet()){
                    System.out.println(s+" "+p.getAllRxLev().get(s));
                }
            }
            /*else
            if (c.getClass().getSimpleName().equals("Cell3G")){
                Cell3G p = (Cell3G)c;
                p.putScrinband();
                System.out.println(p.getClass().getSimpleName()+" "+p.getCi()+" "+p.getScr()+" "+p.getSystem()+" "+p.getBand()+" "+p.getAzimuth()+" "+p.getPointsInCell().size()+" "+p.findAverRSCPPerSCr(Integer.parseInt(p.getScr()+"")));
                for (Integer s : p.getScrInband()){
                    System.out.println(s+" "+p.findAverRSCPPerSCr(s)+" "+p.getCountOfPoints());
                }
            }
            else
            if (c.getClass().getSimpleName().equals("Cell4G")){
                Cell4G p = (Cell4G)c;
                p.putPciinband();;
                System.out.println(p.getClass().getSimpleName()+" "+p.getCi()+" "+p.getPCI()+" "+p.getSystem()+" "+p.getBand()+" "+p.getAzimuth()+" "+p.getPointsInCell().size()+" "+p.findAverRSRPerPCI(Integer.parseInt(p.getPCI()+"")));
                for (Integer s : p.getPciInBand()){
                    System.out.println(s+" "+p.findAverRSRPerPCI(s)+" "+p.getCountOfPoints());
                }
            }*/
            //System.out.println(c.getClass().getSimpleName()+" "+c.getCi()+" "+c.getSystem()+" "+c.getBand()+" "+c.getAzimuth()+" "+c.getPointsInCell().size());
        }
        return builder.toString();
    }


    @RequestMapping(value = "/map", method = RequestMethod.GET)
    public String getMap(Model model){
        model.addAttribute("points",pointss);
        return "map";
    }



    @RequestMapping(value = "/bts", method = RequestMethod.POST)
    @ResponseBody
    public String createBTS(@ModelAttribute("btsForm")BTSForm btsForm, Model model) throws IOException {
        String path = btsForm.getPath();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");

        String fileName= simpleDateFormat.format(new Date())+"_ALL_BS.csv";
        File file = new File(path+"\\"+fileName);
        if (file.exists()){
            return "BTS файл за сегодня уже создан.<br><p><a href='/'>На стартовую страницу</a></p>";
        }
        List<CellInfo>list=cellNameDAO.getAllCells();
        List<CellInfo>listLteEricsson = cellNameDAO.getAllLteEricssonCells();
        List<CellInfo>listLteHuawei = cellNameDAO.getAllLteHuaweiCells();
        StringBuilder log = new StringBuilder();

        System.out.println(path);

        if (!path.isEmpty()) {
            file = new File(path);

        if (file.isFile()){
            return "Вы указали файл а не директорию, ипаравьтесь!<br><p><a href='/'>Ввести директорию еще раз</a></p>";
        }
        else
            if (!file.exists()){
            Files.createDirectory(Paths.get(path));
            }}
            else
        if (path.equals("")){
            path="C://";
        }
        log.append(Helper.writeToCSV(path,list));
        log.append(Helper.writeToCSV(path,listLteEricsson));
        log.append(Helper.writeToCSV(path,listLteHuawei));
        /*StringBuilder stringBuilder = new StringBuilder();
        for (CellInfo a : listLteHuawei){
            //stringBuilder.append(a);
            //System.out.println(a);
        }*/
        return "BTS файл создан<br><p><a href='/'>На стартовую страницу</a></p>";
    }
    @RequestMapping(value = "/exit", method = RequestMethod.GET)
    public String exit(){
        System.exit(0);
        return null;
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    @ResponseBody
    public String  createFile() throws IOException {
        File file1 = new File("C://123.txt");
        String string = "\n привет ghbdtn";
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, true)));
            bufferedWriter.write(string);
            bufferedWriter.close();
        }
        catch (Exception e){
            return "ничего не получилось";
        }
        return file1.toString();
    }

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    @ResponseBody
    public  String readFile() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("C:\\123.txt");
        InputStreamReader reader = new InputStreamReader(fileInputStream,"UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String res = bufferedReader.readLine();
        return res;
    }


}
