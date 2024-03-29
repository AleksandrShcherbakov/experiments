package com.work.sqlServerProject.mapper;

import com.work.sqlServerProject.model.CellInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by a.shcherbakov on 19.02.2019.
 */
public class CellMapper implements RowMapper<CellInfo> {
    public static final String All_SQL = "select distinct\n" +
            "\n" +
            "case b.ran when '4G' then 'LTE' when '3G' then 'UMTS' when '2G' then 'GSM' end as SYSTEM,\n" +
            "a.posname as SITE,\n" +
            "a.latitude as LAT,\n" +
            "a.longitude as LON, \n" +
            "a.ci as CID, \n" +
            "a.name as CELL,\n" +
            "b.LAC as LAC,\n" +
            "a.TAC,\n" +
            "a.frequency as BAND,\n" +
            "b.BSC,\n" +
            "ISNULL(NULLIF(b.uarfcnDL,null),BCCH) as CH,\n" +
            "b.BSIC,\n" +
            "primaryScramblingCode as SCR,   \n" +
            "null as PCI,\n" +
            "a.azimuth as DIR,\n" +
            "a.HEIGHT,\n" +
            "a.TILT,\n" +
            "b.BSC as RNCID,\n" +
            "a.geo_zone as REGION\n" +
            "\n" +
            "\n" +
            "from pmc.bs_general_table_cr as a join pmc.cellstatus as b on a.cellname=b.cellname\n" +
            "\n" +
            "where geo_zone like 'Mck%'\n" +
            " and b.date in\n" +
            "   (SELECT MAX(date) FROM pmc.cellstatus where cellname=b.cellname)\n" +
            "   and (b.ran = '2G' or b.ran = '3G')\n" +
            "   and a.ant_location = 'outdoor' \n";

    public static final String SQL_LTE_Ericsson = "select distinct\n" +
            "            case a.ran when '4G' then 'LTE' end as SYSTEM,\n" +
            "            a.posname as SITE,\n" +
            "            a.ci as CID,\n" +
            "            a.latitude as LAT,\n" +
            "            a.longitude as LON,\n" +
            "            a.name as CELL,\n" +
            "            a.LAC as LAC,\n" +
            "            b.TAC,\n" +
            "            a.frequency as BAND,\n" +
            "            a.BSC as BSC,\n" +
            "            b.earfcndl as CH,\n" +
            "            null as BSIC,\n" +
            "            null as SCR,  \n" +
            "            b.pci as PCI,\n" +
            "            a.azimuth as DIR,\n" +
            "            a.HEIGHT,\n" +
            "            a.TILT,\n" +
            "            a.BSC as RNCID,\n" +
            "            a.geo_zone as REGION\n" +
            "            \n" +
            " from  pmc.bs_general_table_cr as a join pmc.Last_CellStatus_4G as b\n" +
            "on (b.enodeb_id = a.enodeb_id and b.ecell_id=a.ecell_id) where a.vendor = 'E' and a.form = 'out'\n" +
            "and a.geo_zone like 'Mck%'\n";

    public static final String SQL_LTE_Huawei = "select distinct\n" +
            "case a.ran when '4G' then 'LTE' end as SYSTEM,\n" +
            "a.posname as SITE,\n" +
            "a.ci as CID,\n" +
            "a.latitude as LAT,\n" +
            "a.longitude as LON,\n" +
            "a.name as CELL,\n" +
            "a.LAC as LAC,\n" +
            "b.TAC,\n" +
            "a.frequency as BAND,\n" +
            "a.BSC as BSC,\n" +
            "case b.earfcndl when 1326 then 1301 end as CH,\n" +
            "null as BSIC,\n" +
            "null as SCR,\n" +
            "b.pci as PCI,\n" +
            "a.azimuth as DIR,\n" +
            "a.HEIGHT,\n" +
            "a.TILT,\n" +
            "a.BSC as RNCID,\n" +
            "a.geo_zone as REGION\n" +
            "from  pmc.bs_general_table_cr as a join pmc.Last_CellStatus_4G as b\n" +
            "on (b.enodeb_id = a.enodeb_id and b.ecell_id=a.ecell_id) \n" +
            "where a.vendor = 'H' \n" +
            "and a.form = 'out'\n" +
            "and a.geo_zone like 'Mck%'\n";

    @Override
    public CellInfo mapRow(ResultSet resultSet, int i) throws SQLException {
        String system = resultSet.getString("SYSTEM");
        int syte = resultSet.getInt("SITE");
        int ci = resultSet.getInt("CID");
        float lat = resultSet.getFloat("LAT");
        float lon = resultSet.getFloat("LON");
        String cell = resultSet.getString("CELL");
        int lac = resultSet.getInt("LAC");
        Object tac = resultSet.getObject("TAC");
        int band = resultSet.getInt("BAND");
        Object bsc = resultSet.getObject("BSC");
        int ch = resultSet.getInt("CH");
        Object bsic = resultSet.getObject("BSIC");
        Object scr = resultSet.getObject("SCR");
        Object pci = resultSet.getObject("PCI");
        int dir = resultSet.getInt("DIR");
        int height = resultSet.getInt("HEIGHT");
        int tilt = resultSet.getInt("TILT");
        Object rncid = resultSet.getObject("RNCID");
        String region = resultSet.getString("REGION");
        return new CellInfo(system, syte, ci, lat, lon, cell, lac, tac, band, bsc, ch, bsic, scr, pci, dir, height, tilt, rncid, region);
    }
}
