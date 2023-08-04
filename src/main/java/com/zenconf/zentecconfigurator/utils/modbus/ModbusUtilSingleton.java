package com.zenconf.zentecconfigurator.utils.modbus;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.data.ModbusHoldingRegisters;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadInputRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.WriteMultipleRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.response.ReadInputRegistersResponse;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortFactoryJSSC;
import com.intelligt.modbus.jlibmodbus.serial.SerialUtils;
import com.zenconf.zentecconfigurator.models.enums.VarTypes;
import javafx.scene.control.Alert;

// TODO: Сделать билдер

public class ModbusUtilSingleton {
    private static ModbusUtilSingleton instance;

    private ModbusMaster master;
    private int slaveId = 1;
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
                System.out.println("Успешное соединение");
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

    public void disconnect() throws ModbusIOException {
        if (master != null) {
            if (master.isConnected()) {
                master.disconnect();
                System.out.println("Успешное отключение");
            }
        }
    }

    public Object readModbus(int address, VarTypes varType) {
        Object value = null;
        if (varType.equals(VarTypes.BOOL)) {
            value = readModbusCoil(address);
        } else if (varType.equals(VarTypes.FLOAT)) {
            value = readMultipleModbusRegister(address);
        } else {
            value = readSingleModbusRegister(address, varType);
        }
        return value;
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

    public int readSingleModbusRegister(int address, VarTypes varType) {
        int registerValue = 0;
        try {
            master.connect();
            if (varType.equals(VarTypes.UINT8) || varType.equals(VarTypes.UINT16)) {
                registerValue = master.readHoldingRegisters(slaveId, address, 1)[0];
            } else if (varType.equals(VarTypes.UINT32)) {
                registerValue = master.readHoldingRegisters(slaveId, address, 2)[0];
            } else if (varType.equals(VarTypes.SINT8)) {
                registerValue = master.readHoldingRegisters(slaveId, address, 2)[0];
                if (registerValue > Byte.MAX_VALUE) {
                    registerValue -= Byte.MAX_VALUE * 2 + 2;
                }
            }
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

    public void writeSingleModbusRegister(int address, int value, VarTypes varType) {
        try {
            master.connect();
            if (varType.equals(VarTypes.SINT8)) {
                if (value < Byte.MAX_VALUE) {
                    value = Byte.MAX_VALUE * 2 + 2 + value;
                }
            }
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

    public float readMultipleModbusRegister(int address) {
        float registerValue = 0;
        try {
            master.connect();
            ReadInputRegistersRequest request = new ReadInputRegistersRequest();
            request.setServerAddress(slaveId);
            request.setStartAddress(address);
            request.setQuantity(2);
            ReadInputRegistersResponse response = (ReadInputRegistersResponse) request.getResponse();
            master.processRequest(request);
            ModbusHoldingRegisters registers = response.getHoldingRegisters();

            byte[] bytes = registers.getBytes();
            int value = 0;
            for (byte b: bytes) {
                value = (value << 8) + (b & 0xFF);
            }
            registerValue = Float.intBitsToFloat(value);

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

    // Запись Дробный 4-байт
    public void writeMultipleModbusRegister(int address, float value) {
        try {
            int intValue = Float.floatToIntBits(value);
            byte[] bytes = new byte[Float.BYTES];
            int length = bytes.length;
            for (int i = 0; i < length; i++) {
                bytes[length - i - 1] = (byte) (intValue & 0xFF);
                intValue >>= 8;
            }
            master.connect();
            WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest();
            request.setServerAddress(slaveId);
            request.setStartAddress(address);
            request.setQuantity(2);
            request.setBytes(bytes);
            master.processRequest(request);
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

    private byte[] wordSwap(byte[] buf) {
        byte[] returnBuf = new byte[buf.length];
        for (int i = 0; i < buf.length; i = i + 4)
        {
            returnBuf[i] = buf[i+2];
            returnBuf[i+1] = buf[i+3];
            returnBuf[i+2] = buf[i];
            returnBuf[i+3] = buf[i+1];
        }
        return returnBuf;
    }
}
