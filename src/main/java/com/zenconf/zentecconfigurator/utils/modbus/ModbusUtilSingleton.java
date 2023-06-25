package com.zenconf.zentecconfigurator.utils.modbus;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortFactoryJSSC;
import com.intelligt.modbus.jlibmodbus.serial.SerialUtils;
import javafx.scene.control.Alert;

// TODO: Сделать билдер

public class ModbusUtilSingleton {
    private static ModbusUtilSingleton instance;

    private ModbusMaster master;
    private int slaveId = 247;
    private String device;
    private SerialPort.BaudRate baudRate = SerialPort.BaudRate.BAUD_RATE_115200;
    private int dataBits = 8;
    private SerialPort.Parity parity = SerialPort.Parity.EVEN;
    private int stopBits = 1;

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
        Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);
        try {
            if (!device.equals("")) {
                sp.setDevice(device);
                sp.setBaudRate(baudRate);
                sp.setDataBits(dataBits);
                sp.setParity(parity);
                sp.setStopBits(stopBits);

                SerialUtils.setSerialPortFactory(new SerialPortFactoryJSSC());
                master = ModbusMasterFactory.createModbusMasterRTU(sp);
            }
        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Ошибка при подключении к контроллеру\n" + e.getMessage());
            alert.setHeaderText("Ошибка Modbus");
            alert.show();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean readModbusCoil(int address) {
        boolean coilValue = false;
        try {
            master.connect();
            coilValue = master.readCoils(slaveId, address, 1)[0];
            System.out.println("Address: " + address + ", Value: " + coilValue);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e1) {
                e1.printStackTrace();
            }
        }
        return coilValue;
    }

    public void writeModbusCoil(int address, boolean value) {
        try {
            master.connect();
            master.writeSingleCoil(slaveId, address, value);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public int readModbusRegister(int address) {
        int registerValue = 0;
        try {
            master.connect();
            registerValue = master.readHoldingRegisters(slaveId, address, 1)[0];
            System.out.println("Address: " + address + ", Value: " + registerValue);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e1) {
                e1.printStackTrace();
            }
        }
        return registerValue;
    }

    public void writeModbusRegister(int address, int value) {
        try {
            master.connect();
            master.writeSingleRegister(slaveId, address, value);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public String getDevice() {
        return device;
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
}
