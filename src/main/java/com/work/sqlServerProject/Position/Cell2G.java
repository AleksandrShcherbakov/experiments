package com.work.sqlServerProject.Position;

import com.work.sqlServerProject.model.CellInfo;
import com.work.sqlServerProject.model.Point;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by a.shcherbakov on 29.05.2019.
 */
public class Cell2G extends Cell {
    private int BCCH;
    private Object BSIC;
    private Map<String,Double> rxLevperBCCH;
    private boolean rightAzimuth;
    private String bcchBsic;
    private Map<Integer, Double> allRxLev;
    private Map<Integer, Double> allRxLevWeight;



    public Cell2G(CellInfo cellInfo, int distance) {
        super(cellInfo, distance);
        this.BCCH=cellInfo.getCh();
        this.BSIC=cellInfo.getBsic();
        this.bcchBsic=BCCH+" "+BSIC;
    }



    public void putAllrxLevinband(){
        allRxLev= new HashMap<>();
        allRxLevWeight= new HashMap<>();
        for (Cell c : super.getCellsInBand()){
            Cell2G p=(Cell2G)c;
            String [] temp = this.findAverRxLevPerBCCHBSIC(p.getBcchBsic()).split(" ");
            double tempRxLev = Double.parseDouble(temp[0]);
            double tempRxLevWeight = Double.parseDouble(temp[1]);
            allRxLev.put(c.getCi(), tempRxLev);
            allRxLevWeight.put(c.getCi(), tempRxLevWeight);
        }
    }



    public String findBestCI(Map<Integer, Double> map){
        int bestCI=0;
        double maxRxLev=-200;
        double temp=0;
        for (Integer i : map.keySet()){
            temp=map.get(i);
            if (super.getCi()==48575){
                System.out.println(temp);
            }

            if (temp==0)
                continue;
            if (temp>maxRxLev){
                maxRxLev=temp;
                bestCI=i;
                System.out.println(super.getCi()+" "+i+" "+temp);
            }
        }
        return bestCI+" "+(bestCI==super.getCi()? "true":"false");
    }

    public void checkCell(){
        String[] checkWithAverRxLev = findBestCI(allRxLev).split(" ");
        String[] checkWithWeight = findBestCI(allRxLevWeight).split(" ");
        int best1=Integer.parseInt(checkWithAverRxLev[0]);
        int best2=Integer.parseInt(checkWithWeight[0]);
        boolean ok1 = Boolean.parseBoolean(checkWithAverRxLev[1]);
        boolean ok2= Boolean.parseBoolean(checkWithWeight[1]);
        if (best1==best2 && ok1==ok2){
            super.setBestCellID(best1);
            super.setOk(ok1);;
        }
        else
        if (ok1 || ok2){
            super.setOk(true);
            if (ok1) {
                super.setBestCellID(best1);
            }
            else super.setBestCellID(best2);
        }
        else
        if (best1!=0 && best2!=0){
            super.setBestCellID(best2);
        }
        else super.setOk(false);
    }



    public String findAverRxLevPerBCCHBSIC(String bcchBsic){
        Map<String, Double> map=null;
        Double tempRxLev=null;
        Double common=0.0;
        int count=0;
        for (Point p : super.getPointsInCell()) {
            if (super.getBand() == 900) {
                map = p.getRxLevel900();
            }
            else
            if (super.getBand() == 1800) {
                map = p.getRxLevel1800();
            }
            tempRxLev = map.get(bcchBsic);
            if (tempRxLev != null) {
                common = common + tempRxLev;
                count++;
            }
        }
        if (count==0){
            return 0+" "+0;
        }
        return common/count+" "+(common/count)/count;
    }


    @Override
    public String toString() {
        String r =null;
        if (super.getBestCellID()==0){
            r=" измерений в зоне этого сектора нет";
        }
        else
            r=" ok: "+super.isOk();

        return "system: "+super.getSystem()+" "+super.getBand()+
                " selfCI: "+super.getCi()+
                " bestScanCI: "+super.getBestCellID()+
                " азимут: "+super.getAzimuth()+r;

    }

    public Map<Integer, Double> getAllRxLevWeight() {
        return allRxLevWeight;
    }

    public void setAllRxLevWeight(Map<Integer, Double> allRxLevWeight) {
        this.allRxLevWeight = allRxLevWeight;
    }

    public Map<Integer, Double> getAllRxLev() {
        return allRxLev;
    }

    public void setAllRxLev(Map<Integer, Double> allRxLev) {
        this.allRxLev = allRxLev;
    }

    public Map<String, Double> getRxLevperBCCH() {
        return rxLevperBCCH;
    }

    public void setRxLevperBCCH(Map<String, Double> rxLevperBCCH) {
        this.rxLevperBCCH = rxLevperBCCH;
    }

    public String getBcchBsic() {
        return bcchBsic;
    }

    public void setBcchBsic(String bcchBsic) {
        this.bcchBsic = bcchBsic;
    }

    public boolean isRightAzimuth() {
        return rightAzimuth;
    }

    public void setRightAzimuth(boolean rightAzimuth) {
        this.rightAzimuth = rightAzimuth;
    }

    public int getBCCH() {
        return BCCH;
    }

    public void setBCCH(int BCCH) {
        this.BCCH = BCCH;
    }

    public Object getBSIC() {
        return BSIC;
    }

    public void setBSIC(Object BSIC) {
        this.BSIC = BSIC;
    }
}
