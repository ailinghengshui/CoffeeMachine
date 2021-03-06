package com.jingye.coffeemac.service.protocol;

public final class MachineStatusCode {
	
	public static final int SUCCESS = 200; //一切正常

	public static final int USER_CANCELED = 501; //用户取消

	public static final int ERROR = 600; //错误
	
	public static final int HEAT_TEMPERATURE_NOT_CHANGED = 601; //规定时间内加热温度没变化
	
	public static final int TEMPERATURE_SENSOR_ERROR = 602;  //温度传感器故障
	
	public static final int BOILER_TEMPERATURE_TO_HIGH = 603; //锅炉温度过高
	
	public static final int BREW_MOTOR_TIMEOUT = 604; //冲泡电机运行超时
	
	public static final int POWDER_SOLENOID_ABNORMAL = 605;  //下粉电磁阀异常
	
	public static final int PUMPING_TIMEOUT = 606; //下抽水超时
	
	public static final int WASTEWATER_TANK_EXCEED = 607; //废水桶满
	
	public static final int TEMPERATURE_CONTROL_SYSTEM_ABNORMAL = 608; //温度控制系统异常
	
	public static final int NO_CUP = 609; // 没杯,unlock
	
	public static final int FOLL_CUP_MOTOR_RUN_TIMEOUT = 610; //落杯电机运行超时
	
	public static final int BLOCK_CUP = 611; //卡杯
	
	public static final int FOLL_CUP_SYSTEM_EXCEPTION = 612; //落杯系统异常
	
	public static final int MOVE_MOUTH_INSTRUCTION_LOCATION_INCORRECT = 613; //移嘴指令传送位置不正确

	public static final int MOVE_MOUTH_MOTOR_RUN_TIMEOUT = 614; //移嘴电机运行超时
	
	public static final int MOVE_MOUTH_SYSTEM_EXCEPTION = 615; //移嘴系统异常
	
	public static final int SPOON_MOTOR_RUN_TIMEOUT = 616; //落勺电机运行超时 
	
	public static final int SPOON_SYSTEM_EXCEPTION = 617; //落勺系统异常	
	
	public static final int MATERIAL_DEFICIENCY = 618; //物料不足

    public static final int COFFEE_MAKE_TIME_OUT = 619; //咖啡机在规定时间内还没做好,unlock

    public static final int MACHINE_WARM_UP = 620; //咖啡机正在预热,unlock

    public static final int ALREADY_HAVE_CUP = 621; //杯子未取走,unlock

    public static final int DUPLICATE_SERIAL_NUM = 622; //流水号重复

    public static final int GRINDER_SYSTEM_BUSY = 623; //磨豆系统正忙

    public static final int MACHINE_BUSY = 624; //机器正忙

    public static final int FOLL_CUP_SYSTEM_BUSY = 625; //落杯系统正忙

    public static final int MOVE_MOUTH_MOTOR_BUSY = 626; //移嘴系统正忙

    public static final int SPOON_MOTOR_BUSY = 627; //落勺正忙

    public static final int DATA_EXTRACTION_TIMEOUT = 628; //提取数据超时

    public static final int TASK_BUSY = 629; //任务忙

    public static final int SET_TEMP_FAILED = 630; //咖啡机设置温度失败

    public static final int UNKNOW_ERROR = 631; //未知错误

    public static final int UNRELEVANT_RESULT = 632; //不相关结果

	// NEW ADDED
    public static final int WASHING_MACHINE_FAILED = 633; //清洗咖啡机失败,unlock

	public static final int ICE_MAKER_ERROR = 634; //制冰机异常

	public static final int DROP_CUP_TIMEOUT = 635; //落杯超时

	public static final int DROP_ICE_TIMEOUT = 636;//落冰超时

	public static final int UP_BREW_SENSOR_ERROR = 637;//上冲泡器传感器故障

	public static final int DOWN_BREW_SENSOR_ERROR = 638;//下冲泡器传感器故障

	public static final int GRINDER_MOTOR_TIMEOUT= 639; //磨豆电机超时

	public static final int FLOW_METER_DETECTION_TIMEOUT = 640; //流量计检测超时

	public static final int FOLL_CUP_SYSTEM_COMMUNICATE_ERROR = 641; //控制板与落杯板通讯故障

	public static final int MOVE_CUP_MOTOR_RUN_TIMEOUT = 642; //移杯电机运行超时

	public static final int CUP_BARREL_MOTOR_RUN_TIMEOUT = 643;//杯桶电机转动超时

	public static final int MAKE_COFFEE_PORT_TIMEOUT = 644;//下位机请求超时
	public static final int LOCK = 645;//被锁定
}
