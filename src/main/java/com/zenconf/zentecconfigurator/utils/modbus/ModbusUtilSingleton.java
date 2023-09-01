package com.zenconf.zentecconfigurator.utils.modbus;

import com.intelligt.modbus.jlibmodbus.data.ModbusHoldingRegisters;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadInputRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.WriteMultipleRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.response.ReadInputRegistersResponse;
import com.intelligt.modbus.jlibmodbus.serial.*;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.enums.VarTypes;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class ModbusUtilSingleton {
    private static ModbusUtilSingleton instance;

    private static ModbusMaster master;
    private int slaveId = 1;
    private String device;
    private SerialPort.BaudRate baudRate = SerialPort.BaudRate.BAUD_RATE_115200;
    private int dataBits = 8;
    private SerialPort.Parity parity = SerialPort.Parity.EVEN;
    private int stopBits = 1;

    private static final Logger logger = LogManager.getLogger(ModbusUtilSingleton.class);


    public static ModbusUtilSingleton getInstance() {
        if (instance == null) {
            instance = new ModbusUtilSingleton();
        }
        return instance;
    }

    private ModbusUtilSingleton() {

    }

    public void connect() {
        SerialParameters sp = new SerialParameters();
//        Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);
        try {
            if (!device.equals("")) {
                sp.setDevice(device);
                sp.setBaudRate(baudRate);
                sp.setDataBits(dataBits);
                sp.setParity(parity);
                sp.setStopBits(stopBits);

                SerialUtils.setSerialPortFactory(new SerialPortFactoryJSSC());
                if (master != null) {
                    if (master.isConnected()) {
                        master.disconnect();
                    }
                    master.setResponseTimeout(3000);
                }
                master = ModbusMasterFactory.createModbusMasterRTU(sp);
                testConnection();
            }
        } catch (SerialPortException e) {
            logger.error(e.getMessage());
        } catch (ModbusIOException e) {
            throw new RuntimeException(e);
        }
    }

    private void testConnection() throws ModbusIOException {
        if (master != null) {
            if (!master.isConnected()) {
                Thread thread = getThread();
                thread.start();
            }
        }
    }

    private Thread getThread() {
        Thread thread;
        Runnable task = () -> {
            try {
                logger.info("Подключение...");
                master.connect();
                MainController.mainParameters.getDeviceAddressAttribute().readModbusParameter();
                Thread.sleep(1000);
                logger.info("Устройство подключено");
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Успешное подключение");
                    alert.setTitle("Успех");
                    alert.show();
                });
            } catch (Exception e) {
                String errorMessage = "Ошибка при подключении к контроллеру\n" + e.getMessage();
                logger.error(errorMessage);
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText(errorMessage);
                    alert.setHeaderText("Ошибка Modbus");
                    alert.setTitle("Ошибка");
                    alert.show();
                });
                throw new RuntimeException(e);
            } finally {
                try {
                    master.disconnect();
                } catch (ModbusIOException e1) {
                    logger.error(e1.getMessage());
                }
            }
        };
        thread = new Thread(task);
        return thread;
    }

    public void disconnect() throws ModbusIOException {
        try {
            if (master != null) {
                if (master.isConnected()) {
                    master.disconnect();
                }
            }
            logger.info("Устройство отключено");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Устройство отключено");
            alert.setTitle("Успех");
            alert.show();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Устройство уже отключено\n" + ex.getMessage());
            alert.setHeaderText("Ошибка Modbus");
            alert.setTitle("Ошибка");
            alert.show();
        }
    }

    public synchronized Object readModbus(int address, VarTypes varType) throws Exception {
        Object value = null;
        try {
            if (varType.equals(VarTypes.BOOL)) {
                value = readModbusCoil(address);
            } else if (varType.equals(VarTypes.FLOAT)) {
                value = readMultipleModbusRegister(address);
            } else {
                value = readSingleModbusRegister(address, varType);
            }
        } catch (Exception e) {
            logger.error("[ОШИБКА ЧТЕНИЯ] address: " + address + " type: " + varType + " value: " + value);
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return value;
    }

    public synchronized void writeModbus(int address, VarTypes varType, Object value) {
        try {
            if (varType.equals(VarTypes.BOOL)) {
                writeModbusCoil(address, Boolean.parseBoolean(value.toString()));
            } else if (varType.equals(VarTypes.FLOAT)) {
                writeMultipleModbusRegister(address, Float.parseFloat(value.toString()));
            } else {
                writeSingleModbusRegister(address, Integer.parseInt(value.toString()), varType);
            }
        } catch (Exception e) {
            logger.error("[ОШИБКА ЗАПИСИ] address: " + address + " type: " + varType + " value: " + value);
            logger.error(e.getMessage());
        }
    }

    public synchronized boolean readModbusCoil(int address) throws Exception {
        boolean coilValue = false;
        try {
            if (!master.isConnected()) {
                master.connect();
            }
            coilValue = master.readCoils(slaveId, address, 1)[0];
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e1) {
                logger.error(e1.getMessage());
            }
        }
        return coilValue;
    }

    public synchronized void writeModbusCoil(int address, boolean value) throws Exception {
        try {
            if (!master.isConnected()) {
                master.connect();
            }
            master.writeSingleCoil(slaveId, address, value);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e1) {
                logger.error(e1.getMessage());
            }
        }
    }

    public synchronized long readSingleModbusRegister(int address, VarTypes varType) throws Exception {

        long registerValue = 0;
        try {
            if (!master.isConnected()) {
                master.connect();
            }
            if (varType.equals(VarTypes.UINT8) || varType.equals(VarTypes.UINT16)) {
                registerValue = master.readHoldingRegisters(slaveId, address, 1)[0];
            } else if (varType.equals(VarTypes.UINT32)) {
                int[] registerValues = master.readHoldingRegisters(slaveId, address, 2);
                registerValue = (long) registerValues[0] * 65536 + (long) registerValues[1];
            } else if (varType.equals(VarTypes.SINT8)) {
                registerValue = master.readHoldingRegisters(slaveId, address, 1)[0];
                if (registerValue > Byte.MAX_VALUE) {
                    registerValue -= Byte.MAX_VALUE * 2 + 2;
                }
            } else if (varType.equals(VarTypes.SINT16)) {
                registerValue = master.readHoldingRegisters(slaveId, address, 1)[0];
                if (registerValue > 32767) {
                    registerValue -= 32767 * 2 + 2;
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e1) {
                logger.error(e1.getMessage());
            }
        }
        return registerValue;
    }

    public synchronized void writeSingleModbusRegister(int address, int value, VarTypes varType) throws Exception {
        try {
            if (!master.isConnected()) {
                master.connect();
            }
            if (varType.equals(VarTypes.SINT8)) {
                if (value < Byte.MAX_VALUE) {
                    value = Byte.MAX_VALUE * 2 + 2 + value;
                }
            }
            master.writeSingleRegister(slaveId, address, value);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e1) {
                logger.error(e1.getMessage());
            }
        }
    }

    public synchronized float readMultipleModbusRegister(int address) throws Exception {
        float registerValue = 0;
        try {
            if (!master.isConnected()) {
                master.connect();
            }
            ReadInputRegistersRequest request = new ReadInputRegistersRequest();
            request.setServerAddress(slaveId);
            request.setStartAddress(address);
            request.setQuantity(2);
            ReadInputRegistersResponse response = (ReadInputRegistersResponse) request.getResponse();
            master.processRequest(request);
            ModbusHoldingRegisters registers = response.getHoldingRegisters();

            byte[] bytes = registers.getBytes();
            int value = 0;
            for (byte b : bytes) {
                value = (value << 8) + (b & 0xFF);
            }
            registerValue = Float.intBitsToFloat(value);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e1) {
                logger.error(e1.getMessage());
            }
        }
        return registerValue;
    }

    // Запись Дробный 4-байт
    public synchronized void writeMultipleModbusRegister(int address, float value) throws Exception {
        try {
            int intValue = Float.floatToIntBits(value);
            byte[] bytes = new byte[Float.BYTES];
            int length = bytes.length;
            for (int i = 0; i < length; i++) {
                bytes[length - i - 1] = (byte) (intValue & 0xFF);
                intValue >>= 8;
            }
            if (!master.isConnected()) {
                master.connect();
            }
            WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest();
            request.setServerAddress(slaveId);
            request.setStartAddress(address);
            request.setQuantity(2);
            request.setBytes(bytes);
            master.processRequest(request);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e1) {
                logger.error(e1.getMessage());
            }
        }
    }

    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public SerialPort.BaudRate getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(SerialPort.BaudRate baudRate) {
        this.baudRate = baudRate;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public SerialPort.Parity getParity() {
        return parity;
    }

    public void setParity(SerialPort.Parity parity) {
        this.parity = parity;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public ModbusMaster getMaster() {
        return master;
    }

    private byte[] wordSwap(byte[] buf) {
        byte[] returnBuf = new byte[buf.length];
        for (int i = 0; i < buf.length; i = i + 4) {
            returnBuf[i] = buf[i + 2];
            returnBuf[i + 1] = buf[i + 3];
            returnBuf[i + 2] = buf[i];
            returnBuf[i + 3] = buf[i + 1];
        }
        return returnBuf;
    }
}
