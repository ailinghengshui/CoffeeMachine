package com.jingye.coffeemac.service.protocol.enums;

public interface ICoffeeService {

	public interface CommandId {

		public static final short FETCH_COFFEE_BY_CODE = 1;
		
		public static final short ROLL_BACK_FETCH = 2;
		
		public static final short GET_COFFEE = 3;

        public static final short GET_DOSING = 4;
		
		public static final short PAY_QRCODE = 5;
		
		public static final short ASK_PAY_STATUS = 6;
		
		public static final short PAY_RESULT = 7;
		
		public static final short MACHINE_STATUS_SERVER = 8;
		
		public static final short MACHINE_STATUS_REPORT = 9;
		
		public static final short FECTH_COFFEE_BY_QRCODE = 10;
		
		public static final short APP_DOWNLOAD = 12;

		public static final short PAY_ALI_SONICWAVE = 13;

        public static final short CANCEL_TRADE = 16;

        public static final short UPDATE_STOCK = 17;

		public static final short ADD_STOCK = 19;

		public static final short PAY_QRCODE_CART = 20;

		public static final short ASK_PAY_STATUS_CART = 22;

		public static final short ROLL_BACK_CART = 23;

		public static final short CANCEL_TRADE_CART = 24;

		public static final short GET_MACHINE_CONFIG = 26;

		public static final short GET_NOTICE = 27;

		public static final short GET_ADV_PIC = 28;

		public static final short RESET_STOCK = 29;

		public static final short REPORT_ERROR_FETCH = 30;

		public static final short UPLOAD_LOG = 31;

		public static final short EXCHANGE_COFFEE_BY_CODE = 32;

		public static final short MAINTANCE = 33;

		public static final short PRICEUPDATE = 102;

		public static final short ADVUPDATE = 103;
    }
	
	public interface CoffeeType {
		public static final int COFFEE_TYPE_ID = 1; // ID

		public static final int COFFEE_TYPE_TITLE = 2; // 标题

		public static final int COFFEE_TYPE_PRICE = 3; // 价格

		public static final int COFFEE_TYPE_IMGURL = 4; // 图片地址

		public static final int COFFEE_TYPE_SOLD_NUM = 5; // 销量

		public static final int COFFEE_TYPE_DOSING = 6; // 咖啡配料

        public static final int COFFEE_TYPE_DISCOUNT = 7; // 折扣价

//      public static final int COFFEE_TYPE_DISCOUNT_WX = 8; // 微信折扣价

//      public static final int COFFEE_TYPE_DISCOUNT_ALIPAY = 9; //支付宝折扣价

        public static final int COFFEE_TYPE_VOLUME = 10; // 饮料体积

        public static final int COFFEE_TYPE_IS_NEW = 11; // 新品

        public static final int COFFEE_TYPE_IS_HOT = 12; // 热门

		public static final int COFFEE_TYPE_IS_ADD_ICE = 13; // 加冰

		public static final int COFFEE_TYPE_IS_SOLD = 14;  // 在售状态

		public static final int COFFEE_TYPE_IS_SWEET = 15;  // 是否甜品

		public static final int COFFEE_TYPE_TITLE_EN = 16;  // 英文标题

		public static final int COFFEE_PACKAGE = 17;
		public static final int COFFEE_DESC_EN = 18;  // 英文描述
		public static final int COFFEE_DESC = 19;  // 中文描述





	};

    public interface DosingType {

		public static final int COFFEE_TYPE_ID = 1; // ID

        public static final int COFFEE_TYPE_TITLE = 2; // 标题

//      public static final int COFFEE_TYPE_STOCK= 3; // 余量

		public static final int COFFEE_TYPE_BOX_ID = 4; // 料盒号
		public static final int MACHINE_CONFUGURED = 5; // 校正因子
    };
}
