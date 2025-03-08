package com.wp.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wp.dto.MachineDataDTO;
import com.wp.dto.PageDTO;
import com.wp.entity.Machine;
import com.wp.entity.MachineData;
import com.wp.mapper.MachineMapper;
import com.wp.service.MachineService;
import com.wp.vo.MachineDataVO;
import com.wp.vo.PageResult;
import com.wp.vo.PredictDataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MachineServiceImpl implements MachineService {

    private static final int initialNumber = 101;
    private static final String PYTHONINTERPRETER = "D:\\python\\python.exe";
    private static final String SCRIPTPATH = "E:\\wind power project\\information\\WPF\\WPF\\Predict.py";

    @Autowired
    private MachineMapper machineMapper;

    @Autowired
    private PredictDataVO predictDataVO;

    /**
     * 添加风机数据
     *
     * @param file
     * @return
     */
    @Transactional
    public void addMachineData(MultipartFile file) throws IOException {
        //1、封装Machine对象并插入数据
        String filename = file.getOriginalFilename();
        log.info("文件名为：{}", filename);//wp-1-2019-01-01.xlsx
        String[] splits = filename.split("-");//["wp","1","2019","01","01.xlsx"]
        log.info("切割之后的数组为：{}", splits.toString());
        //拿到风机id查询风机列表
        int id = Integer.parseInt(splits[1]);
        // 没有数据插入新的数据，设置新的id，有数据代表风机已经存在，则只需要向wp_data表插入数据即可
        Machine machine = machineMapper.getById(id);
        if (machine == null) {
            //没有数据，插入新的数据
            Integer serialNumber = initialNumber + Integer.parseInt(splits[1]);
            String name = splits[0] + serialNumber;
            machine = Machine.builder()
                    .id(id)
                    .serialNumber(serialNumber)
                    .name(name)
                    .createTime(LocalDate.now())
                    .build();
            machineMapper.insert(machine);
        }
        //2、解析风机数据excel表格并封装数据
        //创建excel对象
        InputStream is = file.getInputStream();
        XSSFWorkbook excel = new XSSFWorkbook(is);

        //获取工作表
        XSSFSheet sheet = excel.getSheetAt(0);
        //获取最后一行
        int lastRowNum = sheet.getLastRowNum();
        //log.info("最后一行为：{}", lastRowNum);

        //封装MachineData数据对象
        ArrayList<MachineData> machineDataList = new ArrayList<>();
        for (int i = 1; i <= lastRowNum; i++) {
            //获取行对象
            XSSFRow row = sheet.getRow(i);

            //日期的是字符串，其他的是double类型的数据
            //log.info("数据格式为：{}",row.getCell(0).getCellTypeEnum());
            String cellValue1 = row.getCell(0).getStringCellValue();

            // 转换数据格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 将字符串转换为LocalDateTime对象
            LocalDateTime recordTime = LocalDateTime.parse(cellValue1, formatter);

            double cellValue2 = row.getCell(1).getNumericCellValue();
            double cellValue3 = row.getCell(2).getNumericCellValue();
            double cellValue4 = row.getCell(3).getNumericCellValue();
            double cellValue5 = row.getCell(4).getNumericCellValue();
            double cellValue6 = row.getCell(5).getNumericCellValue();
            double cellValue7 = row.getCell(6).getNumericCellValue();
            double cellValue8 = row.getCell(7).getNumericCellValue();
            double cellValue9 = row.getCell(8).getNumericCellValue();
            double cellValue10 = row.getCell(9).getNumericCellValue();
            double cellValue11 = row.getCell(10).getNumericCellValue();
            double cellValue12 = row.getCell(11).getNumericCellValue();
            double cellValue13 = row.getCell(12).getNumericCellValue();
            MachineData machineData = MachineData.builder()
                    .machineId(machine.getId())
                    .recordTime(recordTime)
                    .speedTen(cellValue2)
                    .dircTen(cellValue3)
                    .speedThirty(cellValue4)
                    .dircThirty(cellValue5)
                    .speedFifty(cellValue6)
                    .dircFifty(cellValue7)
                    .measureHeight(cellValue8)
                    .measureDirc(cellValue9)
                    .temperature(cellValue10)
                    .pressure(cellValue11)
                    .humidity(cellValue12)
                    .power(cellValue13)
                    .build();
            machineDataList.add(machineData);
        }

        //3、向wp_data插入数据
        for (MachineData machineData : machineDataList) {
            machineMapper.insertData(machineData);
        }
    }

    /**
     * 分页查询风机数据
     *
     * @param pageDTO
     * @return
     */
    public PageResult pageQuery(PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNumber(), pageDTO.getPageSize());

        Page<Machine> page = machineMapper.pageQuery(pageDTO);
        long total = page.getTotal();
        List<Machine> machineList = page.getResult();
        return new PageResult(machineList, total);
    }

    /**
     * 删除风机信息
     *
     * @param id
     */
    @Transactional
    public void delete(Integer id) {
        //删除wp_data表的风机数据
        machineMapper.deleteMachineData(id);

        //删除machine表的风机数据
        machineMapper.delete(id);
    }

    /**
     * 导出风机数据报表
     *
     * @param response
     */
    public void exportMachineData(Integer machineId, HttpServletResponse response) {
        //1. 查询wp_data数据库，根据风机的id获取风机数据
        List<MachineData> machineDataList = machineMapper.getByMachineId(machineId, null, null);
        int size = machineDataList.size();
        //通过POI将数据写入到Excel文件当中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/风机数据报表.xlsx");

        try {
            //基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            XSSFSheet sheet = excel.getSheetAt(0);
            //int number = sheet.getPhysicalNumberOfRows();
            //log.info("该excel文件实际有 {} 行",number);
            //遍历machineDataList，将数据填充到Excel文件中
            for (int i = 1; i < size; i++) {
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    //定义数据格式
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    row.getCell(0).setCellValue(machineDataList.get(i - 1).getRecordTime().format(formatter).toString());
                    row.getCell(1).setCellValue(machineDataList.get(i - 1).getSpeedTen());
                    row.getCell(2).setCellValue(machineDataList.get(i - 1).getDircTen());
                    row.getCell(3).setCellValue(machineDataList.get(i - 1).getSpeedThirty());
                    row.getCell(4).setCellValue(machineDataList.get(i - 1).getDircThirty());
                    row.getCell(5).setCellValue(machineDataList.get(i - 1).getSpeedFifty());
                    row.getCell(6).setCellValue(machineDataList.get(i - 1).getDircFifty());
                    row.getCell(7).setCellValue(machineDataList.get(i - 1).getMeasureHeight());
                    row.getCell(8).setCellValue(machineDataList.get(i - 1).getMeasureDirc());
                    row.getCell(9).setCellValue(machineDataList.get(i - 1).getTemperature());
                    row.getCell(10).setCellValue(machineDataList.get(i - 1).getPressure());
                    row.getCell(11).setCellValue(machineDataList.get(i - 1).getHumidity());
                    row.getCell(12).setCellValue(machineDataList.get(i - 1).getPower());
                }
            }
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据风机id获取风机数据
     *
     * @param machineDataDTO
     * @return
     */
    public PredictDataVO getById(MachineDataDTO machineDataDTO) {
        //原始数据：machineDataDTO中获取的beginTime和endTime，从数据库中查询这个时间段的数据
        //预测之后的数据：endTime+interval,endTime+interval+endTime-beginTime，从数据库中查询这个时间段的数据封装成.xlsx文件并调用脚本
        //传入参数为：模型，风机id，需要处理的excel文件（也就是上面数据封装进excel文件）
        String modelName = machineDataDTO.getModelName();
        Integer machineId = machineDataDTO.getMachineId();
        String beginTime = machineDataDTO.getBeginTime();
        String endTime = machineDataDTO.getEndTime();
        Integer timeInterval = machineDataDTO.getTimeInterval();
//        log.info("模型名字：{}，风机id：{}，开始时间：{}，结束时间：{}，时间间隔：{}", modelName, machineId, beginTime, endTime, timeInterval);

        //这里对开始时间和结束时间进行处理 开始时间往前面推一个时间点，结束时间往后面推一个时间点，一个时间点间隔是15分钟
        LocalDateTime begin = null;
        LocalDateTime end = null;
        if (!(beginTime.equals("") || beginTime.length() == 0)) {
            begin = forthQuarter(beginTime);
        }
        if (!(endTime.equals("") || endTime.length() == 0)) {
            end = backQuarter(endTime);
        }
        log.info("开始时间为：{}，结束时间为：{}", begin, end);
        //从wp_data根据风机id查询到的原数据
        List<MachineData> machineDataList = machineMapper.getByMachineId(machineId, begin, end);
        predictDataVO.setOriginalData(new ArrayList<>());
        predictDataVO.setPredictData(new ArrayList<>());
        //将原数据封装成VO对象
        for (MachineData machineData : machineDataList) {
            MachineDataVO machineDataVO = new MachineDataVO();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String format = machineData.getRecordTime().format(formatter);
            machineDataVO.setRecordTime(format);
            machineDataVO.setPower(machineData.getPower());
            predictDataVO.getOriginalData().add(machineDataVO);
        }
//        log.info("封装之后的predictDataVO中原始数据为:{}", predictDataVO.getOriginalData().toString());

        //通过POI将数据写入到Excel文件当中
        try {
            writeToExcel(machineDataList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //调用脚本生成预测之后的数据
        getDataByScript(modelName);

        //从excel表格中读取预测之后的数据
        //begin:end+timeInterval
        //end:end+timeInterval+end-begin
        //这里先进行判断，比如时间跨度太长，数据库没有数据，则开始时间和结束时间从数据库中拿
//        String min = predictDataVO.getOriginalData().get(0).getRecordTime().replace(" ","T");
//        String max = predictDataVO.getOriginalData().get(predictDataVO.getOriginalData().size() - 1).getRecordTime().replace(" ","T");
//        LocalDateTime minTime = LocalDateTime.parse(min);
//        LocalDateTime maxTime = LocalDateTime.parse(max);
//        begin = begin.isBefore(minTime) ? minTime : begin;
//        end = end.isAfter(maxTime) ? maxTime : end;

//        // 计算预测数据图的开始时间和结束时间
//        //开始时间
//        // 计算 end - begin
//        Duration durationBetween = Duration.between(begin, end);
//        begin = end.plusHours(timeInterval);
//        // 计算 end + interval
//        LocalDateTime endPlusInterval = end.plusHours(timeInterval);
//        // 计算 end + interval + (end - begin)
//        end = endPlusInterval.plus(durationBetween);
//        log.info("开始时间和结束时间为：{}，{}", begin, end);
        String outFilePath = "E:\\wind power project\\information\\WPF\\WPF\\out\\original data_pred.xlsx";
        try {
            FileInputStream fis = new FileInputStream(outFilePath);
            //创建excel对象
            XSSFWorkbook excel = new XSSFWorkbook(fis);
            //获取工作表
            XSSFSheet sheet = excel.getSheetAt(0);
            //获取最后一行
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                MachineDataVO machineDataVO = new MachineDataVO();
                //获取行对象
                XSSFRow row = sheet.getRow(i);
                CellType cellTypeEnum = row.getCell(0).getCellTypeEnum();
                System.out.println(cellTypeEnum);
                String dateTime = row.getCell(0).getStringCellValue();
                log.info("datetime:{}", dateTime);
                machineDataVO.setRecordTime(dateTime);
                double power = row.getCell(1).getNumericCellValue();
                log.info("power:{}", power);
                machineDataVO.setPower(power);
//                //对时间进行判断，如果dateTime在begin和end之间，则封装进去（时间跨度最大为12个小时，也就是48个点）
//                if (dateTime.compareTo(begin.toString()) >= 0 && dateTime.compareTo(end.toString()) <= 0) {
//                    predictDataVO.getPredictData().add(machineDataVO);
//                }
                predictDataVO.getPredictData().add(machineDataVO);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        log.info("封装之后的数据对象为：{}", predictDataVO);
        return predictDataVO;
    }

    public void getDataByScript(String modelName) {
        int index = 1;
        String filePath = "E:\\wind power project\\information\\WPF\\WPF\\input\\original data.xlsx";
        String command = String.format("%s \"%s\" --m_name %s --index %d --f_path \"%s\"",
                PYTHONINTERPRETER, SCRIPTPATH, modelName, index, filePath);
        try {
            //这个方法是类似隐形开启了命令执行器，输入指令执行python脚本
            Process process = Runtime.getRuntime()
                    .exec(command);

            // 读取 Python 脚本的标准输出和错误输出
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            while ((line = inputReader.readLine()) != null) {
                System.out.println(line);  // 输出标准输出
            }

            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);  // 输出错误输出
            }

            // 等待 Python 脚本执行完成
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("调用python脚本并读取结果时出错：" + e.getMessage());
        }
    }

    //将传入的时间往后移动一个时间点（15） 2019-01-01 00:13:00=>2019-01-01 00:15:00
    public static LocalDateTime backQuarter(String inputTime) {
        LocalDateTime dateTime = LocalDateTime.parse(inputTime.replace(" ", "T")); // 转换为 LocalDateTime
        // 获取当前时间的分钟数
        int minutes = dateTime.getMinute();

        // 计算离当前时间最近的 15 分钟点
        int remainder = minutes % 15;  // 取余数
        int adjustMinutes = remainder == 0 ? 0 : (15 - remainder);  // 如果是 15 分钟的倍数，保持不变，否则调整为下一个 15 分钟点

        // 调整时间
        return dateTime.plusMinutes(adjustMinutes).withSecond(0).withNano(0);
    }

    //将传入的时间往前移动一个时间点（15） 2019-01-01 00:13:00=>2019-01-01 00:15:00
    public static LocalDateTime forthQuarter(String inputTime) {
        LocalDateTime dateTime = LocalDateTime.parse(inputTime.replace(" ", "T")); // 转换为 LocalDateTime
        // 获取当前时间的分钟数
        int minutes = dateTime.getMinute();

        // 计算离当前时间最近的 15 分钟点
        int remainder = minutes % 15;  // 取余数
        int adjustMinutes = remainder == 0 ? 0 : -remainder;  // 如果是 15 分钟的倍数，保持不变，否则调整为前一个 15 分钟点

        // 调整时间
        return dateTime.plusMinutes(adjustMinutes).withSecond(0).withNano(0);
    }

    public void writeToExcel(List<MachineData> machineDataList) throws IOException {
        //在内存中创建excel表格
        XSSFWorkbook excel = new XSSFWorkbook();
        //创建工作表
        XSSFSheet sheet = excel.createSheet("originalData");
        //创建行对象
        XSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("Time(year-month-day h:m:s)");
        row.createCell(1).setCellValue("Wind speed at height of 10 meters (m/s)");
        row.createCell(2).setCellValue("Wind direction at height of 10 meters (˚)");
        row.createCell(3).setCellValue("Wind speed at height of 30 meters (m/s)");
        row.createCell(4).setCellValue("Wind direction at height of 30 meters (˚)");
        row.createCell(5).setCellValue("Wind speed at height of 50 meters (m/s)");
        row.createCell(6).setCellValue("Wind direction at height of 50 meters (˚)");
        row.createCell(7).setCellValue("Wind speed - at the height of wheel hub (m/s)");
        row.createCell(8).setCellValue("Wind speed - at the height of wheel hub (˚)");
        row.createCell(9).setCellValue("Air temperature  (°C) ");
        row.createCell(10).setCellValue("Atmosphere (hpa)");
        row.createCell(11).setCellValue("Relative humidity (%)");
        row.createCell(12).setCellValue("Power (MW)");

        //获取集合长度
        int size = machineDataList.size();
        for (int i = 1; i <= size; i++) {
            //创建行对象
            row = sheet.createRow(i);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (row != null) {
                //输入的日期数据格式需要修改一下=>2019-01-01 00:13:00
                row.createCell(0).
                        setCellValue(machineDataList.get(i-1).getRecordTime().format(formatter));
                row.createCell(1).setCellValue(String.valueOf(machineDataList.get(i - 1).getSpeedTen()));
                row.createCell(2).setCellValue(String.valueOf(machineDataList.get(i - 1).getDircTen()));
                row.createCell(3).setCellValue(String.valueOf(machineDataList.get(i - 1).getSpeedThirty()));
                row.createCell(4).setCellValue(String.valueOf(machineDataList.get(i - 1).getDircThirty()));
                row.createCell(5).setCellValue(String.valueOf(machineDataList.get(i - 1).getSpeedFifty()));
                row.createCell(6).setCellValue(String.valueOf(machineDataList.get(i - 1).getDircFifty()));
                row.createCell(7).setCellValue(String.valueOf(machineDataList.get(i - 1).getMeasureHeight()));
                row.createCell(8).setCellValue(String.valueOf(machineDataList.get(i - 1).getMeasureDirc()));
                row.createCell(9).setCellValue(String.valueOf(machineDataList.get(i - 1).getTemperature()));
                row.createCell(10).setCellValue(String.valueOf(machineDataList.get(i - 1).getPressure()));
                row.createCell(11).setCellValue(String.valueOf(machineDataList.get(i - 1).getHumidity()));
                row.createCell(12).setCellValue(String.valueOf(machineDataList.get(i - 1).getPower()));
            }
        }
        //将excel表格写入到磁盘中
        excel.write(new FileOutputStream("E:\\wind power project\\information\\WPF\\WPF\\input\\original data.xlsx"));
    }
}
